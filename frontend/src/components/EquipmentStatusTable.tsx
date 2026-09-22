import type { EquipmentStatusResponse } from '../types/dashborad'

function formatDuration(seconds: number) {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  return `${h}h ${m}m`
}

const statusColor: Record<string, string> = {
  RUNNING: 'text-green-400',
  IDLE: 'text-blue-400',
  FAULT: 'text-yellow-400',
  STOPPED: 'text-red-400',
}

export default function EquipmentStatusTable({ data }: { data: EquipmentStatusResponse[] }) {
  return (
    <div className="bg-slate-900 rounded-lg p-4">
      <p className="text-slate-300 text-sm mb-3">Equipment Status</p>
      <table className="w-full text-sm">
        <thead>
          <tr className="text-slate-500 text-left">
            <th className="font-normal pb-2">Equipment</th>
            <th className="font-normal pb-2">Status</th>
            <th className="font-normal pb-2">Run Time</th>
            <th className="font-normal pb-2">Downtime</th>
          </tr>
        </thead>
        <tbody>
          {data.map((eq) => (
            <tr key={eq.equipmentId} className="border-t border-slate-800">
              <td className="py-2 text-white">{eq.name}</td>
              <td className={`py-2 ${statusColor[eq.status] ?? 'text-slate-400'}`}>{eq.status}</td>
              <td className="py-2 text-slate-300">{formatDuration(eq.runningTimeSeconds)}</td>
              <td className="py-2 text-slate-300">{formatDuration(eq.downtimeSeconds)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}