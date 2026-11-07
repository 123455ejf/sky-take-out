<template>
  <div class="shell">
    <aside class="sidebar">
      <div class="logo">
        <span class="logo-mark">SKY</span>
        <div>
          <strong>苍穹外卖</strong>
          <small>商家管理后台</small>
        </div>
      </div>

      <nav class="nav">
        <router-link class="nav-item active" to="/employee">
          <i class="el-icon-user" />
          <span>员工管理</span>
        </router-link>
      </nav>

      <div class="sidebar-note">
        <span>当前进度</span>
        <strong>A · 员工模块</strong>
        <p>其他模块由对应负责人接入。</p>
      </div>
    </aside>

    <section class="workspace">
      <header class="topbar">
        <div>
          <span class="eyebrow">门店运营</span>
          <h1>{{ $route.meta.title || '管理后台' }}</h1>
        </div>
        <div class="account">
          <span class="avatar">{{ employeeInitial }}</span>
          <div>
            <strong>{{ employeeName }}</strong>
            <small>{{ employee.username }}</small>
          </div>
          <el-button type="text" class="logout" @click="handleLogout">退出</el-button>
        </div>
      </header>

      <main class="content">
        <router-view />
      </main>
    </section>
  </div>
</template>

<script>
import { logout } from '../../api/employee'

export default {
  name: 'AdminLayout',
  computed: {
    employee() {
      return this.$store.state.employee || {}
    },
    employeeName() {
      return this.employee.name || '管理员'
    },
    employeeInitial() {
      return this.employeeName.slice(0, 1)
    }
  },
  methods: {
    async handleLogout() {
      try {
        await logout()
      } catch (error) {
        // 本地登录状态必须清除，服务端 Token 失效失败不阻塞退出。
      }
      await this.$store.dispatch('logout')
      this.$router.replace('/login')
    }
  }
}
</script>

<style scoped>
.shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 248px minmax(0, 1fr);
}

.sidebar {
  position: sticky;
  top: 0;
  height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 28px 20px 24px;
  color: #fff;
  background: #18202d;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 6px 26px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-mark {
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  background: #fff;
  color: #18202d;
  border-radius: 11px;
  font-size: 12px;
  font-weight: 800;
}

.logo strong,
.logo small {
  display: block;
}

.logo strong {
  font-size: 16px;
}

.logo small {
  margin-top: 4px;
  color: #9ba7b8;
  font-size: 11px;
}

.nav {
  margin-top: 24px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 13px 15px;
  color: #b8c1ce;
  border-radius: 10px;
  text-decoration: none;
}

.nav-item.active {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.sidebar-note {
  margin-top: auto;
  padding: 16px;
  color: #9ba7b8;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 12px;
}

.sidebar-note span,
.sidebar-note strong {
  display: block;
}

.sidebar-note span {
  font-size: 11px;
}

.sidebar-note strong {
  margin-top: 7px;
  color: #fff;
  font-size: 14px;
}

.sidebar-note p {
  margin: 8px 0 0;
  font-size: 12px;
  line-height: 1.6;
}

.workspace {
  min-width: 0;
}

.topbar {
  min-height: 92px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 20px 34px;
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1px solid #e5e7eb;
}

.eyebrow {
  color: #8a94a3;
  font-size: 11px;
  letter-spacing: 0.12em;
}

.topbar h1 {
  margin: 5px 0 0;
  font-size: 22px;
}

.account {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  display: grid;
  place-items: center;
  width: 38px;
  height: 38px;
  color: #fff;
  background: #3e4a5d;
  border-radius: 50%;
  font-weight: 700;
}

.account strong,
.account small {
  display: block;
}

.account strong {
  font-size: 13px;
}

.account small {
  margin-top: 3px;
  color: #8a94a3;
  font-size: 11px;
}

.logout {
  margin-left: 8px;
  color: #6b7280;
}

.content {
  padding: 30px 34px 42px;
}

@media (max-width: 900px) {
  .shell {
    grid-template-columns: 86px minmax(0, 1fr);
  }

  .sidebar {
    padding: 20px 12px;
  }

  .logo {
    justify-content: center;
    padding-inline: 0;
  }

  .logo > div,
  .nav-item span,
  .sidebar-note {
    display: none;
  }

  .nav-item {
    justify-content: center;
    font-size: 19px;
  }
}
</style>
