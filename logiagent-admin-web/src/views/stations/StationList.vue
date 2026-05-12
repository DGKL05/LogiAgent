<template>
  <div class="page">
    <div class="page-header"><h1 class="page-title">网点管理</h1><el-button type="primary" :icon="Plus" @click="openCreate">新增网点</el-button></div>
    <el-card class="filter-card" shadow="never">
      <el-form :model="query" inline>
        <el-form-item label="网点名"><el-input v-model="query.stationName" clearable /></el-form-item>
        <el-form-item label="省份"><el-input v-model="query.province" clearable /></el-form-item>
        <el-form-item label="城市"><el-input v-model="query.city" clearable /></el-form-item>
        <el-button type="primary" @click="search">查询</el-button><el-button @click="reset">重置</el-button>
      </el-form>
    </el-card>
    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="stationName" label="网点名称" min-width="150" />
        <el-table-column prop="province" label="省份" />
        <el-table-column prop="city" label="城市" />
        <el-table-column prop="address" label="地址" min-width="220" />
        <el-table-column label="坐标" min-width="180"><template #default="{ row }">{{ row.latitude }}, {{ row.longitude }}</template></el-table-column>
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column label="操作" width="160"><template #default="{ row }"><el-button link type="primary" @click="openEdit(row)">编辑</el-button><el-button link type="danger" @click="disable(row)">禁用</el-button></template></el-table-column>
      </el-table>
      <div class="table-actions"><el-pagination v-model:current-page="query.page" v-model:page-size="query.size" layout="total, prev, pager, next, sizes" :total="total" @change="fetchList" /></div>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑网点' : '新增网点'" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="网点名称"><el-input v-model="form.stationName" /></el-form-item>
        <el-form-item label="省份"><el-input v-model="form.province" /></el-form-item>
        <el-form-item label="城市"><el-input v-model="form.city" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="经度"><el-input-number v-model="form.longitude" :precision="6" /></el-form-item>
        <el-form-item label="纬度"><el-input-number v-model="form.latitude" :precision="6" /></el-form-item>
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
import { createStation, disableStation, getStations, updateStation } from '@/api/station'
import type { StationItem } from '@/types/api'

const loading = ref(false)
const rows = ref<StationItem[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const query = reactive({ page: 1, size: 10, stationName: '', province: '', city: '' })
const form = reactive<Partial<StationItem>>({})
async function fetchList() { loading.value = true; try { const data = await getStations(query); rows.value = data?.records || []; total.value = data?.total || 0 } finally { loading.value = false } }
function search() { query.page = 1; fetchList() }
function reset() { Object.assign(query, { page: 1, size: 10, stationName: '', province: '', city: '' }); fetchList() }
function openCreate() { Object.assign(form, { id: undefined, stationName: '', province: '', city: '', address: '', longitude: 113.264385, latitude: 23.12911, status: '1' }); dialogVisible.value = true }
function openEdit(row: StationItem) { Object.assign(form, row); dialogVisible.value = true }
async function submit() { if (form.id) await updateStation(form.id, form); else await createStation(form); ElMessage.success('保存成功'); dialogVisible.value = false; fetchList() }
async function disable(row: StationItem) { if (!row.id) return; await disableStation(row.id); ElMessage.success('已禁用'); fetchList() }
onMounted(fetchList)
</script>
