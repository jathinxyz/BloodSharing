import { apiClient } from './apiClient';
import {
  AdminDashboardDto,
  AdminReportDto,
  ApiResponse,
  BloodRequestDto,
  PagedResponse,
  RequestStatus,
  Role,
  UserDto,
} from '@/types';

export const adminService = {
  getDashboardStats: async (): Promise<AdminDashboardDto> => {
    const res = await apiClient.get<ApiResponse<AdminDashboardDto>>('/api/admin/dashboard');
    return res.data;
  },

  getUsers: async (role?: Role, page = 0, size = 20): Promise<PagedResponse<UserDto>> => {
    const roleParam = role ? `&role=${role}` : '';
    const res = await apiClient.get<ApiResponse<PagedResponse<UserDto>>>(
      `/api/admin/users?page=${page}&size=${size}${roleParam}`
    );
    return res.data;
  },

  updateUserStatus: async (userId: number, enabled: boolean): Promise<UserDto> => {
    const res = await apiClient.patch<ApiResponse<UserDto>>(`/api/admin/users/${userId}/status`, {
      enabled,
    });
    return res.data;
  },

  getAllRequests: async (
    status?: RequestStatus,
    page = 0,
    size = 20
  ): Promise<PagedResponse<BloodRequestDto>> => {
    const statusParam = status ? `&status=${status}` : '';
    const res = await apiClient.get<ApiResponse<PagedResponse<BloodRequestDto>>>(
      `/api/admin/requests?page=${page}&size=${size}${statusParam}`
    );
    return res.data;
  },

  getReports: async (): Promise<AdminReportDto> => {
    const res = await apiClient.get<ApiResponse<AdminReportDto>>('/api/admin/reports');
    return res.data;
  },
};
