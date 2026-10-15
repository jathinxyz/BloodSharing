import React from 'react';
import { BloodGroup } from '@/types';

interface BloodGroupBadgeProps {
  bloodGroup: BloodGroup;
  size?: 'sm' | 'md' | 'lg';
}

export const BloodGroupBadge: React.FC<BloodGroupBadgeProps> = ({ bloodGroup, size = 'md' }) => {
  const fontSizes = {
    sm: '0.75rem',
    md: '0.875rem',
    lg: '1.125rem',
  };

  const paddings = {
    sm: '0.125rem 0.375rem',
    md: '0.25rem 0.625rem',
    lg: '0.375rem 0.875rem',
  };

  return (
    <span
      className="badge badge-primary"
      style={{
        fontSize: fontSizes[size],
        padding: paddings[size],
        fontWeight: 700,
        letterSpacing: '0.02em',
      }}
    >
      {bloodGroup}
    </span>
  );
};
