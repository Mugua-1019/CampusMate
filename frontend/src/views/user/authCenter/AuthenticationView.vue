]<template>
  <main class="auth-page">
    <section class="auth-shell">
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
          <h2>完成认证，解锁更多校园权益</h2>
          <button class="btn-primary">去认证 <ArrowRight /></button>
        </section>
      </aside>

      <section class="main-content">
        <header class="topbar">
          <div class="welcome-copy">
            <h1>认证中心</h1>
            <p>完成认证，获取更多权益，结识更可信的校园伙伴。</p>
          </div>

          <div class="top-actions">
            <label class="search-box">
              <input v-model="keyword" placeholder="搜索需求、活动或用户" />
              <Search />
            </label>
            <button class="plain-icon" aria-label="我的聊天" @click="goChat"><Message /></button>
            <NotificationBell />
            <button class="profile-chip" aria-label="个人菜单">
              <img v-if="authSummary.avatarUrl" class="profile-avatar" :src="authSummary.avatarUrl" alt="头像" />
              <span v-else class="profile-avatar">{{ authSummary.avatarText }}</span>
              <span>{{ authSummary.statusText }}</span>
              <ArrowDown />
            </button>
          </div>
        </header>

        <section class="summary-card">
          <div class="student-block">
            <img v-if="authSummary.avatarUrl" class="student-avatar" :src="authSummary.avatarUrl" alt="头像" />
            <div v-else class="student-avatar" aria-hidden="true">{{ authSummary.avatarText }}</div>
            <div>
              <strong>{{ authSummary.nickname }}</strong>
              <em>{{ authSummary.statusText }}</em>
              <p>你好呀，欢迎来到星伴大家庭~</p>
            </div>
          </div>
          <div class="summary-divider"></div>
          <div class="level-block">
            <span>当前认证等级</span>
            <strong>{{ authSummary.levelTitle }} <CircleCheck /></strong>
            <p>{{ authSummary.levelHint }}</p>
          </div>
          <div class="summary-divider"></div>
          <div class="benefit-strip">
            <span>认证后可获得以下权益</span>
            <div>
              <article v-for="benefit in benefits" :key="benefit.label">
                <component :is="benefit.icon" />
                <em>{{ benefit.label }}</em>
              </article>
            </div>
          </div>
        </section>

        <section class="tab-row two-tabs">
          <button
            v-for="tab in tabs"
            :key="tab"
            :class="{ active: activeTab === tab }"
            @click="activeTab = tab"
          >
            {{ tab }}
          </button>
        </section>

        <section class="content-grid">
          <form v-if="activeTab === '校园认证'" class="auth-form" @submit.prevent="handleCampusSubmit">
            <div class="form-left">
              <label v-for="field in formFields" :key="field.label" class="form-row">
                <span>{{ field.index }}</span>
                <strong>{{ field.label }}</strong>
                <div>
                  <select v-if="field.type === 'select'" v-model="field.model">
                    <option value="">{{ field.placeholder }}</option>
                    <option v-for="option in field.options" :key="option" :value="option">{{ option }}</option>
                  </select>
                  <input v-else v-model="field.model" :placeholder="field.placeholder" />
                  <small>{{ field.hint }}</small>
                </div>
              </label>

              <div class="submit-row">
                <button type="submit" class="submit-button">提交认证</button>
                <p>提交后预计 1-3 个工作日内完成审核，请耐心等待~</p>
              </div>
            </div>

            <div class="upload-panel">
              <div class="upload-title">
                <strong>上传证明材料</strong>
                <InfoFilled />
                <span>请上传能证明你在校身份的有效材料</span>
              </div>
              <div class="upload-box" @click="triggerMaterialPicker" @dragover.prevent @drop.prevent="handleMaterialDrop">
                <UploadFilled />
                <strong>{{ uploadedMaterial.materialName || '点击或拖拽文件到此处上传' }}</strong>
                <p>{{ uploadedMaterial.materialUrl ? '材料已上传，可提交认证' : '支持 jpg、png、pdf 格式，大小不超过 5MB' }}</p>
                <input ref="materialInputRef" type="file" accept=".jpg,.jpeg,.png,.pdf" hidden @change="handleMaterialChange" />
              </div>
              <div class="sample-title">材料示例</div>
              <div class="sample-list">
                <article v-for="sample in samples" :key="sample.label">
                  <div :class="sample.tone"></div>
                  <span>{{ sample.label }}</span>
                </article>
              </div>
            </div>
          </form>

          <form v-else class="phone-auth-card" @submit.prevent="handlePhoneSubmit">
            <div class="phone-form">
              <label class="form-row">
                <span>1</span>
                <strong>手机号</strong>
                <div>
                  <input v-model="phoneNumber" placeholder="请输入手机号" />
                  <small>请填写你当前可接收短信的手机号</small>
                </div>
              </label>
              <label class="form-row code-row">
                <span>2</span>
                <strong>手机验证码</strong>
                <div>
                  <input v-model="phoneCode" placeholder="请输入手机验证码" />
                  <button type="button" @click="handleSendPhoneCode">发送验证码</button>
                  <small>验证码将发送至你的绑定手机号</small>
                </div>
              </label>
              <div class="submit-row">
                <button type="submit" class="submit-button">提交认证</button>
                <p>完成手机号认证后，可用于找回账号和接收重要通知。</p>
              </div>
            </div>
            <div class="phone-side">
              <div class="phone-visual">
                <Message />
              </div>
              <h2>手机号认证</h2>
              <p>用于重要通知、账号找回和异常登录提醒。认证后手机号不会在个人主页公开展示。</p>
              <div>
                <span>短信验证</span>
                <span>隐私保护</span>
              </div>
            </div>
          </form>

          <aside class="rights-card">
            <div class="rights-copy">
              <h2>认证权益</h2>
              <p>完成校园认证后，你将获得以下专属权益</p>
            </div>
            <div class="rights-list">
              <article v-for="item in rights" :key="item.title">
                <component :is="item.icon" />
                <div>
                  <strong>{{ item.title }}</strong>
                  <span>{{ item.desc }}</span>
                </div>
              </article>
            </div>
            <img :src="rightsImage" alt="" />
          </aside>
        </section>

        <section class="record-card">
          <div class="record-title">
            <h2>审核记录</h2>
            <button>查看全部记录 <ArrowRight /></button>
          </div>
          <table>
            <thead>
              <tr>
                <th>提交时间</th>
                <th>认证类型</th>
                <th>提交内容</th>
                <th>审核状态</th>
                <th>审核反馈</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="record in displayRecords" :key="record.recordId || record.time">
                <td>{{ record.time }}</td>
                <td>{{ record.type }}</td>
                <td>{{ record.content }}</td>
                <td><span class="status" :class="record.statusClass">{{ record.status }}</span></td>
                <td>{{ record.feedback }}</td>
                <td><button>{{ record.action }}</button></td>
              </tr>
            </tbody>
          </table>
        </section>
      </section>
    </section>
  </main>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowDown,
  ArrowRight,
  Bell,
  ChatDotRound,
  CircleCheck,
  DataLine,
  Document,
  Flag,
  HomeFilled,
  InfoFilled,
  Lock,
  Message,
  Promotion,
  Search,
  Select,
  StarFilled,
  UploadFilled,
  User
} from '@element-plus/icons-vue'
import logoImage from '../../../assets/images/logo-star-mascot.png'
import verifyImage from '../../../assets/images/renzheng.png'
import rightsImage from '../../../assets/images/quanyi-clean.png'
import { fetchAuthCenter, sendPhoneAuthCode, submitCampusAuth, submitPhoneAuth, uploadAuthMaterial } from '../../../api/authCenter'
import { getCurrentUser } from '../../../utils/currentUser'
import NotificationBell from '../../../components/NotificationBell.vue'

const router = useRouter()
const keyword = ref('')
const activeTab = ref('校园认证')
const phoneNumber = ref('')
const phoneCode = ref('')
const authCenterData = ref(null)
const submittingCampus = ref(false)
const submittingPhone = ref(false)
const uploadingMaterial = ref(false)
const materialInputRef = ref(null)
const uploadedMaterial = reactive({
  materialName: '',
  materialUrl: ''
})

const navItems = [
  { label: '广场首页', icon: HomeFilled, route: '/home' },
  { label: '发布需求', icon: Promotion, route: '/publish' },
  { label: '我的聊天', icon: Message, route: '/chat' },
  { label: '我的匹配', icon: StarFilled, route: '/my-match' },
  { label: '认证中心', icon: Lock, active: true },
  { label: '安全反馈', icon: Flag, route: '/safety-feedback' },
  { label: '个人中心', icon: User, route: '/profile' }
]

const tabs = ['校园认证', '手机号认证']

const iconMap = {
  Document,
  ChatDotRound,
  Select,
  Lock,
  DataLine,
  document: Document,
  chat: ChatDotRound,
  select: Select,
  lock: Lock,
  data: DataLine
}

const benefits = computed(() => (authCenterData.value?.benefits || []).map((item) => ({
  label: item.label,
  icon: iconMap[item.icon] || Document
})))

const formFields = reactive([
  {
    index: 1,
    label: '学校选择',
    type: 'select',
    model: '',
    placeholder: '请选择你的学校',
    hint: '请输入学校全称进行搜索',
    options: ['北京大学', '华东师范大学', '复旦大学', '同济大学']
  },
  {
    index: 2,
    label: '学号',
    model: '',
    placeholder: '请输入学号',
    hint: '请填写你的完整学号'
  },
  {
    index: 3,
    label: '姓名',
    model: '',
    placeholder: '请输入姓名',
    hint: '请填写你的真实姓名'
  },
  {
    index: 4,
    label: '身份信息',
    type: 'select',
    model: '',
    placeholder: '请选择身份',
    hint: '请选择你的在校身份',
    options: ['本科生', '研究生', '博士生', '教职工']
  }
])

const samples = computed(() => authCenterData.value?.samples || [])

const rights = computed(() => (authCenterData.value?.rights || []).map((item) => ({
  title: item.title,
  desc: item.description,
  icon: iconMap[item.icon] || Document
})))

const displayRecords = computed(() => authCenterData.value?.records || [])
const authSummary = computed(() => authCenterData.value?.summary || {
  avatarUrl: getCurrentUser()?.avatarUrl || '',
  avatarText: getCurrentUser()?.nickname?.slice(0, 1) || '星',
  nickname: getCurrentUser()?.nickname || '小星同学',
  statusText: '待认证',
  levelTitle: '未认证',
  levelHint: '完成认证后可升级为校园认证用户'
})

const requireCurrentUser = () => {
  const user = getCurrentUser()
  if (!user?.userId) {
    ElMessage.error('请先登录账号')
    router.push('/')
    return null
  }
  return user
}

const loadAuthCenter = async () => {
  if (!requireCurrentUser()) {
    return
  }
  try {
    const response = await fetchAuthCenter()
    authCenterData.value = response.data.data
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '认证中心数据加载失败')
  }
}

const handleCampusSubmit = async () => {
  if (submittingCampus.value || !requireCurrentUser()) {
    return
  }
  const [school, studentNo, realName, identityType] = formFields.map((field) => field.model.trim())
  if (!school || !studentNo || !realName || !identityType) {
    ElMessage.error('请完整填写校园认证信息')
    return
  }
  if (!uploadedMaterial.materialUrl) {
    ElMessage.error('请先上传证明材料')
    return
  }
  submittingCampus.value = true
  try {
    await submitCampusAuth({
      school,
      studentNo,
      realName,
      identityType,
      materialName: uploadedMaterial.materialName,
      materialUrl: uploadedMaterial.materialUrl
    })
    ElMessage.success('校园认证已提交，请等待审核')
    await loadAuthCenter()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '校园认证提交失败')
  } finally {
    submittingCampus.value = false
  }
}

const triggerMaterialPicker = () => {
  materialInputRef.value?.click()
}

const handleMaterialChange = async (event) => {
  const file = event.target.files?.[0]
  if (file) {
    await uploadMaterial(file)
  }
  event.target.value = ''
}

const handleMaterialDrop = async (event) => {
  const file = event.dataTransfer.files?.[0]
  if (file) {
    await uploadMaterial(file)
  }
}

const uploadMaterial = async (file) => {
  if (uploadingMaterial.value || !requireCurrentUser()) {
    return
  }
  uploadingMaterial.value = true
  try {
    const response = await uploadAuthMaterial(file)
    uploadedMaterial.materialName = response.data.data.materialName
    uploadedMaterial.materialUrl = response.data.data.materialUrl
    ElMessage.success('证明材料上传成功')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '证明材料上传失败')
  } finally {
    uploadingMaterial.value = false
  }
}

const handleSendPhoneCode = async () => {
  if (!requireCurrentUser()) {
    return
  }
  const phone = phoneNumber.value.trim()
  if (!phone) {
    ElMessage.error('请输入手机号')
    return
  }
  try {
    const response = await sendPhoneAuthCode({ phone })
    phoneCode.value = response.data.data.code
    ElMessage.success('模拟验证码已生成')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '验证码发送失败')
  }
}

const handlePhoneSubmit = async () => {
  if (submittingPhone.value || !requireCurrentUser()) {
    return
  }
  const phone = phoneNumber.value.trim()
  const code = phoneCode.value.trim()
  if (!phone || !code) {
    ElMessage.error('请输入手机号和验证码')
    return
  }
  submittingPhone.value = true
  try {
    await submitPhoneAuth({ phone, code })
    ElMessage.success('手机号认证成功')
    await loadAuthCenter()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '手机号认证失败')
  } finally {
    submittingPhone.value = false
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

onMounted(loadAuthCenter)
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  padding: 14px 20px;
  background:
    radial-gradient(circle at 10% 0%, rgba(255, 215, 238, 0.58), transparent 28%),
    radial-gradient(circle at 92% 8%, rgba(226, 214, 255, 0.78), transparent 34%),
    linear-gradient(135deg, #fbf8ff 0%, #f4f7ff 48%, #fff9fc 100%);
}

.auth-shell {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
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
  padding: 34px 0 28px 16px;
  border-radius: 28px 0 0 28px;
  background: linear-gradient(180deg, rgba(253, 250, 255, 0.96), rgba(247, 244, 255, 0.86));
  box-shadow: inset -1px 0 0 rgba(224, 216, 246, 0.54);
}

.brand,
.top-actions,
.student-block,
.level-block strong,
.record-title,
.upload-title,
.submit-row,
.profile-chip {
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

.brand strong,
.brand span {
  display: block;
  color: #6853f1;
  font-weight: 900;
}

.brand strong {
  font-size: 28px;
  line-height: 1;
}

.brand span {
  font-size: 15px;
  font-style: italic;
}

.nav-list {
  display: grid;
  gap: 8px;
  padding-left: 14px;
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
  line-height: 1;
}

.nav-item :deep(svg) {
  width: 22px;
  height: 22px;
  flex: 0 0 22px;
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
  margin: 0 0 14px;
  color: #20233f;
  font-size: 15px;
  line-height: 1.42;
  font-weight: 800;
}

.verify-card .btn-primary {
  width: 100%;
  height: 44px;
  gap: 8px;
}

.verify-card :deep(svg) {
  width: 16px;
}

.main-content {
  min-width: 0;
  padding-top: 18px;
}

.topbar {
  min-height: 64px;
  margin-bottom: 12px;
}

.welcome-copy h1 {
  margin: 0 0 9px;
  color: #12121d;
  font-size: 30px;
  font-weight: 900;
  line-height: 1.1;
}

.welcome-copy p {
  margin: 0;
  color: #818096;
  font-size: 14px;
  font-weight: 600;
  line-height: 1.45;
}

.top-actions {
  position: absolute;
  top: 38px;
  right: 54px;
  gap: 18px;
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
  font-size: 14px;
  font-weight: 500;
}

.search-box :deep(svg) {
  width: 23px;
  height: 23px;
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

.plain-icon :deep(svg) {
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
  border-radius: 999px;
  color: #fff;
  background: #ef5b75;
  font-size: 12px;
  font-weight: 900;
}

.profile-chip {
  display: grid;
  grid-template-columns: 50px 58px 18px;
  gap: 10px;
  color: #b9802e;
  background: transparent;
  font-size: 14px;
  font-weight: 800;
  line-height: 1.12;
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

.summary-card {
  display: grid;
  grid-template-columns: 380px 1px 300px 1px minmax(420px, 1fr);
  column-gap: 38px;
  align-items: center;
  min-height: 116px;
  margin-top: 18px;
  padding: 18px 24px;
  border: 1px solid #e9e0ff;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.7);
  box-shadow: 0 11px 26px rgba(112, 104, 151, 0.08);
}

.student-block {
  gap: 18px;
}

.student-avatar {
  display: grid;
  width: 82px;
  height: 82px;
  place-items: center;
  border-radius: 50%;
  color: #5f43de;
  background:
    radial-gradient(circle at 32% 28%, rgba(255, 255, 255, 0.86), transparent 24%),
    linear-gradient(145deg, #ffe0e8 0%, #d9ccff 54%, #a58bff 100%);
  box-shadow: inset 0 0 0 5px rgba(255, 255, 255, 0.8), 0 10px 24px rgba(117, 93, 244, 0.18);
  font-size: 30px;
  font-weight: 900;
}

.student-block strong {
  color: #11152f;
  font-size: 22px;
  font-weight: 900;
  line-height: 1.18;
}

.student-block em {
  margin-left: 12px;
  padding: 4px 10px;
  border-radius: 999px;
  color: #745cf2;
  background: #f0ebff;
  font-style: normal;
  font-size: 12px;
  font-weight: 800;
  line-height: 1;
}

.student-block p,
.level-block p,
.benefit-strip span,
.rights-copy p {
  margin: 8px 0 0;
  color: #77798d;
  font-size: 13px;
  font-weight: 600;
  line-height: 1.45;
}

.summary-divider {
  width: 1px;
  height: 78px;
  background: #ebe5f8;
}

.level-block span {
  color: #686a82;
  font-size: 13px;
  font-weight: 700;
  line-height: 1.2;
}

.level-block strong {
  gap: 8px;
  margin-top: 10px;
  color: #11152f;
  font-size: 25px;
  font-weight: 900;
  line-height: 1.05;
}

.level-block :deep(svg) {
  width: 20px;
  color: #cfd1dd;
}

.benefit-strip > div {
  display: grid;
  grid-template-columns: repeat(5, minmax(72px, 1fr));
  gap: 16px;
  margin-top: 13px;
}

.benefit-strip article {
  display: grid;
  justify-items: center;
  gap: 9px;
  color: #565a76;
  font-size: 12px;
  font-weight: 700;
  line-height: 1.2;
}

.benefit-strip article :deep(svg) {
  width: 34px;
  height: 34px;
  padding: 9px;
  border-radius: 50%;
  color: #755df4;
  background: #f0ebff;
}

.benefit-strip em {
  font-style: normal;
}

.tab-row {
  display: flex;
  gap: 24px;
  margin: 20px 0 0;
}

.tab-row.two-tabs {
  width: 260px;
  justify-content: space-between;
}

.tab-row button {
  position: relative;
  min-width: 120px;
  height: 42px;
  color: #686a82;
  background: transparent;
  font-size: 16px;
  font-weight: 800;
  line-height: 1;
}

.tab-row button.active {
  color: #755df4;
}

.tab-row button.active::before {
  position: absolute;
  inset: auto 0 -5px;
  height: 5px;
  border-radius: 999px;
  background: linear-gradient(90deg, #755df4, #d69bff);
  content: "";
}

.content-grid {
  display: grid;
  grid-template-columns: minmax(680px, 1fr) 460px;
  gap: 20px;
  margin-top: 10px;
}

.auth-form,
.phone-auth-card,
.rights-card,
.record-card {
  border: 1px solid #e9e0ff;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.72);
  box-shadow: 0 11px 26px rgba(112, 104, 151, 0.08);
}

.auth-form {
  display: grid;
  grid-template-columns: minmax(350px, 1fr) minmax(320px, 430px);
  gap: 32px;
  min-height: 424px;
  padding: 22px 28px 20px 18px;
}

.phone-auth-card {
  display: grid;
  grid-template-columns: minmax(430px, 0.95fr) minmax(260px, 0.55fr);
  align-items: stretch;
  gap: 34px;
  margin-top: 10px;
  padding: 26px 28px;
  min-height: 424px;
}

.phone-form {
  display: grid;
  align-content: start;
  gap: 22px;
}

.phone-form .submit-row {
  margin-top: 84px;
}

.phone-side {
  display: grid;
  align-content: center;
  justify-items: center;
  padding: 28px 26px;
  border: 1px solid rgba(222, 215, 251, 0.72);
  border-radius: 14px;
  background:
    radial-gradient(circle at 50% 18%, rgba(117, 93, 244, 0.18), transparent 34%),
    linear-gradient(180deg, rgba(250, 248, 255, 0.9), rgba(255, 255, 255, 0.62));
  text-align: center;
}

.phone-visual {
  display: grid;
  width: 84px;
  height: 84px;
  place-items: center;
  border-radius: 26px;
  color: #755df4;
  background:
    linear-gradient(145deg, rgba(255, 255, 255, 0.92), rgba(239, 233, 255, 0.72)),
    #f1ecff;
  box-shadow: inset 0 0 0 1px rgba(117, 93, 244, 0.12), 0 16px 32px rgba(117, 93, 244, 0.16);
}

.phone-visual :deep(svg) {
  width: 40px;
  height: 40px;
}

.phone-side h2 {
  margin: 18px 0 9px;
  color: #151936;
  font-size: 20px;
  font-weight: 900;
  line-height: 1.2;
}

.phone-side p {
  max-width: 280px;
  margin: 0;
  color: #77798d;
  font-size: 13px;
  font-weight: 600;
  line-height: 1.7;
}

.phone-side div:last-child {
  display: flex;
  gap: 10px;
  margin-top: 18px;
}

.phone-side span {
  min-height: 28px;
  padding: 6px 12px;
  border-radius: 999px;
  color: #755df4;
  background: rgba(255, 255, 255, 0.78);
  font-size: 12px;
  font-weight: 800;
}

.form-left {
  display: grid;
  align-content: start;
  gap: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 20px 104px minmax(0, 1fr);
  align-items: start;
  gap: 12px;
}

.form-row > span {
  color: #66708f;
  font-size: 13px;
  font-weight: 800;
  line-height: 34px;
  text-align: center;
}

.form-row strong {
  color: #151936;
  font-size: 14px;
  font-weight: 800;
  line-height: 34px;
}

.form-row div {
  display: grid;
  gap: 8px;
}

.form-row input,
.form-row select {
  width: 100%;
  height: 34px;
  padding: 0 14px;
  border: 1px solid #dedced;
  border-radius: 6px;
  color: #4d5068;
  background: #fff;
  outline: 0;
  font-size: 14px;
  font-weight: 500;
}

.form-row small {
  color: #9a9dae;
  font-size: 12px;
  font-weight: 500;
  line-height: 1.25;
}

.code-row div {
  grid-template-columns: minmax(0, 1fr) 96px;
}

.code-row small {
  grid-column: 1 / -1;
}

.code-row button {
  height: 34px;
  border: 1px solid #d8cef9;
  border-radius: 6px;
  color: #755df4;
  background: #faf8ff;
  font-size: 12px;
  font-weight: 800;
}

.submit-row {
  gap: 26px;
  margin-top: 34px;
}

.submit-button {
  width: 282px;
  height: 38px;
  border-radius: 999px;
  color: #fff;
  background: linear-gradient(105deg, #755df4, #c565d8);
  box-shadow: 0 10px 22px rgba(117, 93, 244, 0.24);
  font-size: 16px;
  font-weight: 800;
}

.submit-row p {
  margin: 0;
  color: #81849a;
  font-size: 13px;
  font-weight: 700;
  line-height: 1.45;
}

.upload-panel {
  display: grid;
  align-content: start;
  gap: 16px;
}

.upload-title {
  flex-wrap: wrap;
  gap: 6px;
}

.upload-title strong,
.sample-title {
  color: #151936;
  font-size: 14px;
  font-weight: 800;
  line-height: 1.25;
}

.upload-title span {
  flex-basis: 100%;
  color: #77798d;
  font-size: 12px;
  font-weight: 600;
  line-height: 1.3;
}

.upload-title :deep(svg) {
  width: 14px;
  color: #8d8fa4;
}

.upload-box {
  display: grid;
  height: 126px;
  place-items: center;
  align-content: center;
  gap: 9px;
  border: 2px dashed #b7a8ff;
  border-radius: 10px;
  background: #fdfcff;
  color: #676a82;
  cursor: pointer;
}

.upload-box :deep(svg) {
  width: 38px;
  height: 38px;
  padding: 9px;
  border-radius: 50%;
  color: #fff;
  background: linear-gradient(135deg, #755df4, #9b83ff);
}

.upload-box strong {
  font-size: 13px;
  font-weight: 800;
  line-height: 1.2;
}

.upload-box p {
  margin: 0;
  color: #9a9dae;
  font-size: 12px;
  font-weight: 500;
}

.sample-list {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.sample-list article {
  display: grid;
  justify-items: center;
  gap: 7px;
  color: #555976;
  font-size: 12px;
  font-weight: 700;
  line-height: 1.2;
}

.sample-list div {
  width: 76px;
  height: 54px;
  border: 1px solid #dedced;
  border-radius: 8px;
}

.sample-id {
  background: linear-gradient(135deg, #eef5ff 0 50%, #f7fbff 50%);
}

.sample-card {
  background: linear-gradient(135deg, #314a9b, #84a1ff);
}

.sample-paper {
  background: linear-gradient(135deg, #fff8e8, #f1dcae);
}

.sample-table {
  background:
    repeating-linear-gradient(0deg, transparent 0 12px, rgba(117, 93, 244, 0.18) 12px 13px),
    repeating-linear-gradient(90deg, #f4f8ff 0 18px, #fff 18px 36px);
}

.rights-card {
  position: relative;
  min-height: 424px;
  overflow: hidden;
  padding: 24px 30px 0;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.8) 0 58%, rgba(248, 244, 255, 0.96) 100%),
    #fff;
}

.rights-copy h2,
.record-title h2 {
  margin: 0;
  color: #151936;
  font-size: 20px;
  font-weight: 900;
  line-height: 1.2;
}

.rights-list {
  display: grid;
  gap: 9px;
  margin-top: 18px;
  position: relative;
  z-index: 1;
}

.rights-list article {
  display: grid;
  grid-template-columns: 42px 1fr;
  align-items: center;
  gap: 12px;
  min-height: 52px;
  padding: 8px 14px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.54);
}

.rights-list :deep(svg) {
  width: 34px;
  height: 34px;
  padding: 9px;
  border-radius: 50%;
  color: #755df4;
  background: #f0ebff;
}

.rights-list strong,
.rights-list span {
  display: block;
}

.rights-list strong {
  color: #151936;
  font-size: 14px;
  font-weight: 800;
  line-height: 1.2;
}

.rights-list span {
  margin-top: 3px;
  color: #77798d;
  font-size: 12px;
  font-weight: 500;
  line-height: 1.35;
}

.rights-card > img {
  position: absolute;
  right: -18px;
  bottom: -18px;
  width: calc(100% + 36px);
  height: 178px;
  object-fit: contain;
  object-position: bottom center;
}

.record-card {
  margin-top: 18px;
  padding: 16px 20px 20px;
}

.record-title {
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 12px;
}

.record-title button {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  flex: 0 0 auto;
  color: #755df4;
  background: transparent;
  font-size: 13px;
  font-weight: 800;
  line-height: 1;
  white-space: nowrap;
}

.record-title button :deep(svg) {
  width: 18px;
  height: 18px;
  flex: 0 0 18px;
}

table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  overflow: hidden;
  border: 1px solid #eeeaf6;
  border-radius: 8px;
  color: #626681;
  font-size: 13px;
  line-height: 1.25;
}

th,
td {
  height: 43px;
  padding: 0 18px;
  border-bottom: 1px solid #eeeaf6;
  text-align: left;
}

th {
  color: #626681;
  background: #faf8ff;
  font-weight: 800;
}

tbody tr:last-child td {
  border-bottom: 0;
}

td button {
  color: #755df4;
  background: transparent;
  font-size: 13px;
  font-weight: 800;
}

.status {
  display: inline-flex;
  align-items: center;
  min-height: 22px;
  padding: 0 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
  line-height: 1;
}

.status.pending {
  color: #f48a1f;
  background: #fff2df;
}

.status.passed {
  color: #2bbf6b;
  background: #e9f9ef;
}

@media (max-width: 1320px) {
  .summary-card,
  .content-grid,
  .auth-form {
    grid-template-columns: 1fr;
  }

  .summary-divider {
    display: none;
  }

  .summary-card {
    gap: 20px;
  }
}

@media (max-width: 900px) {
  .auth-page {
    padding: 0;
  }

  .auth-shell {
    display: block;
    padding: 14px;
    border-radius: 0;
  }

  .sidebar {
    margin: -14px -14px 0;
    padding: 18px;
    border-radius: 0;
  }

  .nav-list {
    display: flex;
    overflow-x: auto;
    padding-left: 0;
  }

  .nav-item {
    flex: 0 0 auto;
  }

  .verify-card {
    display: none;
  }

  .top-actions {
    position: static;
    flex-wrap: wrap;
    margin-top: 20px;
  }

  .search-box {
    width: 100%;
  }

  .benefit-strip > div,
  .sample-list {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .form-row,
  .code-row div {
    grid-template-columns: 1fr;
  }

  .submit-row {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
