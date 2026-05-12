import request from './request'
import type { ExceptionStatistics, PageResult, WaybillItem } from '@/types/api'

export function getWaybills(params: Record<string, unknown>) {
  return request.get<PageResult<WaybillItem>, PageResult<WaybillItem>>('/api/admin/waybills', { params })
}

export function getWaybillDetail(waybillNo: string) {
  return request.get<WaybillItem, WaybillItem>(`/api/admin/waybills/${waybillNo}`)
}

export function updateWaybillStatus(waybillNo: string, status: string) {
  return request.put<unknown, unknown>(`/api/admin/waybills/${waybillNo}/status`, { status })
}

export function markWaybillException(waybillNo: string, data: { exceptionType: string; exceptionReason?: string }) {
  return request.post<unknown, unknown>(`/api/admin/waybills/${waybillNo}/exception`, data)
}

export function resolveWaybillException(waybillNo: string) {
  return request.put<unknown, unknown>(`/api/admin/waybills/${waybillNo}/exception/resolve`)
}

export function getExceptions(params: Record<string, unknown>) {
  return request.get<PageResult<WaybillItem>, PageResult<WaybillItem>>('/api/admin/exceptions', { params })
}

export function getExceptionDetail(waybillNo: string) {
  return request.get<WaybillItem, WaybillItem>(`/api/admin/exceptions/${waybillNo}`)
}

export function resolveException(waybillNo: string) {
  return request.put<unknown, unknown>(`/api/admin/exceptions/${waybillNo}/resolve`)
}

export function getExceptionStatistics() {
  return request.get<ExceptionStatistics, ExceptionStatistics>('/api/admin/exceptions/statistics')
}
