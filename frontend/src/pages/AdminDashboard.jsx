import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Users, UserPlus, Building2, Search, LogOut, Pencil, Trash2, X } from 'lucide-react';
import api, { ensureCsrfToken } from '../services/api';
import './Dashboard.css';

const emptyForm = { employeeCode: '', department: '', designation: '', phone: '', joiningDate: '', userId: '' };

function AdminDashboard() {
  const [employees, setEmployees] = useState([]);
  const [users, setUsers] = useState([]);
  const [adminName, setAdminName] = useState('Administrator');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [query, setQuery] = useState('');
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [saving, setSaving] = useState(false);
  const navigate = useNavigate();

  const loadDirectory = useCallback(async () => {
    try {
      const [employeeResponse, userResponse] = await Promise.all([
        api.get('/api/admin/employees'), api.get('/api/admin/users')
      ]);
      setError('');
      setEmployees(employeeResponse.data);
      setUsers(userResponse.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to load the employee directory.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const timer = window.setTimeout(() => { void loadDirectory(); }, 0);
    return () => window.clearTimeout(timer);
  }, [loadDirectory]);

  useEffect(() => {
    let active = true;
    api.get('/api/test/profile').then(({ data }) => {
      if (active) setAdminName(data.name || 'Administrator');
    }).catch(() => {});
    return () => { active = false; };
  }, []);

  const departments = useMemo(() => new Set(employees.map((employee) => employee.department).filter(Boolean)).size, [employees]);
  const filteredEmployees = useMemo(() => {
    const term = query.trim().toLowerCase();
    if (!term) return employees;
    return employees.filter((employee) => [employee.name, employee.email, employee.employeeCode, employee.department, employee.designation]
      .some((value) => value?.toLowerCase().includes(term)));
  }, [employees, query]);

  const resetForm = () => { setForm(emptyForm); setEditingId(null); };
  const editEmployee = (employee) => {
    const linkedUser = users.find((user) => user.email === employee.email);
    setForm({ employeeCode: employee.employeeCode || '', department: employee.department || '', designation: employee.designation || '', phone: employee.phone || '', joiningDate: employee.joiningDate || '', userId: linkedUser?.id || '' });
    setEditingId(employee.id);
    document.getElementById('employee-form')?.scrollIntoView({ behavior: 'smooth', block: 'center' });
  };

  const saveEmployee = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError('');
    const payload = { ...form, userId: form.userId ? Number(form.userId) : null };
    try {
      await ensureCsrfToken();
      if (editingId) await api.put(`/api/admin/employees/${editingId}`, payload);
      else await api.post('/api/admin/employees', payload);
      resetForm();
      await loadDirectory();
    } catch (err) {
      setError(err.response?.data?.message || 'Could not save this employee. Check for duplicate employee codes or account links.');
    } finally {
      setSaving(false);
    }
  };

  const deleteEmployee = async (employee) => {
    if (!window.confirm(`Delete the employee record for ${employee.name || employee.employeeCode}?`)) return;
    try {
      await ensureCsrfToken();
      await api.delete(`/api/admin/employees/${employee.id}`);
      setEmployees((current) => current.filter((item) => item.id !== employee.id));
    } catch (err) {
      setError(err.response?.data?.message || 'Could not delete this employee.');
    }
  };

  const logout = async () => {
    try { await api.post('/api/auth/logout'); } catch { /* Clear the local session even if the server is unavailable. */ }
    sessionStorage.clear();
    navigate('/login', { replace: true });
  };

  const profiledCount = employees.filter((employee) => employee.department && employee.designation).length;

  return (
    <main className="dashboard-page">
      <header className="dashboard-header">
        <h2 className="brand">people<span>.</span></h2>
        <div className="header-actions"><span className="admin-label">ADMIN</span><button className="icon-button" onClick={logout} aria-label="Sign out"><LogOut size={18} /></button></div>
      </header>

      <section className="dashboard-content">
        <p className="eyebrow">PEOPLE & CULTURE / ADMINISTRATION</p>
        <h1 className="dashboard-title">Good morning,<br /><span>{adminName}.</span></h1>
        <p className="dashboard-subtitle">Manage your people and keep employee records organised.</p>

        <div className="stats-grid">
          <div className="stat-card"><Users size={22} /><p>TOTAL EMPLOYEES</p><h2>{loading ? '…' : employees.length}</h2><span>Employee records</span></div>
          <div className="stat-card"><Building2 size={22} /><p>DEPARTMENTS</p><h2>{loading ? '…' : departments}</h2><span>Across the organisation</span></div>
          <div className="stat-card orange-stat"><UserPlus size={22} /><p>PROFILED</p><h2>{loading ? '…' : profiledCount}</h2><span>With department and designation</span></div>
        </div>

        {error && <p className="dashboard-error" role="alert">{error}</p>}

        <section className="management-section" id="employee-form">
          <div><p className="eyebrow">EMPLOYEE RECORDS</p><h2>{editingId ? 'Edit employee' : 'Add an employee'}</h2><p>Create and maintain employee directory records.</p></div>
          {editingId && <button className="secondary-button" onClick={resetForm}><X size={16} /> Cancel edit</button>}
        </section>

        <form className="employee-form" onSubmit={saveEmployee}>
          <label>Employee code<input required maxLength="50" value={form.employeeCode} onChange={(e) => setForm({ ...form, employeeCode: e.target.value })} placeholder="EMP-001" /></label>
          <label>Account<select value={form.userId} onChange={(e) => setForm({ ...form, userId: e.target.value })}><option value="">No linked account</option>{users.map((user) => <option key={user.id} value={user.id}>{user.name} · {user.email}</option>)}</select></label>
          <label>Department<input value={form.department} onChange={(e) => setForm({ ...form, department: e.target.value })} placeholder="People & Culture" /></label>
          <label>Designation<input value={form.designation} onChange={(e) => setForm({ ...form, designation: e.target.value })} placeholder="People partner" /></label>
          <label>Phone<input type="tel" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} placeholder="+1 555 0100" /></label>
          <label>Joining date<input type="date" value={form.joiningDate} onChange={(e) => setForm({ ...form, joiningDate: e.target.value })} /></label>
          <button className="management-button" type="submit" disabled={saving}>{saving ? 'Saving…' : editingId ? 'Save changes' : 'Add employee'}<UserPlus size={17} /></button>
        </form>

        <div className="directory-toolbar"><div><p className="eyebrow">YOUR WORKSPACE</p><h2>Employee directory</h2></div><label className="search-box"><Search size={18} /><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search people, codes, teams" /></label></div>

        {loading ? <div className="directory-placeholder">Loading employee records…</div> : filteredEmployees.length === 0 ? <div className="directory-placeholder">{query ? 'No employees match your search.' : 'No employee records yet. Add the first employee above.'}</div> : (
          <div className="employee-table-wrap"><table className="employee-table"><thead><tr><th>Employee</th><th>Code</th><th>Department</th><th>Designation</th><th>Phone</th><th>Joined</th><th>Actions</th></tr></thead><tbody>
            {filteredEmployees.map((employee) => <tr key={employee.id}><td><strong>{employee.name || 'Unlinked account'}</strong><small>{employee.email || 'No account linked'}</small></td><td>{employee.employeeCode}</td><td>{employee.department || '—'}</td><td>{employee.designation || '—'}</td><td>{employee.phone || '—'}</td><td>{employee.joiningDate || '—'}</td><td><div className="row-actions"><button className="icon-button" onClick={() => editEmployee(employee)} aria-label={`Edit ${employee.name || employee.employeeCode}`}><Pencil size={16} /></button><button className="icon-button danger-button" onClick={() => deleteEmployee(employee)} aria-label={`Delete ${employee.name || employee.employeeCode}`}><Trash2 size={16} /></button></div></td></tr>)}
          </tbody></table></div>
        )}
      </section>
    </main>
  );
}

export default AdminDashboard;
