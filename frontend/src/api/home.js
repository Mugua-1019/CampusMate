import axios from 'axios'

export const fetchHomePlaza = (params) => {
  return axios.get('/api/home/plaza', { params })
}
