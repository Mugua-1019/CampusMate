import axios from 'axios'

export const fetchHomePlaza = (params) => {
  return axios.get('/api/home/plaza', { params })
}

export const fetchHomePostDetail = (id) => {
  return axios.get(`/api/home/match-posts/${id}`)
}

export const fetchVentPostDetail = (id) => {
  return axios.get(`/api/home/vent-posts/${id}`)
}

export const submitVentPostComfort = (id) => {
  return axios.post(`/api/home/vent-posts/${id}/comforts`)
}

export const submitVentPostReply = (id, data) => {
  return axios.post(`/api/home/vent-posts/${id}/replies`, data)
}

export const fetchHomeNotifications = () => {
  return axios.get('/api/home/notifications')
}

export const markHomeNotificationsRead = () => {
  return axios.post('/api/home/notifications/read')
}

export const submitHomePostReport = (id, data) => {
  return axios.post(`/api/home/match-posts/${id}/reports`, data)
}
