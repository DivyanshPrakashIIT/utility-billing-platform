import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import apiClient from '../api/client'

export default function ImportPage() {
  const [file, setFile] = useState(null)
  const [result, setResult] = useState(null)
  const [errors, setErrors] = useState([])
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const handleUpload = async () => {
    if (!file) return
    setLoading(true)
    setResult(null)
    setErrors([])

    const formData = new FormData()
    formData.append('file', file)

    try {
      const res = await apiClient.post('/import/batches', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
      setResult(res.data)

      if (res.data.failedRows > 0) {
        const errRes = await apiClient.get(`/import/batches/${res.data.id}/errors`)
        setErrors(errRes.data)
      }
    } catch {
      setResult({ status: 'FAILED', errorMessage: 'Upload failed' })
    } finally {
      setLoading(false)
    }
  }

  const statusColor = {
    COMPLETED: 'text-green-600',
    PARTIALLY_FAILED: 'text-orange-600',
    FAILED: 'text-red-600',
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow px-6 py-4 flex justify-between items-center">
        <h1 className="text-xl font-bold text-blue-700">Utility Billing Platform</h1>
        <button onClick={() => navigate('/dashboard')} className="text-sm text-blue-600 hover:underline">
          ← Dashboard
        </button>
      </nav>

      <div className="max-w-3xl mx-auto p-6">
        <h2 className="text-2xl font-bold text-gray-800 mb-6">Import Meter Readings</h2>

        <div className="bg-white rounded-lg shadow p-6 mb-6">
          <p className="text-sm text-gray-500 mb-4">
            Upload a CSV file with columns: <code className="bg-gray-100 px-1 rounded">meter_type, meter_id, reading_date, reading_value</code>
          </p>
          <div className="flex gap-4 items-center">
            <input
              type="file"
              accept=".csv"
              onChange={(e) => setFile(e.target.files[0])}
              className="text-sm text-gray-600"
            />
            <button
              onClick={handleUpload}
              disabled={!file || loading}
              className="bg-green-600 text-white px-6 py-2 rounded hover:bg-green-700 transition disabled:opacity-50 text-sm"
            >
              {loading ? 'Uploading...' : 'Upload CSV'}
            </button>
          </div>
        </div>

        {result && (
          <div className="bg-white rounded-lg shadow p-6 mb-6">
            <h3 className="text-lg font-semibold mb-3">Import Result</h3>
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <span className="text-gray-500">Status:</span>{' '}
                <span className={`font-semibold ${statusColor[result.status] || 'text-gray-700'}`}>
                  {result.status}
                </span>
              </div>
              <div><span className="text-gray-500">Total Rows:</span> {result.totalRows}</div>
              <div><span className="text-gray-500">Success:</span> <span className="text-green-600 font-medium">{result.successRows}</span></div>
              <div><span className="text-gray-500">Failed:</span> <span className="text-red-600 font-medium">{result.failedRows}</span></div>
            </div>
          </div>
        )}

        {errors.length > 0 && (
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold mb-3 text-red-600">Errors</h3>
            <div className="space-y-2">
              {errors.map((err) => (
                <div key={err.id} className="bg-red-50 border border-red-200 rounded px-4 py-2 text-sm">
                  <span className="font-medium">Row {err.rowNumber}</span>
                  {err.fieldName && <span className="text-gray-500"> ({err.fieldName})</span>}
                  : {err.errorMessage}
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  )
}