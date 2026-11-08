import { Navigate, Route, Routes } from 'react-router-dom'
import { AppShell } from './layouts/AppShell'
import { LoginPage } from './pages/auth/LoginPage'
import { DashboardPage } from './pages/dashboard/DashboardPage'
import { ApartmentsPage } from './pages/apartments/ApartmentsPage'
import { ResidentsPage } from './pages/residents/ResidentsPage'
import { ResidencyPage } from './pages/residency/ResidencyPage'
import { FeesPage } from './pages/fees/FeesPage'
import { PaymentsPage } from './pages/payments/PaymentsPage'
import { ReportsPage } from './pages/reports/ReportsPage'
import { NoticesPage } from './pages/notices/NoticesPage'
import { IssuesPage } from './pages/issues/IssuesPage'

export const App = () => (
  <Routes>
    <Route path="/login" element={<LoginPage />} />
    <Route element={<AppShell />}>
      <Route path="/dashboard" element={<DashboardPage />} />
      <Route path="/apartments" element={<ApartmentsPage />} />
      <Route path="/residents" element={<ResidentsPage />} />
      <Route path="/residency" element={<ResidencyPage />} />
      <Route path="/fees" element={<FeesPage />} />
      <Route path="/payments" element={<PaymentsPage />} />
      <Route path="/reports" element={<ReportsPage />} />
      <Route path="/notices" element={<NoticesPage />} />
      <Route path="/issues" element={<IssuesPage />} />
    </Route>
    <Route path="*" element={<Navigate to="/login" replace />} />
  </Routes>
)

export default App;
