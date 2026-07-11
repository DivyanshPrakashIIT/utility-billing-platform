import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import LoginPage from './pages/LoginPage'
import DashboardPage from './pages/DashboardPage'
import BuildingsPage from './pages/BuildingsPage'
import ImportPage from './pages/ImportPage'
import BillingPage from './pages/BillingPage'
import PaymentsPage from './pages/PaymentsPage'
import TenantPortalPage from './pages/TenantPortalPage'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={<Navigate to="/login" replace />} />

          <Route path="/dashboard" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'OWNER']}>
              <DashboardPage />
            </ProtectedRoute>
          } />

          <Route path="/buildings" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'OWNER']}>
              <BuildingsPage />
            </ProtectedRoute>
          } />

          <Route path="/import" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'OWNER']}>
              <ImportPage />
            </ProtectedRoute>
          } />

          <Route path="/billing" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'OWNER']}>
              <BillingPage />
            </ProtectedRoute>
          } />

          <Route path="/payments" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'OWNER']}>
              <PaymentsPage />
            </ProtectedRoute>
          } />

          <Route path="/tenant-portal" element={
            <ProtectedRoute allowedRoles={['TENANT', 'OWNER', 'ADMIN']}>
              <TenantPortalPage />
            </ProtectedRoute>
          } />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}