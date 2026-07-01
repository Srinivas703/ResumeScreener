package com.srinivas.resumescreener.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private JobPosting jobPosting;

    private Double matchScore; // 0-100

    @Column(columnDefinition = "TEXT")
    private String matchReasoning; // AI-generated explanation

    @Builder.Default
    private LocalDateTime evaluatedAt = LocalDateTime.now();
}
