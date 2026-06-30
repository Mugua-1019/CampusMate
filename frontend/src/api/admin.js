import axios from 'axios'
import { getCurrentAdmin } from '../utils/currentUser'

const adminHeaders = () => {
  const admin = getCurrentAdmin()
  return {
    'X-CampusMate-Admin-Id': admin?.adminId
  }
}

export const fetchAdminDashboard = () => {
  return axios.get('/api/admin/dashboard', { headers: adminHeaders() })
}

export const fetchAdminUsers = () => {
  return axios.get('/api/admin/users', { headers: adminHeaders() })
}

export const enableAdminUser = (userId) => {
  return axios.post(`/api/admin/users/${userId}/enable`, null, { headers: adminHeaders() })
}

export const disableAdminUser = (userId) => {
  return axios.post(`/api/admin/users/${userId}/disable`, null, { headers: adminHeaders() })
}

export const fetchAdminPosts = () => {
  return axios.get('/api/admin/posts', { headers: adminHeaders() })
}

export const approveAdminPost = (postId) => {
  return axios.post(`/api/admin/posts/${postId}/approve`, null, { headers: adminHeaders() })
}

export const rejectAdminPost = (postId, reason) => {
  return axios.post(`/api/admin/posts/${postId}/reject`, { reason }, { headers: adminHeaders() })
}

export const deleteAdminPost = (postId) => {
  return axios.post(`/api/admin/posts/${postId}/delete`, null, { headers: adminHeaders() })
}

export const restoreAdminPost = (postId) => {
  return axios.post(`/api/admin/posts/${postId}/restore`, null, { headers: adminHeaders() })
}

export const fetchAdminReports = () => {
  return axios.get('/api/admin/reports', { headers: adminHeaders() })
}

export const handleAdminReport = (reportId, status, handlingNote) => {
  return axios.post(`/api/admin/reports/${reportId}/handle`, { status, handlingNote }, { headers: adminHeaders() })
}

export const fetchAdminAuthRecords = () => {
  return axios.get('/api/admin/auth-records', { headers: adminHeaders() })
}

export const approveAdminAuthRecord = (recordId) => {
  return axios.post(`/api/admin/auth-records/${recordId}/approve`, null, { headers: adminHeaders() })
}

export const rejectAdminAuthRecord = (recordId, reason) => {
  return axios.post(`/api/admin/auth-records/${recordId}/reject`, { reason }, { headers: adminHeaders() })
}

export const fetchAdminAnnouncements = () => {
  return axios.get('/api/admin/announcements', { headers: adminHeaders() })
}

export const createAdminAnnouncement = (data) => {
  return axios.post('/api/admin/announcements', data, { headers: adminHeaders() })
}

export const updateAdminAnnouncement = (announcementId, data) => {
  return axios.put(`/api/admin/announcements/${announcementId}`, data, { headers: adminHeaders() })
}

export const deleteAdminAnnouncement = (announcementId) => {
  return axios.delete(`/api/admin/announcements/${announcementId}`, { headers: adminHeaders() })
}

export const fetchAdminCategories = () => {
  return axios.get('/api/admin/categories', { headers: adminHeaders() })
}

export const createAdminCategory = (data) => {
  return axios.post('/api/admin/categories', data, { headers: adminHeaders() })
}

export const updateAdminCategory = (categoryId, data) => {
  return axios.put(`/api/admin/categories/${categoryId}`, data, { headers: adminHeaders() })
}

export const enableAdminCategory = (categoryId) => {
  return axios.post(`/api/admin/categories/${categoryId}/enable`, null, { headers: adminHeaders() })
}

export const disableAdminCategory = (categoryId) => {
  return axios.post(`/api/admin/categories/${categoryId}/disable`, null, { headers: adminHeaders() })
}

export const createAdminSystemMessage = (data) => {
  return axios.post('/api/admin/system-messages', data, { headers: adminHeaders() })
}

export const fetchAdminLogs = () => {
  return axios.get('/api/admin/logs', { headers: adminHeaders() })
}
