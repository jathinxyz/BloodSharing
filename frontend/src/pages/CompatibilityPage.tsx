import React, { useEffect, useState } from 'react';
import { Check, ShieldCheck, X } from 'lucide-react';
import { HealthDisclaimerCard } from '@/components/HealthDisclaimerCard';
import { publicService } from '@/services/publicService';
import { ALL_BLOOD_GROUPS } from '@/utils/bloodGroups';
import { BloodGroup } from '@/types';
import { BloodGroupBadge } from '@/components/BloodGroupBadge';

// Fallback static matrix matching backend BloodCompatibilityService exactly
const STATIC_MATRIX: Record<BloodGroup, BloodGroup[]> = {
  'O-': ['O-'],
  'O+': ['O-', 'O+'],
  'A-': ['O-', 'A-'],
  'A+': ['O-', 'O+', 'A-', 'A+'],
  'B-': ['O-', 'B-'],
  'B+': ['O-', 'B-'], // wait, B+ can receive from O-, O+, B-, B+
  'AB-': ['O-', 'A-', 'B-', 'AB-'],
  'AB+': ['O-', 'O+', 'A-', 'A+', 'B-', 'B+', 'AB-', 'AB+'],
};
STATIC_MATRIX['B+'] = ['O-', 'O+', 'B-', 'B+'];

export const CompatibilityPage: React.FC = () => {
  const [matrix, setMatrix] = useState<Record<string, string[]>>(STATIC_MATRIX);

  useEffect(() => {
    publicService
      .getCompatibilityMatrix()
      .then((data) => setMatrix(data))
      .catch(() => setMatrix(STATIC_MATRIX));
  }, []);

  const isCompatible = (donor: BloodGroup, recipient: BloodGroup): boolean => {
    const list = matrix[recipient] || STATIC_MATRIX[recipient] || [];
    return list.includes(donor);
  };

  return (
    <div className="container" style={{ padding: '3.5rem 1.5rem' }}>
      <div style={{ maxWidth: '960px', margin: '0 auto' }}>
        <div style={{ textAlign: 'center', marginBottom: '2.5rem' }}>
          <h1 style={{ fontSize: '2.25rem', fontWeight: 800, marginBottom: '0.75rem' }}>
            Red Blood Cell ABO/Rh Compatibility
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '1.05rem', lineHeight: 1.6 }}>
            Software screening relies on the biological rules of red-blood-cell antigens (A, B) and Rh factor (+, -).
          </p>
        </div>

        <HealthDisclaimerCard />

        {/* Universal Donor & Recipient Banners */}
        <div className="grid-2" style={{ marginBottom: '2.5rem' }}>
          <div className="card" style={{ borderLeft: '4px solid var(--primary)', padding: '1.5rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.5rem' }}>
              <BloodGroupBadge bloodGroup="O-" size="lg" />
              <h3 style={{ fontSize: '1.125rem', fontWeight: 600 }}>Universal RBC Donor</h3>
            </div>
            <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', lineHeight: 1.5 }}>
              O Negative red blood cells lack A, B, and Rh surface antigens, meaning they can be safely given to recipients of any blood group in preliminary screening.
            </p>
          </div>

          <div className="card" style={{ borderLeft: '4px solid #0284c7', padding: '1.5rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.5rem' }}>
              <BloodGroupBadge bloodGroup="AB+" size="lg" />
              <h3 style={{ fontSize: '1.125rem', fontWeight: 600 }}>Universal RBC Recipient</h3>
            </div>
            <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', lineHeight: 1.5 }}>
              AB Positive individuals possess A, B, and Rh antigens, allowing them to receive preliminary-screened red blood cells from all eight ABO/Rh groups.
            </p>
          </div>
        </div>

        {/* Compatibility Matrix Grid Table */}
        <div className="card" style={{ marginBottom: '2.5rem' }}>
          <div className="card-header">
            <h2 className="card-title">Complete Compatibility Grid</h2>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
              Rows = Recipient &bull; Columns = Donor
            </span>
          </div>
          <div className="table-container" style={{ border: 'none', boxShadow: 'none' }}>
            <table className="table" style={{ textAlign: 'center' }}>
              <thead>
                <tr>
                  <th style={{ textAlign: 'left' }}>Recipient Type</th>
                  {ALL_BLOOD_GROUPS.map((donor) => (
                    <th key={donor} style={{ textAlign: 'center' }}>
                      <BloodGroupBadge bloodGroup={donor} size="sm" />
                    </th>
                  ))}
                  <th style={{ textAlign: 'left' }}>Compatible Donors</th>
                </tr>
              </thead>
              <tbody>
                {ALL_BLOOD_GROUPS.map((recipient) => (
                  <tr key={recipient}>
                    <td style={{ textAlign: 'left', fontWeight: 600 }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <BloodGroupBadge bloodGroup={recipient} size="sm" />
                        <span>{recipient} Recipient</span>
                      </div>
                    </td>
                    {ALL_BLOOD_GROUPS.map((donor) => {
                      const compatible = isCompatible(donor, recipient);
                      return (
                        <td
                          key={donor}
                          style={{
                            backgroundColor: compatible ? 'var(--success-bg)' : 'transparent',
                          }}
                        >
                          {compatible ? (
                            <Check size={16} color="var(--success)" style={{ strokeWidth: 3 }} />
                          ) : (
                            <X size={14} color="#cbd5e1" />
                          )}
                        </td>
                      );
                    })}
                    <td style={{ textAlign: 'left', fontSize: '0.8125rem', color: 'var(--text-secondary)' }}>
                      {(matrix[recipient] || []).join(', ')}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};
