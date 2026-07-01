package com.srinivas.resumescreener.service;

import com.srinivas.resumescreener.dto.AiExtractionResponse;
import com.srinivas.resumescreener.entity.Candidate;
import com.srinivas.resumescreener.entity.ExtractedSkill;
import com.srinivas.resumescreener.exception.ResourceNotFoundException;
import com.srinivas.resumescreener.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final ResumeParserService resumeParserService;
    private final AiExtractionService aiExtractionService;

    /**
     * Full pipeline: extract text from the uploaded PDF, send it to the LLM for
     * structured extraction, then persist the parsed profile + skills.
     */
    @Transactional
    public Candidate uploadAndParseResume(MultipartFile file) {
        Candidate candidate = getCurrentCandidate();

        String resumeText = resumeParserService.extractText(file);
        AiExtractionResponse extracted = aiExtractionService.extractResumeData(resumeText);

        candidate.setRawResumeText(resumeText);
        candidate.setResumeFileName(file.getOriginalFilename());
        candidate.setSummary(extracted.getSummary());
        candidate.setYearsOfExperience(extracted.getYearsOfExperience());
        candidate.setEducation(extracted.getEducation());
        candidate.setCertifications(extracted.getCertifications());
        candidate.setUpdatedAt(LocalDateTime.now());

        candidate.getSkills().clear();
        if (extracted.getSkills() != null) {
            List<ExtractedSkill> skills = new ArrayList<>();
            for (AiExtractionResponse.SkillDto dto : extracted.getSkills()) {
                skills.add(ExtractedSkill.builder()
                        .candidate(candidate)
                        .skillName(dto.getSkillName())
                        .proficiency(dto.getProficiency())
                        .build());
            }
            candidate.getSkills().addAll(skills);
        }

        return candidateRepository.save(candidate);
    }

    public Candidate getCurrentCandidate() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return candidateRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found for current user"));
    }

    public Candidate getCandidateById(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));
    }

    /** Comma-separated skill names, used when building AI matching prompts. */
    public String getSkillsAsCsv(Candidate candidate) {
        if (candidate.getSkills() == null || candidate.getSkills().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (ExtractedSkill skill : candidate.getSkills()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(skill.getSkillName()).append(" (").append(skill.getProficiency()).append(")");
        }
        return sb.toString();
    }
}
