<template>
  <div class="login-page">
    <section class="login-panel">
      <div class="login-copy">
        <p class="eyebrow">LogiAgent Admin</p>
        <h1>智能物流调度系统</h1>
        <p>统一查看订单、运单、调度、路线和 Agent 分析结果。</p>
      </div>
      <el-card class="login-card" shadow="never">
        <h2>管理员登录</h2>
        <el-form :model="form" label-position="top" @keyup.enter="handleLogin">
          <el-form-item label="用户名">
            <el-input v-model="form.username" placeholder="admin" size="large" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" placeholder="admin123456" size="large" show-password />
          </el-form-item>
          <el-alert title="开发环境账号：admin / admin123456" type="info" :closable="false" show-icon />
          <el-button class="login-button" type="primary" size="large" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form>
      </el-card>
    </section>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const loading = ref(false)
const form = reactive({
  username: 'admin',
  password: 'admin123456'
})

async function handleLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    await auth.login(form.username, form.password)
    ElMessage.success('登录成功')
    router.replace(String(route.query.redirect || '/dashboard'))
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: grid;
  min-height: 100vh;
  place-items: center;
  background:
    linear-gradient(135deg, rgba(16, 32, 51, 0.92), rgba(18, 73, 91, 0.82)),
    radial-gradient(circle at 20% 20%, rgba(40, 199, 111, 0.35), transparent 34%),
    #102033;
}

.login-panel {
  display: grid;
  width: min(1040px, calc(100vw - 48px));
  grid-template-columns: 1.1fr 420px;
  gap: 32px;
  align-items: center;
}

.login-copy {
  color: #fff;
}

.eyebrow {
  color: #55d98d;
  font-size: 14px;
  font-weight: 700;
  text-transform: uppercase;
}

h1 {
  margin: 8px 0 16px;
  font-size: 48px;
  line-height: 1.12;
}

.login-copy p:last-child {
  max-width: 520px;
  color: #d7e1ec;
  font-size: 18px;
  line-height: 1.8;
}

.login-card {
  border: 0;
  border-radius: 8px;
}

.login-card h2 {
  margin: 0 0 20px;
  color: #1f2d3d;
}

.login-button {
  width: 100%;
  margin-top: 18px;
}
</style>
