package com.srinivas.resumescreener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private String requiredSkills; // comma-separated

    private Integer minExperience;

    private String location;
}
