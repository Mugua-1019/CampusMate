import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { fetchProfile } from '../api/profile'
import { CURRENT_USER_CHANGED_EVENT, getCurrentUser, isAuthenticatedUser } from '../utils/currentUser'

export const useCurrentUserProfile = () => {
  const currentUser = ref(getCurrentUser())
  const profile = ref(null)
  const loading = ref(false)
  const error = ref(null)

  const isLoggedIn = computed(() => isAuthenticatedUser(currentUser.value))
  const verified = computed(() => Boolean(profile.value?.verified))
  const statusText = computed(() => (verified.value ? '已认证' : '待认证'))
  const nickname = computed(() => profile.value?.nickname || '小星同学')
  const avatarUrl = computed(() => profile.value?.avatarUrl || '')
  const avatarText = computed(() => (nickname.value || '星').slice(0, 1))

  const loadProfile = async () => {
    if (!isLoggedIn.value) {
      profile.value = null
      return null
    }
    loading.value = true
    error.value = null
    try {
      const { data } = await fetchProfile(currentUser.value.userId)
      profile.value = data.data || null
      return profile.value
    } catch (caughtError) {
      error.value = caughtError
      throw caughtError
    } finally {
      loading.value = false
    }
  }

  const syncCurrentUser = () => {
    currentUser.value = getCurrentUser()
    if (!isLoggedIn.value) {
      profile.value = null
      error.value = null
    }
  }

  onMounted(() => {
    window.addEventListener(CURRENT_USER_CHANGED_EVENT, syncCurrentUser)
  })

  onBeforeUnmount(() => {
    window.removeEventListener(CURRENT_USER_CHANGED_EVENT, syncCurrentUser)
  })

  return {
    currentUser,
    profile,
    loading,
    error,
    isLoggedIn,
    verified,
    statusText,
    nickname,
    avatarUrl,
    avatarText,
    loadProfile
  }
}
