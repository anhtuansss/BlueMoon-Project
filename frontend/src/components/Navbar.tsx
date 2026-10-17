import React from 'react';

export const Navbar: React.FC = () => {
  return (
    <header className="app-header">
      <div className="brand">
        <span className="brand-logo">🏢</span>
        <span className="brand-name">BlueMoon</span>
        <span className="brand-badge">Apartment Management</span>
      </div>
      <div className="header-meta">
        <span className="version-tag">v0.1.0-alpha</span>
      </div>
    </header>
  );
};
