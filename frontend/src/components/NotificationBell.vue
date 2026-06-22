<template>
  <div ref="bellRef" class="notification-bell">
    <button class="plain-icon notice-button" type="button" aria-label="通知" @click="togglePanel">
      <Bell />
      <span v-if="unreadCount">{{ unreadCount }}</span>
    </button>

    <section v-if="panelOpen" class="notification-panel">
      <header>
        <strong>提醒</strong>
        <button v-if="unreadCount" type="button" @click="markRead">全部已读</button>
      </header>
      <p v-if="!isLoggedIn" class="empty-text">登录后查看提醒</p>
      <p v-else-if="!notifications.length" class="empty-text">暂无新提醒</p>
      <article v-for="item in notifications" :key="item.id" :class="{ unread: item.unread }">
        <span></span>
        <div>
          <strong>{{ item.message }}</strong>
          <small>{{ formatTime(item.updatedAt) }}</small>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { Bell } from '@element-plus/icons-vue'
import { fetchHomeNotifications, markHomeNotificationsRead } from '../api/home'
import { getCurrentUser } from '../utils/currentUser'

const bellRef = ref(null)
const panelOpen = ref(false)
const notifications = ref([])
const loading = ref(false)
const isLoggedIn = computed(() => Boolean(getCurrentUser()?.userId))
const unreadCount = computed(() => notifications.value.filter((item) => item.unread).length)

const loadNotifications = async () => {
  if (!isLoggedIn.value || loading.value) {
    return
  }
  loading.value = true
  try {
    const response = await fetchHomeNotifications()
    notifications.value = response.data.data || []
  } catch (error) {
    notifications.value = []
  } finally {
    loading.value = false
  }
}

const togglePanel = async () => {
  panelOpen.value = !panelOpen.value
  if (panelOpen.value) {
    await loadNotifications()
  }
}

const markRead = async () => {
  await markHomeNotificationsRead()
  notifications.value = notifications.value.map((item) => ({ ...item, unread: false }))
}

const formatTime = (value) => {
  if (!value) {
    return '刚刚'
  }
  const time = new Date(value)
  if (Number.isNaN(time.getTime())) {
    return '刚刚'
  }
  const diffMinutes = Math.max(0, Math.floor((Date.now() - time.getTime()) / 60000))
  if (diffMinutes < 1) {
    return '刚刚'
  }
  if (diffMinutes < 60) {
    return `${diffMinutes}分钟前`
  }
  if (diffMinutes < 1440) {
    return `${Math.floor(diffMinutes / 60)}小时前`
  }
  return `${Math.floor(diffMinutes / 1440)}天前`
}

const handleDocumentClick = (event) => {
  if (bellRef.value && !bellRef.value.contains(event.target)) {
    panelOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleDocumentClick)
  loadNotifications()
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleDocumentClick)
})
</script>

<style scoped>
.notification-bell {
  position: relative;
}

.plain-icon {
  position: relative;
  display: grid;
  width: 34px;
  height: 34px;
  place-items: center;
  color: #111;
  background: transparent;
}

.plain-icon svg {
  width: 27px;
  height: 27px;
}

.notice-button span {
  position: absolute;
  top: -8px;
  right: -7px;
  display: grid;
  min-width: 22px;
  height: 22px;
  place-items: center;
  padding: 0 6px;
  border-radius: 999px;
  color: #fff;
  background: #ef5b75;
  font-size: 12px;
  font-weight: 900;
}

.notification-panel {
  position: absolute;
  top: calc(100% + 12px);
  right: -10px;
  z-index: 50;
  width: 280px;
  padding: 12px;
  border: 1px solid #eee7ff;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 18px 40px rgba(87, 75, 128, 0.18);
}

.notification-panel header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
}

.notification-panel header strong {
  color: #20233f;
  font-size: 15px;
}

.notification-panel header button {
  color: #765ff3;
  background: transparent;
  font-size: 12px;
  font-weight: 900;
}

.notification-panel article {
  display: grid;
  grid-template-columns: 10px 1fr;
  gap: 10px;
  padding: 10px 0;
  border-top: 1px solid #f0ecfb;
}

.notification-panel article > span {
  width: 8px;
  height: 8px;
  margin-top: 5px;
  border-radius: 999px;
  background: #d8d4e8;
}

.notification-panel article.unread > span {
  background: #ef5b75;
}

.notification-panel article strong {
  display: block;
  color: #3f4259;
  font-size: 13px;
  line-height: 1.45;
}

.notification-panel article small,
.empty-text {
  color: #8d89a3;
  font-size: 12px;
}

.empty-text {
  margin: 12px 0 2px;
  font-weight: 800;
}
</style>
