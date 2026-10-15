import { apiClient } from './apiClient';
import { ApiResponse } from '@/types';

export interface SystemInfo {
  name: string;
  description: string;
  version: string;
  cooldownDays: number;
  disclaimer: string;
}

export interface DisclaimersInfo {
  screeningDisclaimer: string;
  clinicalNotice: string;
}

export const publicService = {
  getSystemInfo: async (): Promise<SystemInfo> => {
    const res = await apiClient.get<ApiResponse<SystemInfo>>('/api/public/info');
    return res.data;
  },

  getDisclaimers: async (): Promise<DisclaimersInfo> => {
    const res = await apiClient.get<ApiResponse<DisclaimersInfo>>('/api/public/disclaimers');
    return res.data;
  },

  getCompatibilityMatrix: async (): Promise<Record<string, string[]>> => {
    const res = await apiClient.get<ApiResponse<Record<string, string[]>>>(
      '/api/public/compatibility-matrix'
    );
    return res.data;
  },

  getHealth: async (): Promise<{ status: string }> => {
    const res = await apiClient.get<{ status: string }>('/actuator/health');
    return res;
  },
};
