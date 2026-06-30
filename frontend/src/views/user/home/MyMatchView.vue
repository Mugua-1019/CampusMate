<template>
  <main class="match-page">
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
          <h2>完善资料，匹配更精准</h2>
          <p>完成认证后，发布内容会获得更多同学信任。</p>
          <button class="btn-primary" @click="goAuthCenter">去完善资料 <ArrowRight /></button>
        </section>
      </aside>

      <section class="main-content">
        <header class="topbar">
          <div class="welcome-copy">
            <h1>我的匹配</h1>
            <p>管理你的所有匹配关系，把校园里的温暖连接延续下去。</p>
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
              :status-text="userStatusText"
            />
            <button v-else class="login-chip" @click="goLogin">登录</button>
          </div>
        </header>

        <section class="metric-grid" aria-label="匹配概览">
          <article v-for="metric in metrics" :key="metric.label" class="metric-card">
            <span :class="['metric-icon', metric.tone]"><component :is="metric.icon" /></span>
            <div>
              <p>{{ metric.label }}</p>
              <strong>{{ metric.value }}</strong>
              <small>{{ metric.note }}</small>
            </div>
          </article>
        </section>

        <section class="match-columns">
          <article class="match-panel launch-panel">
            <header class="panel-head">
              <div>
                <h2>匹配广场帖子</h2>
                <p>我发起的邀约和我申请参与的匹配都在这里</p>
              </div>
              <strong>共 {{ allMatchPosts.length }} 个匹配</strong>
            </header>

            <div class="segmented-tabs">
              <button :class="{ active: startedFilter === 'all' }" @click="startedFilter = 'all'">全部 {{ allMatchPosts.length }}</button>
              <button :class="{ active: startedFilter === 'mine' }" @click="startedFilter = 'mine'">我发起的 {{ startedPosts.length }}</button>
              <button :class="{ active: startedFilter === 'joined' }" @click="startedFilter = 'joined'">我参与的 {{ joinedPosts.length }}</button>
            </div>

            <section class="post-list">
              <article v-for="post in filteredMatchPosts" :key="`${post.role}-${post.id}`" class="match-card">
                <img class="person-avatar" :src="post.avatar" :alt="post.partner" />
                <div class="card-main">
                  <div class="title-row">
                    <h3>{{ post.title }}</h3>
                    <span class="tag">{{ post.category }}</span>
                    <span :class="['state-pill', post.state]">{{ post.stateText }}</span>
                  </div>
                  <p>{{ post.description }}</p>
                  <div class="meta-line">
                    <span v-if="post.location"><Location />{{ post.location }}</span>
                    <span v-if="post.time"><Clock />{{ post.time }}</span>
                    <span>{{ post.requestCount }} 个请求</span>
                  </div>
                  <div v-if="post.role === 'started'" class="request-box">
                    <span>{{ post.partner }} 想参与这个匹配</span>
                    <button v-if="post.state === 'requesting'" class="btn-primary" @click="approveRequest(post)">同意请求</button>
                    <button v-else class="btn-secondary" @click="chatWith(post)">聊一聊</button>
                  </div>
                  <footer v-else>
                    <span>{{ post.elapsed }}</span>
                    <button class="btn-secondary" @click="chatWith(post)">聊一聊</button>
                    <button v-if="post.state === 'pending'" class="btn-muted" disabled>等待发起者同意</button>
                    <button v-else-if="post.state === 'matched'" class="btn-success" disabled>已成功匹配</button>
                    <button v-else class="btn-primary" @click="sendMatchRequest(post)">发送匹配请求</button>
                  </footer>
                </div>
                <div class="more-menu">
                  <button class="more-icon" aria-label="更多" @click.stop="toggleMenu(post.menuId)"><MoreFilled /></button>
                  <section v-if="openMenuId === post.menuId" class="action-menu">
                    <button :disabled="post.role !== 'started'" @click="editPost(post)">编辑该帖子</button>
                    <button :disabled="post.state !== 'requesting'" @click="rejectRequest(post)">拒绝该请求</button>
                    <button @click="chatWith(post)">聊一聊</button>
                    <button @click="reportPost(post)">举报</button>
                  </section>
                </div>
              </article>
            </section>
          </article>
        </section>
      </section>

      <aside class="right-panel">
        <section class="side-card preference-card">
          <div class="side-title">
            <h2>我的匹配偏好</h2>
            <button @click="openPreferenceEditor">编辑</button>
          </div>
          <div class="preference-list">
            <p v-for="item in displayMatchPreferences" :key="item.label">
              <component :is="item.icon" />
              <strong>{{ item.label }}</strong>
              <span>{{ item.value }}</span>
            </p>
          </div>
          <button class="link-button" @click="openPreferenceEditor">查看全部偏好 <ArrowRight /></button>
        </section>

        <section class="side-card safety-card">
          <div class="side-title">
            <h2>安全提醒</h2>
            <button @click="goSafetyFeedback">更多</button>
          </div>
          <ul>
            <li>不泄露个人隐私信息</li>
            <li>谨防线下见面风险</li>
            <li>遇到不适及时举报</li>
            <li>文明交流，友善互助</li>
          </ul>
          <button class="guide-button" @click="goSafetyFeedback"><Lock /> 查看安全指南</button>
        </section>

        <section class="side-card recent-card">
          <div class="side-title">
            <h2>最近完成匹配</h2>
            <button>更多</button>
          </div>
          <article v-for="item in recentMatches" :key="item.title">
            <img :src="item.avatar" :alt="item.name" />
            <div>
              <strong>{{ item.title }}</strong>
              <span>{{ item.category }}</span>
            </div>
            <small>{{ item.time }}</small>
          </article>
        </section>
      </aside>
    </section>

    <el-dialog v-model="preferenceEditVisible" title="编辑匹配偏好" width="620px">
      <el-form class="preference-form" label-width="86px">
        <el-form-item label="匹配类型">
          <el-checkbox-group v-model="preferenceDraft.types">
            <el-checkbox-button v-for="type in matchTypeOptions" :key="type" :label="type" />
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="可见范围">
          <el-radio-group v-model="preferenceDraft.visibility">
            <el-radio-button v-for="item in visibilityOptions" :key="item" :label="item" />
          </el-radio-group>
        </el-form-item>
        <el-form-item label="活跃时间">
          <el-select v-model="preferenceDraft.activeTimes" multiple collapse-tags collapse-tags-tooltip>
            <el-option v-for="time in activeTimeOptions" :key="time" :label="time" :value="time" />
          </el-select>
        </el-form-item>
        <el-form-item label="偏好标签">
          <div class="tag-editor">
            <div class="tag-list">
              <el-tag
                v-for="tag in preferenceDraft.tags"
                :key="tag"
                closable
                @close="removePreferenceTag(tag)"
              >
                {{ tag }}
              </el-tag>
            </div>
            <div class="tag-input-row">
              <el-input v-model="preferenceTagInput" maxlength="12" @keyup.enter="addPreferenceTag" />
              <button type="button" class="dialog-save small" @click="addPreferenceTag">添加</button>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <button class="dialog-cancel" @click="preferenceEditVisible = false">取消</button>
        <button class="dialog-save" @click="savePreferenceEdit">保存偏好</button>
      </template>
    </el-dialog>
  </main>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowRight,
  ChatDotRound,
  Clock,
  Connection,
  Flag,
  HomeFilled,
  Location,
  Lock,
  Message,
  MoreFilled,
  Promotion,
  Search,
  Select,
  StarFilled,
  User,
  UserFilled
} from '@element-plus/icons-vue'
import logoImage from '../../../assets/images/logo-star-mascot.png'
import verifyImage from '../../../assets/images/renzheng.png'
import NotificationBell from '../../../components/NotificationBell.vue'
import UserMenu from '../../../components/UserMenu.vue'
import { useCurrentUserProfile } from '../../../composables/useCurrentUserProfile'
import { approveMatchRequest, fetchMyMatches, rejectMatchRequest, submitMatchPostRequest } from '../../../api/home'

const router = useRouter()
const keyword = ref('')
const startedFilter = ref('all')
const openMenuId = ref(null)
const preferenceEditVisible = ref(false)
const preferenceTagInput = ref('')
const preferenceDraft = reactive({
  types: [],
  visibility: '',
  activeTimes: [],
  tags: []
})
const {
  currentUser,
  isLoggedIn,
  statusText: userStatusText,
  avatarText: userInitial,
  avatarUrl: userAvatar,
  loadProfile: loadCurrentUserProfile
} = useCurrentUserProfile()

const navItems = [
  { label: '广场首页', icon: HomeFilled, route: '/home' },
  { label: '发布需求', icon: Promotion, route: '/publish' },
  { label: '我的聊天', icon: Message, route: '/chat' },
  { label: '我的匹配', icon: StarFilled, route: '/my-match', active: true },
  { label: '认证中心', icon: Lock, route: '/auth-center' },
  { label: '安全反馈', icon: Flag, route: '/safety-feedback' },
  { label: '个人中心', icon: User, route: '/profile' }
]

const makeAvatar = (face, hair, shirt) => {
  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 80 80">
      <rect width="80" height="80" rx="24" fill="#f3efff"/>
      <circle cx="40" cy="36" r="18" fill="${face}"/>
      <path d="M20 34c3-17 16-23 29-17 9 4 12 13 11 20-8-6-18-8-40-3z" fill="${hair}"/>
      <path d="M18 72c4-15 16-23 22-23s18 8 22 23H18z" fill="${shirt}"/>
      <circle cx="33" cy="38" r="2.5" fill="#2b2440"/>
      <circle cx="47" cy="38" r="2.5" fill="#2b2440"/>
      <path d="M34 47c4 3 8 3 12 0" fill="none" stroke="#9f5f6c" stroke-width="2" stroke-linecap="round"/>
    </svg>
  `
  return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(svg)}`
}

const avatarPool = [
  makeAvatar('#ffd1c7', '#31324a', '#68d3d5'),
  makeAvatar('#f2c5aa', '#28324e', '#7aa7ff'),
  makeAvatar('#ffd6c5', '#5b406b', '#ff92bd'),
  makeAvatar('#ffc7bd', '#1f2740', '#8a78ff'),
  makeAvatar('#f4c3a4', '#4d355b', '#75d18b')
]

const metricIcons = [StarFilled, Connection, Select, StarFilled, ChatDotRound]
const matchPreferenceStorageKey = computed(() => `campusmate-match-preference-${currentUser.value?.userId || 'guest'}`)
const matchTypeOptions = ['学习搭子', '饭搭子', '运动搭子', '比赛组队', '活动同行', '闲聊陪伴']
const visibilityOptions = ['仅校内认证用户可见', '仅同学院可见', '所有同学可见']
const activeTimeOptions = ['工作日早上', '工作日中午', '工作日晚上', '周末白天', '周末晚上', '随时可约']
const defaultMatchPreference = {
  types: ['学习搭子', '运动搭子', '饭搭子', '闲聊陪伴'],
  visibility: '仅校内认证用户可见',
  activeTimes: ['工作日晚上', '周末白天'],
  tags: ['自律', '真诚', '有耐心', '友善', '积极']
}
const matchPreference = ref({ ...defaultMatchPreference })
const displayMatchPreferences = computed(() => [
  { label: '匹配类型', value: joinPreference(matchPreference.value.types), icon: StarFilled },
  { label: '可见范围', value: matchPreference.value.visibility || '未设置', icon: UserFilled },
  { label: '活跃时间', value: joinPreference(matchPreference.value.activeTimes), icon: Clock },
  { label: '偏好标签', value: joinPreference(matchPreference.value.tags), icon: StarFilled }
])
const metrics = ref([
  { label: '总匹配数', value: 12, note: '较上周 +3', icon: StarFilled, tone: 'pink' },
  { label: '待我处理', value: 4, note: '有新请求待同意', icon: Connection, tone: 'purple' },
  { label: '已完成', value: 4, note: '一起很棒的经历', icon: Select, tone: 'blue' },
  { label: '互动好感', value: '98%', note: '缘分指数很高', icon: StarFilled, tone: 'yellow' },
  { label: '本周新增', value: 3, note: '新连接在向你靠近', icon: ChatDotRound, tone: 'green' }
])

const startedPosts = ref([
  {
    id: 1,
    menuId: 'started-1',
    role: 'started',
    title: '今晚图书馆自习',
    category: '学习搭子',
    description: '一起到图书馆安静学习，互相监督，效率加倍。',
    location: '图书馆二楼自习区',
    time: '今晚 19:00',
    requestCount: 3,
    partner: '林同学',
    state: 'requesting',
    stateText: '我发起的',
    avatar: avatarPool[0]
  },
  {
    id: 2,
    menuId: 'started-2',
    role: 'started',
    title: '周末操场跑步',
    category: '运动搭子',
    description: '慢跑三公里，新手友好，结束后一块拉伸。',
    location: '操场西侧入口',
    time: '周六 08:00',
    requestCount: 2,
    partner: '周同学',
    state: 'requesting',
    stateText: '我发起的',
    avatar: avatarPool[1]
  },
  {
    id: 3,
    menuId: 'started-3',
    role: 'started',
    title: '二食堂午饭搭子',
    category: '饭搭子',
    description: '中午一起吃饭，想试新窗口，顺便聊聊天。',
    location: '第二食堂二楼',
    time: '今天 12:10',
    requestCount: 1,
    partner: '沈同学',
    state: 'matched',
    stateText: '已成功匹配',
    avatar: avatarPool[2]
  }
])

const joinedPosts = ref([
  {
    id: 11,
    menuId: 'joined-11',
    role: 'joined',
    title: '想找人聊聊最近的压力',
    category: '学业压力',
    description: '期末临近，感觉有点焦虑，想找人聊聊。',
    location: '线上文字',
    time: '今晚',
    requestCount: 1,
    elapsed: '已聊 2 天',
    tags: ['焦虑', '压力大', '睡眠不好'],
    state: 'pending',
    stateText: '我参与的',
    partner: '许同学',
    avatar: avatarPool[3]
  },
  {
    id: 12,
    menuId: 'joined-12',
    role: 'joined',
    title: '心情有点低落',
    category: '情绪陪伴',
    description: '最近状态不太好，想找个朋友聊聊。',
    location: '线上文字',
    time: '随时',
    requestCount: 1,
    elapsed: '已聊 1 天',
    tags: ['低落', '疲惫', '需要陪伴'],
    state: 'open',
    stateText: '我参与的',
    partner: '郑同学',
    avatar: avatarPool[4]
  },
  {
    id: 13,
    menuId: 'joined-13',
    role: 'joined',
    title: '情绪稳定，想陪你聊聊',
    category: '陪伴倾听',
    description: '心情再小的事，也值得被倾听～',
    location: '校园咖啡角',
    time: '今天下午',
    requestCount: 1,
    elapsed: '已聊 6 天',
    tags: ['耐心倾听', '情绪支持', '随时在线'],
    state: 'matched',
    stateText: '已成功匹配',
    partner: '夏同学',
    avatar: avatarPool[0]
  }
])

const recentMatches = ref([
  { name: '一一', title: '一起复习线性代数', category: '学习搭子', time: '3 天前', avatar: avatarPool[0] },
  { name: '阿周', title: '周末羽毛球约球', category: '运动搭子', time: '1 周前', avatar: avatarPool[1] },
  { name: '深夜', title: '深夜 emo 聊天树洞', category: '情感陪伴', time: '2 周前', avatar: avatarPool[3] }
])

const allMatchPosts = computed(() => [...startedPosts.value, ...joinedPosts.value])
const filteredMatchPosts = computed(() => {
  const source = startedFilter.value === 'mine'
    ? startedPosts.value
    : startedFilter.value === 'joined'
      ? joinedPosts.value
      : allMatchPosts.value
  return source.filter((post) => matchesKeyword(post))
})

const matchesKeyword = (post) => {
  const value = keyword.value.trim()
  if (!value) return true
  return [post.title, post.category, post.description, post.partner].some((field) => field?.includes(value))
}

const normalizeStartedPost = (item, index) => ({
  id: item.requestId,
  menuId: `started-${item.requestId}`,
  role: 'started',
  requestId: item.requestId,
  postId: item.postId,
  peerUserId: item.peerUserId,
  title: item.title,
  category: item.category,
  description: item.description,
  location: item.location,
  time: item.time,
  requestCount: item.requestCount,
  partner: item.partner,
  state: item.status === 'approved' ? 'matched' : 'requesting',
  stateText: item.status === 'approved' ? '已成功匹配' : '我发起的',
  avatar: item.avatarUrl || avatarPool[index % avatarPool.length]
})

const normalizeJoinedPost = (item, index) => ({
  id: item.requestId,
  menuId: `joined-${item.requestId}`,
  role: 'joined',
  requestId: item.requestId,
  postId: item.postId,
  peerUserId: item.peerUserId,
  title: item.title,
  category: item.category,
  description: item.description,
  location: item.location,
  time: item.time,
  requestCount: item.requestCount,
  elapsed: item.elapsed,
  tags: item.tags || [item.category, item.stateText],
  state: item.status === 'approved' ? 'matched' : 'pending',
  stateText: '我参与的',
  partner: item.partner,
  avatar: item.avatarUrl || avatarPool[index % avatarPool.length]
})

const approveRequest = async (post) => {
  if (!post?.requestId) return
  try {
    await approveMatchRequest(post.requestId)
    post.state = 'matched'
    post.stateText = '已成功匹配'
    await loadMyMatches()
    ElMessage.success('已同意请求，现在才算成功匹配')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '同意请求失败')
  }
}

const rejectRequest = async (post) => {
  if (!post?.requestId || post.state !== 'requesting') return
  try {
    await rejectMatchRequest(post.requestId)
    await loadMyMatches()
    openMenuId.value = null
    ElMessage.success('已拒绝该请求')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '拒绝请求失败')
  }
}

const sendMatchRequest = async (post) => {
  if (!post?.postId) return
  try {
    await submitMatchPostRequest(post.postId)
    post.state = 'pending'
    post.stateText = '我参与的'
    await loadMyMatches()
    ElMessage.success('匹配请求已发送，等待发起者同意')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '发送匹配请求失败')
  }
}

const handleNav = (item) => {
  if (item.route) router.push(item.route)
}

const toggleMenu = (id) => {
  openMenuId.value = openMenuId.value === id ? null : id
}

const editPost = (post) => {
  openMenuId.value = null
  router.push({
    path: '/publish',
    query: {
      editPostId: post.postId || post.id
    }
  })
}

const joinPreference = (items) => {
  return Array.isArray(items) && items.length ? items.join('、') : '未设置'
}

const normalizePreference = (preference) => ({
  types: Array.isArray(preference?.types) ? preference.types.filter(Boolean).slice(0, 8) : [...defaultMatchPreference.types],
  visibility: visibilityOptions.includes(preference?.visibility) ? preference.visibility : defaultMatchPreference.visibility,
  activeTimes: Array.isArray(preference?.activeTimes)
    ? preference.activeTimes.filter((item) => activeTimeOptions.includes(item))
    : [...defaultMatchPreference.activeTimes],
  tags: Array.isArray(preference?.tags)
    ? [...new Set(preference.tags.map((tag) => String(tag).trim()).filter(Boolean))].slice(0, 12)
    : [...defaultMatchPreference.tags]
})

const loadMatchPreference = () => {
  try {
    const saved = window.localStorage.getItem(matchPreferenceStorageKey.value)
    matchPreference.value = normalizePreference(saved ? JSON.parse(saved) : defaultMatchPreference)
  } catch (error) {
    matchPreference.value = { ...defaultMatchPreference }
  }
}

const openPreferenceEditor = () => {
  preferenceDraft.types = [...matchPreference.value.types]
  preferenceDraft.visibility = matchPreference.value.visibility
  preferenceDraft.activeTimes = [...matchPreference.value.activeTimes]
  preferenceDraft.tags = [...matchPreference.value.tags]
  preferenceTagInput.value = ''
  preferenceEditVisible.value = true
}

const addPreferenceTag = () => {
  const tag = preferenceTagInput.value.trim()
  if (!tag) return
  if (!preferenceDraft.tags.includes(tag)) {
    preferenceDraft.tags.push(tag)
  }
  preferenceTagInput.value = ''
}

const removePreferenceTag = (tag) => {
  preferenceDraft.tags = preferenceDraft.tags.filter((item) => item !== tag)
}

const savePreferenceEdit = () => {
  matchPreference.value = normalizePreference(preferenceDraft)
  window.localStorage.setItem(matchPreferenceStorageKey.value, JSON.stringify(matchPreference.value))
  preferenceEditVisible.value = false
  ElMessage.success('匹配偏好已更新')
}

const chatWith = (post) => {
  openMenuId.value = null
  if (!post?.peerUserId) {
    ElMessage.error('缺少对方用户信息，暂时无法打开聊天')
    return
  }
  router.push({
    path: '/chat',
    query: {
      userId: post.peerUserId,
      name: post.partner,
      badge: post.category,
      source: post.title
    }
  })
}

const reportPost = (post) => {
  openMenuId.value = null
  if (post.postId) {
    router.push(`/match-post/${post.postId}`)
    ElMessage.info('已打开帖子详情，可在右侧提交举报')
    return
  }
  ElMessage.info('举报入口需要帖子详情')
}

const goChat = () => {
  router.push('/chat')
}

const goLogin = () => {
  router.push('/login')
}

const goAuthCenter = () => {
  router.push(isLoggedIn.value ? '/auth-center' : '/login')
}

const goSafetyFeedback = () => {
  router.push('/safety-feedback')
}

const loadMyMatches = async () => {
  if (!isLoggedIn.value) {
    return
  }
  try {
    const response = await fetchMyMatches()
    const payload = response.data.data || {}
    startedPosts.value = (payload.startedPosts || []).map(normalizeStartedPost)
    joinedPosts.value = (payload.joinedPosts || []).map(normalizeJoinedPost)
    recentMatches.value = (payload.recentMatches || []).map((item, index) => ({
      title: item.title,
      category: item.category,
      time: item.time,
      avatar: item.avatarUrl || avatarPool[index % avatarPool.length]
    }))
    metrics.value = (payload.metrics || []).map((item, index) => ({
      ...item,
      icon: metricIcons[index] || StarFilled
    }))
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '我的匹配数据加载失败，暂时显示本地示例')
  }
}

onMounted(async () => {
  loadMatchPreference()
  loadCurrentUserProfile().catch(() => {})
  loadMyMatches()
})
</script>

<style scoped>
.match-page {
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
  padding: 34px 0 78px 16px;
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
  display: grid;
  height: 82px;
  place-items: center;
  margin-bottom: 10px;
  border-radius: 16px;
  background: linear-gradient(160deg, #f4edff, #fff9fd);
}

.mini-scene img {
  width: 118px;
}

.side-card h2 {
  margin: 0;
  color: #20233f;
  font-size: 17px;
}

.verify-card h2 {
  margin: 0;
  color: #6b56ed;
  font-size: 16px;
}

.verify-card p {
  margin: 8px 0 12px;
  color: #77738b;
  font-size: 13px;
  line-height: 1.5;
}

.verify-card .btn-primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  width: 100%;
  min-height: 40px;
}

.verify-card .btn-primary svg {
  width: 16px;
  height: 16px;
}

.main-content {
  min-width: 0;
  padding-top: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.28);
}

.topbar,
.top-actions,
.metric-card,
.panel-head,
.title-row,
.meta-line,
.request-box,
.match-card footer,
.side-title,
.link-button,
.guide-button {
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
  max-width: 420px;
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

.metric-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 26px;
}

.metric-card {
  gap: 16px;
  min-height: 112px;
  padding: 18px;
  border: 1px solid #ece7f8;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.72);
  box-shadow: 0 10px 24px rgba(112, 104, 151, 0.06);
}

.metric-icon {
  display: grid;
  width: 58px;
  height: 58px;
  flex: 0 0 58px;
  place-items: center;
  border-radius: 50%;
}

.metric-icon svg {
  width: 27px;
  height: 27px;
}

.metric-icon.pink { color: #f1518d; background: #ffe4f1; }
.metric-icon.purple { color: #7357ee; background: #eee8ff; }
.metric-icon.blue { color: #4f7ee8; background: #e9f0ff; }
.metric-icon.yellow { color: #f6b600; background: #fff3cf; }
.metric-icon.green { color: #60bf70; background: #e6f8e9; }

.metric-card p,
.metric-card small {
  margin: 0;
  color: #77738b;
  font-size: 12px;
  font-weight: 800;
}

.metric-card strong {
  display: block;
  margin: 3px 0;
  color: #12121d;
  font-size: 26px;
}

.match-columns {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 20px;
}

.match-panel {
  padding: 22px 20px 16px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.54);
}

.launch-panel {
  border: 1px solid rgba(239, 110, 167, 0.32);
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.48);
}

.join-panel {
  border: 1px solid rgba(99, 116, 242, 0.28);
}

.panel-head {
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.panel-head h2 {
  margin: 0 0 5px;
  color: #ec4f92;
  font-size: 22px;
}

.join-panel .panel-head h2 {
  color: #5d67ef;
}

.panel-head p {
  margin: 0;
  color: #7c7891;
  font-size: 13px;
  font-weight: 800;
}

.panel-head strong {
  color: #9d4670;
  font-size: 13px;
  white-space: nowrap;
}

.join-panel .panel-head strong {
  color: #5960b5;
}

.segmented-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.segmented-tabs button {
  min-width: 92px;
  height: 34px;
  padding: 0 16px;
  border: 1px solid #ece8f7;
  border-radius: 999px;
  color: #77738b;
  background: #fff;
  font-size: 13px;
  font-weight: 900;
}

.segmented-tabs button.active {
  color: #fff;
  border-color: transparent;
  background: linear-gradient(105deg, #755df4, #ef6ea7);
}

.post-list {
  display: grid;
  gap: 14px;
}

.match-card {
  position: relative;
  display: grid;
  grid-template-columns: 64px minmax(0, 1fr) 30px;
  gap: 14px;
  min-height: 142px;
  padding: 16px;
  border: 1px solid #eee9f7;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.86);
  box-shadow: 0 10px 24px rgba(112, 104, 151, 0.07);
}

.person-avatar {
  width: 58px;
  height: 58px;
  border-radius: 18px;
  background: #f1edff;
  object-fit: cover;
}

.card-main {
  min-width: 0;
}

.title-row {
  gap: 8px;
  align-items: flex-start;
}

.title-row h3 {
  flex: 1;
  min-width: 0;
  margin: 0;
  overflow: hidden;
  color: #171724;
  font-size: 17px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tag,
.state-pill,
.tag-row span {
  flex: 0 0 auto;
  padding: 5px 9px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 900;
}

.tag {
  color: #745cf2;
  background: #f0ebff;
}

.state-pill {
  color: #3eb95c;
  background: #e8f8e9;
}

.state-pill.requesting,
.state-pill.pending {
  color: #ce8a21;
  background: #fff1d4;
}

.state-pill.matched {
  color: #f0a800;
  background: #fff0c6;
}

.match-card p {
  margin: 9px 0 12px;
  overflow: hidden;
  color: #68677d;
  font-size: 13px;
  line-height: 1.45;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.meta-line {
  gap: 15px;
  color: #77798d;
  font-size: 12px;
}

.meta-line span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.meta-line svg {
  width: 15px;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-row span {
  color: #7660ef;
  background: #f2efff;
}

.request-box {
  justify-content: space-between;
  gap: 12px;
  margin-top: 12px;
  color: #756f88;
  font-size: 13px;
  font-weight: 800;
}

.match-card footer {
  justify-content: flex-end;
  gap: 10px;
  margin-top: 14px;
}

.match-card footer > span {
  margin-right: auto;
  color: #8a879b;
  font-size: 12px;
  font-weight: 800;
}

.btn-primary,
.btn-secondary,
.btn-muted,
.btn-success {
  min-height: 32px;
  padding: 0 16px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 900;
}

.btn-primary {
  color: #fff;
  background: linear-gradient(135deg, #7460f4, #9d7bff);
}

.btn-secondary {
  color: #765ff3;
  border: 1px solid #d8cff9;
  background: #fff;
}

.btn-muted {
  color: #a4864a;
  background: #fff1d4;
}

.btn-success {
  color: #4fae62;
  background: #e7f8e9;
}

.more-icon {
  display: grid;
  width: 30px;
  height: 30px;
  place-items: center;
  border: 1px solid #eee8f7;
  border-radius: 999px;
  color: #8b6ef3;
  background: #fff;
}

.more-menu {
  position: relative;
}

.action-menu {
  position: absolute;
  top: 36px;
  right: 0;
  z-index: 12;
  display: grid;
  width: 126px;
  padding: 6px;
  border: 1px solid #eee7ff;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 14px 30px rgba(87, 75, 128, 0.16);
}

.action-menu button {
  height: 34px;
  padding: 0 10px;
  border-radius: 8px;
  color: #5f5b78;
  background: transparent;
  font-size: 13px;
  font-weight: 800;
  text-align: left;
}

.action-menu button:hover {
  color: #6d55f0;
  background: #f3efff;
}

.action-menu button:disabled {
  color: #c1bdce;
  background: transparent;
  cursor: not-allowed;
}

.more-icon svg {
  width: 16px;
  height: 16px;
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

.side-title button,
.link-button {
  color: #8a6cf2;
  background: transparent;
  font-weight: 800;
}

.preference-list {
  display: grid;
  gap: 14px;
}

.preference-list p {
  display: grid;
  grid-template-columns: 34px 1fr;
  gap: 2px 10px;
  margin: 0;
  color: #69677c;
  font-size: 12px;
}

.preference-list svg {
  grid-row: span 2;
  width: 34px;
  height: 34px;
  padding: 8px;
  border-radius: 10px;
  color: #745cf2;
  background: #f0ebff;
}

.preference-list strong {
  color: #2d2b3f;
}

.link-button {
  justify-content: flex-start;
  gap: 6px;
  min-height: 30px;
  margin-top: 16px;
  white-space: nowrap;
}

.link-button svg {
  width: 16px;
  height: 16px;
  flex: 0 0 16px;
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

.guide-button {
  justify-content: center;
  gap: 8px;
  width: 100%;
  height: 34px;
  border: 1px solid #ded3ff;
  border-radius: 999px;
  color: #745cf2;
  background: #fff;
  font-weight: 900;
}

.guide-button svg {
  width: 16px;
  height: 16px;
  flex: 0 0 16px;
}

.recent-card {
  display: grid;
  gap: 12px;
}

.recent-card article {
  display: grid;
  grid-template-columns: 38px 1fr auto;
  gap: 10px;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0ecfb;
}

.recent-card img {
  width: 38px;
  height: 38px;
  border-radius: 12px;
}

.recent-card strong,
.recent-card span {
  display: block;
}

.recent-card strong {
  overflow: hidden;
  color: #26263b;
  font-size: 13px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recent-card span,
.recent-card small {
  color: #897ff0;
  font-size: 12px;
  font-weight: 800;
}

.preference-form :deep(.el-select) {
  width: 100%;
}

.tag-editor {
  display: grid;
  width: 100%;
  gap: 10px;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  min-height: 32px;
}

.tag-input-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 10px;
}

.dialog-cancel,
.dialog-save {
  min-width: 82px;
  height: 34px;
  margin-left: 10px;
  border-radius: 999px;
  font-weight: 900;
}

.dialog-cancel {
  color: #626681;
  border: 1px solid #dedced;
  background: #fff;
}

.dialog-save {
  color: #fff;
  background: linear-gradient(135deg, #7460f4, #9d7bff);
}

.dialog-save.small {
  min-width: 64px;
  margin-left: 0;
}

@media (max-width: 1320px) {
  .app-shell {
    grid-template-columns: 220px minmax(0, 1fr);
  }

  .right-panel {
    grid-column: 2;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    padding-top: 0;
  }

  .metric-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .match-columns {
    grid-template-columns: 1fr;
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

  .search-box {
    width: 100%;
  }

  .metric-grid,
  .right-panel {
    grid-template-columns: 1fr;
  }
}
</style>
