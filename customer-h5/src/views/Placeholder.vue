<template>
  <div class="placeholder">
    <van-nav-bar :title="$route.meta.title || '页面'" left-arrow @click-left="$router.push('/home')" />

    <main>
      <p class="label">{{ $route.meta.owner || '待分配' }} 模块</p>
      <h1>{{ $route.meta.title }}</h1>
      <p>页面入口和路由已建立，具体业务由对应负责人按接口文档接入。</p>
      <van-button type="info" block @click="$router.push('/home')">返回点餐首页</van-button>
    </main>
  </div>
</template>

<script>
export default {
  name: 'PlaceholderPage'
}
</script>

<style scoped>
.placeholder {
  min-height: 100vh;
}

main {
  padding: 80px 28px;
}

.label {
  margin: 0 0 16px;
  color: #f28a2e;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.12em;
}

h1 {
  margin: 0;
  font-size: 32px;
}

main > p:nth-of-type(2) {
  margin: 18px 0 34px;
  color: #7b8492;
  font-size: 14px;
  line-height: 1.8;
}
</style>
