export type BloodGroup = 'O-' | 'O+' | 'A-' | 'A+' | 'B-' | 'B+' | 'AB-' | 'AB+';

export type Role = 'DONOR' | 'RECIPIENT' | 'ADMIN';

export type RequestStatus =
  | 'OPEN'
  | 'MATCHING'
  | 'DONORS_NOTIFIED'
  | 'IN_PROGRESS'
  | 'FULFILLED'
  | 'CANCELLED'
  | 'EXPIRED';

export type InvitationStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'WITHDRAWN';

export type UrgencyLevel = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export type NotificationType =
  | 'INVITATION_RECEIVED'
  | 'INVITATION_ACCEPTED'
  | 'INVITATION_REJECTED'
  | 'REQUEST_STATUS_CHANGED'
  | 'DONATION_CONFIRMED'
  | 'SYSTEM_ALERT';

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface UserDto {
  id: number;
  fullName: string;
  email: string;
  role: Role;
  enabled: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresInMs: number;
  refreshToken: string;
  user: UserDto;
}

export interface TokenRefreshResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresInMs: number;
}

export interface DonorProfileDto {
  id: number;
  userId: number;
  fullName: string;
  email: string;
  bloodGroup: BloodGroup;
  phone: string;
  address: string;
  latitude: number | null;
  longitude: number | null;
  available: boolean;
  lastDonationDate: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface UpdateDonorProfileRequest {
  bloodGroup: BloodGroup;
  phone: string;
  address: string;
  latitude?: number | null;
  longitude?: number | null;
  available?: boolean;
  lastDonationDate?: string | null;
}

export interface BloodRequestDto {
  id: number;
  requesterId: number;
  requesterName: string;
  bloodGroup: BloodGroup;
  unitsRequested: number;
  hospitalName: string;
  hospitalLocation: string;
  latitude: number | null;
  longitude: number | null;
  urgency: UrgencyLevel;
  requiredDate: string | null;
  notes: string | null;
  status: RequestStatus;
  createdAt: string;
  updatedAt: string;
}

export interface CreateBloodRequestDto {
  bloodGroup: BloodGroup;
  unitsRequested: number;
  hospitalName: string;
  hospitalLocation: string;
  latitude?: number | null;
  longitude?: number | null;
  urgency: UrgencyLevel;
  requiredDate?: string | null;
  notes?: string;
}

export interface MatchResultDto {
  donorId: number;
  donorName: string;
  bloodGroup: BloodGroup;
  generalLocation: string;
  distanceKm: number | null;
  distanceDescription: string;
  compatibilityOutcome: string;
  eligible: boolean;
  cooldownEligible: boolean;
  cooldownDaysRemaining: number;
  alreadyInvited: boolean;
}

export interface InvitationDto {
  id: number;
  requestId: number;
  requiredBloodGroup: BloodGroup;
  unitsRequested: number;
  hospitalName: string;
  hospitalLocation: string;
  urgency: UrgencyLevel;
  compatibilityOutcome: string;
  distanceKm: number | null;
  status: InvitationStatus;
  invitedAt: string;
  respondedAt: string | null;
  recipientName: string;
  donorName: string;
  donorBloodGroup: BloodGroup;
  contactPhone?: string;
}

export interface DonationRecordDto {
  id: number;
  requestId: number | null;
  donorId: number;
  donorName: string;
  donationDate: string;
  status: string;
  notes: string | null;
  createdAt: string;
}

export interface NotificationDto {
  id: number;
  type: NotificationType;
  title: string;
  message: string;
  read: boolean;
  deliveryStatus?: string;
  requestId?: number | null;
  createdAt: string;
}

export interface AdminDashboardDto {
  totalUsers: number;
  totalDonors: number;
  totalRecipients: number;
  availableDonors: number;
  totalRequests: number;
  openRequests: number;
  inProgressRequests: number;
  fulfilledRequests: number;
  cancelledRequests: number;
  totalDonationsCompleted: number;
  totalInvitationsSent: number;
  invitationsAccepted: number;
  invitationsPending: number;
  invitationsRejected: number;
}

export interface AdminReportDto {
  donorCountByBloodGroup: Record<BloodGroup, number>;
  requestCountByBloodGroup: Record<BloodGroup, number>;
  invitationAcceptanceRate: number;
  requestFulfillmentRate: number;
}
