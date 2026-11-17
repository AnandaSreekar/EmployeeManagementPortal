import AdminDashboard from './pages/AdminDashboard';
import UserDashboard from './pages/UserDashboard';
import { BrowserRouter, Routes, Route, Link, Navigate, useLocation } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { Users, ArrowUpRight, ShieldCheck } from 'lucide-react';
import Login from './pages/Login';
import Register from './pages/Register';
import OAuthSuccess from './pages/OAuthSuccess';
import api from './services/api';
import './App.css';

function ProtectedRoute({ role, children }) {
  const location = useLocation();
  const [currentRole, setCurrentRole] = useState(null);
  const [checking, setChecking] = useState(true);

  useEffect(() => {
    let active = true;
    api.get('/api/test/profile').then(({ data }) => {
      if (active) setCurrentRole(data.role);
    }).catch(() => {
      if (active) setCurrentRole('');
    }).finally(() => {
      if (active) setChecking(false);
    });
    return () => { active = false; };
  }, []);

  if (checking) return <main className="oauth-success">Checking your session…</main>;
  if (!currentRole) return <Navigate to="/login" replace state={{ from: location }} />;
  if (role === 'ADMIN' && currentRole !== 'ADMIN') return <Navigate to="/user" replace />;
  if (role === 'USER' && currentRole !== 'USER') {
    return currentRole === 'ADMIN'
      ? <Navigate to="/admin" replace />
      : <Navigate to="/login" replace />;
  }
  return children;
}

function Home() {
  return (
    <main className="landing-page">

      <nav className="top-nav">
        <h2 className="brand">people<span>.</span></h2>

        <div className="nav-label">
          EMPLOYEE MANAGEMENT PORTAL
        </div>

        <Link to="/login" className="login-button">
          Sign In <ArrowUpRight size={17} />
        </Link>
      </nav>

      <section className="hero-section">
        <div className="hero-content">
          <p className="eyebrow">
            <span></span> PEOPLE & CULTURE MANAGEMENT
          </p>

          <h1>
            Great teams<br />
            build <span>great things.</span>
          </h1>

          <p className="hero-description">
            A thoughtful space to manage people, discover
            possibilities and bring your workplace together.
          </p>

          <Link to="/login" className="primary-button">
            Explore Portal <ArrowUpRight size={18} />
          </Link>

          <div className="hero-footer">
            <ShieldCheck size={17} />
            <span>Secure access · Built for your team</span>
          </div>
        </div>

        <div className="hero-visual">
          <div className="visual-top">
            <span>YOUR PEOPLE. YOUR WORKSPACE.</span>
            <Users size={20} />
          </div>

          <div className="visual-center">
            <span className="visual-number">01</span>
            <h2>People<br />first.</h2>
            <p>Better connections.<br />Better work.</p>
          </div>

          <div className="visual-bottom">
            <span>EMPLOYEE MANAGEMENT</span>
            <span>2026 — PRESENT</span>
          </div>
        </div>
      </section>

      <footer className="page-footer">
        <span>© 2026 PEOPLE PORTAL</span>
        <span>MADE FOR PEOPLE, BUILT FOR PROGRESS.</span>
      </footer>

    </main>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/admin" element={<ProtectedRoute role="ADMIN"><AdminDashboard /></ProtectedRoute>} />
        <Route path="/user" element={<ProtectedRoute role="USER"><UserDashboard /></ProtectedRoute>} />
        <Route path="/oauth2/success" element={<OAuthSuccess />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
