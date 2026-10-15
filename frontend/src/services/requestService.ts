import { apiClient } from './apiClient';
import {
  ApiResponse,
  BloodRequestDto,
  CreateBloodRequestDto,
  InvitationDto,
  MatchResultDto,
  PagedResponse,
} from '@/types';

export const requestService = {
  createRequest: async (payload: CreateBloodRequestDto): Promise<BloodRequestDto> => {
    const res = await apiClient.post<ApiResponse<BloodRequestDto>>('/api/requests', payload);
    return res.data;
  },

  getMyRequests: async (page = 0, size = 10): Promise<PagedResponse<BloodRequestDto>> => {
    const res = await apiClient.get<ApiResponse<PagedResponse<BloodRequestDto>>>(
      `/api/requests/me?page=${page}&size=${size}`
    );
    return res.data;
  },

  getRequestById: async (requestId: number): Promise<BloodRequestDto> => {
    const res = await apiClient.get<ApiResponse<BloodRequestDto>>(`/api/requests/${requestId}`);
    return res.data;
  },

  cancelRequest: async (requestId: number): Promise<BloodRequestDto> => {
    const res = await apiClient.post<ApiResponse<BloodRequestDto>>(`/api/requests/${requestId}/cancel`);
    return res.data;
  },

  fulfillRequest: async (
    requestId: number,
    donorId?: number,
    notes?: string
  ): Promise<BloodRequestDto> => {
    const res = await apiClient.post<ApiResponse<BloodRequestDto>>(
      `/api/requests/${requestId}/fulfill`,
      { donorId, notes }
    );
    return res.data;
  },

  getMatches: async (requestId: number, limit = 10): Promise<MatchResultDto[]> => {
    const res = await apiClient.get<ApiResponse<MatchResultDto[]>>(
      `/api/requests/${requestId}/matches?limit=${limit}`
    );
    return res.data;
  },

  triggerMatch: async (requestId: number, limit = 10): Promise<MatchResultDto[]> => {
    const res = await apiClient.post<ApiResponse<MatchResultDto[]>>(
      `/api/requests/${requestId}/match`,
      { limit }
    );
    return res.data;
  },

  getRequestInvitations: async (requestId: number): Promise<InvitationDto[]> => {
    const res = await apiClient.get<ApiResponse<InvitationDto[]>>(
      `/api/requests/${requestId}/invitations`
    );
    return res.data;
  },
};
