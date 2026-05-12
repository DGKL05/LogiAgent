import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('@/layout/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'dashboard', component: () => import('@/views/dashboard/Dashboard.vue'), meta: { title: '数据看板' } },
      { path: 'orders', name: 'orders', component: () => import('@/views/orders/OrderList.vue'), meta: { title: '订单管理' } },
      { path: 'waybills', name: 'waybills', component: () => import('@/views/waybills/WaybillList.vue'), meta: { title: '运单管理' } },
      { path: 'exceptions', name: 'exceptions', component: () => import('@/views/exceptions/ExceptionList.vue'), meta: { title: '异常件管理' } },
      { path: 'stations', name: 'stations', component: () => import('@/views/stations/StationList.vue'), meta: { title: '网点管理' } },
      { path: 'routes', name: 'routes', component: () => import('@/views/routes/RouteList.vue'), meta: { title: '线路管理' } },
      { path: 'dispatch', name: 'dispatch', component: () => import('@/views/dispatch/DispatchTaskList.vue'), meta: { title: '调度任务' } },
      { path: 'agent/chat', name: 'agent-chat', component: () => import('@/views/agent/AgentChat.vue'), meta: { title: 'Agent 问答' } },
      { path: 'agent/sessions', name: 'agent-sessions', component: () => import('@/views/agent/AgentSessionList.vue'), meta: { title: 'Agent 会话' } },
      { path: 'reports/daily', name: 'daily-report', component: () => import('@/views/reports/DailyReport.vue'), meta: { title: '物流日报' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.public && auth.isLoggedIn) {
    return '/dashboard'
  }
  if (!to.meta.public && !auth.isLoggedIn) {
    return `/login?redirect=${encodeURIComponent(to.fullPath)}`
  }
  return true
})

export default router
