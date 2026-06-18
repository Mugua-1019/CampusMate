const USER_STORAGE_KEY = 'campusmate_user'

export const getCurrentUser = () => {
  try {
    return JSON.parse(localStorage.getItem(USER_STORAGE_KEY) || 'null')
  } catch (error) {
    localStorage.removeItem(USER_STORAGE_KEY)
    return null
  }
}

export const isAuthenticatedUser = (user) => {
  return Boolean(user?.userId)
}

export const saveCurrentUser = (user) => {
  localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(user))
}

export const clearCurrentUser = () => {
  localStorage.removeItem(USER_STORAGE_KEY)
}
