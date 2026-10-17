import React from 'react';
import { useHealthCheck } from '../hooks';

export const HomePage: React.FC = () => {
  const { status, loading, error, refetch } = useHealthCheck();

  return (
    <main className="home-container">
      <section className="hero-card">
        <h1>BlueMoon Apartment Management</h1>
        <p className="subtitle">
          Khung dự án web quản lý chung cư (Base Scaffold) sẵn sàng để phát triển.
        </p>

        <div className="status-grid">
          <div className="status-card">
            <div className="status-title">Frontend</div>
            <div className="status-value status-up">ReactJS + TypeScript + Vite</div>
            <p className="status-desc">Cấu hình sẵn components, pages, services, hooks, types.</p>
          </div>

          <div className="status-card">
            <div className="status-title">Backend API</div>
            {loading ? (
              <div className="status-value status-pending">Checking connection...</div>
            ) : error ? (
              <div className="status-value status-down">Offline / Disconnected</div>
            ) : (
              <div className="status-value status-up">
                Connected ({status?.service || 'UP'})
              </div>
            )}
            <p className="status-desc">
              {error ? `Gợi ý: Khởi động backend trên cổng 8080.` : `Trạng thái: ${status?.status || 'Online'}`}
            </p>
          </div>

          <div className="status-card">
            <div className="status-title">Database</div>
            <div className="status-value status-neutral">MySQL 8.x</div>
            <p className="status-desc">Cấu hình qua biến môi trường (.env / application.properties).</p>
          </div>
        </div>

        <div className="action-row">
          <button className="btn-action" onClick={refetch} disabled={loading}>
            {loading ? 'Đang kiểm tra...' : 'Kiểm tra kết nối Backend'}
          </button>
        </div>
      </section>

      <section className="info-card">
        <h2>Cấu trúc thư mục dự án</h2>
        <div className="structure-overview">
          <div className="structure-col">
            <h3>Frontend (frontend/)</h3>
            <ul>
              <li><code>src/components/</code>: Reusable UI components</li>
              <li><code>src/pages/</code>: Màn hình giao diện</li>
              <li><code>src/services/</code>: Gọi API backend qua fetch/axios</li>
              <li><code>src/hooks/</code>: Custom React hooks</li>
              <li><code>src/types/</code>: TypeScript interfaces & types</li>
            </ul>
          </div>
          <div className="structure-col">
            <h3>Backend (backend/)</h3>
            <ul>
              <li><code>controller/</code>: REST Endpoints tiếp nhận request</li>
              <li><code>service/</code>: Business logic layer</li>
              <li><code>repository/</code>: Spring Data JPA interfaces</li>
              <li><code>entity/</code>: JPA Entity mapping database</li>
              <li><code>dto/</code>: Request/Response DTOs</li>
              <li><code>config/</code>: CORS & cấu hình hệ thống</li>
            </ul>
          </div>
        </div>
      </section>
    </main>
  );
};
