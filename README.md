# Employee Management Portal

A full-stack employee management application with role-based access control.

|     Component             |Platform|                               |Link |
|------------------------|  |------|    |-------------------------------------------------------------------------|
| **Frontend Application** | Vercel |   [Open Frontend](https://employee-management-portal-three-eta.vercel.app/) |
| **Backend API**          | Render |   [Open Backend](https://employeemanagementportal-ggxe.onrender.com/) |
| **Backend Health Check** | Render |   [Check Health](https://employeemanagementportal-ggxe.onrender.com/actuator/health) |

## Tech Stack

**Frontend**
- React
- Vite
- JavaScript
- Axios

**Backend**
- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- JWT Authentication
- Google OAuth2

**Database**
- PostgreSQL

## Features

- User registration and login
- Google OAuth2 authentication
- JWT-based authentication
- ADMIN and USER roles
- Employee management (CRUD)
- Employee profile dashboard
- Admin dashboard
- Protected APIs and routes

## Project Structure

```text
EmployeeManagementPortal/
├── employee-management-portal/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/
│   ├── src/
│   ├── package.json
│   └── vercel.json
├── .gitignore
└── README.md
```

## Local Development

### Backend

Configure the backend environment variables using its `.env.example`.

Run:

```powershell
.\mvnw.cmd spring-boot:run
```

Backend URL: `http://localhost:8080`

### Frontend

Configure `frontend/.env.local` using its `.env.example`.

Set:

```env
VITE_API_BASE_URL=http://localhost:8080
```

Install dependencies and start:

```powershell
npm install
npm run dev
```

Use the local URL printed by Vite.

## Environment Variables

Configure database credentials, JWT settings, Google OAuth credentials, and frontend origins using the provided environment example files.

Never commit real credentials or `.env` files.

## Deployment

**Frontend:** [Deployment URL to be added]

**Backend:** [Deployment URL to be added]

## License

All rights reserved.
