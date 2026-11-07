import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  { path: '/', redirect: '/home' },
  {
    path: '/home',
    component: () => import('../views/Home.vue'),
    meta: { title: '点餐' }
  },
  {
    path: '/cart',
    component: () => import('../views/Placeholder.vue'),
    meta: { title: '购物车', owner: 'E' }
  },
  {
    path: '/orders',
    component: () => import('../views/Placeholder.vue'),
    meta: { title: '我的订单', owner: 'D' }
  },
  {
    path: '/address',
    component: () => import('../views/Placeholder.vue'),
    meta: { title: '地址簿', owner: 'E' }
  },
  {
    path: '/login',
    component: () => import('../views/Placeholder.vue'),
    meta: { title: '客户登录', owner: 'E' }
  }
]

const router = new VueRouter({
  mode: 'hash',
  routes
})

router.afterEach(to => {
  document.title = to.meta.title
    ? `${to.meta.title} - 苍穹外卖`
    : '苍穹外卖'
})

export default router
