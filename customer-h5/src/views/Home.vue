<template>
  <div class="home">
    <van-nav-bar title="苍穹外卖" />

    <section class="hero">
      <p class="eyebrow">TODAY'S MENU</p>
      <h1>好好吃饭，<br>认真生活。</h1>
      <p>分类导航、菜品和套餐将由 B、C 模块负责人接入。</p>
    </section>

    <section class="module-list">
      <h2>页面框架已就位</h2>
      <div class="module-row">
        <span class="module-index">01</span>
        <div>
          <strong>菜品展示</strong>
          <p>B 负责人接入分类和菜品接口</p>
        </div>
      </div>
      <div class="module-row">
        <span class="module-index">02</span>
        <div>
          <strong>套餐展示</strong>
          <p>C 负责人接入套餐和套餐菜品接口</p>
        </div>
      </div>
      <div class="module-row">
        <span class="module-index">03</span>
        <div>
          <strong>下单与购物车</strong>
          <p>D、E 负责人接入对应业务</p>
        </div>
      </div>
    </section>

    <van-tabbar route>
      <van-tabbar-item to="/home" icon="shop-o">点餐</van-tabbar-item>
      <van-tabbar-item to="/cart" icon="shopping-cart-o">购物车</van-tabbar-item>
      <van-tabbar-item to="/orders" icon="orders-o">订单</van-tabbar-item>
      <van-tabbar-item to="/address" icon="location-o">地址</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script>
export default {
  name: 'CustomerHome'
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  padding-bottom: 70px;
}

.hero {
  padding: 56px 26px 42px;
  color: #fff;
  background:
    radial-gradient(circle at 85% 10%, rgba(242, 138, 46, 0.4), transparent 36%),
    #202938;
}

.eyebrow {
  margin: 0 0 18px;
  color: #c6ced9;
  font-size: 10px;
  letter-spacing: 0.2em;
}

.hero h1 {
  margin: 0;
  font-size: 34px;
  line-height: 1.35;
}

.hero > p:last-child {
  max-width: 310px;
  margin: 18px 0 0;
  color: #b7c0cc;
  font-size: 13px;
  line-height: 1.7;
}

.module-list {
  padding: 30px 24px;
}

.module-list h2 {
  margin: 0 0 10px;
  font-size: 18px;
}

.module-row {
  display: grid;
  grid-template-columns: 40px 1fr;
  gap: 12px;
  padding: 20px 0;
  border-bottom: 1px solid #e7e9ec;
}

.module-index {
  color: #f28a2e;
  font-size: 12px;
  font-weight: 700;
}

.module-row strong {
  font-size: 15px;
}

.module-row p {
  margin: 6px 0 0;
  color: #7b8492;
  font-size: 12px;
}
</style>
