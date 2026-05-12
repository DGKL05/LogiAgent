<template>
  <div class="page">
    <div class="page-header"><h1 class="page-title">线路管理</h1><el-button type="primary" :icon="Plus" @click="openCreate">新增线路</el-button></div>
    <el-row :gutter="14">
      <el-col :span="15">
        <el-card class="filter-card" shadow="never">
          <el-form :model="query" inline>
            <el-form-item label="起点 ID"><el-input-number v-model="query.startStationId" :min="1" /></el-form-item>
            <el-form-item label="终点 ID"><el-input-number v-model="query.endStationId" :min="1" /></el-form-item>
            <el-button type="primary" @click="search">查询</el-button><el-button @click="reset">重置</el-button>
          </el-form>
        </el-card>
        <el-card shadow="never">
          <el-table v-loading="loading" :data="rows" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="startStationName" label="起点" />
            <el-table-column prop="endStationName" label="终点" />
            <el-table-column prop="distance" label="距离(km)" width="110" />
            <el-table-column prop="estimatedHours" label="耗时(h)" width="110" />
            <el-table-column prop="cost" label="成本" width="100" />
            <el-table-column prop="status" label="状态" width="90" />
            <el-table-column label="操作" width="150"><template #default="{ row }"><el-button link type="primary" @click="openEdit(row)">编辑</el-button><el-button link type="danger" @click="disable(row)">禁用</el-button></template></el-table-column>
          </el-table>
          <div class="table-actions"><el-pagination v-model:current-page="query.page" v-model:page-size="query.size" layout="total, prev, pager, next" :total="total" @change="fetchList" /></div>
        </el-card>
      </el-col>
      <el-col :span="9">
        <el-card shadow="never">
          <template #header>路线规划测试</template>
          <el-form :model="planForm" label-width="100px">
            <el-form-item label="起点网点"><el-input-number v-model="planForm.startStationId" :min="1" /></el-form-item>
            <el-form-item label="终点网点"><el-input-number v-model="planForm.endStationId" :min="1" /></el-form-item>
            <el-form-item label="Provider"><el-select v-model="planForm.provider"><el-option label="LOCAL_DIJKSTRA" value="LOCAL_DIJKSTRA" /><el-option label="BAIDU_DRIVING" value="BAIDU_DRIVING" /></el-select></el-form-item>
            <el-form-item label="策略"><el-select v-model="planForm.strategy"><el-option v-for="item in strategies" :key="item" :label="item" :value="item" /></el-select></el-form-item>
            <el-button type="primary" @click="submitPlan">规划路线</el-button>
          </el-form>
          <el-divider />
          <div v-if="planResult" class="pre-wrap">
            <p><b>路径：</b>{{ planResult.pathStationNames?.join(' -> ') || planResult.routeSummary }}</p>
            <p><b>距离：</b>{{ planResult.totalDistance }} km</p>
            <p><b>耗时：</b>{{ planResult.totalDuration }} min</p>
            <p><b>fallback：</b><el-tag :type="planResult.fallbackUsed ? 'warning' : 'success'">{{ planResult.fallbackUsed ? '是' : '否' }}</el-tag></p>
            <p v-if="planResult.fallbackReason"><b>原因：</b>{{ planResult.fallbackReason }}</p>
          </div>
          <el-empty v-else description="提交路线规划后显示结果" />
        </el-card>
      </el-col>
    </el-row>
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑线路' : '新增线路'" width="520px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="起点网点 ID"><el-input-number v-model="form.startStationId" :min="1" /></el-form-item>
        <el-form-item label="终点网点 ID"><el-input-number v-model="form.endStationId" :min="1" /></el-form-item>
        <el-form-item label="距离"><el-input-number v-model="form.distance" :precision="2" /></el-form-item>
        <el-form-item label="预计小时"><el-input-number v-model="form.estimatedHours" :precision="2" /></el-form-item>
        <el-form-item label="成本"><el-input-number v-model="form.cost" :precision="2" /></el-form-item>
        <el-form-item label="状态"><el-input v-model="form.status" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="submit">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { createRoute, disableRoute, getRoutes, planRoute, updateRoute } from '@/api/route'
import type { RouteItem, RoutePlanResponse } from '@/types/api'

const strategies = ['DISTANCE_FIRST', 'TIME_FIRST', 'COST_FIRST', 'BALANCED', 'AVOID_HIGHWAY', 'AVOID_TRAFFIC', 'HIGHWAY_FIRST', 'AVOID_FERRY']
const loading = ref(false)
const rows = ref<RouteItem[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const planResult = ref<RoutePlanResponse | null>(null)
const query = reactive<{ page: number; size: number; startStationId?: number; endStationId?: number }>({ page: 1, size: 10 })
const form = reactive<Partial<RouteItem>>({})
const planForm = reactive({ startStationId: 1, endStationId: 3, provider: 'LOCAL_DIJKSTRA', strategy: 'TIME_FIRST' })
async function fetchList() { loading.value = true; try { const data = await getRoutes(query); rows.value = data?.records || []; total.value = data?.total || 0 } finally { loading.value = false } }
function search() { query.page = 1; fetchList() }
function reset() { Object.assign(query, { page: 1, size: 10, startStationId: undefined, endStationId: undefined }); fetchList() }
function openCreate() { Object.assign(form, { id: undefined, startStationId: 1, endStationId: 3, distance: 0, estimatedHours: 0, cost: 0, status: '1' }); dialogVisible.value = true }
function openEdit(row: RouteItem) { Object.assign(form, row); dialogVisible.value = true }
async function submit() { if (form.id) await updateRoute(form.id, form); else await createRoute(form); ElMessage.success('保存成功'); dialogVisible.value = false; fetchList() }
async function disable(row: RouteItem) { if (!row.id) return; await disableRoute(row.id); ElMessage.success('已禁用'); fetchList() }
async function submitPlan() { planResult.value = await planRoute(planForm) }
onMounted(fetchList)
</script>
