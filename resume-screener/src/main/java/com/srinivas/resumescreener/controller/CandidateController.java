package com.srinivas.resumescreener.controller;

import com.srinivas.resumescreener.entity.Candidate;
import com.srinivas.resumescreener.service.CandidateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
@Tag(name = "Candidates", description = "Resume upload and candidate profile management")
public class CandidateController {

    private final CandidateService candidateService;

    @PostMapping(value = "/resume", consumes = "multipart/form-data")
    public ResponseEntity<Candidate> uploadResume(@RequestParam("file") MultipartFile file) {
        Candidate candidate = candidateService.uploadAndParseResume(file);
        return ResponseEntity.ok(candidate);
    }

    @GetMapping("/me")
    public ResponseEntity<Candidate> getMyProfile() {
        return ResponseEntity.ok(candidateService.getCurrentCandidate());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getCandidate(@PathVariable Long id) {
        return ResponseEntity.ok(candidateService.getCandidateById(id));
    }
}
