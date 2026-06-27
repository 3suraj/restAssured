# API Automation Framework

A production-grade API automation framework built with Java 21, REST Assured 5,
JUnit 5, and Allure — designed for use across multiple teams and environments.

## Architecture

Tests never import REST Assured directly. All HTTP is hidden behind an ApiClient
service layer. This means the HTTP library can be swapped without changing a
single test file.

```
Test Layer → Assertion DSL → ApiClient Layer → Core Framework → Config/Infra
```

## Tech Stack

| Layer         | Technology                        |
|---------------|-----------------------------------|
| HTTP Client   | REST Assured 5.4                  |
| Test Runner   | JUnit 5 (parallel execution)      |
| Reporting     | Allure                            |
| Auth          | OAuth2 with auto-refresh          |
| Serialisation | Jackson + Lombok @Builder         |
| Async         | Awaitility (no Thread.sleep)      |
| CI/CD         | GitHub Actions (matrix: dev, qa)  |
| Containers    | Docker + docker-compose           |
| AI Layer      | GPT-4o for test generation        |

## Run Locally

```bash
# Default (qa environment)
mvn test
 
# Specific environment
mvn test -Puat
 
# Smoke tests only
mvn test -Dgroups=smoke
 
# Generate Allure report
mvn allure:report
```

## Run in Docker

```bash
docker compose up --build
ENV=uat docker compose up --build
```

## Key Design Decisions

**ThreadLocal for parallel safety** — RequestSpecification is stored in a
ThreadLocal so 50 parallel tests never corrupt each other's auth state.

**TokenManager singleton** — OAuth2 token is fetched once and auto-refreshed
30s before expiry. No test ever manages auth.

**Factory pattern for test data** — PostRequestFactory has named scenario
methods. One rename = one change, not 50.

**Secret masking** — MaskedLoggingFilter redacts Authorization headers and
sensitive body fields before writing to logs or Allure reports.
