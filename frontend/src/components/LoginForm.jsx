import { useState } from 'react';

export default function LoginForm({ onLogin }) {
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('admin123');
  const [error, setError] = useState('');

  const submit = async (e) => {
    e.preventDefault();
    try {
      setError('');
      await onLogin(username, password);
    } catch {
      setError('Login failed');
    }
  };

  return (
    <form className="card" onSubmit={submit}>
      <h2>Login</h2>
      <p>Default credentials: admin / admin123</p>
      <input value={username} onChange={(e) => setUsername(e.target.value)} placeholder="Username" required />
      <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Password" required />
      <button type="submit">Login</button>
      {error && <p className="error">{error}</p>}
    </form>
  );
}
