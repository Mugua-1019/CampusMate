<template>
  <main class="chat-page">
    <section class="chat-shell">
      <aside class="sidebar">
        <div class="brand">
          <img :src="logoImage" alt="星伴 CampusMate" />
          <div>
            <strong>星伴</strong>
            <span>CampusMate</span>
          </div>
        </div>

        <nav class="nav-list" aria-label="主导航">
          <button
            v-for="item in navItems"
            :key="item.label"
            class="nav-item"
            :class="{ active: item.active }"
            @click="handleNav(item)"
          >
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

      <section class="chat-main">
        <header class="topbar">
          <div class="heading-copy">
            <h1>我的聊天</h1>
            <p>与校园伙伴的每一次对话，都是成长的陪伴。</p>
          </div>

          <div class="top-actions">
            <label class="search-box">
              <input v-model="keyword" placeholder="搜索需求、活动或用户" />
              <Search />
            </label>
            <button class="plain-icon" aria-label="我的聊天" @click="clearSelectedChat"><Message /></button>
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

        <section class="chat-layout">
          <aside class="conversation-panel">
            <div class="conversation-head">
              <h2>{{ showArchived ? '归档会话' : '全部会话' }} ({{ visibleConversations.length }})</h2>
              <button aria-label="新建聊天"><EditPen /></button>
            </div>

            <button
              v-for="conversation in visibleConversations"
              :key="conversation.id"
              class="conversation-item"
              :class="{ active: selectedConversation?.id === conversation.id }"
              @click="selectConversation(conversation)"
            >
              <span class="avatar-wrap">
                <img v-if="conversation.avatarUrl" :src="conversation.avatarUrl" alt="" />
                <em v-else>{{ conversation.avatarText }}</em>
                <i v-if="conversation.online"></i>
              </span>
              <span class="conversation-copy">
                <strong>
                  {{ conversation.name }}
                  <small>{{ conversation.badge }}</small>
                </strong>
                <span>{{ conversation.preview }}</span>
              </span>
              <span class="conversation-meta">
                <time>{{ conversation.time }}</time>
                <b v-if="conversation.unread">{{ conversation.unread }}</b>
              </span>
            </button>

            <button class="archived-link" @click="showArchived = !showArchived">
              {{ showArchived ? '返回全部会话' : `查看已归档会话 (${archivedConversations.length})` }} <ArrowRight />
            </button>
          </aside>

          <section class="message-panel">
            <template v-if="selectedConversation">
              <header class="message-head">
                <div>
                  <img
                    v-if="selectedConversation.avatarUrl"
                    class="chat-avatar"
                    :src="selectedConversation.avatarUrl"
                    alt="聊天对象头像"
                  />
                  <span v-else class="chat-avatar">{{ selectedConversation.avatarText }}</span>
                  <div>
                    <h2>{{ selectedConversation.name }}</h2>
                    <p><i></i> 在线</p>
                  </div>
                </div>
                <div class="message-tools">
                  <button aria-label="搜索聊天记录"><Search /></button>
                  <button :aria-label="selectedConversation.archived ? '取消归档' : '归档会话'" @click="toggleArchiveSelected">
                    <MoreFilled />
                  </button>
                </div>
              </header>

              <section class="safety-strip">
                <Lock />
                <span>请文明友善交流，谨防诈骗与不良信息，保护个人隐私与财产安全。</span>
                <button>了解更多安全守则</button>
                <button aria-label="关闭安全提醒"><Close /></button>
              </section>

              <div class="date-line">5月20日（周二）</div>

              <section class="message-list" aria-label="聊天消息">
                <article
                  v-for="message in selectedConversation.messages"
                  :key="message.id"
                  class="message-row"
                  :class="{ mine: message.mine }"
                >
                  <img
                    v-if="message.mine ? userAvatar : selectedConversation.avatarUrl"
                    class="bubble-avatar"
                    :src="message.mine ? userAvatar : selectedConversation.avatarUrl"
                    alt="消息头像"
                  />
                  <span v-else class="bubble-avatar">{{ message.mine ? userInitial : selectedConversation.avatarText }}</span>
                  <div>
                    <p>
                      <template v-if="message.messageType === 'image'">
                        <img class="message-image" :src="message.attachmentUrl" :alt="message.attachmentName || '图片'" />
                      </template>
                      <template v-else-if="message.messageType === 'file'">
                        <a class="message-file" :href="message.attachmentUrl" target="_blank" rel="noreferrer">
                          <Document />{{ message.attachmentName || message.text }}
                        </a>
                      </template>
                      <template v-else-if="message.messageType === 'location'">
                        <a
                          class="message-file"
                          :href="`https://map.baidu.com/search/${message.latitude},${message.longitude}`"
                          target="_blank"
                          rel="noreferrer"
                        >
                          <Location />{{ message.text }}
                        </a>
                      </template>
                      <template v-else>{{ message.text }}</template>
                    </p>
                    <time>{{ message.time }}</time>
                  </div>
                </article>
              </section>

              <footer class="composer">
                <input v-model="draft" placeholder="输入消息，Enter 发送" @keyup.enter="sendDraft" />
                <div class="composer-tools">
                  <button aria-label="更多发送方式" @click="showAttachMenu = !showAttachMenu"><CirclePlus /></button>
                  <button aria-label="图片" @click="triggerImageUpload"><Picture /></button>
                  <button aria-label="文件" @click="triggerFileUpload"><Document /></button>
                </div>
                <div v-if="showAttachMenu" class="attach-menu">
                  <button @click="sendLocation"><Location />发送定位</button>
                  <button @click="triggerImageUpload"><Picture />发送图片</button>
                  <button @click="triggerFileUpload"><Document />发送附件</button>
                </div>
                <input ref="imageInput" class="hidden-upload" type="file" accept="image/*" @change="handleImageUpload" />
                <input ref="fileInput" class="hidden-upload" type="file" @change="handleFileUpload" />
                <button class="send-button" @click="sendDraft">发送</button>
              </footer>
            </template>

            <section v-else class="empty-chat">
              <Message />
              <h2>请选择聊天</h2>
            </section>
          </section>

          <aside class="profile-panel" v-if="selectedConversation">
            <section class="profile-card">
              <div class="cover"></div>
              <img
                v-if="selectedConversation.avatarUrl"
                class="profile-avatar"
                :src="selectedConversation.avatarUrl"
                alt="聊天对象头像"
              />
              <span v-else class="profile-avatar">{{ selectedConversation.avatarText }}</span>
              <h2>{{ selectedConversation.name }}</h2>
              <p>{{ selectedConversation.gender }} {{ selectedConversation.age }}岁</p>
              <p class="school-line">{{ selectedConversation.school }} · {{ selectedConversation.grade }}</p>
              <p>{{ selectedConversation.major }}</p>
              <div class="tag-row">
                <span v-for="tag in selectedConversation.tags" :key="tag">{{ tag }}</span>
              </div>
            </section>

            <section class="side-card">
              <h2>匹配信息</h2>
              <p>你们在 {{ selectedConversation.matchDate }} 匹配成功</p>
              <p>匹配来源：{{ selectedConversation.source }}</p>
            </section>

            <section class="side-card">
              <h2>共同标签</h2>
              <div class="tag-row">
                <span v-for="tag in selectedConversation.commonTags" :key="tag">{{ tag }}</span>
              </div>
            </section>

            <section class="side-card safety-card">
              <h2>安全小贴士</h2>
              <ul>
                <li>文明友善交流，尊重彼此</li>
                <li>不轻易透露个人隐私信息</li>
                <li>涉及金钱交易需谨慎</li>
                <li>遇到不适可举报或拉黑</li>
              </ul>
              <button>了解更多安全守则 <ArrowRight /></button>
            </section>

            <div class="profile-actions">
              <button><Warning /> 举报</button>
              <button><CircleClose /> 拉黑</button>
            </div>
          </aside>
        </section>
      </section>
    </section>
  </main>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowRight,
  CircleClose,
  CirclePlus,
  Close,
  Document,
  EditPen,
  Flag,
  HomeFilled,
  Location,
  Lock,
  Message,
  MoreFilled,
  Picture,
  Promotion,
  Search,
  StarFilled,
  User,
  Warning
} from '@element-plus/icons-vue'
import logoImage from '../../../assets/images/logo-star-mascot.png'
import verifyImage from '../../../assets/images/renzheng.png'
import { useCurrentUserProfile } from '../../../composables/useCurrentUserProfile'
import {
  fetchChatHome,
  fetchChatPeerProfile,
  fetchChatMessages,
  sendChatMessage,
  updateChatArchiveStatus,
  uploadChatAttachment
} from '../../../api/chat'
import NotificationBell from '../../../components/NotificationBell.vue'
import UserMenu from '../../../components/UserMenu.vue'

const route = useRoute()
const router = useRouter()
const keyword = ref('')
const selectedId = ref('')
const draft = ref('')
const archivedConversations = ref([])
const messagesByConversation = ref({})
const pendingConversation = ref(null)
const showArchived = ref(false)
const showAttachMenu = ref(false)
const imageInput = ref(null)
const fileInput = ref(null)
const socket = ref(null)
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
  { label: '我的聊天', icon: Message, active: true, action: 'chat' },
  { label: '我的匹配', icon: StarFilled },
  { label: '认证中心', icon: Lock, route: '/auth-center' },
  { label: '安全反馈', icon: Flag },
  { label: '个人中心', icon: User, route: '/profile' }
]

const conversations = ref([
  {
    id: 'xiaoxing',
    name: '小星同学',
    avatarText: '小',
    badge: '学习搭子',
    preview: '没问题！我们可以一起制定计划~',
    time: '10:32',
    unread: 2,
    online: true,
    gender: '♀',
    age: 21,
    school: '浙江大学',
    grade: '大二',
    major: '计算机科学与技术',
    matchDate: '2025年5月18日',
    source: '学习搭子 · 期末一起复习',
    tags: ['学习搭子', '已匹配'],
    commonTags: ['期末复习', '自律打卡', '图书馆党', '喜欢奶茶'],
    messages: [
      { id: 1, text: '你好呀！看到你想找学习搭子，我也是大二学生，我们专业好像一样耶~ 😊', time: '10:15' },
      { id: 2, text: '哇，真的吗！太巧了~我也在找一起学习的小伙伴！', time: '10:17', mine: true },
      { id: 3, text: '我平时比较喜欢一起制定学习计划，互相监督打卡，这样效率会更高！你呢？', time: '10:18' },
      { id: 4, text: '我也是！最近准备期末复习，有点压力，要是能一起复习就太棒了~', time: '10:20', mine: true },
      { id: 5, text: '没问题！我们可以一起制定计划~ 要不要加个微信，方便沟通呀？', time: '10:21' },
      { id: 6, text: '好呀好呀，期待和你一起进步！🌟', time: '10:22', mine: true }
    ]
  },
  {
    id: 'wanfeng',
    name: '晚风与海',
    avatarText: '晚',
    badge: '倾诉伙伴',
    preview: '谢谢你听我说这么多，感觉舒服多了',
    time: '昨天',
    unread: 1,
    online: true,
    gender: '♂',
    age: 22,
    school: '浙江大学',
    grade: '大三',
    major: '心理学',
    matchDate: '2025年5月16日',
    source: '倾诉广场 · 压力疏导',
    tags: ['倾诉伙伴', '已匹配'],
    commonTags: ['情绪支持', '深夜在线', '耐心倾听'],
    messages: [
      { id: 1, text: '今天有点累，但还是想找个人聊聊。', time: '21:05' },
      { id: 2, text: '我在，你可以慢慢说。', time: '21:06', mine: true },
      { id: 3, text: '谢谢你听我说这么多，感觉舒服多了', time: '21:28' }
    ]
  },
  {
    id: 'mathhelper',
    name: '数学小能手',
    avatarText: '数',
    badge: '学习搭子',
    preview: '好滴，明天图书馆见！',
    time: '昨天',
    unread: 0,
    online: false,
    gender: '♀',
    age: 20,
    school: '浙江大学',
    grade: '大二',
    major: '数学与应用数学',
    matchDate: '2025年5月15日',
    source: '学习搭子 · 高数复习',
    tags: ['学习搭子'],
    commonTags: ['高数', '图书馆', '错题整理'],
    messages: [
      { id: 1, text: '明天上午有空一起刷题吗？', time: '19:20' },
      { id: 2, text: '可以，我带上错题本。', time: '19:22', mine: true },
      { id: 3, text: '好滴，明天图书馆见！', time: '19:24' }
    ]
  },
  {
    id: 'xiaoshu',
    name: '小树同学',
    avatarText: '树',
    badge: '已匹配',
    preview: '哈哈哈是的呀，周末一起去吧~',
    time: '周一',
    unread: 0,
    online: false,
    gender: '♀',
    age: 21,
    school: '浙江大学',
    grade: '大二',
    major: '新闻传播',
    matchDate: '2025年5月12日',
    source: '活动同行 · 校园市集',
    tags: ['已匹配'],
    commonTags: ['周末', '市集', '摄影'],
    messages: [
      { id: 1, text: '周末那个市集你也想去吗？', time: '15:12', mine: true },
      { id: 2, text: '哈哈哈是的呀，周末一起去吧~', time: '15:14' }
    ]
  }
])

conversations.value = []

const activeConversationList = computed(() => showArchived.value ? archivedConversations.value : conversations.value)

const visibleConversations = computed(() => {
  const value = keyword.value.trim().toLowerCase()
  const source = pendingConversation.value && !showArchived.value
    ? [pendingConversation.value, ...activeConversationList.value]
    : activeConversationList.value
  if (!value) {
    return source
  }
  return source.filter((item) => {
    return [item.name, item.badge, item.preview, item.major].some((text) => String(text).toLowerCase().includes(value))
  })
})

const selectedConversation = computed(() => {
  const conversation = selectedId.value === 'pending'
    ? pendingConversation.value
    : [...conversations.value, ...archivedConversations.value].find((item) => item.id === selectedId.value)
  if (!conversation) {
    return null
  }
  return {
    ...conversation,
    messages: messagesByConversation.value[conversation.id] || conversation.messages || []
  }
})

const applyRouteSelection = async () => {
  const peerId = String(route.query.userId || route.query.peerId || '')
  if (!peerId) {
    selectedId.value = ''
    pendingConversation.value = null
    return
  }

  const existed = conversations.value.find((item) => String(item.peerUserId) === peerId)
  if (existed) {
    pendingConversation.value = null
    selectedId.value = existed.id
    loadMessages(existed.id)
    return
  }

  const response = await fetchChatPeerProfile(peerId, {
    sourceType: route.query.badge || '校园伙伴',
    sourceTitle: route.query.source || '校园匹配'
  })
  pendingConversation.value = {
    ...response.data.data,
    id: 'pending',
    messages: []
  }
  selectedId.value = 'pending'
}

const selectConversation = (conversation) => {
  selectedId.value = conversation.id
  pendingConversation.value = conversation.id === 'pending' ? pendingConversation.value : null
  if (conversation.id !== 'pending') {
    loadMessages(conversation.id)
    router.replace({ path: '/chat', query: { userId: conversation.peerUserId } })
  }
}

const clearSelectedChat = () => {
  selectedId.value = ''
  router.replace('/chat')
}

const loadChatHome = async () => {
  const response = await fetchChatHome()
  conversations.value = response.data.data?.conversations || []
  archivedConversations.value = response.data.data?.archivedConversations || []
  await applyRouteSelection()
}

const loadMessages = async (conversationId) => {
  if (!conversationId || conversationId === 'pending') {
    return
  }
  const response = await fetchChatMessages(conversationId)
  messagesByConversation.value = {
    ...messagesByConversation.value,
    [conversationId]: response.data.data || []
  }
}

const sendDraft = async () => {
  const text = draft.value.trim()
  if (!text || !selectedConversation.value) {
    return
  }
  await sendMessagePayload({
    messageType: 'text',
    content: text
  })
  draft.value = ''
}

const sendMessagePayload = async (payload) => {
  if (!selectedConversation.value) {
    return
  }
  const response = await sendChatMessage({
    conversationId: selectedConversation.value.id === 'pending' ? null : selectedConversation.value.id,
    receiverUserId: selectedConversation.value.peerUserId,
    sourceType: selectedConversation.value.badge,
    sourceTitle: selectedConversation.value.source,
    ...payload
  })
  const message = response.data.data
  if (selectedConversation.value.id === 'pending') {
    pendingConversation.value = null
    await loadChatHome()
    selectedId.value = message.conversationId
  }
  messagesByConversation.value = {
    ...messagesByConversation.value,
    [message.conversationId]: [...(messagesByConversation.value[message.conversationId] || []), message]
  }
  await loadChatHome()
  selectedId.value = message.conversationId
}

const toggleArchiveSelected = async () => {
  if (!selectedConversation.value || selectedConversation.value.id === 'pending') {
    return
  }
  await updateChatArchiveStatus(selectedConversation.value.id, !selectedConversation.value.archived)
  selectedId.value = ''
  await loadChatHome()
}

const triggerImageUpload = () => {
  showAttachMenu.value = false
  imageInput.value?.click()
}

const triggerFileUpload = () => {
  showAttachMenu.value = false
  fileInput.value?.click()
}

const handleImageUpload = async (event) => {
  await handleAttachmentUpload(event, 'image')
}

const handleFileUpload = async (event) => {
  await handleAttachmentUpload(event, 'file')
}

const handleAttachmentUpload = async (event, type) => {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file || !selectedConversation.value) {
    return
  }
  const response = await uploadChatAttachment(file, type)
  const attachment = response.data.data
  await sendMessagePayload({
    messageType: type,
    content: type === 'image' ? '[图片]' : `[附件] ${attachment.name}`,
    attachmentUrl: attachment.url,
    attachmentName: attachment.name
  })
}

const sendLocation = async () => {
  showAttachMenu.value = false
  if (!navigator.geolocation) {
    await sendMessagePayload({
      messageType: 'location',
      content: '[定位] 暂无法获取浏览器定位',
      latitude: 0,
      longitude: 0
    })
    return
  }
  navigator.geolocation.getCurrentPosition(async (position) => {
    await sendMessagePayload({
      messageType: 'location',
      content: '[定位] 我的位置',
      latitude: position.coords.latitude,
      longitude: position.coords.longitude
    })
  })
}

const handleNav = (item) => {
  if (item.action === 'chat') {
    clearSelectedChat()
    return
  }
  if (item.route) {
    router.push(item.route)
  }
}

const goLogin = () => {
  router.push('/login')
}

const goAuthCenter = () => {
  router.push(isLoggedIn.value ? '/auth-center' : '/login')
}

const connectChatSocket = () => {
  if (!currentUser.value?.userId) {
    return
  }
  const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws'
  const host = window.location.host
  socket.value = new WebSocket(`${protocol}://${host}/ws/chat?userId=${currentUser.value.userId}`)
  socket.value.onmessage = async (event) => {
    const payload = JSON.parse(event.data)
    if (payload.type !== 'chat.message') {
      return
    }
    const message = payload.data
    messagesByConversation.value = {
      ...messagesByConversation.value,
      [message.conversationId]: [...(messagesByConversation.value[message.conversationId] || []), message]
    }
    await loadChatHome()
  }
}

watch(() => route.query, () => {
  applyRouteSelection()
})

onMounted(async () => {
  loadCurrentUserProfile().catch(() => {})
  await loadChatHome()
  connectChatSocket()
})
</script>

<style scoped>
.chat-page {
  min-height: 100vh;
  padding: 14px 20px;
  background:
    radial-gradient(circle at 10% 0%, rgba(255, 215, 238, 0.58), transparent 28%),
    radial-gradient(circle at 92% 8%, rgba(226, 214, 255, 0.78), transparent 34%),
    linear-gradient(135deg, #fbf8ff 0%, #f4f7ff 48%, #fff9fc 100%);
}

.chat-shell {
  display: grid;
  grid-template-columns: 245px minmax(0, 1fr);
  max-width: 1760px;
  height: calc(100vh - 28px);
  margin: 0 auto;
  padding: 18px 26px 26px 16px;
  overflow: hidden;
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
.search-box,
.chat-layout,
.message-head,
.message-head > div,
.message-tools,
.safety-strip,
.composer,
.composer-tools,
.tag-row,
.profile-actions {
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

.verify-card h2 {
  margin: 0;
  color: #20233f;
  font-size: 17px;
}

.verify-card p {
  margin: 8px 0 12px;
  color: #77738b;
  font-size: 13px;
  line-height: 1.5;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  min-height: 42px;
  border-radius: 999px;
  color: #fff;
  background: linear-gradient(135deg, #7460f4, #9d7bff);
  font-weight: 900;
}

.btn-primary svg {
  width: 17px;
  height: 17px;
  flex: 0 0 17px;
}

.chat-main {
  display: flex;
  flex-direction: column;
  min-width: 0;
  min-height: 0;
  padding: 18px 0 0 28px;
}

.topbar {
  flex: 0 0 auto;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 24px;
}

.heading-copy h1 {
  margin: 0 0 10px;
  color: #12121d;
  font-size: 29px;
  line-height: 1.1;
}

.heading-copy p {
  margin: 0;
  color: #818096;
  font-size: 14px;
  font-weight: 700;
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

.plain-icon,
.message-tools button,
.conversation-head button,
.composer-tools button {
  display: grid;
  place-items: center;
  color: #111;
  background: transparent;
}

.plain-icon {
  width: 34px;
  height: 34px;
}

.plain-icon svg {
  width: 27px;
  height: 27px;
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

.chat-layout {
  align-items: stretch;
  gap: 18px;
  flex: 1 1 auto;
  min-height: 0;
}

.conversation-panel,
.message-panel,
.profile-panel {
  border: 1px solid #ece7f8;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.72);
  box-shadow: 0 10px 28px rgba(112, 104, 151, 0.08);
}

.conversation-panel {
  display: flex;
  flex-direction: column;
  width: 355px;
  overflow: hidden;
}

.conversation-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 78px;
  padding: 0 22px;
}

.conversation-head h2 {
  margin: 0;
  color: #12121d;
  font-size: 19px;
}

.conversation-head button svg {
  width: 23px;
  color: #6d55f0;
}

.conversation-item {
  display: grid;
  grid-template-columns: 58px 1fr auto;
  gap: 13px;
  flex: 0 0 auto;
  min-height: 88px;
  padding: 12px 14px 12px 18px;
  border-top: 1px solid #f0edf7;
  background: transparent;
  text-align: left;
}

.conversation-item.active {
  background: linear-gradient(100deg, rgba(120, 99, 246, 0.16), rgba(232, 211, 255, 0.38));
}

.avatar-wrap {
  position: relative;
  display: grid;
  width: 54px;
  height: 54px;
  place-items: center;
  border-radius: 50%;
  color: #6c55f0;
  background: #f0ebff;
  font-size: 20px;
  font-weight: 900;
}

.avatar-wrap img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-wrap i {
  position: absolute;
  right: 1px;
  bottom: 4px;
  width: 11px;
  height: 11px;
  border: 2px solid #fff;
  border-radius: 50%;
  background: #4bd06f;
}

.conversation-copy {
  min-width: 0;
}

.conversation-copy strong {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 3px 0 8px;
  color: #1e2036;
  font-size: 16px;
}

.conversation-copy small,
.tag-row span {
  padding: 4px 8px;
  border-radius: 999px;
  color: #765ff3;
  background: #f0ebff;
  font-size: 12px;
  font-weight: 900;
}

.conversation-copy > span {
  display: block;
  overflow: hidden;
  color: #77738b;
  font-size: 13px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conversation-meta {
  display: grid;
  justify-items: end;
  gap: 10px;
  color: #8a859c;
  font-size: 12px;
}

.conversation-meta b {
  display: grid;
  min-width: 21px;
  height: 21px;
  place-items: center;
  border-radius: 999px;
  color: #fff;
  background: #ef5b75;
}

.archived-link {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  gap: 5px;
  margin-top: auto;
  height: 58px;
  flex: 0 0 58px;
  color: #765ff3;
  background: transparent;
  font-weight: 900;
}

.archived-link svg {
  width: 16px;
}

.message-panel {
  display: grid;
  grid-template-rows: auto auto auto 1fr auto;
  flex: 1;
  min-width: 0;
  min-height: 0;
  overflow: hidden;
}

.message-head {
  justify-content: space-between;
  height: 78px;
  padding: 0 22px;
  border-bottom: 1px solid #ece7f8;
}

.message-head > div:first-child {
  gap: 12px;
}

.chat-avatar,
.bubble-avatar,
.profile-avatar {
  display: grid;
  place-items: center;
  color: #6c55f0;
  background: #f0ebff;
  font-weight: 900;
}

.chat-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  font-size: 20px;
  object-fit: cover;
}

.message-head h2 {
  margin: 0 0 6px;
  color: #151629;
  font-size: 18px;
}

.message-head p {
  margin: 0;
  color: #65687d;
  font-size: 13px;
}

.message-head p i {
  display: inline-block;
  width: 8px;
  height: 8px;
  margin-right: 6px;
  border-radius: 50%;
  background: #4bd06f;
}

.message-tools {
  gap: 14px;
}

.message-tools svg {
  width: 24px;
  color: #6f6b86;
}

.safety-strip {
  gap: 10px;
  min-height: 52px;
  margin: 0;
  padding: 0 20px;
  color: #765ff3;
  background: linear-gradient(100deg, rgba(118, 98, 244, 0.08), rgba(255, 249, 252, 0.78));
  font-size: 13px;
  font-weight: 800;
}

.safety-strip svg {
  width: 18px;
  flex: 0 0 18px;
}

.safety-strip span {
  flex: 1;
}

.safety-strip button {
  color: #765ff3;
  background: transparent;
  font-weight: 900;
}

.date-line {
  padding: 18px 0 10px;
  color: #85819a;
  text-align: center;
  font-size: 13px;
  font-weight: 800;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 0;
  padding: 0 20px 18px;
  overflow: auto;
}

.message-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.message-row.mine {
  flex-direction: row-reverse;
}

.bubble-avatar {
  width: 36px;
  height: 36px;
  flex: 0 0 36px;
  border-radius: 50%;
  object-fit: cover;
}

.message-row div {
  display: grid;
  gap: 6px;
  max-width: min(560px, 76%);
}

.message-row p {
  margin: 0;
  padding: 14px 18px;
  border: 1px solid #e6e0f4;
  border-radius: 14px;
  color: #34364f;
  background: #fff;
  line-height: 1.65;
}

.message-row.mine p {
  color: #4d3fae;
  background: linear-gradient(135deg, #eeeaff, #f5edff);
}

.message-image {
  display: block;
  max-width: min(260px, 100%);
  max-height: 180px;
  border-radius: 10px;
  object-fit: cover;
}

.message-file {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: inherit;
  font-weight: 900;
}

.message-file svg {
  width: 18px;
  height: 18px;
  flex: 0 0 18px;
}

.message-row time {
  color: #9a96ad;
  font-size: 12px;
}

.message-row.mine time {
  text-align: right;
}

.composer {
  position: relative;
  display: block;
  min-height: 88px;
  margin: 0 18px 16px;
  padding: 16px 92px 44px 18px;
  border: 1px solid #e6e0f4;
  border-radius: 18px;
  background: #fff;
}

.composer input {
  width: 100%;
  height: 24px;
  border: 0;
  outline: 0;
  color: #4b4961;
  background: transparent;
  font-size: 14px;
  line-height: 24px;
}

.composer-tools {
  position: absolute;
  left: 18px;
  bottom: 13px;
  gap: 14px;
}

.composer-tools button {
  width: 24px;
  height: 24px;
  border-radius: 8px;
  color: #77738b;
}

.composer-tools svg {
  width: 20px;
  height: 20px;
  color: #7a778f;
}

.attach-menu {
  position: absolute;
  left: 12px;
  bottom: 44px;
  display: grid;
  width: 142px;
  padding: 8px;
  border: 1px solid #e6e0f4;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 12px 28px rgba(112, 104, 151, 0.16);
  z-index: 4;
}

.attach-menu button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 34px;
  padding: 0 8px;
  border-radius: 8px;
  color: #5d5a73;
  background: transparent;
  font-weight: 900;
}

.attach-menu button:hover {
  color: #6c55f0;
  background: #f3efff;
}

.attach-menu svg {
  width: 17px;
  height: 17px;
}

.hidden-upload {
  display: none;
}

.send-button {
  position: absolute;
  right: 16px;
  bottom: 10px;
  width: 72px;
  height: 40px;
  border-radius: 999px;
  color: #fff;
  background: linear-gradient(135deg, #7460f4, #9d7bff);
  font-weight: 900;
}

.empty-chat {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  height: 100%;
  min-height: 100%;
  padding-bottom: 72px;
  color: #8a859c;
}

.empty-chat svg {
  width: 64px;
  height: 64px;
  color: #bfb4f5;
  stroke-width: 1.8;
}

.empty-chat h2 {
  margin: 0;
  color: #77738b;
  font-size: 22px;
}

.profile-panel {
  display: grid;
  align-content: start;
  gap: 14px;
  width: 300px;
  min-height: 0;
  padding: 14px;
  overflow: auto;
}

.profile-card {
  position: relative;
  padding: 58px 12px 16px;
  text-align: center;
}

.cover {
  position: absolute;
  inset: 0 0 auto;
  height: 84px;
  border-radius: 14px;
  background: linear-gradient(135deg, #f3e6ff, #fff4f8);
}

.profile-avatar {
  position: relative;
  width: 78px;
  height: 78px;
  margin: 0 auto 12px;
  border: 4px solid #fff;
  border-radius: 50%;
  font-size: 26px;
  object-fit: cover;
}

.profile-card h2 {
  margin: 0 0 8px;
  color: #20233f;
  font-size: 18px;
}

.profile-card p,
.side-card p,
.safety-card li {
  color: #666b84;
  font-size: 13px;
  line-height: 1.55;
}

.profile-card p {
  margin: 5px 0;
}

.school-line {
  margin-top: 14px !important;
}

.tag-row {
  justify-content: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.side-card {
  padding: 14px;
  border-radius: 10px;
  background: #f8f4ff;
}

.side-card h2 {
  margin: 0 0 10px;
  color: #20233f;
  font-size: 15px;
}

.side-card p {
  margin: 6px 0;
}

.safety-card {
  background:
    linear-gradient(140deg, rgba(255, 255, 255, 0.84), rgba(248, 244, 255, 0.9)),
    radial-gradient(circle at 88% 70%, rgba(128, 102, 245, 0.16), transparent 34%);
}

.safety-card ul {
  display: grid;
  gap: 6px;
  margin: 0 0 12px;
  padding-left: 18px;
}

.safety-card button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #745cf2;
  background: transparent;
  font-weight: 900;
}

.safety-card button svg {
  width: 16px;
  height: 16px;
  flex: 0 0 16px;
}

.profile-actions {
  gap: 10px;
}

.profile-actions button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  flex: 1;
  height: 38px;
  border: 1px solid #e4def5;
  border-radius: 999px;
  color: #666b84;
  background: #fff;
  font-weight: 900;
}

.profile-actions button:last-child {
  color: #ef5b75;
  border-color: #ffc8d4;
}

.profile-actions svg {
  width: 17px;
}

@media (max-width: 1320px) {
  .profile-panel {
    display: none;
  }
}

@media (max-width: 980px) {
  .chat-shell {
    display: block;
    padding: 14px;
  }

  .sidebar {
    margin: 0;
    padding-bottom: 18px;
    border-radius: 28px 28px 0 0;
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

  .chat-main {
    padding-left: 0;
  }

  .topbar,
  .top-actions,
  .chat-layout {
    align-items: stretch;
    flex-direction: column;
  }

  .search-box,
  .conversation-panel {
    width: 100%;
  }

  .message-panel {
    min-height: 640px;
  }
}
</style>
