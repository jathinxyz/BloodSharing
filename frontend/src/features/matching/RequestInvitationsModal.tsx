import React, { useEffect, useState } from 'react';
import { Check, Clock, Phone, UserCheck, Users, X } from 'lucide-react';
import { BloodRequestDto, InvitationDto } from '@/types';
import { requestService } from '@/services/requestService';
import { Modal } from '@/components/Modal';
import { BloodGroupBadge } from '@/components/BloodGroupBadge';
import { StatusBadge } from '@/components/StatusBadge';
import { formatDateTime, formatDistance } from '@/utils/formatters';
import { LoadingSpinner } from '@/components/LoadingSpinner';

interface RequestInvitationsModalProps {
  isOpen: boolean;
  onClose: () => void;
  request: BloodRequestDto;
}

export const RequestInvitationsModal: React.FC<RequestInvitationsModalProps> = ({
  isOpen,
  onClose,
  request,
}) => {
  const [invitations, setInvitations] = useState<InvitationDto[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (isOpen) {
      setLoading(true);
      requestService
        .getRequestInvitations(request.id)
        .then(setInvitations)
        .catch(() => setInvitations([]))
        .finally(() => setLoading(false));
    }
  }, [isOpen, request.id]);

  const acceptedCount = invitations.filter((i) => i.status === 'ACCEPTED').length;

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={`Invited Donors — Request #${request.id} (${request.bloodGroup})`}
      maxWidth="650px"
    >
      {loading ? (
        <LoadingSpinner message="Loading invitation responses..." />
      ) : invitations.length === 0 ? (
        <div style={{ padding: '2.5rem 1rem', textAlign: 'center', color: 'var(--text-muted)' }}>
          <Users size={36} color="#cbd5e1" style={{ margin: '0 auto 0.75rem' }} />
          <h4 style={{ fontSize: '1rem', fontWeight: 600, color: 'var(--text-primary)', marginBottom: '0.25rem' }}>
            No Donors Invited Yet
          </h4>
          <p style={{ fontSize: '0.8125rem' }}>
            Use the "Find Matches" tool on your dashboard to notify compatible donors.
          </p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.875rem' }}>
          <div
            style={{
              padding: '0.625rem 1rem',
              backgroundColor: 'var(--bg-surface-alt)',
              borderRadius: 'var(--radius-md)',
              fontSize: '0.8125rem',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
            }}
          >
            <span>Total Notified: <strong>{invitations.length}</strong></span>
            <span>Accepted Responses: <strong style={{ color: 'var(--success)' }}>{acceptedCount}</strong></span>
          </div>

          {invitations.map((inv) => (
            <div
              key={inv.id}
              className="card"
              style={{
                padding: '1rem',
                borderLeft: inv.status === 'ACCEPTED' ? '4px solid var(--success)' : '4px solid var(--border-light)',
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <BloodGroupBadge bloodGroup={inv.donorBloodGroup} size="sm" />
                  <strong style={{ fontSize: '0.925rem' }}>{inv.donorName}</strong>
                </div>
                <StatusBadge status={inv.status} type="invitation" />
              </div>

              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1rem', fontSize: '0.8125rem', color: 'var(--text-secondary)', marginBottom: '0.5rem' }}>
                <span>Distance: {formatDistance(inv.distanceKm)}</span>
                <span>Invited: {formatDateTime(inv.invitedAt)}</span>
                {inv.respondedAt && <span>Responded: {formatDateTime(inv.respondedAt)}</span>}
              </div>

              {/* Reveal Phone if Donor Accepted */}
              {inv.status === 'ACCEPTED' ? (
                <div
                  style={{
                    backgroundColor: 'var(--success-bg)',
                    border: '1px solid var(--success-border)',
                    padding: '0.5rem 0.75rem',
                    borderRadius: 'var(--radius-sm)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    fontSize: '0.8125rem',
                  }}
                >
                  <span style={{ color: '#166534', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                    <Check size={14} /> Donor Accepted — Contact Unlocked:
                  </span>
                  <a
                    href={`tel:${inv.contactPhone}`}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '0.25rem',
                      fontWeight: 700,
                      color: 'var(--primary)',
                    }}
                  >
                    <Phone size={13} /> {inv.contactPhone || 'Phone unavailable'}
                  </a>
                </div>
              ) : (
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  Donor contact details remain protected until they accept the invitation.
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </Modal>
  );
};
