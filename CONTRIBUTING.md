# Contributing to API Automation Framework

Thank you for your interest in improving this framework! This guide will help you get started.

## 🚀 Getting Started

### 1. Fork & Clone

```bash
git clone https://github.com/YOUR_USERNAME/restAssured.git
cd restAssured/api-automation-framework
```

### 2. Set Up Local Environment

```bash
# Install dependencies
mvn clean install

# Run tests
mvn test

# View Allure report
mvn allure:serve
```

### 3. Create a Branch

```bash
git checkout -b feature/my-feature
# or
git checkout -b fix/bug-name
```

---

## 📋 Before You Start

### Understand the Architecture

- **Tests** live in `src/test/java/com/suraj/qa/tests/`
- **ApiClient** in `src/main/java/com/suraj/qa/client/` (test layer never touches REST Assured)
- **Models** in `src/main/java/com/suraj/qa/model/` (request/response DTOs)
- **Factories** in `src/main/java/com/suraj/qa/factory/` (test data builders)
- **Config** in `src/main/java/com/suraj/qa/config/` (environment & RequestContext)

### Code Style Guidelines

**Java:**
- Use Java 21 features (records, sealed classes, pattern matching)
- Use Lombok (`@Data`, `@Builder`, `@Slf4j`) to reduce boilerplate
- Write fluent APIs where possible

**Tests:**
- Use `@DisplayName` for clarity: ❌ `test1()` → ✅ `shouldReturnPostById()`
- Use `@Tag("smoke")`, `@Tag("regression")` for grouping
- Always provide meaningful assertion error messages
- Use factory methods: `PostRequestFactory.validPost()` instead of hardcoded JSON

**Naming:**
- Tests: `ShouldXXX` or `XXXTest`
- ApiClients: `XxxApiClient`
- Factories: `XxxRequestFactory` or `XxxDataFactory`
- Assertions: `XxxAssertions`

---

## ✨ Adding a New Test

### Step 1: Create Test Data Factory

```java
// src/main/java/com/suraj/qa/factory/UserRequestFactory.java
public class UserRequestFactory {
    public static UserRequest.UserRequestBuilder defaultUser() {
        return UserRequest.builder()
            .name("Test User")
            .email("test@example.com")
            .active(true);
    }
    
    public static UserRequest validUser() {
        return defaultUser().build();
    }
    
    public static UserRequest inactiveUser() {
        return defaultUser().active(false).build();
    }
}
```

### Step 2: Create/Extend ApiClient

```java
// src/main/java/com/suraj/qa/client/UserApiClient.java
public class UserApiClient extends ApiClient {
    public UserApiClient() {
        super("/users");
    }
    
    public UserAssertions create(UserRequest request) {
        Response response = post("", request);
        return new UserAssertions(response);
    }
}
```

### Step 3: Create Assertion DSL

```java
// src/main/java/com/suraj/qa/assertions/UserAssertions.java
public class UserAssertions {
    private final Response response;
    
    public UserAssertions(Response response) {
        this.response = response;
    }
    
    public UserAssertions assertStatusCreated() {
        assertEquals(201, response.statusCode(), "Expected 201 Created");
        return this;
    }
    
    public UserAssertions assertUserActive() {
        UserResponse body = response.as(UserResponse.class);
        assertTrue(body.isActive(), "User should be active");
        return this;
    }
}
```

### Step 4: Write the Test

```java
// src/test/java/com/suraj/qa/tests/UserApiTest.java
@Epic("Users API")
@Feature("CRUD Operations")
class UserApiTest extends BaseTest {
    
    private final UserApiClient userApi = new UserApiClient();
    
    @Test
    @Story("Create user")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should create user and return 201")
    @Tag("smoke")
    void shouldCreateUserSuccessfully() {
        userApi.create(UserRequestFactory.validUser())
            .assertStatusCreated()
            .assertUserActive();
    }
}
```

### Step 5: Run & Verify

```bash
mvn clean test
mvn allure:serve
```

---

## 🧪 Testing Guidelines

### Do's ✅

- ✅ Use descriptive test names: `shouldReturnUserById` not `test1`
- ✅ Follow Arrange-Act-Assert pattern (implicit in fluent DSL)
- ✅ Group related tests in the same class
- ✅ Use `@BeforeEach` / `@AfterEach` for setup/cleanup
- ✅ Test one behavior per test
- ✅ Use factories for test data

### Don'ts ❌

- ❌ Never import REST Assured in tests
- ❌ Don't hardcode URLs, use `EnvironmentConfig.baseUrl()`
- ❌ Don't create magic strings; use constants or factories
- ❌ Don't test implementation details; test contracts
- ❌ Don't sleep; use Awaitility instead
- ❌ Don't ignore test failures; fix them before committing

---

## 📝 Commit Messages

Follow conventional commits:

```bash
git commit -m "feat: Add user creation endpoint tests"
git commit -m "fix: Correct OAuth token refresh interval"
git commit -m "docs: Update README with contribution guide"
git commit -m "test: Add parallel execution safety tests"
git commit -m "chore: Upgrade REST Assured to 5.5.0"
```

---

## 🔄 Submitting a Pull Request

1. **Push your branch:**
   ```bash
   git push origin feature/my-feature
   ```

2. **Open a PR on GitHub** with:
   - Descriptive title: `Add X API test suite`
   - Reference related issue: `Closes #42`
   - List changes: `- Added 5 new tests` `- Fixed token refresh bug`
   - Link to Allure report (if applicable)

3. **Wait for CI/CD:**
   - GitHub Actions will run `mvn clean test -Pqa -Puat`
   - All tests must pass
   - Code will be reviewed

4. **Address feedback:**
   - Make requested changes
   - Push to the same branch (PR auto-updates)

5. **Merge:**
   - Once approved, maintainer will merge

---

## 🐛 Reporting Bugs

If you find a bug:

1. Check if it's already reported: [GitHub Issues](https://github.com/3suraj/restAssured/issues)
2. Create a new issue with:
   - **Title:** Brief description
   - **Environment:** Java version, Maven version
   - **Steps to reproduce:** Clear steps
   - **Expected vs actual:** What should happen vs what did
   - **Error log:** Full stack trace (if applicable)

---

## 💡 Suggesting Enhancements

Have an idea? Open a feature request:

1. Go to [GitHub Issues](https://github.com/3suraj/restAssured/issues)
2. Click "New Issue" → "Feature request"
3. Describe:
   - **Problem you're solving**
   - **Proposed solution**
   - **Why it would be useful**

---

## 📚 Resources

- [REST Assured Docs](https://rest-assured.io/)
- [JUnit 5 Docs](https://junit.org/junit5/docs/current/user-guide/)
- [Allure Docs](https://docs.qameta.io/allure/)
- [Lombok Docs](https://projectlombok.org/)

---

## ❓ Questions?

- Open an issue on GitHub
- Check the [README](./api-automation-framework/README.md)
- Review existing test examples

---

**Thank you for contributing! 🙏**
