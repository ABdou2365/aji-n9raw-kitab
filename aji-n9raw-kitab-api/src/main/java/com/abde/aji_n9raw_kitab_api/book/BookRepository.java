package com.abde.aji_n9raw_kitab_api.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book,Integer>, JpaSpecificationExecutor<Book> {

    @Query(
            """
        SELECT book FROM Book book
        WHERE book.archived == false
        AND book.shareable == true
        AND book.owner.id != :userId
    """
    )
    Page<Book> findAllDisplayableBooks(Pageable pageable, Integer userId);



// THE OLD WAY
//    @Query(
//            """
//        SELECT book FROM Book book
//        WHERE book.archived = false
//        AND book.shareable = true
//        AND book.owner.id == :userId
//"""
//    )
//    Page<Book> findAllDisplayedBooksByOwner(Pageable pageable, Integer userId);
//
}
