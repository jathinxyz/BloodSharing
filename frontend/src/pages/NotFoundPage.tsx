import React from 'react';
import { Link } from 'react-router-dom';
import { AlertCircle, Home } from 'lucide-react';

export const NotFoundPage: React.FC = () => {
  return (
    <div className="container" style={{ padding: '6rem 1.5rem', textAlign: 'center' }}>
      <div style={{ maxWidth: '440px', margin: '0 auto' }}>
        <AlertCircle size={48} color="var(--primary)" style={{ margin: '0 auto 1.25rem' }} />
        <h1 style={{ fontSize: '2rem', fontWeight: 800, marginBottom: '0.75rem' }}>404 &bull; Page Not Found</h1>
        <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem', fontSize: '0.95rem' }}>
          The page or medical resource you are searching for does not exist or has been relocated.
        </p>
        <Link to="/" className="btn btn-primary btn-lg">
          <Home size={18} />
          <span>Return to Home</span>
        </Link>
      </div>
    </div>
  );
};
