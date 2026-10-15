import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { PublicLayout } from '@/layouts/PublicLayout';
import { LandingPage } from '@/pages/LandingPage';
import { HowItWorksPage } from '@/pages/HowItWorksPage';
import { CompatibilityPage } from '@/pages/CompatibilityPage';
import { DisclaimersPage } from '@/pages/DisclaimersPage';
import { LoginPage } from '@/pages/LoginPage';
import { RegisterPage } from '@/pages/RegisterPage';
import { NotificationsPage } from '@/pages/NotificationsPage';
import { NotFoundPage } from '@/pages/NotFoundPage';
import { ProtectedRoute } from '@/features/auth/ProtectedRoute';
import { DonorDashboard } from '@/features/donor/DonorDashboard';
import { RecipientDashboard } from '@/features/requests/RecipientDashboard';
import { AdminDashboard } from '@/features/admin/AdminDashboard';

export const App: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<PublicLayout />}>
          {/* Public Pages */}
          <Route index element={<LandingPage />} />
          <Route path="how-it-works" element={<HowItWorksPage />} />
          <Route path="compatibility" element={<CompatibilityPage />} />
          <Route path="disclaimers" element={<DisclaimersPage />} />
          <Route path="login" element={<LoginPage />} />
          <Route path="register" element={<RegisterPage />} />

          {/* Protected General Notifications */}
          <Route
            path="notifications"
            element={
              <ProtectedRoute>
                <NotificationsPage />
              </ProtectedRoute>
            }
          />

          {/* Protected Donor Workspace */}
          <Route
            path="donor"
            element={
              <ProtectedRoute allowedRoles={['DONOR', 'ADMIN']}>
                <DonorDashboard />
              </ProtectedRoute>
            }
          />

          {/* Protected Recipient Workspace */}
          <Route
            path="recipient"
            element={
              <ProtectedRoute allowedRoles={['RECIPIENT', 'ADMIN']}>
                <RecipientDashboard />
              </ProtectedRoute>
            }
          />

          {/* Protected Admin Oversight */}
          <Route
            path="admin"
            element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <AdminDashboard />
              </ProtectedRoute>
            }
          />

          {/* 404 Not Found Catchall */}
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};
