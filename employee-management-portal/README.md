# Employee Management Portal — Backend

Spring Boot 4, Java 17, PostgreSQL, Spring Security, JWT cookies, and Google OAuth2.

## Local setup

1. Install Java 17 and PostgreSQL. Create the `employee_management_db` database without dropping an existing database.
2. Copy `.env.example` to `.env`. Spring loads this local file; it is ignored by Git. Set a database password, Google OAuth client credentials, and a fresh JWT secret. Generate a secret with PowerShell:

   ```powershell
   $bytes = [byte[]]::new(32); [Security.Cryptography.RandomNumberGenerator]::Fill($bytes); [Convert]::ToBase64String($bytes)
   ```

3. Set `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` from Google Cloud. Add this authorized redirect URI to the OAuth web client: `http://localhost:8080/login/oauth2/code/google`.
4. Start the backend with `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`). It listens on port 8080. The frontend origin defaults to `http://localhost:5173`; `http://localhost:5174` is also allowed.

Database schema handling defaults to `update` to preserve existing data. Review a migration plan before changing schema strategy for production.

## Environment variables

See `.env.example`. `DATABASE_URL` must be a JDBC URL (`jdbc:postgresql://host:5432/database`). `JWT_SECRET` must be Base64 encoded and decode to at least 32 bytes. Production should set `COOKIE_SECURE=true` and `COOKIE_SAME_SITE=None` when the frontend and API use different sites over HTTPS. Set `FRONTEND_URL` to the exact deployed frontend origin, with no path.

## Deploying the backend

The included Dockerfile builds the executable WAR and runs it on the platform-provided `PORT`. On Render, create a Web Service from this directory and choose Docker. Attach a PostgreSQL database and provide its JDBC connection URL, username, and password as `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD`. Add the remaining production variables in the hosting dashboard; never commit a real `.env` file.

Set `JPA_DDL_AUTO=update` only if that is your deliberate migration strategy; use a managed migration process for production schema changes. Health check path: `/actuator/health`.

For a new database, set `BOOTSTRAP_ADMIN_EMAIL` and a unique `BOOTSTRAP_ADMIN_PASSWORD` (12–72 UTF-8 bytes) for the first startup. The app creates an ADMIN only if that email is absent; it never elevates an existing USER or changes an existing admin password. Remove both variables after confirming the administrator can sign in. Public registration always creates USER accounts.

For cookie-based Google OAuth in browsers with strict third-party-cookie controls, use custom frontend and backend domains under the same registered domain (for example, `portal.example.com` and `api.example.com`). They remain separate origins for CORS but are same-site for cookies.

Google OAuth's authorized redirect URI must be `https://YOUR-BACKEND-DOMAIN/login/oauth2/code/google`. Update it when the backend domain changes.
