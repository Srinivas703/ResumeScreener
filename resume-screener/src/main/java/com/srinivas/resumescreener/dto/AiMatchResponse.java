package com.srinivas.resumescreener.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Maps the structured JSON returned by the LLM when scoring
 * a candidate profile against a job description.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiMatchResponse {
    private Double matchScore; // 0-100
    private String reasoning;
}
