<template>
  <main class="vent-page">
    <section class="vent-shell">
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
      </aside>

      <section class="page-body">
        <header class="topbar">
          <button class="back-button" @click="goBack">
            <ArrowLeft />
            返回倾诉广场
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
          <section class="main-column">
            <article class="post-hero">
              <div class="title-area">
                <h1>{{ post.title || '倾诉详情' }}</h1>
                <div class="tag-row">
                  <span v-for="tag in tagChips" :key="tag">{{ tag }}</span>
                </div>
              </div>

              <button class="more-button" aria-label="更多操作">
                <MoreFilled />
              </button>

              <div class="author-line">
                <img v-if="post.publisherAvatarUrl" class="author-avatar" :src="post.publisherAvatarUrl" alt="发布者头像" />
                <span v-else class="author-avatar">{{ post.avatarText || '匿' }}</span>
                <div>
                  <strong>{{ post.publisherName || '匿名同学' }} <CircleCheckFilled /></strong>
                  <p>{{ post.publisherStatus || '等待倾听中' }} · 收获 {{ post.currentCount || 0 }} 个安慰 · 仅心灵伙伴可见</p>
                </div>
              </div>
            </article>

            <article class="story-card">
              <div class="story-copy">
                <h2><ChatDotRound /> 倾诉内容</h2>
                <p v-for="paragraph in storyParagraphs" :key="paragraph">{{ paragraph }}</p>
              </div>

              <section class="preference-panel">
                <div class="preference-block">
                  <h3><StarFilled /> 我现在的状态</h3>
                  <div class="chip-group mood">
                    <span v-for="item in currentState" :key="item">{{ item }}</span>
                  </div>
                </div>
                <div class="preference-block">
                  <h3><StarFilled /> 希望你可以</h3>
                  <ul>
                    <li v-for="item in hopeYouCan" :key="item"><CircleCheckFilled /> {{ item }}</li>
                  </ul>
                </div>
                <div class="preference-block">
                  <h3><ChatDotRound /> 偏好方式</h3>
                  <div class="method-box">
                    <span v-for="(item, index) in preferredWay" :key="item">
                      <component :is="index === 0 ? ChatRound : Clock" /> {{ item }}
                    </span>
                  </div>
                </div>
              </section>
            </article>

            <section class="action-card">
              <button class="primary-action" @click="goChatWithPublisher"><Promotion /> 聊一聊</button>
              <button class="comfort-action" :disabled="comfortSubmitting" @click="sendComfort">
                <span class="heart-icon" aria-hidden="true">♡</span>
                {{ comfortSubmitting ? '发送中...' : '发送安慰' }}
                <span class="comfort-count">{{ post.currentCount || 0 }}</span>
              </button>
              <button class="collect-action"><Star /> 收藏帖子</button>
              <p><Lock /> 请保持尊重与善意，你的陪伴可能会带来很大的温暖。</p>
            </section>

            <section class="replies-card">
              <div class="side-title">
                <h2>温柔回应 <small>({{ replies.length }})</small></h2>
              </div>
              <form class="reply-form" @submit.prevent="submitReply">
                <textarea
                  v-model.trim="replyContent"
                  maxlength="1000"
                  placeholder="写下一句温柔回应"
                  rows="3"
                ></textarea>
                <div>
                  <span>{{ replyContent.length }}/1000</span>
                  <button type="submit" :disabled="replySubmitting || !replyContent">
                    {{ replySubmitting ? '发送中...' : '发送回应' }}
                  </button>
                </div>
              </form>
              <p v-if="!replies.length" class="reply-empty">还没有回应，先把一点善意留在这里。</p>
              <article v-for="reply in replies" :key="reply.id || reply.name" class="reply-item">
                <span>{{ reply.avatar }}</span>
                <div>
                  <header>
                    <strong>{{ reply.name }}</strong>
                    <em>{{ reply.badge }}</em>
                    <time>{{ reply.time }}</time>
                  </header>
                  <p>{{ reply.text }}</p>
                  <small><Star /> {{ reply.likes }}</small>
                </div>
              </article>
            </section>
          </section>

          <aside class="right-column">
            <section class="side-card publish-status">
              <h2>发帖者状态 <QuestionFilled /></h2>
              <div class="status-item">
                <span class="status-icon purple"><ChatDotRound /></span>
                <div>
                  <strong>{{ post.anonymous ? '匿名发布' : '实名发布' }}</strong>
                  <p>{{ post.anonymous ? '身份仅对匹配的倾听者可见' : '发帖者愿意展示昵称' }}</p>
                </div>
              </div>
              <div class="status-item">
                <span class="status-icon green"><ChatRound /></span>
                <div>
                  <strong>{{ post.publisherStatus || '回复开放中' }}</strong>
                  <p>{{ post.publisherStatusNote || 'TA 目前希望收到回复' }}</p>
                </div>
              </div>
              <p class="status-note"><Star /> 尊重对方意愿，真诚陪伴。</p>
            </section>

            <section class="side-card safety-card">
              <div>
                <span class="safety-label">安全小贴士</span>
                <h2>保护自己，也尊重他人</h2>
                <div class="safety-points">
                  <p><Lock /> 不透露真实姓名、学校、联系方式等隐私信息</p>
                  <p><ChatDotRound /> 遇到不适或困扰，请及时呼救，我们会尽快处理</p>
                </div>
              </div>
              <span class="shield-visual"><Lock /></span>
            </section>

            <section class="side-card similar-card">
              <div class="side-title">
                <h2>相似倾诉</h2>
                <button>换一换</button>
              </div>
              <article v-for="item in similarPosts" :key="item.title" class="similar-item">
                <span>{{ item.avatarText || '倾' }}</span>
                <div>
                  <strong>{{ item.title }}</strong>
                  <p>
                    <em v-for="tag in item.tags" :key="tag">{{ tag }}</em>
                  </p>
                </div>
              </article>
            </section>
          </aside>
        </section>
      </section>
    </section>
  </main>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Bell,
  ChatDotRound,
  ChatRound,
  CircleCheckFilled,
  Clock,
  Flag,
  HomeFilled,
  Lock,
  Message,
  MoreFilled,
  Promotion,
  QuestionFilled,
  Search,
  Star,
  StarFilled,
  User
} from '@element-plus/icons-vue'
import logoImage from '../../../assets/images/logo-star-mascot.png'
import { fetchVentPostDetail, submitVentPostComfort, submitVentPostReply } from '../../../api/home'
import { useCurrentUserProfile } from '../../../composables/useCurrentUserProfile'
import NotificationBell from '../../../components/NotificationBell.vue'
import UserMenu from '../../../components/UserMenu.vue'

const route = useRoute()
const router = useRouter()
const keyword = ref('')
const post = ref({})
const loading = ref(false)
const apiError = ref('')
const replyContent = ref('')
const replySubmitting = ref(false)
const comfortSubmitting = ref(false)
const {
  isLoggedIn,
  statusText: userStatusText,
  avatarText: userInitial,
  avatarUrl: userAvatar,
  loadProfile: loadCurrentUserProfile
} = useCurrentUserProfile()

const navItems = [
  { label: '广场首页', icon: HomeFilled, route: '/home', active: true },
  { label: '发布需求', icon: Promotion },
  { label: '我的聊天', icon: Message, route: '/chat' },
  { label: '我的匹配', icon: StarFilled },
  { label: '认证中心', icon: Lock, route: '/auth-center' },
  { label: '安全反馈', icon: Flag },
  { label: '个人中心', icon: User, route: '/profile' }
]

const fallbackSimilarPosts = [
  { icon: '☁', title: '晚上想找个人聊聊天，感觉有点emo', tags: ['心情不好', '只想被听见', '在线文字'] },
  { icon: '⭐', title: '最近压力有点大，想找人听我说说', tags: ['学业压力', '只想被听见', '在线文字'] },
  { icon: '🌥', title: '有点迷茫，想找人聊聊未来的方向', tags: ['迷茫困惑', '希望建议', '在线文字'] }
]

const tagChips = computed(() => {
  const tags = [
    post.value.category,
    post.value.anonymous ? '匿名发布' : '实名发布',
    ...(post.value.tags || [])
  ].filter(Boolean)
  return [...new Set(tags)].slice(0, 4)
})

const storyParagraphs = computed(() => {
  const description = post.value.description || ''
  return description
    .split(/\r?\n/)
    .map((item) => item.trim())
    .filter(Boolean)
})

const currentState = computed(() => post.value.currentState?.length ? post.value.currentState : ['想被理解'])
const hopeYouCan = computed(() => post.value.hopeYouCan?.length ? post.value.hopeYouCan : ['耐心倾听'])
const preferredWay = computed(() => post.value.preferredWay?.length ? post.value.preferredWay : [post.value.location, post.value.time].filter(Boolean))

const replies = computed(() => {
  return (post.value.replies || []).map((reply) => ({
    id: reply.id,
    avatar: reply.avatarText || '同',
    name: reply.authorName || '温柔同学',
    badge: '心灵伙伴',
    time: formatReplyTime(reply.createdAt),
    text: reply.content,
    likes: reply.likeCount || 0
  }))
})

const similarPosts = computed(() => {
  if (post.value.similarPosts?.length) {
    return post.value.similarPosts.map((item) => ({
      ...item,
      tags: item.tags || []
    }))
  }
  return fallbackSimilarPosts
})

const formatReplyTime = (value) => {
  if (!value) {
    return '刚刚'
  }
  const replyTime = new Date(value)
  if (Number.isNaN(replyTime.getTime())) {
    return '刚刚'
  }
  const diffMinutes = Math.max(0, Math.floor((Date.now() - replyTime.getTime()) / 60000))
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

const loadVentPost = async () => {
  loading.value = true
  apiError.value = ''
  try {
    const response = await fetchVentPostDetail(route.params.id)
    post.value = response.data.data || {}
  } catch (error) {
    apiError.value = '倾诉详情加载失败，请确认已登录并完成校园认证。'
    ElMessage.error(apiError.value)
  } finally {
    loading.value = false
  }
}

const submitReply = async () => {
  if (!replyContent.value || replySubmitting.value) {
    return
  }
  replySubmitting.value = true
  try {
    const response = await submitVentPostReply(route.params.id, {
      content: replyContent.value
    })
    const createdReply = response.data.data
    post.value = {
      ...post.value,
      replies: [...(post.value.replies || []), createdReply]
    }
    replyContent.value = ''
    ElMessage.success('回应已发送')
  } catch (error) {
    ElMessage.error('回应发送失败，请确认已登录并完成校园认证。')
  } finally {
    replySubmitting.value = false
  }
}

const sendComfort = async () => {
  if (comfortSubmitting.value) {
    return
  }
  comfortSubmitting.value = true
  try {
    const response = await submitVentPostComfort(route.params.id)
    post.value = {
      ...post.value,
      currentCount: response.data.data?.currentCount ?? post.value.currentCount
    }
    ElMessage.success('安慰已送达')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '发送安慰失败，请确认已登录并完成校园认证。')
  } finally {
    comfortSubmitting.value = false
  }
}

const goBack = () => {
  router.push('/home')
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

const handleNav = (item) => {
  if (item.route) {
    router.push(item.route)
  }
}

onMounted(() => {
  loadCurrentUserProfile().catch(() => {})
  loadVentPost()
})
</script>

<style scoped>
.vent-page {
  min-height: 100vh;
  padding: 14px 20px;
  background:
    radial-gradient(circle at 10% 0%, rgba(255, 215, 238, 0.58), transparent 28%),
    radial-gradient(circle at 92% 8%, rgba(226, 214, 255, 0.78), transparent 34%),
    linear-gradient(135deg, #fbf8ff 0%, #f4f7ff 48%, #fff9fc 100%);
}

.vent-shell {
  position: relative;
  display: grid;
  grid-template-columns: 245px minmax(760px, 1fr);
  max-width: 1760px;
  min-height: calc(100vh - 28px);
  margin: 0 auto;
  padding: 18px 26px 26px 16px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.88);
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

.brand,
.nav-item,
.topbar,
.top-actions,
.back-button,
.search-box,
.author-line,
.tag-row,
.preference-panel,
.side-title,
.status-item,
.action-card,
.reply-item header,
.safety-card,
.similar-item {
  display: flex;
  align-items: center;
}

.brand {
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
  padding-left: 14px;
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

.page-body {
  min-width: 0;
  padding: 18px 0 0 28px;
}

.topbar {
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 40px;
}

.back-button {
  gap: 8px;
  color: #7b6fea;
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
  gap: 34px;
}

.main-column,
.right-column {
  display: grid;
  align-content: start;
  gap: 14px;
}

.post-hero {
  position: relative;
  padding: 0 0 28px 12px;
}

.title-area h1 {
  margin: 0 0 18px;
  color: #12121d;
  font-size: 31px;
  line-height: 1.14;
}

.tag-row {
  flex-wrap: wrap;
  gap: 12px;
}

.tag-row span {
  display: inline-flex;
  align-items: center;
  min-height: 34px;
  padding: 0 13px;
  border-radius: 9px;
  color: #6c55f0;
  background: #eeeaff;
  font-size: 14px;
  font-weight: 900;
}

.tag-row span:nth-child(2) {
  background: #f4e9ff;
}

.tag-row span:nth-child(3) {
  color: #ee5e90;
  background: #ffe8f0;
}

.tag-row span:nth-child(4) {
  color: #1aa889;
  background: #ddf8f1;
}

.more-button {
  position: absolute;
  right: 0;
  bottom: 22px;
  display: grid;
  width: 46px;
  height: 46px;
  place-items: center;
  border: 1px solid #ece7f8;
  border-radius: 18px;
  color: #7e7b94;
  background: rgba(255, 255, 255, 0.76);
}

.more-button svg {
  width: 22px;
}

.author-line {
  gap: 18px;
  margin-top: 32px;
}

.author-avatar {
  display: grid;
  width: 50px;
  height: 50px;
  place-items: center;
  border-radius: 50%;
  color: #6a52f0;
  background: #e6ddff;
  font-size: 24px;
  font-weight: 900;
  object-fit: cover;
}

.author-line strong {
  display: flex;
  align-items: center;
  gap: 7px;
  color: #20233f;
  font-size: 15px;
}

.author-line strong svg {
  width: 18px;
  color: #7863f6;
}

.author-line p {
  margin: 8px 0 0;
  color: #7d7b94;
  font-size: 13px;
  font-weight: 800;
}

.story-card,
.action-card,
.replies-card,
.side-card {
  border: 1px solid #ece7f8;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.72);
  box-shadow: 0 10px 28px rgba(112, 104, 151, 0.08);
}

.story-card {
  position: relative;
  overflow: hidden;
  min-height: 0;
  padding: 30px 28px 24px;
  background:
    linear-gradient(90deg, rgba(118, 98, 244, 0.09) 0 6px, transparent 6px),
    linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(250, 248, 255, 0.88) 48%, rgba(255, 249, 252, 0.9));
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.82),
    0 10px 28px rgba(112, 104, 151, 0.08);
}

.story-copy {
  position: relative;
  max-width: 100%;
  padding: 2px 8px 26px 22px;
  border-bottom: 1px solid rgba(226, 219, 248, 0.92);
}

.story-copy h2,
.preference-block h3,
.side-card h2 {
  margin: 0;
  color: #20233f;
  font-size: 17px;
}

.story-copy h2,
.preference-block h3 {
  display: flex;
  align-items: center;
  gap: 8px;
}

.story-copy h2 svg,
.preference-block h3 svg {
  width: 21px;
  color: #7158ef;
}

.story-copy p {
  max-width: 820px;
  margin: 13px 0 0;
  color: #4f526b;
  font-size: 15px;
  line-height: 1.75;
}

.preference-panel {
  position: static;
  min-height: 146px;
  margin-top: 22px;
  padding: 20px 16px;
  border: 1px solid #ece7f8;
  border-radius: 14px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.7), rgba(246, 241, 255, 0.72)),
    linear-gradient(90deg, rgba(118, 98, 244, 0.04), rgba(238, 91, 135, 0.04));
}

.preference-block {
  flex: 1;
  min-width: 0;
  padding: 0 22px;
}

.preference-block + .preference-block {
  border-left: 1px solid #ece7f8;
}

.chip-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 16px;
}

.chip-group span {
  display: inline-flex;
  align-items: center;
  min-height: 34px;
  padding: 0 14px;
  border-radius: 9px;
  color: #7762e8;
  background: #efebff;
  font-weight: 900;
}

.chip-group span:nth-child(3) {
  color: #ef5b75;
  background: #ffe9ef;
}

.chip-group span:nth-child(4) {
  color: #19a783;
  background: #e4f8f1;
}

.preference-block ul {
  display: grid;
  gap: 11px;
  margin: 14px 0 0;
  padding: 0;
  list-style: none;
}

.preference-block li {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #555972;
  font-size: 15px;
  font-weight: 800;
}

.preference-block li svg {
  width: 16px;
  color: #7762e8;
}

.method-box {
  display: grid;
  gap: 10px;
  margin-top: 16px;
  padding: 14px;
  border-radius: 10px;
  background: #f3efff;
}

.method-box span {
  display: flex;
  align-items: center;
  gap: 9px;
  color: #353853;
  font-size: 14px;
  font-weight: 900;
}

.method-box svg {
  width: 17px;
  color: #765ff3;
}

.action-card {
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
  min-height: 128px;
  padding: 20px 26px;
}

.action-card button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  flex: 1 1 220px;
  max-width: 286px;
  height: 58px;
  border-radius: 14px;
  font-size: 18px;
  font-weight: 900;
}

.action-card button svg {
  width: 24px;
  height: 24px;
}

.heart-icon {
  display: inline-grid;
  width: 24px;
  height: 24px;
  place-items: center;
  color: #ec5e8b;
  font-size: 28px;
  font-weight: 900;
  line-height: 1;
}

.comfort-count {
  display: inline-grid;
  min-width: 30px;
  height: 24px;
  place-items: center;
  padding: 0 8px;
  border-radius: 999px;
  color: #ec5e8b;
  background: rgba(255, 255, 255, 0.72);
  font-size: 13px;
  font-weight: 900;
  line-height: 1;
}

.primary-action {
  color: #fff;
  background: linear-gradient(105deg, #7460f4, #8065f1);
  box-shadow: 0 14px 28px rgba(129, 91, 244, 0.24);
}

.comfort-action {
  color: #ec5e8b;
  background: linear-gradient(105deg, #fff4f8, #ffe9f0);
}

.collect-action {
  color: #7562dd;
  border: 1px solid #e1dcf4;
  background: #fff;
}

.action-card p {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 100%;
  gap: 8px;
  margin: 0;
  color: #9a88df;
  font-size: 15px;
  font-weight: 900;
}

.action-card p svg {
  width: 18px;
}

.right-column {
  gap: 12px;
}

.side-card {
  padding: 18px 18px;
}

.publish-status {
  min-height: 200px;
}

.publish-status h2 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.publish-status h2 svg {
  width: 17px;
  color: #9692ad;
}

.status-item {
  gap: 13px;
  margin-top: 14px;
}

.status-icon {
  display: grid;
  width: 36px;
  height: 36px;
  flex: 0 0 36px;
  place-items: center;
  border-radius: 13px;
  color: #fff;
}

.status-icon svg {
  width: 20px;
}

.status-icon.purple {
  background: linear-gradient(135deg, #8c72fa, #755df0);
}

.status-icon.green {
  background: linear-gradient(135deg, #8bdac8, #59c7a7);
}

.status-item strong {
  color: #20233f;
  font-size: 14px;
}

.status-item p,
.status-note {
  margin: 4px 0 0;
  color: #85819a;
  font-size: 13px;
  font-weight: 800;
}

.status-note {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 18px;
  color: #d95986;
}

.status-note svg {
  width: 15px;
}

.side-title {
  justify-content: space-between;
  margin-bottom: 12px;
}

.side-title small {
  color: #5d5a73;
  font-size: 14px;
}

.side-title button {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #765ff3;
  background: transparent;
  font-size: 13px;
  font-weight: 900;
}

.side-title button svg {
  width: 14px;
}

.replies-card {
  padding: 22px 26px;
}

.replies-card .side-title {
  margin-bottom: 4px;
}

.replies-card .side-title h2 {
  font-size: 19px;
}

.replies-card .side-title h2::before {
  content: "";
  display: inline-block;
  width: 8px;
  height: 20px;
  margin-right: 10px;
  border-radius: 999px;
  vertical-align: -4px;
  background: linear-gradient(180deg, #7662f4, #f06f9f);
}

.reply-form {
  display: grid;
  gap: 10px;
  margin: 14px 0 4px;
  padding: 14px;
  border: 1px solid #ece7f8;
  border-radius: 14px;
  background: rgba(250, 248, 255, 0.72);
}

.reply-form textarea {
  width: 100%;
  resize: vertical;
  min-height: 82px;
  border: 0;
  outline: 0;
  color: #4f526b;
  background: transparent;
  font-size: 14px;
  line-height: 1.6;
}

.reply-form div {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.reply-form span {
  color: #9a96ad;
  font-size: 12px;
  font-weight: 800;
}

.reply-form button {
  min-width: 96px;
  height: 36px;
  border-radius: 10px;
  color: #fff;
  background: linear-gradient(105deg, #7460f4, #8065f1);
  font-size: 13px;
  font-weight: 900;
}

.reply-form button:disabled {
  cursor: not-allowed;
  opacity: 0.58;
}

.reply-empty {
  margin: 16px 0 0;
  color: #85819a;
  font-size: 13px;
  font-weight: 800;
}

.reply-item {
  display: grid;
  grid-template-columns: 44px 1fr;
  gap: 14px;
  padding: 16px 0;
}

.reply-item + .reply-item {
  border-top: 1px solid #ece7f8;
}

.reply-item > span,
.similar-item > span {
  display: grid;
  width: 38px;
  height: 38px;
  place-items: center;
  border-radius: 12px;
  color: #6c55f0;
  background: #e6efff;
  font-size: 16px;
  font-weight: 900;
}

.replies-card .reply-item > span {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  font-size: 18px;
}

.reply-item:nth-of-type(2) > span {
  background: #dff8ef;
}

.reply-item:nth-of-type(3) > span {
  background: #e8efff;
}

.reply-item header {
  gap: 8px;
}

.reply-item strong {
  color: #20233f;
  font-size: 14px;
}

.reply-item em,
.similar-item em {
  padding: 2px 7px;
  border-radius: 999px;
  color: #765ff3;
  background: #f0ebff;
  font-size: 11px;
  font-style: normal;
  font-weight: 900;
}

.reply-item time {
  margin-left: auto;
  color: #918ca3;
  font-size: 12px;
}

.reply-item p {
  margin: 7px 0 2px;
  color: #65687d;
  font-size: 13px;
  line-height: 1.65;
}

.reply-item small {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 4px;
  color: #ef5b87;
  font-size: 12px;
}

.reply-item small svg {
  width: 14px;
}

.safety-card {
  position: relative;
  align-items: center;
  justify-content: space-between;
  overflow: hidden;
  min-height: 176px;
  gap: 18px;
  padding: 20px;
  background:
    linear-gradient(145deg, rgba(255, 255, 255, 0.88), rgba(249, 246, 255, 0.94)),
    radial-gradient(circle at 88% 18%, rgba(118, 98, 244, 0.12), transparent 32%);
}

.safety-label {
  display: inline-flex;
  align-items: center;
  height: 26px;
  padding: 0 10px;
  border-radius: 999px;
  color: #765ff3;
  background: #f0ebff;
  font-size: 12px;
  font-weight: 900;
}

.safety-card h2 {
  margin-top: 10px;
  font-size: 18px;
  line-height: 1.25;
}

.safety-points {
  display: grid;
  gap: 10px;
  margin-top: 16px;
}

.safety-points p {
  display: flex;
  align-items: center;
  gap: 9px;
  margin: 0;
  color: #686b83;
  font-size: 13px;
  line-height: 1.45;
}

.safety-points svg {
  width: 17px;
  height: 17px;
  flex: 0 0 17px;
  color: #7a62f1;
}

.shield-visual {
  display: grid;
  width: 76px;
  height: 76px;
  flex: 0 0 76px;
  place-items: center;
  border-radius: 22px;
  color: #9380f5;
  background:
    linear-gradient(145deg, rgba(255, 255, 255, 0.42), transparent 42%),
    linear-gradient(145deg, #eeeaff, #e3dbff);
  box-shadow: inset 0 0 0 1px rgba(118, 98, 244, 0.08);
}

.shield-visual svg {
  width: 40px;
  height: 40px;
}

.similar-card {
  min-height: 172px;
}

.similar-item {
  gap: 10px;
  padding: 7px 0;
}

.similar-item strong {
  color: #20233f;
  font-size: 13px;
}

.similar-item p {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin: 5px 0 0;
}

.similar-item em:nth-child(2) {
  color: #ef5b87;
  background: #ffe9ef;
}

.similar-item em:nth-child(3) {
  color: #1aa889;
  background: #ddf8f1;
}

@media (max-width: 1320px) {
  .content-layout {
    grid-template-columns: 1fr;
  }

  .right-column {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 980px) {
  .vent-shell {
    display: block;
    padding: 14px;
  }

  .sidebar {
    margin: 0;
    padding-bottom: 18px;
    border-radius: 28px 28px 0 0;
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
  .preference-panel,
  .action-card {
    align-items: flex-start;
    flex-direction: column;
  }

  .search-box {
    width: 100%;
  }

  .story-card {
    padding-bottom: 24px;
  }

  .preference-panel {
    position: static;
    margin-top: 20px;
  }

  .preference-block {
    width: 100%;
    padding: 0;
  }

  .preference-block + .preference-block {
    padding-top: 18px;
    border-top: 1px solid #ece7f8;
    border-left: 0;
  }

  .right-column {
    grid-template-columns: 1fr;
  }

  .action-card button {
    width: 100%;
  }
}
</style>
