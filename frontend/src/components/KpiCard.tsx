interface Props {
  label: string
  value: string
  target?: string
  accent?: string
}

export default function KpiCard({ label, value, target, accent = 'text-blue-400' }: Props) {
  return (
    <div className="bg-slate-900 rounded-lg p-4 flex-1 min-w-40">
      <p className="text-slate-400 text-xs uppercase tracking-wide">{label}</p>
      <p className={`text-2xl font-bold mt-1 ${accent}`}>{value}</p>
      {target && <p className="text-slate-500 text-xs mt-1">Target: {target}</p>}
    </div>
  )
}