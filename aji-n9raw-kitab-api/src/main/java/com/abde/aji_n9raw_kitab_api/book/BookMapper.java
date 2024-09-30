package com.abde.aji_n9raw_kitab_api.book;

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
                //to do later
                //.cover()
                .build();
    }
}
