package com.srinivas.resumescreener.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srinivas.resumescreener.dto.AiExtractionResponse;
import com.srinivas.resumescreener.dto.AiMatchResponse;
import com.srinivas.resumescreener.exception.AiServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles all calls to the LLM (OpenAI-compatible /v1/chat/completions endpoint).
 * Two responsibilities:
 *  1. Extract structured candidate data (skills, experience, education) from raw resume text
 *  2. Score a candidate profile against a job posting
 *
 * Both use strict "JSON-only" prompts so responses can be parsed reliably.
 */
@Service
@RequiredArgsConstructor
public class AiExtractionService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.ai.api-url}")
    private String apiUrl;

    @Value("${app.ai.api-key}")
    private String apiKey;

    @Value("${app.ai.model}")
    private String model;

    public AiExtractionResponse extractResumeData(String resumeText) {
        String promptTemplate = loadTemplate("prompts/extraction-prompt-template.txt");
        String prompt = promptTemplate.replace("{RESUME_TEXT}", truncate(resumeText, 12000));

        String rawJson = callLlm(prompt);

        try {
            return objectMapper.readValue(rawJson, AiExtractionResponse.class);
        } catch (Exception e) {
            throw new AiServiceException("Failed to parse AI extraction response as JSON: " + e.getMessage(), e);
        }
    }

    public AiMatchResponse scoreMatch(String candidateSummary, Integer candidateExperience, String candidateEducation,
                                       String candidateCertifications, String candidateSkills,
                                       String jobTitle, String jobDescription, String jobRequiredSkills,
                                       Integer jobMinExperience) {

        String promptTemplate = loadTemplate("prompts/matching-prompt-template.txt");
        String prompt = promptTemplate
                .replace("{CANDIDATE_SUMMARY}", nullSafe(candidateSummary))
                .replace("{CANDIDATE_EXPERIENCE}", String.valueOf(candidateExperience == null ? 0 : candidateExperience))
                .replace("{CANDIDATE_EDUCATION}", nullSafe(candidateEducation))
                .replace("{CANDIDATE_CERTIFICATIONS}", nullSafe(candidateCertifications))
                .replace("{CANDIDATE_SKILLS}", nullSafe(candidateSkills))
                .replace("{JOB_TITLE}", nullSafe(jobTitle))
                .replace("{JOB_DESCRIPTION}", truncate(nullSafe(jobDescription), 4000))
                .replace("{JOB_REQUIRED_SKILLS}", nullSafe(jobRequiredSkills))
                .replace("{JOB_MIN_EXPERIENCE}", String.valueOf(jobMinExperience == null ? 0 : jobMinExperience));

        String rawJson = callLlm(prompt);

        try {
            return objectMapper.readValue(rawJson, AiMatchResponse.class);
        } catch (Exception e) {
            throw new AiServiceException("Failed to parse AI match response as JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Calls the LLM's chat completions endpoint with a JSON-only system prompt,
     * and extracts + sanitizes the JSON content from the response.
     */
    private String callLlm(String userPrompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a precise JSON-only API. Never include markdown formatting, " +
                "code fences, or explanatory text outside the JSON object.");

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", new Object[]{systemMessage, userMessage});
        requestBody.put("temperature", 0.2);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            JsonNode response = restTemplate.postForObject(apiUrl, requestEntity, JsonNode.class);

            if (response == null || !response.has("choices")) {
                throw new AiServiceException("AI provider returned an unexpected response format");
            }

            String content = response.get("choices").get(0).get("message").get("content").asText();
            return sanitizeJson(content);

        } catch (AiServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new AiServiceException("Failed to call AI provider: " + e.getMessage(), e);
        }
    }

    /**
     * Strips markdown code fences (```json ... ```) some models add despite instructions,
     * and trims to the outermost JSON object braces as a safety net.
     */
    private String sanitizeJson(String content) {
        String cleaned = content.trim();
        cleaned = cleaned.replaceAll("^```json", "").replaceAll("^```", "").replaceAll("```$", "").trim();

        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return cleaned.substring(start, end + 1);
        }
        return cleaned;
    }

    private String loadTemplate(String classpathLocation) {
        // Reads via input stream (not getFile()) so this also works when the app
        // is packaged and run as a jar, not just from exploded classes.
        try (var inputStream = new ClassPathResource(classpathLocation).getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new AiServiceException("Could not load prompt template: " + classpathLocation, e);
        }
    }

    private String truncate(String text, int maxChars) {
        if (text == null) return "";
        return text.length() > maxChars ? text.substring(0, maxChars) : text;
    }

    private String nullSafe(String s) {
        return s == null ? "" : s;
    }
}
