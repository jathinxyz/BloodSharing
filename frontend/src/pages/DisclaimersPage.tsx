import React from 'react';
import { AlertOctagon, FileText, PhoneCall, ShieldCheck, Stethoscope } from 'lucide-react';

export const DisclaimersPage: React.FC = () => {
  return (
    <div className="container" style={{ padding: '3.5rem 1.5rem' }}>
      <div style={{ maxWidth: '800px', margin: '0 auto' }}>
        <div style={{ textAlign: 'center', marginBottom: '3rem' }}>
          <div
            style={{
              width: '48px',
              height: '48px',
              borderRadius: '50%',
              backgroundColor: 'var(--danger-bg)',
              color: 'var(--danger)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              margin: '0 auto 1rem',
            }}
          >
            <AlertOctagon size={28} />
          </div>
          <h1 style={{ fontSize: '2.25rem', fontWeight: 800, marginBottom: '0.75rem' }}>
            Medical & Safety Disclaimers
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '1.05rem', lineHeight: 1.6 }}>
            Please read these medical guidelines and terms of use carefully before using Blood Matcher.
          </p>
        </div>

        {/* 1. Preliminary Screening Notice */}
        <div className="card" style={{ marginBottom: '2rem', borderLeft: '4px solid var(--primary)' }}>
          <div className="card-body">
            <h2 style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--primary)', marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Stethoscope size={22} />
              1. Preliminary Software Screening, Not Medical Clearance
            </h2>
            <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', lineHeight: 1.6, marginBottom: '0.75rem' }}>
              Blood Matcher is exclusively a communications coordination platform and computational preliminary screening tool based on standard red-blood-cell ABO/Rh serological tables and geographical distance.
            </p>
            <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', lineHeight: 1.6 }}>
              <strong>The system does NOT provide medical diagnosis, clinical clearance, or certification of donor health or transfusion safety.</strong> Transfusion reactions can arise from minor red cell antigens (Kell, Duffy, Kidd, MNS), irregular antibodies, or underlying infectious agents that cannot be determined by algorithmic matching.
            </p>
          </div>
        </div>

        {/* 2. Mandatory Laboratory Procedures */}
        <div className="card" style={{ marginBottom: '2rem' }}>
          <div className="card-body">
            <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <ShieldCheck size={22} color="var(--success)" />
              2. Mandatory Blood Bank Verification
            </h2>
            <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', lineHeight: 1.6, marginBottom: '0.75rem' }}>
              Prior to any blood withdrawal or patient transfusion, the certified medical facility, hospital, or authorized blood bank must conduct:
            </p>
            <ul style={{ paddingLeft: '1.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)', lineHeight: 1.6 }}>
              <li>Complete donor health questionnaire and confidential vital sign screening.</li>
              <li>Official forward and reverse ABO and Rh blood grouping.</li>
              <li>Atypical antibody screening (indirect antiglobulin test).</li>
              <li>Serological cross-matching between donor red cells and recipient serum.</li>
              <li>Mandatory viral and infectious disease pathogen screening (HIV, Hepatitis B/C, Syphilis, etc.).</li>
            </ul>
          </div>
        </div>

        {/* 3. Emergency Situations */}
        <div className="card" style={{ marginBottom: '2rem', borderLeft: '4px solid #b45309', backgroundColor: '#fffbeb' }}>
          <div className="card-body">
            <h2 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#92400e', marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <PhoneCall size={22} />
              3. Immediate Life-Threatening Emergencies
            </h2>
            <p style={{ fontSize: '0.9rem', color: '#78350f', lineHeight: 1.6, marginBottom: '0.75rem' }}>
              Blood Matcher is not an automated emergency dispatch service. Do NOT wait for website responses or email notifications during an acute surgical trauma or hemorrhage.
            </p>
            <p style={{ fontSize: '0.9rem', color: '#78350f', lineHeight: 1.6 }}>
              Immediately contact emergency response services (e.g. 911 / local emergency number), regional blood banks, or trauma centers directly for immediate supply coordination.
            </p>
          </div>
        </div>

        {/* 4. Voluntary Participation & Privacy */}
        <div className="card">
          <div className="card-body">
            <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <FileText size={22} color="var(--info)" />
              4. Voluntary Participation & Privacy
            </h2>
            <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', lineHeight: 1.6 }}>
              All donor participations are strictly voluntary and unpaid. Generating an invitation does not obligate a donor to accept, nor does an accepted invitation guarantee physical appearance or medical qualification. Contact information is securely restricted and only disclosed after mutual match confirmation.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
