# AI-Powered Resume Screener & Job Matching Platform

A Spring Boot backend that parses resumes (PDF), uses an LLM to extract structured candidate
data (skills, experience, education), and scores candidates against job postings using
AI-driven match reasoning — not just keyword overlap.

## Why this project exists

Most fresher backend projects are CRUD apps. This one demonstrates:
- Real LLM integration (prompt design, structured JSON output, response validation/sanitization)
- File processing (PDF text extraction with Apache PDFBox)
- Secure REST APIs with JWT authentication and role-based access (Candidate / Recruiter)
- Clean layered architecture (Controller → Service → Repository) with global exception handling
- API documentation via Swagger/OpenAPI

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT (jjwt) |
| Persistence | Spring Data JPA + MySQL |
| PDF Parsing | Apache PDFBox |
| AI | OpenAI-compatible Chat Completions API |
| Docs | springdoc-openapi (Swagger UI) |
| Build | Maven |

## Architecture

```
Client → Controller → Service → Repository → MySQL
                          │
                          └──→ AiExtractionService → LLM API (resume parsing + match scoring)
```

## Core Flow

1. **Candidate registers/logs in** → receives a JWT
2. **Candidate uploads resume (PDF)** → text extracted via PDFBox
3. **Raw resume text sent to LLM** with a strict JSON-only prompt → returns structured skills,
   experience, education, certifications
4. **Recruiter posts a job** with required skills, description, minimum experience
5. **Matching endpoint** sends both the candidate profile and job requirements to the LLM,
   which returns a **0–100 match score with reasoning** (not just keyword overlap)
6. Recruiters can view **ranked candidates per job**; candidates can view **ranked jobs** for
   their own profile

## API Endpoints

### Auth
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register as CANDIDATE or RECRUITER |
| POST | `/api/auth/login` | Login, returns JWT |

### Candidates
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/candidates/resume` | Upload resume PDF (triggers AI extraction) |
| GET | `/api/candidates/me` | Get current candidate's profile |
| GET | `/api/candidates/{id}` | Get a candidate by ID |

### Jobs
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/jobs` | Create a job posting (recruiter) |
| GET | `/api/jobs` | List all job postings |
| GET | `/api/jobs/{id}` | Get a job posting by ID |
| GET | `/api/jobs/my-postings` | List postings by the logged-in recruiter |

### Matching
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/matches/evaluate?candidateId=&jobId=` | Run AI match scoring |
| GET | `/api/matches/job/{jobId}/candidates` | Ranked candidates for a job |
| GET | `/api/matches/candidate/{candidateId}/jobs` | Ranked jobs for a candidate |

Full interactive docs available at `/swagger-ui.html` once the app is running.

## Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8+
- An API key from an OpenAI-compatible LLM provider

### 1. Clone and configure

```bash
git clone https://github.com/Srinivas703/resume-screener.git
cd resume-screener
```

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password

app.jwt.secret=<a random 32+ character string>
app.ai.api-key=<your LLM API key>
```

> In a real deployment, move `app.jwt.secret` and `app.ai.api-key` to environment variables
> instead of committing them — this repo ships with placeholders only.

### 2. Run

```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8080`. Swagger UI: `http://localhost:8080/swagger-ui.html`

### 3. Try it

```bash
# Register a candidate
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Srinivas","email":"srinivas@test.com","password":"password123","role":"CANDIDATE"}'

# Upload a resume (use the token from the response above)
curl -X POST http://localhost:8080/api/candidates/resume \
  -H "Authorization: Bearer <token>" \
  -F "file=@resume.pdf"
```

## Design Decisions

- **Why PDFBox over Tika?** Lighter weight and sufficient for text-based PDF resumes; scanned/image
  resumes would need OCR (not in scope for v1).
- **Why prompt-based extraction instead of regex/NLP rules?** Resumes vary wildly in format.
  An LLM handles that variance without brittle pattern matching — but the response is always
  validated and JSON-parsed before persisting, never trusted blindly.
- **Why store match results instead of computing on the fly?** Avoids re-calling the LLM (cost +
  latency) every time someone views the same candidate-job pair; results are cached as rows and
  can be manually re-evaluated when a resume or job posting changes.

## Possible Extensions
- OCR support for scanned resumes (Tesseract)
- Vector embeddings + pgvector/Redis for "find similar candidates" without re-calling the LLM per pair
- Async processing (Spring `@Async` or a queue) for bulk resume uploads
- Dockerfile + docker-compose for one-command local setup

Project Structure
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
