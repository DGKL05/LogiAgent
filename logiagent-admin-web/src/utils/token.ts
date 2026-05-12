const TOKEN_KEY = 'logiagent_admin_token'
const USER_KEY = 'logiagent_admin_user'

export interface StoredUser {
  userId?: number
  username?: string
  roles?: string[]
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token: string) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function getStoredUser(): StoredUser {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) {
    return {}
  }
  try {
    return JSON.parse(raw) as StoredUser
  } catch {
    localStorage.removeItem(USER_KEY)
    return {}
  }
}

export function setStoredUser(user: StoredUser) {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function clearStoredAuth() {
  removeToken()
  localStorage.removeItem(USER_KEY)
}
