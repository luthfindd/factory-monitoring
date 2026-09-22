import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts'
import type { OeeTrendPointResponse } from '../types/dashborad'

function formatHour(iso: string) {
  return new Date(iso).toLocaleTimeString('id-ID', { hour: '2-digit', minute: '2-digit' })
}

export default function OeeTrendChart({ data }: { data: OeeTrendPointResponse[] }) {
  const chartData = data.map((d) => ({ ...d, time: formatHour(d.recordedAt) }))

  return (
    <div className="bg-slate-900 rounded-lg p-4 h-72">
      <p className="text-slate-300 text-sm mb-2">OEE Trend (Last 24 Hours)</p>
      <ResponsiveContainer width="100%" height="90%">
        <LineChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
          <XAxis dataKey="time" stroke="#94a3b8" fontSize={11} interval={11} />
          <YAxis domain={[0, 100]} stroke="#94a3b8" fontSize={11} />
          <Tooltip
            contentStyle={{ background: '#0f172a', border: '1px solid #334155' }}
            labelStyle={{ color: '#e2e8f0' }}
          />
          <Line type="monotone" dataKey="oee" stroke="#3b82f6" dot={false} strokeWidth={2} />
        </LineChart>
      </ResponsiveContainer>
    </div>
  )
}