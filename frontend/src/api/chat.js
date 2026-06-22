import axios from 'axios'

export const fetchChatHome = () => {
  return axios.get('/api/chat')
}

export const fetchChatPeerProfile = (peerUserId, params) => {
  return axios.get(`/api/chat/users/${peerUserId}`, { params })
}

export const fetchChatMessages = (conversationId) => {
  return axios.get(`/api/chat/conversations/${conversationId}/messages`)
}

export const sendChatMessage = (data) => {
  return axios.post('/api/chat/messages', data)
}

export const updateChatArchiveStatus = (conversationId, archived) => {
  return axios.patch(`/api/chat/conversations/${conversationId}/archive`, { archived })
}

export const uploadChatAttachment = (file, type) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('type', type)
  return axios.post('/api/chat/attachments', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
