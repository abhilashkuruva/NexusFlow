import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { riskAPI, shipmentAPI } from '../services/api';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';

function Risk() {
  const [riskStats, setRiskStats] = useState(null);
  const [highRiskShipments, setHighRiskShipments] = useState([]);
  const [predictedDelays, setPredictedDelays] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [statsRes, highRiskRes, delaysRes, distributionRes] = await Promise.all([
        riskAPI.getStats(),
        riskAPI.getHighRisk(),
        riskAPI.getPredictedDelays(),
        riskAPI.getDistribution()
      ]);
      setRiskStats(statsRes.data);
      setHighRiskShipments(highRiskRes.data);
      setPredictedDelays(delaysRes.data);
    } catch (error) {
      console.error('Error fetching risk data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCalculateAll = async () => {
    try {
      await riskAPI.calculateAll();
      fetchData();
      alert('Risk calculations completed!');
    } catch (error) {
      console.error('Error calculating risks:', error);
    }
  };

  const getRiskColor = (level) => {
    const colors = {
      LOW: 'bg-green-100 text-green-800',
      MEDIUM: 'bg-yellow-100 text-yellow-800',
      HIGH: 'bg-orange-100 text-orange-800',
      CRITICAL: 'bg-red-100 text-red-800'
    };
    return colors[level] || 'bg-gray-100 text-gray-800';
  };

  const COLORS = ['#10B981', '#F59E0B', '#F97316', '#EF4444'];

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
              <Link to="/risk" className="text-primary-600 font-medium">Risk</Link>
              <Link to="/analytics" className="text-gray-600 hover:text-primary-600">Analytics</Link>
            </nav>
          </div>
          <Link to="/dashboard" className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700">Back to Dashboard</Link>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-8">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-900">Risk Analysis Dashboard</h2>
          <button onClick={handleCalculateAll} className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700">
            Calculate All Risks
          </button>
        </div>

        {/* Risk Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
          <div className="bg-white rounded-xl shadow-sm p-6">
            <div className="text-sm text-gray-500 mb-1">Low Risk</div>
            <div className="text-3xl font-bold text-green-600">{riskStats?.lowRisk || 0}</div>
          </div>
          <div className="bg-white rounded-xl shadow-sm p-6">
            <div className="text-sm text-gray-500 mb-1">Medium Risk</div>
            <div className="text-3xl font-bold text-yellow-600">{riskStats?.mediumRisk || 0}</div>
          </div>
          <div className="bg-white rounded-xl shadow-sm p-6">
            <div className="text-sm text-gray-500 mb-1">High Risk</div>
            <div className="text-3xl font-bold text-orange-600">{riskStats?.highRisk || 0}</div>
          </div>
          <div className="bg-white rounded-xl shadow-sm p-6">
            <div className="text-sm text-gray-500 mb-1">Critical Risk</div>
            <div className="text-3xl font-bold text-red-600">{riskStats?.criticalRisk || 0}</div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          {/* Risk Distribution Chart */}
          <div className="bg-white rounded-xl shadow-sm p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Risk Level Distribution</h3>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={[
                    { name: 'Low', value: riskStats?.lowRisk || 0 },
                    { name: 'Medium', value: riskStats?.mediumRisk || 0 },
                    { name: 'High', value: riskStats?.highRisk || 0 },
                    { name: 'Critical', value: riskStats?.criticalRisk || 0 }
                  ]}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, value }) => `${name}: ${value}`}
                  outerRadius={100}
                  dataKey="value"
                >
                  <Cell fill="#10B981" />
                  <Cell fill="#F59E0B" />
                  <Cell fill="#F97316" />
                  <Cell fill="#EF4444" />
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>

          {/* Predicted Delays */}
          <div className="bg-white rounded-xl shadow-sm p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Predicted Delays ({predictedDelays.length})</h3>
            <div className="space-y-3 max-h-64 overflow-y-auto">
              {predictedDelays.slice(0, 10).map((delay) => (
                <div key={delay.id} className="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                  <div>
                    <div className="font-medium text-sm">{delay.trackingNumber}</div>
                    <div className="text-xs text-gray-500">Confidence: {delay.confidenceScore}%</div>
                  </div>
                  <div className="text-right">
                    <div className="text-sm font-medium text-red-600">{delay.predictedDelayHours}h delay</div>
                    <div className="text-xs text-gray-500">Predicted</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* High Risk Shipments Table */}
        <div className="bg-white rounded-xl shadow-sm">
          <div className="p-4 border-b">
            <h3 className="text-lg font-semibold text-gray-900">High & Critical Risk Shipments</h3>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Tracking #</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Risk Level</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Risk Score</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Delay Probability</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Factors</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Calculated</th>
                </tr>
              </thead>
              <tbody>
                {highRiskShipments.map((risk) => (
                  <tr key={risk.id} className="border-t hover:bg-gray-50">
                    <td className="px-4 py-3 text-sm font-medium">{risk.trackingNumber}</td>
                    <td className="px-4 py-3">
                      <span className={`px-2 py-1 rounded-full text-xs font-medium ${getRiskColor(risk.riskLevel)}`}>
                        {risk.riskLevel}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-sm">{risk.riskScore}</td>
                    <td className="px-4 py-3 text-sm">{risk.delayProbability}%</td>
                    <td className="px-4 py-3 text-sm text-gray-600 max-w-xs truncate">{risk.factors}</td>
                    <td className="px-4 py-3 text-sm text-gray-500">{new Date(risk.calculatedAt).toLocaleDateString()}</td>
                  </tr>
                ))}
                {highRiskShipments.length === 0 && (
                  <tr>
                    <td colSpan={6} className="px-4 py-8 text-center text-gray-500">No high-risk shipments found</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </main>
    </div>
  );
}

export default Risk;