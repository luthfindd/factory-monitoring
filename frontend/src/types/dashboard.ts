export interface EquipmentResponse {
  id: number
  name: string
  type: string
  status: 'RUNNING' | 'IDLE' | 'FAULT' | 'STOPPED'
  updatedAt: string
}

export interface AlarmResponse {
  id: number
  equipmentId: number
  equipmentName: string
  area: string
  message: string
  level: 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW'
  raisedAt: string
  acknowledgedAt: string | null
}

export interface DashboardKpiResponse {
  oee: number
  oeeTarget: number
  availability: number
  availabilityTarget: number
  performance: number
  performanceTarget: number
  quality: number
  qualityTarget: number
  runningTimeSeconds: number
  downtimeSeconds: number
}

export interface OeeTrendPointResponse {
  recordedAt: string
  oee: number
}

export interface EquipmentStatusResponse {
  equipmentId: number
  name: string
  status: string
  runningTimeSeconds: number
  downtimeSeconds: number
}

export interface ProductionTrendPointResponse {
  date: string
  plannedKg: number
  actualKg: number
  oeePercent: number
}

export interface ProductionSummaryResponse {
  plannedKg: number
  actualKg: number
  goodKg: number
  rejectKg: number
  yieldPercent: number
}