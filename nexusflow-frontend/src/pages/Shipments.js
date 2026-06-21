import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { shipmentAPI, supplierAPI } from '../services/api';

function Shipments() {
  const [shipments, setShipments] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [formData, setFormData] = useState({
    supplierId: '', originCity: '', originCountry: '',
    destinationCity: '', destinationCountry: '',
    shipmentDate: '', estimatedDeliveryDate: '',
    cargoType: '', weightKg: '', valueUsd: '',
    priority: 'MEDIUM', notes: ''
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [shipmentsRes, suppliersRes] = await Promise.all([
        shipmentAPI.getAll(),
        supplierAPI.getAll()
      ]);
      setShipments(shipmentsRes.data);
      setSuppliers(suppliersRes.data);
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (searchQuery.trim()) {
      try {
        const res = await shipmentAPI.search(searchQuery);
        setShipments(res.data);
      } catch (error) {
        console.error('Search error:', error);
      }
    } else {
      fetchData();
    }
  };

  const handleInputChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await shipmentAPI.create(formData);
      setShowForm(false);
      fetchData();
      setFormData({
        supplierId: '', originCity: '', originCountry: '',
        destinationCity: '', destinationCountry: '',
        shipmentDate: '', estimatedDeliveryDate: '',
        cargoType: '', weightKg: '', valueUsd: '',
        priority: 'MEDIUM', notes: ''
      });
    } catch (error) {
      console.error('Error creating shipment:', error);
    }
  };

  const handleStatusUpdate = async (id, newStatus) => {
    try {
      await shipmentAPI.updateStatus(id, newStatus);
      fetchData();
    } catch (error) {
      console.error('Error updating status:', error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this shipment?')) {
      try {
        await shipmentAPI.delete(id);
        fetchData();
      } catch (error) {
        console.error('Error deleting shipment:', error);
      }
    }
  };

  const getStatusColor = (status) => {
    const colors = {
      PENDING: 'bg-yellow-100 text-yellow-800',
      IN_TRANSIT: 'bg-blue-100 text-blue-800',
      DELAYED: 'bg-red-100 text-red-800',
      DELIVERED: 'bg-green-100 text-green-800',
      CANCELLED: 'bg-gray-100 text-gray-800'
    };
    return colors[status] || 'bg-gray-100 text-gray-800';
  };

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
              <Link to="/shipments" className="text-primary-600 font-medium">Shipments</Link>
              <Link to="/suppliers" className="text-gray-600 hover:text-primary-600">Suppliers</Link>
              <Link to="/risk" className="text-gray-600 hover:text-primary-600">Risk</Link>
              <Link to="/analytics" className="text-gray-600 hover:text-primary-600">Analytics</Link>
            </nav>
          </div>
          <Link to="/dashboard" className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700">Back to Dashboard</Link>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-8">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-900">Shipment Management</h2>
          <button onClick={() => setShowForm(!showForm)} className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700">
            {showForm ? 'Cancel' : 'Add Shipment'}
          </button>
        </div>

        {showForm && (
          <div className="bg-white rounded-xl shadow-sm p-6 mb-6">
            <h3 className="text-lg font-semibold mb-4">Add New Shipment</h3>
            <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <select name="supplierId" value={formData.supplierId} onChange={handleInputChange} className="border rounded-lg px-3 py-2" required>
                <option value="">Select Supplier</option>
                {suppliers.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
              </select>
              <input name="originCity" placeholder="Origin City" value={formData.originCity} onChange={handleInputChange} className="border rounded-lg px-3 py-2" required />
              <input name="originCountry" placeholder="Origin Country" value={formData.originCountry} onChange={handleInputChange} className="border rounded-lg px-3 py-2" required />
              <input name="destinationCity" placeholder="Destination City" value={formData.destinationCity} onChange={handleInputChange} className="border rounded-lg px-3 py-2" required />
              <input name="destinationCountry" placeholder="Destination Country" value={formData.destinationCountry} onChange={handleInputChange} className="border rounded-lg px-3 py-2" required />
              <input name="shipmentDate" type="date" value={formData.shipmentDate} onChange={handleInputChange} className="border rounded-lg px-3 py-2" required />
              <input name="estimatedDeliveryDate" type="date" value={formData.estimatedDeliveryDate} onChange={handleInputChange} className="border rounded-lg px-3 py-2" required />
              <input name="cargoType" placeholder="Cargo Type" value={formData.cargoType} onChange={handleInputChange} className="border rounded-lg px-3 py-2" />
              <input name="weightKg" type="number" placeholder="Weight (kg)" value={formData.weightKg} onChange={handleInputChange} className="border rounded-lg px-3 py-2" />
              <input name="valueUsd" type="number" placeholder="Value (USD)" value={formData.valueUsd} onChange={handleInputChange} className="border rounded-lg px-3 py-2" />
              <select name="priority" value={formData.priority} onChange={handleInputChange} className="border rounded-lg px-3 py-2">
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
                <option value="URGENT">Urgent</option>
              </select>
              <textarea name="notes" placeholder="Notes" value={formData.notes} onChange={handleInputChange} className="border rounded-lg px-3 py-2 md:col-span-3" rows={2} />
              <button type="submit" className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 md:col-span-3">Create Shipment</button>
            </form>
          </div>
        )}

        <div className="bg-white rounded-xl shadow-sm mb-6">
          <div className="p-4 border-b">
            <div className="flex gap-2">
              <input type="text" placeholder="Search shipments..." value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} className="flex-1 border rounded-lg px-3 py-2" />
              <button onClick={handleSearch} className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700">Search</button>
            </div>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Tracking #</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Supplier</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Route</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Est. Delivery</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Status</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Priority</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Actions</th>
                </tr>
              </thead>
              <tbody>
                {shipments.map((shipment) => (
                  <tr key={shipment.id} className="border-t hover:bg-gray-50">
                    <td className="px-4 py-3 text-sm font-medium">{shipment.trackingNumber}</td>
                    <td className="px-4 py-3 text-sm">{shipment.supplierName}</td>
                    <td className="px-4 py-3 text-sm">{shipment.originCity} → {shipment.destinationCity}</td>
                    <td className="px-4 py-3 text-sm">{shipment.estimatedDeliveryDate}</td>
                    <td className="px-4 py-3"><span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(shipment.status)}`}>{shipment.status}</span></td>
                    <td className="px-4 py-3 text-sm">{shipment.priority}</td>
                    <td className="px-4 py-3 text-sm">
                      <select onChange={(e) => handleStatusUpdate(shipment.id, e.target.value)} value={shipment.status} className="border rounded px-2 py-1 text-sm">
                        <option value="PENDING">Pending</option>
                        <option value="IN_TRANSIT">In Transit</option>
                        <option value="DELAYED">Delayed</option>
                        <option value="DELIVERED">Delivered</option>
                        <option value="CANCELLED">Cancelled</option>
                      </select>
                      <button onClick={() => handleDelete(shipment.id)} className="ml-2 text-red-600 hover:text-red-800">Delete</button>
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

export default Shipments;