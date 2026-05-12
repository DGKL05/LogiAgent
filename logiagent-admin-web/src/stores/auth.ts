import { defineStore } from 'pinia'
import { loginApi, logoutApi } from '@/api/auth'
import { clearStoredAuth, getStoredUser, getToken, setStoredUser, setToken } from '@/utils/token'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken(),
    userId: getStoredUser().userId,
    username: getStoredUser().username || '',
    roles: getStoredUser().roles || []
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token),
    isAdmin: (state) => state.roles.includes('ADMIN')
  },
  actions: {
    async login(username: string, password: string) {
      const data = await loginApi({ username, password })
      this.token = data.token
      this.userId = data.userId
      this.username = data.username
      this.roles = data.roles || []
      setToken(data.token)
      setStoredUser({
        userId: data.userId,
        username: data.username,
        roles: data.roles || []
      })
    },
    async logout() {
      try {
        if (this.token) {
          await logoutApi()
        }
      } finally {
        this.clear()
      }
    },
    clear() {
      this.token = ''
      this.userId = undefined
      this.username = ''
      this.roles = []
      clearStoredAuth()
    }
  }
})
