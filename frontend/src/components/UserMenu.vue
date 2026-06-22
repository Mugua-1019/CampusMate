<template>
  <div ref="menuRef" class="user-menu">
    <button class="profile-chip" type="button" aria-label="个人菜单" @click="menuOpen = !menuOpen">
      <img v-if="avatarUrl" class="profile-avatar" :src="avatarUrl" alt="头像" />
      <span v-else class="profile-avatar">{{ avatarText || '星' }}</span>
      <span>{{ statusText || '个人中心' }}</span>
      <ArrowDown />
    </button>

    <section v-if="menuOpen" class="menu-panel">
      <button type="button" @click="goProfile"><User /> 个人中心</button>
      <button type="button" @click="openPasswordDialog"><Lock /> 修改密码</button>
      <button type="button" @click="logout"><SwitchButton /> 退出登录</button>
    </section>

    <el-dialog v-model="passwordVisible" title="修改密码" width="430px" class="password-dialog" @closed="resetPasswordForm">
      <el-form label-width="86px">
        <el-form-item label="手机号">
          <div class="code-row">
            <el-input v-model="passwordForm.phone" maxlength="11" placeholder="请输入绑定手机号" />
            <button type="button" class="code-button" :disabled="codeSending || countdown > 0" @click="sendCode">
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </button>
          </div>
        </el-form-item>
        <el-form-item label="验证码">
          <el-input v-model="passwordForm.code" maxlength="6" placeholder="请输入短信验证码" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.password" type="password" maxlength="32" show-password placeholder="至少 6 位" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" maxlength="32" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <button type="button" class="dialog-cancel" @click="passwordVisible = false">取消</button>
        <button type="button" class="dialog-save" :disabled="passwordSubmitting" @click="submitPassword">
          {{ passwordSubmitting ? '提交中...' : '确认修改' }}
        </button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown, Lock, SwitchButton, User } from '@element-plus/icons-vue'
import { sendPhoneAuthCode } from '../api/authCenter'
import { changePassword } from '../api/auth'
import { clearCurrentUser } from '../utils/currentUser'

defineProps({
  avatarUrl: {
    type: String,
    default: ''
  },
  avatarText: {
    type: String,
    default: '星'
  },
  statusText: {
    type: String,
    default: '个人中心'
  }
})

const router = useRouter()
const menuRef = ref(null)
const menuOpen = ref(false)
const passwordVisible = ref(false)
const codeSending = ref(false)
const passwordSubmitting = ref(false)
const countdown = ref(0)
let countdownTimer = null
const passwordForm = reactive({
  phone: '',
  code: '',
  password: '',
  confirmPassword: ''
})

const goProfile = () => {
  menuOpen.value = false
  router.push('/profile')
}

const openPasswordDialog = () => {
  menuOpen.value = false
  passwordVisible.value = true
}

const logout = () => {
  clearCurrentUser()
  menuOpen.value = false
  router.push('/home')
}

const sendCode = async () => {
  if (!/^\d{11}$/.test(passwordForm.phone)) {
    ElMessage.error('请输入 11 位手机号')
    return
  }
  codeSending.value = true
  try {
    const { data } = await sendPhoneAuthCode({ phone: passwordForm.phone })
    ElMessage.success(`验证码已发送${data.data?.code ? `：${data.data.code}` : ''}`)
    startCountdown()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '验证码发送失败')
  } finally {
    codeSending.value = false
  }
}

const submitPassword = async () => {
  if (!/^\d{11}$/.test(passwordForm.phone)) {
    ElMessage.error('请输入 11 位手机号')
    return
  }
  if (!passwordForm.code.trim()) {
    ElMessage.error('请输入验证码')
    return
  }
  if (passwordForm.password.length < 6) {
    ElMessage.error('新密码至少 6 位')
    return
  }
  if (passwordForm.password !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  passwordSubmitting.value = true
  try {
    await changePassword({
      phone: passwordForm.phone,
      code: passwordForm.code,
      password: passwordForm.password
    })
    ElMessage.success('密码已修改，请重新登录')
    clearCurrentUser()
    passwordVisible.value = false
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '密码修改失败')
  } finally {
    passwordSubmitting.value = false
  }
}

const resetPasswordForm = () => {
  passwordForm.phone = ''
  passwordForm.code = ''
  passwordForm.password = ''
  passwordForm.confirmPassword = ''
  clearCountdown()
}

const startCountdown = () => {
  clearCountdown()
  countdown.value = 60
  countdownTimer = window.setInterval(() => {
    countdown.value -= 1
    if (countdown.value <= 0) {
      clearCountdown()
    }
  }, 1000)
}

const clearCountdown = () => {
  if (countdownTimer) {
    window.clearInterval(countdownTimer)
    countdownTimer = null
  }
  countdown.value = 0
}

const handleDocumentClick = (event) => {
  if (menuRef.value && !menuRef.value.contains(event.target)) {
    menuOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleDocumentClick)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleDocumentClick)
  clearCountdown()
})
</script>

<style scoped>
.user-menu {
  position: relative;
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

.profile-chip svg {
  width: 18px;
  height: 18px;
  color: #c48a39;
}

.menu-panel {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  display: grid;
  width: 154px;
  padding: 8px;
  border: 1px solid #eee7ff;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 16px 34px rgba(87, 75, 128, 0.16);
  z-index: 40;
}

.menu-panel button {
  display: flex;
  align-items: center;
  gap: 9px;
  height: 38px;
  padding: 0 10px;
  border-radius: 8px;
  color: #5c5f78;
  background: transparent;
  font-size: 14px;
  font-weight: 800;
  text-align: left;
}

.menu-panel button:hover {
  color: #6d55f0;
  background: #f3efff;
}

.menu-panel svg {
  width: 17px;
  height: 17px;
  flex: 0 0 17px;
}

.code-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 108px;
  gap: 10px;
  width: 100%;
}

.code-button,
.dialog-cancel,
.dialog-save {
  border-radius: 999px;
  font-weight: 900;
}

.code-button {
  height: 32px;
  color: #745cf2;
  border: 1px solid #d7ccfb;
  background: #fff;
}

.code-button:disabled,
.dialog-save:disabled {
  cursor: not-allowed;
  opacity: 0.68;
}

.dialog-cancel,
.dialog-save {
  min-width: 84px;
  height: 34px;
  margin-left: 10px;
}

.dialog-cancel {
  border: 1px solid #dedced;
  color: #626681;
  background: #fff;
}

.dialog-save {
  color: #fff;
  background: linear-gradient(105deg, #755df4, #ef6ea7);
}

@media (max-width: 760px) {
  .menu-panel {
    right: auto;
    left: 0;
  }

  .code-row {
    grid-template-columns: 1fr;
  }
}
</style>
