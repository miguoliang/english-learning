# English Learning

A spaced repetition learning platform built with Spring Boot, Kotlin Coroutines, and R2DBC.

## Prerequisites

- Java 25+
- PostgreSQL (or use Docker)
- Gradle 9.x

## Common Commands

### Build

```bash
# Compile the project
./gradlew compileKotlin

# Build without tests
./gradlew build -x test

# Build with tests
./gradlew build
```

### Run

```bash
# Run the application
./gradlew bootRun

# Run with specific profile
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Test

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "ClassName"

# Run with test containers
./gradlew test
```

### Code Quality

```bash
# Run all quality checks (Detekt + Ktlint)
./gradlew qualityCheck

# Ktlint - check formatting
./gradlew ktlintCheck

# Ktlint - auto-fix formatting
./gradlew ktlintFormat

# Detekt - static analysis
./gradlew detekt
```

### Database

```bash
# Run Flyway migrations
./gradlew flywayMigrate

# Clean database
./gradlew flywayClean

# View migration info
./gradlew flywayInfo
```

### Dependency Management

```bash
# List all dependencies
./gradlew dependencies

# Check for dependency updates
./gradlew dependencyUpdates
```

### GraalVM Native Image

```bash
# Build native image (requires GraalVM)
./gradlew nativeCompile

# Run native image tests
./gradlew nativeTest

# Build native image for Docker
./gradlew bootBuildImage

# Run the native executable
./build/native/nativeCompile/english-learning
```

**Prerequisites for native image:**
- GraalVM 21+ with `native-image` installed
- Or use Docker: `./gradlew bootBuildImage`

### Clean

```bash
# Clean build artifacts
./gradlew clean

# Clean and rebuild
./gradlew clean build
```

## Project Structure

```
src/
├── main/
│   ├── kotlin/
│   │   └── com/miguoliang/englishlearning/
│   │       ├── config/       # Configuration classes
│   │       ├── controller/   # REST controllers
│   │       ├── dto/          # Data transfer objects
│   │       ├── model/        # Domain entities
│   │       ├── repository/   # R2DBC repositories
│   │       └── service/      # Business logic
│   └── resources/
│       ├── db/migration/     # Flyway migrations
│       └── application.yml   # Application config
└── test/
    └── kotlin/               # Test classes
```

## Tech Stack

- **Framework**: Spring Boot 4.0
- **Language**: Kotlin 2.3 with Coroutines
- **Database**: PostgreSQL with R2DBC
- **Migrations**: Flyway
- **Template Engine**: FreeMarker
- **Code Quality**: Detekt, Ktlint
