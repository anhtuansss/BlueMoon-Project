import apiClient from './apiClient';

/**
 * Service cho Thu tiền, Thanh toán & Mock Webhook SePay (Thành viên 4 - Tuấn - F06).
 */
export const paymentService = {
  // Ghi nhận thanh toán thủ công cho nghĩa vụ phí bắt buộc
  async recordManualPayment(obligationId, data) {
    return apiClient.post(`/fee-obligations/${obligationId}/payments`, data);
  },

  // Ghi nhận đóng góp tự nguyện thủ công
  async recordManualDonationPayment(campaignId, data) {
    return apiClient.post(`/donation-campaigns/${campaignId}/payments`, data);
  },

  // Lấy danh sách thanh toán (hỗ trợ lọc theo căn hộ, nghĩa vụ, đợt đóng góp)
  async getPayments(params = {}) {
    const query = new URLSearchParams();
    if (params.apartmentId) query.append('apartmentId', params.apartmentId);
    if (params.obligationId) query.append('obligationId', params.obligationId);
    if (params.donationCampaignId) query.append('donationCampaignId', params.donationCampaignId);
    const queryString = query.toString() ? `?${query.toString()}` : '';
    return apiClient.get(`/payments${queryString}`);
  },

  // Xem chi tiết 1 giao dịch thanh toán
  async getPaymentById(id) {
    return apiClient.get(`/payments/${id}`);
  },

  // Gửi mock webhook để kiểm thử xử lý tự động từ SePay
  async sendMockWebhook(data) {
    return apiClient.post('/integrations/sepay/mock-webhook', data);
  },
};

export default paymentService;
