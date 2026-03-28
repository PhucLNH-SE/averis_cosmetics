import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

interface OrdersChartProps {
  period: 'month' | 'year';
  month: string;
  year: string;
}

export function OrdersChart({ period, month, year }: OrdersChartProps) {
  // Mock data for year view
  const yearData = [
    { name: 'Month 1', total: 4, completed: 3, cancelled: 1 },
    { name: 'Month 2', total: 5, completed: 3, cancelled: 1 },
    { name: 'Month 3', total: 9, completed: 8, cancelled: 1 },
  ];

  // Mock data for month view (daily or weekly data)
  const monthData = [
    { name: 'Week 1', total: 2, completed: 2, cancelled: 0 },
    { name: 'Week 2', total: 3, completed: 2, cancelled: 1 },
    { name: 'Week 3', total: 4, completed: 3, cancelled: 1 },
    { name: 'Week 4', total: 5, completed: 4, cancelled: 1 },
  ];

  const data = period === 'year' ? yearData : monthData;

  return (
    <div className="bg-white p-6 rounded-lg shadow-sm">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-lg font-semibold text-gray-900">Orders Overview</h2>
        <p className="text-sm text-gray-600">
          {period === 'year' 
            ? `Total, completed and cancelled orders by month in ${year}` 
            : `Total, completed and cancelled orders by week in ${month}/${year}`}
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
            allowDecimals={false}
          />
          <Tooltip 
            formatter={(value: number) => value}
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
            dataKey="total" 
            stroke="#3b82f6" 
            strokeWidth={2}
            name="Total Orders"
            dot={{ fill: '#3b82f6', r: 4 }}
            activeDot={{ r: 6 }}
          />
          <Line 
            type="monotone" 
            dataKey="completed" 
            stroke="#10b981" 
            strokeWidth={2}
            name="Completed"
            dot={{ fill: '#10b981', r: 4 }}
            activeDot={{ r: 6 }}
          />
          <Line 
            type="monotone" 
            dataKey="cancelled" 
            stroke="#ef4444" 
            strokeWidth={2}
            name="Cancelled"
            dot={{ fill: '#ef4444', r: 4 }}
            activeDot={{ r: 6 }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
