import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Droplet, HeartHandshake, LogIn, LogOut, Menu, User, UserPlus, X } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { NotificationBell } from '@/features/notifications/NotificationBell';

export const Navbar: React.FC = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const getDashboardPath = () => {
    if (!user) return '/';
    if (user.role === 'ADMIN') return '/admin';
    if (user.role === 'DONOR') return '/donor';
    return '/recipient';
  };

  return (
    <header
      style={{
        backgroundColor: 'var(--bg-surface)',
        borderBottom: '1px solid var(--border-light)',
        position: 'sticky',
        top: 0,
        zIndex: 40,
      }}
    >
      <div
        className="container"
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          height: '64px',
        }}
      >
        {/* Brand Logo */}
        <Link
          to="/"
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.625rem',
            color: 'var(--text-primary)',
            fontWeight: 700,
            fontSize: '1.125rem',
            letterSpacing: '-0.02em',
          }}
        >
          <div
            style={{
              width: '32px',
              height: '32px',
              borderRadius: '8px',
              backgroundColor: 'var(--primary)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#fff',
            }}
          >
            <Droplet size={20} fill="#fff" />
          </div>
          <span>Blood Matcher</span>
        </Link>

        {/* Desktop Navigation Links */}
        <nav
          style={{
            display: 'none',
            alignItems: 'center',
            gap: '1.5rem',
            fontSize: '0.875rem',
            fontWeight: 500,
          }}
          className="desktop-nav"
        >
          <Link to="/" style={{ color: 'var(--text-secondary)' }}>
            Home
          </Link>
          <Link to="/how-it-works" style={{ color: 'var(--text-secondary)' }}>
            How It Works
          </Link>
          <Link to="/compatibility" style={{ color: 'var(--text-secondary)' }}>
            Compatibility
          </Link>
          <Link to="/disclaimers" style={{ color: 'var(--text-secondary)' }}>
            Safety & Disclaimers
          </Link>
        </nav>

        {/* User / Auth Actions */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.875rem' }}>
          {isAuthenticated && user ? (
            <>
              <NotificationBell />
              <Link to={getDashboardPath()} className="btn btn-primary btn-sm">
                <HeartHandshake size={15} />
                <span>Dashboard ({user.role})</span>
              </Link>
              <button
                type="button"
                onClick={handleLogout}
                className="btn btn-secondary btn-sm"
                title="Log out"
              >
                <LogOut size={15} />
                <span className="hide-mobile">Logout</span>
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="btn btn-outline btn-sm">
                <LogIn size={15} />
                <span>Login</span>
              </Link>
              <Link to="/register" className="btn btn-primary btn-sm">
                <UserPlus size={15} />
                <span>Register</span>
              </Link>
            </>
          )}

          {/* Mobile hamburger button */}
          <button
            type="button"
            className="btn btn-secondary btn-sm mobile-toggle"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            aria-label="Toggle navigation menu"
            style={{ padding: '0.4rem' }}
          >
            {mobileMenuOpen ? <X size={20} /> : <Menu size={20} />}
          </button>
        </div>
      </div>

      {/* Mobile Drawer */}
      {mobileMenuOpen && (
        <div
          style={{
            backgroundColor: 'var(--bg-surface)',
            borderTop: '1px solid var(--border-light)',
            padding: '1rem 1.5rem',
            display: 'flex',
            flexDirection: 'column',
            gap: '0.75rem',
            fontSize: '0.925rem',
            fontWeight: 500,
          }}
        >
          <Link to="/" onClick={() => setMobileMenuOpen(false)}>
            Home
          </Link>
          <Link to="/how-it-works" onClick={() => setMobileMenuOpen(false)}>
            How It Works
          </Link>
          <Link to="/compatibility" onClick={() => setMobileMenuOpen(false)}>
            Blood Compatibility
          </Link>
          <Link to="/disclaimers" onClick={() => setMobileMenuOpen(false)}>
            Safety & Disclaimers
          </Link>
          {isAuthenticated && (
            <Link to={getDashboardPath()} onClick={() => setMobileMenuOpen(false)}>
              My Dashboard ({user?.role})
            </Link>
          )}
        </div>
      )}

      <style>{`
        @media (min-width: 768px) {
          .desktop-nav {
            display: flex !important;
          }
          .mobile-toggle {
            display: none !important;
          }
        }
        @media (max-width: 480px) {
          .hide-mobile {
            display: none;
          }
        }
      `}</style>
    </header>
  );
};
