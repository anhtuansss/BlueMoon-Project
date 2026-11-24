import apiClient from './apiClient';

/**
 * Service cho Khoản thu, Đợt thu phí & Nghĩa vụ phí (Thành viên 3 - Long - F05).
 */
export const feeService = {
  // Lấy danh sách loại phí
  async getFeeTypes() {
    return apiClient.get('/fee-types');
  },

  // Xem trước danh sách nghĩa vụ phí khi tạo đợt thu
  async previewFeeCampaign(data) {
    return apiClient.post('/fee-campaigns/preview', data);
  },

  // Tạo đợt thu phí và phát sinh nghĩa vụ phí theo căn hộ
  async createFeeCampaign(data) {
    return apiClient.post('/fee-campaigns', data);
  },

  // Danh sách các đợt thu phí
  async getFeeCampaigns() {
    return apiClient.get('/fee-campaigns');
  },

  // Danh sách nghĩa vụ phí theo đợt thu hoặc căn hộ
  async getFeeObligations(params = {}) {
    const query = new URLSearchParams();
    if (params.campaignId) query.append('campaignId', params.campaignId);
    if (params.apartmentId) query.append('apartmentId', params.apartmentId);
    const queryString = query.toString() ? `?${query.toString()}` : '';
    return apiClient.get(`/fee-obligations${queryString}`);
  },
};

export default feeService;
