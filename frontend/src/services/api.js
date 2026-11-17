
import axios from 'axios';

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: apiBaseUrl.replace(/\/$/, ''),
  withCredentials: true,

  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',
  withXSRFToken: true
});

api.interceptors.request.use((config) => {
  if (['post', 'put', 'patch', 'delete'].includes(config.method?.toLowerCase())
      && !config.url?.startsWith('/api/auth/')) {
    const csrfToken = sessionStorage.getItem('csrf-token');
    if (csrfToken) {
      config.headers = config.headers || {};
      config.headers['X-XSRF-TOKEN'] = csrfToken;
    }
  }

  return config;
});

api.interceptors.response.use((response) => {
  if (response.config.url?.endsWith('/api/auth/csrf') && response.data?.token) {
    sessionStorage.setItem('csrf-token', response.data.token);
  }
  return response;
});

export const ensureCsrfToken = async () => {
  if (!sessionStorage.getItem('csrf-token')) await api.get('/api/auth/csrf');
};

export default api;
