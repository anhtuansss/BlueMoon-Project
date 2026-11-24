import apiClient from './apiClient';

/**
 * Service cho Thu tiền, Thanh toán & Mock Webhook SePay (Thành viên 4 - Tuấn - F06).
 */
export const paymentService = {
  // Ghi nhận thanh toán thủ công cho nghĩa vụ phí
  async recordManualPayment(obligationId, data) {
    return apiClient.post(`/fee-obligations/${obligationId}/payments`, data);
  },

  // Gửi mock webhook để kiểm thử xử lý tự động từ SePay
  async sendMockWebhook(data) {
    return apiClient.post('/integrations/sepay/mock-webhook', data);
  },
};

export default paymentService;
