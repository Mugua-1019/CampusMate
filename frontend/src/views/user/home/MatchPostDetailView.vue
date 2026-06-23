<template>
  <main class="detail-page">
    <section class="detail-shell">
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
          <h2>完善资料，匹配更精准</h2>
          <button @click="goAuthCenter">去完善 <ArrowRight /></button>
        </section>
      </aside>

      <section class="page-body">
        <header class="topbar">
          <button class="back-button" @click="goBack">
            <ArrowLeft />
            返回匹配广场
          </button>

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
              :status-text="userStatusText"
            />
            <button v-else class="login-chip" @click="goLogin">登录</button>
          </div>
        </header>

        <section class="content-layout">
          <div class="main-column">
            <article class="hero-card">
              <div class="cover-placeholder" aria-label="活动头图留空"></div>
              <div class="hero-copy">
                <span class="category-pill">{{ post.category || '匹配需求' }}</span>
                <h1>{{ loading ? '加载中...' : post.title }}</h1>
                <p>{{ post.description }}</p>
                <div class="tag-row">
                  <span v-for="tag in post.tags" :key="tag">{{ tag }}</span>
                </div>
                <div class="meta-row">
                  <span>发布者：{{ post.publisherName }}</span>
                  <span class="verified-dot">{{ post.verified ? '学生认证' : '未认证' }}</span>
                  <span>{{ post.currentCount }}/{{ post.maxCount }}人已参与</span>
                </div>
              </div>
              <button class="share-button" @click="openShareDialog"><Share /> 分享</button>
            </article>

            <section class="detail-card">
              <div class="tab-row">
                <button class="active">详情介绍</button>
                <button>安全提醒</button>
              </div>

              <section class="info-block">
                <h2><Document /> 详细说明</h2>
                <p>{{ post.description }}</p>
              </section>

              <section class="schedule-grid">
                <div>
                  <strong><Clock /> 时间安排</strong>
                  <span>{{ post.time }}</span>
                </div>
                <div>
                  <strong><Location /> 活动地点</strong>
                  <span>{{ post.location }}</span>
                </div>
                <div>
                  <strong><Wallet /> 消费预算</strong>
                  <span>{{ post.aaFee || '费用待定' }}</span>
                </div>
                <div>
                  <strong><UserFilled /> 参与人数</strong>
                  <span><b>{{ post.currentCount }} / {{ post.maxCount }}</b> 人</span>
                </div>
              </section>

              <section class="map-row">
                <div>
                  <h2><MapLocation /> 地点地图</h2>
                  <p>{{ post.location }}</p>
                  <span>加入后请与发布者确认具体细节</span>
                </div>
                <div class="map-placeholder">
                  <MapLocation />
                </div>
              </section>
            </section>

            <section class="safety-panel">
              <div class="section-title">
                <h2>安全提醒</h2>
                <span>平台守护，放心匹配</span>
              </div>
              <div class="safety-grid">
                <article v-for="item in safetyItems" :key="item.title">
                  <component :is="item.icon" />
                  <h3>{{ item.title }}</h3>
                  <p>{{ item.text }}</p>
                </article>
              </div>
            </section>
          </div>

          <aside class="right-column">
            <section class="status-card">
              <div class="status-head">
                <span>活动状态</span>
                <strong>{{ post.full ? '已满员' : '待匹配中' }}</strong>
              </div>
              <div class="progress-count">
                <span>当前进度</span>
                <strong>{{ post.currentCount }}<em>/{{ post.maxCount }} 人</em></strong>
              </div>
              <div class="people-row">
                <UserFilled v-for="index in activeCount" :key="`active-${index}`" class="active-person" />
                <UserFilled v-for="index in emptyCount" :key="`empty-${index}`" />
              </div>
              <p>还差 <b>{{ emptyCount }}</b> 人即可成局</p>
              <button class="primary-action" :disabled="requestSubmitting || post.full" @click="applyToJoin">
                <Promotion /> {{ requestSubmitting ? '申请中...' : post.full ? '已满员' : '申请加入' }}
              </button>
              <button class="secondary-action" @click="goChatWithPublisher"><Message /> 私信发布者</button>
            </section>

            <section class="author-card">
              <h2>发布者信誉</h2>
              <div class="author-row">
                <img v-if="post.publisherAvatarUrl" class="author-avatar" :src="post.publisherAvatarUrl" alt="发布者头像" />
                <span v-else class="author-avatar">{{ post.avatarText || '星' }}</span>
                <div>
                  <strong>{{ post.publisherName }}</strong>
                  <p>{{ post.anonymous ? '匿名发布' : post.category }}</p>
                </div>
                <em>{{ post.verified ? '已认证' : '未认证' }}</em>
              </div>
              <div class="author-stats">
                <div><strong>{{ post.currentCount }}</strong><span>当前人数</span></div>
                <div><strong>{{ post.maxCount }}</strong><span>目标人数</span></div>
                <div><strong>{{ progressPercent }}</strong><span>进度</span></div>
              </div>
            </section>

            <section class="report-card">
              <div class="blank-shield" aria-label="安全与举报盾牌留空"></div>
              <h2>安全与举报</h2>
              <p>如遇虚假信息、骚扰或不良行为，请及时举报，我们会尽快处理。</p>
              <button @click="openReportDialog">举报此帖子</button>
            </section>
          </aside>
        </section>

        <section class="recommend-section">
          <div class="section-title">
            <h2>更多推荐</h2>
            <button @click="loadRecommendations">换一换 <Refresh /></button>
          </div>
          <div class="recommend-grid">
            <article v-for="item in recommendations" :key="item.id" class="recommend-card">
              <span>{{ item.category }}</span>
              <h3>{{ item.title }}</h3>
              <p>{{ item.currentCount }}/{{ item.maxCount }}人 · {{ item.time }} · {{ item.location }} · {{ item.aaFee || '费用待定' }}</p>
              <footer>
                <small>发布者：{{ item.publisherName }}</small>
                <button @click="goPost(item.id)">去看看</button>
              </footer>
            </article>
          </div>
        </section>
      </section>
    </section>

    <div v-if="shareDialogVisible" class="modal-mask" @click.self="shareDialogVisible = false">
      <section class="action-dialog">
        <header>
          <h2>分享帖子</h2>
          <button aria-label="关闭分享弹窗" @click="shareDialogVisible = false">×</button>
        </header>
        <p>复制链接后发给同学，对方可以直接打开这个匹配需求。</p>
        <label class="copy-box">
          <input :value="shareLink" readonly />
          <button @click="copyShareLink">复制</button>
        </label>
        <div class="share-preview">
          <strong>{{ post.title }}</strong>
          <span>{{ post.category }} · {{ post.currentCount }}/{{ post.maxCount }}人 · {{ post.location }} · {{ post.aaFee || '费用待定' }}</span>
        </div>
      </section>
    </div>

    <div v-if="reportDialogVisible" class="modal-mask" @click.self="closeReportDialog">
      <form class="action-dialog report-dialog" @submit.prevent="submitReport">
        <header>
          <h2>举报帖子</h2>
          <button type="button" aria-label="关闭举报弹窗" @click="closeReportDialog">×</button>
        </header>
        <label>
          <span>举报原因</span>
          <select v-model="reportForm.reason" required>
            <option value="">请选择原因</option>
            <option v-for="reason in reportReasons" :key="reason" :value="reason">{{ reason }}</option>
          </select>
        </label>
        <label>
          <span>补充说明</span>
          <textarea v-model="reportForm.detail" maxlength="500" placeholder="请描述你遇到的问题，便于后续管理员处理"></textarea>
        </label>
        <label>
          <span>联系方式</span>
          <input v-model="reportForm.contact" maxlength="80" placeholder="选填，方便管理员核实" />
        </label>
        <footer>
          <button type="button" class="dialog-secondary" @click="closeReportDialog">取消</button>
          <button type="submit" class="dialog-primary" :disabled="reportSubmitting">
            {{ reportSubmitting ? '提交中...' : '提交举报' }}
          </button>
        </footer>
      </form>
    </div>

    <div v-if="showVerifyTip" class="modal-mask" @click.self="showVerifyTip = false">
      <section class="action-dialog">
        <header>
          <h2>完成校园认证后查看帖子</h2>
          <button aria-label="关闭认证提示" @click="showVerifyTip = false">×</button>
        </header>
        <p>认证后可以查看匹配详情、发起聊天和参与匹配。</p>
        <footer>
          <button type="button" class="dialog-secondary" @click="goBack">返回广场</button>
          <button type="button" class="dialog-primary" @click="goAuthCenter">去认证</button>
        </footer>
      </section>
    </div>
  </main>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  ArrowDown,
  ArrowRight,
  Bell,
  Clock,
  Document,
  Flag,
  HomeFilled,
  Location,
  Lock,
  MapLocation,
  Message,
  Promotion,
  Refresh,
  Search,
  Share,
  StarFilled,
  User,
  UserFilled,
  Wallet,
  Warning
} from '@element-plus/icons-vue'
import logoImage from '../../../assets/images/logo-star-mascot.png'
import verifyImage from '../../../assets/images/renzheng.png'
import { fetchHomePlaza, fetchHomePostDetail, submitHomePostReport, submitMatchPostRequest } from '../../../api/home'
import { useCurrentUserProfile } from '../../../composables/useCurrentUserProfile'
import NotificationBell from '../../../components/NotificationBell.vue'
import UserMenu from '../../../components/UserMenu.vue'

const route = useRoute()
const router = useRouter()
const keyword = ref('')
const loading = ref(false)
const shareDialogVisible = ref(false)
const reportDialogVisible = ref(false)
const reportSubmitting = ref(false)
const requestSubmitting = ref(false)
const showVerifyTip = ref(false)
const post = ref({
  tags: [],
  currentCount: 0,
  maxCount: 0
})
const recommendations = ref([])
const reportReasons = ['虚假信息', '骚扰或不当言论', '广告或引流', '风险邀约', '其他问题']
const reportForm = ref({
  reason: '',
  detail: '',
  contact: ''
})

const navItems = [
  { label: '广场首页', icon: HomeFilled, route: '/home', active: true },
  { label: '发布需求', icon: Promotion, route: '/publish' },
  { label: '我的聊天', icon: Message, route: '/chat' },
  { label: '我的匹配', icon: StarFilled, route: '/my-match' },
  { label: '认证中心', icon: Lock, route: '/auth-center' },
  { label: '安全反馈', icon: Flag, route: '/safety-feedback' },
  { label: '个人中心', icon: User, route: '/profile' }
]

const safetyItems = [
  {
    title: '优先公开场地',
    text: '篮球、跑步、自习等线下活动建议选择校内或人流稳定的公开区域。',
    icon: Location
  },
  {
    title: '保护个人信息',
    text: '不要在帖子或私信中过早提供手机号、宿舍号、证件号等敏感信息。',
    icon: Lock
  },
  {
    title: '异常及时反馈',
    text: '若发现虚假邀约、骚扰或财产风险，请保留证据并使用举报入口。',
    icon: Warning
  }
]

const activeCount = computed(() => Math.max(0, Math.min(post.value.currentCount || 0, post.value.maxCount || 0)))
const emptyCount = computed(() => Math.max(0, (post.value.maxCount || 0) - (post.value.currentCount || 0)))
const progressPercent = computed(() => {
  if (!post.value.maxCount) {
    return '0%'
  }
  return `${Math.min(100, Math.round((post.value.currentCount / post.value.maxCount) * 100))}%`
})
const shareLink = computed(() => `${window.location.origin}/match-post/${route.params.id}`)
const {
  currentUser,
  isLoggedIn,
  statusText: userStatusText,
  avatarText: userInitial,
  avatarUrl: userAvatar,
  loadProfile: loadCurrentUserProfile
} = useCurrentUserProfile()

const loadPost = async () => {
  try {
    const response = await fetchHomePostDetail(route.params.id)
    post.value = response.data.data
  } catch (error) {
    const message = error.response?.data?.message || '帖子详情加载失败'
    ElMessage.error(message)
    if (error.response?.status === 403 || message.includes('认证')) {
      showVerifyTip.value = true
      return false
    }
    router.push('/home')
    return false
  }
  return true
}

const loadRecommendations = async () => {
  const response = await fetchHomePlaza({
    plaza: 'match',
    category: '全部',
    keyword: keyword.value
  })
  recommendations.value = (response.data.data.posts || [])
    .filter((item) => String(item.id) !== String(route.params.id))
    .slice(0, 3)
}

const goBack = () => {
  router.push('/home')
}

const goPost = (id) => {
  router.push(`/match-post/${id}`)
}

const goAuthCenter = () => {
  showVerifyTip.value = false
  if (isLoggedIn.value) {
    router.push('/auth-center')
    return
  }
  router.push('/login')
}

const goLogin = () => {
  router.push('/login')
}

const goChat = () => {
  router.push('/chat')
}

const goChatWithPublisher = () => {
  router.push({
    path: '/chat',
    query: {
      userId: post.value.publisherUserId || post.value.publisherId || post.value.userId,
      name: post.value.publisherName,
      badge: post.value.category,
      source: post.value.title
    }
  })
}

const applyToJoin = async () => {
  if (requestSubmitting.value || post.value.full) {
    return
  }
  requestSubmitting.value = true
  try {
    await submitMatchPostRequest(route.params.id)
    ElMessage.success('申请已发送，等待发起者同意后才会成功匹配')
    router.push('/my-match')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '申请加入失败，请稍后重试')
  } finally {
    requestSubmitting.value = false
  }
}

const openShareDialog = () => {
  shareDialogVisible.value = true
}

const copyShareLink = async () => {
  try {
    await navigator.clipboard.writeText(shareLink.value)
    ElMessage.success('分享链接已复制')
  } catch (error) {
    ElMessage.error('复制失败，请手动复制链接')
  }
}

const openReportDialog = () => {
  reportDialogVisible.value = true
}

const closeReportDialog = () => {
  reportDialogVisible.value = false
  reportForm.value = {
    reason: '',
    detail: '',
    contact: ''
  }
}

const submitReport = async () => {
  if (reportSubmitting.value) {
    return
  }
  if (!reportForm.value.reason) {
    ElMessage.error('请选择举报原因')
    return
  }
  reportSubmitting.value = true
  try {
    await submitHomePostReport(route.params.id, {
      reason: reportForm.value.reason,
      detail: reportForm.value.detail,
      contact: reportForm.value.contact
    })
    ElMessage.success('举报已提交，平台会尽快处理')
    closeReportDialog()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '举报提交失败，请稍后重试')
  } finally {
    reportSubmitting.value = false
  }
}

const handleNav = (item) => {
  if (item.route) {
    router.push(item.route)
  }
}

const loadPage = async () => {
  loading.value = true
  try {
    const loaded = await loadPost()
    if (!loaded) {
      return
    }
    await loadRecommendations()
  } finally {
    loading.value = false
  }
}

watch(() => route.params.id, loadPage)

onMounted(() => {
  loadCurrentUserProfile().catch(() => {})
  loadPage()
})
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  padding: 10px 18px;
  background:
    radial-gradient(circle at 8% 4%, rgba(255, 207, 233, 0.58), transparent 28%),
    radial-gradient(circle at 92% 5%, rgba(220, 214, 255, 0.72), transparent 34%),
    linear-gradient(135deg, #fbf8ff 0%, #f5f7ff 52%, #fff9fc 100%);
}

.detail-shell {
  display: grid;
  grid-template-columns: 245px minmax(0, 1fr);
  max-width: 1780px;
  min-height: calc(100vh - 20px);
  margin: 0 auto;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: 0 22px 52px rgba(105, 82, 166, 0.16);
  overflow: hidden;
}

.sidebar {
  display: flex;
  flex-direction: column;
  padding: 34px 20px 36px 24px;
  background: linear-gradient(180deg, rgba(253, 250, 255, 0.96), rgba(247, 244, 255, 0.9));
  box-shadow: inset -1px 0 0 rgba(224, 216, 246, 0.64);
}

.brand,
.nav-item,
.topbar,
.top-actions,
.back-button,
.search-box,
.hero-card,
.tag-row,
.meta-row,
.tab-row,
.schedule-grid strong,
.map-row,
.section-title,
.author-row,
.recommend-card footer {
  display: flex;
  align-items: center;
}

.brand {
  gap: 10px;
  margin-bottom: 28px;
}

.brand img {
  width: 72px;
  height: 58px;
  object-fit: contain;
  transform: scale(1.22);
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
}

.nav-item {
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
  margin-top: auto;
  padding: 16px;
  border: 1px solid #eee4ff;
  border-radius: 18px;
  background: linear-gradient(145deg, #fff, #f5eeff);
  box-shadow: 0 12px 26px rgba(118, 103, 201, 0.12);
}

.mini-scene {
  display: grid;
  height: 82px;
  place-items: center;
  margin-bottom: 12px;
  border-radius: 16px;
  background: linear-gradient(160deg, #f4edff, #fff9fd);
}

.mini-scene img {
  width: 118px;
}

.verify-card h2 {
  margin: 0 0 14px;
  color: #6b56ed;
  font-size: 16px;
}

.verify-card button,
.recommend-card button,
.report-card button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-radius: 999px;
  font-weight: 900;
}

.verify-card button svg,
.share-button svg,
.primary-action svg,
.secondary-action svg,
.section-title button svg,
.recommend-card button svg {
  width: 18px;
  height: 18px;
  flex: 0 0 18px;
  display: block;
}

.verify-card button {
  width: 100%;
  height: 44px;
  color: #fff;
  background: linear-gradient(135deg, #7460f4, #9d7bff);
}

.page-body {
  min-width: 0;
  padding: 28px 28px 26px;
}

.topbar {
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 20px;
}

.back-button {
  gap: 8px;
  color: #74708c;
  background: transparent;
  font-size: 15px;
  font-weight: 800;
}

.back-button svg {
  width: 18px;
}

.top-actions {
  gap: 18px;
}

.search-box {
  gap: 10px;
  width: 320px;
  height: 46px;
  padding: 0 15px 0 22px;
  border: 1px solid #eceaf5;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.88);
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
  width: 22px;
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
  width: 27px;
  height: 27px;
}

.notice-button span {
  position: absolute;
  top: -8px;
  right: -7px;
  display: grid;
  width: 22px;
  height: 22px;
  place-items: center;
  border-radius: 999px;
  color: #fff;
  background: #ef5b75;
  font-size: 12px;
  font-weight: 900;
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

.profile-avatar {
  display: grid;
  width: 50px;
  height: 50px;
  place-items: center;
  border-radius: 50%;
  color: #6853f1;
  background: #dcd5ff;
  font-size: 22px;
  font-weight: 900;
}

.profile-chip span:nth-child(2) {
  padding: 7px 12px;
  border-radius: 999px;
  background: #fff0d9;
}

.profile-chip svg {
  width: 18px;
  height: 18px;
  color: #c48a39;
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

.content-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 370px;
  gap: 20px;
}

.main-column,
.right-column {
  display: grid;
  align-content: start;
  gap: 18px;
}

.hero-card,
.detail-card,
.safety-panel,
.status-card,
.author-card,
.report-card,
.recommend-section {
  border: 1px solid #ece7f8;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.72);
  box-shadow: 0 10px 28px rgba(112, 104, 151, 0.08);
}

.hero-card {
  position: relative;
  align-items: stretch;
  gap: 28px;
  min-height: 245px;
  padding: 24px;
}

.cover-placeholder {
  width: 166px;
  min-height: 166px;
  flex: 0 0 166px;
  border: 1px dashed #d9cef9;
  border-radius: 18px;
  background: rgba(248, 244, 255, 0.74);
}

.hero-copy {
  min-width: 0;
  padding: 16px 130px 0 0;
}

.category-pill,
.tag-row span {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 13px;
  border-radius: 999px;
  color: #7a61d9;
  background: #f0ebff;
  font-size: 13px;
  font-weight: 900;
}

.category-pill {
  color: #f05d92;
  background: #fff0f6;
}

.hero-copy h1 {
  margin: 16px 0 10px;
  color: #11132d;
  font-size: 31px;
  line-height: 1.16;
}

.hero-copy p {
  margin: 0 0 16px;
  color: #7f7b95;
  font-size: 15px;
  font-weight: 700;
}

.tag-row {
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 22px;
}

.meta-row {
  flex-wrap: wrap;
  gap: 22px;
  color: #74708c;
  font-size: 14px;
  font-weight: 800;
}

.verified-dot::before {
  content: "";
  display: inline-block;
  width: 8px;
  height: 8px;
  margin-right: 7px;
  border-radius: 999px;
  background: #32c776;
}

.share-button {
  position: absolute;
  top: 38px;
  right: 34px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 98px;
  height: 42px;
  justify-content: center;
  border: 1px solid #ded3ff;
  border-radius: 999px;
  color: #745cf2;
  background: #fff;
  font-weight: 900;
}

.detail-card {
  padding: 0 18px 18px;
}

.tab-row {
  gap: 58px;
  height: 58px;
  border-bottom: 1px solid #ece7f8;
}

.tab-row button {
  position: relative;
  height: 100%;
  color: #64627a;
  background: transparent;
  font-size: 16px;
  font-weight: 900;
}

.tab-row button.active {
  color: #6853f1;
}

.tab-row button.active::after {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  bottom: -1px;
  height: 3px;
  border-radius: 999px;
  background: #6853f1;
}

.info-block {
  padding: 18px 0 16px;
  border-bottom: 1px solid #ece7f8;
}

.info-block h2,
.map-row h2,
.section-title h2,
.author-card h2,
.report-card h2 {
  margin: 0;
  color: #20233f;
  font-size: 17px;
}

.info-block h2,
.map-row h2 {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-block h2 svg,
.map-row h2 svg,
.schedule-grid svg {
  width: 20px;
  color: #6f57f0;
}

.info-block p {
  margin: 14px 0 0 28px;
  color: #5d6178;
  font-size: 14px;
  line-height: 1.8;
}

.schedule-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  padding: 18px 0;
  border-bottom: 1px solid #ece7f8;
}

.schedule-grid div {
  display: grid;
  gap: 10px;
}

.schedule-grid strong {
  gap: 8px;
  color: #20233f;
  font-size: 15px;
}

.schedule-grid span {
  color: #666b84;
  font-size: 15px;
}

.schedule-grid b {
  color: #11132d;
  font-size: 22px;
}

.map-row {
  justify-content: space-between;
  gap: 24px;
  padding-top: 18px;
}

.map-row p,
.map-row span {
  margin: 10px 0 0 28px;
  color: #666b84;
  font-size: 14px;
}

.map-placeholder {
  display: grid;
  width: 320px;
  height: 92px;
  place-items: center;
  border-radius: 12px;
  background:
    linear-gradient(90deg, rgba(148, 193, 255, 0.22) 1px, transparent 1px),
    linear-gradient(rgba(148, 193, 255, 0.22) 1px, transparent 1px),
    linear-gradient(135deg, #eef5ff, #f6f0ff);
  background-size: 36px 36px;
}

.map-placeholder svg {
  width: 42px;
  height: 42px;
  color: #735cf1;
}

.safety-panel,
.recommend-section {
  padding: 18px;
}

.section-title {
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title span,
.section-title button {
  color: #766f95;
  font-size: 13px;
  font-weight: 900;
}

.section-title button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: transparent;
}

.safety-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.safety-grid article {
  min-height: 142px;
  padding: 18px;
  border: 1px solid #eee8fb;
  border-radius: 14px;
  background: linear-gradient(145deg, #fff, #f8f4ff);
}

.safety-grid svg {
  width: 26px;
  color: #6f57f0;
}

.safety-grid h3 {
  margin: 10px 0 8px;
  color: #20233f;
  font-size: 15px;
}

.safety-grid p {
  margin: 0;
  color: #666b84;
  font-size: 13px;
  line-height: 1.65;
}

.status-card,
.author-card,
.report-card {
  padding: 20px;
}

.status-head {
  margin: -20px -20px 18px;
  padding: 22px 20px;
  border-radius: 18px 18px 0 0;
  color: #fff;
  background: linear-gradient(105deg, #7460f4, #f07aa9);
  text-align: center;
}

.status-head span {
  display: block;
  font-weight: 800;
}

.status-head strong {
  display: block;
  margin-top: 6px;
  font-size: 26px;
}

.progress-count {
  text-align: center;
}

.progress-count span {
  color: #77738b;
  font-size: 14px;
}

.progress-count strong {
  display: block;
  margin-top: 4px;
  color: #12121d;
  font-size: 36px;
}

.progress-count em {
  color: #77738b;
  font-size: 24px;
  font-style: normal;
}

.people-row {
  display: flex;
  justify-content: center;
  gap: 17px;
  margin: 14px 0;
  color: #c8cad4;
}

.people-row svg {
  width: 32px;
  height: 32px;
}

.people-row .active-person {
  color: #6f57f0;
}

.status-card p {
  margin: 0 0 20px;
  color: #555b73;
  text-align: center;
  font-size: 14px;
}

.status-card p b {
  color: #f05d92;
}

.primary-action,
.secondary-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 100%;
  height: 50px;
  border-radius: 999px;
  font-size: 16px;
  font-weight: 900;
  overflow: hidden;
}

.primary-action {
  color: #fff;
  background: linear-gradient(105deg, #7460f4, #f07aa9);
  box-shadow: 0 14px 28px rgba(129, 91, 244, 0.24);
}

.secondary-action {
  margin-top: 12px;
  color: #745cf2;
  border: 1px solid #d7ccfb;
  background: #fff;
}

.author-card h2 {
  margin-bottom: 16px;
}

.author-row {
  gap: 12px;
}

.author-avatar {
  display: grid;
  width: 58px;
  height: 58px;
  place-items: center;
  border-radius: 18px;
  color: #6853f1;
  background: linear-gradient(145deg, #ded7ff, #f3edff);
  font-size: 24px;
  font-weight: 900;
  object-fit: cover;
}

.author-row div {
  flex: 1;
}

.author-row strong {
  color: #20233f;
}

.author-row p {
  margin: 6px 0 0;
  color: #77738b;
  font-size: 13px;
}

.author-row em {
  padding: 5px 9px;
  border-radius: 999px;
  color: #32a86a;
  background: #e7f8ee;
  font-size: 12px;
  font-style: normal;
  font-weight: 900;
}

.author-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
  margin-top: 18px;
}

.author-stats div {
  padding: 10px;
  border-radius: 10px;
  background: #f5f1ff;
  text-align: center;
}

.author-stats strong,
.author-stats span {
  display: block;
}

.author-stats strong {
  color: #745cf2;
  font-size: 20px;
}

.author-stats span {
  color: #7e71ad;
  font-size: 12px;
  font-weight: 800;
}

.report-card {
  display: grid;
  justify-items: start;
}

.blank-shield {
  width: 38px;
  height: 38px;
  margin-bottom: 12px;
  border: 1px dashed #d9cef9;
  border-radius: 12px;
  background: rgba(248, 244, 255, 0.64);
}

.report-card p {
  margin: 10px 0 18px;
  color: #666b84;
  font-size: 14px;
  line-height: 1.7;
}

.report-card button {
  width: 100%;
  height: 44px;
  color: #745cf2;
  border: 1px solid #d7ccfb;
  background: #fff;
}

.recommend-section {
  margin-top: 18px;
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

.recommend-card {
  min-height: 148px;
  padding: 18px;
  border: 1px solid #f0d9df;
  border-radius: 14px;
  background: linear-gradient(145deg, #fff6f8, #fff);
}

.recommend-card span {
  display: inline-flex;
  min-height: 25px;
  align-items: center;
  padding: 0 10px;
  border-radius: 999px;
  color: #7a61d9;
  background: #f0ebff;
  font-size: 12px;
  font-weight: 900;
}

.recommend-card h3 {
  margin: 12px 0 8px;
  color: #20233f;
  font-size: 17px;
}

.recommend-card p {
  margin: 0;
  color: #77738b;
  font-size: 13px;
}

.recommend-card footer {
  justify-content: space-between;
  gap: 12px;
  margin-top: 18px;
}

.recommend-card small {
  color: #6a6d82;
  font-weight: 800;
}

.recommend-card button {
  width: 78px;
  height: 32px;
  color: #745cf2;
  border: 1px solid #d7ccfb;
  background: #fff;
  font-size: 13px;
}

.modal-mask {
  position: fixed;
  inset: 0;
  display: grid;
  place-items: center;
  padding: 20px;
  background: rgba(32, 35, 63, 0.24);
  z-index: 30;
}

.action-dialog {
  width: min(460px, 100%);
  padding: 22px;
  border: 1px solid #eee7ff;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 20px 52px rgba(87, 75, 128, 0.22);
}

.action-dialog header,
.action-dialog footer,
.copy-box {
  display: flex;
  align-items: center;
}

.action-dialog header {
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 14px;
}

.action-dialog h2 {
  margin: 0;
  color: #20233f;
  font-size: 20px;
}

.action-dialog header button {
  display: grid;
  width: 34px;
  height: 34px;
  place-items: center;
  border-radius: 50%;
  color: #745cf2;
  background: #f0ebff;
  font-size: 22px;
  font-weight: 900;
}

.action-dialog p,
.share-preview span,
.action-dialog label span {
  color: #666b84;
  font-size: 14px;
  line-height: 1.55;
}

.copy-box {
  gap: 10px;
  margin: 16px 0;
}

.copy-box input,
.report-dialog input,
.report-dialog select,
.report-dialog textarea {
  width: 100%;
  border: 1px solid #dedced;
  border-radius: 10px;
  color: #4d5068;
  background: #fff;
  outline: 0;
  font-size: 14px;
}

.copy-box input {
  height: 42px;
  padding: 0 12px;
}

.copy-box button,
.dialog-primary {
  height: 42px;
  padding: 0 18px;
  border-radius: 999px;
  color: #fff;
  background: linear-gradient(105deg, #7460f4, #f07aa9);
  font-weight: 900;
}

.copy-box button {
  min-width: 74px;
  flex: 0 0 74px;
  padding: 0 16px;
  white-space: nowrap;
}

.share-preview {
  display: grid;
  gap: 6px;
  padding: 14px;
  border-radius: 12px;
  background: #f8f4ff;
}

.share-preview strong {
  color: #20233f;
}

.report-dialog {
  display: grid;
  gap: 14px;
}

.report-dialog label {
  display: grid;
  gap: 8px;
}

.report-dialog input,
.report-dialog select {
  height: 40px;
  padding: 0 12px;
}

.report-dialog textarea {
  min-height: 104px;
  padding: 10px 12px;
  resize: vertical;
}

.action-dialog footer {
  justify-content: flex-end;
  gap: 10px;
  margin-top: 4px;
}

.dialog-secondary {
  height: 42px;
  padding: 0 18px;
  border: 1px solid #d7ccfb;
  border-radius: 999px;
  color: #745cf2;
  background: #fff;
  font-weight: 900;
}

.dialog-primary:disabled {
  opacity: 0.72;
  cursor: not-allowed;
}

@media (max-width: 1320px) {
  .content-layout {
    grid-template-columns: 1fr;
  }

  .right-column {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 980px) {
  .detail-shell {
    display: block;
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
  }

  .nav-item {
    flex: 0 0 auto;
  }

  .topbar,
  .top-actions,
  .hero-card,
  .map-row {
    align-items: flex-start;
    flex-direction: column;
  }

  .search-box,
  .map-placeholder {
    width: 100%;
  }

  .cover-placeholder {
    width: 100%;
    flex-basis: 190px;
  }

  .hero-copy {
    padding-right: 0;
  }

  .schedule-grid,
  .safety-grid,
  .right-column,
  .recommend-grid {
    grid-template-columns: 1fr;
  }
}
</style>
