export interface Result<T> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  size: number
}

export interface LoginResponse {
  token: string
  tokenType: string
  expiresIn: number
  userId: number
  username: string
  roles: string[]
}

export interface DashboardOverview {
  totalOrderCount?: number
  todayOrderCount?: number
  totalWaybillCount?: number
  todayWaybillCount?: number
  exceptionWaybillCount?: number
  signedWaybillCount?: number
  trackUpdateCount?: number
  activeWaybillCount?: number
  overloadedStationCount?: number
  agentSessionCount?: number
  unavailableFields?: string[]
}

export interface DashboardTrend {
  date: string
  orderCount?: number
  waybillCount?: number
  trackUpdateCount?: number
}

export interface DashboardRisk {
  exceptionTypeCountMap?: Record<string, number>
  highLoadStations?: StationLoad[]
  failedToolCallCount?: number
  riskSuggestions?: string[]
  unavailableFields?: string[]
}

export interface OrderItem {
  id?: number
  orderNo: string
  senderId?: number
  receiverName?: string
  receiverPhone?: string
  receiverAddress?: string
  goodsName?: string
  weight?: number
  status?: string
  createTime?: string
  updateTime?: string
}

export interface WaybillItem {
  id?: number
  waybillNo: string
  orderNo?: string
  currentStatus?: string
  currentNodeId?: number
  exceptionType?: string
  exceptionReason?: string
  expectedArriveTime?: string
  actualArriveTime?: string
  createTime?: string
  updateTime?: string
}

export interface StationItem {
  id?: number
  stationName: string
  province?: string
  city?: string
  address?: string
  longitude?: number
  latitude?: number
  status?: string
  createTime?: string
  updateTime?: string
}

export interface RouteItem {
  id?: number
  startStationId?: number
  startStationName?: string
  endStationId?: number
  endStationName?: string
  distance?: number
  estimatedHours?: number
  cost?: number
  status?: string
  createTime?: string
  updateTime?: string
}

export interface RoutePlanResponse {
  provider?: string
  strategy?: string
  pathStationIds?: number[]
  pathStationNames?: string[]
  totalDistance?: number
  totalDuration?: number
  totalCost?: number
  toll?: number
  trafficCondition?: string
  routeSummary?: string
  fallbackUsed?: boolean
  fallbackReason?: string
  steps?: Array<Record<string, unknown>>
}

export interface DispatchTask {
  id?: number
  taskNo?: string
  waybillNo?: string
  courierId?: number
  stationId?: number
  taskStatus?: string
  assignTime?: string
  finishTime?: string
  createTime?: string
  updateTime?: string
}

export interface StationLoad {
  stationId?: number
  stationName?: string
  pendingWaybillCount?: number
  exceptionWaybillCount?: number
  dispatchTaskCount?: number
  courierCount?: number
  driverCount?: number
  loadLevel?: string
}

export interface DispatchSuggestion {
  stationId?: number
  stationName?: string
  loadLevel?: string
  suggestion?: string
}

export interface AgentChatResponse {
  sessionId: string
  intent: string
  answer: string
  toolCalls?: string[]
}

export interface AgentSession {
  id?: number
  sessionId: string
  userId?: number
  question?: string
  intent?: string
  answer?: string
  createTime?: string
}

export interface AgentToolLog {
  id?: number
  sessionId?: string
  toolName?: string
  requestParams?: string
  responseResult?: string
  success?: boolean
  errorMsg?: string
  costMs?: number
  createTime?: string
}

export interface AgentUsageStatistics {
  totalSessionCount?: number
  todaySessionCount?: number
  intentCountMap?: Record<string, number>
  totalToolCallCount?: number
  failedToolCallCount?: number
}

export interface ExceptionStatistics {
  totalExceptionCount?: number
  timeoutExceptionCount?: number
  rejectedExceptionCount?: number
  lostExceptionCount?: number
  exceptionTypeCountMap?: Record<string, number>
}
