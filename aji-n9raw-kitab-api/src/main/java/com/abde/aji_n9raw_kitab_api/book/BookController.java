package com.abde.aji_n9raw_kitab_api.book;

import com.abde.aji_n9raw_kitab_api.common.PageResponse;
import com.abde.aji_n9raw_kitab_api.history.BookTransactionHistoryResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "book")
public class BookController {


    private final BookService service;


    @PostMapping("save-book")
    public ResponseEntity<Integer> saveBook(
            @Valid @RequestBody BookRequest bookRequest,
            Authentication user
    ) {
        return ResponseEntity.ok(service.saveBook(bookRequest,user));
    }

    @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> getBook(
            @PathVariable("book-id") Integer bookId
    ){
        return ResponseEntity.ok(service.getBook(bookId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.findAllBooks(page, size, connectedUser));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.findAllBooksByOwner(page, size, connectedUser));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BookTransactionHistoryResponse>> findAllBorrowedBooks(
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.findAllBorrowedBooks(page, size, connectedUser));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BookTransactionHistoryResponse>> findAllReturnedBooks(
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.findAllReturnedBooks(page, size, connectedUser));
    }

    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateBookShareable(
            @PathVariable("book-id") Integer id,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.updateBookShareable(id,connectedUser));
    }

    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateBookArchived(
            @PathVariable("book-id") Integer id,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.updateBookArchived(id,connectedUser));
    }

    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Integer> borrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.borrowBook(bookId,connectedUser));
    }

    @PatchMapping("/borrow/return/{book-id}")
    public ResponseEntity<Integer> returnBorrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.returnBorrowBook(bookId,connectedUser));
    }

    @PatchMapping("/borrow/returnapproved/{book-id}")
    public ResponseEntity<Integer> approveReturnBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.approveReturnBook(bookId,connectedUser));
    }

    @PostMapping(value = "/cover/{book-id}",consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookCoverPicture(
        @PathVariable("book-id") Integer bookId,
        @Parameter()
        @RequestPart("file") MultipartFile file,
        Authentication connectedUser
    ){
        service.uploadBookCoverPicture(file,connectedUser,bookId);
        return ResponseEntity.accepted().build();
    }
}
