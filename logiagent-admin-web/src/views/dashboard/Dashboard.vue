<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">数据看板</h1>
      <el-button :icon="Refresh" @click="loadData">刷新</el-button>
    </div>

    <el-row :gutter="14">
      <el-col v-for="item in metrics" :key="item.label" :span="6">
        <el-card shadow="never" class="metric-card">
          <div class="metric-label">{{ item.label }}</div>
          <div class="metric-value">{{ item.value ?? 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="14">
      <el-col :span="16">
        <el-card shadow="never">
          <template #header>近 7 日趋势</template>
          <div ref="trendRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>风险概览</template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="失败 Tool 调用">{{ risk.failedToolCallCount ?? 0 }}</el-descriptions-item>
            <el-descriptions-item label="不可用字段">
              <el-tag v-for="field in risk.unavailableFields || []" :key="field" class="tag">{{ field }}</el-tag>
              <span v-if="!risk.unavailableFields?.length">无</span>
            </el-descriptions-item>
          </el-descriptions>
          <el-divider />
          <div class="pre-wrap">
            <p v-for="suggestion in risk.riskSuggestions || []" :key="suggestion">{{ suggestion }}</p>
            <p v-if="!risk.riskSuggestions?.length" class="muted">暂无风险建议</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import { Refresh } from '@element-plus/icons-vue'
import { getDashboardOverview, getDashboardRisks, getDashboardTrends } from '@/api/dashboard'
import type { DashboardOverview, DashboardRisk, DashboardTrend } from '@/types/api'

const overview = ref<DashboardOverview>({})
const trends = ref<DashboardTrend[]>([])
const risk = ref<DashboardRisk>({})
const trendRef = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null

const metrics = computed(() => [
  { label: '总订单数', value: overview.value.totalOrderCount },
  { label: '今日订单数', value: overview.value.todayOrderCount },
  { label: '总运单数', value: overview.value.totalWaybillCount },
  { label: '今日运单数', value: overview.value.todayWaybillCount },
  { label: '异常运单数', value: overview.value.exceptionWaybillCount },
  { label: '已签收数量', value: overview.value.signedWaybillCount },
  { label: '轨迹更新数', value: overview.value.trackUpdateCount },
  { label: 'Agent 会话数', value: overview.value.agentSessionCount }
])

function renderChart() {
  if (!trendRef.value) return
  chart ||= echarts.init(trendRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { left: 36, right: 20, top: 42, bottom: 28 },
    xAxis: { type: 'category', data: trends.value.map((item) => item.date) },
    yAxis: { type: 'value' },
    series: [
      { name: '订单', type: 'line', smooth: true, data: trends.value.map((item) => item.orderCount || 0) },
      { name: '运单', type: 'line', smooth: true, data: trends.value.map((item) => item.waybillCount || 0) },
      { name: '轨迹', type: 'bar', data: trends.value.map((item) => item.trackUpdateCount || 0) }
    ]
  })
}

async function loadData() {
  const endDate = dayjs().format('YYYY-MM-DD')
  const startDate = dayjs().subtract(6, 'day').format('YYYY-MM-DD')
  const [overviewData, trendData, riskData] = await Promise.all([
    getDashboardOverview(),
    getDashboardTrends({ startDate, endDate }),
    getDashboardRisks()
  ])
  overview.value = overviewData || {}
  trends.value = trendData || []
  risk.value = riskData || {}
  await nextTick()
  renderChart()
}

onMounted(loadData)
</script>

<style scoped>
.metric-card {
  margin-bottom: 14px;
}

.metric-label {
  color: #7a8797;
  font-size: 13px;
}

.metric-value {
  margin-top: 10px;
  color: #102033;
  font-size: 30px;
  font-weight: 800;
}

.chart {
  height: 360px;
}

.tag {
  margin: 2px 4px 2px 0;
}
</style>
