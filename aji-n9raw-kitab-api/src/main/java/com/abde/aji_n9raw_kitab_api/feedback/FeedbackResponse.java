package com.abde.aji_n9raw_kitab_api.feedback;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackResponse {


    private Double note;
    private String comment;
    private boolean isOwnFeedback;

}
