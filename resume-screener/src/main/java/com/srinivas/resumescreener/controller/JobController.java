package com.srinivas.resumescreener.controller;

import com.srinivas.resumescreener.dto.JobRequest;
import com.srinivas.resumescreener.entity.JobPosting;
import com.srinivas.resumescreener.service.JobService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Job posting management for recruiters")
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<JobPosting> createJob(@Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(jobService.createJob(request));
    }

    @GetMapping
    public ResponseEntity<List<JobPosting>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPosting> getJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @GetMapping("/my-postings")
    public ResponseEntity<List<JobPosting>> getMyJobs() {
        return ResponseEntity.ok(jobService.getJobsByCurrentRecruiter());
    }
}
