import React from 'react';
import { Link } from 'react-router-dom';
import { Droplet, Heart, Shield } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer
      style={{
        backgroundColor: '#0f172a',
        color: '#94a3b8',
        padding: '3rem 0 1.5rem',
        marginTop: 'auto',
        fontSize: '0.875rem',
      }}
    >
      <div className="container">
        <div className="grid-4" style={{ marginBottom: '2.5rem' }}>
          {/* Brand info */}
          <div>
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.5rem',
                color: '#fff',
                fontWeight: 700,
                fontSize: '1.1rem',
                marginBottom: '0.75rem',
              }}
            >
              <Droplet size={18} fill="#ef4444" color="#ef4444" />
              <span>Blood Matcher</span>
            </div>
            <p style={{ lineHeight: 1.5, fontSize: '0.8125rem' }}>
              Smart Blood Donor and Recipient Matching System providing transparent, preliminary RBC compatibility screening and proximity coordination.
            </p>
          </div>

          {/* Quick links */}
          <div>
            <h4 style={{ color: '#fff', fontSize: '0.875rem', fontWeight: 600, marginBottom: '0.75rem' }}>
              Navigation
            </h4>
            <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <li><Link to="/" style={{ color: '#94a3b8' }}>Home</Link></li>
              <li><Link to="/how-it-works" style={{ color: '#94a3b8' }}>How It Works</Link></li>
              <li><Link to="/compatibility" style={{ color: '#94a3b8' }}>Compatibility Matrix</Link></li>
              <li><Link to="/disclaimers" style={{ color: '#94a3b8' }}>Medical Disclaimer</Link></li>
            </ul>
          </div>

          {/* Donor & Recipient Access */}
          <div>
            <h4 style={{ color: '#fff', fontSize: '0.875rem', fontWeight: 600, marginBottom: '0.75rem' }}>
              Portals
            </h4>
            <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <li><Link to="/login" style={{ color: '#94a3b8' }}>Sign In</Link></li>
              <li><Link to="/register" style={{ color: '#94a3b8' }}>Donor / Recipient Registration</Link></li>
              <li><Link to="/donor" style={{ color: '#94a3b8' }}>Donor Workspace</Link></li>
              <li><Link to="/recipient" style={{ color: '#94a3b8' }}>Request Management</Link></li>
            </ul>
          </div>

          {/* Clinical notice */}
          <div>
            <h4 style={{ color: '#fff', fontSize: '0.875rem', fontWeight: 600, marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
              <Shield size={16} color="#ef4444" /> Clinical Notice
            </h4>
            <p style={{ fontSize: '0.775rem', lineHeight: 1.45, color: '#cbd5e1' }}>
              Blood Matcher does not perform transfusions or provide medical certification. Always verify recipient compatibility through authorized hospital blood banks.
            </p>
          </div>
        </div>

        <div
          style={{
            borderTop: '1px solid #1e293b',
            paddingTop: '1.5rem',
            display: 'flex',
            flexWrap: 'wrap',
            alignItems: 'center',
            justifyContent: 'space-between',
            gap: '1rem',
            fontSize: '0.75rem',
          }}
        >
          <div>
            &copy; {new Date().getFullYear()} Blood Matcher Platform. Built for healthcare technology coordination.
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
            Powered by Spring Boot 3 & React <Heart size={13} fill="#ef4444" color="#ef4444" />
          </div>
        </div>
      </div>
    </footer>
  );
};
