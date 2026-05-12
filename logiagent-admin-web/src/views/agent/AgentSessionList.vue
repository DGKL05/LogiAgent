<template>
  <div class="page">
    <div class="page-header"><h1 class="page-title">Agent 会话管理</h1><el-button :icon="Refresh" @click="loadAll">刷新</el-button></div>
    <el-row :gutter="14">
      <el-col :span="6" v-for="item in statCards" :key="item.label"><el-card shadow="never"><div class="muted">{{ item.label }}</div><h2>{{ item.value ?? 0 }}</h2></el-card></el-col>
    </el-row>
    <el-card class="filter-card" shadow="never">
      <el-form :model="query" inline>
        <el-form-item label="Session"><el-input v-model="query.sessionId" clearable /></el-form-item>
        <el-form-item label="User ID"><el-input-number v-model="query.userId" :min="1" /></el-form-item>
        <el-form-item label="意图"><el-input v-model="query.intent" clearable /></el-form-item>
        <el-button type="primary" @click="search">查询</el-button><el-button @click="reset">重置</el-button>
      </el-form>
    </el-card>
    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="sessionId" label="Session ID" min-width="210" />
        <el-table-column prop="userId" label="用户" width="90" />
        <el-table-column prop="intent" label="意图" min-width="170" />
        <el-table-column prop="question" label="问题" min-width="260" show-overflow-tooltip />
        <el-table-column prop="createTime" label="时间" min-width="170" />
        <el-table-column label="操作" width="150"><template #default="{ row }"><el-button link type="primary" @click="openDetail(row)">详情</el-button><el-button link type="primary" @click="openLogs(row)">Tool 日志</el-button></template></el-table-column>
      </el-table>
      <div class="table-actions"><el-pagination v-model:current-page="query.page" v-model:page-size="query.size" layout="total, prev, pager, next, sizes" :total="total" @change="fetchList" /></div>
    </el-card>
    <el-dialog v-model="detailVisible" title="会话详情" width="720px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="问题">{{ current?.question }}</el-descriptions-item>
        <el-descriptions-item label="意图">{{ current?.intent }}</el-descriptions-item>
        <el-descriptions-item label="回答"><div class="pre-wrap">{{ current?.answer }}</div></el-descriptions-item>
      </el-descriptions>
    </el-dialog>
    <el-dialog v-model="logsVisible" title="Tool 调用日志" width="920px">
      <el-table :data="logs">
        <el-table-column prop="toolName" label="Tool" min-width="180" />
        <el-table-column prop="success" label="成功" width="90"><template #default="{ row }"><el-tag :type="row.success ? 'success' : 'danger'">{{ row.success ? '是' : '否' }}</el-tag></template></el-table-column>
        <el-table-column prop="costMs" label="耗时(ms)" width="110" />
        <el-table-column prop="errorMsg" label="错误" min-width="180" show-overflow-tooltip />
        <el-table-column prop="createTime" label="时间" min-width="170" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { getAgentSessionDetail, getAgentSessions, getAgentStatistics, getAgentToolLogs } from '@/api/agent'
import type { AgentSession, AgentToolLog, AgentUsageStatistics } from '@/types/api'

const rows = ref<AgentSession[]>([])
const logs = ref<AgentToolLog[]>([])
const statistics = ref<AgentUsageStatistics>({})
const total = ref(0)
const loading = ref(false)
const current = ref<AgentSession | null>(null)
const detailVisible = ref(false)
const logsVisible = ref(false)
const query = reactive<{ page: number; size: number; sessionId: string; intent: string; userId?: number }>({ page: 1, size: 10, sessionId: '', intent: '' })
const statCards = computed(() => [
  { label: '总会话数', value: statistics.value.totalSessionCount },
  { label: '今日会话', value: statistics.value.todaySessionCount },
  { label: 'Tool 调用', value: statistics.value.totalToolCallCount },
  { label: '失败调用', value: statistics.value.failedToolCallCount }
])
async function fetchList() { loading.value = true; try { const data = await getAgentSessions(query); rows.value = data?.records || []; total.value = data?.total || 0 } finally { loading.value = false } }
async function loadAll() { statistics.value = await getAgentStatistics(); await fetchList() }
function search() { query.page = 1; fetchList() }
function reset() { Object.assign(query, { page: 1, size: 10, sessionId: '', intent: '', userId: undefined }); fetchList() }
async function openDetail(row: AgentSession) { current.value = await getAgentSessionDetail(row.sessionId); detailVisible.value = true }
async function openLogs(row: AgentSession) { logs.value = await getAgentToolLogs(row.sessionId); logsVisible.value = true }
onMounted(loadAll)
</script>
