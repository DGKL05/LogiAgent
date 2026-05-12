<template>
  <div class="page">
    <div class="page-header"><h1 class="page-title">运单管理</h1><el-button :icon="Refresh" @click="fetchList">刷新</el-button></div>
    <el-card class="filter-card" shadow="never">
      <el-form :model="query" inline>
        <el-form-item label="运单号"><el-input v-model="query.waybillNo" clearable /></el-form-item>
        <el-form-item label="订单号"><el-input v-model="query.orderNo" clearable /></el-form-item>
        <el-form-item label="状态"><el-input v-model="query.currentStatus" clearable /></el-form-item>
        <el-form-item label="异常类型"><el-input v-model="query.exceptionType" clearable /></el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form>
    </el-card>
    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="waybillNo" label="运单号" min-width="180" />
        <el-table-column prop="orderNo" label="订单号" min-width="170" />
        <el-table-column prop="currentStatus" label="状态" width="140"><template #default="{ row }"><el-tag>{{ row.currentStatus }}</el-tag></template></el-table-column>
        <el-table-column prop="exceptionType" label="异常类型" width="130" />
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
            <el-button link type="primary" @click="openStatus(row)">改状态</el-button>
            <el-button link type="danger" @click="openException(row)">标异常</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-actions"><el-pagination v-model:current-page="query.page" v-model:page-size="query.size" layout="total, prev, pager, next, sizes" :total="total" @change="fetchList" /></div>
    </el-card>

    <el-dialog v-model="detailVisible" title="运单详情" width="620px"><el-descriptions :column="1" border><el-descriptions-item v-for="(value, key) in current" :key="key" :label="key">{{ value }}</el-descriptions-item></el-descriptions></el-dialog>
    <el-dialog v-model="statusVisible" title="修改运单状态" width="420px">
      <el-input v-model="nextStatus" placeholder="TRANSPORTING / SIGNED / EXCEPTION" />
      <template #footer><el-button @click="statusVisible = false">取消</el-button><el-button type="primary" @click="submitStatus">保存</el-button></template>
    </el-dialog>
    <el-dialog v-model="exceptionVisible" title="标记异常件" width="460px">
      <el-form :model="exceptionForm" label-width="90px">
        <el-form-item label="异常类型"><el-input v-model="exceptionForm.exceptionType" placeholder="TIMEOUT" /></el-form-item>
        <el-form-item label="异常原因"><el-input v-model="exceptionForm.exceptionReason" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="exceptionVisible = false">取消</el-button><el-button type="danger" @click="submitException">标记</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getWaybillDetail, getWaybills, markWaybillException, updateWaybillStatus } from '@/api/waybill'
import type { WaybillItem } from '@/types/api'

const loading = ref(false)
const rows = ref<WaybillItem[]>([])
const total = ref(0)
const current = ref<WaybillItem | null>(null)
const detailVisible = ref(false)
const statusVisible = ref(false)
const exceptionVisible = ref(false)
const nextStatus = ref('')
const exceptionForm = reactive({ exceptionType: '', exceptionReason: '' })
const query = reactive({ page: 1, size: 10, waybillNo: '', orderNo: '', currentStatus: '', exceptionType: '' })

async function fetchList() {
  loading.value = true
  try {
    const data = await getWaybills(query)
    rows.value = data?.records || []
    total.value = data?.total || 0
  } finally {
    loading.value = false
  }
}

function search() { query.page = 1; fetchList() }
function reset() { Object.assign(query, { page: 1, size: 10, waybillNo: '', orderNo: '', currentStatus: '', exceptionType: '' }); fetchList() }
async function openDetail(row: WaybillItem) { current.value = await getWaybillDetail(row.waybillNo); detailVisible.value = true }
function openStatus(row: WaybillItem) { current.value = row; nextStatus.value = row.currentStatus || ''; statusVisible.value = true }
function openException(row: WaybillItem) { current.value = row; Object.assign(exceptionForm, { exceptionType: 'TIMEOUT', exceptionReason: '' }); exceptionVisible.value = true }
async function submitStatus() {
  if (!current.value?.waybillNo || !nextStatus.value) return
  await updateWaybillStatus(current.value.waybillNo, nextStatus.value)
  ElMessage.success('状态已更新')
  statusVisible.value = false
  fetchList()
}
async function submitException() {
  if (!current.value?.waybillNo || !exceptionForm.exceptionType) return
  await markWaybillException(current.value.waybillNo, exceptionForm)
  ElMessage.success('异常已标记')
  exceptionVisible.value = false
  fetchList()
}
onMounted(fetchList)
</script>
