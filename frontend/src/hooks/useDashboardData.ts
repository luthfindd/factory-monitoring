import { useQuery } from '@tanstack/react-query'
import {
  getKpi,
  getOeeTrend,
  getEquipmentStatus,
  getProductionTrend,
  getProductionSummary,
  getRecentAlarms,
} from '../api/dashboard'

const REFRESH_MS = 30_000

export function useKpi() {
  return useQuery({ queryKey: ['kpi'], queryFn: getKpi, refetchInterval: REFRESH_MS })
}

export function useOeeTrend() {
  return useQuery({ queryKey: ['oee-trend'], queryFn: getOeeTrend, refetchInterval: REFRESH_MS })
}

export function useEquipmentStatus() {
  return useQuery({
    queryKey: ['equipment-status'],
    queryFn: getEquipmentStatus,
    refetchInterval: REFRESH_MS,
  })
}

export function useProductionTrend() {
  return useQuery({
    queryKey: ['production-trend'],
    queryFn: getProductionTrend,
    refetchInterval: REFRESH_MS,
  })
}

export function useProductionSummary() {
  return useQuery({
    queryKey: ['production-summary'],
    queryFn: getProductionSummary,
    refetchInterval: REFRESH_MS,
  })
}

export function useRecentAlarms() {
  return useQuery({
    queryKey: ['alarms'],
    queryFn: () => getRecentAlarms(5),
    refetchInterval: REFRESH_MS,
  })
}