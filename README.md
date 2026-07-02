# AI-Powered Resume Screener & Job Matching Platform

A Spring Boot backend that parses resumes using AI and automatically scores candidates against job postings — built to demonstrate real LLM integration, not just CRUD operations.


## Features
- 🔐 JWT-based authentication with role-based access (Candidate / Recruiter)
- 📄 Resume upload and text extraction from PDF files (Apache PDFBox)
- 🤖 AI-powered resume parsing — extracts skills, experience, education, and certifications using an LLM
- 🎯 AI-driven candidate-to-job match scoring (0–100) with reasoning, not just keyword matching
- 📊 Ranked candidate lists per job posting, and ranked job recommendations per candidate
- 📚 Swagger/OpenAPI documentation for all endpoints
- 🏗️ Clean layered architecture (Controller → Service → Repository) with centralized exception handling

  ## Tech Stack
Java 17 · Spring Boot 3.2 · Spring Security (JWT) · Spring Data JPA · MySQL · Apache PDFBox · OpenAI-compatible LLM API · Maven · Swagger/OpenAPI

## What This Project Demonstrates
- Real-world LLM integration: prompt engineering, structured JSON output parsing, and response validation
- Secure REST API design with JWT authentication and role-based authorization
- File processing pipeline (PDF → text → AI extraction → database)
- Clean separation of concerns across Controller, Service, and Repository layers
- Global exception handling with meaningful HTTP status codes


  ## Getting Started
1. Clone the repo
2. Set your MySQL password and AI API key in `application.properties`
3. Run `mvn spring-boot:run`
4. Explore the API at `http://localhost:8080/swagger-ui.html`


## Project Structure

```
resume-screener/
├── src/main/java/com/srinivas/resumescreener/
│   ├── ResumeScreenerApplication.java   # Main entry point
│   │
│   ├── config/                          # App-level configuration
│   │   ├── SecurityConfig.java          # Spring Security + JWT setup
│   │   ├── AiClientConfig.java          # RestTemplate bean for LLM API calls
│   │   └── SwaggerConfig.java           # OpenAPI/Swagger docs config
│   │
│   ├── controller/                      # REST API endpoints
│   │   ├── AuthController.java          # Register / login
│   │   ├── CandidateController.java     # Resume upload, candidate profile
│   │   ├── JobController.java           # Job posting management
│   │   └── MatchController.java         # AI-driven match scoring
│   │
│   ├── service/                         # Business logic
│   │   ├── AuthService.java
│   │   ├── ResumeParserService.java     # PDF text extraction (PDFBox)
│   │   ├── AiExtractionService.java     # LLM calls: resume parsing + match scoring
│   │   ├── CandidateService.java
│   │   ├── JobService.java
│   │   └── MatchingService.java
│   │
│   ├── repository/                      # Spring Data JPA repositories
│   │   ├── UserRepository.java
│   │   ├── CandidateRepository.java
│   │   ├── JobRepository.java
│   │   └── MatchResultRepository.java
│   │
│   ├── entity/                          # Database models
│   │   ├── User.java
│   │   ├── Candidate.java
│   │   ├── JobPosting.java
│   │   ├── ExtractedSkill.java
│   │   └── MatchResult.java
│   │
│   ├── dto/                             # Request/response objects
│   ├── security/                        # JWT filter, token utils, user details
│   └── exception/                       # Global exception handling
│
├── src/main/resources/
│   ├── application.properties           # DB, JWT, and AI provider config
│   └── prompts/                         # LLM prompt templates
│       ├── extraction-prompt-template.txt
│       └── matching-prompt-template.txt
│
└── pom.xml
```
