package com.srinivas.resumescreener.service;

import com.srinivas.resumescreener.dto.JobRequest;
import com.srinivas.resumescreener.entity.JobPosting;
import com.srinivas.resumescreener.entity.User;
import com.srinivas.resumescreener.exception.ResourceNotFoundException;
import com.srinivas.resumescreener.repository.JobRepository;
import com.srinivas.resumescreener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobPosting createJob(JobRequest request) {
        User recruiter = getCurrentUser();

        JobPosting job = JobPosting.builder()
                .recruiter(recruiter)
                .title(request.getTitle())
                .description(request.getDescription())
                .requiredSkills(request.getRequiredSkills())
                .minExperience(request.getMinExperience())
                .location(request.getLocation())
                .build();

        return jobRepository.save(job);
    }

    public List<JobPosting> getAllJobs() {
        return jobRepository.findAll();
    }

    public JobPosting getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found with id: " + id));
    }

    public List<JobPosting> getJobsByCurrentRecruiter() {
        User recruiter = getCurrentUser();
        return jobRepository.findByRecruiterId(recruiter.getId());
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
