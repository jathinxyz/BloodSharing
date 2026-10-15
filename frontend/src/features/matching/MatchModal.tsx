import React, { useEffect, useState } from 'react';
import {
  AlertCircle,
  CheckCircle,
  Clock,
  Compass,
  MapPin,
  Send,
  ShieldCheck,
  User,
  Users,
} from 'lucide-react';
import { BloodRequestDto, MatchResultDto } from '@/types';
import { requestService } from '@/services/requestService';
import { Modal } from '@/components/Modal';
import { BloodGroupBadge } from '@/components/BloodGroupBadge';
import { LoadingSpinner } from '@/components/LoadingSpinner';
import { formatDistance } from '@/utils/formatters';

interface MatchModalProps {
  isOpen: boolean;
  onClose: () => void;
  request: BloodRequestDto;
  onRequestUpdated: () => void;
}

export const MatchModal: React.FC<MatchModalProps> = ({
  isOpen,
  onClose,
  request,
  onRequestUpdated,
}) => {
  const [matches, setMatches] = useState<MatchResultDto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [triggerLoading, setTriggerLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  const fetchMatches = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await requestService.getMatches(request.id, 20);
      setMatches(data);
    } catch (err: any) {
      setError(err?.message || 'Failed to load matching candidates');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isOpen) {
      fetchMatches();
      setSuccessMsg(null);
    }
  }, [isOpen, request.id]);

  const handleTriggerMatch = async () => {
    setTriggerLoading(true);
    setError(null);
    try {
      const notified = await requestService.triggerMatch(request.id, 10);
      setSuccessMsg(`Successfully notified ${notified.length} eligible donor(s) via in-app & email!`);
      onRequestUpdated();
      fetchMatches(); // refresh alreadyInvited tags
    } catch (err: any) {
      setError(err?.message || 'Failed to trigger matching invitations');
    } finally {
      setTriggerLoading(false);
    }
  };

  const eligibleCount = matches.filter((m) => m.eligible && !m.alreadyInvited).length;

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={`Candidate Matches — Request #${request.id} (${request.bloodGroup})`}
      maxWidth="680px"
      footer={
        <div style={{ display: 'flex', justifyContent: 'space-between', width: '100%', alignItems: 'center' }}>
          <span style={{ fontSize: '0.8125rem', color: 'var(--text-muted)' }}>
            {eligibleCount} eligible, uninvited candidate(s) available
          </span>
          <div style={{ display: 'flex', gap: '0.75rem' }}>
            <button type="button" className="btn btn-secondary" onClick={onClose}>
              Close
            </button>
            <button
              type="button"
              className="btn btn-primary"
              onClick={handleTriggerMatch}
              disabled={triggerLoading || eligibleCount === 0 || request.status === 'FULFILLED' || request.status === 'CANCELLED'}
            >
              <Send size={15} />
              <span>{triggerLoading ? 'Notifying Donors...' : 'Notify Available Donors'}</span>
            </button>
          </div>
        </div>
      }
    >
      {successMsg && (
        <div className="alert alert-success" style={{ marginBottom: '1rem' }}>
          <CheckCircle size={18} />
          <span>{successMsg}</span>
        </div>
      )}

      {error && (
        <div className="alert alert-danger" style={{ marginBottom: '1rem' }}>
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {loading ? (
        <LoadingSpinner message="Calculating ABO/Rh compatibility & Haversine proximity..." />
      ) : matches.length === 0 ? (
        <div style={{ padding: '2.5rem 1rem', textAlign: 'center', color: 'var(--text-muted)' }}>
          <Users size={36} color="#cbd5e1" style={{ margin: '0 auto 0.75rem' }} />
          <h4 style={{ fontSize: '1rem', fontWeight: 600, color: 'var(--text-primary)', marginBottom: '0.25rem' }}>
            No Compatible Donors Found
          </h4>
          <p style={{ fontSize: '0.8125rem' }}>
            There are currently no active, available donors with compatible blood for {request.bloodGroup} in the system.
          </p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
          {matches.map((match) => (
            <div
              key={match.donorId}
              className="card"
              style={{
                padding: '1rem',
                borderLeft: match.alreadyInvited
                  ? '4px solid #94a3b8'
                  : match.eligible
                  ? '4px solid var(--success)'
                  : '4px solid #f59e0b',
                backgroundColor: match.alreadyInvited ? 'var(--bg-surface-alt)' : '#fff',
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <BloodGroupBadge bloodGroup={match.bloodGroup} size="sm" />
                  <strong style={{ fontSize: '0.925rem' }}>{match.donorName}</strong>
                </div>

                <div style={{ display: 'flex', gap: '0.35rem' }}>
                  {match.alreadyInvited && (
                    <span className="badge badge-neutral">Already Invited</span>
                  )}
                  {match.eligible && !match.alreadyInvited && (
                    <span className="badge badge-success">Ready to Invite</span>
                  )}
                  {!match.cooldownEligible && (
                    <span className="badge badge-warning">
                      Cooldown ({match.cooldownDaysRemaining}d remaining)
                    </span>
                  )}
                </div>
              </div>

              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1rem', fontSize: '0.8125rem', color: 'var(--text-secondary)', marginBottom: '0.5rem' }}>
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                  <MapPin size={13} color="var(--text-muted)" />
                  {match.generalLocation}
                </span>
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                  <Compass size={13} color="var(--text-muted)" />
                  {formatDistance(match.distanceKm)}
                </span>
              </div>

              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                <ShieldCheck size={12} color="var(--success)" />
                <span>{match.compatibilityOutcome}</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </Modal>
  );
};
