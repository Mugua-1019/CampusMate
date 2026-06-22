<template>
  <main class="home-page">
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

        <section class="plaza-tabs" aria-label="广场切换">
          <button
            v-for="tab in plazaTabs"
            :key="tab.key"
            :class="{ active: activePlaza === tab.key }"
            @click="selectPlaza(tab.key)"
          >
            {{ tab.label }}
          </button>
        </section>

        <section class="filter-row" aria-label="分类筛选">
          <button
            v-for="category in activeCategories"
            :key="category"
            :class="{ active: selectedCategory === category }"
            @click="selectedCategory = category"
          >
            {{ category }}
          </button>
          <button class="filter-button">筛选<Filter /></button>
        </section>

        <section class="hero-card">
          <img :src="bannerImage" alt="今日精选推荐" />
        </section>

        <section class="section-heading">
          <h2>{{ activeTab.label }}需求</h2>
          <span>{{ loading ? '加载中...' : `${posts.length} 条可浏览内容` }}</span>
        </section>

        <p v-if="apiError" class="empty-state">{{ apiError }}</p>

        <section v-else class="post-grid" aria-label="星伴需求列表">
          <article
            v-for="post in posts"
            :key="post.id"
            class="note-card"
            :class="toneMap[post.category] || 'note-other'"
            @click="goPostDetail(post)"
          >
            <span class="pin" :class="isFull(post) ? 'pin-muted' : 'pin-pink'"></span>
            <span v-if="isFull(post)" class="tape"></span>
            <div class="card-title-row">
              <h2>{{ post.title }}</h2>
              <span class="status-pill" :class="{ full: isFull(post) }">{{ isFull(post) ? '已满员' : '待匹配' }}</span>
            </div>
            <div class="tag-row">
              <span>{{ post.category }}</span>
              <span v-if="post.anonymous">匿名发布</span>
              <span>{{ post.verified ? '已认证' : '未认证' }}</span>
              <span v-for="tag in post.tags.slice(0, 2)" :key="tag">{{ tag }}</span>
            </div>
            <p>{{ post.description }}</p>
            <div class="meta-line">
              <span><Location />{{ post.location }}</span>
              <span><Clock />{{ post.time }}</span>
              <span v-if="post.aaFee"><Wallet />{{ post.aaFee }}</span>
            </div>
            <footer>
              <div class="publisher">
                <img v-if="post.publisherAvatarUrl" class="avatar" :src="post.publisherAvatarUrl" alt="发布者头像" />
                <span v-else class="avatar">{{ post.avatarText }}</span>
                <strong>{{ post.publisherName }}</strong>
              </div>
              <span class="count">{{ post.currentCount }}/{{ post.maxCount }}人</span>
              <button class="chat-button" :disabled="isFull(post)" @click.stop="handleChat(post)">
                {{ isFull(post) ? '已满员' : '聊一聊' }}
              </button>
            </footer>
          </article>
        </section>

        <p v-if="!apiError && !loading && posts.length === 0" class="empty-state">暂时没有符合条件的需求，换个关键词试试。</p>
        <button class="more-button">查看更多需求<ArrowDown /></button>
      </section>

      <aside class="right-panel">
        <section class="side-card status-card">
          <div class="side-title">
            <h2>我的星伴状态</h2>
            <button>编辑</button>
          </div>
          <div class="status-body">
            <img v-if="userAvatar" class="status-placeholder" :src="userAvatar" alt="头像" />
            <span v-else class="status-placeholder">{{ userInitial }}</span>
            <div>
              <strong>{{ userSummary.nickname || '小星同学' }}</strong>
              <p><span></span> {{ userSummary.verified ? '已认证' : '待认证' }}</p>
            </div>
          </div>
          <div class="status-metrics">
            <div v-for="stat in userSummary.stats" :key="stat.label">
              <strong>{{ stat.value }}</strong>
              <span>{{ stat.label }}</span>
            </div>
          </div>
          <p class="status-note">完成校园认证后，可以发布需求、发起聊天和参与匹配。</p>
        </section>

        <section class="side-card confirm-card">
          <div class="side-title">
            <h2>待确认见面信息</h2>
            <em>{{ userSummary.pendingMeet?.title ? 1 : 0 }}</em>
          </div>
          <div class="confirm-user">
            <span>{{ userSummary.pendingMeet?.partner?.slice(0, 1) || '星' }}</span>
            <div>
              <strong>{{ userSummary.pendingMeet?.title || '暂无待确认事项' }}</strong>
              <p v-if="userSummary.pendingMeet?.partner">
                {{ userSummary.pendingMeet.partner }} 邀请你确认 {{ userSummary.pendingMeet.category }}
              </p>
              <small v-if="userSummary.pendingMeet?.time">时间：{{ userSummary.pendingMeet.time }}</small>
              <small v-if="userSummary.pendingMeet?.location">地点：{{ userSummary.pendingMeet.location }}</small>
            </div>
          </div>
          <div class="confirm-actions">
            <button class="btn-primary">同意匹配</button>
            <button class="btn-secondary">再聊一聊</button>
          </div>
        </section>

        <section class="side-card safety-card">
          <h2>安全小贴士</h2>
          <ul>
            <li>见面请选择公开场所</li>
            <li>保护个人隐私与财产安全</li>
            <li>遇到不适随时举报</li>
          </ul>
          <button @click="goSafetyFeedback">查看安全指南 <ArrowRight /></button>
        </section>

        <section class="side-card mood-card">
          <div>
            <h2>今天的心情需要被听见吗？</h2>
            <p>有时候，说出来就会好一点。</p>
            <button class="btn-primary" @click="selectPlaza('vent')">去倾诉广场</button>
          </div>
          <small>星伴平台不提供专业心理咨询服务，如遇严重心理问题，请及时寻求专业帮助。</small>
        </section>
      </aside>

      <button class="publish-button" @click="goPublish">
        <Promotion />
        发布星伴需求
      </button>
    </section>

    <div v-if="showVerifyTip" class="modal-mask" @click.self="showVerifyTip = false">
      <section class="verify-modal">
        <img :src="verifyImage" alt="" />
        <h2>完成校园认证后，才可以发布需求、发起聊天和参与匹配。</h2>
        <p>认证能帮助大家更安心地在校园公共场景中相遇。</p>
        <div>
          <button class="btn-primary" @click="goAuthCenter">去认证</button>
          <button class="btn-secondary" @click="showVerifyTip = false">稍后再说</button>
        </div>
      </section>
    </div>
  </main>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowDown,
  ArrowRight,
  Bell,
  Clock,
  Filter,
  Flag,
  HomeFilled,
  Location,
  Lock,
  Message,
  Promotion,
  Search,
  StarFilled,
  User,
  Wallet
} from '@element-plus/icons-vue'
import logoImage from '../../../assets/images/logo-star-mascot.png'
import verifyImage from '../../../assets/images/renzheng.png'
import bannerImage from '../../../assets/images/guanggaotiao.png'
import { fetchHomePlaza } from '../../../api/home'
import { getCurrentUser, isAuthenticatedUser } from '../../../utils/currentUser'
import NotificationBell from '../../../components/NotificationBell.vue'
import UserMenu from '../../../components/UserMenu.vue'

const router = useRouter()
const currentUser = ref(getCurrentUser())
const activePlaza = ref('match')
const selectedCategory = ref('全部')
const keyword = ref('')
const showVerifyTip = ref(false)
const loading = ref(false)
const apiError = ref('')
const plazaTabs = ref([])
const posts = ref([])
const userSummary = ref({
  verified: false,
  nickname: '小星同学',
  avatarUrl: '',
  stats: [],
  pendingMeet: {}
})

const navItems = [
  { label: '广场首页', icon: HomeFilled, active: true },
  { label: '发布需求', icon: Promotion, route: '/publish' },
  { label: '我的聊天', icon: Message, route: '/chat' },
  { label: '我的匹配', icon: StarFilled, route: '/my-match' },
  { label: '认证中心', icon: Lock, route: '/auth-center' },
  { label: '安全反馈', icon: Flag, route: '/safety-feedback' },
  { label: '个人中心', icon: User, route: '/profile' }
]

const toneMap = {
  学习搭子: 'note-blue',
  饭搭子: 'note-yellow',
  运动搭子: 'note-green',
  比赛组队: 'note-orange',
  活动同行: 'note-purple',
  闲聊陪伴: 'note-blue',
  想找人聊聊: 'note-pink',
  心情不好: 'note-pink',
  学业压力: 'note-pink',
  考试焦虑: 'note-pink',
  生活分享: 'note-purple',
  只想被听见: 'note-pink'
}

const fallbackTab = computed(() => ({
  key: activePlaza.value,
  label: activePlaza.value === 'vent' ? '倾诉广场' : '匹配广场',
  categories: ['全部']
}))

const activeTab = computed(() => plazaTabs.value.find((tab) => tab.key === activePlaza.value) || fallbackTab.value)
const activeCategories = computed(() => activeTab.value.categories || ['全部'])
const isLoggedIn = computed(() => isAuthenticatedUser(currentUser.value))
const userInitial = computed(() => (userSummary.value.nickname || '星').slice(0, 1))
const userAvatar = computed(() => userSummary.value.avatarUrl || currentUser.value?.avatarUrl || '')

const isFull = (post) => post.full || post.currentCount >= post.maxCount || post.status === 'full'

const selectPlaza = (plaza) => {
  activePlaza.value = plaza
  selectedCategory.value = '全部'
}

const handleChat = (post) => {
  if (isFull(post)) return
  if (!userSummary.value.verified) {
    showVerifyTip.value = true
    return
  }
  router.push({
    path: '/chat',
    query: {
      userId: post.publisherUserId || post.publisherId || post.userId,
      name: post.publisherName,
      badge: post.category,
      source: post.title
    }
  })
}

const goPostDetail = (post) => {
  if (!userSummary.value.verified) {
    showVerifyTip.value = true
    return
  }
  router.push(post.plaza === 'vent' ? `/vent-post/${post.id}` : `/match-post/${post.id}`)
}

const goProfile = () => {
  if (isLoggedIn.value) {
    router.push('/profile')
    return
  }
  goLogin()
}

const goAuthCenter = () => {
  showVerifyTip.value = false
  if (isLoggedIn.value) {
    router.push('/auth-center')
    return
  }
  goLogin()
}

const handleUserStatusClick = () => {
  if (!userSummary.value.verified) {
    goAuthCenter()
    return
  }
  goProfile()
}

const goLogin = () => {
  router.push('/login')
}

const goPublish = () => {
  router.push('/publish')
}

const goChat = () => {
  router.push('/chat')
}

const goSafetyFeedback = () => {
  router.push('/safety-feedback')
}

const handleNav = (item) => {
  if (item.route) {
    if ((item.route === '/profile' || item.route === '/auth-center') && !isLoggedIn.value) {
      goLogin()
      return
    }
    router.push(item.route)
  }
}

const loadHomePlaza = async () => {
  loading.value = true
  apiError.value = ''
  try {
    const response = await fetchHomePlaza({
      userId: isLoggedIn.value ? currentUser.value.userId : undefined,
      plaza: activePlaza.value,
      category: selectedCategory.value,
      keyword: keyword.value
    })
    const payload = response.data.data
    activePlaza.value = payload.activePlaza
    selectedCategory.value = payload.selectedCategory
    plazaTabs.value = payload.tabs || []
    userSummary.value = {
      verified: payload.userSummary?.verified || false,
      nickname: payload.userSummary?.nickname || '小星同学',
      avatarUrl: isLoggedIn.value ? payload.userSummary?.avatarUrl || currentUser.value?.avatarUrl || '' : '',
      stats: payload.userSummary?.stats || [],
      pendingMeet: payload.userSummary?.pendingMeet || {}
    }
    posts.value = payload.posts || []
  } catch (error) {
    apiError.value = '首页数据加载失败，请确认 Spring Boot 服务和数据库初始化已完成。'
    posts.value = []
  } finally {
    loading.value = false
  }
}

let searchTimer = null
watch([activePlaza, selectedCategory, keyword], () => {
  window.clearTimeout(searchTimer)
  searchTimer = window.setTimeout(loadHomePlaza, 180)
})

onMounted(loadHomePlaza)
</script>

<style scoped>
.home-page {
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
  grid-template-columns: 245px minmax(760px, 1fr) 300px;
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

.verify-card h2,
.side-card h2 {
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

.verify-card .btn-primary {
  width: 100%;
  margin-top: 8px;
}

.main-content {
  min-width: 0;
  padding-top: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.28);
}

.topbar,
.top-actions,
.filter-row,
.meta-line,
.note-card footer,
.publisher,
.side-title,
.status-body,
.confirm-actions,
.publish-button,
.card-title-row {
  display: flex;
  align-items: center;
}

.topbar {
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 28px;
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

.notice-button span,
.confirm-card em {
  display: grid;
  place-items: center;
  border-radius: 999px;
  color: #fff;
  background: #ef5b75;
  font-style: normal;
  font-weight: 900;
}

.notice-button span {
  position: absolute;
  top: -8px;
  right: -7px;
  width: 22px;
  height: 22px;
  font-size: 12px;
}

.profile-chip {
  display: grid;
  grid-template-columns: 50px 58px 18px;
  align-items: center;
  gap: 10px;
  color: #b9802e;
  background: transparent;
  font-size: 14px;
  font-weight: 800;
}

.login-chip {
  min-width: 74px;
  height: 38px;
  padding: 0 18px;
  border-radius: 999px;
  color: #fff;
  background: linear-gradient(135deg, #7460f4, #9d7bff);
  font-size: 14px;
  font-weight: 900;
}

.profile-avatar {
  display: grid;
  width: 50px;
  height: 50px;
  place-items: center;
  border-radius: 50%;
  background: #dcd5ff;
  color: #6853f1;
  font-size: 22px;
  font-weight: 900;
  object-fit: cover;
}

.profile-chip span:nth-child(2) {
  padding: 7px 12px;
  border-radius: 999px;
  background: #fff0d9;
}

.plaza-tabs {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  width: 370px;
  margin-bottom: 18px;
  border-radius: 14px;
  background: #f1eff8;
  overflow: hidden;
}

.plaza-tabs button {
  height: 52px;
  color: #817b95;
  background: transparent;
  font-size: 18px;
  font-weight: 900;
}

.plaza-tabs button.active {
  color: #fff;
  background: linear-gradient(105deg, #7460f4, #ffa4bd);
}

.filter-row {
  gap: 16px;
  margin-bottom: 18px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.filter-row button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  min-width: 92px;
  min-height: 44px;
  padding: 0 22px;
  border: 1px solid #ebe8f5;
  border-radius: 999px;
  color: #7f7b90;
  background: rgba(255, 255, 255, 0.78);
  font-weight: 800;
  line-height: 1.1;
  box-shadow: 0 4px 12px rgba(112, 105, 160, 0.05);
}

.filter-row button.active {
  color: #654eea;
  border-color: #745df5;
  background: #fff;
}

.filter-button svg {
  width: 17px;
}

.hero-card {
  aspect-ratio: 1872 / 625;
  height: auto;
  margin-bottom: 24px;
  overflow: hidden;
  border: 1px solid #eee5ff;
  border-radius: 18px;
  background: #f5efff;
  box-shadow: 0 12px 28px rgba(111, 98, 160, 0.14);
}

.hero-card img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.section-heading {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.section-heading h2 {
  margin: 0;
  color: #20233f;
  font-size: 21px;
}

.section-heading span {
  color: #817b95;
  font-size: 13px;
  font-weight: 800;
}

.post-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 22px 18px;
}

.note-card {
  position: relative;
  min-height: 222px;
  padding: 28px 20px 16px;
  border-radius: 12px;
  box-shadow: 0 9px 18px rgba(104, 112, 180, 0.14);
  transition: transform 0.2s ease-out, box-shadow 0.2s ease-out;
}

.note-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 36px rgba(104, 112, 180, 0.16);
}

.card-title-row {
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
}

.note-card h2 {
  display: -webkit-box;
  overflow: hidden;
  margin: 0 0 10px;
  color: #171724;
  font-size: 18px;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.status-pill {
  flex: 0 0 auto;
  padding: 5px 9px;
  border-radius: 999px;
  color: #654eea;
  background: rgba(255, 255, 255, 0.56);
  font-size: 12px;
  font-weight: 900;
}

.status-pill.full {
  color: #8b8178;
  background: #eee6d8;
}

.note-card p {
  display: -webkit-box;
  overflow: hidden;
  min-height: 40px;
  margin: 12px 0;
  color: #57586c;
  font-size: 13px;
  line-height: 1.55;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.note-yellow {
  border: 1px solid #f6dba2;
  background: linear-gradient(145deg, #fff3c9, #fffaf0);
}

.note-green {
  border: 1px solid #d8e4ad;
  background: linear-gradient(145deg, #f4fad9, #fbfff3);
}

.note-blue {
  border: 1px solid #beddf6;
  background: linear-gradient(145deg, #e9f5ff, #f8fcff);
}

.note-purple {
  border: 1px solid #dfcff7;
  background: linear-gradient(145deg, #f6eefe, #fff9ff);
}

.note-pink {
  border: 1px solid #f5c4cf;
  background: linear-gradient(145deg, #ffe8ec, #fff8f9);
}

.note-orange,
.note-other {
  border: 1px solid #ead2a4;
  background: linear-gradient(145deg, #fff0ca, #fff9ee);
}

.pin {
  position: absolute;
  top: -15px;
  display: grid;
  width: 36px;
  height: 36px;
  place-items: center;
  border-radius: 50%;
  filter: drop-shadow(0 5px 5px rgba(114, 91, 142, 0.18));
}

.pin::before {
  content: "";
  width: 14px;
  height: 14px;
  border: 3px solid rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  background: #ef5b75;
}

.pin-pink {
  left: 18px;
}

.pin-muted {
  right: 28px;
}

.pin-muted::before {
  background: #b9ad9c;
}

.tape {
  position: absolute;
  top: -22px;
  right: 0;
  width: 86px;
  height: 28px;
  transform: rotate(28deg);
  border-radius: 2px;
  background: repeating-linear-gradient(-45deg, #f5d5c8 0 7px, #f8ded3 7px 14px);
  opacity: 0.9;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 7px;
}

.tag-row span,
.status-tags span {
  padding: 5px 9px;
  border-radius: 7px;
  color: #7a61d9;
  background: rgba(255, 255, 255, 0.52);
  font-size: 12px;
  font-weight: 800;
}

.meta-line {
  gap: 14px;
  color: #71758a;
  font-size: 12px;
}

.meta-line span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  min-width: 0;
}

.meta-line svg {
  width: 15px;
}

.note-card footer {
  gap: 10px;
  margin-top: 14px;
}

.publisher {
  min-width: 0;
  gap: 6px;
  flex: 1;
}

.avatar {
  display: grid;
  width: 28px;
  height: 28px;
  flex: 0 0 28px;
  place-items: center;
  border-radius: 50%;
  color: #6853f1;
  background: #f0e9ff;
  font-size: 13px;
  font-weight: 900;
  object-fit: cover;
}

.publisher strong {
  overflow: hidden;
  color: #56586d;
  font-size: 12px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.count {
  color: #64687d;
  font-size: 12px;
}

.chat-button {
  min-width: 56px;
  height: 29px;
  border-radius: 999px;
  color: #fff;
  background: linear-gradient(135deg, #7763f5, #9d7bff);
  font-size: 12px;
  font-weight: 900;
}

.chat-button:disabled {
  color: #8b8178;
  background: #e2d7c5;
  cursor: not-allowed;
}

.more-button,
.safety-card button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-radius: 999px;
  font-weight: 900;
}

.more-button {
  display: flex;
  width: 210px;
  height: 40px;
  margin: 34px auto 0;
  color: #8063df;
  border: 1px solid #d9cef9;
  background: #fff;
}

.more-button svg,
.safety-card button svg {
  width: 18px;
  height: 18px;
  flex: 0 0 18px;
  stroke-width: 3;
}

.empty-state {
  margin: 18px 0;
  padding: 18px;
  border-radius: 18px;
  color: #817b95;
  background: rgba(255, 255, 255, 0.74);
  text-align: center;
  font-weight: 800;
}

.right-panel {
  display: grid;
  align-content: start;
  gap: 20px;
  padding-top: 145px;
}

.side-card {
  padding: 20px;
  border: 1px solid #ece7f8;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.7);
  box-shadow: 0 10px 28px rgba(112, 104, 151, 0.08);
}

.side-title {
  justify-content: space-between;
  margin-bottom: 14px;
}

.side-title button {
  color: #8a6cf2;
  background: transparent;
  font-weight: 800;
}

.status-body {
  gap: 14px;
}

.status-placeholder {
  display: grid;
  width: 76px;
  height: 76px;
  place-items: center;
  border-radius: 20px;
  color: #6853f1;
  background:
    linear-gradient(135deg, rgba(255,255,255,0.42), transparent 42%),
    linear-gradient(145deg, #ded7ff, #f3edff);
  box-shadow: inset 0 0 0 1px rgba(128, 106, 242, 0.1);
  font-size: 24px;
  font-weight: 900;
  object-fit: cover;
}

.status-body strong {
  color: #252235;
  font-size: 14px;
}

.status-body p {
  margin: 9px 0 0;
  color: #6c6f82;
  font-size: 13px;
}

.status-body p span {
  display: inline-block;
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: #63cd76;
}

.status-metrics {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
  margin: 14px 0 12px;
}

.status-metrics div {
  padding: 10px;
  border-radius: 10px;
  background: #f0ebff;
}

.status-metrics strong,
.status-metrics span {
  display: block;
}

.status-metrics strong {
  color: #745cf2;
  font-size: 20px;
}

.status-metrics span {
  color: #7e71ad;
  font-size: 12px;
  font-weight: 800;
}

.status-note {
  margin: 0;
  padding: 10px;
  border-radius: 8px;
  color: #7e71ad;
  background: #f0ebff;
  font-size: 13px;
  font-weight: 800;
}

.confirm-card em {
  width: 22px;
  height: 22px;
}

.confirm-user {
  display: grid;
  grid-template-columns: 48px 1fr;
  gap: 12px;
  margin-bottom: 16px;
}

.confirm-user > span {
  display: grid;
  width: 48px;
  height: 48px;
  place-items: center;
  border-radius: 12px;
  color: #6853f1;
  background: #e7efff;
  font-size: 20px;
  font-weight: 900;
}

.confirm-user strong {
  color: #171724;
  font-size: 14px;
}

.confirm-user p,
.confirm-user small {
  display: block;
  margin: 4px 0 0;
  color: #77798d;
  font-size: 12px;
}

.confirm-actions {
  gap: 12px;
}

.confirm-actions button {
  flex: 1;
  min-height: 38px;
  padding: 0 10px;
}

.safety-card {
  background:
    linear-gradient(140deg, rgba(255, 255, 255, 0.84), rgba(248, 244, 255, 0.9)),
    radial-gradient(circle at 88% 56%, rgba(128, 102, 245, 0.18), transparent 30%);
}

.safety-card ul {
  display: grid;
  gap: 9px;
  margin: 14px 0 16px;
  padding: 0;
  list-style: none;
}

.safety-card li {
  color: #537553;
  font-size: 13px;
}

.safety-card li::before {
  content: "✓";
  margin-right: 7px;
  color: #45b85b;
  font-weight: 900;
}

.safety-card button {
  height: 34px;
  padding: 0 14px;
  color: #745cf2;
  border: 1px solid #ded3ff;
  background: #fff;
}

.mood-card {
  position: relative;
  min-height: 146px;
  overflow: hidden;
}

.mood-card p {
  margin: 8px 0 14px;
  color: #77798d;
  font-size: 13px;
}

.mood-card small {
  display: block;
  margin-top: 12px;
  color: #8b8ea0;
  font-size: 11px;
  line-height: 1.55;
}

.publish-button {
  position: fixed;
  right: 170px;
  bottom: 34px;
  justify-content: center;
  gap: 12px;
  min-width: 254px;
  height: 64px;
  padding: 0 28px;
  border-radius: 999px;
  color: #fff;
  background: linear-gradient(105deg, #815bf4, #ff9ab7);
  box-shadow: 0 18px 36px rgba(129, 91, 244, 0.32);
  font-size: 20px;
  font-weight: 900;
  z-index: 8;
}

.publish-button svg {
  width: 28px;
  height: 28px;
}

.btn-primary,
.btn-secondary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 40px;
  border-radius: 999px;
  font-weight: 900;
}

.btn-primary {
  color: #fff;
  background: linear-gradient(135deg, #7460f4, #9d7bff);
}

.btn-secondary {
  color: #666078;
  border: 1px solid #dcd8eb;
  background: #fff;
}

.modal-mask {
  position: fixed;
  inset: 0;
  display: grid;
  place-items: center;
  padding: 20px;
  background: rgba(32, 35, 63, 0.22);
  z-index: 20;
}

.verify-modal {
  width: min(420px, 100%);
  padding: 28px;
  border: 1px solid #eee7ff;
  border-radius: 24px;
  background: #fff;
  box-shadow: 0 20px 52px rgba(87, 75, 128, 0.2);
}

.verify-modal img {
  width: 130px;
}

.verify-modal h2 {
  margin: 8px 0;
  color: #22223a;
  font-size: 18px;
}

.verify-modal p {
  color: #77798d;
  line-height: 1.6;
}

.verify-modal div {
  display: flex;
  gap: 12px;
}

.verify-modal button {
  flex: 1;
}

@media (max-width: 1320px) {
  .app-shell {
    grid-template-columns: 220px minmax(0, 1fr);
  }

  .right-panel {
    grid-column: 2;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    padding-top: 0;
  }

  .post-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .publish-button {
    right: 52px;
  }
}

@media (max-width: 980px) {
  .app-shell {
    display: block;
    padding: 14px;
  }

  .sidebar {
    padding-bottom: 18px;
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

  .search-box,
  .plaza-tabs {
    width: 100%;
  }

  .post-grid,
  .right-panel {
    grid-template-columns: 1fr;
  }

  .publish-button {
    left: 16px;
    right: 16px;
    bottom: 16px;
    min-width: 0;
  }
}
</style>
