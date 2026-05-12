import request from './request'
import type { DashboardOverview, DashboardRisk, DashboardTrend } from '@/types/api'

export function getDashboardOverview() {
  return request.get<DashboardOverview, DashboardOverview>('/api/admin/dashboard/overview')
}

export function getDashboardTrends(params: { startDate?: string; endDate?: string }) {
  return request.get<DashboardTrend[], DashboardTrend[]>('/api/admin/dashboard/trends', { params })
}

export function getDashboardRisks() {
  return request.get<DashboardRisk, DashboardRisk>('/api/admin/dashboard/risks')
}
