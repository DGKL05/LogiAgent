import request from './request'
import type { DispatchSuggestion, DispatchTask, PageResult, StationLoad } from '@/types/api'

export function getDispatchTasks(params: Record<string, unknown>) {
  return request.get<PageResult<DispatchTask>, PageResult<DispatchTask>>('/api/admin/dispatch/tasks', { params })
}

export function getDispatchTaskDetail(taskNo: string) {
  return request.get<DispatchTask, DispatchTask>(`/api/admin/dispatch/tasks/${taskNo}`)
}

export function createDispatchTask(data: Partial<DispatchTask>) {
  return request.post<DispatchTask, DispatchTask>('/api/admin/dispatch/tasks', data)
}

export function updateDispatchTaskStatus(taskNo: string, status: string) {
  return request.put<unknown, unknown>(`/api/admin/dispatch/tasks/${taskNo}/status`, { status })
}

export function getLoadRanking() {
  return request.get<StationLoad[], StationLoad[]>('/api/admin/dispatch/stations/load-ranking')
}

export function getDispatchSuggestions() {
  return request.get<DispatchSuggestion[], DispatchSuggestion[]>('/api/admin/dispatch/suggestions')
}
