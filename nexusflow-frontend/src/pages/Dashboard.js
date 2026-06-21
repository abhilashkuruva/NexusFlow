import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { analyticsAPI } from '../services/api';
import {
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer
} from 'recharts';

function Dashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState(null);

  useEffect(() => {
    const userData = JSON.parse(localStorage.getItem('user'));
    setUser(userData);
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const [statsRes, shipmentChart, riskChart] = await Promise.all([
        analyticsAPI.getQuickStats(1),
        analyticsAPI.getShipmentStatusChart(),
        analyticsAPI.getRiskDistributionChart()
      ]);

      setStats({
        quickStats: statsRes.data,
        shipmentChart: shipmentChart.data,
        riskChart: riskChart.data
      });

    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const COLORS = [
    '#3B82F6',
    '#10B981',
    '#F59E0B',
    '#EF4444',
    '#8B5CF6'
  ];

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login';
  };


  if (loading) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <div className="text-xl">Loading...</div>
      </div>
    );
  }


  return (
    <div className="min-h-screen bg-gray-100">

      <header className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">

          <div className="flex items-center space-x-4">

            <h1 className="text-2xl font-bold text-primary-600">
              NexusFlow
            </h1>

            <nav className="hidden md:flex space-x-6">

              <Link to="/dashboard"
                className="text-primary-600 font-medium">
                Dashboard
              </Link>

              <Link to="/shipments"
                className="text-gray-600 hover:text-primary-600">
                Shipments
              </Link>

              <Link to="/suppliers"
                className="text-gray-600 hover:text-primary-600">
                Suppliers
              </Link>

              <Link to="/risk"
                className="text-gray-600 hover:text-primary-600">
                Risk
              </Link>

              <Link to="/analytics"
                className="text-gray-600 hover:text-primary-600">
                Analytics
              </Link>

            </nav>

          </div>


          <div className="flex items-center space-x-4">

            <span className="text-gray-600">
              Welcome, {user?.firstName}
            </span>

            <button
              onClick={handleLogout}
              className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700">
              Logout
            </button>

          </div>

        </div>
      </header>


      <main className="max-w-7xl mx-auto px-4 py-8">


        <div className="grid grid-cols-1 md:grid-cols-5 gap-4 mb-8">

          {[
            ["Total Shipments", stats?.quickStats?.totalShipments, "text-gray-900"],
            ["Delayed", stats?.quickStats?.delayedShipments, "text-red-600"],
            ["High Risk", stats?.quickStats?.highRiskShipments, "text-orange-600"],
            ["Predicted Delays", stats?.quickStats?.predictedDelays, "text-yellow-600"],
            ["Unread Alerts", stats?.quickStats?.unreadNotifications, "text-blue-600"]
          ].map(([title,value,color]) => (

            <div key={title}
              className="bg-white rounded-xl shadow-sm p-6">

              <div className="text-sm text-gray-500 mb-1">
                {title}
              </div>

              <div className={`text-3xl font-bold ${color}`}>
                {value || 0}
              </div>

            </div>

          ))}

        </div>



        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">


          <div className="bg-white rounded-xl shadow-sm p-6">

            <h3 className="text-lg font-semibold mb-4">
              Shipment Status Distribution
            </h3>

            <ResponsiveContainer width="100%" height={300}>

              <BarChart data={stats?.shipmentChart || []}>

                <CartesianGrid strokeDasharray="3 3"/>

                <XAxis dataKey="status"/>

                <YAxis/>

                <Tooltip/>

                <Bar dataKey="count" fill="#3B82F6"/>

              </BarChart>

            </ResponsiveContainer>

          </div>



          <div className="bg-white rounded-xl shadow-sm p-6">

            <h3 className="text-lg font-semibold mb-4">
              Risk Level Distribution
            </h3>

            <ResponsiveContainer width="100%" height={300}>

              <PieChart>

                <Pie
                  data={stats?.riskChart || []}
                  dataKey="count"
                  outerRadius={100}
                  label={({level,count}) =>
                    `${level}: ${count}`
                  }
                >

                  {(stats?.riskChart || []).map((entry,index)=>(
                    <Cell
                      key={index}
                      fill={COLORS[index % COLORS.length]}
                    />
                  ))}

                </Pie>

                <Tooltip/>

              </PieChart>

            </ResponsiveContainer>

          </div>


        </div>


      </main>

    </div>
  );
}


export default Dashboard;