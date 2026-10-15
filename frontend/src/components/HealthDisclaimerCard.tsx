import React from 'react';
import { ShieldAlert } from 'lucide-react';

export const HealthDisclaimerCard: React.FC<{ compact?: boolean }> = ({ compact }) => {
  return (
    <div className="alert alert-warning" style={{ alignItems: 'flex-start', margin: compact ? '0' : '1.5rem 0' }}>
      <ShieldAlert size={20} style={{ flexShrink: 0, marginTop: '2px', color: '#b45309' }} />
      <div>
        <strong style={{ display: 'block', marginBottom: '0.25rem', color: '#92400e' }}>
          Preliminary Software Screening Notice & Medical Disclaimer
        </strong>
        <p style={{ margin: 0, color: '#78350f', lineHeight: 1.45 }}>
          Blood Matcher coordinates preliminary donor identification based solely on red-blood-cell ABO/Rh compatibility and proximity.
          <strong> It does NOT constitute clinical clearance, donor medical qualification, or a guarantee of transfusion safety.</strong> Prior to any transfusion, authorized blood banks and certified medical professionals must perform independent serological testing, antibody screening, donor eligibility evaluations, and clinical cross-matching.
        </p>
      </div>
    </div>
  );
};
