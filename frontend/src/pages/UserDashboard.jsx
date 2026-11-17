
import { useEffect, useState } from 'react';
import {
  UserRound,
  BriefcaseBusiness,
  CalendarDays,
  Building2,
  ArrowUpRight,
  Phone,
  LogOut
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';

import api from '../services/api';
import './Dashboard.css';

function UserDashboard() {
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const logout = async () => {
    try { await api.post('/api/auth/logout'); } catch { /* Clear the local session even if the server is unavailable. */ }
    sessionStorage.clear();
    navigate('/login', { replace: true });
  };

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await api.get('/api/user/my-profile');
        setProfile(response.data);
      } catch (err) {
        setError(
          typeof err.response?.data === 'string'
            ? err.response.data
            : err.response?.data?.message || 'Unable to load employee details.'
        );
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []);

  if (loading) {
    return <main className="dashboard-page">Loading your profile...</main>;
  }

  if (error) {
    return <main className="dashboard-page">{error}</main>;
  }

  return (
    <main className="dashboard-page">

      <header className="dashboard-header">
        <h2 className="brand">people<span>.</span></h2>
        <div className="header-actions"><span className="admin-label">EMPLOYEE PORTAL</span><button className="icon-button" onClick={logout} aria-label="Sign out"><LogOut size={18} /></button></div>
      </header>

      <section className="dashboard-content">

        <p className="eyebrow">MY WORKSPACE / OVERVIEW</p>

        <h1 className="dashboard-title">
          Welcome back,<br />
          <span>{profile.name}.</span>
        </h1>

        <p className="dashboard-subtitle">
          Here's a little overview of your professional profile.
        </p>

        <div className="profile-card">

          <div className="profile-avatar">
            <UserRound size={38} />
          </div>

          <div className="profile-info">
            <p className="eyebrow">YOUR PROFILE</p>
            <h2>{profile.name}</h2>
            <span>{profile.email}</span>
            <p>{profile.employeeCode}</p>
          </div>

          <div className="profile-status">
            <span className="status-dot"></span>
            EMPLOYEE
          </div>

        </div>

        <div className="management-section">
          <div>
            <p className="eyebrow">EMPLOYMENT DETAILS</p>
            <h2>Your professional information</h2>
          </div>
        </div>

        <div className="employee-details-grid">

          <div className="stat-card">
            <Building2 size={22} />
            <p>DEPARTMENT</p>
            <h3>{profile.department || '--'}</h3>
          </div>

          <div className="stat-card">
            <BriefcaseBusiness size={22} />
            <p>DESIGNATION</p>
            <h3>{profile.designation || '--'}</h3>
          </div>

          <div className="stat-card">
            <CalendarDays size={22} />
            <p>JOINING DATE</p>
            <h3>{profile.joiningDate || '--'}</h3>
          </div>

          <div className="stat-card">
            <Phone size={22} />
            <p>PHONE NUMBER</p>
            <h3>{profile.phone || '--'}</h3>
          </div>

        </div>

        <div className="directory-placeholder">
          <h3>Your workspace, all in one place.</h3>
          <p>Your employee information is connected to your account.</p>
          <ArrowUpRight size={22} />
        </div>

      </section>
    </main>
  );
}

export default UserDashboard;
