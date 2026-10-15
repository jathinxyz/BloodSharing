import React, { useEffect, useState } from 'react';
import { Bell, Check, Clock } from 'lucide-react';
import { notificationService } from '@/services/notificationService';
import { NotificationDto, PagedResponse } from '@/types';
import { formatDateTime } from '@/utils/formatters';
import { LoadingSpinner } from '@/components/LoadingSpinner';

export const NotificationsPage: React.FC = () => {
  const [notifications, setNotifications] = useState<PagedResponse<NotificationDto> | null>(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);

  const fetchNotifications = async () => {
    setLoading(true);
    try {
      const data = await notificationService.getMyNotifications(0, 50);
      setNotifications(data);
    } catch {
      // ignore
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, []);

  const handleMarkAsRead = async (id: number) => {
    try {
      await notificationService.markAsRead(id);
      if (notifications) {
        setNotifications({
          ...notifications,
          content: notifications.content.map((n) => (n.id === id ? { ...n, read: true } : n)),
        });
      }
    } catch {
      // ignore
    }
  };

  const handleMarkAllRead = async () => {
    setActionLoading(true);
    try {
      await notificationService.markAllAsRead();
      if (notifications) {
        setNotifications({
          ...notifications,
          content: notifications.content.map((n) => ({ ...n, read: true })),
        });
      }
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return <LoadingSpinner message="Loading notification history..." />;
  }

  return (
    <div className="container" style={{ padding: '3rem 1.5rem', maxWidth: '720px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h1 style={{ fontSize: '1.5rem', fontWeight: 800, margin: 0 }}>Notification History</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.8125rem' }}>
            System alerts, invitation requests, and matching updates
          </p>
        </div>

        {notifications && notifications.content.some((n) => !n.read) && (
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            onClick={handleMarkAllRead}
            disabled={actionLoading}
          >
            <Check size={14} />
            <span>Mark All as Read</span>
          </button>
        )}
      </div>

      <div className="card">
        {!notifications || notifications.content.length === 0 ? (
          <div style={{ padding: '3.5rem 1.5rem', textAlign: 'center', color: 'var(--text-muted)' }}>
            <Bell size={40} color="#cbd5e1" style={{ margin: '0 auto 1rem' }} />
            <h3 style={{ fontSize: '1.125rem', fontWeight: 600, color: 'var(--text-primary)', marginBottom: '0.25rem' }}>
              No Notifications
            </h3>
            <p style={{ fontSize: '0.875rem' }}>You're all caught up!</p>
          </div>
        ) : (
          <div>
            {notifications.content.map((n) => (
              <div
                key={n.id}
                onClick={() => !n.read && handleMarkAsRead(n.id)}
                style={{
                  padding: '1rem 1.25rem',
                  borderBottom: '1px solid var(--border-light)',
                  backgroundColor: n.read ? 'transparent' : 'var(--primary-light)',
                  cursor: n.read ? 'default' : 'pointer',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'flex-start',
                  gap: '1rem',
                }}
              >
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                    <strong style={{ fontSize: '0.9rem', color: 'var(--text-primary)' }}>
                      {n.title}
                    </strong>
                    {!n.read && (
                      <span className="badge badge-primary" style={{ fontSize: '0.65rem' }}>
                        NEW
                      </span>
                    )}
                  </div>
                  <p style={{ fontSize: '0.8125rem', color: 'var(--text-secondary)', marginBottom: '0.35rem' }}>
                    {n.message}
                  </p>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', fontSize: '0.725rem', color: 'var(--text-muted)' }}>
                    <Clock size={12} />
                    <span>{formatDateTime(n.createdAt)}</span>
                  </div>
                </div>

                {!n.read && (
                  <button
                    type="button"
                    className="btn btn-outline btn-sm"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleMarkAsRead(n.id);
                    }}
                    style={{ padding: '0.2rem 0.5rem', fontSize: '0.7rem' }}
                  >
                    Mark Read
                  </button>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
