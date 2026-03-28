import { useState } from 'react';
import { RevenueChart } from './components/RevenueChart';
import { OrdersChart } from './components/OrdersChart';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './components/ui/select';
import { Button } from './components/ui/button';

export default function App() {
  const [period, setPeriod] = useState<'month' | 'year'>('year');
  const [selectedMonth, setSelectedMonth] = useState('1');
  const [selectedYear, setSelectedYear] = useState('2026');

  // Mock data for statistics
  const stats = {
    totalRevenue: '10.874.000 VND',
    totalProfit: '3.294.000 VND',
    totalOrders: 18,
    completed: 14,
    cancelled: 3,
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Sidebar */}
      <div className="fixed left-0 top-0 h-full w-20 bg-[#1a2942] flex flex-col items-center py-6 gap-6">
        <div className="w-12 h-12 bg-[#2d3e57] rounded-lg flex items-center justify-center text-white font-semibold">
          A
        </div>
        <div className="flex flex-col gap-4">
          <div className="w-12 h-12 bg-[#2d3e57] rounded-lg flex items-center justify-center text-white">
            <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
              <path d="M2 11a1 1 0 011-1h2a1 1 0 011 1v5a1 1 0 01-1 1H3a1 1 0 01-1-1v-5zM8 7a1 1 0 011-1h2a1 1 0 011 1v9a1 1 0 01-1 1H9a1 1 0 01-1-1V7zM14 4a1 1 0 011-1h2a1 1 0 011 1v12a1 1 0 01-1 1h-2a1 1 0 01-1-1V4z" />
            </svg>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="ml-20 p-8">
        {/* Header */}
        <div className="flex justify-between items-start mb-8">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">Statistic Overview</h1>
            <p className="text-gray-600">
              Summary of revenue, profit, orders, and sold products in {period === 'year' ? `year ${selectedYear}` : `${selectedMonth}/${selectedYear}`}.
            </p>
          </div>

          {/* Filter Controls */}
          <div className="flex gap-4 items-center bg-white p-4 rounded-lg shadow-sm">
            <div>
              <label className="text-sm text-gray-600 block mb-2">Period</label>
              <Select value={period} onValueChange={(value: 'month' | 'year') => setPeriod(value)}>
                <SelectTrigger className="w-32">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="month">Month</SelectItem>
                  <SelectItem value="year">Year</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {period === 'month' && (
              <div>
                <label className="text-sm text-gray-600 block mb-2">Month</label>
                <Select value={selectedMonth} onValueChange={setSelectedMonth}>
                  <SelectTrigger className="w-32">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {Array.from({ length: 12 }, (_, i) => (
                      <SelectItem key={i + 1} value={String(i + 1)}>
                        {i + 1}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            )}

            <div>
              <label className="text-sm text-gray-600 block mb-2">Year</label>
              <input
                type="number"
                value={selectedYear}
                onChange={(e) => setSelectedYear(e.target.value)}
                className="w-32 h-10 px-3 border border-gray-300 rounded-md"
              />
            </div>

            <Button className="mt-6 bg-blue-600 hover:bg-blue-700">Apply</Button>
          </div>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-5 gap-4 mb-8">
          <div className="bg-white p-6 rounded-lg shadow-sm">
            <div className="text-sm text-gray-600 mb-2">Total Revenue</div>
            <div className="text-2xl font-bold text-gray-900">{stats.totalRevenue}</div>
          </div>
          <div className="bg-white p-6 rounded-lg shadow-sm">
            <div className="text-sm text-gray-600 mb-2">Total Profit</div>
            <div className="text-2xl font-bold text-gray-900">{stats.totalProfit}</div>
          </div>
          <div className="bg-white p-6 rounded-lg shadow-sm">
            <div className="text-sm text-gray-600 mb-2">Total Orders</div>
            <div className="text-2xl font-bold text-gray-900">{stats.totalOrders}</div>
          </div>
          <div className="bg-white p-6 rounded-lg shadow-sm">
            <div className="text-sm text-gray-600 mb-2">Completed</div>
            <div className="text-2xl font-bold text-gray-900">{stats.completed}</div>
          </div>
          <div className="bg-white p-6 rounded-lg shadow-sm">
            <div className="text-sm text-gray-600 mb-2">Cancelled</div>
            <div className="text-2xl font-bold text-gray-900">{stats.cancelled}</div>
          </div>
        </div>

        {/* Charts */}
        <div className="grid grid-cols-2 gap-6 mb-8">
          <RevenueChart period={period} month={selectedMonth} year={selectedYear} />
          <OrdersChart period={period} month={selectedMonth} year={selectedYear} />
        </div>

        {/* Tables */}
        <div className="grid grid-cols-2 gap-6">
          {/* Sold Products Table */}
          <div className="bg-white p-6 rounded-lg shadow-sm">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-lg font-semibold text-gray-900">Sold Products in 2026</h2>
              <p className="text-sm text-gray-600">Sorted by total sold in descending order</p>
            </div>
            <table className="w-full">
              <thead>
                <tr className="border-b">
                  <th className="text-left py-3 text-sm font-medium text-gray-600">PRODUCT</th>
                  <th className="text-left py-3 text-sm font-medium text-gray-600">TOTAL SOLD</th>
                  <th className="text-left py-3 text-sm font-medium text-gray-600">REVENUE</th>
                  <th className="text-left py-3 text-sm font-medium text-gray-600">PROFIT</th>
                </tr>
              </thead>
              <tbody>
                <tr className="border-b">
                  <td className="py-4">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 bg-gray-200 rounded"></div>
                      <span className="font-medium">TO AHA 30% + BHA 2%</span>
                    </div>
                  </td>
                  <td className="py-4">16</td>
                  <td className="py-4">5.304.000 VND</td>
                  <td className="py-4">1.644.000 VND</td>
                </tr>
              </tbody>
            </table>
          </div>

          {/* Top Selling Products Table */}
          <div className="bg-white p-6 rounded-lg shadow-sm">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-lg font-semibold text-gray-900">Top Selling Products</h2>
              <p className="text-sm text-gray-600">Top products in selected period</p>
            </div>
            <table className="w-full">
              <thead>
                <tr className="border-b">
                  <th className="text-left py-3 text-sm font-medium text-gray-600">PRODUCT</th>
                  <th className="text-right py-3 text-sm font-medium text-gray-600">SOLD</th>
                </tr>
              </thead>
              <tbody>
                <tr className="border-b">
                  <td className="py-4 font-medium">TO AHA 30% + BHA 2%</td>
                  <td className="py-4 text-right">16</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
