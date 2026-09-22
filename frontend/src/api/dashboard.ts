import client from './client'
import type {
  DashboardKpiResponse,
  OeeTrendPointResponse,
  EquipmentStatusResponse,
  ProductionTrendPointResponse,
  ProductionSummaryResponse,
  AlarmResponse,
} from '../types/dashboard'

export const getKpi = () => client.get<DashboardKpiResponse>('/dashboard/kpi').then((r) => r.data)

export const getOeeTrend = () =>
  client.get<OeeTrendPointResponse[]>('/dashboard/oee-trend').then((r) => r.data)

export const getEquipmentStatus = () =>
  client.get<EquipmentStatusResponse[]>('/dashboard/equipment-status').then((r) => r.data)

export const getProductionTrend = () =>
  client.get<ProductionTrendPointResponse[]>('/dashboard/production-trend').then((r) => r.data)

export const getProductionSummary = () =>
  client.get<ProductionSummaryResponse>('/dashboard/production-summary').then((r) => r.data)

export const getRecentAlarms = (limit = 5) =>
  client.get<AlarmResponse[]>('/alarms', { params: { limit } }).then((r) => r.data)