import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { ArrowLeft, ArrowRight, Lock, Mail, UserRound } from 'lucide-react';
import api from '../services/api';
import './Login.css';

const passwordRule = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,72}$/;

function Register() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: '', email: '', password: '', confirmPassword: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const update = (field) => (event) => setForm((current) => ({ ...current, [field]: event.target.value }));

  const submit = async (event) => {
    event.preventDefault();
    setError('');
    if (Object.values(form).some((value) => !value.trim())) {
      setError('Please complete every field.');
      return;
    }
    if (!passwordRule.test(form.password)) {
      setError('Use at least 8 characters with an uppercase letter, a lowercase letter, and a number.');
      return;
    }
    if (new TextEncoder().encode(form.password).length > 72) {
      setError('Password must be no longer than 72 bytes.');
      return;
    }
    if (form.password !== form.confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    setLoading(true);
    try {
      await api.post('/api/auth/register', {
        name: form.name.trim(), email: form.email.trim(), password: form.password
      });
      navigate('/login', { replace: true, state: { message: 'Account created. Please sign in with your new credentials.' } });
    } catch (err) {
      const data = err.response?.data;
      setError(typeof data === 'string' ? data : data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="login-page">
      <header className="login-header">
        <Link to="/" className="brand">people<span>.</span></Link>
        <Link to="/login" className="back-link"><ArrowLeft size={16} /> Sign in</Link>
      </header>
      <section className="login-container">
        <div className="login-intro">
          <p className="eyebrow">A PLACE FOR YOUR PEOPLE</p>
          <h1>Make room<br /><span>for great work.</span></h1>
          <p>Create your employee account to access your personal workspace.</p>
        </div>
        <div className="login-card">
          <h2>Create account.</h2>
          <p className="card-description">Your account will be created with employee access.</p>
          <form onSubmit={submit}>
            <label htmlFor="register-name">Full name</label>
            <div className="input-wrapper"><UserRound size={18} /><input id="register-name" autoComplete="name" maxLength="120" value={form.name} onChange={update('name')} required /></div>
            <label htmlFor="register-email">Email address</label>
            <div className="input-wrapper"><Mail size={18} /><input id="register-email" type="email" autoComplete="email" maxLength="254" value={form.email} onChange={update('email')} required /></div>
            <label htmlFor="register-password">Password</label>
            <div className="input-wrapper"><Lock size={18} /><input id="register-password" type="password" autoComplete="new-password" minLength="8" maxLength="72" value={form.password} onChange={update('password')} required /></div>
            <label htmlFor="register-confirm">Confirm password</label>
            <div className="input-wrapper"><Lock size={18} /><input id="register-confirm" type="password" autoComplete="new-password" value={form.confirmPassword} onChange={update('confirmPassword')} required /></div>
            {error && <p className="login-error" role="alert">{error}</p>}
            <button type="submit" className="login-submit" disabled={loading}>{loading ? 'Creating account…' : 'Create account'}{!loading && <ArrowRight size={17} />}</button>
          </form>
          <p className="login-footer">Already registered? <Link to="/login">Sign in</Link></p>
        </div>
      </section>
    </main>
  );
}

export default Register;
