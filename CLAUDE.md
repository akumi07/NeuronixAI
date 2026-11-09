# NeuronixAI backend

Spring Boot REST API for NeuronixAI: JWT authentication, chat conversations backed by OpenAI (Spring AI), and document RAG (PDF upload to S3, chunking, pgvector embeddings, retrieval and answering).

The frontend is a separate repo: `D:\Neuronix\frontend\NeuronixAi-frontend` (github.com/akumi07/NeuronixAi-frontend).

## Stack
- Java 21, Spring Boot 4.0.7, Maven (use the wrapper)
- Spring AI 2.0 with OpenAI (chat: gpt-4o-mini, embeddings: text-embedding-3-small, 1536 dimensions)
- PostgreSQL with pgvector, Flyway, Spring Data JPA (`ddl-auto=validate`)
- AWS S3 (bucket `neuronix-ai-documents`, region eu-north-1), PDFBox, jjwt (HS256)

## Build and test
```
.\mvnw.cmd clean verify          # Windows: build + tests
./mvnw clean verify              # macOS / Linux
.\mvnw.cmd test                  # tests only
.\mvnw.cmd spring-boot:run       # run locally (port 8080)
```
`NeuronixBackendApplicationTests.contextLoads` starts the full context, so it needs these environment variables and a reachable Postgres with pgvector:
`DB_URL` (must start with `jdbc:`), `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` (32+ bytes), `OPENAI_API_KEY`.
S3 calls also need AWS credentials (`AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY` or a profile).
Health check: `GET http://localhost:8080/actuator/health`.

## Package structure (`com.neuronix`)
- `ai` – `LlmClient` abstraction and its OpenAI implementation
- `auth` – register / login / logout / password endpoints and DTOs
- `chat` – conversations and messages (entities, repositories, services, controllers)
- `config` – `SecurityConfig`, `S3Config`
- `document` – document upload and storage (controller, dto, entity, repository, service)
- `exception` – domain exceptions and `GlobalExceptionHandler`
- `rag` – chunking, embeddings, retrieval and RAG answering (controller, embedding, retrieval, service)
- `security` – JWT service and filter, user details, current user, token revocation
- `user` – user entity, repository, service, controller

## Flyway migrations (`src/main/resources/db/migration`)
- V1 create users table
- V2 create conversations table
- V3 create messages table
- V4 create documents table
- V5 create document_chunks table
- V6 add embeddings (`vector(1536)`) to document_chunks (requires the pgvector extension)

## NeuronixAI rules
- One ticket, one branch, one PR: feature/S1-02-short-name.
- Never commit .env, .venv, target/, .DS_Store, keys or tokens.
- Never force-push, rewrite history, or delete shared branches. Never push or merge without asking.
- Flyway: never edit a merged migration; ask for the next V number before creating one.
- Public API contracts don't change without lead approval.
- Python deps only via uv add; commit pyproject.toml and uv.lock together.
- Python never rebuilds RAG; document questions go to the Java RAG API.
- A ticket is done only with evidence: build output, test counts, and anything not run stated.

## Deferred security work (fix before production)
- SEC-01 to SEC-12 from the team plan; the main one is that forgot-password returns an access token.
