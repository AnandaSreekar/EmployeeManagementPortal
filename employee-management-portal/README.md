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

See `.env.example`. For Neon, use a JDBC URL such as `jdbc:postgresql://YOUR-POOLED-HOST.neon.tech/YOUR_DATABASE?sslmode=require`; copy the pooled hostname, database name, username, and password from Neon’s Connect panel. `DATABASE_USERNAME` and `DATABASE_PASSWORD` remain separate environment variables. `JWT_SECRET` must be Base64 encoding of at least 32 random bytes; the application validates it on startup. Production uses a one-hour JWT expiration unless `JWT_EXPIRATION` is set.

## Deploying the backend

The included Dockerfile builds the executable WAR and runs it on the platform-provided `PORT`. It activates the `prod` profile, defaults cookies to `Secure; SameSite=None`, excludes localhost from production CORS, and limits JVM heap sizing for Render’s 512 MB free instance. On Render, create a Web Service from this directory and choose Docker. Set `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `JWT_SECRET`, `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, and `FRONTEND_URL` in the service environment. `DATABASE_URL` should use Neon’s pooled JDBC hostname and `sslmode=require`. Set `FRONTEND_URL` to the exact Vercel production origin with no trailing slash. Never commit `.env` or credentials.

Render Free is suitable for a preview or low-traffic hobby deployment, not a durable production service: it can spin down after 15 minutes idle and has 512 MB RAM; plan for cold starts and intermittent restarts. Neon provides the persistent database, but use appropriate backups and service plans for production data.

`JPA_DDL_AUTO` defaults to `update` to keep a new Neon database compatible with the existing entity schema. Use a managed migration process before switching to `validate`. Health check path: `/actuator/health`.

For a new database, set `BOOTSTRAP_ADMIN_EMAIL` and a unique `BOOTSTRAP_ADMIN_PASSWORD` (12–72 UTF-8 bytes) for the first startup. The app creates an ADMIN only if that email is absent; it never elevates an existing USER or changes an existing admin password. Remove both variables after confirming the administrator can sign in. Public registration always creates USER accounts.

For cookie-based Google OAuth in browsers with strict third-party-cookie controls, use custom frontend and backend domains under the same registered domain (for example, `portal.example.com` and `api.example.com`). They remain separate origins for CORS but are same-site for cookies.

Google OAuth's authorized redirect URI must be `https://YOUR-BACKEND-DOMAIN/login/oauth2/code/google`. Update it when the backend domain changes.
