import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts'
import type { ProductionTrendPointResponse } from '../types/dashborad'

export default function ProductionTrendChart({ data }: { data: ProductionTrendPointResponse[] }) {
  const chartData = data.map((d) => ({
    ...d,
    label: new Date(d.date).toLocaleDateString('id-ID', { day: '2-digit', month: 'short' }),
  }))

  return (
    <div className="bg-slate-900 rounded-lg p-4 h-72">
      <p className="text-slate-300 text-sm mb-2">Production Trend (Last 7 Days)</p>
      <ResponsiveContainer width="100%" height="90%">
        <BarChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
          <XAxis dataKey="label" stroke="#94a3b8" fontSize={11} />
          <YAxis stroke="#94a3b8" fontSize={11} />
          <Tooltip
            contentStyle={{ background: '#0f172a', border: '1px solid #334155' }}
            labelStyle={{ color: '#e2e8f0' }}
          />
          <Legend wrapperStyle={{ fontSize: 12 }} />
          <Bar dataKey="actualKg" fill="#3b82f6" name="Actual (kg)" />
          <Bar dataKey="plannedKg" fill="#64748b" name="Planned (kg)" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}