import type { AlarmResponse } from '../types/dashborad'
const levelColor: Record<string, string> = {
  CRITICAL: 'text-red-400',
  HIGH: 'text-orange-400',
  MEDIUM: 'text-yellow-400',
  LOW: 'text-blue-400',
}

export default function AlarmList({ data }: { data: AlarmResponse[] }) {
  return (
    <div className="bg-slate-900 rounded-lg p-4">
      <p className="text-slate-300 text-sm mb-3">Recent Alarms</p>
      <ul className="space-y-2">
        {data.map((alarm) => (
          <li key={alarm.id} className="flex justify-between text-sm border-t border-slate-800 pt-2">
            <div>
              <p className="text-white">{alarm.message}</p>
              <p className="text-slate-500 text-xs">{alarm.area}</p>
            </div>
            <span className={levelColor[alarm.level] ?? 'text-slate-400'}>{alarm.level}</span>
          </li>
        ))}
        {data.length === 0 && <p className="text-slate-500 text-sm">Belum ada alarm</p>}
      </ul>
    </div>
  )
}