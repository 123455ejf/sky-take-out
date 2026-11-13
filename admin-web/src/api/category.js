import request from '../utils/request'

export function getCategoryPage(params) {
  return request.get('/admin/category/page', { params })
}

export function getCategoryList(type = 1) {
  return request.get('/admin/category/list', { params: { type } })
}

export function createCategory(data) {
  return request.post('/admin/category', data)
}

export function updateCategory(data) {
  return request.put('/admin/category', data)
}

export function deleteCategory(id) {
  return request.delete('/admin/category', { params: { id } })
}
