import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'

export default function DashboardPage() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const cards = [
    { title: 'Buildings', description: 'Manage buildings and units', path: '/buildings', color: 'bg-blue-500' },
    { title: 'Import Readings', description: 'Upload CSV meter readings', path: '/import', color: 'bg-green-500' },
    { title: 'Billing', description: 'Create and generate bills', path: '/billing', color: 'bg-purple-500' },
    { title: 'Payments', description: 'Record tenant payments', path: '/payments', color: 'bg-orange-500' },
  ]

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow px-6 py-4 flex justify-between items-center">
        <h1 className="text-xl font-bold text-blue-700">Utility Billing Platform</h1>
        <div className="flex items-center gap-4">
          <span className="text-sm text-gray-600">
            {user?.username}{' '}
            <span className="bg-blue-100 text-blue-700 px-2 py-0.5 rounded text-xs font-medium">
              {user?.role}
            </span>
          </span>
          <button onClick={handleLogout} className="text-sm text-red-600 hover:underline">
            Logout
          </button>
        </div>
      </nav>

      <div className="max-w-5xl mx-auto p-6">
        <h2 className="text-2xl font-bold text-gray-800 mb-6">Dashboard</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
          {cards.map((card) => (
            <div
              key={card.path}
              onClick={() => navigate(card.path)}
              className="bg-white rounded-xl shadow p-6 cursor-pointer hover:shadow-md transition border-l-4 border-blue-500"
            >
              <div className={`w-10 h-10 ${card.color} rounded-full mb-3`} />
              <h3 className="text-lg font-semibold text-gray-800">{card.title}</h3>
              <p className="text-gray-500 text-sm mt-1">{card.description}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}