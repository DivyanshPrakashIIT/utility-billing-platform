import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import apiClient from '../api/client'

export default function PaymentsPage() {
  const navigate = useNavigate()
  const [unitBillId, setUnitBillId] = useState('')
  const [payments, setPayments] = useState([])
  const [bill, setBill] = useState(null)
  const [form, setForm] = useState({ amount: '', method: 'UPI' })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const fetchBillAndPayments = async () => {
    if (!unitBillId) return
    setError('')
    try {
      const [billRes, payRes] = await Promise.all([
        apiClient.get(`/units/1/bills`),
        apiClient.get(`/unit-bills/${unitBillId}/payments`),
      ])
      const found = billRes.data.find(b => b.id === Number(unitBillId))
      setBill(found || null)
      setPayments(payRes.data)
    } catch {
      setError('Could not load bill details')
    }
  }

  const handlePayment = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    setSuccess('')
    try {
      await apiClient.post(`/unit-bills/${unitBillId}/payments`, {
        amount: Number(form.amount),
        method: form.method,
      })
      setSuccess('Payment recorded successfully!')
      setForm({ amount: '', method: 'UPI' })
      fetchBillAndPayments()
    } catch (err) {
      setError(err.response?.data || 'Payment failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow px-6 py-4 flex justify-between items-center">
        <h1 className="text-xl font-bold text-blue-700">Utility Billing Platform</h1>
        <button onClick={() => navigate('/dashboard')} className="text-sm text-blue-600 hover:underline">
          ← Dashboard
        </button>
      </nav>

      <div className="max-w-3xl mx-auto p-6 space-y-6">
        <h2 className="text-2xl font-bold text-gray-800">Record Payment</h2>

        <div className="bg-white rounded-lg shadow p-6">
          <label className="block text-sm font-medium text-gray-700 mb-1">Enter Unit Bill ID</label>
          <div className="flex gap-3">
            <input
              type="number"
              value={unitBillId}
              onChange={(e) => setUnitBillId(e.target.value)}
              className="border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="e.g. 1"
            />
            <button
              onClick={fetchBillAndPayments}
              className="bg-gray-700 text-white px-4 py-2 rounded hover:bg-gray-800 transition text-sm"
            >
              Load Bill
            </button>
          </div>
        </div>

        {error && <div className="bg-red-100 text-red-700 px-4 py-2 rounded text-sm">{error}</div>}
        {success && <div className="bg-green-100 text-green-700 px-4 py-2 rounded text-sm">{success}</div>}

        {bill && (
          <div className="bg-white rounded-lg shadow p-6 space-y-4">
            <div className="grid grid-cols-3 gap-4 text-sm">
              <div><span className="text-gray-500">Amount Due:</span> <span className="font-semibold">${bill.amountDue}</span></div>
              <div><span className="text-gray-500">Amount Paid:</span> <span className="font-semibold text-green-600">${bill.amountPaid}</span></div>
              <div>
                <span className="text-gray-500">Status:</span>{' '}
                <span className={`font-semibold ${bill.status === 'PAID' ? 'text-green-600' : bill.status === 'PARTIAL' ? 'text-orange-600' : 'text-red-600'}`}>
                  {bill.status}
                </span>
              </div>
            </div>

            {bill.status !== 'PAID' && (
              <form onSubmit={handlePayment} className="flex gap-4 items-end">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Amount</label>
                  <input
                    type="number" step="0.01" value={form.amount}
                    onChange={(e) => setForm({ ...form, amount: e.target.value })}
                    className="border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Method</label>
                  <select value={form.method}
                    onChange={(e) => setForm({ ...form, method: e.target.value })}
                    className="border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500">
                    {['CASH', 'BANK', 'UPI', 'CARD', 'OTHER'].map(m => (
                      <option key={m}>{m}</option>
                    ))}
                  </select>
                </div>
                <button type="submit" disabled={loading}
                  className="bg-orange-500 text-white px-6 py-2 rounded hover:bg-orange-600 transition disabled:opacity-50">
                  {loading ? 'Saving...' : 'Record Payment'}
                </button>
              </form>
            )}

            {payments.length > 0 && (
              <div>
                <h4 className="font-medium text-gray-700 mb-2">Payment History</h4>
                <div className="space-y-2">
                  {payments.map((p) => (
                    <div key={p.id} className="bg-gray-50 border rounded px-4 py-2 text-sm flex justify-between">
                      <span className="font-medium">${p.amount}</span>
                      <span className="text-gray-500">{p.method}</span>
                      <span className="text-gray-400">{new Date(p.paidAt).toLocaleDateString()}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  )
}