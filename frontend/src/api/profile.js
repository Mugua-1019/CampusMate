import axios from 'axios'

export const fetchProfile = (userId) => {
  return axios.get('/api/profile', { params: { userId } })
}

export const updateProfile = (data) => {
  return axios.put('/api/profile', data)
}

export const updateProfilePreferences = (data) => {
  return axios.put('/api/profile/preferences', data)
}

export const uploadAvatar = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return axios.post('/api/profile/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
