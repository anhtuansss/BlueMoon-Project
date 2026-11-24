import apiClient from './apiClient';

/**
 * Service cho Căn hộ, Hộ gia đình và Cư dân (Thành viên 2 - Thành - F02, F03).
 */
export const apartmentService = {
  // Căn hộ (Apartments)
  async getApartments(keyword) {
    const params = keyword ? `?keyword=${encodeURIComponent(keyword)}` : '';
    return apiClient.get(`/apartments${params}`);
  },

  async getApartmentById(id) {
    return apiClient.get(`/apartments/${id}`);
  },

  async createApartment(data) {
    return apiClient.post('/apartments', data);
  },

  // Cư dân (Residents)
  async getResidents(keyword) {
    const params = keyword ? `?keyword=${encodeURIComponent(keyword)}` : '';
    return apiClient.get(`/residents${params}`);
  },

  async getResidentById(id) {
    return apiClient.get(`/residents/${id}`);
  },

  async createResident(data) {
    return apiClient.post('/residents', data);
  },

  // Hộ gia đình (Households)
  async getHouseholds(keyword) {
    const params = keyword ? `?keyword=${encodeURIComponent(keyword)}` : '';
    return apiClient.get(`/households${params}`);
  },

  async getHouseholdById(id) {
    return apiClient.get(`/households/${id}`);
  },

  async createHousehold(data) {
    return apiClient.post('/households', data);
  },
};

export default apartmentService;
