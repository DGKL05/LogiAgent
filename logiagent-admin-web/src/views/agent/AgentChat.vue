<template>
  <div class="page agent-chat-page">
    <div class="page-header"><h1 class="page-title">Agent 智能问答</h1></div>
    <el-card shadow="never" class="chat-card">
      <div class="messages">
        <div v-for="item in messages" :key="item.id" :class="['message', item.role]">
          <div class="bubble pre-wrap">
            <b>{{ item.role === 'user' ? '我' : 'LogiAgent' }}</b>
            <p>{{ item.content }}</p>
            <div v-if="item.intent" class="muted">Intent: {{ item.intent }}</div>
          </div>
        </div>
      </div>
      <div class="quick-prompts">
        <el-button v-for="prompt in prompts" :key="prompt" size="small" @click="message = prompt">{{ prompt }}</el-button>
      </div>
      <div class="chat-input">
        <el-input v-model="message" type="textarea" :rows="3" placeholder="输入物流问题，例如：生成今天的物流日报" />
        <el-button type="primary" :loading="loading" @click="send">发送</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { chatWithAgent } from '@/api/agent'
import { useAuthStore } from '@/stores/auth'

interface ChatMessage {
  id: number
  role: 'user' | 'agent'
  content: string
  intent?: string
}

const auth = useAuthStore()
const loading = ref(false)
const message = ref('')
const messages = ref<ChatMessage[]>([])
const prompts = ['查询运单 WB20260509204042075 到哪了', '用百度地图规划广州到深圳真实道路路线', '今天哪个网点压力最大', '生成今天的物流日报']

async function send() {
  if (!message.value.trim()) {
    ElMessage.warning('请输入问题')
    return
  }
  const question = message.value.trim()
  messages.value.push({ id: Date.now(), role: 'user', content: question })
  message.value = ''
  loading.value = true
  try {
    const data = await chatWithAgent({ userId: auth.userId, message: question })
    messages.value.push({ id: Date.now() + 1, role: 'agent', content: data.answer, intent: data.intent })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.chat-card {
  height: calc(100vh - 140px);
  display: flex;
}

.chat-card :deep(.el-card__body) {
  display: flex;
  width: 100%;
  flex-direction: column;
  gap: 14px;
}

.messages {
  flex: 1;
  overflow: auto;
}

.message {
  display: flex;
  margin-bottom: 14px;
}

.message.user {
  justify-content: flex-end;
}

.bubble {
  max-width: 72%;
  padding: 12px 14px;
  border-radius: 8px;
  background: #eef3f8;
}

.message.user .bubble {
  background: #e0f6ea;
}

.bubble p {
  margin: 8px 0 0;
}

.quick-prompts {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chat-input {
  display: grid;
  grid-template-columns: 1fr 100px;
  gap: 12px;
  align-items: stretch;
}
</style>
