import { ApiResponse } from '@/types';

class ApiError extends Error {
  status: number;
  data: any;

  constructor(message: string, status: number, data?: any) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.data = data;
  }
}

export { ApiError };

const TOKEN_KEY = 'bm_access_token';
const REFRESH_TOKEN_KEY = 'bm_refresh_token';

export const tokenStorage = {
  getAccessToken: (): string | null => localStorage.getItem(TOKEN_KEY),
  setAccessToken: (token: string): void => localStorage.setItem(TOKEN_KEY, token),
  getRefreshToken: (): string | null => localStorage.getItem(REFRESH_TOKEN_KEY),
  setRefreshToken: (token: string): void => localStorage.setItem(REFRESH_TOKEN_KEY, token),
  clear: (): void => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
  },
};

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

let isRefreshing = false;
let refreshSubscribers: ((token: string) => void)[] = [];

function onRefreshed(token: string) {
  refreshSubscribers.forEach((callback) => callback(token));
  refreshSubscribers = [];
}

async function request<T>(
  endpoint: string,
  options: RequestInit = {},
  isRetry = false
): Promise<T> {
  const url = `${BASE_URL}${endpoint}`;
  const headers = new Headers(options.headers || {});

  if (!headers.has('Content-Type') && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json');
  }

  const token = tokenStorage.getAccessToken();
  if (token && !headers.has('Authorization')) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const response = await fetch(url, {
    ...options,
    headers,
  });

  // Handle 401 and attempt refresh
  if (response.status === 401 && !isRetry && !endpoint.includes('/auth/login') && !endpoint.includes('/auth/refresh')) {
    const refreshToken = tokenStorage.getRefreshToken();
    if (refreshToken) {
      if (!isRefreshing) {
        isRefreshing = true;
        try {
          const refreshRes = await fetch(`${BASE_URL}/api/auth/refresh`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ refreshToken }),
          });

          if (refreshRes.ok) {
            const data: ApiResponse<{ accessToken: string; refreshToken: string }> = await refreshRes.json();
            tokenStorage.setAccessToken(data.data.accessToken);
            if (data.data.refreshToken) {
              tokenStorage.setRefreshToken(data.data.refreshToken);
            }
            onRefreshed(data.data.accessToken);
            isRefreshing = false;
            return request<T>(endpoint, options, true);
          } else {
            tokenStorage.clear();
            isRefreshing = false;
            window.dispatchEvent(new CustomEvent('auth:expired'));
          }
        } catch (err) {
          tokenStorage.clear();
          isRefreshing = false;
          window.dispatchEvent(new CustomEvent('auth:expired'));
        }
      } else {
        // Wait for active refresh
        return new Promise<T>((resolve) => {
          refreshSubscribers.push((newToken: string) => {
            headers.set('Authorization', `Bearer ${newToken}`);
            resolve(request<T>(endpoint, { ...options, headers }, true));
          });
        });
      }
    } else {
      window.dispatchEvent(new CustomEvent('auth:expired'));
    }
  }

  if (!response.ok) {
    let errorMsg = `HTTP Error ${response.status}`;
    let errorData: any = null;
    try {
      errorData = await response.json();
      if (errorData?.message) {
        errorMsg = errorData.message;
      }
    } catch {
      // response was not JSON
    }
    throw new ApiError(errorMsg, response.status, errorData);
  }

  if (response.status === 204) {
    return {} as T;
  }

  return response.json();
}

export const apiClient = {
  get: <T>(endpoint: string, options?: RequestInit) =>
    request<T>(endpoint, { ...options, method: 'GET' }),
  post: <T>(endpoint: string, body?: any, options?: RequestInit) =>
    request<T>(endpoint, {
      ...options,
      method: 'POST',
      body: body instanceof FormData ? body : JSON.stringify(body),
    }),
  put: <T>(endpoint: string, body?: any, options?: RequestInit) =>
    request<T>(endpoint, {
      ...options,
      method: 'PUT',
      body: JSON.stringify(body),
    }),
  patch: <T>(endpoint: string, body?: any, options?: RequestInit) =>
    request<T>(endpoint, {
      ...options,
      method: 'PATCH',
      body: body ? JSON.stringify(body) : undefined,
    }),
  delete: <T>(endpoint: string, options?: RequestInit) =>
    request<T>(endpoint, { ...options, method: 'DELETE' }),
};
