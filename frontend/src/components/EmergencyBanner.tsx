import React from 'react';
import { AlertTriangle, PhoneCall } from 'lucide-react';

export const EmergencyBanner: React.FC = () => {
  return (
    <div className="emergency-bar">
      <AlertTriangle size={16} />
      <span>
        <strong>MEDICAL EMERGENCY:</strong> If someone requires immediate blood transfusion or emergency medical care, call{' '}
        <a href="tel:911" style={{ color: '#fff', textDecoration: 'underline' }}>
          emergency services
        </a>{' '}
        or your local blood bank directly. Blood Matcher is a software coordination tool, not an emergency dispatcher.
      </span>
      <PhoneCall size={14} style={{ marginLeft: '4px' }} />
    </div>
  );
};
