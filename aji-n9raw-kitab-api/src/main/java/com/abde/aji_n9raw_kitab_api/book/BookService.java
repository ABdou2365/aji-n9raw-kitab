package com.abde.aji_n9raw_kitab_api.book;

import com.abde.aji_n9raw_kitab_api.common.PageResponse;
import com.abde.aji_n9raw_kitab_api.exception.OperationNotPermittedException;
import com.abde.aji_n9raw_kitab_api.file.FileStorageService;
import com.abde.aji_n9raw_kitab_api.history.BookTransactionHistory;
import com.abde.aji_n9raw_kitab_api.history.BookTransactionHistoryRepository;
import com.abde.aji_n9raw_kitab_api.history.BookTransactionHistoryResponse;
import com.abde.aji_n9raw_kitab_api.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final FileStorageService fileStorageService;


    public Integer saveBook(BookRequest bookRequest, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book = bookMapper.to(bookRequest);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse getBook(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(()-> new EntityNotFoundException("No book found with id " + bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").ascending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable,user.getId());
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").ascending());
        // This is the old way to retrieve a books by the owner
//        Page<Book> books = bookRepository.findAllDisplayedBooksByOwner(pageable,user.getId());

        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()),pageable);
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookTransactionHistoryResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").ascending());
        Page<BookTransactionHistory> borrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable,user.getId());
        List<BookTransactionHistoryResponse> borrowedBookResponses = borrowedBooks.stream()
                .map(bookMapper::toBookTransactionHistoryResponse)
                .toList();
        return new PageResponse<>(
                borrowedBookResponses,
                borrowedBooks.getNumber(),
                borrowedBooks.getSize(),
                borrowedBooks.getTotalElements(),
                borrowedBooks.getTotalPages(),
                borrowedBooks.isFirst(),
                borrowedBooks.isLast()
                );
    }

    public PageResponse<BookTransactionHistoryResponse> findAllReturnedBooks(int page,int size,Authentication connectedUser){
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").ascending());
        Page<BookTransactionHistory> returnedBooks = transactionHistoryRepository.findAllReturnedBooks(pageable,user.getId());
        List<BookTransactionHistoryResponse> returnedBookResponses = returnedBooks.stream()
                .map(bookMapper::toBookTransactionHistoryResponse)
                .toList();
        return new PageResponse<>(
                returnedBookResponses,
                returnedBooks.getNumber(),
                returnedBooks.getSize(),
                returnedBooks.getTotalElements(),
                returnedBooks.getTotalPages(),
                returnedBooks.isFirst(),
                returnedBooks.isLast()
        );

    }

    public Integer updateBookShareable(Integer id, Authentication connectedUser) {
        Book book = bookRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("No book found with id " + id));
        User user = (User) connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Only the owner of this book is allowed to update the book shareable status");
        }
        boolean shareableStatus = book.isShareable();
        book.setShareable(!shareableStatus);
        return bookRepository.save(book).getId();

    }

    public Integer updateBookArchived(Integer id, Authentication connectedUser) {
        Book book = bookRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("No book found with id " + id));
        User user = (User) connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Only the owner of this book is allowed to update the book archived status");
        }
        boolean archivedStatus = book.isArchived();
        book.setArchived(!archivedStatus);
        return bookRepository.save(book).getId();
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No book found with id " + bookId));

        if(book.isShareable() && !book.isArchived()) {
            throw new OperationNotPermittedException("You cant borrow a book that is not shareable or archived");
        }

        User user = (User) connectedUser.getPrincipal();

        if(Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You can not borrow your own book");
        }

        boolean isAlreadyBorrowed = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId,user.getId());

        if(isAlreadyBorrowed) {
            throw new OperationNotPermittedException("The requested book is already borrowed");
        }

        BookTransactionHistory borrowedBook = BookTransactionHistory.builder()
                .book(book)
                .user(user)
                .returned(false)
                .returnApproved(false)
                .build();

        return transactionHistoryRepository.save(borrowedBook).getId();

    }

    public Integer returnBorrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No book found with id " + bookId));

        if(book.isShareable() && !book.isArchived()) {
            throw new OperationNotPermittedException("You cant borrow a book that is not shareable or archived");
        }
        User user = (User) connectedUser.getPrincipal();

        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }
        BookTransactionHistory borrowedBook = transactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(()-> new EntityNotFoundException("There is no book with id " + bookId));
        borrowedBook.setReturned(true);
        return transactionHistoryRepository.save(borrowedBook).getId();
    }

    public Integer approveReturnBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No book found with id " + bookId));

        if(book.isShareable() && !book.isArchived()) {
            throw new OperationNotPermittedException("You cant borrow a book that is not shareable or archived");
        }
        User user = (User) connectedUser.getPrincipal();

        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }
        BookTransactionHistory borrowedBook = transactionHistoryRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(()-> new EntityNotFoundException("The book is not returned, you cannot approvet" +
                        " it's return"));
        borrowedBook.setReturnApproved(true);
        return transactionHistoryRepository.save(borrowedBook).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No book found with id " + bookId));
        User user = (User) connectedUser.getPrincipal();
        var bookCoverPicture = fileStorageService.saveFile(file,user.getId());
        book.setBookCover(bookCoverPicture);
        bookRepository.save(book);
    }
}
