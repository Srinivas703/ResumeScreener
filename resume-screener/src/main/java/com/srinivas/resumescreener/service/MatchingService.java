package com.srinivas.resumescreener.service;

import com.srinivas.resumescreener.dto.AiMatchResponse;
import com.srinivas.resumescreener.dto.MatchScoreResponse;
import com.srinivas.resumescreener.entity.Candidate;
import com.srinivas.resumescreener.entity.JobPosting;
import com.srinivas.resumescreener.entity.MatchResult;
import com.srinivas.resumescreener.repository.MatchResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final MatchResultRepository matchResultRepository;
    private final CandidateService candidateService;
    private final JobService jobService;
    private final AiExtractionService aiExtractionService;

    /**
     * Runs (or re-runs) an AI-driven match between a candidate and a job posting.
     * Persists the result so repeat lookups don't re-call the LLM unnecessarily.
     */
    @Transactional
    public MatchScoreResponse evaluateMatch(Long candidateId, Long jobId) {
        Candidate candidate = candidateService.getCandidateById(candidateId);
        JobPosting job = jobService.getJobById(jobId);

        String skillsCsv = candidateService.getSkillsAsCsv(candidate);

        AiMatchResponse aiResponse = aiExtractionService.scoreMatch(
                candidate.getSummary(),
                candidate.getYearsOfExperience(),
                candidate.getEducation(),
                candidate.getCertifications(),
                skillsCsv,
                job.getTitle(),
                job.getDescription(),
                job.getRequiredSkills(),
                job.getMinExperience()
        );

        MatchResult result = MatchResult.builder()
                .candidate(candidate)
                .jobPosting(job)
                .matchScore(aiResponse.getMatchScore())
                .matchReasoning(aiResponse.getReasoning())
                .build();

        result = matchResultRepository.save(result);

        return toResponse(result);
    }

    /** Returns all candidates ranked against a given job, highest score first. */
    public List<MatchScoreResponse> getRankedCandidatesForJob(Long jobId) {
        return matchResultRepository.findByJobPostingIdOrderByMatchScoreDesc(jobId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /** Returns all jobs ranked for a given candidate, highest score first. */
    public List<MatchScoreResponse> getRankedJobsForCandidate(Long candidateId) {
        return matchResultRepository.findByCandidateIdOrderByMatchScoreDesc(candidateId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private MatchScoreResponse toResponse(MatchResult result) {
        return MatchScoreResponse.builder()
                .matchId(result.getId())
                .candidateId(result.getCandidate().getId())
                .candidateName(result.getCandidate().getUser().getFullName())
                .jobId(result.getJobPosting().getId())
                .jobTitle(result.getJobPosting().getTitle())
                .matchScore(result.getMatchScore())
                .matchReasoning(result.getMatchReasoning())
                .build();
    }
}
