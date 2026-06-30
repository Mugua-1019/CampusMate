import { createApp } from 'vue'
import axios from 'axios'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './assets/styles/global.css'
import App from './App.vue'
import router from './router'
import { getCurrentAdmin, getCurrentRole, getCurrentUser, ROLE_ADMIN, ROLE_USER } from './utils/currentUser'

axios.interceptors.request.use((config) => {
  const headers = config.headers || {}
  delete headers['X-CampusMate-User-Id']
  delete headers['X-CampusMate-Admin-Id']

  const url = config.url || ''
  const role = getCurrentRole()
  if (url.startsWith('/api/admin')) {
    const currentAdmin = role === ROLE_ADMIN ? getCurrentAdmin() : null
    if (currentAdmin?.adminId) {
      headers['X-CampusMate-Admin-Id'] = currentAdmin.adminId
    }
  } else {
    const currentUser = role === ROLE_USER ? getCurrentUser() : null
    if (currentUser?.userId) {
      headers['X-CampusMate-User-Id'] = currentUser.userId
    }
  }
  config.headers = headers
  return config
})

createApp(App).use(router).use(ElementPlus).mount('#app')
