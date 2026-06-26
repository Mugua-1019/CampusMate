<template>
  <main class="profile-page">
    <section class="profile-shell">
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

      </aside>

      <section class="main-content" v-loading="loading">
        <header class="topbar">
          <div class="top-actions">
            <label class="search-box">
              <input v-model="keyword" placeholder="搜索需求、活动或用户" />
              <Search />
            </label>
            <button class="plain-icon" aria-label="我的聊天" @click="goChat"><Message /></button>
            <NotificationBell />
            <UserMenu
              :avatar-url="displayAvatarUrl"
              :avatar-text="avatarText"
              :status-text="verifyText"
            />
          </div>
        </header>

        <section class="profile-hero card">
          <img v-if="displayAvatarUrl" :src="displayAvatarUrl" alt="" class="avatar avatar-image" />
          <div v-else class="avatar">{{ avatarText }}</div>
          <div class="hero-info">
            <div class="name-row">
              <h1>{{ profile.nickname || '未命名用户' }}</h1>
              <EditPen />
              <span class="verify-dot" :class="{ pending: !profile.verified }"></span>
              <em>{{ verifyText }}</em>
            </div>
            <div class="info-pills">
              <span><Monitor /> {{ profile.major || '未填写专业' }}</span>
              <span><Reading /> {{ profile.grade || '未填写年级' }}</span>
              <span><School /> {{ profile.college || '未填写学院' }}</span>
            </div>
          </div>
          <div class="hero-bio">
            <p>{{ profile.bio || '这个同学还没有留下个人简介。' }}</p>
          </div>
          <button class="edit-profile" @click="openEditDialog">编辑资料</button>
        </section>

        <section class="summary-grid">
          <article class="card completion-card">
            <h2>我的资料完成度</h2>
            <div class="completion-body">
              <div class="progress-ring" :style="completionStyle">
                <strong>{{ profile.completionPercent || 0 }}%</strong>
              </div>
              <div>
                <p>{{ profile.completionHint || '完善资料，获得更多匹配机会。' }}</p>
                <button @click="openEditDialog">去完善</button>
              </div>
            </div>
          </article>

          <article class="card campus-card">
            <h2>{{ profile.campusVerify?.title || '校园认证' }}</h2>
            <div class="campus-body">
              <div class="shield-placeholder" aria-label="认证盾牌占位"></div>
              <div>
                <p>{{ profile.campusVerify?.description || '认证功能暂未开放。' }}</p>
                <button disabled>{{ profile.campusVerify?.actionText || '暂未开放' }}</button>
              </div>
            </div>
          </article>

          <article class="card tag-card">
            <div class="card-title">
              <h2>我的标签</h2>
              <button @click="openEditDialog">编辑</button>
            </div>
            <div class="tag-list">
              <span v-for="tag in profile.interestTags" :key="tag">{{ tag }}</span>
              <span v-if="!profile.interestTags?.length" class="empty-tag">暂无标签</span>
            </div>
            <p>共 {{ profile.interestTags?.length || 0 }} 个标签</p>
          </article>

          <article class="card preference-card">
            <div class="card-title">
              <h2>我的偏好搭子</h2>
              <button @click="openPreferenceDialog">编辑</button>
            </div>
            <div v-for="preference in preferences" :key="preference.label" class="preference-row">
              <span class="preference-icon" :class="preference.tone">
                <component :is="preferenceIcon(preference.icon)" />
              </span>
              <strong>{{ preference.label }}</strong>
              <em>偏好度</em>
              <div class="heart-row">
                <span
                  v-for="index in 6"
                  :key="index"
                  :class="{ muted: index > preference.score }"
                ></span>
              </div>
            </div>
          </article>
        </section>

        <section class="middle-grid">
          <article class="card vitality-card">
            <div class="card-title">
              <h2>本周活跃度</h2>
              <button>详情</button>
            </div>
            <div class="vitality-body">
              <div class="bar-chart">
                <div v-for="bar in weekBars" :key="bar.day">
                  <span :style="{ height: `${bar.value}px` }"></span>
                  <em>{{ bar.day }}</em>
                </div>
              </div>
              <div class="score-block">
                <div class="score-star"><StarFilled /></div>
                <strong>{{ profile.activity?.score || 0 }} <small>分</small></strong>
                <span>活跃值</span>
                <p>超过了 <b>{{ profile.activity?.percentile || 0 }}%</b> 的同学</p>
              </div>
            </div>
          </article>

          <article class="card safety-card">
            <h2>信用/安全状态</h2>
            <div class="safety-body">
              <div class="safety-shield"><Check /></div>
              <strong>{{ profile.safety?.status || '暂无' }}</strong>
              <div class="safety-metrics">
                <span>信用分 {{ profile.safety?.creditScore || 0 }} 分</span>
                <i></i>
                <span>安全等级 <b>{{ profile.safety?.safetyLevel || '未评估' }}</b></span>
              </div>
              <ul>
                <li v-for="item in safetyItems" :key="item">{{ item }}</li>
              </ul>
              <button disabled>去认证</button>
            </div>
          </article>
        </section>

        <section class="content-grid">
          <article class="card chat-card">
            <div class="card-title">
              <h2>最近聊天与匹配</h2>
              <button @click="goChat">查看全部 <ArrowRight /></button>
            </div>
            <div v-for="chat in chats" :key="chat.name" class="chat-row" @click="openRecentChat(chat)">
              <div class="mini-avatar" :class="chat.tone">{{ chat.avatar }}</div>
              <div>
                <strong>{{ chat.name }} <span>{{ chat.tag }}</span></strong>
                <p>{{ chat.message }}</p>
              </div>
              <time>{{ chat.time }}</time>
              <em v-if="chat.unread">{{ chat.unread }}</em>
            </div>
          </article>

          <article class="card publish-card">
            <div class="card-title">
              <h2>我的发布</h2>
              <button @click="goMyMatch">查看全部 <ArrowRight /></button>
            </div>
            <div v-for="post in posts" :key="post.title" class="post-row" @click="openPublishedPost(post)">
              <div class="post-type" :class="post.tone">{{ post.shortLabel }}</div>
              <div>
                <strong>{{ post.title }} <span>{{ post.tag }}</span> <small>{{ post.period }}</small></strong>
                <p>{{ post.description }}</p>
                <div class="post-meta">
                  <span><Location /> {{ post.location }}</span>
                  <span><Clock /> {{ post.time }}</span>
                </div>
              </div>
              <em>{{ post.matched }}人已匹配</em>
            </div>
          </article>
        </section>

        <section class="badge-section">
          <h2>我的成就徽章</h2>
          <div class="badge-grid">
            <article
              v-for="badge in featuredAchievements"
              :key="badge.key"
              class="badge-card"
              :class="[badge.tone, { locked: !badge.achieved }]"
            >
              <img :src="badge.image" :alt="badge.title" class="badge-image" />
              <div>
                <strong>{{ badge.title }}</strong>
                <p>{{ badge.condition }}</p>
                <span>获得时间：{{ badge.achievedAt }}</span>
              </div>
            </article>
            <article class="badge-card more-badge" role="button" tabindex="0" @click="badgeWallVisible = true" @keydown.enter="badgeWallVisible = true">
              <div class="badge-more-icon"><ArrowRight /></div>
              <div>
                <strong>更多徽章</strong>
                <p>打开完整徽章墙</p>
                <span>共 {{ achievementBadges.length }} 枚</span>
              </div>
            </article>
          </div>
        </section>
      </section>
    </section>

    <el-dialog v-model="editVisible" title="编辑资料" width="560px">
      <el-form label-width="86px">
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            :auto-upload="false"
            :show-file-list="false"
            accept="image/png,image/jpeg,image/webp,image/gif"
            :on-change="handleAvatarChange"
          >
            <img v-if="editForm.avatarUrl" :src="editForm.avatarUrl" alt="" class="edit-avatar" />
            <div v-else class="edit-avatar empty"><Plus /></div>
          </el-upload>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" maxlength="60" />
        </el-form-item>
        <el-form-item label="性别">
          <el-input v-model="editForm.gender" maxlength="20" />
        </el-form-item>
        <el-form-item label="年级">
          <el-input v-model="editForm.grade" maxlength="20" />
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="editForm.college" maxlength="80" />
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="editForm.major" maxlength="80" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="tagText" placeholder="用逗号分隔多个标签" maxlength="255" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="editForm.bio" type="textarea" :rows="3" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <button class="dialog-cancel" @click="editVisible = false">取消</button>
        <button class="dialog-save" :disabled="saving" @click="submitProfile">
          {{ saving ? '保存中...' : '保存' }}
        </button>
      </template>
    </el-dialog>

    <el-dialog v-model="preferenceVisible" title="选择偏好档位" width="560px">
      <section class="preference-dialog">
        <article
          v-for="preference in preferenceForm"
          :key="preference.label"
          class="preference-editor-row"
        >
          <span class="preference-icon" :class="preference.tone">
            <component :is="preferenceIcon(preference.icon)" />
          </span>
          <div>
            <strong>{{ preference.label }}</strong>
            <p>{{ preferenceLevelText(preference.score) }}</p>
          </div>
          <el-rate
            v-model="preference.score"
            :max="6"
            :texts="preferenceLevelTexts"
            show-text
          />
        </article>
        <p v-if="!preferenceForm.length" class="empty-note">暂无可编辑偏好</p>
      </section>
      <template #footer>
        <button class="dialog-cancel" @click="preferenceVisible = false">取消</button>
        <button class="dialog-save" :disabled="preferenceSaving || !preferenceForm.length" @click="submitPreferences">
          {{ preferenceSaving ? '保存中...' : '保存' }}
        </button>
      </template>
    </el-dialog>

    <el-dialog v-model="badgeWallVisible" title="成就徽章墙" width="860px" class="badge-wall-dialog">
      <section class="badge-wall-grid">
        <article
          v-for="badge in achievementBadges"
          :key="badge.key"
          class="badge-wall-card"
          :class="[badge.tone, { locked: !badge.achieved }]"
        >
          <img :src="badge.image" :alt="badge.title" class="badge-wall-image" />
          <div>
            <strong>{{ badge.title }}</strong>
            <p>{{ badge.condition }}</p>
            <span>获得时间：{{ badge.achievedAt }}</span>
          </div>
        </article>
      </section>
    </el-dialog>

    <el-dialog v-model="cropVisible" title="裁剪头像" width="520px" @closed="resetCropper">
      <div class="cropper-panel">
        <div
          ref="cropFrameRef"
          class="crop-frame"
          @pointerdown="startCropDrag"
          @pointermove="moveCropDrag"
          @pointerup="endCropDrag"
          @pointerleave="endCropDrag"
        >
          <img
            v-if="cropImageUrl"
            ref="cropImageRef"
            :src="cropImageUrl"
            alt=""
            class="crop-image"
            :style="cropImageStyle"
            draggable="false"
            @load="handleCropImageLoad"
          />
          <span class="crop-mask"></span>
        </div>
        <div class="crop-toolbar">
          <span>缩放</span>
          <el-slider v-model="cropState.scale" :min="1" :max="3" :step="0.01" />
        </div>
      </div>
      <template #footer>
        <button class="dialog-cancel" @click="cropVisible = false">取消</button>
        <button class="dialog-save" :disabled="cropping" @click="confirmCropAvatar">
          {{ cropping ? '上传中...' : '确认裁剪' }}
        </button>
      </template>
    </el-dialog>
  </main>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import {
  ArrowDown,
  ArrowRight,
  Bell,
  Check,
  Clock,
  EditPen,
  Flag,
  Headset,
  HomeFilled,
  Location,
  Lock,
  Message,
  Monitor,
  Plus,
  Promotion,
  Reading,
  School,
  Search,
  StarFilled,
  User
} from '@element-plus/icons-vue'
import { fetchProfile, updateProfile, updateProfilePreferences, uploadAvatar } from '../../../api/profile'
import { getCurrentUser, saveCurrentUser } from '../../../utils/currentUser'
import logoImage from '../../../assets/images/logo-star-mascot.png'
import anquanweishiImage from '../../../assets/images/chengjiu/anquanweishi.png'
import pipeidarenImage from '../../../assets/images/chengjiu/pipeidaren.png'
import xiaoyuanhuodituImage from '../../../assets/images/chengjiu/xiaoyuanhuoditu.png'
import xuexidarenImage from '../../../assets/images/chengjiu/xuexidaren.png'
import yundongdarenImage from '../../../assets/images/chengjiu/yundongdaren.png'
import zuijiatingzhongImage from '../../../assets/images/chengjiu/zuijiatingzhong.png'
import NotificationBell from '../../../components/NotificationBell.vue'
import UserMenu from '../../../components/UserMenu.vue'

const router = useRouter()
const currentUser = ref(getCurrentUser())
const currentUserId = computed(() => currentUser.value?.userId || 1)
const keyword = ref('')
const loading = ref(false)
const saving = ref(false)
const preferenceSaving = ref(false)
const cropping = ref(false)
const editVisible = ref(false)
const preferenceVisible = ref(false)
const badgeWallVisible = ref(false)
const cropVisible = ref(false)
const tagText = ref('')
const profile = ref({})
const preferenceForm = ref([])
const cropFrameRef = ref(null)
const cropImageRef = ref(null)
const cropImageUrl = ref('')
const selectedAvatarFile = ref(null)
const cropDrag = reactive({
  active: false,
  pointerId: null,
  startX: 0,
  startY: 0,
  originX: 0,
  originY: 0
})
const cropState = reactive({
  scale: 1,
  offsetX: 0,
  offsetY: 0,
  naturalWidth: 0,
  naturalHeight: 0
})
const editForm = reactive({
  userId: null,
  avatarUrl: '',
  nickname: '',
  gender: '',
  grade: '',
  college: '',
  major: '',
  bio: ''
})

const navItems = [
  { label: '广场首页', icon: HomeFilled, route: '/home' },
  { label: '发布需求', icon: Promotion, route: '/publish' },
  { label: '我的聊天', icon: Message, route: '/chat' },
  { label: '我的匹配', icon: StarFilled, route: '/my-match' },
  { label: '认证中心', icon: Lock, route: '/auth-center' },
  { label: '安全反馈', icon: Flag, route: '/safety-feedback' },
  { label: '个人中心', icon: User, active: true }
]

const defaultAchievements = [
  { key: 'anquanweishi', title: '安全卫士', condition: '完成校园认证', achieved: false, achievedAt: '----------', tone: 'green' },
  { key: 'pipeidaren', title: '匹配达人', condition: '累计完成五次匹配', achieved: false, achievedAt: '----------', tone: 'purple' },
  { key: 'xiaoyuanhuoditu', title: '校园活地图', condition: '完成校园认证且个人资料完善度达到100%', achieved: false, achievedAt: '----------', tone: 'blue' },
  { key: 'xuexidaren', title: '学习达人', condition: '该用户完成三次学习搭子匹配', achieved: false, achievedAt: '----------', tone: 'indigo' },
  { key: 'yundongdaren', title: '运动达人', condition: '该用户完成三次运动搭子匹配', achieved: false, achievedAt: '----------', tone: 'green' },
  { key: 'zuijiatingzhong', title: '最佳听众', condition: '该用户累计评论或安慰倾诉广场帖子达十次', achieved: false, achievedAt: '----------', tone: 'orange' }
]

const achievementImages = {
  anquanweishi: anquanweishiImage,
  pipeidaren: pipeidarenImage,
  xiaoyuanhuoditu: xiaoyuanhuodituImage,
  xuexidaren: xuexidarenImage,
  yundongdaren: yundongdarenImage,
  zuijiatingzhong: zuijiatingzhongImage
}

const preferences = computed(() => profile.value.preferences || [])
const preferenceLevelTexts = ['轻度', '有点', '常用', '喜欢', '优先', '强烈']
const weekBars = computed(() => profile.value.activity?.weekBars || [])
const safetyItems = computed(() => profile.value.safety?.items || [])
const chats = computed(() => profile.value.chats || [])
const posts = computed(() => profile.value.posts || [])
const achievementBadges = computed(() => {
  const source = profile.value.achievements?.length ? profile.value.achievements : defaultAchievements
  return source.map((badge) => ({
    ...badge,
    image: achievementImages[badge.key] || anquanweishiImage,
    achievedAt: badge.achieved ? (badge.achievedAt || '已获得') : '----------'
  }))
})
const featuredAchievements = computed(() => achievementBadges.value.slice(0, 3))
const avatarText = computed(() => (profile.value.nickname || '星').slice(0, 1))
const verifyText = computed(() => (profile.value.verified ? '已认证' : '待认证'))
const displayAvatarUrl = computed(() => {
  const avatarUrl = profile.value.avatarUrl || currentUser.value?.avatarUrl || ''
  return avatarUrl
})
const completionStyle = computed(() => ({
  background: `radial-gradient(circle, #fff 0 55%, transparent 56%), conic-gradient(#725af1 0 ${profile.value.completionPercent || 0}%, #e7e1ff ${profile.value.completionPercent || 0}% 100%)`
}))
const cropImageStyle = computed(() => ({
  width: cropState.naturalWidth ? `${cropDisplayWidth.value}px` : '100%',
  height: cropState.naturalHeight ? `${cropDisplayHeight.value}px` : '100%',
  transform: `translate(-50%, -50%) translate(${cropState.offsetX}px, ${cropState.offsetY}px) scale(${cropState.scale})`
}))
const cropBaseScale = computed(() => {
  if (!cropState.naturalWidth || !cropState.naturalHeight) {
    return 1
  }
  return Math.max(280 / cropState.naturalWidth, 280 / cropState.naturalHeight)
})
const cropDisplayWidth = computed(() => cropState.naturalWidth * cropBaseScale.value)
const cropDisplayHeight = computed(() => cropState.naturalHeight * cropBaseScale.value)

const iconMap = {
  reading: Reading,
  promotion: Promotion,
  headset: Headset
}

const preferenceIcon = (icon) => iconMap[icon] || StarFilled

const preferenceLevelText = (score) => {
  const index = Math.max(1, Math.min(6, Number(score) || 1)) - 1
  return `${preferenceLevelTexts[index]}偏好`
}

const loadProfile = async () => {
  loading.value = true
  try {
    const { data } = await fetchProfile(currentUserId.value)
    profile.value = data.data || {}
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '个人中心数据加载失败')
  } finally {
    loading.value = false
  }
}

const openEditDialog = () => {
  const current = profile.value
  editForm.userId = current.userId || currentUserId.value
  editForm.avatarUrl = current.avatarUrl || ''
  editForm.nickname = current.nickname || ''
  editForm.gender = current.gender || ''
  editForm.grade = current.grade || ''
  editForm.college = current.college || ''
  editForm.major = current.major || ''
  editForm.bio = current.bio || ''
  tagText.value = (current.interestTags || []).join('，')
  editVisible.value = true
}

const openPreferenceDialog = () => {
  preferenceForm.value = preferences.value.map((item) => ({
    label: item.label,
    icon: item.icon,
    tone: item.tone,
    score: Math.max(1, Math.min(6, Number(item.score) || 1))
  }))
  preferenceVisible.value = true
}

const handleAvatarChange = async (uploadFile) => {
  if (!uploadFile.raw) {
    return
  }
  if (!uploadFile.raw.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }
  openCropper(uploadFile.raw)
}

const openCropper = async (file) => {
  revokeCropImageUrl()
  selectedAvatarFile.value = file
  cropState.scale = 1
  cropState.offsetX = 0
  cropState.offsetY = 0
  cropState.naturalWidth = 0
  cropState.naturalHeight = 0
  cropImageUrl.value = URL.createObjectURL(file)
  cropVisible.value = true
  await nextTick()
}

const handleCropImageLoad = () => {
  if (!cropImageRef.value) {
    return
  }
  cropState.naturalWidth = cropImageRef.value.naturalWidth
  cropState.naturalHeight = cropImageRef.value.naturalHeight
}

const startCropDrag = (event) => {
  if (!cropImageUrl.value) {
    return
  }
  cropDrag.active = true
  cropDrag.pointerId = event.pointerId
  cropDrag.startX = event.clientX
  cropDrag.startY = event.clientY
  cropDrag.originX = cropState.offsetX
  cropDrag.originY = cropState.offsetY
  event.currentTarget.setPointerCapture(event.pointerId)
}

const moveCropDrag = (event) => {
  if (!cropDrag.active || cropDrag.pointerId !== event.pointerId) {
    return
  }
  cropState.offsetX = cropDrag.originX + event.clientX - cropDrag.startX
  cropState.offsetY = cropDrag.originY + event.clientY - cropDrag.startY
}

const endCropDrag = (event) => {
  if (cropDrag.pointerId === event.pointerId) {
    cropDrag.active = false
    cropDrag.pointerId = null
  }
}

const confirmCropAvatar = async () => {
  if (!cropImageRef.value || !cropFrameRef.value || !selectedAvatarFile.value) {
    return
  }
  try {
    cropping.value = true
    const avatarFile = await createCroppedAvatarFile()
    const { data } = await uploadAvatar(avatarFile)
    editForm.avatarUrl = data.data.avatarUrl
    profile.value = {
      ...profile.value,
      avatarUrl: data.data.avatarUrl
    }
    currentUser.value = {
      ...(currentUser.value || {}),
      avatarUrl: data.data.avatarUrl
    }
    saveCurrentUser(currentUser.value)
    cropVisible.value = false
    ElMessage.success('头像上传成功')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '头像上传失败')
  } finally {
    cropping.value = false
  }
}

const createCroppedAvatarFile = () => {
  const frameRect = cropFrameRef.value.getBoundingClientRect()
  const size = frameRect.width
  const outputSize = 512
  const canvas = document.createElement('canvas')
  canvas.width = outputSize
  canvas.height = outputSize
  const context = canvas.getContext('2d')
  if (!context) {
    throw new Error('avatar crop failed')
  }
  const finalScale = cropBaseScale.value * cropState.scale
  const imageLeft = size / 2 + cropState.offsetX - (cropState.naturalWidth * finalScale) / 2
  const imageTop = size / 2 + cropState.offsetY - (cropState.naturalHeight * finalScale) / 2
  const outputRatio = outputSize / size
  context.fillStyle = '#ffffff'
  context.fillRect(0, 0, outputSize, outputSize)
  context.drawImage(
    cropImageRef.value,
    imageLeft * outputRatio,
    imageTop * outputRatio,
    cropState.naturalWidth * finalScale * outputRatio,
    cropState.naturalHeight * finalScale * outputRatio
  )
  return new Promise((resolve, reject) => {
    canvas.toBlob((blob) => {
      if (!blob) {
        reject(new Error('avatar crop failed'))
        return
      }
      resolve(new File([blob], 'avatar.jpg', { type: 'image/jpeg' }))
    }, 'image/jpeg', 0.9)
  })
}

const revokeCropImageUrl = () => {
  if (cropImageUrl.value) {
    URL.revokeObjectURL(cropImageUrl.value)
    cropImageUrl.value = ''
  }
}

const resetCropper = () => {
  revokeCropImageUrl()
  selectedAvatarFile.value = null
  cropDrag.active = false
  cropDrag.pointerId = null
  cropState.scale = 1
  cropState.offsetX = 0
  cropState.offsetY = 0
  cropState.naturalWidth = 0
  cropState.naturalHeight = 0
}

onBeforeUnmount(() => {
  revokeCropImageUrl()
})

const submitProfile = async () => {
  saving.value = true
  try {
    const tags = tagText.value
      .split(/[,，]/)
      .map((tag) => tag.trim())
      .filter(Boolean)
    const nickname = (
      editForm.nickname ||
      profile.value.nickname ||
      currentUser.value?.nickname ||
      '未命名用户'
    ).trim()
    const { data } = await updateProfile({
      ...editForm,
      nickname,
      interestTags: tags
    })
    profile.value = data.data || {}
    const nextUser = {
      ...(currentUser.value || {}),
      userId: profile.value.userId,
      nickname: profile.value.nickname,
      avatarUrl: profile.value.avatarUrl
    }
    currentUser.value = nextUser
    saveCurrentUser(nextUser)
    editVisible.value = false
    ElMessage.success('资料已保存')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '资料保存失败')
  } finally {
    saving.value = false
  }
}

const submitPreferences = async () => {
  preferenceSaving.value = true
  try {
    const { data } = await updateProfilePreferences({
      userId: currentUserId.value,
      preferences: preferenceForm.value.map((item) => ({
        label: item.label,
        score: item.score
      }))
    })
    profile.value = data.data || {}
    preferenceVisible.value = false
    ElMessage.success('偏好档位已保存')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '偏好保存失败')
  } finally {
    preferenceSaving.value = false
  }
}

const handleNav = (item) => {
  if (item.route) {
    router.push(item.route)
  }
}

const goChat = () => {
  router.push('/chat')
}

const goMyMatch = () => {
  router.push('/my-match')
}

const openRecentChat = (chat) => {
  if (chat.peerUserId) {
    router.push({
      path: '/chat',
      query: { userId: chat.peerUserId }
    })
    return
  }
  router.push({
    path: '/chat',
    query: chat.name ? { name: chat.name, source: chat.tag } : {}
  })
}

const openPublishedPost = (post) => {
  if (post.postId) {
    router.push(`/${post.plaza === 'vent' ? 'vent-post' : 'match-post'}/${post.postId}`)
    return
  }
  router.push({
    path: '/my-match',
    query: post.title ? { keyword: post.title } : {}
  })
}

onMounted(loadProfile)
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  padding: 14px 20px;
  background:
    radial-gradient(circle at 10% 0%, rgba(255, 215, 238, 0.58), transparent 28%),
    radial-gradient(circle at 92% 8%, rgba(226, 214, 255, 0.78), transparent 34%),
    linear-gradient(135deg, #fbf8ff 0%, #f4f7ff 48%, #fff9fc 100%);
}

.profile-shell {
  position: relative;
  display: grid;
  grid-template-columns: 245px minmax(760px, 1fr);
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

.brand,
.topbar,
.top-actions,
.completion-body,
.campus-body,
.vitality-body {
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

.card h2,
.badge-section h2 {
  margin: 0;
}

.main-content {
  min-width: 0;
  padding-top: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.28);
}

.topbar {
  justify-content: flex-end;
  gap: 20px;
  min-height: 64px;
  margin-bottom: 28px;
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

.plain-icon,
.profile-chip,
.card-title button {
  background: transparent;
}

.plain-icon {
  position: relative;
  display: grid;
  width: 34px;
  height: 34px;
  place-items: center;
  color: #111;
}

.plain-icon svg {
  width: 28px;
  height: 28px;
}

.notice-button span {
  position: absolute;
  top: -8px;
  right: -7px;
  display: grid;
  width: 22px;
  height: 22px;
  place-items: center;
  border-radius: 50%;
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
  font-size: 14px;
  font-weight: 800;
}

.profile-avatar,
.profile-avatar-image,
.avatar {
  display: grid;
  place-items: center;
  border-radius: 50%;
}

.profile-avatar,
.profile-avatar-image {
  width: 50px;
  height: 50px;
}

.profile-avatar {
  color: #6853f1;
  background: #dcd5ff;
  font-size: 22px;
  font-weight: 900;
}

.profile-avatar-image,
.avatar-image,
.edit-avatar {
  object-fit: cover;
}

.profile-chip span:nth-child(2) {
  padding: 7px 12px;
  border-radius: 999px;
  background: #fff0d9;
}

.profile-chip :deep(svg) {
  width: 18px;
  height: 18px;
}

.card {
  border: 1px solid #ebe7f8;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.76);
  box-shadow: 0 9px 24px rgba(112, 104, 151, 0.08);
}

.profile-hero {
  display: grid;
  grid-template-columns: 112px minmax(300px, 1fr) minmax(240px, 1fr) 176px;
  align-items: center;
  gap: 20px;
  min-height: 142px;
  padding: 20px 28px 20px 44px;
}

.avatar {
  width: 88px;
  height: 88px;
  color: #4b2868;
  background: linear-gradient(150deg, #e6d2ff, #c6b4ff);
  font-size: 31px;
  font-weight: 900;
}

.name-row,
.info-pills,
.card-title,
.post-meta,
.heart-row {
  display: flex;
  align-items: center;
}

.name-row {
  gap: 12px;
  margin-bottom: 20px;
}

.name-row h1 {
  margin: 0;
  color: #11152f;
  font-size: 24px;
}

.name-row :deep(svg) {
  width: 16px;
  height: 16px;
  flex: 0 0 16px;
  color: #9790b0;
}

.name-row em {
  color: #88889d;
  font-style: normal;
  font-size: 14px;
  font-weight: 800;
}

.verify-dot {
  width: 11px;
  height: 11px;
  border-radius: 50%;
  background: #51c975;
}

.verify-dot.pending {
  background: #ffbc35;
}

.info-pills {
  flex-wrap: wrap;
  gap: 16px;
}

.info-pills span {
  display: inline-flex;
  align-items: center;
  gap: 9px;
  min-height: 36px;
  padding: 0 16px;
  border: 1px solid #eee9fa;
  border-radius: 8px;
  color: #515174;
  background: rgba(255, 255, 255, 0.82);
  font-size: 13px;
  font-weight: 900;
}

.info-pills :deep(svg) {
  width: 17px;
  height: 17px;
  flex: 0 0 17px;
  color: #7760f5;
}

.hero-bio {
  min-height: 98px;
  padding: 18px;
  border-radius: 10px;
  background: #f7f4ff;
}

.hero-bio p {
  margin: 0;
  color: #666985;
  font-size: 14px;
  line-height: 1.7;
}

.edit-profile,
.completion-card button,
.campus-card button,
.safety-card button,
.dialog-save {
  border: 0;
  border-radius: 999px;
  color: #fff;
  background: linear-gradient(105deg, #755df4, #ef6ea7);
  font-weight: 900;
  cursor: pointer;
}

.edit-profile {
  justify-self: end;
  min-width: 166px;
  height: 50px;
  font-size: 16px;
}

.summary-grid,
.middle-grid,
.content-grid {
  display: grid;
  gap: 20px;
  margin-top: 18px;
}

.summary-grid {
  grid-template-columns: 1.15fr 1.1fr 1.34fr 1.4fr;
}

.middle-grid {
  grid-template-columns: 1.03fr 1.24fr;
}

.content-grid {
  grid-template-columns: 1fr 1.22fr;
}

.summary-grid .card,
.middle-grid .card,
.content-grid .card {
  min-height: 148px;
  padding: 18px 22px;
}

.card h2,
.badge-section h2 {
  color: #151936;
  font-size: 18px;
}

.card-title {
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 14px;
}

.card-title button {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  flex: 0 0 auto;
  color: #7460f4;
  font-size: 13px;
  font-weight: 900;
  white-space: nowrap;
}

.card-title button :deep(svg) {
  width: 16px;
  height: 16px;
  flex: 0 0 16px;
}

.completion-body {
  justify-content: space-between;
  gap: 18px;
  margin-top: 16px;
}

.progress-ring {
  display: grid;
  width: 88px;
  height: 88px;
  flex: 0 0 88px;
  place-items: center;
  border-radius: 50%;
}

.progress-ring strong {
  color: #725af1;
  font-size: 24px;
  line-height: 1;
}

.progress-ring span {
  margin-top: -20px;
  color: #77738b;
  font-size: 12px;
  font-weight: 800;
}

.completion-card p,
.campus-card p {
  max-width: 174px;
  margin: 0 0 13px;
  color: #666985;
  font-size: 13px;
  font-weight: 800;
  line-height: 1.55;
}

.completion-card button,
.campus-card button,
.safety-card button {
  min-width: 88px;
  height: 32px;
}

.campus-card button:disabled,
.safety-card button:disabled,
.dialog-save:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.campus-body {
  gap: 22px;
  margin-top: 22px;
}

.shield-placeholder {
  width: 82px;
  height: 82px;
  border: 2px dashed rgba(117, 93, 244, 0.28);
  border-radius: 22px 22px 28px 28px;
  background: rgba(247, 244, 255, 0.62);
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin: 20px 0;
}

.tag-list span {
  min-height: 30px;
  padding: 6px 16px;
  border: 1px solid #ded7fb;
  border-radius: 999px;
  color: #765ff2;
  background: #f8f5ff;
  font-size: 13px;
  font-weight: 900;
}

.tag-list .empty-tag {
  color: #9692a6;
}

.tag-card p {
  margin: 0;
  color: #575a78;
  font-weight: 900;
}

.preference-row {
  display: grid;
  grid-template-columns: 34px 92px 52px 1fr;
  align-items: center;
  gap: 12px;
  min-height: 36px;
  color: #64677e;
  font-size: 13px;
}

.preference-icon {
  display: grid;
  width: 28px;
  height: 28px;
  place-items: center;
  border-radius: 50%;
  color: #fff;
}

.preference-icon :deep(svg) {
  width: 17px;
  height: 17px;
}

.preference-icon.purple {
  background: #b49eff;
}

.preference-icon.coral {
  background: #ff908d;
}

.preference-icon.pink {
  background: #ffa4a5;
}

.preference-row em {
  color: #8e90a5;
  font-style: normal;
}

.heart-row {
  gap: 7px;
}

.heart-row span {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #7563f4;
}

.heart-row span.muted {
  background: #e3def8;
}

.vitality-body {
  justify-content: space-between;
  gap: 28px;
  margin-top: 14px;
}

.bar-chart {
  display: grid;
  grid-template-columns: repeat(7, 34px);
  align-items: end;
  gap: 18px;
  height: 90px;
}

.bar-chart div {
  display: grid;
  justify-items: center;
  gap: 8px;
}

.bar-chart span {
  width: 11px;
  border-radius: 8px 8px 2px 2px;
  background: linear-gradient(180deg, #755df4, #c6b4ff);
}

.bar-chart em,
.score-block span,
.score-block small {
  color: #666985;
  font-style: normal;
  font-size: 13px;
}

.score-block {
  display: grid;
  grid-template-columns: 44px 1fr;
  align-items: center;
  gap: 2px 14px;
  min-width: 178px;
}

.score-star {
  grid-row: span 2;
  display: grid;
  width: 42px;
  height: 42px;
  place-items: center;
  border-radius: 12px;
  color: #fff;
  background: #ffbc35;
}

.score-star :deep(svg) {
  width: 28px;
  height: 28px;
}

.score-block strong {
  color: #11152f;
  font-size: 24px;
}

.score-block p {
  grid-column: 1 / -1;
  margin: 12px 0 0;
  color: #777a94;
  font-size: 13px;
  text-align: right;
}

.score-block b,
.safety-metrics b {
  color: #27bf6c;
}

.safety-body {
  display: grid;
  grid-template-columns: 74px 54px minmax(180px, 1fr) minmax(220px, 330px) 88px;
  align-items: center;
  gap: 18px;
  margin-top: 20px;
}

.safety-shield {
  display: grid;
  width: 66px;
  height: 66px;
  place-items: center;
  border-radius: 19px 19px 24px 24px;
  color: #39c783;
  background: #dff9ef;
}

.safety-card strong {
  color: #25bd68;
  font-size: 18px;
  text-align: center;
}

.safety-metrics {
  display: grid;
  gap: 12px;
  color: #6b6e88;
  font-size: 13px;
  text-align: center;
}

.safety-metrics i {
  height: 1px;
  background: #dedced;
}

.safety-body ul {
  display: grid;
  gap: 9px;
  margin: 0;
  padding: 14px 26px;
  border: 1px solid #eee9fa;
  border-radius: 10px;
  list-style: none;
}

.safety-body li {
  color: #676a82;
  font-size: 13px;
}

.safety-card button {
  color: #765ff2;
  border: 1px solid #cfc4ff;
  background: #fff;
}

.chat-row,
.post-row {
  position: relative;
  display: grid;
  align-items: center;
  border-bottom: 1px solid #eeedf5;
  cursor: pointer;
}

.chat-row:hover,
.post-row:hover {
  background: rgba(246, 242, 255, 0.72);
}

.chat-row {
  grid-template-columns: 46px 1fr 58px;
  gap: 14px;
  min-height: 54px;
}

.chat-row:last-child,
.post-row:last-child {
  border-bottom: 0;
}

.mini-avatar {
  display: grid;
  width: 38px;
  height: 38px;
  place-items: center;
  border-radius: 50%;
  color: #fff;
  font-weight: 900;
}

.mini-avatar.warm {
  background: #ffb681;
}

.mini-avatar.pink {
  background: #ff9db5;
}

.mini-avatar.green {
  background: #81d8a6;
}

.chat-row strong,
.post-row strong {
  display: block;
  color: #343753;
  font-size: 14px;
}

.chat-row strong span,
.post-row strong span,
.post-row strong small {
  margin-left: 8px;
  padding: 2px 7px;
  border-radius: 6px;
  color: #7460f4;
  background: #f1edff;
  font-size: 11px;
  font-weight: 900;
}

.chat-row p,
.post-row p {
  margin: 6px 0 0;
  color: #666985;
  font-size: 13px;
}

.chat-row time {
  color: #7d8098;
  font-size: 12px;
  text-align: right;
}

.chat-row em {
  position: absolute;
  right: 2px;
  top: 27px;
  display: grid;
  width: 18px;
  height: 18px;
  place-items: center;
  border-radius: 50%;
  color: #fff;
  background: #ef5b75;
  font-style: normal;
  font-size: 11px;
  font-weight: 900;
}

.post-row {
  grid-template-columns: 50px minmax(0, 1fr) 88px;
  gap: 14px;
  min-height: 78px;
}

.post-type {
  display: grid;
  width: 44px;
  height: 44px;
  place-items: center;
  border-radius: 10px;
  font-size: 22px;
  font-weight: 900;
}

.post-type.study {
  color: #715df4;
  background: #eee9ff;
}

.post-type.sport {
  color: #20ba63;
  background: #e9f9ef;
}

.post-meta {
  gap: 20px;
  margin-top: 8px;
  color: #7a7e96;
  font-size: 12px;
}

.post-meta :deep(svg) {
  width: 14px;
  height: 14px;
  flex: 0 0 14px;
}

.post-row em {
  color: #5d55d9;
  font-style: normal;
  font-size: 13px;
  font-weight: 900;
  text-align: right;
}

.badge-section {
  margin-top: 18px;
}

.badge-section h2 {
  margin-bottom: 12px;
  padding-left: 28px;
}

.badge-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 28px;
}

.badge-card {
  display: grid;
  grid-template-columns: 106px 1fr;
  align-items: center;
  gap: 18px;
  min-height: 108px;
  padding: 16px 24px;
  border: 1px solid #ebe7f8;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.74);
}

.badge-card.purple {
  border-color: #dbcfff;
  background: #f6f0ff;
}

.badge-card.orange {
  border-color: #f3d9b5;
  background: #fff5e8;
}

.badge-card.green {
  border-color: #ccead3;
  background: #eefaf2;
}

.badge-card.blue {
  border-color: #cddff8;
  background: #f0f6ff;
}

.badge-card.indigo {
  border-color: #d7d5ff;
  background: #f3f2ff;
}

.badge-card.locked {
  border-color: #dedde8;
  background: #f6f6f9;
}

.badge-image,
.badge-wall-image {
  object-fit: contain;
  transition: filter 0.2s ease, opacity 0.2s ease;
}

.badge-image {
  width: 86px;
  height: 76px;
  padding: 4px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.58);
}

.badge-card.locked .badge-image,
.badge-wall-card.locked .badge-wall-image {
  filter: grayscale(1);
  opacity: 0.42;
}

.badge-card strong {
  color: #25283f;
  font-size: 16px;
}

.badge-card p {
  margin: 10px 0;
  color: #575a78;
  font-size: 13px;
  font-weight: 800;
}

.badge-card span {
  color: #6f728b;
  font-size: 13px;
}

.more-badge {
  border-style: dashed;
  cursor: pointer;
}

.more-badge:hover {
  border-color: #cfc4ff;
  background: #fbf9ff;
}

.badge-more-icon {
  display: grid;
  width: 86px;
  height: 76px;
  place-items: center;
  border: 1px dashed rgba(117, 93, 244, 0.34);
  border-radius: 18px;
  color: #7460f4;
  background: rgba(255, 255, 255, 0.58);
}

.badge-more-icon :deep(svg) {
  width: 28px;
  height: 28px;
}

.badge-wall-dialog :deep(.el-dialog) {
  border-radius: 18px;
  background: #fff;
}

.badge-wall-dialog :deep(.el-dialog__title) {
  color: #151936;
  font-weight: 900;
}

.badge-wall-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.badge-wall-card {
  display: grid;
  grid-template-columns: 98px minmax(0, 1fr);
  align-items: center;
  gap: 18px;
  min-height: 122px;
  padding: 16px 18px;
  border: 1px solid #ebe7f8;
  border-radius: 12px;
  background: #fbf9ff;
}

.badge-wall-card.purple {
  border-color: #dbcfff;
  background: #f6f0ff;
}

.badge-wall-card.orange {
  border-color: #f3d9b5;
  background: #fff5e8;
}

.badge-wall-card.green {
  border-color: #ccead3;
  background: #eefaf2;
}

.badge-wall-card.blue {
  border-color: #cddff8;
  background: #f0f6ff;
}

.badge-wall-card.indigo {
  border-color: #d7d5ff;
  background: #f3f2ff;
}

.badge-wall-card.locked {
  border-color: #dedde8;
  background: #f6f6f9;
}

.badge-wall-image {
  width: 86px;
  height: 86px;
  padding: 6px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.62);
}

.badge-wall-card strong {
  color: #25283f;
  font-size: 17px;
}

.badge-wall-card p {
  margin: 10px 0;
  color: #575a78;
  font-size: 13px;
  font-weight: 800;
  line-height: 1.55;
}

.badge-wall-card span {
  color: #6f728b;
  font-size: 13px;
}

.avatar-uploader {
  line-height: 0;
}

.edit-avatar {
  width: 92px;
  height: 92px;
  border-radius: 50%;
}

.edit-avatar.empty {
  display: grid;
  place-items: center;
  border: 1px dashed #cfc4ff;
  color: #765ff2;
  background: #f8f5ff;
}

.cropper-panel {
  display: grid;
  gap: 18px;
  justify-items: center;
}

.crop-frame {
  position: relative;
  width: 280px;
  height: 280px;
  overflow: hidden;
  border-radius: 50%;
  background: #f3efff;
  cursor: grab;
  touch-action: none;
  user-select: none;
  box-shadow: inset 0 0 0 1px #ded7fb, 0 10px 28px rgba(112, 104, 151, 0.12);
}

.crop-frame:active {
  cursor: grabbing;
}

.crop-image {
  position: absolute;
  top: 50%;
  left: 50%;
  max-width: none;
  max-height: none;
  object-fit: contain;
  transform-origin: center;
  will-change: transform;
}

.crop-mask {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  pointer-events: none;
  box-shadow: inset 0 0 0 2px rgba(117, 93, 244, 0.46);
}

.crop-toolbar {
  display: grid;
  grid-template-columns: 48px minmax(0, 320px);
  align-items: center;
  gap: 14px;
  width: 100%;
  max-width: 390px;
  color: #626681;
  font-size: 14px;
  font-weight: 800;
}

.dialog-cancel,
.dialog-save {
  min-width: 80px;
  height: 34px;
  margin-left: 10px;
}

.dialog-cancel {
  border: 1px solid #dedced;
  border-radius: 999px;
  color: #626681;
  background: #fff;
  font-weight: 800;
  cursor: pointer;
}

.preference-dialog {
  display: grid;
  gap: 14px;
}

.preference-editor-row {
  display: grid;
  grid-template-columns: 36px minmax(116px, 1fr) minmax(220px, 260px);
  align-items: center;
  gap: 14px;
  padding: 12px 14px;
  border: 1px solid #eee9fa;
  border-radius: 10px;
  background: #fbf9ff;
}

.preference-editor-row strong {
  display: block;
  color: #25283f;
  font-size: 14px;
}

.preference-editor-row p,
.empty-note {
  margin: 4px 0 0;
  color: #777a94;
  font-size: 13px;
  font-weight: 800;
}

.preference-editor-row :deep(.el-rate) {
  justify-content: flex-end;
  height: auto;
}

.preference-editor-row :deep(.el-rate__text) {
  min-width: 38px;
  color: #7460f4;
  font-size: 12px;
  font-weight: 900;
}

@media (max-width: 1320px) {
  .profile-shell {
    grid-template-columns: 220px minmax(0, 1fr);
  }

  .profile-hero,
  .summary-grid,
  .middle-grid,
  .content-grid,
  .badge-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .hero-bio {
    grid-column: 1 / -1;
  }

  .safety-body {
    grid-template-columns: 74px 54px 1fr;
  }

  .safety-body ul,
  .safety-card button {
    grid-column: 1 / -1;
  }
}

@media (max-width: 900px) {
  .profile-page {
    padding: 0;
  }

  .profile-shell {
    display: block;
    border-radius: 0;
  }

  .sidebar {
    padding: 18px;
  }

  .nav-list {
    display: flex;
    overflow-x: auto;
  }

  .nav-item {
    flex: 0 0 auto;
  }

  .main-content {
    padding: 20px 14px 30px;
  }

  .top-actions {
    flex-wrap: wrap;
    justify-content: flex-start;
  }

  .search-box {
    width: 100%;
  }

  .profile-hero,
  .summary-grid,
  .middle-grid,
  .content-grid,
  .badge-grid {
    grid-template-columns: 1fr;
  }

  .profile-hero {
    padding: 22px;
  }

  .edit-profile {
    justify-self: start;
  }

  .badge-card {
    grid-template-columns: 82px 1fr;
  }
}
</style>
