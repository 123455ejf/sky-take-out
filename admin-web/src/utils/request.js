import axios from 'axios'
import { Message } from 'element-ui'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('sky_admin_token')
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
      handleUnauthorized()
    }
    return Promise.reject(new Error((body && body.msg) || '请求失败'))
  },
  error => {
    if (error.response && error.response.status === 401) {
      handleUnauthorized()
    }
    const body = error.response && error.response.data
    const message = (body && body.msg) || error.message || '网络请求失败'
    return Promise.reject(new Error(message))
  }
)

function handleUnauthorized() {
  localStorage.removeItem('sky_admin_token')
  localStorage.removeItem('sky_employee')
  if (window.location.hash !== '#/login') {
    Message.warning('登录已失效，请重新登录')
    window.location.hash = '#/login'
    window.location.reload()
  }
}

export default request
