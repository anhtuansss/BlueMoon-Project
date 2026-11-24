import apiClient from './apiClient';

/**
 * Service xác thực & phân quyền (Thành viên 1 - Nguyên - F01).
 */
export const authService = {
  async login(username, password) {
    const data = await apiClient.post('/auth/login', { username, password });
    if (data?.accessToken) {
      localStorage.setItem('token', data.accessToken);
      localStorage.setItem('user', JSON.stringify(data.user));
    }
    return data;
  },

  async logout() {
    try {
      await apiClient.post('/auth/logout');
    } catch {
      // Ignored
    } finally {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
  },

  async getCurrentUser() {
    return apiClient.get('/auth/me');
  },

  getToken() {
    return localStorage.getItem('token');
  },

  getUser() {
    const raw = localStorage.getItem('user');
    return raw ? JSON.parse(raw) : null;
  },

  isAuthenticated() {
    return !!localStorage.getItem('token');
  },

  getUserRole() {
    const user = this.getUser();
    return user?.role || null;
  },
};

export default authService;
