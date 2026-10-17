import React from 'react';

export const Footer: React.FC = () => {
  return (
    <footer className="app-footer">
      <p>© {new Date().getFullYear()} BlueMoon Apartment Management Project. All rights reserved.</p>
    </footer>
  );
};
