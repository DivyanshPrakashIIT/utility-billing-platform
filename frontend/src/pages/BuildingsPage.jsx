import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import apiClient from '../api/client'

export default function BuildingsPage() {
  const [buildings, setBuildings] = useState([])
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({ name: '', address: '', currencyCode: 'USD' })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const navigate = useNavigate()

  useEffect(() => { fetchBuildings() }, [])

  const fetchBuildings = async () => {
    try {
      const res = await apiClient.get('/buildings')
      setBuildings(res.data)
    } catch {
      setError('Failed to load buildings')
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      await apiClient.post('/buildings', form)
      setForm({ name: '', address: '', currencyCode: 'USD' })
      setShowForm(false)
      fetchBuildings()
    } catch {
      setError('Failed to create building')
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

      <div className="max-w-4xl mx-auto p-6">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-800">Buildings</h2>
          <button
            onClick={() => setShowForm(!showForm)}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition text-sm"
          >
            {showForm ? 'Cancel' : '+ Add Building'}
          </button>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-300 text-red-700 px-4 py-2 rounded mb-4 text-sm">
            {error}
          </div>
        )}

        {showForm && (
          <div className="bg-white rounded-xl shadow p-6 mb-6">
            <h3 className="text-lg font-semibold mb-4">New Building</h3>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Building Name *</label>
                <input
                  type="text"
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Address</label>
                <input
                  type="text"
                  value={form.address}
                  onChange={(e) => setForm({ ...form, address: e.target.value })}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Currency Code</label>
                <input
                  type="text"
                  value={form.currencyCode}
                  onChange={(e) => setForm({ ...form, currencyCode: e.target.value })}
                  maxLength={3}
                  className="w-32 border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <button
                type="submit"
                disabled={loading}
                className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition disabled:opacity-50"
              >
                {loading ? 'Saving...' : 'Save Building'}
              </button>
            </form>
          </div>
        )}

        <div className="space-y-4">
          {buildings.length === 0 ? (
            <div className="bg-white rounded-xl shadow p-8 text-center text-gray-400">
              No buildings yet. Click "+ Add Building" to create one.
            </div>
          ) : (
            buildings.map((b) => (
              <div key={b.id} className="bg-white rounded-xl shadow p-5 flex justify-between items-center">
                <div>
                  <h3 className="text-lg font-semibold text-gray-800">{b.name}</h3>
                  <p className="text-gray-500 text-sm">{b.address || 'No address'}</p>
                  <span className="text-xs bg-gray-100 text-gray-600 px-2 py-0.5 rounded mt-1 inline-block">
                    {b.currencyCode}
                  </span>
                </div>
                <span className="text-sm text-gray-400">ID: {b.id}</span>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  )
}