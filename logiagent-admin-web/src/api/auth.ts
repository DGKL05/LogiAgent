import request from './request'
import type { LoginResponse } from '@/types/api'

export function loginApi(data: { username: string; password: string }) {
  return request.post<LoginResponse, LoginResponse>('/api/auth/login', data)
}

export function logoutApi() {
  return request.post<unknown, unknown>('/api/auth/logout')
}

export function getMeApi() {
  return request.get<LoginResponse, LoginResponse>('/api/auth/me')
}
