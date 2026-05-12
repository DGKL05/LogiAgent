import request from './request'
import type { PageResult, RouteItem, RoutePlanResponse } from '@/types/api'

export function getRoutes(params: Record<string, unknown>) {
  return request.get<PageResult<RouteItem>, PageResult<RouteItem>>('/api/admin/routes', { params })
}

export function getRouteDetail(routeId: number) {
  return request.get<RouteItem, RouteItem>(`/api/admin/routes/${routeId}`)
}

export function createRoute(data: Partial<RouteItem>) {
  return request.post<RouteItem, RouteItem>('/api/admin/routes', data)
}

export function updateRoute(routeId: number, data: Partial<RouteItem>) {
  return request.put<RouteItem, RouteItem>(`/api/admin/routes/${routeId}`, data)
}

export function disableRoute(routeId: number) {
  return request.delete<unknown, unknown>(`/api/admin/routes/${routeId}`)
}

export function planRoute(data: Record<string, unknown>) {
  return request.post<RoutePlanResponse, RoutePlanResponse>('/api/routes/plan', data)
}
