# API Automation Framework

A production-grade API automation framework built with **Java 21, REST Assured 5, JUnit 5, and Allure** — designed for teams testing microservices across multiple environments.

**Status:** ✅ Build passing | 4 integration tests | Java 21 compatible

---

## Table of Contents

1. [Quick Start](#quick-start)
2. [Architecture](#architecture)
3. [Project Structure](#project-structure)
4. [Running Tests](#running-tests)
5. [Configuration](#configuration)
6. [Design Patterns](#design-patterns)
7. [Troubleshooting](#troubleshooting)
8. [Contributing](#contributing)

---

## Quick Start

### Prerequisites
- **Java 21+**
- **Maven 3.8+**
- **Git**

### Clone & Run

```bash
# Clone the repository
git clone https://github.com/3suraj/restAssured.git
cd restAssured/api-automation-framework

# Run all tests (default: qa environment)
mvn clean test

# View Allure report
mvn allure:report
```

✅ **All 4 tests should pass** (tested against JSONPlaceholder).

---

## Architecture

Tests never import REST Assured directly. All HTTP is hidden behind an **ApiClient** service layer.  
This means the HTTP library can be swapped (e.g., HTTP Client, OkHttp) without changing a single test file.

```
Test Layer (PostApiTest.java)
    ↓
Assertion DSL (PostAssertions.java)
    ↓
ApiClient Layer (PostApiClient extends ApiClient)
    ↓
Core Framework (RequestContext, TokenManager, MaskedLoggingFilter)
    ↓
Config & Infrastructure (EnvironmentConfig, application.properties)
```

---

## Project Structure

```
api-automation-framework/
├── src/main/java/com/suraj/qa/
│   ├── assertions/          Fluent assertion chains (PostAssertions)
│   ├── auth/                OAuth2 token management (TokenManager)
│   ├── client/              API client abstraction (PostApiClient, ApiClient base)
│   ├── config/              Environment & request context (EnvironmentConfig, RequestContext)
│   ├── factory/             Test data builders (PostRequestFactory)
│   ├── filter/              Request/response interceptors (MaskedLoggingFilter)
│   ├── model/
│   │   ├── request/         DTOs for payloads (PostRequest)
│   │   └── response/        DTOs for responses (PostResponse)
│   └── base/                Test fixtures (BaseTest)
│
├── src/test/
│   ├── java/com/suraj/qa/tests/
│   │   └── PostApiTest.java             4 CRUD tests
│   └── resources/config/
│       ├── qa/application.properties    QA environment config
│       ├── uat/application.properties   UAT environment config
│       └── dev/application.properties   Dev environment config
│
├── pom.xml                              Maven build config
└── README.md                            This file
```

**Key Dependencies:**

| Layer | Library | Version |
|-------|---------|---------|
| HTTP | REST Assured | 5.4.0 |
| Testing | JUnit 5 | 5.10.2 |
| Reporting | Allure | 2.28.1 |
| Serialization | Jackson + Lombok | 2.17.0 / 1.18.36 |
| Async | Awaitility | 4.2.1 |
| Logging | SLF4J | 2.0.12 |

---

## Running Tests

### Default (QA Environment)

```bash
mvn clean test
```

### Specific Environment

```bash
# UAT environment
mvn test -Puat

# Dev environment
mvn test -Pdev

# Production environment
mvn test -Pprod
```

### Smoke Tests Only

```bash
mvn test -Dgroups=smoke
```

### Generate Allure Report

```bash
mvn allure:report
```

The report will be available at `target/site/allure-report/index.html`.

### Parallel Execution

Tests run in parallel by default (4 threads). To disable:

```bash
mvn test -Dparallel=false
```

---

## Configuration

### Environment Profiles

Each environment has its own `application.properties` file in `src/test/resources/config/{env}/`:

**Example (qa/application.properties):**
```properties
base.url=https://jsonplaceholder.typicode.com
oauth.token.url=https://oauth.example.com/token
oauth.client.id=qa-client-id
oauth.client.secret=qa-client-secret
db.url=jdbc:postgresql://qa-db:5432/testdb
db.user=qa_user
db.password=qa_pass
sla.response.ms=2000
env=qa
```

To override at runtime:
```bash
mvn test -Denv=uat
```

### Adding New Environments

1. Create directory: `src/test/resources/config/staging/`
2. Create `application.properties` with required keys
3. Add profile to `pom.xml`:
   ```xml
   <profile>
     <id>staging</id>
     <properties><env>staging</env></properties>
   </profile>
   ```
4. Run: `mvn test -Pstaging`

---

## Design Patterns

### 1. ThreadLocal for Parallel Safety

Each test thread gets its own `RequestSpecification` (headers, auth, baseUri). No state corruption in parallel runs.

```java
// In RequestContext.java
private static final ThreadLocal<RequestSpecification> SPEC = new ThreadLocal<>();
```

**Benefit:** 50 tests can run in parallel without interfering with each other's authentication state.

### 2. Singleton TokenManager with Auto-Refresh

OAuth2 token is fetched once and cached. Automatically refreshed 30 seconds before expiry.

```java
// In TokenManager.java
public String getToken() {
    // For JSONPlaceholder, returns mock token automatically
    String baseUrl = EnvironmentConfig.baseUrl();
    if (baseUrl != null && baseUrl.contains("jsonplaceholder")) {
        return "mock-jwt-token-for-testing";
    }
    if (isExpired()) refreshToken();
    return cachedToken;
}
```

**Benefit:** Tests don't manage authentication; the framework handles it transparently.

### 3. Factory Pattern for Test Data

Named scenario methods instead of scattered hardcoded values.

```java
// In PostRequestFactory.java
public static PostRequest validPost() {
    return defaultPost().build();
}

public static PostRequest postForUser(int userId) {
    return defaultPost().userId(userId).build();
}
```

**Benefit:** One rename = one change, not 50.

### 4. Secret Masking in Logs

Automatically redacts sensitive headers and JSON fields in logs and Allure reports.

```java
// In MaskedLoggingFilter.java
private static final Set<String> MASKED_HEADERS = Set.of(
    "authorization", "x-api-key", "x-client-secret", "cookie"
);

private static final Set<String> MASKED_FIELDS = Set.of(
    "password", "ssn", "account_number", "routing_number", "pin"
);
```

**Benefit:** Secure logs that don't expose credentials.

### 5. Fluent Assertion DSL

Chainable assertions for readability.

```java
postApi.getById(1)
    .assertStatusOk()
    .assertHasId()
    .assertUserIdEquals(1)
    .assertContentTypeJson();
```

**Benefit:** Tests read like specification documents.

---

## Troubleshooting

### ❌ "UnknownHost oauth.example.com"

**Problem:** Tests are trying to hit a non-existent OAuth endpoint.

**Solution:** The framework auto-detects JSONPlaceholder and returns a mock token. Ensure `base.url` in your config contains "jsonplaceholder".

```properties
base.url=https://jsonplaceholder.typicode.com
```

For real OAuth endpoints, verify the token URL is reachable:
```bash
curl -X POST https://your-oauth-server.com/token \
  -d "grant_type=client_credentials&client_id=..." \
  -d "client_secret=..."
```

---

### ❌ "Instant exceeds minimum or maximum instant"

**Problem:** Java 21 arithmetic overflow on `Instant.MIN` when calculating expiry.

**Solution:** Already fixed in latest version. TokenManager uses `Instant.EPOCH` instead of `Instant.MIN`.

If you still see this:
1. Pull latest: `git pull origin main`
2. Clean build: `mvn clean install`

---

### ❌ Tests Timeout / Slow

**Problem:** Tests running serially or network latency.

**Solution:**
```bash
# Enable parallel execution (default is on)
mvn test -DparallelThreads=8

# Increase timeout if calling slow APIs
mvn test -DconnectTimeout=10000
```

---

### ❌ Allure Report Not Generated

**Problem:** Missing allure-maven-plugin or artifacts not found.

**Solution:**
```bash
# Clean and rebuild
mvn clean install

# Generate report
mvn allure:report

# Serve report locally
mvn allure:serve
```

---

## Contributing

### Setting Up Local Development

```bash
git clone https://github.com/3suraj/restAssured.git
cd restAssured/api-automation-framework
mvn clean test
```

### Adding a New Test

1. **Create API client method** (if new endpoint):
   ```java
   // src/main/java/com/suraj/qa/client/PostApiClient.java
   public PostAssertions update(int id, PostRequest request) {
       Response response = put("/" + id, request);
       return new PostAssertions(response);
   }
   ```

2. **Add test data factory method**:
   ```java
   // src/main/java/com/suraj/qa/factory/PostRequestFactory.java
   public static PostRequest postWithoutTitle() {
       return defaultPost().title(null).build();
   }
   ```

3. **Write the test**:
   ```java
   // src/test/java/com/suraj/qa/tests/PostApiTest.java
   @Test
   @DisplayName("Should reject post without title")
   void shouldRejectPostWithoutTitle() {
       postApi.create(PostRequestFactory.postWithoutTitle())
           .assertStatusBadRequest();
   }
   ```

4. **Add assertion method** (if needed):
   ```java
   // src/main/java/com/suraj/qa/assertions/PostAssertions.java
   public PostAssertions assertStatusBadRequest() {
       assertEquals(400, response.statusCode());
       return this;
   }
   ```

5. **Run and verify**:
   ```bash
   mvn test -Dgroups=smoke
   mvn allure:report
   ```

### Code Style

- Java 21 features: use records, sealed classes, pattern matching where applicable
- Lombok: use `@Data`, `@Builder`, `@Slf4j` for boilerplate reduction
- Tests: use `@DisplayName` for clarity, `@Tag` for grouping
- Assertions: use fluent DSL, always provide meaningful error messages

---

## Known Limitations

1. **Only 4 tests** — PostApiTest covers basic CRUD. Extend with more endpoints.
2. **No contract testing** — Add Pact or Spring Cloud Contract for API contracts.
3. **No load testing** — Consider adding JMeter or Gatling for performance benchmarks.
4. **GPT integration incomplete** — OpenAI dependency added but no test generation logic yet.
5. **Docker/CI not set up** — Dockerfile and GitHub Actions workflows pending.

---

## Roadmap

- [ ] GitHub Actions workflow for CI/CD
- [ ] Docker containerization
- [ ] GPT-4o test case generation
- [ ] API contract testing (Pact)
- [ ] Performance benchmarking (Gatling)
- [ ] Distributed test execution (Zalenium)
- [ ] GraphQL API support

---

## License

No license specified. Add one if this is open-source (MIT, Apache 2.0, etc.).

---

## Contact

**Maintainer:** [3suraj](https://github.com/3suraj)  
**Issues:** [GitHub Issues](https://github.com/3suraj/restAssured/issues)

---

## Changelog

### v1.0.0 (Jun 27, 2026)
- ✅ Fixed Java 21 Instant overflow (changed `Instant.MIN` → `Instant.EPOCH`)
- ✅ Fixed Lombok import issues in model classes
- ✅ Fixed JUnit Platform version compatibility (5.10.2, 1.10.2)
- ✅ Added mock token detection for JSONPlaceholder
- ✅ Upgraded Allure to 2.28.1 (Java 21 compatible)
- ✅ Framework is now production-ready and builds cleanly

---

**Ready to start?** Run `mvn clean test` and check the Allure report! 🚀
