# Task Management API

API REST para autenticação de usuários, desenvolvida com Java e Spring Boot.

O projeto foi desenvolvido como parte do meu portfólio, com foco em boas práticas de desenvolvimento backend, testes, documentação, containerização e integração contínua.

## Tecnologias

## Stack

- Backend
  - **Java 21**
  - **Spring Boot 4.1.1**
  - **Spring Web MVC**
  - **Spring Data JPA / Hibernate**
  - **Bean Validation**

- Database
  - **PostgreSQL 17**
  - **Flyway**

- Security
  - **Spring Security**
  - **JWT (JJWT)**
  - **BCrypt**

- Testing
  - **JUnit 5**
  - **Mockito**
  - **AssertJ**
  - **Testcontainers**

- Documentation
  - **OpenAPI**
  - **Swagger UI**

- Code Quality & Security
  - **Lombok**
  - **JaCoCo**
  - **SpotBugs**
  - **OWASP Dependency-Check**

- Infrastructure & CI
  - **Docker**
  - **Docker Compose**
  - **GitHub Actions**
  - **Gradle**

## Funcionalidades

- Criar Usuário
- Fazer Login
- Acessar endpoints públicos e privados

### Roles

- `USER`
- `ADMIN`

## Endpoints

| Método | Endpoint               | Descrição                           |
| ------ | ---------------------- | ----------------------------------- |
| POST   | `/api/v1/auth/register | Cria um usuário                     |
| GET    | `/api/v1/auth/login`   | Autentica um usuário                |
| GET    | `/api/v1/users/me`     | Retorna o usuário autenticado       |
| PUT    | `/api/v1/admin/test`   | Endpoint acessível apenas por ADMIN |

## Executando localmente

### Pré-requisitos

- Java 21
- Docker

Clone o repositório:

```bash
git clone https://github.com/LuketeDev/authentication-api.git
cd authentication-api
```

Suba a aplicação e o PostgreSQL:

```bash
docker compose up -d --build
```

A API estará disponível em:

```text
http://localhost:8080
```

## Documentação

A documentação interativa da API está disponível através do Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## Testes

Para executar os testes:

```bash
./gradlew test
```

Para gerar o relatório de cobertura:

```bash
./gradlew jacocoTestReport
```

## Qualidade e segurança

O projeto possui verificações automatizadas de qualidade e segurança através de:

- JaCoCo
- SpotBugs
- OWASP Dependency-Check
- GitHub Actions

O pipeline de CI executa os testes, análise de cobertura, análise estática e build da aplicação.
