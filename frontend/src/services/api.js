import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add JWT token to all requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // If unauthorized on protected routes, clear auth data and handle redirect
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      // Only redirect if not already on the login page
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// Auth APIs
export const authAPI = {
  login: (email, password) => api.post('/auth/login', { email, password }),
  register: (data) => api.post('/auth/register', data),
  validateToken: (token) => api.post('/auth/validate', { token }),
  health: () => api.get('/auth/health'),
};

// Shipment APIs
export const shipmentAPI = {
  getAll: () => api.get('/shipments'),
  getById: (id) => api.get(`/shipments/${id}`),
  getByTracking: (trackingNumber) => api.get(`/shipments/tracking/${trackingNumber}`),
  search: (query) => api.get(`/shipments/search?query=${query}`),
  getByStatus: (status) => api.get(`/shipments/status/${status}`),
  getDelayed: () => api.get('/shipments/delayed'),
  getBySupplier: (supplierId) => api.get(`/shipments/supplier/${supplierId}`),
  create: (data) => api.post('/shipments', data),
  update: (id, data) => api.put(`/shipments/${id}`, data),
  updateStatus: (id, status) => api.patch(`/shipments/${id}/status`, { status }),
  delete: (id) => api.delete(`/shipments/${id}`),
  getStats: () => api.get('/shipments/stats/summary'),
};

// Supplier APIs
export const supplierAPI = {
  getAll: () => api.get('/suppliers'),
  getById: (id) => api.get(`/suppliers/${id}`),
  search: (query) => api.get(`/suppliers/search?query=${query}`),
  getByCountry: (country) => api.get(`/suppliers/country/${country}`),
  getLowReliability: (threshold) => api.get(`/suppliers/low-reliability?threshold=${threshold}`),
  create: (data) => api.post('/suppliers', data),
  update: (id, data) => api.put(`/suppliers/${id}`, data),
  delete: (id) => api.delete(`/suppliers/${id}`),
  getStats: () => api.get('/suppliers/stats/summary'),
  getByCountryStats: () => api.get('/suppliers/stats/by-country'),
};

// Inventory APIs
export const inventoryAPI = {
  getAll: () => api.get('/inventory'),
  getById: (id) => api.get(`/inventory/${id}`),
  getLowStock: () => api.get('/inventory/low-stock'),
  getReorderNeeded: () => api.get('/inventory/reorder-needed'),
  getOverstock: () => api.get('/inventory/overstock'),
  getStats: () => api.get('/inventory/stats/summary'),
  create: (data) => api.post('/inventory', data),
  update: (id, data) => api.put(`/inventory/${id}`, data),
  delete: (id) => api.delete(`/inventory/${id}`),
  search: (query) => api.get(`/inventory/search?q=${encodeURIComponent(query)}`),
};

// Risk APIs
export const riskAPI = {
  calculate: (shipmentId) => api.post(`/risk/calculate/${shipmentId}`),
  predict: (shipmentId) => api.post(`/risk/predict/${shipmentId}`),
  calculateAll: () => api.post('/risk/calculate-all'),
  getHighRisk: () => api.get('/risk/high-risk'),
  getCriticalRisk: () => api.get('/risk/critical-risk'),
  getPredictedDelays: () => api.get('/risk/predicted-delays'),
  getScore: (shipmentId) => api.get(`/risk/score/${shipmentId}`),
  getPrediction: (shipmentId) => api.get(`/risk/prediction/${shipmentId}`),
  getStats: () => api.get('/risk/stats/summary'),
  getDistribution: () => api.get('/risk/stats/distribution'),
};

// Analytics APIs
export const analyticsAPI = {
  getDashboard: () => api.get('/analytics/dashboard'),
  getQuickStats: (userId) => api.get(`/analytics/quick-stats?userId=${userId}`),
  getShipmentSummary: () => api.get('/analytics/shipments/summary'),
  getSupplierSummary: () => api.get('/analytics/suppliers/summary'),
  getRiskSummary: () => api.get('/analytics/risk/summary'),
  getUnreadCount: (userId) => api.get(`/analytics/notifications/unread?userId=${userId}`),
  getShipmentStatusChart: () => api.get('/analytics/charts/shipment-status'),
  getMonthlyShipmentsChart: () => api.get('/analytics/charts/monthly-shipments'),
  getRiskDistributionChart: () => api.get('/analytics/charts/risk-distribution'),
  getSuppliersByCountryChart: () => api.get('/analytics/charts/suppliers-by-country'),
};

// Intelligence APIs (AI risk events, predictions, recommendations)
export const intelligenceAPI = {
  recentEvents: (limit = 10) => api.get(`/intelligence/events/recent?limit=${limit}`),
  activeRecommendations: (limit = 10) => api.get(`/intelligence/recommendations/active?limit=${limit}`),
  topPredictions: (limit = 10) => api.get(`/intelligence/predictions/top?limit=${limit}`),
};

// Notification APIs
export const notificationAPI = {
  getByUser: (userId) => api.get(`/notifications/user/${userId}`),
  getUnread: (userId) => api.get(`/notifications/user/${userId}/unread`),
  getRecent: (userId, limit = 10) => api.get(`/notifications/user/${userId}/recent?limit=${limit}`),
  markAsRead: (id) => api.patch(`/notifications/${id}/read`),
  markAllAsRead: (userId) => api.patch(`/notifications/user/${userId}/read-all`),
  delete: (id) => api.delete(`/notifications/${id}`),
  getUnreadCount: (userId) => api.get(`/notifications/user/${userId}/unread-count`),
};

// User APIs
export const userAPI = {
  getAll: () => api.get('/users'),
  getMe: () => api.get('/users/me'),
  getById: (id) => api.get(`/users/${id}`),
  update: (id, data) => api.put(`/users/${id}`, data),
  deactivate: (id) => api.delete(`/users/${id}`),
};

export default api;
