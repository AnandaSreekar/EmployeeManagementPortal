# Employee Management Portal — Frontend

React, Vite, React Router, and Axios frontend for the Employee Management Portal.

## Local setup

1. Copy `.env.example` to `.env.local`. Set `VITE_API_BASE_URL=http://localhost:8080`.
2. From this directory run `npm install` if dependencies are not installed, then `npm run dev`.
3. Open the Vite URL shown in the terminal (normally `http://localhost:5173`). The backend must allow that origin and run on port 8080.

The API client in `src/services/api.js` is the only place that configures the backend URL, credentials, CSRF handling, and bearer token header. Do not put secrets in `VITE_*` variables; Vite embeds them into the public browser bundle.

## Deploying the frontend

Deploy this directory as a Vercel project. Set `VITE_API_BASE_URL` to `https://YOUR-BACKEND-DOMAIN` before building. `vercel.json` rewrites SPA routes to `index.html`. Set the backend's `FRONTEND_URL` to the exact frontend origin and configure production cookies (`COOKIE_SECURE=true`, `COOKIE_SAME_SITE=None`). Add the backend OAuth callback URL to the Google OAuth client. Redeploy after changing build environment variables.

Production hosting must serve the frontend over HTTPS. Since the app uses credentialed cross-origin requests, the backend must allow the exact frontend origin and cookies; wildcard CORS origins are not supported. For reliable Google OAuth cookies under strict browser privacy settings, use frontend and API custom domains under the same registered domain (for example, `portal.example.com` and `api.example.com`).
