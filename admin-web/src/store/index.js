import Vue from 'vue'
import Vuex from 'vuex'
import { login } from '../api/employee'

Vue.use(Vuex)

function readEmployee() {
  try {
    return JSON.parse(localStorage.getItem('sky_employee') || 'null')
  } catch (error) {
    return null
  }
}

export default new Vuex.Store({
  state: {
    token: localStorage.getItem('sky_admin_token') || '',
    employee: readEmployee()
  },
  mutations: {
    setLogin(state, payload) {
      state.token = payload.token
      state.employee = {
        id: payload.id,
        username: payload.username,
        name: payload.name
      }
      localStorage.setItem('sky_admin_token', state.token)
      localStorage.setItem('sky_employee', JSON.stringify(state.employee))
    },
    clearLogin(state) {
      state.token = ''
      state.employee = null
      localStorage.removeItem('sky_admin_token')
      localStorage.removeItem('sky_employee')
    }
  },
  actions: {
    async login({ commit }, form) {
      const data = await login(form)
      commit('setLogin', data)
      return data
    },
    logout({ commit }) {
      commit('clearLogin')
    }
  }
})
