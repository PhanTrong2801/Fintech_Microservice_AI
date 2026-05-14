import axios from 'axios';

// Khởi tạo Axios instance
// Cấu hình Vite proxy chuyển /api sang localhost:8080 (Gateway)
export const api = axios.create({
  baseURL: '/api',
  timeout: 60000, // Timeout dài cho AI generate (60s)
});

// Thêm Interceptor để nhét JWT Token vào Header của mọi Request
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Xử lý lỗi global (ví dụ 401 Unauthorized -> logout)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('email');
      // Tùy chọn: redirect về login
      window.dispatchEvent(new Event('unauthorized'));
    }
    return Promise.reject(error);
  }
);
