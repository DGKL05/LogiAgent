import request from './request'
import type { AgentChatResponse, AgentSession, AgentToolLog, AgentUsageStatistics, PageResult } from '@/types/api'

export function chatWithAgent(data: { userId?: number; message: string }) {
  return request.post<AgentChatResponse, AgentChatResponse>('/api/agent/chat', data)
}

export function getAgentSessions(params: Record<string, unknown>) {
  return request.get<PageResult<AgentSession>, PageResult<AgentSession>>('/api/admin/agent/sessions', { params })
}

export function getAgentSessionDetail(sessionId: string) {
  return request.get<AgentSession, AgentSession>(`/api/admin/agent/sessions/${sessionId}`)
}

export function getAgentToolLogs(sessionId: string) {
  return request.get<AgentToolLog[], AgentToolLog[]>(`/api/admin/agent/sessions/${sessionId}/tool-logs`)
}

export function getAgentStatistics() {
  return request.get<AgentUsageStatistics, AgentUsageStatistics>('/api/admin/agent/statistics')
}
