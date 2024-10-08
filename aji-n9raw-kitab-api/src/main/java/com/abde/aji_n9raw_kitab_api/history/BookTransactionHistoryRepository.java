package com.abde.aji_n9raw_kitab_api.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer>{

    @Query("""
    SELECT history FROM BookTransactionHistory history
    WHERE history.user.id == :userId
""")
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);


    //IDK WHY HERE THE RETURNED SHOULD BE FALSE IF WE WANT TO FIND THE BOOKS THAT ARE RETURNED

    @Query("""
    SELECT history FROM BookTransactionHistory history
    WHERE history.book.owner.id == :userId
    AND history.returned == false
""")
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);


    // IF THE USER IS ALREADY BORROWING THIS BOOK
    @Query(
            """
    SELECT
    (COUNT(*) > 0) AS isBorrowed
    FROM BookTransactionHistory bookTransactionHistory
    WHERE bookTransactionHistory.book.id = :bookId AND
    bookTransactionHistory.user.id = :userId AND
    bookTransactionHistory.returnApproved = false
"""
    )
    boolean isAlreadyBorrowedByUser(Integer bookId, Integer userId);

    @Query("""
        SELECT bookTransaction FROM BookTransactionHistory bookTransaction
        WHERE bookTransaction.book.id = :bookId
        AND bookTransaction.user.id = :userId
        AND bookTransaction.returned = false
        AND bookTransaction.returnApproved = false
""")
    Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer userId);

    @Query("""
        SELECT bookTransaction FROM BookTransactionHistory bookTransaction
        WHERE bookTransaction.book.id = :bookId
        AND bookTransaction.book.owner.id = :userId
        AND bookTransaction.returned = true
        AND bookTransaction.returnApproved = false
""")
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Integer bookId, Integer id);
}
