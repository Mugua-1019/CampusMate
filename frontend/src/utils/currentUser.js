const USER_STORAGE_KEY = 'campusmate_user'
const ADMIN_STORAGE_KEY = 'campusmate_admin'
const ROLE_STORAGE_KEY = 'campusmate_role'
export const CURRENT_USER_CHANGED_EVENT = 'campusmate:current-user-changed'
export const CURRENT_ADMIN_CHANGED_EVENT = 'campusmate:current-admin-changed'

export const ROLE_USER = 'user'
export const ROLE_ADMIN = 'admin'

const notifyCurrentUserChanged = (user) => {
  window.dispatchEvent(new CustomEvent(CURRENT_USER_CHANGED_EVENT, { detail: user }))
}

const notifyCurrentAdminChanged = (admin) => {
  window.dispatchEvent(new CustomEvent(CURRENT_ADMIN_CHANGED_EVENT, { detail: admin }))
}

const readStorage = (key) => {
  try {
    return JSON.parse(localStorage.getItem(key) || 'null')
  } catch (error) {
    localStorage.removeItem(key)
    return null
  }
}

const setCurrentRole = (role) => {
  if (role) {
    localStorage.setItem(ROLE_STORAGE_KEY, role)
  } else {
    localStorage.removeItem(ROLE_STORAGE_KEY)
  }
}

export const getCurrentRole = () => {
  const role = localStorage.getItem(ROLE_STORAGE_KEY)
  if (role === ROLE_USER || role === ROLE_ADMIN) {
    return role
  }

  const hasUser = Boolean(toLoginIdentity(readStorage(USER_STORAGE_KEY)))
  const hasAdmin = Boolean(toAdminIdentity(readStorage(ADMIN_STORAGE_KEY)))
  if (hasUser && !hasAdmin) {
    setCurrentRole(ROLE_USER)
    return ROLE_USER
  }
  if (hasAdmin && !hasUser) {
    setCurrentRole(ROLE_ADMIN)
    return ROLE_ADMIN
  }
  if (hasUser && hasAdmin) {
    clearCurrentUser()
    clearCurrentAdmin()
  }
  return null
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
  if (getCurrentRole() !== ROLE_USER) {
    return null
  }
  const identity = toLoginIdentity(readStorage(USER_STORAGE_KEY))
  if (identity) {
    localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(identity))
  }
  return identity
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
  localStorage.removeItem(ADMIN_STORAGE_KEY)
  setCurrentRole(ROLE_USER)
  localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(identity))
  notifyCurrentAdminChanged(null)
  notifyCurrentUserChanged(identity)
}

export const clearCurrentUser = () => {
  localStorage.removeItem(USER_STORAGE_KEY)
  if (localStorage.getItem(ROLE_STORAGE_KEY) === ROLE_USER) {
    setCurrentRole(null)
  }
  notifyCurrentUserChanged(null)
}

const toAdminIdentity = (admin) => {
  if (!admin?.adminId) {
    return null
  }
  return {
    adminId: admin.adminId,
    username: admin.username || '',
    displayName: admin.displayName || '',
    role: admin.role || '',
    token: admin.token || ''
  }
}

export const getCurrentAdmin = () => {
  if (getCurrentRole() !== ROLE_ADMIN) {
    return null
  }
  const identity = toAdminIdentity(readStorage(ADMIN_STORAGE_KEY))
  if (identity) {
    localStorage.setItem(ADMIN_STORAGE_KEY, JSON.stringify(identity))
  }
  return identity
}

export const isAuthenticatedAdmin = (admin) => {
  return Boolean(admin?.adminId)
}

export const saveCurrentAdmin = (admin) => {
  const identity = toAdminIdentity(admin)
  if (!identity) {
    clearCurrentAdmin()
    return
  }
  localStorage.removeItem(USER_STORAGE_KEY)
  setCurrentRole(ROLE_ADMIN)
  localStorage.setItem(ADMIN_STORAGE_KEY, JSON.stringify(identity))
  notifyCurrentUserChanged(null)
  notifyCurrentAdminChanged(identity)
}

export const clearCurrentAdmin = () => {
  localStorage.removeItem(ADMIN_STORAGE_KEY)
  if (localStorage.getItem(ROLE_STORAGE_KEY) === ROLE_ADMIN) {
    setCurrentRole(null)
  }
  notifyCurrentAdminChanged(null)
}
