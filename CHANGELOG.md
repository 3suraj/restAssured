# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.1.0] - 2026-06-27

### 🎉 Added

- **Test Suites Expanded:**
  - `ErrorHandlingTest.java` - 404, null handling, SLA validation (3 tests)
  - `ContractTest.java` - API schema/response validation (3 tests)
  - `ParallelExecutionTest.java` - ThreadLocal safety verification (4 tests)
  - **Total: 14 tests** (up from 4)

- **CI/CD Pipeline:**
  - GitHub Actions workflow (`.github/workflows/test.yml`)
  - Multi-environment matrix testing (qa, uat)
  - Automated Allure report generation
  - Test results publishing to PR comments

- **Docker Support:**
  - `Dockerfile` - Multi-stage build for optimized image
  - `docker-compose.yml` - Multi-service orchestration
  - PostgreSQL service for integration tests
  - Environment-specific test isolation

- **Documentation:**
  - `CONTRIBUTING.md` - Complete contribution guide
  - `CHANGELOG.md` - This file
  - README expanded with troubleshooting & roadmap

- **Performance Testing:**
  - SLA assertion in error handling tests
  - Response time validation framework

- **Contract Testing:**
  - Response schema validation
  - Field presence verification
  - Value range constraints

### 🔧 Fixed (from v1.0.0)

- ✅ Java 21 compatibility: Changed `Instant.MIN` → `Instant.EPOCH`
- ✅ Lombok imports: Added missing annotations in model classes
- ✅ JUnit Platform: Upgraded and aligned all versions (5.10.2, 1.10.2)
- ✅ OAuth detection: Added mock token for JSONPlaceholder
- ✅ Allure compatibility: Upgraded to 2.28.1 (Java 21 support)

### 📚 Documentation

- Expanded README with architecture diagrams
- Added design patterns explanation with code examples
- Added troubleshooting section (5 common issues + solutions)
- Added step-by-step test authoring guide
- Added roadmap and known limitations

### 🔐 Security

- Verified secret masking in MaskedLoggingFilter
- Documented sensitive field redaction

---

## [1.0.0] - 2026-06-27

### 🎉 Initial Release

- **4 CRUD Tests:**
  - `shouldCreatePostSuccessfully()`
  - `shouldGetPostById()`
  - `shouldCreatePostForSpecificUser()`
  - `shouldDeletePost()`

- **Architecture:**
  - ApiClient abstraction layer (no REST Assured in tests)
  - Fluent assertion DSL
  - Factory pattern for test data
  - ThreadLocal for parallel safety

- **Features:**
  - OAuth2 token management with auto-refresh
  - Secret masking in logs
  - Multi-environment configuration (qa, uat, dev, prod)
  - Allure reporting integration

- **Tech Stack:**
  - Java 21
  - REST Assured 5.4.0
  - JUnit 5 5.10.2
  - Allure 2.25.0
  - Lombok 1.18.36
  - Jackson 2.17.0
  - Awaitility 4.2.1
  - SLF4J 2.0.12

- **Documentation:**
  - Basic README with tech stack and architecture
  - Key design decisions documented

---

## Roadmap (Planned)

- [ ] **GPT-4o Integration** - AI-assisted test case generation
- [ ] **Performance Benchmarking** - Gatling integration for load testing
- [ ] **API Contract Testing** - Pact framework integration
- [ ] **Advanced Mocking** - WireMock integration for service stubs
- [ ] **Distributed Execution** - Zalenium support for grid testing
- [ ] **GraphQL Support** - GraphQL API test layer
- [ ] **Mobile API Testing** - Device farm integration
- [ ] **Security Testing** - OWASP validation framework
- [ ] **Documentation Site** - MkDocs with examples

---

## [Unreleased]

### In Progress

- GPT test case generator implementation
- Advanced performance metrics dashboard

---

## How to Upgrade

### From v1.0.0 → v1.1.0

```bash
git pull origin main
mvn clean install
mvn test  # All 14 tests should pass
```

No breaking changes. All v1.0.0 code is compatible.

---

## Support

- 🐛 **Bug Reports:** [Issues](https://github.com/3suraj/restAssured/issues)
- 💬 **Discussions:** [GitHub Discussions](https://github.com/3suraj/restAssured/discussions)
- 📖 **Docs:** [README](./api-automation-framework/README.md)
- 🤝 **Contributing:** [CONTRIBUTING.md](./CONTRIBUTING.md)

---

## Version Compatibility

| Component | Version |
|-----------|---------|
| Java | 21+ |
| Maven | 3.8+ |
| REST Assured | 5.4.0+ |
| JUnit Platform | 1.10.2+ |
| Allure | 2.28.1+ |

---

**Last Updated:** June 27, 2026
