import React, { useEffect, useState } from 'react';
import {
  AlertCircle,
  Calendar,
  CheckCircle,
  CheckCircle2,
  Clock,
  Droplet,
  FileText,
  Hospital,
  MapPin,
  Plus,
  Send,
  Users,
  X,
  XCircle,
} from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { BloodRequestDto, PagedResponse } from '@/types';
import { requestService } from '@/services/requestService';
import { BloodGroupBadge } from '@/components/BloodGroupBadge';
import { StatusBadge } from '@/components/StatusBadge';
import { formatDate, formatDateTime } from '@/utils/formatters';
import { LoadingSpinner } from '@/components/LoadingSpinner';
import { CreateRequestModal } from './CreateRequestModal';
import { MatchModal } from '../matching/MatchModal';
import { RequestInvitationsModal } from '../matching/RequestInvitationsModal';
import { Modal } from '@/components/Modal';

export const RecipientDashboard: React.FC = () => {
  const { user } = useAuth();

  const [requests, setRequests] = useState<PagedResponse<BloodRequestDto> | null>(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  // Modals state
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [selectedMatchReq, setSelectedMatchReq] = useState<BloodRequestDto | null>(null);
  const [selectedInvReq, setSelectedInvReq] = useState<BloodRequestDto | null>(null);

  // Fulfillment Modal
  const [fulfillModalReq, setFulfillModalReq] = useState<BloodRequestDto | null>(null);
  const [fulfillNotes, setFulfillNotes] = useState('');

  const loadRequests = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await requestService.getMyRequests(0, 20);
      setRequests(data);
    } catch (err: any) {
      setError(err?.message || 'Failed to load blood requests');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRequests();
  }, []);

  const handleCancelRequest = async (id: number) => {
    if (!window.confirm('Are you sure you want to cancel this blood request?')) return;
    setActionLoading(true);
    try {
      await requestService.cancelRequest(id);
      setSuccessMsg('Request marked as cancelled.');
      setTimeout(() => setSuccessMsg(null), 4000);
      loadRequests();
    } catch (err: any) {
      setError(err?.message || 'Failed to cancel request');
    } finally {
      setActionLoading(false);
    }
  };

  const handleConfirmFulfill = async () => {
    if (!fulfillModalReq) return;
    setActionLoading(true);
    try {
      await requestService.fulfillRequest(fulfillModalReq.id, undefined, fulfillNotes);
      setSuccessMsg('Blood request successfully marked as fulfilled.');
      setFulfillModalReq(null);
      setFulfillNotes('');
      setTimeout(() => setSuccessMsg(null), 4000);
      loadRequests();
    } catch (err: any) {
      setError(err?.message || 'Failed to fulfill request');
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return <LoadingSpinner message="Loading recipient dashboard..." />;
  }

  return (
    <div className="container" style={{ padding: '2.5rem 1.5rem' }}>
      {/* Top Banner / Actions */}
      <div
        style={{
          display: 'flex',
          flexWrap: 'wrap',
          alignItems: 'center',
          justifyContent: 'space-between',
          gap: '1rem',
          marginBottom: '2rem',
        }}
      >
        <div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800, margin: 0 }}>
            Blood Requests Workspace
          </h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Manage hospital blood needs, preview matches, and dispatch donor notifications
          </p>
        </div>

        <button
          type="button"
          className="btn btn-primary btn-lg"
          onClick={() => setIsCreateOpen(true)}
        >
          <Plus size={18} />
          <span>New Blood Request</span>
        </button>
      </div>

      {successMsg && (
        <div className="alert alert-success" style={{ marginBottom: '1.5rem' }}>
          <CheckCircle size={18} />
          <span>{successMsg}</span>
        </div>
      )}

      {error && (
        <div className="alert alert-danger" style={{ marginBottom: '1.5rem' }}>
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* Requests List */}
      {!requests || requests.content.length === 0 ? (
        <div className="card" style={{ padding: '4rem 1.5rem', textAlign: 'center' }}>
          <Droplet size={48} color="#cbd5e1" style={{ margin: '0 auto 1.25rem' }} />
          <h3 style={{ fontSize: '1.25rem', fontWeight: 600, marginBottom: '0.5rem' }}>
            No Active Blood Requests
          </h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.925rem', maxWidth: '440px', margin: '0 auto 1.75rem' }}>
            Create a blood request for hospital patients in need to discover compatible nearby donors.
          </p>
          <button
            type="button"
            className="btn btn-primary"
            onClick={() => setIsCreateOpen(true)}
          >
            <Plus size={16} />
            <span>Submit First Request</span>
          </button>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          {requests.content.map((req) => (
            <div key={req.id} className="card">
              <div
                className="card-body"
                style={{
                  display: 'flex',
                  flexWrap: 'wrap',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  gap: '1.25rem',
                }}
              >
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.5rem', flexWrap: 'wrap' }}>
                    <BloodGroupBadge bloodGroup={req.bloodGroup} size="md" />
                    <h3 style={{ fontSize: '1.125rem', fontWeight: 700 }}>
                      {req.hospitalName}
                    </h3>
                    <StatusBadge status={req.status} type="request" />
                    <StatusBadge status={req.urgency} type="urgency" />
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                      #REQ-{req.id}
                    </span>
                  </div>

                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1.25rem', fontSize: '0.8125rem', color: 'var(--text-secondary)' }}>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                      <Hospital size={14} /> {req.hospitalLocation}
                    </span>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                      <Droplet size={14} color="var(--primary)" /> {req.unitsRequested} Unit(s)
                    </span>
                    {req.requiredDate && (
                      <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                        <Calendar size={14} /> Required By: {formatDate(req.requiredDate)}
                      </span>
                    )}
                    <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                      <Clock size={14} /> Created: {formatDateTime(req.createdAt)}
                    </span>
                  </div>

                  {req.notes && (
                    <p style={{ marginTop: '0.625rem', fontSize: '0.8125rem', color: 'var(--text-muted)', fontStyle: 'italic' }}>
                      &ldquo;{req.notes}&rdquo;
                    </p>
                  )}
                </div>

                {/* Operations on Request */}
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                  <button
                    type="button"
                    className="btn btn-secondary btn-sm"
                    onClick={() => setSelectedInvReq(req)}
                    title="View responses from invited donors"
                  >
                    <Users size={14} />
                    <span>Responses</span>
                  </button>

                  {!req.status.includes('FULFILLED') && !req.status.includes('CANCELLED') && (
                    <>
                      <button
                        type="button"
                        className="btn btn-outline btn-sm"
                        onClick={() => setSelectedMatchReq(req)}
                        title="Preview matching donors and dispatch invitations"
                      >
                        <Send size={14} color="var(--primary)" />
                        <span>Find Matches</span>
                      </button>

                      <button
                        type="button"
                        className="btn btn-outline btn-sm"
                        onClick={() => setFulfillModalReq(req)}
                        title="Mark request fulfilled"
                      >
                        <CheckCircle2 size={14} color="var(--success)" />
                        <span>Fulfill</span>
                      </button>

                      <button
                        type="button"
                        className="btn btn-outline-danger btn-sm"
                        onClick={() => handleCancelRequest(req.id)}
                        disabled={actionLoading}
                        title="Cancel this request"
                      >
                        <X size={14} />
                        <span>Cancel</span>
                      </button>
                    </>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Create Request Modal */}
      <CreateRequestModal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        onCreated={() => {
          setSuccessMsg('Blood request submitted successfully!');
          setTimeout(() => setSuccessMsg(null), 4000);
          loadRequests();
        }}
      />

      {/* Match Preview & Dispatch Modal */}
      {selectedMatchReq && (
        <MatchModal
          isOpen={!!selectedMatchReq}
          onClose={() => setSelectedMatchReq(null)}
          request={selectedMatchReq}
          onRequestUpdated={loadRequests}
        />
      )}

      {/* Invitations & Responses Modal */}
      {selectedInvReq && (
        <RequestInvitationsModal
          isOpen={!!selectedInvReq}
          onClose={() => setSelectedInvReq(null)}
          request={selectedInvReq}
        />
      )}

      {/* Fulfill Confirmation Modal */}
      <Modal
        isOpen={!!fulfillModalReq}
        onClose={() => setFulfillModalReq(null)}
        title={`Fulfill Request #${fulfillModalReq?.id}`}
        maxWidth="480px"
      >
        <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginBottom: '1rem', lineHeight: 1.5 }}>
          Confirming fulfillment indicates that the required blood units have been collected or received from the donor. This will transition the request to FULFILLED.
        </p>
        <div className="form-group">
          <label className="label">Fulfillment Notes / Blood Bank Serial (Optional)</label>
          <textarea
            className="textarea"
            placeholder="e.g. Received 2 units via Donor Dan at City Hospital Blood Bank"
            value={fulfillNotes}
            onChange={(e) => setFulfillNotes(e.target.value)}
          />
        </div>
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.25rem' }}>
          <button type="button" className="btn btn-secondary" onClick={() => setFulfillModalReq(null)}>
            Back
          </button>
          <button
            type="button"
            className="btn btn-primary"
            onClick={handleConfirmFulfill}
            disabled={actionLoading}
          >
            Confirm Fulfillment
          </button>
        </div>
      </Modal>
    </div>
  );
};
