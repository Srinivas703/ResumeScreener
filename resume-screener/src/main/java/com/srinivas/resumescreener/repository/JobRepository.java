package com.srinivas.resumescreener.repository;

import com.srinivas.resumescreener.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<JobPosting, Long> {
    List<JobPosting> findByRecruiterId(Long recruiterId);
}
