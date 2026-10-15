import { apiClient } from './apiClient';
import {
  ApiResponse,
  DonationRecordDto,
  DonorProfileDto,
  InvitationDto,
  PagedResponse,
  UpdateDonorProfileRequest,
} from '@/types';

export const donorService = {
  getMyProfile: async (): Promise<DonorProfileDto> => {
    const res = await apiClient.get<ApiResponse<DonorProfileDto>>('/api/donors/me');
    return res.data;
  },

  updateMyProfile: async (payload: UpdateDonorProfileRequest): Promise<DonorProfileDto> => {
    const res = await apiClient.put<ApiResponse<DonorProfileDto>>('/api/donors/me', payload);
    return res.data;
  },

  updateAvailability: async (available: boolean): Promise<DonorProfileDto> => {
    const res = await apiClient.patch<ApiResponse<DonorProfileDto>>('/api/donors/me/availability', {
      available,
    });
    return res.data;
  },

  getMyInvitations: async (page = 0, size = 10): Promise<PagedResponse<InvitationDto>> => {
    const res = await apiClient.get<ApiResponse<PagedResponse<InvitationDto>>>(
      `/api/donors/me/invitations?page=${page}&size=${size}`
    );
    return res.data;
  },

  getMyDonations: async (page = 0, size = 10): Promise<PagedResponse<DonationRecordDto>> => {
    const res = await apiClient.get<ApiResponse<PagedResponse<DonationRecordDto>>>(
      `/api/donors/me/donations?page=${page}&size=${size}`
    );
    return res.data;
  },

  acceptInvitation: async (invitationId: number): Promise<InvitationDto> => {
    const res = await apiClient.post<ApiResponse<InvitationDto>>(
      `/api/donors/me/invitations/${invitationId}/accept`
    );
    return res.data;
  },

  rejectInvitation: async (invitationId: number): Promise<InvitationDto> => {
    const res = await apiClient.post<ApiResponse<InvitationDto>>(
      `/api/donors/me/invitations/${invitationId}/reject`
    );
    return res.data;
  },
};
