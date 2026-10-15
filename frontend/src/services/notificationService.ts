import { apiClient } from './apiClient';
import { ApiResponse, NotificationDto, PagedResponse } from '@/types';

export const notificationService = {
  getMyNotifications: async (page = 0, size = 20): Promise<PagedResponse<NotificationDto>> => {
    const res = await apiClient.get<ApiResponse<PagedResponse<NotificationDto>>>(
      `/api/notifications/me?page=${page}&size=${size}`
    );
    return res.data;
  },

  markAsRead: async (notificationId: number): Promise<NotificationDto> => {
    const res = await apiClient.patch<ApiResponse<NotificationDto>>(
      `/api/notifications/${notificationId}/read`
    );
    return res.data;
  },

  markAllAsRead: async (): Promise<{ updatedCount: number }> => {
    const res = await apiClient.patch<ApiResponse<{ updatedCount: number }>>(
      '/api/notifications/me/read-all'
    );
    return res.data;
  },
};
