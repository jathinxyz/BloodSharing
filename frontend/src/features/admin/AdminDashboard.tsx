import React, { useEffect, useState } from 'react';
import {
  Activity,
  AlertCircle,
  BarChart3,
  CheckCircle,
  Clock,
  Droplet,
  Heart,
  Hospital,
  Shield,
  UserCheck,
  UserX,
  Users,
} from 'lucide-react';
import { adminService } from '@/services/adminService';
import {
  AdminDashboardDto,
  AdminReportDto,
  BloodGroup,
  BloodRequestDto,
  PagedResponse,
  Role,
  UserDto,
} from '@/types';
import { LoadingSpinner } from '@/components/LoadingSpinner';
import { BloodGroupBadge } from '@/components/BloodGroupBadge';
import { StatusBadge } from '@/components/StatusBadge';
import { formatDate, formatDateTime } from '@/utils/formatters';
import { ALL_BLOOD_GROUPS } from '@/utils/bloodGroups';

export const AdminDashboard: React.FC = () => {
  const [stats, setStats] = useState<AdminDashboardDto | null>(null);
  const [reports, setReports] = useState<AdminReportDto | null>(null);
  const [users, setUsers] = useState<PagedResponse<UserDto> | null>(null);
  const [requests, setRequests] = useState<PagedResponse<BloodRequestDto> | null>(null);

  const [activeTab, setActiveTab] = useState<'metrics' | 'users' | 'requests' | 'reports'>('metrics');
  const [selectedRole, setSelectedRole] = useState<Role | undefined>(undefined);

  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  const loadData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [sData, rData, uData, reqData] = await Promise.all([
        adminService.getDashboardStats().catch(() => null),
        adminService.getReports().catch(() => null),
        adminService.getUsers(selectedRole, 0, 20).catch(() => null),
        adminService.getAllRequests(undefined, 0, 20).catch(() => null),
      ]);
      setStats(sData);
      setReports(rData);
      setUsers(uData);
      setRequests(reqData);
    } catch (err: any) {
      setError(err?.message || 'Failed to load administrator data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [selectedRole]);

  const handleToggleUserStatus = async (user: UserDto) => {
    const newStatus = !user.enabled;
    setActionLoading(true);
    try {
      await adminService.updateUserStatus(user.id, newStatus);
      setSuccessMsg(`User ${user.fullName} (${user.email}) status changed to: ${newStatus ? 'ENABLED' : 'DISABLED'}`);
      setTimeout(() => setSuccessMsg(null), 4000);
      const uData = await adminService.getUsers(selectedRole, 0, 20);
      setUsers(uData);
    } catch (err: any) {
      setError(err?.message || 'Failed to update user status');
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return <LoadingSpinner message="Loading administrator oversight telemetry..." />;
  }

  return (
    <div className="container" style={{ padding: '2.5rem 1.5rem' }}>
      <div style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
          <Shield size={24} color="var(--primary)" />
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800, margin: 0 }}>
            Administrator Oversight Console
          </h1>
        </div>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
          Real operational telemetry, security management, and blood supply analytics
        </p>
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

      {/* Navigation Tabs */}
      <div
        style={{
          display: 'flex',
          gap: '1rem',
          borderBottom: '1px solid var(--border-light)',
          marginBottom: '2rem',
          flexWrap: 'wrap',
        }}
      >
        <button
          type="button"
          onClick={() => setActiveTab('metrics')}
          style={{
            background: 'none',
            border: 'none',
            borderBottom: activeTab === 'metrics' ? '2px solid var(--primary)' : '2px solid transparent',
            padding: '0.75rem 0.5rem',
            fontWeight: 600,
            fontSize: '0.925rem',
            color: activeTab === 'metrics' ? 'var(--primary)' : 'var(--text-secondary)',
            cursor: 'pointer',
          }}
        >
          System Telemetry
        </button>
        <button
          type="button"
          onClick={() => setActiveTab('users')}
          style={{
            background: 'none',
            border: 'none',
            borderBottom: activeTab === 'users' ? '2px solid var(--primary)' : '2px solid transparent',
            padding: '0.75rem 0.5rem',
            fontWeight: 600,
            fontSize: '0.925rem',
            color: activeTab === 'users' ? 'var(--primary)' : 'var(--text-secondary)',
            cursor: 'pointer',
          }}
        >
          User Accounts ({users?.totalElements || 0})
        </button>
        <button
          type="button"
          onClick={() => setActiveTab('requests')}
          style={{
            background: 'none',
            border: 'none',
            borderBottom: activeTab === 'requests' ? '2px solid var(--primary)' : '2px solid transparent',
            padding: '0.75rem 0.5rem',
            fontWeight: 600,
            fontSize: '0.925rem',
            color: activeTab === 'requests' ? 'var(--primary)' : 'var(--text-secondary)',
            cursor: 'pointer',
          }}
        >
          Requests Oversight ({requests?.totalElements || 0})
        </button>
        <button
          type="button"
          onClick={() => setActiveTab('reports')}
          style={{
            background: 'none',
            border: 'none',
            borderBottom: activeTab === 'reports' ? '2px solid var(--primary)' : '2px solid transparent',
            padding: '0.75rem 0.5rem',
            fontWeight: 600,
            fontSize: '0.925rem',
            color: activeTab === 'reports' ? 'var(--primary)' : 'var(--text-secondary)',
            cursor: 'pointer',
          }}
        >
          Blood Supply Analytics
        </button>
      </div>

      {/* Tab 1: System Telemetry */}
      {activeTab === 'metrics' && stats && (
        <div>
          {/* Key Metric Stat Cards */}
          <div className="grid-4" style={{ marginBottom: '2rem' }}>
            <div className="card" style={{ padding: '1.25rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                <span style={{ fontSize: '0.8125rem', fontWeight: 600, color: 'var(--text-secondary)' }}>
                  Total Donors
                </span>
                <Heart size={18} color="var(--primary)" />
              </div>
              <div style={{ fontSize: '1.75rem', fontWeight: 800 }}>{stats.totalDonors}</div>
              <span style={{ fontSize: '0.75rem', color: 'var(--success)', fontWeight: 500 }}>
                {stats.availableDonors} currently available
              </span>
            </div>

            <div className="card" style={{ padding: '1.25rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                <span style={{ fontSize: '0.8125rem', fontWeight: 600, color: 'var(--text-secondary)' }}>
                  Total Requests
                </span>
                <Hospital size={18} color="#0284c7" />
              </div>
              <div style={{ fontSize: '1.75rem', fontWeight: 800 }}>{stats.totalRequests}</div>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                {stats.openRequests} open &bull; {stats.inProgressRequests} in progress
              </span>
            </div>

            <div className="card" style={{ padding: '1.25rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                <span style={{ fontSize: '0.8125rem', fontWeight: 600, color: 'var(--text-secondary)' }}>
                  Fulfilled Requests
                </span>
                <CheckCircle size={18} color="var(--success)" />
              </div>
              <div style={{ fontSize: '1.75rem', fontWeight: 800 }}>{stats.fulfilledRequests}</div>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                {stats.cancelledRequests} cancelled
              </span>
            </div>

            <div className="card" style={{ padding: '1.25rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                <span style={{ fontSize: '0.8125rem', fontWeight: 600, color: 'var(--text-secondary)' }}>
                  Completed Donations
                </span>
                <Droplet size={18} color="var(--primary)" fill="var(--primary)" />
              </div>
              <div style={{ fontSize: '1.75rem', fontWeight: 800 }}>{stats.totalDonationsCompleted}</div>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                Logged to medical history
              </span>
            </div>
          </div>

          {/* Invitation Response Breakdown */}
          <div className="card" style={{ padding: '1.5rem', marginBottom: '2rem' }}>
            <h3 style={{ fontSize: '1.125rem', fontWeight: 700, marginBottom: '1.25rem' }}>
              Donor Invitation Performance Breakdown
            </h3>
            <div className="grid-4">
              <div style={{ padding: '1rem', backgroundColor: 'var(--bg-surface-alt)', borderRadius: 'var(--radius-md)' }}>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Total Invitations Dispatched</span>
                <strong style={{ fontSize: '1.5rem' }}>{stats.totalInvitationsSent}</strong>
              </div>
              <div style={{ padding: '1rem', backgroundColor: 'var(--success-bg)', borderRadius: 'var(--radius-md)' }}>
                <span style={{ fontSize: '0.75rem', color: '#166534', display: 'block' }}>Accepted</span>
                <strong style={{ fontSize: '1.5rem', color: 'var(--success)' }}>{stats.invitationsAccepted}</strong>
              </div>
              <div style={{ padding: '1rem', backgroundColor: 'var(--warning-bg)', borderRadius: 'var(--radius-md)' }}>
                <span style={{ fontSize: '0.75rem', color: '#92400e', display: 'block' }}>Awaiting Donor Response</span>
                <strong style={{ fontSize: '1.5rem', color: 'var(--warning)' }}>{stats.invitationsPending}</strong>
              </div>
              <div style={{ padding: '1rem', backgroundColor: 'var(--danger-bg)', borderRadius: 'var(--radius-md)' }}>
                <span style={{ fontSize: '0.75rem', color: '#991b1b', display: 'block' }}>Declined</span>
                <strong style={{ fontSize: '1.5rem', color: 'var(--danger)' }}>{stats.invitationsRejected}</strong>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Tab 2: User Accounts */}
      {activeTab === 'users' && users && (
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">User Accounts Directory</h3>
            <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
              <span style={{ fontSize: '0.8125rem', color: 'var(--text-muted)' }}>Filter Role:</span>
              <select
                className="select"
                style={{ width: 'auto', padding: '0.25rem 0.5rem', fontSize: '0.8125rem' }}
                value={selectedRole || ''}
                onChange={(e) => setSelectedRole((e.target.value as Role) || undefined)}
              >
                <option value="">All Roles</option>
                <option value="DONOR">Donor</option>
                <option value="RECIPIENT">Recipient</option>
                <option value="ADMIN">Admin</option>
              </select>
            </div>
          </div>

          <div className="table-container" style={{ border: 'none', boxShadow: 'none' }}>
            <table className="table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Full Name</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Status</th>
                  <th>Registered</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.content.map((u) => (
                  <tr key={u.id}>
                    <td>#{u.id}</td>
                    <td style={{ fontWeight: 600 }}>{u.fullName}</td>
                    <td>{u.email}</td>
                    <td>
                      <span className="badge badge-neutral">{u.role}</span>
                    </td>
                    <td>
                      <span className={`badge ${u.enabled ? 'badge-success' : 'badge-danger'}`}>
                        {u.enabled ? 'ACTIVE' : 'DISABLED'}
                      </span>
                    </td>
                    <td>{formatDate(u.createdAt)}</td>
                    <td style={{ textAlign: 'right' }}>
                      {u.role !== 'ADMIN' && (
                        <button
                          type="button"
                          className={`btn btn-sm ${u.enabled ? 'btn-outline-danger' : 'btn-outline'}`}
                          onClick={() => handleToggleUserStatus(u)}
                          disabled={actionLoading}
                        >
                          {u.enabled ? <UserX size={13} /> : <UserCheck size={13} />}
                          <span>{u.enabled ? 'Disable' : 'Enable'}</span>
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Tab 3: System-Wide Requests */}
      {activeTab === 'requests' && requests && (
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Hospital Blood Requests Oversight</h3>
          </div>
          <div className="table-container" style={{ border: 'none', boxShadow: 'none' }}>
            <table className="table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Requester</th>
                  <th>Blood Group</th>
                  <th>Units</th>
                  <th>Hospital</th>
                  <th>Urgency</th>
                  <th>Status</th>
                  <th>Created</th>
                </tr>
              </thead>
              <tbody>
                {requests.content.map((r) => (
                  <tr key={r.id}>
                    <td>#REQ-{r.id}</td>
                    <td style={{ fontWeight: 600 }}>{r.requesterName}</td>
                    <td>
                      <BloodGroupBadge bloodGroup={r.bloodGroup} size="sm" />
                    </td>
                    <td>{r.unitsRequested} Unit(s)</td>
                    <td>{r.hospitalName}</td>
                    <td>
                      <StatusBadge status={r.urgency} type="urgency" />
                    </td>
                    <td>
                      <StatusBadge status={r.status} type="request" />
                    </td>
                    <td>{formatDate(r.createdAt)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Tab 4: Blood Supply Reports */}
      {activeTab === 'reports' && reports && (
        <div>
          {/* Key Performance Indicators */}
          <div className="grid-2" style={{ marginBottom: '2rem' }}>
            <div className="card" style={{ padding: '1.5rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
                <Activity size={20} color="var(--primary)" />
                <h3 style={{ fontSize: '1rem', fontWeight: 600 }}>Invitation Acceptance Rate</h3>
              </div>
              <div style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--primary)' }}>
                {reports.invitationAcceptanceRate}%
              </div>
              <p style={{ fontSize: '0.8125rem', color: 'var(--text-muted)' }}>
                Percentage of dispatched invitations accepted by nearby compatible donors.
              </p>
            </div>

            <div className="card" style={{ padding: '1.5rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
                <CheckCircle size={20} color="var(--success)" />
                <h3 style={{ fontSize: '1rem', fontWeight: 600 }}>Request Fulfillment Rate</h3>
              </div>
              <div style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--success)' }}>
                {reports.requestFulfillmentRate}%
              </div>
              <p style={{ fontSize: '0.8125rem', color: 'var(--text-muted)' }}>
                Ratio of fulfilled hospital blood requests to total published requirements.
              </p>
            </div>
          </div>

          {/* Blood Group Distribution Table */}
          <div className="card">
            <div className="card-header">
              <h3 className="card-title">Blood Group Supply vs. Demand Distribution</h3>
            </div>
            <div className="table-container" style={{ border: 'none', boxShadow: 'none' }}>
              <table className="table">
                <thead>
                  <tr>
                    <th>Blood Group</th>
                    <th>Registered Donors</th>
                    <th>Total Requests</th>
                    <th>Supply Status</th>
                  </tr>
                </thead>
                <tbody>
                  {ALL_BLOOD_GROUPS.map((bg) => {
                    const donors = reports.donorCountByBloodGroup[bg] || 0;
                    const reqs = reports.requestCountByBloodGroup[bg] || 0;
                    return (
                      <tr key={bg}>
                        <td>
                          <BloodGroupBadge bloodGroup={bg} size="sm" />
                        </td>
                        <td style={{ fontWeight: 600 }}>{donors}</td>
                        <td style={{ fontWeight: 600 }}>{reqs}</td>
                        <td>
                          {donors >= reqs ? (
                            <span className="badge badge-success">Adequate Coverage</span>
                          ) : (
                            <span className="badge badge-warning">Critical Shortage Risk</span>
                          )}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
