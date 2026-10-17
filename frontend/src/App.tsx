import React from 'react';
import { Navbar, Footer } from './components';
import { HomePage } from './pages';
import './App.css';

export const App: React.FC = () => {
  return (
    <div className="app-layout">
      <Navbar />
      <HomePage />
      <Footer />
    </div>
  );
};

export default App;
