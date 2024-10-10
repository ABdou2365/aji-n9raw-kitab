package com.abde.aji_n9raw_kitab_api.feedback;

import com.abde.aji_n9raw_kitab_api.book.Book;
import com.abde.aji_n9raw_kitab_api.book.BookRepository;
import com.abde.aji_n9raw_kitab_api.common.PageResponse;
import com.abde.aji_n9raw_kitab_api.exception.OperationNotPermittedException;
import com.abde.aji_n9raw_kitab_api.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;



    public Integer save(FeedbackRequest feedbackRequest, Authentication connectedUser) {

        Integer bookId = feedbackRequest.bookId();
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException("No book found with id " + bookId)
        );
        if(!book.isArchived() && book.isShareable()){
            throw new OperationNotPermittedException("You cant borrow a book that is not shareable or archived");
        }
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You can not give a feedback to you own book");
        }
        Feedback feedback = feedbackMapper.toFeedback(feedbackRequest);
        return feedbackRepository.save(feedback).getId();

    }

    public PageResponse<FeedbackResponse> getAllBooks(int page, int size , Integer bookId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Feedback> feedbacks = feedbackRepository.getAllBooksById(pageable,bookId);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f,user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
