<template>
  <main class="login-page">
    <section class="login-panel">
      <div class="brand">
        <div class="brand-mark">SKY</div>
        <p>门店管理系统</p>
      </div>

      <div class="copy">
        <h1>苍穹外卖</h1>
        <p>登录后管理员工、菜品和门店订单。</p>
      </div>

      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-position="top"
        @keyup.enter.native="submit"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model.trim="form.username"
            size="large"
            autocomplete="username"
            placeholder="请输入用户名"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            size="large"
            type="password"
            autocomplete="current-password"
            show-password
            placeholder="请输入密码"
          />
        </el-form-item>
        <el-button
          class="login-button"
          type="primary"
          size="large"
          :loading="loading"
          @click="submit"
        >
          登录管理后台
        </el-button>
      </el-form>

      <p class="hint">默认账号：admin　密码：123456</p>
    </section>
  </main>
</template>

<script>
export default {
  name: 'LoginView',
  data() {
    return {
      loading: false,
      form: {
        username: 'admin',
        password: '123456'
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    submit() {
      this.$refs.form.validate(async valid => {
        if (!valid || this.loading) return
        this.loading = true
        try {
          await this.$store.dispatch('login', this.form)
          this.$message.success('登录成功')
          this.$router.replace('/employee')
        } catch (error) {
          this.$message.error(error.message)
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 32px;
  background:
    radial-gradient(circle at 14% 18%, rgba(255, 255, 255, 0.14), transparent 28%),
    linear-gradient(135deg, #111827 0%, #273245 58%, #3c4a5f 100%);
}

.login-panel {
  width: min(440px, 100%);
  padding: 44px 46px 36px;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.28);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #6b7280;
  font-size: 13px;
  letter-spacing: 0.08em;
}

.brand-mark {
  display: grid;
  place-items: center;
  width: 44px;
  height: 44px;
  color: #fff;
  background: #202938;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 700;
}

.copy {
  margin: 32px 0 30px;
}

.copy h1 {
  margin: 0 0 10px;
  color: #161b22;
  font-size: 30px;
  line-height: 1.25;
}

.copy p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.login-button {
  width: 100%;
  margin-top: 8px;
}

.hint {
  margin: 24px 0 0;
  color: #9ca3af;
  font-size: 12px;
  text-align: center;
}
</style>
