import request from '../utils/request'

export function getDishPage(params) {
  return request.get('/admin/dish/page', { params })
}

export function getDishById(id) {
  return request.get(`/admin/dish/${id}`)
}

export function createDish(data) {
  return request.post('/admin/dish', data)
}

export function updateDish(data) {
  return request.put('/admin/dish', data)
}

export function deleteDish(ids) {
  return request.delete('/admin/dish', { params: { ids } })
}

export function changeDishStatus(id, status) {
  return request.put(`/admin/dish/status/${status}`, null, { params: { id } })
}

/** 菜品图片上传，走公共接口（E 的 MinIO），成功返回图片 url */
export function uploadDishImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/admin/common/upload', formData)
}
