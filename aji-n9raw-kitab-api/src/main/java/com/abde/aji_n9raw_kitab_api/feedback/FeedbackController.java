package com.abde.aji_n9raw_kitab_api.feedback;

import com.abde.aji_n9raw_kitab_api.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/save")
    public ResponseEntity<Integer> save(
            @Valid @RequestBody FeedbackRequest feedbackRequest,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(feedbackService.save(feedbackRequest,connectedUser));
    }

    @GetMapping("/book/{book-Id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> getAllBooks(
        @RequestParam(name = "page",required = false,defaultValue = "0") int page,
        @RequestParam(name = "size",required = false,defaultValue = "10") int size,
        @PathVariable("book-Id") Integer bookId,
        Authentication connectedUser
    ){
        return ResponseEntity.ok(feedbackService.getAllBooks(page,size,bookId,connectedUser));
    }

}
