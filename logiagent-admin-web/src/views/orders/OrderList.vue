<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">订单管理</h1>
      <el-button :icon="Refresh" @click="fetchList">刷新</el-button>
    </div>
    <el-card class="filter-card" shadow="never">
      <el-form :model="query" inline>
        <el-form-item label="订单号"><el-input v-model="query.orderNo" clearable /></el-form-item>
        <el-form-item label="收件人"><el-input v-model="query.receiverName" clearable /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="query.receiverPhone" clearable /></el-form-item>
        <el-form-item label="状态"><el-input v-model="query.status" clearable /></el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form>
    </el-card>
    <el-card class="table-card" shadow="never">
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column prop="receiverName" label="收件人" />
        <el-table-column prop="receiverPhone" label="手机号" min-width="130" />
        <el-table-column prop="goodsName" label="货物" />
        <el-table-column prop="weight" label="重量" width="90" />
        <el-table-column prop="status" label="状态" width="130"><template #default="{ row }"><el-tag>{{ row.status }}</el-tag></template></el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
            <el-button link type="primary" @click="openStatus(row)">改状态</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-actions">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" layout="total, prev, pager, next, sizes" :total="total" @change="fetchList" />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="订单详情" width="620px">
      <el-descriptions :column="1" border>
        <el-descriptions-item v-for="(value, key) in current" :key="key" :label="key">{{ value }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="statusVisible" title="修改订单状态" width="420px">
      <el-input v-model="nextStatus" placeholder="CREATED / CANCELLED / FINISHED" />
      <template #footer>
        <el-button @click="statusVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStatus">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getOrderDetail, getOrders, updateOrderStatus } from '@/api/order'
import type { OrderItem } from '@/types/api'

const loading = ref(false)
const rows = ref<OrderItem[]>([])
const total = ref(0)
const current = ref<OrderItem | null>(null)
const detailVisible = ref(false)
const statusVisible = ref(false)
const nextStatus = ref('')
const query = reactive({ page: 1, size: 10, orderNo: '', receiverName: '', receiverPhone: '', status: '' })

async function fetchList() {
  loading.value = true
  try {
    const data = await getOrders(query)
    rows.value = data?.records || []
    total.value = data?.total || 0
  } finally {
    loading.value = false
  }
}

function search() {
  query.page = 1
  fetchList()
}

function reset() {
  Object.assign(query, { page: 1, size: 10, orderNo: '', receiverName: '', receiverPhone: '', status: '' })
  fetchList()
}

async function openDetail(row: OrderItem) {
  current.value = await getOrderDetail(row.orderNo)
  detailVisible.value = true
}

function openStatus(row: OrderItem) {
  current.value = row
  nextStatus.value = row.status || ''
  statusVisible.value = true
}

async function submitStatus() {
  if (!current.value?.orderNo || !nextStatus.value) return
  await updateOrderStatus(current.value.orderNo, nextStatus.value)
  ElMessage.success('状态已更新')
  statusVisible.value = false
  fetchList()
}

onMounted(fetchList)
</script>
