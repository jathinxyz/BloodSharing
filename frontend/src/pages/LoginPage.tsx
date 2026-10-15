import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { AlertCircle, Droplet, KeyRound, Lock, LogIn, Mail } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';

export const LoginPage: React.FC = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const from = (location.state as any)?.from?.pathname || '/';

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !password) {
      setError('Please provide both email and password');
      return;
    }

    setError(null);
    setLoading(true);

    try {
      await login({ email: email.trim(), password });
      navigate(from === '/' ? '/donor' : from, { replace: true });
    } catch (err: any) {
      setError(err?.message || 'Login failed. Please verify your credentials.');
    } finally {
      setLoading(false);
    }
  };

  const fillQuickCreds = (e: string, p: string) => {
    setEmail(e);
    setPassword(p);
  };

  return (
    <div className="container" style={{ padding: '3.5rem 1.5rem', display: 'flex', justifyContent: 'center' }}>
      <div style={{ width: '100%', maxWidth: '440px' }}>
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <div
            style={{
              width: '44px',
              height: '44px',
              borderRadius: '10px',
              backgroundColor: 'var(--primary)',
              color: '#fff',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              margin: '0 auto 0.75rem',
            }}
          >
            <Droplet size={24} fill="#fff" />
          </div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 700 }}>Sign In to Blood Matcher</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Access your donor, recipient, or administrative portal
          </p>
        </div>

        <div className="card">
          <div className="card-body">
            {error && (
              <div className="alert alert-danger" style={{ marginBottom: '1.25rem' }}>
                <AlertCircle size={18} style={{ flexShrink: 0 }} />
                <span>{error}</span>
              </div>
            )}

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label className="label" htmlFor="email">
                  Email Address
                </label>
                <div style={{ position: 'relative' }}>
                  <input
                    id="email"
                    type="email"
                    className="input"
                    placeholder="name@example.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    style={{ paddingLeft: '2.25rem' }}
                  />
                  <Mail
                    size={16}
                    color="var(--text-muted)"
                    style={{ position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)' }}
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="label" htmlFor="password">
                  Password
                </label>
                <div style={{ position: 'relative' }}>
                  <input
                    id="password"
                    type="password"
                    className="input"
                    placeholder="Enter your password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    style={{ paddingLeft: '2.25rem' }}
                  />
                  <Lock
                    size={16}
                    color="var(--text-muted)"
                    style={{ position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)' }}
                  />
                </div>
              </div>

              <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
                <LogIn size={16} />
                <span>{loading ? 'Authenticating...' : 'Sign In'}</span>
              </button>
            </form>

            {/* Quick Demo Credentials for Reviewers */}
            <div
              style={{
                marginTop: '1.5rem',
                paddingTop: '1.25rem',
                borderTop: '1px solid var(--border-light)',
                fontSize: '0.75rem',
                color: 'var(--text-muted)',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', marginBottom: '0.5rem', fontWeight: 600 }}>
                <KeyRound size={13} /> Demo Administrator Access:
              </div>
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                style={{ fontSize: '0.75rem', width: '100%', justifyContent: 'flex-start' }}
                onClick={() => fillQuickCreds('admin@bloodmatcher.org', 'Admin@BloodMatcher2024!')}
              >
                Use Admin: admin@bloodmatcher.org
              </button>
            </div>
          </div>

          <div
            className="card-footer"
            style={{ justifyContent: 'center', fontSize: '0.8125rem', color: 'var(--text-secondary)' }}
          >
            Don't have an account?{' '}
            <Link to="/register" style={{ fontWeight: 600, marginLeft: '0.25rem' }}>
              Create an Account
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};
