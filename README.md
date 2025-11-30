# Git API - Spring Boot JGit REST API

A RESTful API for Git operations built with Spring Boot and JGit.

## Features

- Initialize new Git repositories
- Clone remote repositories
- List branches and commits
- Create branches
- Stage and commit files
- Checkout branches

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Building the Project

```bash
mvn clean install
```

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

## API Endpoints

### Initialize Repository
```
POST /api/repositories/init
Content-Type: application/json

{"name": "my-repo"}
```

### Clone Repository
```
POST /api/repositories/clone
Content-Type: application/json

{"url": "https://github.com/user/repo.git", "name": "my-repo"}
```

### Get Repository Info
```
GET /api/repositories/{name}
```

### List Branches
```
GET /api/repositories/{name}/branches
```

### Create Branch
```
POST /api/repositories/{name}/branches
Content-Type: application/json

{"branchName": "feature-branch"}
```

### List Commits
```
GET /api/repositories/{name}/commits?limit=10
```

### Stage Files
```
POST /api/repositories/{name}/add
Content-Type: application/json

{"pattern": "."}
```

### Commit Changes
```
POST /api/repositories/{name}/commit
Content-Type: application/json

{"message": "Initial commit", "author": "John Doe", "email": "john@example.com"}
```

### Checkout Branch
```
POST /api/repositories/{name}/checkout
Content-Type: application/json

{"branchName": "feature-branch"}
```

## Configuration

Configuration can be set in `src/main/resources/application.properties`:

- `server.port`: Server port (default: 8080)
- `git.repositories.base-path`: Base path for repositories (default: /tmp/repositories)

## Testing

```bash
mvn test
```

## Technologies

- Spring Boot 3.2.0
- JGit 6.7.0
- Java 17
