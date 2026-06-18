import { createApp } from 'vue'
import axios from 'axios'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './assets/styles/global.css'
import App from './App.vue'
import router from './router'
import { getCurrentUser } from './utils/currentUser'

axios.interceptors.request.use((config) => {
  const currentUser = getCurrentUser()
  if (currentUser?.userId) {
    config.headers['X-CampusMate-User-Id'] = currentUser.userId
  }
  return config
})

createApp(App).use(router).use(ElementPlus).mount('#app')
