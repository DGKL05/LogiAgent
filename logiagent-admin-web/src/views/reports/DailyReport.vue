<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">物流日报</h1>
      <div>
        <el-button :icon="DocumentCopy" :disabled="!report" @click="copyReport">复制日报</el-button>
        <el-button type="primary" :loading="loading" @click="generateReport">生成今天的物流日报</el-button>
      </div>
    </div>
    <el-card shadow="never">
      <div v-if="report" class="report pre-wrap">{{ report }}</div>
      <el-empty v-else description="点击按钮生成 Markdown 物流运营日报" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { DocumentCopy } from '@element-plus/icons-vue'
import { chatWithAgent } from '@/api/agent'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const loading = ref(false)
const report = ref('')

async function generateReport() {
  loading.value = true
  try {
    const data = await chatWithAgent({ userId: auth.userId, message: '生成今天的物流日报' })
    report.value = data.answer
  } finally {
    loading.value = false
  }
}

async function copyReport() {
  await navigator.clipboard.writeText(report.value)
  ElMessage.success('日报已复制')
}
</script>

<style scoped>
.report {
  min-height: 520px;
  font-family: "Microsoft YaHei", "PingFang SC", sans-serif;
}
</style>
