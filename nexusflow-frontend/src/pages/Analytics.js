import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { analyticsAPI } from '../services/api';
import { BarChart, Bar, LineChart, Line, PieChart, Pie, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, Cell } from 'recharts';

function Analytics() {
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const res = await analyticsAPI.getDashboard();
      setAnalytics(res.data);
    } catch (error) {
      console.error('Error fetching analytics data:', error);
    } finally {
      setLoading(false);
    }
  };

  const COLORS = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6'];

  if (loading) {
    return <div className="min-h-screen bg-gray-100 flex items-center justify-center"><div className="text-xl">Loading...</div></div>;
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <header className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
          <div className="flex items-center space-x-4">
            <h1 className="text-2xl font-bold text-primary-600">NexusFlow</h1>
            <nav className="hidden md:flex space-x-6">
              <Link to="/dashboard" className="text-gray-600 hover:text-primary-600">Dashboard</Link>
              <Link to="/shipments" className="text-gray-600 hover:text-primary-600">Shipments</Link>
              <Link to="/suppliers" className="text-gray-600 hover:text-primary-600">Suppliers</Link>
              <Link to="/risk" className="text-gray-600 hover:text-primary-600">Risk</Link>
              <Link to="/analytics" className="text-primary-600 font-medium">Analytics</Link>
            </nav>
          </div>
          <Link to="/dashboard" className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700">Back to Dashboard</Link>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-8">
        <h2 className="text-2xl font-bold text-gray-900 mb-6">Analytics Dashboard</h2>

        {/* Overview Stats */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
          <div className="bg-white rounded-xl shadow-sm p-6">
            <div className="text-sm text-gray-500 mb-1">Total Shipments</div>
            <div className="text-3xl font-bold text-gray-900">{analytics?.totalShipments || 0}</div>
          </div>
          <div className="bg-white rounded-xl shadow-sm p-6">
            <div className="text-sm text-gray-500 mb-1">Total Suppliers</div>
            <div className="text-3xl font-bold text-gray-900">{analytics?.totalSuppliers || 0}</div>
          </div>
          <div className="bg-white rounded-xl shadow-sm p-6">
            <div className="text-sm text-gray-500 mb-1">Avg Supplier Reliability</div>
            <div className="text-3xl font-bold text-primary-600">{analytics?.averageReliabilityScore?.toFixed(1) || '0.0'}/5.0</div>
          </div>
          <div className="bg-white rounded-xl shadow-sm p-6">
            <div className="text-sm text-gray-500 mb-1">Predicted Delays</div>
            <div className="text-3xl font-bold text-yellow-600">{analytics?.predictedDelays || 0}</div>
          </div>
        </div>

        {/* Charts Row 1 */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          {/* Shipment Status Distribution */}
          <div className="bg-white rounded-xl shadow-sm p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Shipment Status Distribution</h3>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={analytics?.shipmentByStatus || []}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="status" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="count" fill="#3B82F6" name="Shipments" />
              </BarChart>
            </ResponsiveContainer>
          </div>

          {/* Risk Level Distribution */}
          <div className="bg-white rounded-xl shadow-sm p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Risk Level Distribution</h3>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={analytics?.riskByLevel || []}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ level, count }) => `${level}: ${count}`}
                  outerRadius={100}
                  dataKey="count"
                >
                  {(analytics?.riskByLevel || []).map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Charts Row 2 */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          {/* Monthly Shipments */}
          <div className="bg-white rounded-xl shadow-sm p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Monthly Shipment Trends</h3>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={analytics?.shipmentByMonth || []}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="count" stroke="#3B82F6" name="Shipments" strokeWidth={2} />
              </LineChart>
            </ResponsiveContainer>
          </div>

          {/* Suppliers by Country */}
          <div className="bg-white rounded-xl shadow-sm p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Suppliers by Country</h3>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={analytics?.suppliersByCountry || []} layout="vertical">
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis type="number" />
                <YAxis dataKey="country" type="category" width={100} />
                <Tooltip />
                <Bar dataKey="count" fill="#10B981" name="Suppliers" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Detailed Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="bg-white rounded-xl shadow-sm p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Shipment Breakdown</h3>
            <div className="space-y-3">
              <div className="flex justify-between">
                <span className="text-gray-600">Pending</span>
                <span className="font-medium">{analytics?.pendingShipments || 0}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">In Transit</span>
                <span className="font-medium">{analytics?.inTransitShipments || 0}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Delayed</span>
                <span className="font-medium text-red-600">{analytics?.delayedShipments || 0}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Delivered</span>
                <span className="font-medium text-green-600">{analytics?.deliveredShipments || 0}</span>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-xl shadow-sm p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Risk Summary</h3>
            <div className="space-y-3">
              <div className="flex justify-between">
                <span className="text-gray-600">High Risk</span>
                <span className="font-medium text-orange-600">{analytics?.highRiskShipments || 0}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Critical Risk</span>
                <span className="font-medium text-red-600">{analytics?.criticalRiskShipments || 0}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Avg Risk Score</span>
                <span className="font-medium">{analytics?.averageRiskScore?.toFixed(1) || '0.0'}</span>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-xl shadow-sm p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Delay Predictions</h3>
            <div className="space-y-3">
              <div className="flex justify-between">
                <span className="text-gray-600">Predicted Delays</span>
                <span className="font-medium text-yellow-600">{analytics?.predictedDelays || 0}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Avg Delay Hours</span>
                <span className="font-medium">{analytics?.averagePredictedDelayHours?.toFixed(1) || '0.0'}h</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Confidence Score</span>
                <span className="font-medium text-green-600">{analytics?.averageConfidenceScore?.toFixed(1) || '0.0'}%</span>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}

export default Analytics;