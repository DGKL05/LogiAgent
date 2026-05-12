import request from './request'
import type { PageResult, StationItem } from '@/types/api'

export function getStations(params: Record<string, unknown>) {
  return request.get<PageResult<StationItem>, PageResult<StationItem>>('/api/admin/stations', { params })
}

export function getStationDetail(stationId: number) {
  return request.get<StationItem, StationItem>(`/api/admin/stations/${stationId}`)
}

export function createStation(data: Partial<StationItem>) {
  return request.post<StationItem, StationItem>('/api/admin/stations', data)
}

export function updateStation(stationId: number, data: Partial<StationItem>) {
  return request.put<StationItem, StationItem>(`/api/admin/stations/${stationId}`, data)
}

export function disableStation(stationId: number) {
  return request.delete<unknown, unknown>(`/api/admin/stations/${stationId}`)
}
