package com.srinivas.resumescreener.controller;

import com.srinivas.resumescreener.dto.MatchScoreResponse;
import com.srinivas.resumescreener.service.MatchingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@Tag(name = "Matching", description = "AI-driven candidate-to-job matching and scoring")
public class MatchController {

    private final MatchingService matchingService;

    @PostMapping("/evaluate")
    public ResponseEntity<MatchScoreResponse> evaluate(@RequestParam Long candidateId, @RequestParam Long jobId) {
        return ResponseEntity.ok(matchingService.evaluateMatch(candidateId, jobId));
    }

    @GetMapping("/job/{jobId}/candidates")
    public ResponseEntity<List<MatchScoreResponse>> getRankedCandidates(@PathVariable Long jobId) {
        return ResponseEntity.ok(matchingService.getRankedCandidatesForJob(jobId));
    }

    @GetMapping("/candidate/{candidateId}/jobs")
    public ResponseEntity<List<MatchScoreResponse>> getRankedJobs(@PathVariable Long candidateId) {
        return ResponseEntity.ok(matchingService.getRankedJobsForCandidate(candidateId));
    }
}
