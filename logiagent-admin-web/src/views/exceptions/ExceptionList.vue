<template>
  <div class="page">
    <div class="page-header"><h1 class="page-title">异常件管理</h1><el-button :icon="Refresh" @click="loadAll">刷新</el-button></div>
    <el-row :gutter="14">
      <el-col :span="6" v-for="item in statCards" :key="item.label">
        <el-card shadow="never"><div class="muted">{{ item.label }}</div><h2>{{ item.value ?? 0 }}</h2></el-card>
      </el-col>
    </el-row>
    <el-card class="filter-card" shadow="never">
      <el-form :model="query" inline>
        <el-form-item label="运单号"><el-input v-model="query.waybillNo" clearable /></el-form-item>
        <el-form-item label="订单号"><el-input v-model="query.orderNo" clearable /></el-form-item>
        <el-form-item label="异常类型"><el-input v-model="query.exceptionType" clearable /></el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form>
    </el-card>
    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="waybillNo" label="运单号" min-width="180" />
        <el-table-column prop="orderNo" label="订单号" min-width="170" />
        <el-table-column prop="exceptionType" label="异常类型" width="150"><template #default="{ row }"><el-tag type="danger">{{ row.exceptionType }}</el-tag></template></el-table-column>
        <el-table-column prop="exceptionReason" label="异常原因" min-width="220" />
        <el-table-column prop="updateTime" label="更新时间" min-width="170" />
        <el-table-column label="操作" width="150"><template #default="{ row }"><el-button link type="primary" @click="openDetail(row)">详情</el-button><el-button link type="success" @click="resolve(row)">解除</el-button></template></el-table-column>
      </el-table>
      <div class="table-actions"><el-pagination v-model:current-page="query.page" v-model:page-size="query.size" layout="total, prev, pager, next, sizes" :total="total" @change="fetchList" /></div>
    </el-card>
    <el-dialog v-model="detailVisible" title="异常件详情" width="620px"><el-descriptions :column="1" border><el-descriptions-item v-for="(value, key) in current" :key="key" :label="key">{{ value }}</el-descriptions-item></el-descriptions></el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getExceptionDetail, getExceptions, getExceptionStatistics, resolveException } from '@/api/waybill'
import type { ExceptionStatistics, WaybillItem } from '@/types/api'

const loading = ref(false)
const rows = ref<WaybillItem[]>([])
const total = ref(0)
const current = ref<WaybillItem | null>(null)
const detailVisible = ref(false)
const statistics = ref<ExceptionStatistics>({})
const query = reactive({ page: 1, size: 10, waybillNo: '', orderNo: '', exceptionType: '' })
const statCards = computed(() => [
  { label: '异常总数', value: statistics.value.totalExceptionCount },
  { label: '超时件', value: statistics.value.timeoutExceptionCount },
  { label: '拒收件', value: statistics.value.rejectedExceptionCount },
  { label: '遗失件', value: statistics.value.lostExceptionCount }
])

async function fetchList() {
  loading.value = true
  try {
    const data = await getExceptions(query)
    rows.value = data?.records || []
    total.value = data?.total || 0
  } finally {
    loading.value = false
  }
}
async function loadAll() { statistics.value = await getExceptionStatistics(); await fetchList() }
function search() { query.page = 1; fetchList() }
function reset() { Object.assign(query, { page: 1, size: 10, waybillNo: '', orderNo: '', exceptionType: '' }); fetchList() }
async function openDetail(row: WaybillItem) { current.value = await getExceptionDetail(row.waybillNo); detailVisible.value = true }
async function resolve(row: WaybillItem) { await resolveException(row.waybillNo); ElMessage.success('异常已解除'); loadAll() }
onMounted(loadAll)
</script>
