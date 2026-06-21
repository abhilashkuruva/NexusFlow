import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { riskAPI } from '../services/api';
import {
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell
} from 'recharts';

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
      const [
        statsRes,
        highRiskRes,
        delaysRes
      ] = await Promise.all([
        riskAPI.getStats(),
        riskAPI.getHighRisk(),
        riskAPI.getPredictedDelays()
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
      await fetchData();
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


  if (loading) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <div className="text-xl">
          Loading...
        </div>
      </div>
    );
  }


  const chartData = [
    {
      name: 'Low',
      value: riskStats?.lowRisk || 0
    },
    {
      name: 'Medium',
      value: riskStats?.mediumRisk || 0
    },
    {
      name: 'High',
      value: riskStats?.highRisk || 0
    },
    {
      name: 'Critical',
      value: riskStats?.criticalRisk || 0
    }
  ];


  return (
    <div className="min-h-screen bg-gray-100">

      <header className="bg-white shadow-sm">

        <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">

          <div className="flex items-center space-x-4">

            <h1 className="text-2xl font-bold text-primary-600">
              NexusFlow
            </h1>

            <nav className="hidden md:flex space-x-6">

              <Link 
                to="/dashboard"
                className="text-gray-600 hover:text-primary-600">
                Dashboard
              </Link>

              <Link 
                to="/shipments"
                className="text-gray-600 hover:text-primary-600">
                Shipments
              </Link>

              <Link 
                to="/suppliers"
                className="text-gray-600 hover:text-primary-600">
                Suppliers
              </Link>

              <Link 
                to="/risk"
                className="text-primary-600 font-medium">
                Risk
              </Link>

              <Link 
                to="/analytics"
                className="text-gray-600 hover:text-primary-600">
                Analytics
              </Link>

            </nav>

          </div>


          <Link
            to="/dashboard"
            className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700">
            Back to Dashboard
          </Link>

        </div>

      </header>



      <main className="max-w-7xl mx-auto px-4 py-8">


        <div className="flex justify-between items-center mb-6">

          <h2 className="text-2xl font-bold text-gray-900">
            Risk Analysis Dashboard
          </h2>

          <button
            onClick={handleCalculateAll}
            className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700">

            Calculate All Risks

          </button>

        </div>



        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">


          {[
            ['Low Risk', riskStats?.lowRisk, 'text-green-600'],
            ['Medium Risk', riskStats?.mediumRisk, 'text-yellow-600'],
            ['High Risk', riskStats?.highRisk, 'text-orange-600'],
            ['Critical Risk', riskStats?.criticalRisk, 'text-red-600']
          ].map(([title,value,color]) => (

            <div
              key={title}
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
              Risk Level Distribution
            </h3>


            <ResponsiveContainer width="100%" height={300}>

              <PieChart>

                <Pie
                  data={chartData}
                  dataKey="value"
                  outerRadius={100}
                  label={({name,value}) =>
                    `${name}: ${value}`
                  }
                >

                  <Cell fill="#10B981"/>
                  <Cell fill="#F59E0B"/>
                  <Cell fill="#F97316"/>
                  <Cell fill="#EF4444"/>

                </Pie>

                <Tooltip />

              </PieChart>

            </ResponsiveContainer>


          </div>




          <div className="bg-white rounded-xl shadow-sm p-6">

            <h3 className="text-lg font-semibold mb-4">
              Predicted Delays ({predictedDelays.length})
            </h3>


            <div className="space-y-3 max-h-64 overflow-y-auto">

              {predictedDelays.slice(0,10).map((delay)=>(
                
                <div
                  key={delay.id}
                  className="flex justify-between items-center p-3 bg-gray-50 rounded-lg">

                  <div>

                    <div className="font-medium text-sm">
                      {delay.trackingNumber}
                    </div>

                    <div className="text-xs text-gray-500">
                      Confidence: {delay.confidenceScore}%
                    </div>

                  </div>


                  <div className="text-right">

                    <div className="text-sm font-medium text-red-600">
                      {delay.predictedDelayHours}h delay
                    </div>

                    <div className="text-xs text-gray-500">
                      Predicted
                    </div>

                  </div>


                </div>

              ))}

            </div>


          </div>


        </div>




        <div className="bg-white rounded-xl shadow-sm">

          <div className="p-4 border-b">

            <h3 className="text-lg font-semibold">
              High & Critical Risk Shipments
            </h3>

          </div>


          <div className="overflow-x-auto">

            <table className="w-full">

              <thead className="bg-gray-50">

                <tr>

                  <th className="px-4 py-3 text-left">
                    Tracking #
                  </th>

                  <th className="px-4 py-3 text-left">
                    Risk Level
                  </th>

                  <th className="px-4 py-3 text-left">
                    Risk Score
                  </th>

                  <th className="px-4 py-3 text-left">
                    Delay Probability
                  </th>

                </tr>

              </thead>


              <tbody>

                {highRiskShipments.map((risk)=>(

                  <tr key={risk.id}
                    className="border-t">

                    <td className="px-4 py-3">
                      {risk.trackingNumber}
                    </td>

                    <td className="px-4 py-3">

                      <span className={`px-2 py-1 rounded-full text-xs ${getRiskColor(risk.riskLevel)}`}>
                        {risk.riskLevel}
                      </span>

                    </td>

                    <td className="px-4 py-3">
                      {risk.riskScore}
                    </td>

                    <td className="px-4 py-3">
                      {risk.delayProbability}%
                    </td>

                  </tr>

                ))}


              </tbody>


            </table>

          </div>


        </div>


      </main>


    </div>
  );
}


export default Risk;