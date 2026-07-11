import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import apiClient from '../api/client'

export default function TenantPortalPage() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [unitId, setUnitId] = useState('')
  const [billHistory, setBillHistory] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const fetchBillHistory = async () => {
    if (!unitId) return
    setLoading(true)
    setError('')
    try {
      const res = await apiClient.get(`/tenant-portal/units/${unitId}/bill-history`)
      setBillHistory(res.data)
    } catch {
      setError('Could not load bill history')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow px-6 py-4 flex justify-between items-center">
        <h1 className="text-xl font-bold text-blue-700">Tenant Portal</h1>
        <div className="flex items-center gap-4">
          <span className="text-sm text-gray-600">{user?.username}</span>
          <button onClick={handleLogout} className="text-sm text-red-600 hover:underline">Logout</button>
        </div>
      </nav>

      <div className="max-w-3xl mx-auto p-6 space-y-6">
        <h2 className="text-2xl font-bold text-gray-800">My Bill History</h2>

        <div className="bg-white rounded-lg shadow p-6">
          <label className="block text-sm font-medium text-gray-700 mb-1">Your Unit ID</label>
          <div className="flex gap-3">
            <input
              type="number"
              value={unitId}
              onChange={(e) => setUnitId(e.target.value)}
              className="border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="e.g. 1"
            />
            <button
              onClick={fetchBillHistory}
              disabled={loading}
              className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition text-sm disabled:opacity-50"
            >
              {loading ? 'Loading...' : 'View Bills'}
            </button>
          </div>
        </div>

        {error && <div className="bg-red-100 text-red-700 px-4 py-2 rounded text-sm">{error}</div>}

        {billHistory.map((entry, i) => (
          <div key={i} className="bg-white rounded-lg shadow p-6">
            <div className="grid grid-cols-3 gap-4 text-sm mb-4">
              <div><span className="text-gray-500">Amount Due:</span> <span className="font-semibold">${entry.bill.amountDue}</span></div>
              <div><span className="text-gray-500">Amount Paid:</span> <span className="font-semibold text-green-600">${entry.bill.amountPaid}</span></div>
              <div>
                <span className="text-gray-500">Status:</span>{' '}
                <span className={`font-semibold ${entry.bill.status === 'PAID' ? 'text-green-600' : entry.bill.status === 'PARTIAL' ? 'text-orange-600' : 'text-red-600'}`}>
                  {entry.bill.status}
                </span>
              </div>
            </div>

            {entry.payments?.length > 0 && (
              <div>
                <h4 className="text-sm font-medium text-gray-700 mb-2">Payments</h4>
                {entry.payments.map((p) => (
                  <div key={p.id} className="text-xs bg-gray-50 border rounded px-3 py-1.5 flex justify-between mb-1">
                    <span>${p.amount}</span>
                    <span className="text-gray-500">{p.method}</span>
                    <span className="text-gray-400">{new Date(p.paidAt).toLocaleDateString()}</span>
                  </div>
                ))}
              </div>
            )}
          </div>
        ))}

        {billHistory.length === 0 && unitId && !loading && !error && (
          <div className="bg-white rounded-lg shadow p-6 text-center text-gray-400">
            No bills found for Unit ID {unitId}.
          </div>
        )}
      </div>
    </div>
  )
}