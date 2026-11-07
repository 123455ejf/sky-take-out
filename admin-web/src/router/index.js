import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '../store'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue')
  },
  {
    path: '/',
    component: () => import('../views/layout/index.vue'),
    redirect: '/employee',
    children: [
      {
        path: 'employee',
        name: 'Employee',
        component: () => import('../views/employee/index.vue'),
        meta: { title: '员工管理' }
      }
    ]
  },
  {
    path: '*',
    redirect: '/'
  }
]

const router = new VueRouter({
  mode: 'hash',
  routes
})

router.beforeEach((to, from, next) => {
  const token = store.state.token
  if (to.path !== '/login' && !token) {
    next('/login')
    return
  }
  if (to.path === '/login' && token) {
    next('/employee')
    return
  }
  document.title = to.meta.title
    ? `${to.meta.title} - 苍穹外卖`
    : '苍穹外卖管理后台'
  next()
})

export default router
