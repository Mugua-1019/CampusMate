<template>
  <main class="safety-page">
    <section class="app-shell">
      <aside class="sidebar">
        <div class="brand">
          <img :src="logoImage" alt="星伴 CampusMate" />
          <div>
            <strong>星伴</strong>
            <span>CampusMate</span>
          </div>
        </div>

        <nav class="nav-list" aria-label="主导航">
          <button v-for="item in navItems" :key="item.label" class="nav-item" :class="{ active: item.active }" @click="handleNav(item)">
            <component :is="item.icon" />
            <span>{{ item.label }}</span>
          </button>
        </nav>

        <section class="verify-card">
          <div class="mini-scene">
            <img :src="verifyImage" alt="" />
          </div>
          <h2>完成认证，解锁更多功能</h2>
          <p>提升匹配可信度</p>
          <p>可主动发起聊天</p>
          <p>参与更多活动</p>
          <button class="btn-primary" @click="goAuthCenter">去认证</button>
        </section>
      </aside>

      <section class="main-content">
        <header class="topbar">
          <div class="welcome-copy">
            <h1>Hello，今天想找什么星伴？</h1>
            <p>在校园里找到一起学习、运动、吃饭、倾诉的温暖伙伴。</p>
          </div>

          <div class="top-actions">
            <label class="search-box">
              <input v-model="keyword" placeholder="搜索需求、活动或用户" />
              <Search />
            </label>
            <button class="plain-icon" aria-label="我的聊天" @click="goChat"><Message /></button>
            <NotificationBell />
            <UserMenu
              v-if="isLoggedIn"
              :avatar-url="userAvatar"
              :avatar-text="userInitial"
              :status-text="userSummary.verified ? '已认证' : '待认证'"
            />
            <button v-else class="login-chip" @click="goLogin">登录</button>
          </div>
        </header>

        <section class="safety-image-wrap" aria-label="安全反馈">
          <img :src="safetyImage" alt="安全反馈" />
        </section>
      </section>
    </section>
  </main>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Flag, HomeFilled, Lock, Message, Promotion, Search, StarFilled, User } from '@element-plus/icons-vue'
import logoImage from '../../../assets/images/logo-star-mascot.png'
import verifyImage from '../../../assets/images/renzheng.png'
import safetyImage from '../../../assets/images/anquan.png'
import NotificationBell from '../../../components/NotificationBell.vue'
import UserMenu from '../../../components/UserMenu.vue'
import { fetchHomePlaza } from '../../../api/home'
import { getCurrentUser, isAuthenticatedUser } from '../../../utils/currentUser'

const router = useRouter()
const keyword = ref('')
const currentUser = ref(getCurrentUser())
const userSummary = ref({
  verified: false,
  nickname: '小星同学',
  avatarUrl: ''
})

const navItems = [
  { label: '广场首页', icon: HomeFilled, route: '/home' },
  { label: '发布需求', icon: Promotion, route: '/publish' },
  { label: '我的聊天', icon: Message, route: '/chat' },
  { label: '我的匹配', icon: StarFilled, route: '/my-match' },
  { label: '认证中心', icon: Lock, route: '/auth-center' },
  { label: '安全反馈', icon: Flag, route: '/safety-feedback', active: true },
  { label: '个人中心', icon: User, route: '/profile' }
]

const isLoggedIn = computed(() => isAuthenticatedUser(currentUser.value))
const userInitial = computed(() => (userSummary.value.nickname || '星').slice(0, 1))
const userAvatar = computed(() => userSummary.value.avatarUrl || currentUser.value?.avatarUrl || '')

const goAuthCenter = () => {
  router.push(isLoggedIn.value ? '/auth-center' : '/login')
}

const goLogin = () => {
  router.push('/login')
}

const goChat = () => {
  router.push('/chat')
}

const handleNav = (item) => {
  if ((item.route === '/profile' || item.route === '/auth-center') && !isLoggedIn.value) {
    goLogin()
    return
  }
  router.push(item.route)
}

const loadUserSummary = async () => {
  if (!isLoggedIn.value) {
    return
  }
  try {
    const response = await fetchHomePlaza({
      userId: currentUser.value.userId,
      plaza: 'match',
      category: '全部',
      keyword: ''
    })
    const payload = response.data.data
    userSummary.value = {
      verified: payload.userSummary?.verified || false,
      nickname: payload.userSummary?.nickname || '小星同学',
      avatarUrl: payload.userSummary?.avatarUrl || currentUser.value?.avatarUrl || ''
    }
  } catch (error) {
    userSummary.value = {
      verified: false,
      nickname: '小星同学',
      avatarUrl: currentUser.value?.avatarUrl || ''
    }
  }
}

onMounted(loadUserSummary)
</script>

<style scoped>
.safety-page {
  min-height: 100vh;
  padding: 14px 20px;
  background:
    radial-gradient(circle at 10% 0%, rgba(255, 215, 238, 0.58), transparent 28%),
    radial-gradient(circle at 92% 8%, rgba(226, 214, 255, 0.78), transparent 34%),
    linear-gradient(135deg, #fbf8ff 0%, #f4f7ff 48%, #fff9fc 100%);
}

.app-shell {
  position: relative;
  display: grid;
  grid-template-columns: 245px minmax(0, 1fr);
  gap: 28px;
  max-width: 1760px;
  min-height: calc(100vh - 28px);
  margin: 0 auto;
  padding: 18px 26px 26px 16px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.86);
  box-shadow: 0 22px 52px rgba(105, 82, 166, 0.16);
}

.sidebar {
  display: flex;
  flex-direction: column;
  margin: -18px 0 -26px -16px;
  padding: 34px 0 86px 16px;
  border-radius: 28px 0 0 28px;
  background: linear-gradient(180deg, rgba(253, 250, 255, 0.96), rgba(247, 244, 255, 0.86));
  box-shadow: inset -1px 0 0 rgba(224, 216, 246, 0.54);
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 24px 28px;
}

.brand img {
  width: 72px;
  height: 58px;
  object-fit: contain;
  transform: scale(1.22);
  transform-origin: center;
}

.brand strong {
  display: block;
  color: #6853f1;
  font-size: 29px;
  line-height: 1;
}

.brand span {
  color: #6853f1;
  font-size: 16px;
  font-weight: 800;
  font-style: italic;
}

.nav-list {
  display: grid;
  gap: 8px;
  padding: 0 0 0 14px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 18px;
  height: 56px;
  padding: 0 18px;
  border-radius: 10px;
  color: #9592a8;
  background: transparent;
  font-size: 16px;
  font-weight: 800;
}

.nav-item svg {
  width: 22px;
  height: 22px;
}

.nav-item.active {
  color: #6c55f0;
  background: linear-gradient(100deg, rgba(120, 99, 246, 0.18), rgba(232, 211, 255, 0.55));
}

.verify-card {
  margin: auto 18px 0;
  padding: 16px;
  border: 1px solid #eee4ff;
  border-radius: 18px;
  background: linear-gradient(145deg, #fff, #f5eeff);
  box-shadow: 0 12px 26px rgba(118, 103, 201, 0.12);
}

.mini-scene {
  display: flex;
  align-items: end;
  justify-content: center;
  height: 82px;
  margin-bottom: 10px;
  border-radius: 16px;
  background: linear-gradient(160deg, #f4edff, #fff9fd);
}

.mini-scene img {
  width: 118px;
}

.verify-card h2 {
  margin: 0;
  color: #20233f;
  font-size: 17px;
}

.verify-card p {
  margin: 8px 0;
  color: #77738b;
  font-size: 13px;
}

.verify-card p::before {
  content: "•";
  margin-right: 8px;
  color: #4e4566;
}

.main-content {
  min-width: 0;
  padding-top: 18px;
}

.topbar,
.top-actions {
  display: flex;
  align-items: center;
}

.topbar {
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 24px;
}

.welcome-copy h1 {
  margin: 0 0 10px;
  color: #12121d;
  font-size: 29px;
  line-height: 1.1;
  white-space: nowrap;
}

.welcome-copy p {
  max-width: 360px;
  margin: 0;
  color: #818096;
  font-size: 14px;
  font-weight: 700;
}

.top-actions {
  position: absolute;
  top: 38px;
  right: 54px;
  gap: 18px;
  z-index: 5;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 320px;
  height: 46px;
  padding: 0 15px 0 22px;
  border: 1px solid #eceaf5;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.85);
  box-shadow: inset 0 1px 0 #fff, 0 8px 20px rgba(111, 105, 148, 0.07);
}

.search-box input {
  width: 100%;
  border: 0;
  outline: 0;
  color: #4b4961;
  background: transparent;
}

.search-box svg {
  width: 23px;
  color: #8f8aa8;
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
  width: 28px;
  height: 28px;
}

.login-chip,
.btn-primary {
  border-radius: 999px;
  color: #fff;
  background: linear-gradient(135deg, #7460f4, #9d7bff);
  font-weight: 900;
}

.login-chip {
  min-width: 74px;
  height: 38px;
  padding: 0 18px;
  font-size: 14px;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  min-height: 40px;
  margin-top: 8px;
}

.safety-image-wrap {
  min-height: calc(100vh - 170px);
  overflow: hidden;
  border: 1px solid #eee5ff;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 12px 28px rgba(111, 98, 160, 0.14);
}

.safety-image-wrap img {
  display: block;
  width: 100%;
  height: 100%;
  min-height: calc(100vh - 170px);
  object-fit: cover;
  object-position: top center;
}

@media (max-width: 980px) {
  .app-shell {
    display: block;
    padding: 14px;
  }

  .sidebar {
    margin: -14px -14px 20px;
    padding: 24px 14px 18px;
    border-radius: 24px 24px 0 0;
  }

  .verify-card {
    display: none;
  }

  .nav-list {
    display: flex;
    overflow-x: auto;
    padding: 0;
  }

  .nav-item {
    flex: 0 0 auto;
  }

  .topbar,
  .top-actions {
    align-items: flex-start;
    flex-direction: column;
  }

  .top-actions {
    position: static;
  }

  .search-box {
    width: 100%;
  }

  .welcome-copy h1 {
    white-space: normal;
  }
}
</style>
