import React from 'react';
import { InvitationStatus, RequestStatus, UrgencyLevel } from '@/types';

interface StatusBadgeProps {
  status: RequestStatus | InvitationStatus | UrgencyLevel | string;
  type?: 'request' | 'invitation' | 'urgency';
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status, type = 'request' }) => {
  let badgeClass = 'badge-neutral';
  let label = status.replace('_', ' ');

  if (type === 'urgency') {
    switch (status) {
      case 'CRITICAL':
        badgeClass = 'badge-danger';
        break;
      case 'HIGH':
        badgeClass = 'badge-warning';
        break;
      case 'MEDIUM':
        badgeClass = 'badge-info';
        break;
      case 'LOW':
        badgeClass = 'badge-neutral';
        break;
    }
    return <span className={`badge ${badgeClass}`}>{label}</span>;
  }

  if (type === 'invitation') {
    switch (status) {
      case 'ACCEPTED':
        badgeClass = 'badge-success';
        break;
      case 'PENDING':
        badgeClass = 'badge-warning';
        break;
      case 'REJECTED':
        badgeClass = 'badge-danger';
        break;
      case 'WITHDRAWN':
        badgeClass = 'badge-neutral';
        break;
    }
    return <span className={`badge ${badgeClass}`}>{label}</span>;
  }

  // Request statuses
  switch (status) {
    case 'OPEN':
      badgeClass = 'badge-info';
      break;
    case 'DONORS_NOTIFIED':
    case 'MATCHING':
      badgeClass = 'badge-warning';
      break;
    case 'IN_PROGRESS':
      badgeClass = 'badge-primary';
      break;
    case 'FULFILLED':
      badgeClass = 'badge-success';
      break;
    case 'CANCELLED':
    case 'EXPIRED':
      badgeClass = 'badge-neutral';
      break;
  }

  return <span className={`badge ${badgeClass}`}>{label}</span>;
};
