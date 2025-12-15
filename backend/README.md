PredictWin Backend
==================

Simple Spring Boot backend for PredictWin with JWT auth and H2 in-memory database.

Features
- Spring Boot + Spring Security
- JWT token generation/validation
- H2 in-memory DB for development
- Endpoints:
  - POST /api/auth/register { username, password }
  - POST /api/auth/login { username, password } -> { token }
  - GET  /api/user/me (requires Authorization: Bearer <token>)
   - POST /api/auth/forgot { username | mobile } -> triggers OTP (dev: logged to console)
   - POST /api/auth/reset { username | mobile, otp, password } -> resets password using OTP

Run locally
1. Ensure you have Java 11+ and Maven installed.
2. From the `predictwin-backend` folder run:

```powershell
mvn spring-boot:run
```

3. The backend will run on `http://localhost:8080` and the H2 console is available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:predictwin`).

Linking frontend
- The frontend (Angular dev server) usually runs on `http://localhost:4200`.
- The backend CORS config already allows `http://localhost:4200`.
- To call API from frontend, add the `Authorization: Bearer <token>` header after login.

Security notes
- Change `jwt.secret` in `src/main/resources/application.properties` to a secure random string for production.
- Use a persistent DB (Postgres/MySQL) in production; H2 is for development only.

Production / Docker
- Build and run with Docker Compose (uses Postgres):

```bash
docker-compose build
docker-compose up -d
```

Set production secrets via environment variables: `JWT_SECRET`, `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.

Note: The project targets Java 8 for compatibility with older JDKs, but the Docker image uses Temurin 11 for broad compatibility. If running locally keep Java 8 or upgrade your JDK.

Example curl

Register:
```bash
curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d '{"username":"test","password":"pass"}'
```

Login:
```bash
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"username":"test","password":"pass"}'
```

Get user info (replace <token>):
```bash
curl http://localhost:8080/api/user/me -H "Authorization: Bearer <token>"
```
