package com.la.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AttemptResponse {

    private boolean correct;
    private Integer answer;
    private String analysis;
    private int delta;
    private int newMastery;
    private boolean wrongBookAdded;
    private LocalDate reviewNext;
}
