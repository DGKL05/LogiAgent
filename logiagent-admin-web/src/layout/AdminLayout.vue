<template>
  <el-container class="admin-shell">
    <el-aside width="248px" class="sidebar">
      <div class="brand">
        <div class="brand-mark">LA</div>
        <div>
          <div class="brand-title">LogiAgent</div>
          <div class="brand-subtitle">智能物流调度系统</div>
        </div>
      </div>
      <el-menu router :default-active="route.path" class="side-menu">
        <el-menu-item index="/dashboard"><el-icon><DataBoard /></el-icon><span>数据看板</span></el-menu-item>
        <el-menu-item index="/orders"><el-icon><Tickets /></el-icon><span>订单管理</span></el-menu-item>
        <el-menu-item index="/waybills"><el-icon><Van /></el-icon><span>运单管理</span></el-menu-item>
        <el-menu-item index="/exceptions"><el-icon><Warning /></el-icon><span>异常件管理</span></el-menu-item>
        <el-menu-item index="/stations"><el-icon><OfficeBuilding /></el-icon><span>网点管理</span></el-menu-item>
        <el-menu-item index="/routes"><el-icon><Connection /></el-icon><span>线路管理</span></el-menu-item>
        <el-menu-item index="/dispatch"><el-icon><Guide /></el-icon><span>调度任务</span></el-menu-item>
        <el-menu-item index="/agent/chat"><el-icon><ChatDotRound /></el-icon><span>Agent 问答</span></el-menu-item>
        <el-menu-item index="/agent/sessions"><el-icon><Clock /></el-icon><span>Agent 会话</span></el-menu-item>
        <el-menu-item index="/reports/daily"><el-icon><Document /></el-icon><span>物流日报</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="topbar">
        <div>
          <div class="topbar-title">{{ currentTitle }}</div>
          <div class="topbar-subtitle">Gateway 统一鉴权，管理接口需 ADMIN Token</div>
        </div>
        <div class="topbar-actions">
          <el-tag type="success" effect="plain">{{ auth.username || 'admin' }}</el-tag>
          <el-button :icon="SwitchButton" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { SwitchButton } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const currentTitle = computed(() => String(route.meta.title || '管理后台'))

async function handleLogout() {
  await auth.logout()
  router.replace('/login')
}
</script>

<style scoped>
.admin-shell {
  height: 100vh;
  background: #f3f6fa;
}

.sidebar {
  border-right: 1px solid #e4e9f2;
  background: #102033;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  height: 76px;
  padding: 0 18px;
  color: #fff;
}

.brand-mark {
  display: grid;
  width: 42px;
  height: 42px;
  place-items: center;
  border-radius: 8px;
  background: #28c76f;
  color: #0d1b2a;
  font-weight: 900;
}

.brand-title {
  font-size: 18px;
  font-weight: 800;
}

.brand-subtitle {
  margin-top: 2px;
  color: #a9b8c8;
  font-size: 12px;
}

.side-menu {
  border-right: 0;
  background: transparent;
}

.side-menu :deep(.el-menu-item) {
  margin: 3px 10px;
  border-radius: 6px;
  color: #c6d2df;
}

.side-menu :deep(.el-menu-item.is-active) {
  background: #1d3b59;
  color: #fff;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 76px;
  border-bottom: 1px solid #e4e9f2;
  background: #fff;
}

.topbar-title {
  color: #1f2d3d;
  font-size: 20px;
  font-weight: 700;
}

.topbar-subtitle {
  margin-top: 4px;
  color: #8492a6;
  font-size: 13px;
}

.topbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.main {
  height: calc(100vh - 76px);
  padding: 18px;
  overflow: auto;
}
</style>
