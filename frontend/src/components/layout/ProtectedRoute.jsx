import React from 'react';
import { Navigate } from 'react-router-dom';
import { authService } from '../../services/authService';

/**
 * Thành phần bảo vệ route theo phiên đăng nhập và vai trò người dùng (Thành viên 1 - Nguyên - F01).
 * @param {object} props
 * @param {React.ReactNode} props.children
 * @param {string[]} [props.allowedRoles] - Danh sách vai trò được phép (BQT, NV, CU_DAN). Nếu không truyền thì chỉ cần đăng nhập.
 */
export function ProtectedRoute({ children, allowedRoles }) {
  const isAuth = authService.isAuthenticated();

  if (!isAuth) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && allowedRoles.length > 0) {
    const currentRole = authService.getUserRole();
    if (!currentRole || !allowedRoles.includes(currentRole)) {
      return (
        <div className="flex flex-col items-center justify-center min-h-[60vh] text-center p-6">
          <h2 className="text-2xl font-bold text-red-600 mb-2">403 - Không có quyền truy cập</h2>
          <p className="text-gray-600 mb-4">Tài khoản của bạn ({currentRole || 'Khách'}) không có quyền truy cập chức năng này.</p>
          <a href="/" className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700">Về trang chủ</a>
        </div>
      );
    }
  }

  return children;
}

export default ProtectedRoute;
