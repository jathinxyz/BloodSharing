import React, { useEffect, useState } from 'react';
import {
  AlertCircle,
  Calendar,
  Check,
  CheckCircle,
  Clock,
  Crosshair,
  Droplet,
  Edit2,
  Heart,
  Hospital,
  MapPin,
  Phone,
  Power,
  RefreshCw,
  X,
  XCircle,
} from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { donorService } from '@/services/donorService';
import { DonationRecordDto, DonorProfileDto, InvitationDto, PagedResponse } from '@/types';
import { BloodGroupBadge } from '@/components/BloodGroupBadge';
import { StatusBadge } from '@/components/StatusBadge';
import { formatDate, formatDateTime, formatDistance } from '@/utils/formatters';
import { Modal } from '@/components/Modal';
import { LoadingSpinner } from '@/components/LoadingSpinner';
import { ALL_BLOOD_GROUPS } from '@/utils/bloodGroups';
import { useGeolocation } from '@/hooks/useGeolocation';

export const DonorDashboard: React.FC = () => {
  const { user } = useAuth();

  const [profile, setProfile] = useState<DonorProfileDto | null>(null);
  const [invitations, setInvitations] = useState<PagedResponse<InvitationDto> | null>(null);
  const [donations, setDonations] = useState<PagedResponse<DonationRecordDto> | null>(null);

  const [activeTab, setActiveTab] = useState<'invitations' | 'donations'>('invitations');
  const [loading, setLoading] = useState<boolean>(true);
  const [actionLoading, setActionLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  // Profile Edit Modal State
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editBloodGroup, setEditBloodGroup] = useState(profile?.bloodGroup || 'O+');
  const [editPhone, setEditPhone] = useState('');
  const [editAddress, setEditAddress] = useState('');
  const [editLat, setEditLat] = useState('');
  const [editLon, setEditLon] = useState('');

  const { latitude: geoLat, longitude: geoLon, loading: geoLoading, getLocation } = useGeolocation();

  useEffect(() => {
    if (geoLat !== null && geoLon !== null) {
      setEditLat(geoLat.toString());
      setEditLon(geoLon.toString());
    }
  }, [geoLat, geoLon]);

  const loadData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [profData, invData, donData] = await Promise.all([
        donorService.getMyProfile().catch(() => null),
        donorService.getMyInvitations(0, 20).catch(() => null),
        donorService.getMyDonations(0, 20).catch(() => null),
      ]);
      setProfile(profData);
      setInvitations(invData);
      setDonations(donData);

      if (profData) {
        setEditBloodGroup(profData.bloodGroup);
        setEditPhone(profData.phone);
        setEditAddress(profData.address);
        setEditLat(profData.latitude?.toString() || '');
        setEditLon(profData.longitude?.toString() || '');
      }
    } catch (err: any) {
      setError(err?.message || 'Failed to load donor dashboard');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleToggleAvailability = async () => {
    if (!profile) return;
    const newStatus = !profile.available;
    setActionLoading(true);
    try {
      const updated = await donorService.updateAvailability(newStatus);
      setProfile(updated);
      setSuccessMsg(`Availability status changed to: ${newStatus ? 'AVAILABLE' : 'UNAVAILABLE'}`);
      setTimeout(() => setSuccessMsg(null), 4000);
    } catch (err: any) {
      setError(err?.message || 'Failed to update availability status');
    } finally {
      setActionLoading(false);
    }
  };

  const handleAccept = async (id: number) => {
    setActionLoading(true);
    try {
      await donorService.acceptInvitation(id);
      setSuccessMsg('Invitation accepted! Your contact phone is now shared with the hospital coordinator.');
      setTimeout(() => setSuccessMsg(null), 5000);
      const invData = await donorService.getMyInvitations(0, 20);
      setInvitations(invData);
    } catch (err: any) {
      setError(err?.message || 'Failed to accept invitation');
    } finally {
      setActionLoading(false);
    }
  };

  const handleReject = async (id: number) => {
    setActionLoading(true);
    try {
      await donorService.rejectInvitation(id);
      setSuccessMsg('Invitation declined.');
      setTimeout(() => setSuccessMsg(null), 4000);
      const invData = await donorService.getMyInvitations(0, 20);
      setInvitations(invData);
    } catch (err: any) {
      setError(err?.message || 'Failed to decline invitation');
    } finally {
      setActionLoading(false);
    }
  };

  const handleSaveProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    setActionLoading(true);
    try {
      const updated = await donorService.updateMyProfile({
        bloodGroup: editBloodGroup,
        phone: editPhone.trim(),
        address: editAddress.trim(),
        latitude: editLat ? parseFloat(editLat) : null,
        longitude: editLon ? parseFloat(editLon) : null,
        available: profile?.available ?? true,
      });
      setProfile(updated);
      setIsEditModalOpen(false);
      setSuccessMsg('Donor profile updated successfully.');
      setTimeout(() => setSuccessMsg(null), 4000);
    } catch (err: any) {
      setError(err?.message || 'Failed to update profile');
    } finally {
      setActionLoading(false);
    }
  };

  // Cooldown calculation (56 days)
  const getCooldownStatus = () => {
    if (!profile?.lastDonationDate) {
      return { eligible: true, message: 'Eligible to donate' };
    }
    const lastDate = new Date(profile.lastDonationDate);
    const cooldownMs = 56 * 24 * 60 * 60 * 1000;
    const nextEligibleDate = new Date(lastDate.getTime() + cooldownMs);
    const now = new Date();

    if (now >= nextEligibleDate) {
      return { eligible: true, message: 'Eligible to donate (56-day cooldown completed)' };
    }

    const diffDays = Math.ceil((nextEligibleDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
    return {
      eligible: false,
      message: `Recovery Cooldown: ${diffDays} days remaining (Eligible ${formatDate(nextEligibleDate.toISOString())})`,
    };
  };

  const cooldown = getCooldownStatus();

  if (loading) {
    return <LoadingSpinner message="Loading donor workspace..." />;
  }

  return (
    <div className="container" style={{ padding: '2.5rem 1.5rem' }}>
      {/* Messages */}
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

      {/* Header Profile Summary */}
      <div className="card" style={{ marginBottom: '2rem' }}>
        <div
          className="card-body"
          style={{
            display: 'flex',
            flexWrap: 'wrap',
            justifyContent: 'space-between',
            alignItems: 'center',
            gap: '1.5rem',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
            {profile && <BloodGroupBadge bloodGroup={profile.bloodGroup} size="lg" />}
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.25rem' }}>
                <h1 style={{ fontSize: '1.5rem', fontWeight: 700, margin: 0 }}>
                  {profile?.fullName || user?.fullName}
                </h1>
                <span className={`badge ${profile?.available ? 'badge-success' : 'badge-neutral'}`}>
                  {profile?.available ? 'AVAILABLE FOR DONATION' : 'CURRENTLY UNAVAILABLE'}
                </span>
              </div>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1.25rem', fontSize: '0.8125rem', color: 'var(--text-secondary)' }}>
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                  <MapPin size={14} color="var(--text-muted)" />
                  {profile?.address || 'Address unlisted'}
                </span>
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                  <Phone size={14} color="var(--text-muted)" />
                  {profile?.phone || 'Phone unlisted'}
                </span>
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                  <Calendar size={14} color="var(--text-muted)" />
                  Last Donation: {formatDate(profile?.lastDonationDate)}
                </span>
              </div>
            </div>
          </div>

          {/* Quick Actions */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <button
              type="button"
              className={`btn ${profile?.available ? 'btn-outline-danger' : 'btn-primary'}`}
              onClick={handleToggleAvailability}
              disabled={actionLoading}
            >
              <Power size={16} />
              <span>{profile?.available ? 'Go Unavailable' : 'Set as Available'}</span>
            </button>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsEditModalOpen(true)}
            >
              <Edit2 size={16} />
              <span>Edit Profile</span>
            </button>
          </div>
        </div>

        {/* Cooldown Status Strip */}
        <div
          style={{
            padding: '0.75rem 1.5rem',
            backgroundColor: cooldown.eligible ? 'var(--success-bg)' : 'var(--warning-bg)',
            borderTop: '1px solid var(--border-light)',
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem',
            fontSize: '0.8125rem',
            color: cooldown.eligible ? '#166534' : '#92400e',
            fontWeight: 500,
          }}
        >
          <Clock size={16} />
          <span>{cooldown.message}</span>
        </div>
      </div>

      {/* Navigation Tabs */}
      <div
        style={{
          display: 'flex',
          gap: '1rem',
          borderBottom: '1px solid var(--border-light)',
          marginBottom: '1.5rem',
        }}
      >
        <button
          type="button"
          onClick={() => setActiveTab('invitations')}
          style={{
            background: 'none',
            border: 'none',
            borderBottom: activeTab === 'invitations' ? '2px solid var(--primary)' : '2px solid transparent',
            padding: '0.75rem 0.5rem',
            fontWeight: 600,
            fontSize: '0.925rem',
            color: activeTab === 'invitations' ? 'var(--primary)' : 'var(--text-secondary)',
            cursor: 'pointer',
          }}
        >
          Incoming Invitations ({invitations?.totalElements || 0})
        </button>
        <button
          type="button"
          onClick={() => setActiveTab('donations')}
          style={{
            background: 'none',
            border: 'none',
            borderBottom: activeTab === 'donations' ? '2px solid var(--primary)' : '2px solid transparent',
            padding: '0.75rem 0.5rem',
            fontWeight: 600,
            fontSize: '0.925rem',
            color: activeTab === 'donations' ? 'var(--primary)' : 'var(--text-secondary)',
            cursor: 'pointer',
          }}
        >
          Donation History ({donations?.totalElements || 0})
        </button>
      </div>

      {/* Tab 1: Invitations List */}
      {activeTab === 'invitations' && (
        <div>
          {!invitations || invitations.content.length === 0 ? (
            <div className="card" style={{ padding: '3.5rem 1.5rem', textAlign: 'center' }}>
              <Heart size={40} color="#cbd5e1" style={{ margin: '0 auto 1rem' }} />
              <h3 style={{ fontSize: '1.125rem', fontWeight: 600, marginBottom: '0.5rem' }}>
                No Invitations Right Now
              </h3>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', maxWidth: '420px', margin: '0 auto' }}>
                When hospitals or patients request {profile?.bloodGroup || 'your'} blood in your vicinity, you will be notified here immediately.
              </p>
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {invitations.content.map((inv) => (
                <div key={inv.id} className="card">
                  <div
                    className="card-body"
                    style={{
                      display: 'flex',
                      flexWrap: 'wrap',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      gap: '1rem',
                    }}
                  >
                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.5rem' }}>
                        <BloodGroupBadge bloodGroup={inv.requiredBloodGroup} />
                        <h3 style={{ fontSize: '1.125rem', fontWeight: 600 }}>
                          {inv.hospitalName}
                        </h3>
                        <StatusBadge status={inv.urgency} type="urgency" />
                        <StatusBadge status={inv.status} type="invitation" />
                      </div>

                      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1.25rem', fontSize: '0.8125rem', color: 'var(--text-secondary)' }}>
                        <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                          <Hospital size={14} /> {inv.hospitalLocation}
                        </span>
                        <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                          <Droplet size={14} color="var(--primary)" /> {inv.unitsRequested} Unit(s) Requested
                        </span>
                        <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                          <MapPin size={14} /> {formatDistance(inv.distanceKm)}
                        </span>
                        <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                          <Clock size={14} /> Invited {formatDateTime(inv.invitedAt)}
                        </span>
                      </div>

                      {inv.status === 'ACCEPTED' && (
                        <div
                          style={{
                            marginTop: '0.75rem',
                            padding: '0.5rem 0.75rem',
                            backgroundColor: 'var(--success-bg)',
                            borderRadius: 'var(--radius-sm)',
                            fontSize: '0.75rem',
                            color: '#166534',
                            display: 'flex',
                            alignItems: 'center',
                            gap: '0.5rem',
                          }}
                        >
                          <Check size={14} />
                          <span>
                            You accepted this request. The hospital coordinator has received your contact details to schedule your donation.
                          </span>
                        </div>
                      )}
                    </div>

                    {inv.status === 'PENDING' && (
                      <div style={{ display: 'flex', gap: '0.75rem' }}>
                        <button
                          type="button"
                          className="btn btn-outline-danger btn-sm"
                          onClick={() => handleReject(inv.id)}
                          disabled={actionLoading}
                        >
                          <X size={15} />
                          <span>Decline</span>
                        </button>
                        <button
                          type="button"
                          className="btn btn-primary btn-sm"
                          onClick={() => handleAccept(inv.id)}
                          disabled={actionLoading}
                        >
                          <Check size={15} />
                          <span>Accept Request</span>
                        </button>
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Tab 2: Donation History */}
      {activeTab === 'donations' && (
        <div>
          {!donations || donations.content.length === 0 ? (
            <div className="card" style={{ padding: '3.5rem 1.5rem', textAlign: 'center' }}>
              <Calendar size={40} color="#cbd5e1" style={{ margin: '0 auto 1rem' }} />
              <h3 style={{ fontSize: '1.125rem', fontWeight: 600, marginBottom: '0.5rem' }}>
                No Completed Donations Recorded
              </h3>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
                Once you complete a donation through an authorized request, it will be catalogued in your medical donation history.
              </p>
            </div>
          ) : (
            <div className="table-container">
              <table className="table">
                <thead>
                  <tr>
                    <th>Donation Date</th>
                    <th>Request Ref</th>
                    <th>Status</th>
                    <th>Clinical Notes</th>
                  </tr>
                </thead>
                <tbody>
                  {donations.content.map((rec) => (
                    <tr key={rec.id}>
                      <td style={{ fontWeight: 600 }}>{formatDate(rec.donationDate)}</td>
                      <td>{rec.requestId ? `#REQ-${rec.requestId}` : 'External Donation'}</td>
                      <td>
                        <span className="badge badge-success">{rec.status}</span>
                      </td>
                      <td style={{ color: 'var(--text-secondary)' }}>
                        {rec.notes || 'Routine whole blood donation'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* Edit Profile Modal */}
      <Modal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        title="Update Donor Profile"
      >
        <form onSubmit={handleSaveProfile}>
          <div className="form-group">
            <label className="label">Blood Group</label>
            <select
              className="select"
              value={editBloodGroup}
              onChange={(e) => setEditBloodGroup(e.target.value as any)}
              required
            >
              {ALL_BLOOD_GROUPS.map((bg) => (
                <option key={bg} value={bg}>
                  {bg}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label className="label">Contact Phone</label>
            <input
              type="tel"
              className="input"
              value={editPhone}
              onChange={(e) => setEditPhone(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="label">General Location / Address</label>
            <input
              type="text"
              className="input"
              value={editAddress}
              onChange={(e) => setEditAddress(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.375rem' }}>
              <label className="label" style={{ margin: 0 }}>
                Coordinates (Optional)
              </label>
              <button
                type="button"
                onClick={getLocation}
                disabled={geoLoading}
                className="btn btn-outline btn-sm"
                style={{ fontSize: '0.7rem' }}
              >
                <Crosshair size={12} />
                <span>{geoLoading ? 'Acquiring...' : 'Use My GPS'}</span>
              </button>
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem' }}>
              <input
                type="number"
                step="any"
                className="input"
                placeholder="Latitude"
                value={editLat}
                onChange={(e) => setEditLat(e.target.value)}
              />
              <input
                type="number"
                step="any"
                className="input"
                placeholder="Longitude"
                value={editLon}
                onChange={(e) => setEditLon(e.target.value)}
              />
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsEditModalOpen(false)}
            >
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={actionLoading}>
              Save Changes
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
