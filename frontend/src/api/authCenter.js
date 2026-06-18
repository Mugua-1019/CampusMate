import axios from 'axios'

export const fetchAuthCenter = () => {
  return axios.get('/api/auth-center')
}

export const submitCampusAuth = (data) => {
  return axios.post('/api/auth-center/campus', data)
}

export const uploadAuthMaterial = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return axios.post('/api/auth-center/material', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const sendPhoneAuthCode = (data) => {
  return axios.post('/api/auth-center/phone/code', data)
}

export const submitPhoneAuth = (data) => {
  return axios.post('/api/auth-center/phone', data)
}
