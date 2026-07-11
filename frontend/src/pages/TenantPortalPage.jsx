import { useState } from 'react'
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

  const handleLogout = () => { logout(); navigate('/login') }

  const fetchBillHistory = async () => {
    if (!unitId) return
    setLoading(true)
    setError('')
    try {
      const res = await apiClient.get(`/tenant-portal/units/${unitId}/bill-history`)
      setBillHistory(res.data)
    } catch {
      setError('Could not load bill history. Check your unit ID.')
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

        <div className="bg-white rounded-xl shadow p-6">
          <label className="block text-sm font-medium text-gray-700 mb-2">Your Unit ID</label>
          <div className="flex gap-3">
            <input type="number" value={unitId} onChange={(e) => setUnitId(e.target.value)}
              className="border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 w-40"
              placeholder="e.g. 1" />
            <button onClick={fetchBillHistory} disabled={loading}
              className="bg-blue-600 text-white px-5 py-2 rounded-lg hover:bg-blue-700 transition text-sm disabled:opacity-50">
              {loading ? 'Loading...' : 'View Bills'}
            </button>
          </div>
        </div>

        {error && <div className="bg-red-50 border border-red-300 text-red-700 px-4 py-3 rounded-lg text-sm">{error}</div>}

        {billHistory.map((entry, i) => (
          <div key={i} className="bg-white rounded-xl shadow p-6">
            <div className="grid grid-cols-3 gap-4 mb-4">
              <div className="bg-gray-50 rounded-lg p-3 text-center">
                <div className="font-bold text-gray-700">₹{entry.bill.amountDue}</div>
                <div className="text-xs text-gray-500">Amount Due</div>
              </div>
              <div className="bg-green-50 rounded-lg p-3 text-center">
                <div className="font-bold text-green-600">₹{entry.bill.amountPaid}</div>
                <div className="text-xs text-gray-500">Amount Paid</div>
              </div>
              <div className="rounded-lg p-3 text-center border">
                <div className={`font-bold ${entry.bill.status === 'PAID' ? 'text-green-600' : entry.bill.status === 'PARTIAL' ? 'text-orange-500' : 'text-red-500'}`}>
                  {entry.bill.status}
                </div>
                <div className="text-xs text-gray-500">Status</div>
              </div>
            </div>
            {entry.payments?.length > 0 && (
              <div>
                <h4 className="text-sm font-medium text-gray-700 mb-2">Payments</h4>
                {entry.payments.map((p) => (
                  <div key={p.id} className="text-xs bg-gray-50 border rounded-lg px-3 py-2 flex justify-between mb-1">
                    <span className="font-medium">₹{p.amount}</span>
                    <span className="text-gray-500">{p.method}</span>
                    <span className="text-gray-400">{new Date(p.paidAt).toLocaleDateString()}</span>
                  </div>
                ))}
              </div>
            )}
          </div>
        ))}

        {billHistory.length === 0 && unitId && !loading && !error && (
          <div className="bg-white rounded-xl shadow p-8 text-center text-gray-400">
            No bills found for Unit ID {unitId}.
          </div>
        )}
      </div>
    </div>
  )
}