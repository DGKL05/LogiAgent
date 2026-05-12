import request from './request'
import type { OrderItem, PageResult } from '@/types/api'

export function getOrders(params: Record<string, unknown>) {
  return request.get<PageResult<OrderItem>, PageResult<OrderItem>>('/api/admin/orders', { params })
}

export function getOrderDetail(orderNo: string) {
  return request.get<OrderItem, OrderItem>(`/api/admin/orders/${orderNo}`)
}

export function updateOrderStatus(orderNo: string, status: string) {
  return request.put<unknown, unknown>(`/api/admin/orders/${orderNo}/status`, { status })
}

export function getOrderStatistics() {
  return request.get<Record<string, unknown>, Record<string, unknown>>('/api/admin/orders/statistics')
}
