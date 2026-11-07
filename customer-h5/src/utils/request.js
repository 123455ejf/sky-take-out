import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('sky_customer_token')
  if (token) {
    config.headers.token = token
  }
  return config
})

request.interceptors.response.use(
  response => {
    const body = response.data
    if (body && body.code === 1) {
      return body.data
    }
    if (body && body.code === 401) {
      localStorage.removeItem('sky_customer_token')
      window.location.hash = '#/login'
    }
    return Promise.reject(new Error((body && body.msg) || '请求失败'))
  },
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('sky_customer_token')
      window.location.hash = '#/login'
    }
    const body = error.response && error.response.data
    return Promise.reject(new Error((body && body.msg) || error.message || '网络请求失败'))
  }
)

export default request
