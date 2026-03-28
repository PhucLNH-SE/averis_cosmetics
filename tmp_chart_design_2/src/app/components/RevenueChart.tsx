import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

interface RevenueChartProps {
  period: 'month' | 'year';
  month: string;
  year: string;
}

export function RevenueChart({ period, month, year }: RevenueChartProps) {
  // Mock data for year view
  const yearData = [
    { name: 'Month 1', revenue: 2100000, profit: 650000 },
    { name: 'Month 2', revenue: 2300000, profit: 720000 },
    { name: 'Month 3', revenue: 6474000, profit: 1924000 },
  ];

  // Mock data for month view (daily data)
  const monthData = [
    { name: 'Day 1', revenue: 700000, profit: 210000 },
    { name: 'Day 5', revenue: 850000, profit: 255000 },
    { name: 'Day 10', revenue: 920000, profit: 276000 },
    { name: 'Day 15', revenue: 1100000, profit: 330000 },
    { name: 'Day 20', revenue: 1250000, profit: 375000 },
    { name: 'Day 25', revenue: 1400000, profit: 420000 },
    { name: 'Day 30', revenue: 1554000, profit: 458000 },
  ];

  const data = period === 'year' ? yearData : monthData;

  return (
    <div className="bg-white p-6 rounded-lg shadow-sm">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-lg font-semibold text-gray-900">Revenue vs Profit</h2>
        <p className="text-sm text-gray-600">
          {period === 'year' 
            ? `Comparison by month in ${year}` 
            : `Comparison by day in ${month}/${year}`}
        </p>
      </div>
      
      <ResponsiveContainer width="100%" height={300}>
        <LineChart data={data} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
          <XAxis 
            dataKey="name" 
            tick={{ fontSize: 12 }}
            stroke="#9ca3af"
          />
          <YAxis 
            tick={{ fontSize: 12 }}
            stroke="#9ca3af"
            tickFormatter={(value) => `${(value / 1000000).toFixed(1)}M`}
          />
          <Tooltip 
            formatter={(value: number) => `${value.toLocaleString()} VND`}
            contentStyle={{ 
              backgroundColor: 'white', 
              border: '1px solid #e5e7eb',
              borderRadius: '8px',
              boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)'
            }}
          />
          <Legend 
            verticalAlign="bottom" 
            height={36}
            iconType="line"
            wrapperStyle={{ paddingTop: '20px' }}
          />
          <Line 
            type="monotone" 
            dataKey="revenue" 
            stroke="#3b82f6" 
            strokeWidth={2}
            name="Revenue"
            dot={{ fill: '#3b82f6', r: 4 }}
            activeDot={{ r: 6 }}
          />
          <Line 
            type="monotone" 
            dataKey="profit" 
            stroke="#10b981" 
            strokeWidth={2}
            name="Profit"
            dot={{ fill: '#10b981', r: 4 }}
            activeDot={{ r: 6 }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
