const USER_STORAGE_KEY = 'campusmate_user'

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
}

export const clearCurrentUser = () => {
  localStorage.removeItem(USER_STORAGE_KEY)
}
