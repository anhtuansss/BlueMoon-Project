import apiClient from './apiClient';

/**
 * Service cho Thu tiền, Thanh toán QR & Webhook SePay (Thành viên 4 - Tuấn - F06).
 */
export const paymentService = {
  // 1. Ghi nhận thanh toán thủ công (Phase 1)
  async recordManualPayment(obligationId, data) {
    return apiClient.post(`/fee-obligations/${obligationId}/payments`, data);
  },

  async recordManualDonationPayment(campaignId, data) {
    return apiClient.post(`/donation-campaigns/${campaignId}/payments`, data);
  },

  async getPayments(params = {}) {
    const query = new URLSearchParams();
    if (params.apartmentId) query.append('apartmentId', params.apartmentId);
    if (params.obligationId) query.append('obligationId', params.obligationId);
    if (params.donationCampaignId) query.append('donationCampaignId', params.donationCampaignId);
    const queryString = query.toString() ? `?${query.toString()}` : '';
    return apiClient.get(`/payments${queryString}`);
  },

  async getPaymentById(id) {
    return apiClient.get(`/payments/${id}`);
  },

  // 2. Tạo & Quản lý yêu cầu thanh toán mã QR (Phase 2)
  async createFeePaymentRequest(obligationId) {
    return apiClient.post(`/fee-obligations/${obligationId}/payment-requests`, {});
  },

  async createDonationPaymentRequest(donationCampaignId, data) {
    return apiClient.post(`/donation-campaigns/${donationCampaignId}/payment-requests`, data);
  },

  async getPaymentRequestById(id) {
    return apiClient.get(`/payment-requests/${id}`);
  },

  async cancelPaymentRequest(id) {
    return apiClient.post(`/payment-requests/${id}/cancel`, {});
  },

  // 3. Tra cứu biến động ngân hàng SePay & Mock Webhook (Phase 2)
  async getBankTransactions(status) {
    const params = status && status !== 'ALL' ? `?status=${encodeURIComponent(status)}` : '';
    return apiClient.get(`/bank-transactions${params}`);
  },

  async getBankTransactionById(id) {
    return apiClient.get(`/bank-transactions/${id}`);
  },

  async sendMockWebhook(data) {
    return apiClient.post('/integrations/sepay/mock-webhook', data);
  },
};

export default paymentService;
