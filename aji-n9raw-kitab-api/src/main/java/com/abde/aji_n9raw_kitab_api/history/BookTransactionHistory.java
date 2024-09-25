package com.abde.aji_n9raw_kitab_api.history;

import com.abde.aji_n9raw_kitab_api.book.Book;
import com.abde.aji_n9raw_kitab_api.common.BaseEntity;
import com.abde.aji_n9raw_kitab_api.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class BookTransactionHistory extends BaseEntity {


    @ManyToOne
    private User user;

    @ManyToOne
    private Book book;

    private boolean returned;

    private boolean returnApproved;

}
