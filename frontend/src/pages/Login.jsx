
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  ArrowRight,
  Mail,
  Lock,
  ShieldCheck
} from 'lucide-react';

import api from '../services/api';
import './Login.css';

function Login() {
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await api.post('/api/auth/login', {
        email,
        password
      });

      const { token, role } = response.data;

      sessionStorage.setItem('token', token);
      sessionStorage.setItem('role', role);
      sessionStorage.removeItem('oauth-session');

      if (role === 'ADMIN') {
        navigate('/admin');
      } else {
        navigate('/user');
      }

    } catch (err) {
      setError(
        err.response?.data?.message ||
        'Login failed. Please check your credentials.'
      );
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleLogin = () => {
    window.location.href = `${api.defaults.baseURL}/oauth2/authorization/google`;
  };

  return (
    <main className="login-page">

      <header className="login-header">
        <a href="/" className="brand">
          people<span>.</span>
        </a>

        <a href="/" className="back-link">
          <ArrowLeft size={16} /> Back to home
        </a>
      </header>

      <section className="login-container">

        <div className="login-intro">
          <p className="eyebrow">WELCOME TO YOUR WORKSPACE</p>

          <h1>
            Good people.<br />
            <span>Great work.</span>
          </h1>

          <p>
            Sign in to access your employee workspace
            and stay connected with your team.
          </p>

          <div className="login-note">
            <ShieldCheck size={18} />
            Secure employee access
          </div>
        </div>

        <div className="login-card">

          <h2>Welcome back.</h2>
          <p className="card-description">
            Enter your details to continue.
          </p>

          <form onSubmit={handleLogin}>
            <label>Email address</label>

            <div className="input-wrapper">
              <Mail size={18} />
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="name@company.com"
                required
              />
            </div>

            <label>Password</label>

            <div className="input-wrapper">
              <Lock size={18} />
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter your password"
                required
              />
            </div>

            {error && <p className="login-error">{error}</p>}

            <button
              type="submit"
              className="login-submit"
              disabled={loading}
            >
              {loading ? 'Signing in...' : 'Sign in'}
              {!loading && <ArrowRight size={17} />}
            </button>
          </form>

          <div className="login-divider">
            <span>OR CONTINUE WITH</span>
          </div>

          <button
            type="button"
            className="google-button"
            onClick={handleGoogleLogin}
          >
            <span className="google-mark">G</span>
            Continue with Google
          </button>

          <p className="login-footer">
            New to the portal? Contact your administrator.
          </p>

        </div>
      </section>
    </main>
  );
}

export default Login;
