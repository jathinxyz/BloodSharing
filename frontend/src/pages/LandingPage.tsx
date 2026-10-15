import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  Activity,
  ArrowRight,
  Clock,
  Droplet,
  Heart,
  MapPin,
  ShieldCheck,
  Users,
} from 'lucide-react';
import { HealthDisclaimerCard } from '@/components/HealthDisclaimerCard';
import { publicService, SystemInfo } from '@/services/publicService';
import { ALL_BLOOD_GROUPS } from '@/utils/bloodGroups';
import { BloodGroupBadge } from '@/components/BloodGroupBadge';

export const LandingPage: React.FC = () => {
  const [systemInfo, setSystemInfo] = useState<SystemInfo | null>(null);

  useEffect(() => {
    publicService.getSystemInfo().then(setSystemInfo).catch(() => {});
  }, []);

  return (
    <div>
      {/* Hero Section */}
      <section
        style={{
          backgroundColor: '#fff',
          borderBottom: '1px solid var(--border-light)',
          padding: '4rem 0 3.5rem',
        }}
      >
        <div className="container">
          <div style={{ maxWidth: '780px', margin: '0 auto', textAlign: 'center' }}>
            <div
              style={{
                display: 'inline-flex',
                alignItems: 'center',
                gap: '0.5rem',
                backgroundColor: 'var(--primary-light)',
                color: 'var(--primary)',
                padding: '0.35rem 0.85rem',
                borderRadius: 'var(--radius-full)',
                fontSize: '0.8125rem',
                fontWeight: 600,
                marginBottom: '1.25rem',
              }}
            >
              <Droplet size={14} fill="currentColor" />
              Smart Blood Donor & Recipient Matching
            </div>

            <h1
              style={{
                fontSize: '2.5rem',
                fontWeight: 800,
                lineHeight: 1.2,
                color: 'var(--text-primary)',
                letterSpacing: '-0.03em',
                marginBottom: '1.25rem',
              }}
            >
              Connecting Lifesaving Donors With Patients in Critical Need
            </h1>

            <p
              style={{
                fontSize: '1.125rem',
                color: 'var(--text-secondary)',
                lineHeight: 1.6,
                marginBottom: '2rem',
              }}
            >
              Blood Matcher coordinates preliminary red-blood-cell compatibility screening, prioritizes verified nearby donors, and facilitates structured hospital requests during urgent shortages.
            </p>

            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '1rem',
                flexWrap: 'wrap',
                marginBottom: '2rem',
              }}
            >
              <Link to="/register" className="btn btn-primary btn-lg">
                <Heart size={18} />
                <span>Register as Donor</span>
              </Link>
              <Link to="/register" className="btn btn-outline btn-lg">
                <span>Submit Blood Request</span>
                <ArrowRight size={18} />
              </Link>
            </div>

            {/* Mandatory Health Disclaimer Callout */}
            <HealthDisclaimerCard compact />
          </div>
        </div>
      </section>

      {/* Blood Groups Quick Overview */}
      <section style={{ padding: '3.5rem 0', backgroundColor: 'var(--bg-page)' }}>
        <div className="container">
          <div style={{ textAlign: 'center', marginBottom: '2.5rem' }}>
            <h2 style={{ fontSize: '1.75rem', fontWeight: 700, marginBottom: '0.5rem' }}>
              All 8 Human Blood Types Supported
            </h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.925rem' }}>
              Preliminary screening follows standard ABO and Rh red-blood-cell serological compatibility matrices.
            </p>
          </div>

          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(120px, 1fr))',
              gap: '1rem',
              maxWidth: '900px',
              margin: '0 auto 2rem',
            }}
          >
            {ALL_BLOOD_GROUPS.map((bg) => (
              <div
                key={bg}
                className="card"
                style={{
                  padding: '1.25rem 0.5rem',
                  textAlign: 'center',
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  gap: '0.5rem',
                }}
              >
                <BloodGroupBadge bloodGroup={bg} size="lg" />
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 500 }}>
                  {bg === 'O-' ? 'Universal Donor' : bg === 'AB+' ? 'Universal Recipient' : 'RBC Type'}
                </span>
              </div>
            ))}
          </div>

          <div style={{ textAlign: 'center' }}>
            <Link to="/compatibility" className="btn btn-secondary btn-sm">
              View Detailed Compatibility Matrix &rarr;
            </Link>
          </div>
        </div>
      </section>

      {/* Core Architectural Features */}
      <section style={{ padding: '4rem 0', backgroundColor: 'var(--bg-surface)' }}>
        <div className="container">
          <div style={{ textAlign: 'center', marginBottom: '3rem' }}>
            <h2 style={{ fontSize: '1.875rem', fontWeight: 700, marginBottom: '0.5rem' }}>
              Engineered for Healthcare Safety & Coordination
            </h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem', maxWidth: '650px', margin: '0 auto' }}>
              Built with robust security, deterministic matching algorithms, and strict privacy protection for both donors and recipients.
            </p>
          </div>

          <div className="grid-3">
            <div className="card" style={{ padding: '2rem' }}>
              <div
                style={{
                  width: '44px',
                  height: '44px',
                  borderRadius: '8px',
                  backgroundColor: 'var(--primary-light)',
                  color: 'var(--primary)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  marginBottom: '1.25rem',
                }}
              >
                <Activity size={24} />
              </div>
              <h3 style={{ fontSize: '1.125rem', fontWeight: 600, marginBottom: '0.5rem' }}>
                Deterministic RBC Matching
              </h3>
              <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', lineHeight: 1.55 }}>
                Software filtering follows the strict medical red-blood-cell compatibility rules without relying on probabilistic guesswork or black-box predictions.
              </p>
            </div>

            <div className="card" style={{ padding: '2rem' }}>
              <div
                style={{
                  width: '44px',
                  height: '44px',
                  borderRadius: '8px',
                  backgroundColor: 'var(--info-bg)',
                  color: 'var(--info)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  marginBottom: '1.25rem',
                }}
              >
                <MapPin size={24} />
              </div>
              <h3 style={{ fontSize: '1.125rem', fontWeight: 600, marginBottom: '0.5rem' }}>
                Haversine Proximity Ranking
              </h3>
              <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', lineHeight: 1.55 }}>
                When donors and hospitals provide coordinates, candidates are prioritized by geographic proximity to minimize transit time during emergencies.
              </p>
            </div>

            <div className="card" style={{ padding: '2rem' }}>
              <div
                style={{
                  width: '44px',
                  height: '44px',
                  borderRadius: '8px',
                  backgroundColor: 'var(--success-bg)',
                  color: 'var(--success)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  marginBottom: '1.25rem',
                }}
              >
                <Clock size={24} />
              </div>
              <h3 style={{ fontSize: '1.125rem', fontWeight: 600, marginBottom: '0.5rem' }}>
                56-Day Cooldown Protection
              </h3>
              <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', lineHeight: 1.55 }}>
                Donor health is protected by an automated 56-day (8 weeks) recovery filter tracking the donor's previous donation date before sending new alerts.
              </p>
            </div>

            <div className="card" style={{ padding: '2rem' }}>
              <div
                style={{
                  width: '44px',
                  height: '44px',
                  borderRadius: '8px',
                  backgroundColor: '#f3e8ff',
                  color: '#7e22ce',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  marginBottom: '1.25rem',
                }}
              >
                <ShieldCheck size={24} />
              </div>
              <h3 style={{ fontSize: '1.125rem', fontWeight: 600, marginBottom: '0.5rem' }}>
                Strict Privacy Safeguards
              </h3>
              <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', lineHeight: 1.55 }}>
                Donor phone numbers and exact coordinates are strictly masked until a donor explicitly reviews and accepts a hospital invitation.
              </p>
            </div>

            <div className="card" style={{ padding: '2rem' }}>
              <div
                style={{
                  width: '44px',
                  height: '44px',
                  borderRadius: '8px',
                  backgroundColor: 'var(--warning-bg)',
                  color: 'var(--warning)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  marginBottom: '1.25rem',
                }}
              >
                <Users size={24} />
              </div>
              <h3 style={{ fontSize: '1.125rem', fontWeight: 600, marginBottom: '0.5rem' }}>
                Multi-Channel Alerts
              </h3>
              <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', lineHeight: 1.55 }}>
                In-app notifications deliver real-time invitations, acceptances, and status updates, coupled with optional SMTP email notifications.
              </p>
            </div>

            <div className="card" style={{ padding: '2rem' }}>
              <div
                style={{
                  width: '44px',
                  height: '44px',
                  borderRadius: '8px',
                  backgroundColor: '#e0f2fe',
                  color: '#0369a1',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  marginBottom: '1.25rem',
                }}
              >
                <Droplet size={24} />
              </div>
              <h3 style={{ fontSize: '1.125rem', fontWeight: 600, marginBottom: '0.5rem' }}>
                Hospital Request Tracking
              </h3>
              <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', lineHeight: 1.55 }}>
                Full lifecycle management from Open, Donors Notified, In Progress, to explicit hospital-confirmed Fulfillment with donation records.
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};
