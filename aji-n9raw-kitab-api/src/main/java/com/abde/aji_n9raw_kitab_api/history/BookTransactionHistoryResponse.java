package com.abde.aji_n9raw_kitab_api.history;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookTransactionHistoryResponse {

    private Integer id;
    private String title;
    private String author;
    private double rate;
    private boolean returned;
    private boolean returnedApproved;
}
