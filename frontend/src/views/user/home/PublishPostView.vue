<template>
  <main class="publish-page">
    <section class="publish-shell">
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

      <section class="page-body">
        <header class="topbar">
          <div class="heading-copy">
            <h1>{{ pageTitle }}</h1>
            <p>{{ pageDescription }}</p>
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

        <section class="mode-tabs" aria-label="发布类型">
          <button :class="{ active: activeMode === 'match' }" :disabled="isEditingPost" @click="activeMode = 'match'">匹配广场</button>
          <button :class="{ active: activeMode === 'vent' }" :disabled="isEditingPost" @click="activeMode = 'vent'">倾诉广场</button>
        </section>

        <section class="content-layout">
          <form class="form-card" @submit.prevent="submitDraft">
            <template v-if="activeMode === 'vent'">
              <PublishStep index="1" title="倾诉标题" hint="一句话概括你想倾诉的主题（10-30字）">
                <label class="text-field">
                  <input v-model="ventForm.title" maxlength="30" placeholder="例如：考试压力好大，想找个人听我说说" />
                  <span>{{ ventForm.title.length }} / 30</span>
                </label>
              </PublishStep>

              <PublishStep index="2" title="现在想说的话" hint="详细描述一下你的感受或经历，我们会更好地理解你（10-1000字）">
                <label class="textarea-field">
                  <textarea v-model="ventForm.content" maxlength="1000" placeholder="此处输入你想倾诉的内容。你可以放心表达，这里没有评判，只有理解与陪伴。"></textarea>
                  <span>{{ ventForm.content.length }} / 1000</span>
                </label>
              </PublishStep>

              <PublishStep index="3" title="情绪标签" hint="选择最贴近你当前感受的标签（可多选）">
                <div class="chip-row">
                  <button
                    v-for="tag in visibleVentTags"
                    :key="tag"
                    type="button"
                    :class="{ active: ventForm.tags.includes(tag) }"
                    @click="toggleItem(ventForm.tags, tag)"
                  >
                    {{ tag }}
                  </button>
                  <button v-if="!customVentTagVisible" type="button" class="soft-action add-tag-button" @click="customVentTagVisible = true">
                    <span>+</span>
                    自定义标签
                  </button>
                  <label v-else class="custom-tag-field">
                    <input
                      v-model.trim="customVentTag"
                      maxlength="8"
                      placeholder="输入标签"
                      autofocus
                      @keyup.enter="addCustomVentTag"
                    />
                    <button type="button" class="custom-tag-submit" @click="addCustomVentTag">添加</button>
                    <button type="button" class="custom-tag-cancel" @click="customVentTagVisible = false; customVentTag = ''">取消</button>
                  </label>
                </div>
              </PublishStep>

              <PublishStep index="4" title="希望得到的回应" hint="你希望心灵伙伴以怎样的方式回应你？">
                <div class="choice-grid three">
                  <button
                    v-for="item in ventResponseOptions"
                    :key="item.title"
                    type="button"
                    :class="{ active: ventForm.response === item.title }"
                    @click="ventForm.response = item.title"
                  >
                    <component :is="item.icon" />
                    <strong>{{ item.title }}</strong>
                    <span>{{ item.desc }}</span>
                  </button>
                </div>
              </PublishStep>

              <PublishStep index="5" title="偏好方式" hint="你更希望通过什么方式被陪伴？">
                <div class="choice-grid three">
                  <button
                    v-for="item in ventMethodOptions"
                    :key="item.title"
                    type="button"
                    :class="{ active: ventForm.method === item.title }"
                    @click="ventForm.method = item.title"
                  >
                    <component :is="item.icon" />
                    <strong>{{ item.title }}</strong>
                    <span>{{ item.desc }}</span>
                  </button>
                </div>
              </PublishStep>

              <PublishStep index="6" title="匿名设置" hint="你的安全与隐私很重要">
                <div class="toggle-row">
                  <div>
                    <strong>匿名发布（推荐）</strong>
                    <p>隐藏你的昵称与头像，保护你的隐私。</p>
                  </div>
                  <button type="button" class="switch" :class="{ on: ventForm.anonymous }" @click="ventForm.anonymous = !ventForm.anonymous">
                    <span></span>
                  </button>
                </div>
              </PublishStep>

              <PublishStep index="7" title="可见范围" hint="谁可以看到你的这条倾诉？">
                <div class="choice-grid two">
                  <button
                    v-for="item in visibilityOptions"
                    :key="item.title"
                    type="button"
                    :class="{ active: ventForm.visibility === item.title }"
                    @click="ventForm.visibility = item.title"
                  >
                    <CircleCheck />
                    <strong>{{ item.title }}</strong>
                    <span>{{ item.desc }}</span>
                  </button>
                </div>
              </PublishStep>
            </template>

            <template v-else>
              <PublishStep index="1" title="匹配标题" hint="一句话说明你想找什么搭子（10-30字）">
                <label class="text-field">
                  <input v-model="matchForm.title" maxlength="30" placeholder="例如：周五晚想找羽毛球搭子" />
                  <span>{{ matchForm.title.length }} / 30</span>
                </label>
              </PublishStep>

              <PublishStep index="2" title="需求分类" hint="选择最合适的广场分类">
                <div class="chip-row">
                  <button
                    v-for="category in matchCategories"
                    :key="category"
                    type="button"
                    :class="{ active: matchForm.category === category }"
                    @click="matchForm.category = category"
                  >
                    {{ category }}
                  </button>
                </div>
              </PublishStep>

              <PublishStep index="3" title="详细说明" hint="补充活动内容、要求和你期待的氛围（10-800字）">
                <label class="textarea-field">
                  <textarea v-model="matchForm.content" maxlength="800" placeholder="例如：想找 1-2 位同学一起打羽毛球，新手友好，地点可商量。"></textarea>
                  <span>{{ matchForm.content.length }} / 800</span>
                </label>
              </PublishStep>

              <PublishStep index="4" title="时间地点" hint="把关键信息写清楚，减少来回确认">
                <div class="field-grid">
                  <label>
                    <span>时间</span>
                    <input v-model="matchForm.time" placeholder="例如：周五 19:00" />
                  </label>
                  <label>
                    <span>地点</span>
                    <input v-model="matchForm.location" placeholder="例如：体育馆 2 号场" />
                  </label>
                  <label>
                    <span>人数</span>
                    <input v-model="matchForm.people" placeholder="例如：2-4 人" />
                  </label>
                  <label>
                    <span>AA 费用</span>
                    <input v-model="matchForm.aaFee" placeholder="例如：AA 制，约 15 元/人" />
                  </label>
                </div>
              </PublishStep>

              <PublishStep index="5" title="匹配偏好" hint="让同学更快判断是否适合加入">
                <div class="chip-row">
                  <button
                    v-for="tag in matchTags"
                    :key="tag"
                    type="button"
                    :class="{ active: matchForm.tags.includes(tag) }"
                    @click="toggleItem(matchForm.tags, tag)"
                  >
                    {{ tag }}
                  </button>
                </div>
              </PublishStep>

              <PublishStep index="6" title="发布方式" hint="选择展示给广场同学的信息">
                <div class="choice-grid two">
                  <button
                    v-for="item in publishIdentityOptions"
                    :key="item.title"
                    type="button"
                    :class="{ active: matchForm.identity === item.title }"
                    @click="matchForm.identity = item.title"
                  >
                    <component :is="item.icon" />
                    <strong>{{ item.title }}</strong>
                    <span>{{ item.desc }}</span>
                  </button>
                </div>
              </PublishStep>
            </template>

            <section class="safety-row">
              <CircleCheck />
              <div>
                <strong>请勿发布涉及暴力、歧视、违法、极端或不当内容。</strong>
                <p>如遇不适或被冒犯，你可以随时屏蔽或举报。</p>
              </div>
              <button type="button" @click="goSafetyFeedback">查看社区规范 <ArrowRight /></button>
            </section>

            <section class="sensitive-row">
              <strong>敏感内容提示（可选）</strong>
              <label v-for="item in sensitiveItems" :key="item">
                <input v-model="activeSensitive" type="checkbox" :value="item" />
                <span>{{ item }}</span>
              </label>
            </section>

            <footer class="form-actions">
              <button type="button" class="draft-button" @click="saveDraft">保存草稿</button>
              <button type="submit" class="publish-button" :disabled="submitting">
                <Promotion />
                {{ submitButtonText }}
              </button>
            </footer>
            <p class="form-note">{{ formNote }}</p>
          </form>

          <aside class="preview-column">
            <section class="preview-card">
              <h2>预览效果 <span>仅你可见</span></h2>
              <div class="cloud-scene">
                <img :src="logoImage" alt="" />
              </div>
              <article class="post-preview">
                <div class="preview-head">
                  <span class="avatar">{{ activeMode === 'vent' ? '倾' : '伴' }}</span>
                  <div>
                    <strong>{{ activeMode === 'vent' ? '匿名倾诉' : '匹配需求' }}</strong>
                    <p>
                      <em v-for="tag in previewTags" :key="tag">{{ tag }}</em>
                    </p>
                  </div>
                </div>
                <h3>{{ previewTitle }}</h3>
                <p>{{ previewContent }}</p>
                <footer>
                  <span>{{ previewMeta }}</span>
                  <span>刚刚发布</span>
                </footer>
              </article>
            </section>

            <section class="guard-card">
              <h2>星伴的温暖守护 <span>❤</span></h2>
              <div v-for="item in guardItems" :key="item.title" class="guard-item">
                <span :class="item.tone"><component :is="item.icon" /></span>
                <div>
                  <strong>{{ item.title }}</strong>
                  <p>{{ item.desc }}</p>
                </div>
              </div>
            </section>

            <section class="comfort-card">
              <div class="plant-visual">✦</div>
              <div>
                <strong>你并不孤单</strong>
                <p>说出你的感受，就是迈出改变的第一步。我们在这里，陪你一起走下去。</p>
              </div>
            </section>
          </aside>
        </section>
      </section>
    </section>
  </main>
</template>

<script setup>
import { computed, h, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowRight,
  ChatDotRound,
  ChatRound,
  CircleCheck,
  Flag,
  Headset,
  HomeFilled,
  Lock,
  Message,
  Promotion,
  Search,
  Star,
  StarFilled,
  User
} from '@element-plus/icons-vue'
import logoImage from '../../../assets/images/logo-star-mascot.png'
import verifyImage from '../../../assets/images/renzheng.png'
import { createHomePost, fetchHomePostDetail, updateHomePost } from '../../../api/home'
import { useCurrentUserProfile } from '../../../composables/useCurrentUserProfile'
import NotificationBell from '../../../components/NotificationBell.vue'
import UserMenu from '../../../components/UserMenu.vue'

const PublishStep = {
  props: {
    index: { type: String, required: true },
    title: { type: String, required: true },
    hint: { type: String, required: true }
  },
  setup(props, { slots }) {
    return () => h('section', { class: 'publish-step' }, [
      h('div', { class: 'step-label' }, [
        h('span', props.index),
        h('div', [
          h('strong', props.title),
          h('p', props.hint)
        ])
      ]),
      h('div', { class: 'step-control' }, slots.default?.())
    ])
  }
}

const router = useRouter()
const route = useRoute()
const activeMode = ref('vent')
const keyword = ref('')
const activeSensitive = ref([])
const submitting = ref(false)
const customVentTag = ref('')
const customVentTagVisible = ref(false)
const editPostId = computed(() => {
  const id = route.query.editPostId
  return Array.isArray(id) ? id[0] : id
})
const isEditingPost = computed(() => Boolean(editPostId.value))
const {
  isLoggedIn,
  verified,
  statusText: userStatusText,
  avatarText: userInitial,
  avatarUrl: userAvatar,
  loadProfile: loadCurrentUserProfile
} = useCurrentUserProfile()

const ventForm = reactive({
  title: '',
  content: '',
  tags: ['心情不好'],
  response: '只倾听',
  method: '在线文字',
  anonymous: true,
  visibility: '仅匹配心灵伙伴可见'
})

const matchForm = reactive({
  title: '',
  category: '学习搭子',
  content: '',
  time: '',
  location: '',
  people: '2 人',
  aaFee: 'AA 制，费用待定',
  tags: ['新手友好', '校内'],
  identity: '实名发布'
})

const navItems = [
  { label: '广场首页', icon: HomeFilled, route: '/home' },
  { label: '发布需求', icon: Promotion, active: true },
  { label: '我的聊天', icon: Message, route: '/chat' },
  { label: '我的匹配', icon: StarFilled, route: '/my-match' },
  { label: '认证中心', icon: Lock, route: '/auth-center' },
  { label: '安全反馈', icon: Flag, route: '/safety-feedback' },
  { label: '个人中心', icon: User, route: '/profile' }
]

const ventTags = ['心情不好', '考试焦虑', '学业压力', '生活分享', '只想被听见', '人际困扰', '恋爱烦恼', '未来迷茫']
const matchCategories = ['学习搭子', '饭搭子', '运动搭子', '比赛组队', '活动同行', '闲聊陪伴']
const matchTags = ['新手友好', '校内', '可长期', '女生优先', '安静一点', 'AA 制', '时间灵活', '同专业优先']
const sensitiveItems = ['可能包含负面情绪', '涉及人际冲突', '感到自我否定', '其他（可选）']
const ventResponseOptions = [
  { title: '只倾听', desc: '静静听我说', icon: CircleCheck },
  { title: '温柔鼓励', desc: '给我一些安慰', icon: Star },
  { title: '想聊一聊', desc: '展开对话聊聊', icon: ChatRound }
]
const ventMethodOptions = [
  { title: '在线文字', desc: '文字聊天', icon: CircleCheck },
  { title: '语音', desc: '语音通话', icon: ChatDotRound },
  { title: '今晚可聊', desc: '希望今晚有人回应', icon: Headset }
]
const visibilityOptions = [
  { title: '仅匹配心灵伙伴可见', desc: '只有被你匹配的心灵伙伴能看到' },
  { title: '公开可见', desc: '所有人都可以看到此倾诉' }
]
const publishIdentityOptions = [
  { title: '实名发布', desc: '展示昵称和认证状态', icon: User },
  { title: '匿名发布', desc: '隐藏昵称，先保护隐私', icon: Lock }
]
const guardItems = [
  { title: '尊重与理解', desc: '我们倡导友善、尊重和包容的沟通环境。', icon: CircleCheck, tone: 'purple' },
  { title: '隐私保护', desc: '你的倾诉内容仅在你选择的范围内可见。', icon: Lock, tone: 'blue' },
  { title: '安全陪伴', desc: '心灵伙伴均经过认证，提供温暖陪伴。', icon: Star, tone: 'pink' },
  { title: '遇到不适可举报', desc: '任何不当言论，都可以举报或屏蔽。', icon: Flag, tone: 'violet' }
]

const previewTitle = computed(() => {
  if (activeMode.value === 'vent') {
    return ventForm.title || '考试压力好大，想找个人听我说说'
  }
  return matchForm.title || '周五晚想找羽毛球搭子'
})
const pageTitle = computed(() => {
  if (isEditingPost.value) {
    return '编辑匹配需求'
  }
  return activeMode.value === 'vent' ? '发布倾诉需求' : '发布匹配需求'
})
const pageDescription = computed(() => {
  if (isEditingPost.value) {
    return '修改时间、地点和期待后，同学会看到最新的匹配信息。'
  }
  return activeMode.value === 'vent'
    ? '在这里，说出你心里的话。星伴会保护你的隐私，温柔陪伴你。'
    : '写清楚时间、地点和期待，让合适的同学更快找到你。'
})
const submitButtonText = computed(() => {
  if (submitting.value) {
    return isEditingPost.value ? '保存中...' : '发布中...'
  }
  if (isEditingPost.value) {
    return '保存修改'
  }
  return `发布到${activeMode.value === 'vent' ? '倾诉广场' : '匹配广场'}`
})
const formNote = computed(() => {
  if (isEditingPost.value) {
    return '保存后可在“我的匹配”继续查看进度与回应。'
  }
  return `发布后可在“我的${activeMode.value === 'vent' ? '倾诉' : '匹配'}”查看进度与回应。`
})
const previewContent = computed(() => {
  if (activeMode.value === 'vent') {
    return ventForm.content || '这段时间复习压力有点大，总是静不下心来，想找一个温柔的人听我说说。'
  }
  return matchForm.content || '想找同学一起活动，时间地点可商量，希望轻松友好、不赶进度。'
})
const previewTags = computed(() => activeMode.value === 'vent' ? ventForm.tags.slice(0, 3) : [matchForm.category, ...matchForm.tags].slice(0, 3))
const visibleVentTags = computed(() => [...new Set([...ventTags, ...ventForm.tags])])
const previewMeta = computed(() => {
  if (activeMode.value === 'vent') {
    return `${ventForm.method} · ${ventForm.visibility}`
  }
  return `${matchForm.time || '时间待定'} · ${matchForm.location || '地点待定'} · ${matchForm.people} · ${matchForm.aaFee || 'AA 费用待定'}`
})

const toggleItem = (list, item) => {
  const index = list.indexOf(item)
  if (index >= 0) {
    list.splice(index, 1)
    return
  }
  list.push(item)
}

const addCustomVentTag = () => {
  const tag = customVentTag.value.trim()
  if (!tag) {
    ElMessage.warning('请输入自定义标签')
    return
  }
  if (ventForm.tags.includes(tag) || ventTags.includes(tag)) {
    if (!ventForm.tags.includes(tag)) {
      ventForm.tags.push(tag)
    }
    customVentTag.value = ''
    customVentTagVisible.value = false
    return
  }
  ventForm.tags.push(tag)
  customVentTag.value = ''
  customVentTagVisible.value = false
}

const saveDraft = () => {
  ElMessage.success('草稿已保存（前端模拟）')
}

const parsePeopleCount = (value) => {
  const count = Number.parseInt(String(value || '').match(/\d+/)?.[0] || '', 10)
  return Number.isFinite(count) && count > 0 ? count : 2
}

const validatePost = () => {
  const form = activeMode.value === 'vent' ? ventForm : matchForm
  if (!form.title.trim()) {
    ElMessage.error('请填写需求标题')
    return false
  }
  if (!form.content.trim()) {
    ElMessage.error('请填写详细内容')
    return false
  }
  if (activeMode.value === 'match' && (!matchForm.time.trim() || !matchForm.location.trim())) {
    ElMessage.error('请填写匹配需求的时间和地点')
    return false
  }
  return true
}

const buildPostPayload = () => {
  if (activeMode.value === 'vent') {
    return {
      plaza: 'vent',
      category: ventForm.tags[0] || '想找人聊聊',
      title: ventForm.title.trim(),
      description: ventForm.content.trim(),
      time: ventForm.method,
      location: ventForm.method,
      maxCount: 40,
      anonymous: ventForm.anonymous,
      tags: [...ventForm.tags, ventForm.response, ventForm.method],
      currentState: ventForm.tags,
      hopeYouCan: [ventForm.response],
      preferredWay: [ventForm.method, ventForm.visibility]
    }
  }

  return {
    plaza: 'match',
    category: matchForm.category,
    title: matchForm.title.trim(),
    description: matchForm.content.trim(),
    time: matchForm.time.trim(),
    location: matchForm.location.trim(),
    aaFee: matchForm.aaFee.trim(),
    maxCount: parsePeopleCount(matchForm.people),
    anonymous: matchForm.identity === '匿名发布',
    tags: matchForm.tags
  }
}

const fillMatchForm = (post) => {
  activeMode.value = 'match'
  matchForm.title = post.title || ''
  matchForm.category = post.category || matchForm.category
  matchForm.content = post.description || post.content || ''
  matchForm.time = post.time || ''
  matchForm.location = post.location || ''
  matchForm.people = post.maxCount ? `${post.maxCount} 人` : matchForm.people
  matchForm.aaFee = post.aaFee || ''
  matchForm.tags = Array.isArray(post.tags) ? [...post.tags] : []
  matchForm.identity = post.anonymous ? '匿名发布' : '实名发布'
}

const loadEditingPost = async () => {
  if (!editPostId.value) {
    return
  }
  try {
    const response = await fetchHomePostDetail(editPostId.value)
    fillMatchForm(response.data.data || {})
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '帖子加载失败，无法编辑')
    router.push('/my-match')
  }
}

const submitDraft = async () => {
  if (submitting.value) {
    return
  }
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录后再发布需求')
    goLogin()
    return
  }
  if (!verified.value) {
    ElMessage.warning('请先完成校园认证后再发布需求')
    goAuthCenter()
    return
  }
  if (!validatePost()) {
    return
  }

  submitting.value = true
  try {
    if (isEditingPost.value) {
      await updateHomePost(editPostId.value, buildPostPayload())
      ElMessage.success('匹配需求已更新')
      router.push(`/match-post/${editPostId.value}`)
      return
    }
    const response = await createHomePost(buildPostPayload())
    const createdPost = response.data.data
    ElMessage.success(`已发布到${activeMode.value === 'vent' ? '倾诉广场' : '匹配广场'}`)
    router.push(activeMode.value === 'vent' ? `/vent-post/${createdPost.id}` : `/match-post/${createdPost.id}`)
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '发布失败，请稍后再试')
  } finally {
    submitting.value = false
  }
}

const goLogin = () => {
  router.push('/login')
}

const goChat = () => {
  router.push('/chat')
}

const goAuthCenter = () => {
  router.push(isLoggedIn.value ? '/auth-center' : '/login')
}

const handleNav = (item) => {
  if (item.action === 'vent') {
    activeMode.value = 'vent'
    return
  }
  if (item.route) {
    router.push(item.route)
  }
}
const goSafetyFeedback = () => {
  router.push('/safety-feedback')
}
onMounted(() => {
  loadCurrentUserProfile().catch(() => {})
  loadEditingPost()
})
</script>

<style scoped>
.publish-page {
  min-height: 100vh;
  padding: 14px 20px;
  background:
    radial-gradient(circle at 10% 0%, rgba(255, 215, 238, 0.58), transparent 28%),
    radial-gradient(circle at 92% 8%, rgba(226, 214, 255, 0.78), transparent 34%),
    linear-gradient(135deg, #fbf8ff 0%, #f4f7ff 48%, #fff9fc 100%);
}

.publish-shell {
  display: grid;
  grid-template-columns: 245px minmax(0, 1fr);
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
  padding: 34px 0 78px 16px;
  border-radius: 28px 0 0 28px;
  background: linear-gradient(180deg, rgba(253, 250, 255, 0.96), rgba(247, 244, 255, 0.86));
  box-shadow: inset -1px 0 0 rgba(224, 216, 246, 0.54);
}

.brand,
.nav-item,
.topbar,
.top-actions,
.search-box,
.mode-tabs,
.publish-step,
.step-label,
.chip-row,
.toggle-row,
.safety-row,
.sensitive-row,
.form-actions,
.preview-head,
.guard-item,
.comfort-card {
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
  padding: 0 0 0 14px;
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
  color: #6b56ed;
  font-size: 16px;
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
}

.page-body {
  min-width: 0;
  padding: 18px 0 0 28px;
}

.topbar {
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 18px;
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

.plain-icon {
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

.mode-tabs {
  width: 388px;
  margin-bottom: 16px;
  overflow: hidden;
  border-radius: 14px;
  background: #f1eff8;
}

.mode-tabs button {
  flex: 1;
  height: 52px;
  color: #817b95;
  background: transparent;
  font-size: 18px;
  font-weight: 900;
}

.mode-tabs button.active {
  color: #fff;
  background: linear-gradient(105deg, #7460f4, #ffa4bd);
}

.content-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 420px;
  gap: 34px;
}

.form-card,
.preview-card,
.guard-card,
.comfort-card {
  border: 1px solid #ece7f8;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.74);
  box-shadow: 0 10px 28px rgba(112, 104, 151, 0.08);
}

.form-card {
  padding: 14px 18px 20px;
}

.form-card :deep(.publish-step) {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding: 12px 0;
  border-bottom: 1px solid #ece7f8;
}

.form-card :deep(.step-label) {
  display: flex;
  width: 210px;
  flex: 0 0 210px;
  align-items: flex-start;
  gap: 10px;
}

.form-card :deep(.step-label > span) {
  display: grid;
  width: 23px;
  height: 23px;
  place-items: center;
  border: 1px solid #cfc4ff;
  border-radius: 6px;
  color: #7061d8;
  background: #f0edff;
  font-size: 13px;
  font-weight: 900;
}

.form-card :deep(.step-label strong) {
  color: #20233f;
  font-size: 15px;
}

.form-card :deep(.step-label p) {
  margin: 4px 0 0;
  color: #85819a;
  font-size: 12px;
  line-height: 1.35;
}

.form-card :deep(.step-control) {
  flex: 1;
  min-width: 0;
}

.text-field,
.textarea-field {
  position: relative;
  display: block;
}

.text-field input,
.textarea-field textarea,
.field-grid input {
  width: 100%;
  border: 1px solid #e4def5;
  border-radius: 13px;
  color: #4b4961;
  background: rgba(255, 255, 255, 0.88);
  outline: 0;
  box-shadow: inset 0 1px 0 #fff;
}

.text-field input {
  height: 42px;
  padding: 0 70px 0 18px;
}

.textarea-field textarea {
  min-height: 98px;
  padding: 15px 16px 30px;
  resize: vertical;
  line-height: 1.55;
}

.text-field span,
.textarea-field span {
  position: absolute;
  right: 14px;
  bottom: 10px;
  color: #8d89a3;
  font-size: 12px;
  font-weight: 900;
}

.chip-row {
  flex-wrap: wrap;
  gap: 10px;
}

.chip-row button,
.choice-grid button {
  border: 1px solid #e7e2f3;
  color: #7a778f;
  background: rgba(255, 255, 255, 0.72);
  font-weight: 900;
}

.chip-row button {
  min-height: 36px;
  padding: 0 18px;
  border-radius: 10px;
}

.chip-row button.active,
.choice-grid button.active {
  color: #6650ed;
  border-color: #8c72fa;
  background: linear-gradient(135deg, #fff, #f3efff);
  box-shadow: 0 8px 18px rgba(118, 98, 244, 0.12);
}

.chip-row .soft-action {
  color: #765ff3;
  background: #f7f2ff;
}

.add-tag-button {
  display: inline-flex;
  align-items: center;
  gap: 7px;
}

.add-tag-button span {
  display: grid;
  width: 18px;
  height: 18px;
  place-items: center;
  border-radius: 50%;
  color: #fff;
  background: linear-gradient(135deg, #7460f4, #f06f9f);
  font-size: 15px;
  line-height: 1;
}

.custom-tag-field {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 38px;
  padding: 3px;
  overflow: hidden;
  border: 1px solid rgba(140, 114, 250, 0.62);
  border-radius: 12px;
  background:
    linear-gradient(#fff, #fff) padding-box,
    linear-gradient(105deg, rgba(116, 96, 244, 0.48), rgba(240, 111, 159, 0.38)) border-box;
  box-shadow: 0 10px 22px rgba(118, 98, 244, 0.13);
}

.custom-tag-field:focus-within {
  border-color: #8c72fa;
  box-shadow: 0 12px 26px rgba(118, 98, 244, 0.18);
}

.custom-tag-field input {
  width: 112px;
  height: 100%;
  padding: 0 12px;
  border: 0;
  outline: 0;
  color: #4b4961;
  border-radius: 9px;
  background: #faf8ff;
  font-size: 13px;
  font-weight: 800;
}

.chip-row .custom-tag-field button {
  min-height: 30px;
  padding: 0 11px;
  border: 0;
  border-radius: 9px;
  font-size: 12px;
}

.chip-row .custom-tag-submit {
  color: #fff;
  background: linear-gradient(105deg, #7460f4, #f06f9f);
}

.chip-row .custom-tag-cancel {
  color: #8a85a0;
  background: #f3efff;
}

.choice-grid {
  display: grid;
  gap: 12px;
}

.choice-grid.two {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.choice-grid.three {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.choice-grid button {
  display: grid;
  grid-template-columns: 28px 1fr;
  gap: 3px 10px;
  min-height: 58px;
  padding: 9px 12px;
  border-radius: 12px;
  text-align: left;
}

.choice-grid svg {
  grid-row: span 2;
  width: 22px;
  height: 22px;
  align-self: center;
  color: #836af5;
}

.choice-grid strong {
  color: inherit;
  font-size: 13px;
}

.choice-grid span {
  color: #8b879e;
  font-size: 11px;
}

.toggle-row {
  justify-content: space-between;
  gap: 16px;
  min-height: 58px;
  padding: 12px 16px;
  border: 1px solid #e7e2f3;
  border-radius: 13px;
  background: linear-gradient(135deg, #fff, #f8f4ff);
}

.toggle-row strong {
  color: #20233f;
}

.toggle-row p {
  margin: 4px 0 0;
  color: #85819a;
  font-size: 12px;
}

.switch {
  position: relative;
  width: 48px;
  height: 26px;
  border-radius: 999px;
  background: #d8d3ea;
}

.switch span {
  position: absolute;
  top: 4px;
  left: 4px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #fff;
  transition: transform 0.18s ease-out;
}

.switch.on {
  background: linear-gradient(105deg, #7460f4, #9d7bff);
}

.switch.on span {
  transform: translateX(22px);
}

.field-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.field-grid label {
  display: grid;
  gap: 7px;
}

.field-grid span {
  color: #666b84;
  font-size: 12px;
  font-weight: 900;
}

.field-grid input {
  height: 42px;
  padding: 0 12px;
}

.safety-row {
  gap: 12px;
  margin: 14px 0 10px;
  padding: 13px 16px;
  border-radius: 14px;
  background: linear-gradient(105deg, #fff6df, #fff8fb 58%, #f7f2ff);
}

.safety-row svg {
  width: 26px;
  color: #e0a622;
}

.safety-row div {
  flex: 1;
}

.safety-row strong {
  color: #4d415f;
  font-size: 13px;
}

.safety-row p {
  margin: 3px 0 0;
  color: #8d8199;
  font-size: 12px;
}

.safety-row button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #765ff3;
  background: transparent;
  font-size: 12px;
  font-weight: 900;
}

.safety-row button svg {
  width: 14px;
}

.sensitive-row {
  flex-wrap: wrap;
  gap: 18px;
  padding: 8px 0 12px;
  color: #85819a;
  font-size: 12px;
  font-weight: 900;
}

.sensitive-row strong {
  color: #20233f;
}

.sensitive-row label {
  display: inline-flex;
  align-items: center;
  gap: 7px;
}

.form-actions {
  justify-content: center;
  gap: 28px;
  padding-top: 6px;
}

.draft-button,
.publish-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 44px;
  border-radius: 999px;
  font-size: 15px;
  font-weight: 900;
}

.draft-button {
  min-width: 300px;
  color: #765ff3;
  border: 1px solid #8c72fa;
  background: #fff;
}

.publish-button {
  gap: 10px;
  min-width: 360px;
  color: #fff;
  background: linear-gradient(105deg, #7460f4, #f06f9f);
  box-shadow: 0 14px 28px rgba(129, 91, 244, 0.24);
}

.publish-button svg {
  width: 20px;
}

.publish-button:disabled {
  cursor: not-allowed;
  opacity: 0.72;
}

.form-note {
  margin: 10px 0 0;
  color: #8d89a3;
  text-align: center;
  font-size: 12px;
  font-weight: 800;
}

.preview-column {
  display: grid;
  align-content: start;
  gap: 18px;
}

.preview-card,
.guard-card {
  padding: 20px;
}

.preview-card h2,
.guard-card h2 {
  margin: 0;
  color: #6d55f0;
  font-size: 18px;
}

.preview-card h2 span {
  color: #85819a;
  font-size: 12px;
}

.cloud-scene {
  display: grid;
  height: 144px;
  place-items: center;
}

.cloud-scene img {
  width: 176px;
  transform: scale(1.18);
}

.post-preview {
  padding: 16px;
  border: 1px solid #eee7ff;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.82);
}

.preview-head {
  gap: 12px;
}

.avatar {
  display: grid;
  width: 42px;
  height: 42px;
  place-items: center;
  border-radius: 14px;
  color: #fff;
  background: linear-gradient(145deg, #8c72fa, #f06f9f);
  font-weight: 900;
}

.preview-head strong,
.post-preview h3 {
  color: #20233f;
}

.preview-head p {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin: 5px 0 0;
}

.preview-head em {
  padding: 3px 7px;
  border-radius: 999px;
  color: #765ff3;
  background: #f0ebff;
  font-size: 11px;
  font-style: normal;
  font-weight: 900;
}

.post-preview h3 {
  margin: 14px 0 8px;
  font-size: 17px;
}

.post-preview > p {
  margin: 0;
  color: #666b84;
  font-size: 13px;
  line-height: 1.65;
}

.post-preview footer {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 18px;
  color: #9a96ad;
  font-size: 12px;
  font-weight: 900;
}

.guard-card {
  display: grid;
  gap: 14px;
}

.guard-card h2 span {
  color: #ef5b87;
}

.guard-item {
  gap: 12px;
}

.guard-item > span {
  display: grid;
  width: 42px;
  height: 42px;
  flex: 0 0 42px;
  place-items: center;
  border-radius: 50%;
}

.guard-item svg {
  width: 22px;
}

.guard-item .purple {
  color: #765ff3;
  background: #eeeaff;
}

.guard-item .blue {
  color: #5b8dff;
  background: #eaf5ff;
}

.guard-item .pink {
  color: #ef5b87;
  background: #ffeaf3;
}

.guard-item .violet {
  color: #765ff3;
  background: #f0ebff;
}

.guard-item strong {
  color: #20233f;
  font-size: 14px;
}

.guard-item p {
  margin: 3px 0 0;
  color: #77798d;
  font-size: 12px;
  line-height: 1.45;
}

.comfort-card {
  gap: 14px;
  padding: 18px;
  background: linear-gradient(105deg, #f0ebff, #fff7fb);
}

.plant-visual {
  display: grid;
  width: 54px;
  height: 54px;
  flex: 0 0 54px;
  place-items: center;
  border-radius: 16px;
  color: #2ab28c;
  background: linear-gradient(145deg, #e1faf2, #fff);
  font-size: 28px;
}

.comfort-card strong {
  color: #20233f;
}

.comfort-card p {
  margin: 5px 0 0;
  color: #77798d;
  font-size: 13px;
  line-height: 1.55;
}

@media (max-width: 1320px) {
  .content-layout {
    grid-template-columns: 1fr;
  }

  .preview-column {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 980px) {
  .publish-shell {
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

  .page-body {
    padding-left: 0;
  }

  .topbar,
  .top-actions,
  .safety-row {
    align-items: flex-start;
    flex-direction: column;
  }

  .form-card :deep(.publish-step) {
    align-items: stretch;
    flex-direction: column;
  }

  .search-box,
  .mode-tabs,
  .draft-button,
  .publish-button {
    width: 100%;
    min-width: 0;
  }

  .form-card :deep(.step-label) {
    width: 100%;
    flex-basis: auto;
  }

  .choice-grid.two,
  .choice-grid.three,
  .field-grid,
  .preview-column {
    grid-template-columns: 1fr;
  }
}
</style>
