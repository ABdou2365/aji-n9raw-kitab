package com.abde.aji_n9raw_kitab_api.book;

import com.abde.aji_n9raw_kitab_api.common.BaseEntity;
import com.abde.aji_n9raw_kitab_api.feedback.Feedback;
import com.abde.aji_n9raw_kitab_api.history.BookTransactionHistory;
import com.abde.aji_n9raw_kitab_api.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Book extends BaseEntity {
    private String title;
    private String author;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;


    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;

    @Transient
    public double getRate(){
        if(feedbacks == null || feedbacks.isEmpty()){
            return 0.0;
        }
        var rate = this.feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);
        // math bounding if it's 3.25 it will be 3 , if it's 3.88 it will be 4;
        double roundedRate = Math.round(rate * 10.0)/10.0;
        return roundedRate;
    }
}
