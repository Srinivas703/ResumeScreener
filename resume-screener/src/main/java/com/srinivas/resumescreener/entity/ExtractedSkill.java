package com.srinivas.resumescreener.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "extracted_skills")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractedSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(nullable = false)
    private String skillName;

    // e.g. BEGINNER, INTERMEDIATE, ADVANCED - as inferred by the AI model
    private String proficiency;
}
