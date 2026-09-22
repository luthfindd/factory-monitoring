import KpiCard from '../components/KpiCard'
import OeeTrendChart from '../components/OeeTrendChart'
import ProductionTrendChart from '../components/ProductionTrendChart'
import EquipmentStatusTable from '../components/EquipmentStatusTable'
import AlarmList from '../components/AlarmList'
import {
  useKpi,
  useOeeTrend,
  useEquipmentStatus,
  useProductionTrend,
  useRecentAlarms,
} from '../hooks/useDashboardData'

function formatDuration(seconds: number) {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  return `${h}h ${m}m`
}

export default function DashboardPage() {
  const kpi = useKpi()
  const oeeTrend = useOeeTrend()
  const equipmentStatus = useEquipmentStatus()
  const productionTrend = useProductionTrend()
  const alarms = useRecentAlarms()

  function logout() {
    localStorage.removeItem('token')
    window.location.href = '/login'
  }

  return (
    <div className="min-h-screen bg-slate-950 text-white p-6 space-y-4">
      <div className="flex justify-between items-center">
        <h1 className="text-xl font-semibold">Factory Overview</h1>
        <button onClick={logout} className="text-sm text-slate-400 hover:text-white">
          Logout
        </button>
      </div>

      {kpi.isLoading && <p className="text-slate-400">Memuat data...</p>}
      {kpi.isError && <p className="text-red-400">Gagal memuat KPI</p>}

      {kpi.data && (
        <div className="flex flex-wrap gap-4">
          <KpiCard label="Overall OEE" value={`${kpi.data.oee}%`} target={`${kpi.data.oeeTarget}%`} />
          <KpiCard
            label="Availability"
            value={`${kpi.data.availability}%`}
            target={`${kpi.data.availabilityTarget}%`}
            accent="text-green-400"
          />
          <KpiCard
            label="Performance"
            value={`${kpi.data.performance}%`}
            target={`${kpi.data.performanceTarget}%`}
            accent="text-yellow-400"
          />
          <KpiCard
            label="Quality"
            value={`${kpi.data.quality}%`}
            target={`${kpi.data.qualityTarget}%`}
            accent="text-purple-400"
          />
          <KpiCard label="Running Time" value={formatDuration(kpi.data.runningTimeSeconds)} />
          <KpiCard label="Downtime" value={formatDuration(kpi.data.downtimeSeconds)} accent="text-red-400" />
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {oeeTrend.data && <OeeTrendChart data={oeeTrend.data} />}
        {productionTrend.data && <ProductionTrendChart data={productionTrend.data} />}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {equipmentStatus.data && <EquipmentStatusTable data={equipmentStatus.data} />}
        {alarms.data && <AlarmList data={alarms.data} />}
      </div>
    </div>
  )
}