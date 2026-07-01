package com.srinivas.resumescreener.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Maps the structured JSON we instruct the LLM to return
 * when extracting fields from a raw resume text.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiExtractionResponse {

    private String summary;
    private Integer yearsOfExperience;
    private String education;
    private String certifications;
    private List<SkillDto> skills;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SkillDto {
        private String skillName;
        private String proficiency;
    }
}
