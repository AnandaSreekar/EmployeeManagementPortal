
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { CheckCircle, LoaderCircle } from 'lucide-react';
import api from '../services/api';

function OAuthSuccess() {
  const navigate = useNavigate();
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    api.get('/api/test/profile').then(({ data }) => {
      if (!active) return;
      navigate(data.role === 'ADMIN' ? '/admin' : '/user', { replace: true });
    }).catch(() => {
      if (active) setError('Google sign-in could not be verified. Please sign in again.');
    });
    return () => { active = false; };
  }, [navigate]);

  return (
    <main className="oauth-success">
      <div className="oauth-success-card">
        {error ? <CheckCircle size={48} /> : <LoaderCircle size={42} className="spin" />}
        <h1>{error ? 'Sign-in needs attention' : 'Verifying your sign-in'}</h1>
        <p>{error || 'Connecting your secure employee session...'}</p>
      </div>
    </main>
  );
}

export default OAuthSuccess;
