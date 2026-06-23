const USER_STORAGE_KEY = 'campusmate_user'
export const CURRENT_USER_CHANGED_EVENT = 'campusmate:current-user-changed'

const notifyCurrentUserChanged = (user) => {
  window.dispatchEvent(new CustomEvent(CURRENT_USER_CHANGED_EVENT, { detail: user }))
}

const toLoginIdentity = (user) => {
  if (!user?.userId) {
    return null
  }
  return {
    userId: user.userId,
    account: user.account || '',
    phone: user.phone || '',
    email: user.email || ''
  }
}

export const getCurrentUser = () => {
  try {
    const user = JSON.parse(localStorage.getItem(USER_STORAGE_KEY) || 'null')
    const identity = toLoginIdentity(user)
    if (identity) {
      localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(identity))
    }
    return identity
  } catch (error) {
    localStorage.removeItem(USER_STORAGE_KEY)
    return null
  }
}

export const isAuthenticatedUser = (user) => {
  return Boolean(user?.userId)
}

export const saveCurrentUser = (user) => {
  const identity = toLoginIdentity(user)
  if (!identity) {
    clearCurrentUser()
    return
  }
  localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(identity))
  notifyCurrentUserChanged(identity)
}

export const clearCurrentUser = () => {
  localStorage.removeItem(USER_STORAGE_KEY)
  notifyCurrentUserChanged(null)
}
