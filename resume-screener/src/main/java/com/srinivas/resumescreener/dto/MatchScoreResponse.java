package com.srinivas.resumescreener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchScoreResponse {
    private Long matchId;
    private Long candidateId;
    private String candidateName;
    private Long jobId;
    private String jobTitle;
    private Double matchScore;
    private String matchReasoning;
}
