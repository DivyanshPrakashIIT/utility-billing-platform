import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import apiClient from '../api/client'

export default function BillingPage() {
  const navigate = useNavigate()
  const [billForm, setBillForm] = useState({
    masterMeterId: '',
    billingPeriodStart: '',
    billingPeriodEnd: '',
    totalAmount: '',
  })
  const [generateForm, setGenerateForm] = useState({
    providerBillId: '',
    buildingId: '',
    allocationRuleId: '',
  })
  const [createdBill, setCreatedBill] = useState(null)
  const [generatedBills, setGeneratedBills] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleCreateBill = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      const res = await apiClient.post('/provider-bills', {
        masterMeterId: Number(billForm.masterMeterId),
        billingPeriodStart: billForm.billingPeriodStart,
        billingPeriodEnd: billForm.billingPeriodEnd,
        totalAmount: Number(billForm.totalAmount),
      })
      setCreatedBill(res.data)
    } catch {
      setError('Failed to create provider bill')
    } finally {
      setLoading(false)
    }
  }

  const handleGenerateBills = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      const res = await apiClient.post(
        `/provider-bills/${generateForm.providerBillId}/generate`,
        {
          buildingId: Number(generateForm.buildingId),
          allocationRuleId: Number(generateForm.allocationRuleId),
        }
      )
      setGeneratedBills(res.data)
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to generate bills')
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

      <div className="max-w-4xl mx-auto p-6 space-y-6">
        <h2 className="text-2xl font-bold text-gray-800">Billing</h2>

        {error && <div className="bg-red-100 text-red-700 px-4 py-2 rounded text-sm">{error}</div>}

        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold mb-4">Step 1 — Record Provider Bill</h3>
          <form onSubmit={handleCreateBill} className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Master Meter ID</label>
              <input type="number" value={billForm.masterMeterId}
                onChange={(e) => setBillForm({ ...billForm, masterMeterId: e.target.value })}
                className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" required />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Total Amount</label>
              <input type="number" step="0.01" value={billForm.totalAmount}
                onChange={(e) => setBillForm({ ...billForm, totalAmount: e.target.value })}
                className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" required />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Period Start</label>
              <input type="date" value={billForm.billingPeriodStart}
                onChange={(e) => setBillForm({ ...billForm, billingPeriodStart: e.target.value })}
                className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" required />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Period End</label>
              <input type="date" value={billForm.billingPeriodEnd}
                onChange={(e) => setBillForm({ ...billForm, billingPeriodEnd: e.target.value })}
                className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" required />
            </div>
            <div className="col-span-2">
              <button type="submit" disabled={loading}
                className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700 transition disabled:opacity-50">
                {loading ? 'Saving...' : 'Create Provider Bill'}
              </button>
            </div>
          </form>

          {createdBill && (
            <div className="mt-4 bg-green-50 border border-green-200 rounded px-4 py-3 text-sm">
              ✅ Provider Bill created — ID: <strong>{createdBill.id}</strong>, Status: <strong>{createdBill.status}</strong>
            </div>
          )}
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold mb-4">Step 2 — Generate Unit Bills</h3>
          <form onSubmit={handleGenerateBills} className="grid grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Provider Bill ID</label>
              <input type="number" value={generateForm.providerBillId}
                onChange={(e) => setGenerateForm({ ...generateForm, providerBillId: e.target.value })}
                className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" required />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Building ID</label>
              <input type="number" value={generateForm.buildingId}
                onChange={(e) => setGenerateForm({ ...generateForm, buildingId: e.target.value })}
                className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" required />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Allocation Rule ID</label>
              <input type="number" value={generateForm.allocationRuleId}
                onChange={(e) => setGenerateForm({ ...generateForm, allocationRuleId: e.target.value })}
                className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" required />
            </div>
            <div className="col-span-3">
              <button type="submit" disabled={loading}
                className="bg-purple-600 text-white px-6 py-2 rounded hover:bg-purple-700 transition disabled:opacity-50">
                {loading ? 'Generating...' : 'Generate Unit Bills'}
              </button>
            </div>
          </form>

          {generatedBills.length > 0 && (
            <div className="mt-4">
              <h4 className="font-medium text-gray-700 mb-2">Generated Bills</h4>
              <div className="space-y-2">
                {generatedBills.map((bill) => (
                  <div key={bill.id} className="bg-gray-50 border rounded px-4 py-2 text-sm flex justify-between">
                    <span>Unit ID: {bill.unitId}</span>
                    <span className="font-semibold">Amount Due: ${bill.amountDue}</span>
                    <span className={bill.status === 'PAID' ? 'text-green-600' : 'text-orange-600'}>{bill.status}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}