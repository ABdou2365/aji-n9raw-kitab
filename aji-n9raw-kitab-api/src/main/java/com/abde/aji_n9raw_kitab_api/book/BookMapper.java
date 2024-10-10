package com.abde.aji_n9raw_kitab_api.book;

import com.abde.aji_n9raw_kitab_api.file.FileUtils;
import com.abde.aji_n9raw_kitab_api.history.BookTransactionHistory;
import com.abde.aji_n9raw_kitab_api.history.BookTransactionHistoryResponse;

import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    //Used to map the book request to the book entity
    public Book to(BookRequest request){
        return Book.builder()
                .id(request.id())
                .title(request.title())
                .author(request.author())
                .synopsis(request.synopsis())
                .archived(false)
                .shareable(request.shareable())
                .build();

    }

    public BookResponse toBookResponse(Book book) {
        return BookResponse
                .builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .rate(book.getRate())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .owner(book.getOwner().getFullName())
                .cover(FileUtils.readFileFromLocation(book.getBookCover()))
                .build();
    }

    public BookTransactionHistoryResponse toBookTransactionHistoryResponse(BookTransactionHistory bookTransactionHistory) {
        return BookTransactionHistoryResponse
                .builder()
                .id(bookTransactionHistory.getBook().getId())
                .title(bookTransactionHistory.getBook().getTitle())
                .author(bookTransactionHistory.getBook().getAuthor())
                .rate(bookTransactionHistory.getBook().getRate())
                .returned(bookTransactionHistory.isReturned())
                .returnedApproved(bookTransactionHistory.isReturnApproved())
                .build();
    }
}
