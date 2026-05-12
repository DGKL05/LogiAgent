<template>
  <div class="page">
    <div class="page-header"><h1 class="page-title">调度任务管理</h1><el-button type="primary" :icon="Plus" @click="openCreate">新建任务</el-button></div>
    <el-row :gutter="14">
      <el-col :span="16">
        <el-card class="filter-card" shadow="never">
          <el-form :model="query" inline>
            <el-form-item label="任务号"><el-input v-model="query.taskNo" clearable /></el-form-item>
            <el-form-item label="运单号"><el-input v-model="query.waybillNo" clearable /></el-form-item>
            <el-form-item label="网点 ID"><el-input-number v-model="query.stationId" :min="1" /></el-form-item>
            <el-button type="primary" @click="search">查询</el-button><el-button @click="reset">重置</el-button>
          </el-form>
        </el-card>
        <el-card shadow="never">
          <el-table v-loading="loading" :data="rows" stripe>
            <el-table-column prop="taskNo" label="任务号" min-width="170" />
            <el-table-column prop="waybillNo" label="运单号" min-width="170" />
            <el-table-column prop="courierId" label="快递员" width="100" />
            <el-table-column prop="stationId" label="网点" width="90" />
            <el-table-column prop="taskStatus" label="状态" width="120"><template #default="{ row }"><el-tag>{{ row.taskStatus }}</el-tag></template></el-table-column>
            <el-table-column label="操作" width="140"><template #default="{ row }"><el-button link type="primary" @click="openStatus(row)">改状态</el-button></template></el-table-column>
          </el-table>
          <div class="table-actions"><el-pagination v-model:current-page="query.page" v-model:page-size="query.size" layout="total, prev, pager, next" :total="total" @change="fetchList" /></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>网点负载排行</template>
          <el-table :data="loads" size="small">
            <el-table-column prop="stationName" label="网点" />
            <el-table-column prop="dispatchTaskCount" label="任务" width="70" />
            <el-table-column prop="loadLevel" label="负载" width="100" />
          </el-table>
        </el-card>
        <el-card shadow="never" class="suggest-card">
          <template #header>调度建议</template>
          <div class="pre-wrap"><p v-for="item in suggestions" :key="`${item.stationId}-${item.suggestion}`">{{ item.stationName }}：{{ item.suggestion }}</p></div>
        </el-card>
      </el-col>
    </el-row>
    <el-dialog v-model="dialogVisible" title="新建调度任务" width="460px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="运单号"><el-input v-model="form.waybillNo" /></el-form-item>
        <el-form-item label="快递员"><el-input-number v-model="form.courierId" :min="1" /></el-form-item>
        <el-form-item label="网点"><el-input-number v-model="form.stationId" :min="1" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="submitCreate">保存</el-button></template>
    </el-dialog>
    <el-dialog v-model="statusVisible" title="修改任务状态" width="420px">
      <el-input v-model="nextStatus" placeholder="CREATED / ASSIGNED / DELIVERING / FINISHED" />
      <template #footer><el-button @click="statusVisible = false">取消</el-button><el-button type="primary" @click="submitStatus">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { createDispatchTask, getDispatchSuggestions, getDispatchTasks, getLoadRanking, updateDispatchTaskStatus } from '@/api/dispatch'
import type { DispatchSuggestion, DispatchTask, StationLoad } from '@/types/api'

const rows = ref<DispatchTask[]>([])
const loads = ref<StationLoad[]>([])
const suggestions = ref<DispatchSuggestion[]>([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const statusVisible = ref(false)
const current = ref<DispatchTask | null>(null)
const nextStatus = ref('')
const query = reactive<{ page: number; size: number; taskNo: string; waybillNo: string; stationId?: number }>({ page: 1, size: 10, taskNo: '', waybillNo: '' })
const form = reactive<Partial<DispatchTask>>({ waybillNo: '', courierId: 1, stationId: 1 })
async function fetchList() { loading.value = true; try { const data = await getDispatchTasks(query); rows.value = data?.records || []; total.value = data?.total || 0 } finally { loading.value = false } }
async function loadSide() { loads.value = await getLoadRanking(); suggestions.value = await getDispatchSuggestions() }
function search() { query.page = 1; fetchList() }
function reset() { Object.assign(query, { page: 1, size: 10, taskNo: '', waybillNo: '', stationId: undefined }); fetchList() }
function openCreate() { Object.assign(form, { waybillNo: '', courierId: 1, stationId: 1 }); dialogVisible.value = true }
async function submitCreate() { await createDispatchTask(form); ElMessage.success('创建成功'); dialogVisible.value = false; fetchList(); loadSide() }
function openStatus(row: DispatchTask) { current.value = row; nextStatus.value = row.taskStatus || ''; statusVisible.value = true }
async function submitStatus() { if (!current.value?.taskNo || !nextStatus.value) return; await updateDispatchTaskStatus(current.value.taskNo, nextStatus.value); ElMessage.success('状态已更新'); statusVisible.value = false; fetchList() }
onMounted(() => { fetchList(); loadSide() })
</script>

<style scoped>
.suggest-card {
  margin-top: 14px;
}
</style>
