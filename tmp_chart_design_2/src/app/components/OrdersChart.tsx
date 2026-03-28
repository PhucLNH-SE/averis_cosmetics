import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts';

interface OrdersChartProps {
  period: 'month' | 'year';
  month: string;
  year: string;
}

const COLORS = {
  completed: '#10b981',
  cancelled: '#ef4444',
  pending: '#f59e0b',
};

export function OrdersChart({ period, month, year }: OrdersChartProps) {
  // Mock data based on period
  const stats = period === 'year' 
    ? { total: 18, completed: 14, cancelled: 3 }
    : { total: 14, completed: 11, cancelled: 2 };

  const pending = stats.total - stats.completed - stats.cancelled;

  const data = [
    { name: 'Completed', value: stats.completed, color: COLORS.completed },
    { name: 'Pending', value: pending, color: COLORS.pending },
    { name: 'Cancelled', value: stats.cancelled, color: COLORS.cancelled },
  ];

  const renderCustomLabel = ({ cx, cy, midAngle, innerRadius, outerRadius, percent }: any) => {
    const radius = innerRadius + (outerRadius - innerRadius) * 0.5;
    const x = cx + radius * Math.cos(-midAngle * Math.PI / 180);
    const y = cy + radius * Math.sin(-midAngle * Math.PI / 180);

    return (
      <text 
        x={x} 
        y={y} 
        fill="white" 
        textAnchor={x > cx ? 'start' : 'end'} 
        dominantBaseline="central"
        fontSize="14"
        fontWeight="600"
      >
        {`${(percent * 100).toFixed(0)}%`}
      </text>
    );
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-sm">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-lg font-semibold text-gray-900">Orders Overview</h2>
        <p className="text-sm text-gray-600">
          {period === 'year' 
            ? `Orders distribution in ${year}` 
            : `Orders distribution in ${month}/${year}`}
        </p>
      </div>
      
      {/* Stats Summary */}
      <div className="grid grid-cols-3 gap-4 mb-6">
        <div className="text-center">
          <div className="text-2xl font-bold text-gray-900">{stats.completed}</div>
          <div className="text-sm text-gray-600 flex items-center justify-center gap-2">
            <div className="w-3 h-3 rounded-full bg-[#10b981]"></div>
            Completed
          </div>
        </div>
        <div className="text-center">
          <div className="text-2xl font-bold text-gray-900">{pending}</div>
          <div className="text-sm text-gray-600 flex items-center justify-center gap-2">
            <div className="w-3 h-3 rounded-full bg-[#f59e0b]"></div>
            Pending
          </div>
        </div>
        <div className="text-center">
          <div className="text-2xl font-bold text-gray-900">{stats.cancelled}</div>
          <div className="text-sm text-gray-600 flex items-center justify-center gap-2">
            <div className="w-3 h-3 rounded-full bg-[#ef4444]"></div>
            Cancelled
          </div>
        </div>
      </div>

      <ResponsiveContainer width="100%" height={280}>
        <PieChart>
          <Pie
            data={data}
            cx="50%"
            cy="50%"
            labelLine={false}
            label={renderCustomLabel}
            outerRadius={100}
            fill="#8884d8"
            dataKey="value"
          >
            {data.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={entry.color} />
            ))}
          </Pie>
          <Tooltip 
            formatter={(value: number) => [`${value} orders`, 'Count']}
            contentStyle={{ 
              backgroundColor: 'white', 
              border: '1px solid #e5e7eb',
              borderRadius: '8px',
              boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)'
            }}
          />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
}