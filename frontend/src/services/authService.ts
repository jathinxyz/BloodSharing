import { apiClient, tokenStorage } from './apiClient';
import { ApiResponse, AuthResponse, BloodGroup, Role, UserDto } from '@/types';

export interface RegisterPayload {
  fullName: string;
  email: string;
  password: string;
  role: Role;
  bloodGroup?: BloodGroup;
  phone?: string;
  address?: string;
  latitude?: number | null;
  longitude?: number | null;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export const authService = {
  register: async (payload: RegisterPayload): Promise<AuthResponse> => {
    const res = await apiClient.post<ApiResponse<AuthResponse>>('/api/auth/register', payload);
    if (res.data.accessToken) {
      tokenStorage.setAccessToken(res.data.accessToken);
      if (res.data.refreshToken) {
        tokenStorage.setRefreshToken(res.data.refreshToken);
      }
    }
    return res.data;
  },

  login: async (payload: LoginPayload): Promise<AuthResponse> => {
    const res = await apiClient.post<ApiResponse<AuthResponse>>('/api/auth/login', payload);
    if (res.data.accessToken) {
      tokenStorage.setAccessToken(res.data.accessToken);
      if (res.data.refreshToken) {
        tokenStorage.setRefreshToken(res.data.refreshToken);
      }
    }
    return res.data;
  },

  getCurrentUser: async (): Promise<UserDto> => {
    const res = await apiClient.get<ApiResponse<UserDto>>('/api/auth/me');
    return res.data;
  },

  logout: async (): Promise<void> => {
    try {
      await apiClient.post<ApiResponse<void>>('/api/auth/logout');
    } catch {
      // ignore network errors on logout
    } finally {
      tokenStorage.clear();
    }
  },
};
