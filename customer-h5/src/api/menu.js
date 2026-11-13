import request from '../utils/request'

/** C 端分类列表，type：1 菜品分类，2 套餐分类 */
export function getCategoryList(type = 1) {
  return request.get('/user/category/list', { params: { type } })
}

/** 某个分类下在售的菜品 */
export function getDishList(categoryId) {
  return request.get('/user/dish/list', { params: { categoryId } })
}
