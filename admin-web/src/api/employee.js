import request from '../utils/request'

export function login(data) {
  return request.post('/admin/employee/login', data)
}

export function logout() {
  return request.post('/admin/employee/logout')
}

export function getEmployeePage(params) {
  return request.get('/admin/employee/page', { params })
}

export function getEmployeeById(id) {
  return request.get(`/admin/employee/${id}`)
}

export function createEmployee(data) {
  return request.post('/admin/employee', data)
}

export function updateEmployee(data) {
  return request.put('/admin/employee', data)
}

export function changeEmployeeStatus(id, status) {
  return request.put(`/admin/employee/status/${status}`, null, {
    params: { id }
  })
}
