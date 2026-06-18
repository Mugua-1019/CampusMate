<template>
  <main class="login-page">
    <section class="login-shell">
      <div class="brand-block">
        <img :src="logoImage" alt="星伴 CampusMate" />
        <div>
          <strong>星伴</strong>
          <span>CampusMate</span>
        </div>
      </div>

      <section class="hero-copy" aria-label="登录引导">
        <h1>在校园里，<br />遇见与你同频的伙伴。</h1>
        <span class="gold-line"></span>
      </section>

      <section class="login-card" aria-label="登录表单">
        <div class="login-tabs">
          <button :class="{ active: activeTab === 'login' }" type="button" @click="activeTab = 'login'">账号登录</button>
          <button :class="{ active: activeTab === 'register' }" type="button" @click="activeTab = 'register'">用户注册</button>
        </div>

        <form class="login-form" @submit.prevent="handleSubmit">
          <label v-if="activeTab === 'register'" class="input-row">
            <User />
            <input v-model="nickname" type="text" autocomplete="nickname" placeholder="请输入昵称" />
          </label>

          <label class="input-row">
            <User />
            <input v-model="account" type="text" autocomplete="username" placeholder="请输入账号" />
          </label>

          <label class="input-row">
            <Lock />
            <input v-model="password" :type="passwordVisible ? 'text' : 'password'" autocomplete="current-password" placeholder="请输入密码" />
            <button class="icon-button" type="button" aria-label="切换密码可见性" @click="passwordVisible = !passwordVisible">
              <View v-if="passwordVisible" />
              <Hide v-else />
            </button>
          </label>

          <label v-if="activeTab === 'register'" class="input-row">
            <Lock />
            <input v-model="confirmPassword" :type="passwordVisible ? 'text' : 'password'" autocomplete="new-password" placeholder="请确认密码" />
          </label>

          <div v-if="activeTab === 'login'" class="form-options">
            <label class="remember-row">
              <input v-model="rememberMe" type="checkbox" />
              <span>记住我</span>
            </label>
            <button type="button" @click="showResetModal = true">忘记密码</button>
          </div>

          <button class="login-button" type="submit" :disabled="submitting">{{ activeTab === 'login' ? '立即登录' : '立即注册' }}</button>
        </form>

        <div class="divider">
          <span></span>
          <em>或</em>
          <span></span>
        </div>

        <button class="plaza-button" type="button" @click="goGuestHome">
          <OfficeBuilding />
          先看看广场
        </button>
      </section>

      <p class="agreement">
        登录即表示同意
        <a href="#">《用户协议》</a>
        和
        <a href="#">《隐私政策》</a>
      </p>

      <div v-if="showResetModal" class="modal-mask" @click.self="closeResetModal">
        <section class="reset-modal" aria-label="找回密码">
          <button class="modal-close" type="button" aria-label="关闭找回密码弹窗" @click="closeResetModal">×</button>
          <div class="modal-heading">
            <strong>找回密码</strong>
            <span>验证账号后即可重新设置登录密码</span>
          </div>

          <form class="reset-form" @submit.prevent="handleResetPassword">
            <label class="input-row">
              <User />
              <input v-model="resetAccount" type="text" autocomplete="username" placeholder="请输入账号 / 手机号 / 邮箱" />
            </label>

            <label class="input-row">
              <Lock />
              <input v-model="resetPassword" type="password" autocomplete="new-password" placeholder="请输入新密码" />
            </label>

            <label class="input-row">
              <Lock />
              <input v-model="resetConfirmPassword" type="password" autocomplete="new-password" placeholder="请确认新密码" />
            </label>

            <div class="modal-actions">
              <button class="modal-secondary" type="button" @click="closeResetModal">取消</button>
              <button class="modal-primary" type="submit">重置密码</button>
            </div>
          </form>
        </section>
      </div>
    </section>
  </main>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Hide, Lock, OfficeBuilding, User, View } from '@element-plus/icons-vue'
import logoImage from '../../../assets/images/logo-star-mascot.png'
import { login, register, resetPassword as resetPasswordApi } from '../../../api/auth'
import { clearCurrentUser, saveCurrentUser } from '../../../utils/currentUser'

const router = useRouter()
const activeTab = ref('login')
const account = ref('')
const nickname = ref('')
const password = ref('')
const confirmPassword = ref('')
const passwordVisible = ref(false)
const rememberMe = ref(true)
const showResetModal = ref(false)
const resetAccount = ref('')
const resetPassword = ref('')
const resetConfirmPassword = ref('')
const submitting = ref(false)

const goHome = () => {
  router.push('/home')
}

const goGuestHome = () => {
  clearCurrentUser()
  goHome()
}

const handleSubmit = async () => {
  if (submitting.value) {
    return
  }
  const loginAccount = account.value.trim()
  if (!loginAccount || !password.value) {
    ElMessage.error('请输入账号和密码')
    return
  }
  if (activeTab.value === 'register' && !nickname.value.trim()) {
    ElMessage.error('请输入昵称')
    return
  }
  if (activeTab.value === 'register' && password.value !== confirmPassword.value) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  submitting.value = true
  try {
    const request = { account: loginAccount, password: password.value, nickname: nickname.value.trim() }
    const response = activeTab.value === 'login' ? await login(request) : await register(request)
    saveCurrentUser(response.data.data)
    ElMessage.success(activeTab.value === 'login' ? '登录成功' : '注册成功')
    goHome()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

const closeResetModal = () => {
  showResetModal.value = false
  resetPassword.value = ''
  resetConfirmPassword.value = ''
}

const handleResetPassword = async () => {
  const loginAccount = resetAccount.value.trim()
  if (!loginAccount || !resetPassword.value) {
    ElMessage.error('请输入账号和新密码')
    return
  }
  if (resetPassword.value !== resetConfirmPassword.value) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }
  try {
    await resetPasswordApi({ account: loginAccount, password: resetPassword.value })
    ElMessage.success('密码已重置，请使用新密码登录')
    closeResetModal()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '重置失败，请稍后重试')
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  padding: 26px;
  font-family: "Alibaba PuHuiTi 3.0", "MiSans", "HarmonyOS Sans SC", "PingFang SC", "Microsoft YaHei", sans-serif;
  font-synthesis: none;
  -webkit-font-smoothing: antialiased;
  text-rendering: geometricPrecision;
  background:
    radial-gradient(circle at 12% 12%, rgba(158, 137, 255, 0.2), transparent 28%),
    radial-gradient(circle at 88% 10%, rgba(247, 174, 225, 0.36), transparent 30%),
    linear-gradient(135deg, #f4f2ff 0%, #fff6fb 100%);
}

.login-shell {
  position: relative;
  min-height: calc(100vh - 52px);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.72);
  border-radius: 32px;
  background:
    linear-gradient(90deg, rgba(255, 255, 255, 0.08) 0%, rgba(255, 255, 255, 0.72) 64%, rgba(255, 247, 253, 0.78) 100%),
    url("../../../assets/images/LoginBack.png") left bottom / cover no-repeat;
  box-shadow: 0 22px 58px rgba(126, 98, 179, 0.14);
}

.brand-block {
  position: absolute;
  top: 62px;
  left: 56px;
  display: flex;
  align-items: center;
  gap: 14px;
  z-index: 2;
}

.brand-block img {
  width: 94px;
  height: 66px;
  object-fit: contain;
}

.brand-block strong {
  display: block;
  color: #624df0;
  background: linear-gradient(180deg, #745bf8 0%, #5942dd 58%, #4631b8 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-size: 31px;
  line-height: 0.98;
  font-weight: 900;
  letter-spacing: 0;
}

.brand-block span {
  display: block;
  margin-top: 2px;
  color: #624df0;
  background: linear-gradient(180deg, #6f58f5 0%, #523dde 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-size: 17px;
  line-height: 1;
  font-style: italic;
  font-weight: 800;
  letter-spacing: 0;
}

.hero-copy {
  position: absolute;
  top: 202px;
  left: 154px;
  z-index: 2;
}

.hero-copy h1 {
  margin: 0;
  color: #5540c8;
  background: linear-gradient(180deg, #7159f4 0%, #5d45dc 48%, #3f2baa 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-size: 42px;
  line-height: 1.43;
  font-weight: 900;
  letter-spacing: 0;
  text-shadow: 0 4px 14px rgba(255, 255, 255, 0.44);
}

.gold-line {
  display: block;
  width: 220px;
  height: 24px;
  margin: 8px 0 0 196px;
  border-top: 3px solid #ffd020;
  border-radius: 50%;
  transform: rotate(-8deg);
}

.login-card {
  position: absolute;
  top: 118px;
  right: 138px;
  z-index: 3;
  width: min(590px, calc(100% - 80px));
  padding: 49px 48px 47px;
  border: 1px solid rgba(255, 255, 255, 0.78);
  border-radius: 26px;
  background: rgba(255, 255, 255, 0.62);
  box-shadow: 0 22px 58px rgba(108, 88, 166, 0.18);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
}

.login-tabs {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  align-items: end;
  height: 53px;
  margin-bottom: 34px;
  border-bottom: 1px solid rgba(168, 160, 195, 0.24);
}

.login-tabs button {
  position: relative;
  height: 53px;
  color: #858099;
  background: transparent;
  font-size: 20px;
  line-height: 1;
  font-weight: 900;
  letter-spacing: 0;
}

.login-tabs button.active {
  color: #6d55f1;
}

.login-tabs button.active::after {
  content: "";
  position: absolute;
  left: 50%;
  bottom: -2px;
  width: 46px;
  height: 4px;
  border-radius: 999px;
  background: #785df4;
  transform: translateX(-50%);
}

.login-form {
  display: grid;
  gap: 24px;
}

.input-row {
  display: flex;
  align-items: center;
  gap: 17px;
  height: 68px;
  padding: 0 25px;
  border: 1px solid #ded9ef;
  border-radius: 13px;
  background: rgba(255, 255, 255, 0.76);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.input-row svg {
  width: 22px;
  height: 22px;
  color: #9a94b7;
  flex: 0 0 auto;
}

.input-row input {
  width: 100%;
  min-width: 0;
  border: 0;
  outline: 0;
  color: #4d4965;
  background: transparent;
  font-size: 16px;
  font-weight: 400;
  letter-spacing: 0;
}

.input-row input::placeholder {
  color: #b8b4c8;
  font-weight: 400;
}

.icon-button {
  display: grid;
  width: 30px;
  height: 30px;
  flex: 0 0 30px;
  place-items: center;
  color: #9a94b7;
  background: transparent;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 5px;
}

.remember-row {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  color: #67617a;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0;
}

.remember-row input {
  width: 17px;
  height: 17px;
  accent-color: #765df4;
}

.form-options button {
  color: #7056f1;
  background: transparent;
  font-size: 14px;
  font-weight: 800;
  letter-spacing: 0;
}

.login-button,
.plaza-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 69px;
  border-radius: 999px;
  font-size: 23px;
  font-weight: 900;
  line-height: 1;
  letter-spacing: 0;
}

.login-button {
  margin-top: 6px;
  color: #fff;
  background: linear-gradient(105deg, #7659f6 0%, #a766e9 52%, #f06f9f 100%);
  box-shadow: 0 16px 34px rgba(129, 91, 244, 0.3);
}

.login-button:disabled {
  cursor: not-allowed;
  opacity: 0.72;
}

.divider {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 22px;
  margin: 33px 0 28px;
}

.divider span {
  height: 1px;
  background: rgba(172, 166, 194, 0.26);
}

.divider em {
  color: #918aa4;
  font-style: normal;
  font-size: 15px;
  font-weight: 600;
  line-height: 1;
}

.plaza-button {
  gap: 18px;
  color: #7056f1;
  font-weight: 900;
  border: 1px solid rgba(117, 102, 246, 0.28);
  background: rgba(255, 255, 255, 0.46);
}

.plaza-button svg {
  width: 28px;
  height: 28px;
}

.agreement {
  position: absolute;
  left: 50%;
  bottom: 23px;
  z-index: 2;
  margin: 0;
  color: #706b7e;
  font-size: 12px;
  font-weight: 600;
  line-height: 1;
  letter-spacing: 0;
  transform: translateX(-50%);
}

.agreement a {
  color: #6554e8;
  font-weight: 800;
  text-decoration: none;
}

.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 20;
  display: grid;
  place-items: center;
  padding: 24px;
  background: rgba(51, 42, 92, 0.22);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.reset-modal {
  position: relative;
  width: min(476px, 100%);
  padding: 38px 36px 34px;
  border: 1px solid rgba(255, 255, 255, 0.82);
  border-radius: 26px;
  background:
    radial-gradient(circle at 12% 0%, rgba(255, 255, 255, 0.78), transparent 36%),
    linear-gradient(145deg, rgba(255, 255, 255, 0.82), rgba(250, 245, 255, 0.72));
  box-shadow: 0 24px 70px rgba(92, 74, 150, 0.22);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
}

.modal-close {
  position: absolute;
  top: 18px;
  right: 20px;
  width: 34px;
  height: 34px;
  color: #9b94b2;
  background: transparent;
  font-size: 28px;
  line-height: 1;
}

.modal-heading {
  display: grid;
  gap: 10px;
  margin-bottom: 26px;
  text-align: center;
}

.modal-heading strong {
  color: #624df0;
  background: linear-gradient(180deg, #745bf8 0%, #5942dd 58%, #4631b8 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-size: 26px;
  line-height: 1;
  font-weight: 900;
}

.modal-heading span {
  color: #858099;
  font-size: 14px;
  font-weight: 600;
}

.reset-form {
  display: grid;
  gap: 18px;
}

.reset-form .input-row {
  height: 62px;
}

.modal-actions {
  display: grid;
  grid-template-columns: 1fr 1.35fr;
  gap: 14px;
  margin-top: 6px;
}

.modal-secondary,
.modal-primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 56px;
  border-radius: 999px;
  font-size: 17px;
  font-weight: 900;
}

.modal-secondary {
  color: #706b7e;
  border: 1px solid rgba(117, 102, 246, 0.2);
  background: rgba(255, 255, 255, 0.62);
}

.modal-primary {
  color: #fff;
  background: linear-gradient(105deg, #7659f6 0%, #a766e9 52%, #f06f9f 100%);
  box-shadow: 0 14px 30px rgba(129, 91, 244, 0.26);
}

@media (max-width: 1180px) {
  .login-shell {
    min-height: auto;
    padding: 38px 24px 88px;
    background-position: left bottom;
  }

  .brand-block,
  .hero-copy,
  .login-card,
  .agreement {
    position: relative;
    top: auto;
    right: auto;
    bottom: auto;
    left: auto;
  }

  .brand-block,
  .hero-copy,
  .login-card {
    margin-left: auto;
    margin-right: auto;
  }

  .brand-block {
    justify-content: center;
    margin-bottom: 44px;
  }

  .hero-copy {
    width: min(590px, 100%);
    margin-bottom: 32px;
  }

  .login-card {
    width: min(590px, 100%);
  }

  .agreement {
    width: 100%;
    margin-top: 34px;
    text-align: center;
    transform: none;
  }
}

@media (max-width: 640px) {
  .login-page {
    padding: 14px;
  }

  .login-shell {
    padding: 26px 14px 44px;
    border-radius: 24px;
  }

  .brand-block {
    gap: 8px;
    margin-bottom: 30px;
  }

  .brand-block img {
    width: 72px;
    height: 54px;
  }

  .brand-block strong {
    font-size: 27px;
  }

  .brand-block span {
    font-size: 15px;
  }

  .hero-copy h1 {
    font-size: 30px;
    line-height: 1.42;
  }

  .gold-line {
    width: 150px;
    margin-left: 120px;
  }

  .login-card {
    padding: 28px 18px;
    border-radius: 22px;
  }

  .login-tabs button {
    font-size: 18px;
  }

  .input-row,
  .login-button,
  .plaza-button {
    height: 58px;
  }

  .login-button,
  .plaza-button {
    font-size: 20px;
  }

  .reset-modal {
    padding: 32px 18px 24px;
    border-radius: 22px;
  }

  .modal-actions {
    grid-template-columns: 1fr;
  }
}
</style>
