package com.abde.aji_n9raw_kitab_api.feedback;


import jakarta.validation.constraints.*;
import org.springframework.lang.NonNull;

public record FeedbackRequest(
        @Positive(message = "200")
                @Min(value = 1,message = "201")
                @Max(value = 5,message = "202")
        Double note,
        @NotNull(message = "203")
        @NotEmpty(message = "203")
        @NotBlank(message = "203")
        String comment,
        @NotNull(message = "204")
        Integer bookId
) {
}
