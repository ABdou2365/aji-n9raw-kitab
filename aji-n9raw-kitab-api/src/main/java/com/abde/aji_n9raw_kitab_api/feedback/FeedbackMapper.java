package com.abde.aji_n9raw_kitab_api.feedback;

import com.abde.aji_n9raw_kitab_api.book.Book;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {

    public Feedback toFeedback(FeedbackRequest feedbackRequest) {
        return Feedback.builder()
                .note(feedbackRequest.note())
                .comment(feedbackRequest.comment())
                //This is often done when you need to associate a feedback entity
                // with an existing book in the database, and you
                // only need to pass the book's id rather than loading
                // the entire Book entity from the database.
                .book(Book.builder()
                        .id(feedbackRequest.bookId())
                        .build()
                )
                .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer id) {
        return FeedbackResponse.builder()
                    .note(feedback.getNote())
                    .comment(feedback.getComment())
                            .isOwnFeedback(Objects.equals(feedback.getCreatedBy(),id))
                .build();
    }
}
