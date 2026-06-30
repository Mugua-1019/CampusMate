<template>
  <main class="admin-page">
    <aside class="admin-sidebar">
      <div class="brand">
        <strong>星伴后台</strong>
        <span>CampusMate Admin</span>
      </div>
      <nav>
        <button
          v-for="item in navItems"
          :key="item.key"
          :class="{ active: activeSection === item.key }"
          type="button"
          @click="setActiveSection(item.key)"
        >
          {{ item.label }}
        </button>
      </nav>
    </aside>

    <section class="admin-main">
      <header class="admin-header">
        <div>
          <h1>{{ currentTitle }}</h1>
          <p>{{ currentAdmin?.displayName || '管理员' }}，欢迎回来</p>
        </div>
        <button type="button" @click="logout">退出</button>
      </header>

      <template v-if="activeSection === 'dashboard'">
        <section class="stats-grid">
          <article v-for="item in stats" :key="item.label" class="stat-card">
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
          </article>
        </section>

        <section class="work-panel">
          <div>
            <h2>待处理事项</h2>
            <p>需要优先关注审核和举报，避免违规内容继续展示。</p>
          </div>
          <ul>
            <li>待审核内容：{{ dashboard.pendingPosts ?? 0 }}</li>
            <li>待处理举报：{{ dashboard.pendingReports ?? 0 }}</li>
            <li>待认证审核：{{ dashboard.pendingAuthRecords ?? 0 }}</li>
          </ul>
        </section>
      </template>

      <section v-else class="table-panel">
        <div class="panel-heading">
          <div>
            <h2>{{ currentTitle }}</h2>
            <p>{{ currentDescription }}</p>
          </div>
          <button v-if="activeSection !== 'logs'" type="button" @click="reloadCurrentSection">刷新</button>
        </div>

        <form v-if="activeSection === 'announcements'" class="admin-form" @submit.prevent="submitAnnouncement">
          <input v-model="announcementForm.title" type="text" placeholder="公告标题" />
          <select v-model="announcementForm.status">
            <option value="draft">草稿</option>
            <option value="published">发布</option>
            <option value="offline">下线</option>
          </select>
          <textarea v-model="announcementForm.content" placeholder="公告内容"></textarea>
          <div class="form-actions">
            <button type="submit">{{ editingAnnouncementId ? '保存公告' : '新增公告' }}</button>
            <button v-if="editingAnnouncementId" class="secondary" type="button" @click="resetAnnouncementForm">取消编辑</button>
          </div>
        </form>

        <form v-if="activeSection === 'categories'" class="admin-form inline-form" @submit.prevent="submitCategory">
          <input v-model="categoryForm.plazaKey" type="text" placeholder="广场标识，如 match / vent" />
          <input v-model="categoryForm.categoryName" type="text" placeholder="分类名称" />
          <input v-model.number="categoryForm.sortWeight" type="number" placeholder="排序值" />
          <div class="form-actions">
            <button type="submit">{{ editingCategoryId ? '保存分类' : '新增分类' }}</button>
            <button v-if="editingCategoryId" class="secondary" type="button" @click="resetCategoryForm">取消编辑</button>
          </div>
        </form>

        <form v-if="activeSection === 'messages'" class="admin-form" @submit.prevent="submitSystemMessage">
          <select v-model="messageForm.targetType">
            <option value="all">全部用户</option>
            <option value="user">指定用户</option>
          </select>
          <input v-if="messageForm.targetType === 'user'" v-model="messageForm.targetUserId" type="number" placeholder="用户ID" />
          <input v-model="messageForm.title" type="text" placeholder="消息标题" />
          <textarea v-model="messageForm.content" placeholder="消息内容"></textarea>
          <div class="form-actions">
            <button type="submit">发送系统消息</button>
          </div>
        </form>

        <div v-if="loading" class="empty-state">加载中...</div>
        <div v-else-if="currentRecords.length === 0 && activeSection !== 'messages'" class="empty-state">暂无数据</div>
        <table v-else-if="activeSection !== 'messages'">
          <thead>
            <tr>
              <th v-for="column in currentColumns" :key="column.key">{{ column.label }}</th>
              <th v-if="hasActions">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="record in currentRecords" :key="recordKey(record)">
              <td v-for="column in currentColumns" :key="column.key" :title="formatCell(record, column.key)">
                {{ formatCell(record, column.key) }}
              </td>
              <td v-if="hasActions">
                <div class="row-actions">
                  <template v-if="activeSection === 'users'">
                    <button type="button" @click="toggleUser(record)">{{ truthy(record.enabled) ? '禁用' : '启用' }}</button>
                  </template>
                  <template v-else-if="activeSection === 'posts'">
                    <button v-if="record.reviewStatus !== 'visible'" type="button" @click="approvePost(record)">通过</button>
                    <button v-if="record.reviewStatus !== 'rejected'" type="button" @click="rejectPost(record)">拒绝</button>
                    <button type="button" @click="togglePostDeleted(record)">{{ truthy(record.deleted) ? '恢复' : '删除' }}</button>
                  </template>
                  <template v-else-if="activeSection === 'reports'">
                    <button type="button" @click="handleReport(record, 'resolved')">已处理</button>
                    <button type="button" @click="handleReport(record, 'ignored')">忽略</button>
                  </template>
                  <template v-else-if="activeSection === 'authRecords'">
                    <button v-if="record.status === 'pending'" type="button" @click="approveAuthRecord(record)">通过</button>
                    <button v-if="record.status === 'pending'" type="button" @click="rejectAuthRecord(record)">驳回</button>
                    <a v-if="record.materialUrl" :href="record.materialUrl" target="_blank" rel="noreferrer">材料</a>
                  </template>
                  <template v-else-if="activeSection === 'announcements'">
                    <button type="button" @click="editAnnouncement(record)">编辑</button>
                    <button type="button" @click="removeAnnouncement(record)">删除</button>
                  </template>
                  <template v-else-if="activeSection === 'categories'">
                    <button type="button" @click="editCategory(record)">编辑</button>
                    <button type="button" @click="toggleCategory(record)">{{ truthy(record.enabled) ? '停用' : '启用' }}</button>
                  </template>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </section>
    </section>
  </main>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  approveAdminAuthRecord,
  approveAdminPost,
  createAdminAnnouncement,
  createAdminCategory,
  createAdminSystemMessage,
  deleteAdminAnnouncement,
  deleteAdminPost,
  disableAdminCategory,
  disableAdminUser,
  enableAdminCategory,
  enableAdminUser,
  fetchAdminAnnouncements,
  fetchAdminAuthRecords,
  fetchAdminCategories,
  fetchAdminDashboard,
  fetchAdminLogs,
  fetchAdminPosts,
  fetchAdminReports,
  fetchAdminUsers,
  handleAdminReport,
  rejectAdminAuthRecord,
  rejectAdminPost,
  restoreAdminPost,
  updateAdminAnnouncement,
  updateAdminCategory
} from '../../api/admin'
import { clearCurrentAdmin, getCurrentAdmin } from '../../utils/currentUser'

const router = useRouter()
const currentAdmin = ref(getCurrentAdmin())
const activeSection = ref('dashboard')
const dashboard = ref({})
const users = ref([])
const posts = ref([])
const reports = ref([])
const authRecords = ref([])
const announcements = ref([])
const categories = ref([])
const logs = ref([])
const loading = ref(false)
const editingAnnouncementId = ref(null)
const editingCategoryId = ref(null)

const announcementForm = reactive({
  title: '',
  content: '',
  status: 'draft'
})

const categoryForm = reactive({
  plazaKey: '',
  categoryName: '',
  sortWeight: 100
})

const messageForm = reactive({
  targetType: 'all',
  targetUserId: '',
  title: '',
  content: ''
})

const navItems = [
  { key: 'dashboard', label: '总览' },
  { key: 'users', label: '用户' },
  { key: 'authRecords', label: '认证' },
  { key: 'posts', label: '内容' },
  { key: 'reports', label: '举报' },
  { key: 'announcements', label: '公告' },
  { key: 'categories', label: '分类' },
  { key: 'messages', label: '系统消息' },
  { key: 'logs', label: '日志' }
]

const tableConfig = {
  users: {
    title: '用户管理',
    description: '查看用户账号、认证和启用状态。',
    columns: [
      { key: 'userId', label: 'ID' },
      { key: 'account', label: '账号' },
      { key: 'nickname', label: '昵称' },
      { key: 'enabled', label: '状态' },
      { key: 'verifyStatus', label: '认证' }
    ]
  },
  posts: {
    title: '内容管理',
    description: '审核、拒绝、删除或恢复用户发布的内容。',
    columns: [
      { key: 'id', label: 'ID' },
      { key: 'plaza', label: '广场' },
      { key: 'title', label: '标题' },
      { key: 'reviewStatus', label: '审核' },
      { key: 'deleted', label: '状态' }
    ]
  },
  reports: {
    title: '举报管理',
    description: '处理用户提交的内容举报。',
    columns: [
      { key: 'reportId', label: 'ID' },
      { key: 'postId', label: '内容ID' },
      { key: 'reason', label: '原因' },
      { key: 'status', label: '状态' },
      { key: 'postTitle', label: '内容标题' }
    ]
  },
  authRecords: {
    title: '认证审核',
    description: '审核用户提交的校园认证材料。',
    columns: [
      { key: 'recordId', label: 'ID' },
      { key: 'userId', label: '用户ID' },
      { key: 'nickname', label: '昵称' },
      { key: 'school', label: '学校' },
      { key: 'studentNo', label: '学号' },
      { key: 'realName', label: '真实姓名' },
      { key: 'status', label: '状态' }
    ]
  },
  announcements: {
    title: '公告管理',
    description: '维护后台公告，支持草稿、发布和下线。',
    columns: [
      { key: 'announcementId', label: 'ID' },
      { key: 'title', label: '标题' },
      { key: 'status', label: '状态' },
      { key: 'updatedAt', label: '更新时间' }
    ]
  },
  categories: {
    title: '分类管理',
    description: '维护广场分类和展示顺序。',
    columns: [
      { key: 'id', label: 'ID' },
      { key: 'plazaKey', label: '广场' },
      { key: 'categoryName', label: '分类' },
      { key: 'enabled', label: '状态' },
      { key: 'sortWeight', label: '排序' }
    ]
  },
  messages: {
    title: '系统消息',
    description: '给全部用户或指定用户发送系统消息。',
    columns: []
  },
  logs: {
    title: '操作日志',
    description: '查看管理员近期操作记录。',
    columns: [
      { key: 'logId', label: 'ID' },
      { key: 'username', label: '管理员' },
      { key: 'action', label: '动作' },
      { key: 'targetType', label: '对象' },
      { key: 'createdAt', label: '时间' }
    ]
  }
}

const stats = computed(() => [
  { label: '用户总数', value: dashboard.value.totalUsers ?? 0 },
  { label: '今日新增', value: dashboard.value.todayUsers ?? 0 },
  { label: '内容总数', value: dashboard.value.totalPosts ?? 0 },
  { label: '待审核', value: dashboard.value.pendingPosts ?? 0 },
  { label: '待处理举报', value: dashboard.value.pendingReports ?? 0 },
  { label: '待认证', value: dashboard.value.pendingAuthRecords ?? 0 }
])

const currentConfig = computed(() => tableConfig[activeSection.value] || { title: '管理员工作台', description: '', columns: [] })
const currentTitle = computed(() => activeSection.value === 'dashboard' ? '管理员工作台' : currentConfig.value.title)
const currentDescription = computed(() => currentConfig.value.description)
const currentColumns = computed(() => currentConfig.value.columns)
const currentRecords = computed(() => {
  const records = {
    users: users.value,
    posts: posts.value,
    reports: reports.value,
    authRecords: authRecords.value,
    announcements: announcements.value,
    categories: categories.value,
    logs: logs.value
  }
  return records[activeSection.value] || []
})
const hasActions = computed(() => ['users', 'authRecords', 'posts', 'reports', 'announcements', 'categories'].includes(activeSection.value))

const loadDashboard = async () => {
  try {
    const response = await fetchAdminDashboard()
    dashboard.value = response.data.data || {}
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '加载后台数据失败')
  }
}

const loadSection = async (section) => {
  if (section === 'dashboard') {
    await loadDashboard()
    return
  }
  if (section === 'messages') {
    return
  }

  loading.value = true
  try {
    const loaders = {
      users: fetchAdminUsers,
      posts: fetchAdminPosts,
      reports: fetchAdminReports,
      authRecords: fetchAdminAuthRecords,
      announcements: fetchAdminAnnouncements,
      categories: fetchAdminCategories,
      logs: fetchAdminLogs
    }
    const response = await loaders[section]()
    const data = response.data.data
    const records = Array.isArray(data) ? data : data?.records || []
    if (section === 'users') {
      users.value = records
    } else if (section === 'posts') {
      posts.value = records
    } else if (section === 'reports') {
      reports.value = records
    } else if (section === 'authRecords') {
      authRecords.value = records
    } else if (section === 'announcements') {
      announcements.value = records
    } else if (section === 'categories') {
      categories.value = records
    } else if (section === 'logs') {
      logs.value = records
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '加载后台数据失败')
  } finally {
    loading.value = false
  }
}

const setActiveSection = async (section) => {
  activeSection.value = section
  await loadSection(section)
}

const reloadCurrentSection = async () => {
  await loadSection(activeSection.value)
}

const logout = () => {
  clearCurrentAdmin()
  router.push('/login')
}

const toggleUser = async (user) => {
  await confirmAction(`确认${truthy(user.enabled) ? '禁用' : '启用'}该用户？`)
  if (truthy(user.enabled)) {
    await disableAdminUser(user.userId)
  } else {
    await enableAdminUser(user.userId)
  }
  ElMessage.success('用户状态已更新')
  await loadSection('users')
}

const approvePost = async (post) => {
  await approveAdminPost(post.id)
  ElMessage.success('内容已通过')
  await loadSection('posts')
  await loadDashboard()
}

const rejectPost = async (post) => {
  const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝内容', {
    confirmButtonText: '拒绝',
    cancelButtonText: '取消',
    inputValue: '内容不符合平台规范'
  })
  await rejectAdminPost(post.id, value)
  ElMessage.success('内容已拒绝')
  await loadSection('posts')
  await loadDashboard()
}

const togglePostDeleted = async (post) => {
  await confirmAction(`确认${truthy(post.deleted) ? '恢复' : '删除'}该内容？`)
  if (truthy(post.deleted)) {
    await restoreAdminPost(post.id)
  } else {
    await deleteAdminPost(post.id)
  }
  ElMessage.success('内容状态已更新')
  await loadSection('posts')
}

const handleReport = async (report, status) => {
  const label = status === 'resolved' ? '已处理' : '忽略'
  const { value } = await ElMessageBox.prompt(`请输入${label}说明`, '处理举报', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    inputValue: label
  })
  await handleAdminReport(report.reportId, status, value)
  ElMessage.success('举报状态已更新')
  await loadSection('reports')
  await loadDashboard()
}

const approveAuthRecord = async (record) => {
  await confirmAction('确认通过该用户的校园认证？')
  await approveAdminAuthRecord(record.recordId)
  ElMessage.success('认证已通过')
  await loadSection('authRecords')
}

const rejectAuthRecord = async (record) => {
  const { value } = await ElMessageBox.prompt('请输入驳回原因', '驳回认证', {
    confirmButtonText: '驳回',
    cancelButtonText: '取消',
    inputValue: '认证材料不符合要求，请重新提交'
  })
  await rejectAdminAuthRecord(record.recordId, value)
  ElMessage.success('认证已驳回')
  await loadSection('authRecords')
}

const submitAnnouncement = async () => {
  const payload = {
    title: announcementForm.title.trim(),
    content: announcementForm.content.trim(),
    status: announcementForm.status
  }
  if (!payload.title || !payload.content) {
    ElMessage.error('请输入公告标题和内容')
    return
  }
  if (editingAnnouncementId.value) {
    await updateAdminAnnouncement(editingAnnouncementId.value, payload)
    ElMessage.success('公告已更新')
  } else {
    await createAdminAnnouncement(payload)
    ElMessage.success('公告已新增')
  }
  resetAnnouncementForm()
  await loadSection('announcements')
}

const editAnnouncement = (announcement) => {
  editingAnnouncementId.value = announcement.announcementId
  announcementForm.title = announcement.title || ''
  announcementForm.content = announcement.content || ''
  announcementForm.status = announcement.status || 'draft'
}

const removeAnnouncement = async (announcement) => {
  await confirmAction('确认删除该公告？')
  await deleteAdminAnnouncement(announcement.announcementId)
  ElMessage.success('公告已删除')
  await loadSection('announcements')
}

const resetAnnouncementForm = () => {
  editingAnnouncementId.value = null
  announcementForm.title = ''
  announcementForm.content = ''
  announcementForm.status = 'draft'
}

const submitCategory = async () => {
  const payload = {
    plazaKey: categoryForm.plazaKey.trim(),
    categoryName: categoryForm.categoryName.trim(),
    sortWeight: categoryForm.sortWeight || 100
  }
  if (!payload.plazaKey || !payload.categoryName) {
    ElMessage.error('请输入广场标识和分类名称')
    return
  }
  if (editingCategoryId.value) {
    await updateAdminCategory(editingCategoryId.value, payload)
    ElMessage.success('分类已更新')
  } else {
    await createAdminCategory(payload)
    ElMessage.success('分类已新增')
  }
  resetCategoryForm()
  await loadSection('categories')
}

const editCategory = (category) => {
  editingCategoryId.value = category.id
  categoryForm.plazaKey = category.plazaKey || ''
  categoryForm.categoryName = category.categoryName || ''
  categoryForm.sortWeight = category.sortWeight || 100
}

const toggleCategory = async (category) => {
  if (truthy(category.enabled)) {
    await disableAdminCategory(category.id)
  } else {
    await enableAdminCategory(category.id)
  }
  ElMessage.success('分类状态已更新')
  await loadSection('categories')
}

const resetCategoryForm = () => {
  editingCategoryId.value = null
  categoryForm.plazaKey = ''
  categoryForm.categoryName = ''
  categoryForm.sortWeight = 100
}

const submitSystemMessage = async () => {
  const payload = {
    targetType: messageForm.targetType,
    targetUserId: messageForm.targetType === 'user' ? messageForm.targetUserId : null,
    title: messageForm.title.trim(),
    content: messageForm.content.trim()
  }
  if (!payload.title || !payload.content) {
    ElMessage.error('请输入消息标题和内容')
    return
  }
  await createAdminSystemMessage(payload)
  ElMessage.success('系统消息已发送')
  messageForm.targetType = 'all'
  messageForm.targetUserId = ''
  messageForm.title = ''
  messageForm.content = ''
}

const confirmAction = (message) => {
  return ElMessageBox.confirm(message, '确认操作', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  })
}

const recordKey = (record) => {
  return record.id || record.userId || record.reportId || record.recordId || record.announcementId || record.logId
}

const formatCell = (record, key) => {
  const value = record[key]
  if (key === 'enabled') {
    return truthy(value) ? '启用' : '禁用'
  }
  if (key === 'deleted') {
    return truthy(value) ? '已删除' : '正常'
  }
  return value ?? '-'
}

const truthy = (value) => {
  return value === true || value === 1 || value === '1'
}

onMounted(loadDashboard)
</script>

<style scoped>
.admin-page {
  display: grid;
  grid-template-columns: 240px 1fr;
  min-height: 100vh;
  color: #1f2937;
  background: #f5f7fb;
  font-family: "Alibaba PuHuiTi 3.0", "MiSans", "PingFang SC", "Microsoft YaHei", sans-serif;
}

.admin-sidebar {
  padding: 28px 20px;
  background: #111827;
  color: #fff;
}

.brand {
  display: grid;
  gap: 6px;
  margin-bottom: 34px;
}

.brand strong {
  font-size: 24px;
  font-weight: 900;
}

.brand span {
  color: #a5b4fc;
  font-size: 13px;
}

nav {
  display: grid;
  gap: 10px;
}

nav button {
  height: 44px;
  padding: 0 14px;
  border-radius: 8px;
  color: #cbd5e1;
  background: transparent;
  text-align: left;
  font-weight: 700;
}

nav button.active,
nav button:hover {
  color: #fff;
  background: #374151;
}

.admin-main {
  padding: 32px;
  min-width: 0;
}

.admin-header,
.panel-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
}

.admin-header {
  margin-bottom: 26px;
}

.admin-header h1 {
  margin: 0;
  font-size: 28px;
}

.admin-header p,
.panel-heading p {
  margin: 8px 0 0;
  color: #6b7280;
}

.admin-header button,
.panel-heading button,
.form-actions button,
.row-actions button {
  height: 40px;
  padding: 0 18px;
  border-radius: 8px;
  color: #fff;
  background: #4f46e5;
  font-weight: 800;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(120px, 1fr));
  gap: 16px;
}

.stat-card,
.work-panel,
.table-panel {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 12px 26px rgba(15, 23, 42, 0.06);
}

.stat-card {
  display: grid;
  gap: 12px;
  padding: 20px;
}

.stat-card span {
  color: #6b7280;
  font-size: 14px;
  font-weight: 700;
}

.stat-card strong {
  font-size: 30px;
}

.work-panel {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  margin-top: 18px;
  padding: 24px;
}

.work-panel h2,
.table-panel h2 {
  margin: 0;
  font-size: 20px;
}

.work-panel p {
  margin: 8px 0 0;
  color: #6b7280;
}

.work-panel ul {
  margin: 0;
  color: #374151;
  font-weight: 800;
  line-height: 1.9;
}

.table-panel {
  display: grid;
  gap: 18px;
  padding: 24px;
  overflow-x: auto;
}

.admin-form {
  display: grid;
  gap: 12px;
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f9fafb;
}

.inline-form {
  grid-template-columns: 1.2fr 1.2fr 0.7fr auto;
  align-items: start;
}

.admin-form input,
.admin-form select,
.admin-form textarea {
  min-height: 42px;
  padding: 0 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  color: #1f2937;
  background: #fff;
  font: inherit;
}

.admin-form textarea {
  min-height: 96px;
  padding-top: 10px;
  resize: vertical;
}

.form-actions {
  display: flex;
  gap: 10px;
}

.form-actions .secondary {
  color: #374151;
  background: #e5e7eb;
}

table {
  width: 100%;
  min-width: 820px;
  border-collapse: collapse;
  table-layout: fixed;
}

th,
td {
  padding: 14px 12px;
  border-bottom: 1px solid #e5e7eb;
  text-align: left;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

th {
  color: #6b7280;
  background: #f9fafb;
  font-size: 13px;
}

td {
  color: #374151;
  font-size: 14px;
}

.row-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.row-actions button {
  height: 32px;
  padding: 0 10px;
  background: #eef2ff;
  color: #4338ca;
  font-size: 13px;
}

.row-actions a {
  display: inline-flex;
  align-items: center;
  height: 32px;
  padding: 0 10px;
  border-radius: 8px;
  color: #047857;
  background: #ecfdf5;
  font-size: 13px;
  font-weight: 800;
  text-decoration: none;
}

.empty-state {
  display: grid;
  min-height: 180px;
  place-items: center;
  color: #6b7280;
  background: #f9fafb;
  border-radius: 8px;
  font-weight: 700;
}

@media (max-width: 900px) {
  .admin-page {
    grid-template-columns: 1fr;
  }

  .admin-sidebar {
    display: block;
  }

  .stats-grid,
  .inline-form {
    grid-template-columns: 1fr;
  }

  .work-panel,
  .admin-header,
  .panel-heading {
    display: grid;
  }
}
</style>
