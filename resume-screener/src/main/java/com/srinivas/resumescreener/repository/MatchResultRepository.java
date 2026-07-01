package com.srinivas.resumescreener.repository;

import com.srinivas.resumescreener.entity.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {

    List<MatchResult> findByJobPostingIdOrderByMatchScoreDesc(Long jobId);

    List<MatchResult> findByCandidateIdOrderByMatchScoreDesc(Long candidateId);

    boolean existsByCandidateIdAndJobPostingId(Long candidateId, Long jobId);
}
