import axios from 'axios'

export const login = (data) => {
  return axios.post('/api/auth/login', data)
}

export const register = (data) => {
  return axios.post('/api/auth/register', data)
}

export const resetPassword = (data) => {
  return axios.post('/api/auth/reset-password', data)
}
