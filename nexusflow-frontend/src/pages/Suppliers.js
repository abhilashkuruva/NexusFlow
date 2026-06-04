import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { supplierAPI } from '../services/api';

function Suppliers() {
  const [suppliers, setSuppliers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [formData, setFormData] = useState({
    name: '', contactPerson: '', email: '', phone: '', address: '', country: ''
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const res = await supplierAPI.getAll();
      setSuppliers(res.data);
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (searchQuery.trim()) {
      try {
        const res = await supplierAPI.search(searchQuery);
        setSuppliers(res.data);
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
      await supplierAPI.create(formData);
      setShowForm(false);
      fetchData();
      setFormData({ name: '', contactPerson: '', email: '', phone: '', address: '', country: '' });
    } catch (error) {
      console.error('Error creating supplier:', error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this supplier?')) {
      try {
        await supplierAPI.delete(id);
        fetchData();
      } catch (error) {
        console.error('Error deleting supplier:', error);
      }
    }
  };

  const getReliabilityColor = (score) => {
    if (score >= 4) return 'text-green-600';
    if (score >= 3) return 'text-yellow-600';
    return 'text-red-600';
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
              <Link to="/shipments" className="text-gray-600 hover:text-primary-600">Shipments</Link>
              <Link to="/suppliers" className="text-primary-600 font-medium">Suppliers</Link>
              <Link to="/risk" className="text-gray-600 hover:text-primary-600">Risk</Link>
              <Link to="/analytics" className="text-gray-600 hover:text-primary-600">Analytics</Link>
            </nav>
          </div>
          <Link to="/dashboard" className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700">Back to Dashboard</Link>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-8">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-900">Supplier Management</h2>
          <button onClick={() => setShowForm(!showForm)} className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700">
            {showForm ? 'Cancel' : 'Add Supplier'}
          </button>
        </div>

        {showForm && (
          <div className="bg-white rounded-xl shadow-sm p-6 mb-6">
            <h3 className="text-lg font-semibold mb-4">Add New Supplier</h3>
            <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <input name="name" placeholder="Supplier Name" value={formData.name} onChange={handleInputChange} className="border rounded-lg px-3 py-2" required />
              <input name="contactPerson" placeholder="Contact Person" value={formData.contactPerson} onChange={handleInputChange} className="border rounded-lg px-3 py-2" required />
              <input name="email" type="email" placeholder="Email" value={formData.email} onChange={handleInputChange} className="border rounded-lg px-3 py-2" required />
              <input name="phone" placeholder="Phone" value={formData.phone} onChange={handleInputChange} className="border rounded-lg px-3 py-2" />
              <input name="address" placeholder="Address" value={formData.address} onChange={handleInputChange} className="border rounded-lg px-3 py-2" />
              <input name="country" placeholder="Country" value={formData.country} onChange={handleInputChange} className="border rounded-lg px-3 py-2" required />
              <button type="submit" className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 md:col-span-3">Create Supplier</button>
            </form>
          </div>
        )}

        <div className="bg-white rounded-xl shadow-sm">
          <div className="p-4 border-b">
            <div className="flex gap-2">
              <input type="text" placeholder="Search suppliers..." value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} className="flex-1 border rounded-lg px-3 py-2" />
              <button onClick={handleSearch} className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700">Search</button>
            </div>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Name</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Contact</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Email</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Country</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Reliability</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Delay Rate</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Actions</th>
                </tr>
              </thead>
              <tbody>
                {suppliers.map((supplier) => (
                  <tr key={supplier.id} className="border-t hover:bg-gray-50">
                    <td className="px-4 py-3 text-sm font-medium">{supplier.name}</td>
                    <td className="px-4 py-3 text-sm">{supplier.contactPerson}</td>
                    <td className="px-4 py-3 text-sm">{supplier.email}</td>
                    <td className="px-4 py-3 text-sm">{supplier.country}</td>
                    <td className="px-4 py-3 text-sm">
                      <span className={`font-semibold ${getReliabilityColor(supplier.reliabilityScore)}`}>
                        {supplier.reliabilityScore?.toFixed(1) || 'N/A'}/5.0
                      </span>
                    </td>
                    <td className="px-4 py-3 text-sm">{supplier.delayRate?.toFixed(1) || '0'}%</td>
                    <td className="px-4 py-3 text-sm">
                      <button onClick={() => handleDelete(supplier.id)} className="text-red-600 hover:text-red-800">Delete</button>
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

export default Suppliers;