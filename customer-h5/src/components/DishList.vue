<template>
  <div class="dish-list">
    <div v-if="!list.length" class="empty">该分类暂无在售菜品</div>

    <div v-for="dish in list" :key="dish.id" class="dish-card" @click="openSpec(dish)">
      <div class="thumb">
        <img v-if="dish.image" :src="dish.image" alt="">
        <div v-else class="thumb-placeholder">暂无图片</div>
      </div>
      <div class="info">
        <div class="name">{{ dish.name }}</div>
        <div class="desc">{{ dish.description }}</div>
        <div class="bottom">
          <span class="price">￥{{ formatPrice(dish.price) }}</span>
          <van-button size="mini" type="danger" round @click.stop="openSpec(dish)">
            {{ hasFlavor(dish) ? '选规格' : '加入' }}
          </van-button>
        </div>
      </div>
    </div>

    <van-popup v-model="show" position="bottom" round closeable>
      <div class="spec-panel">
        <div class="spec-title">{{ current.name }}</div>

        <div v-for="flavor in current.flavors || []" :key="flavor.name" class="spec-group">
          <div class="spec-label">{{ flavor.name }}</div>
          <div class="spec-values">
            <span
              v-for="value in splitValue(flavor.value)"
              :key="value"
              :class="['spec-tag', { active: selected[flavor.name] === value }]"
              @click="$set(selected, flavor.name, value)"
            >{{ value }}</span>
          </div>
        </div>

        <div v-if="!current.flavors || !current.flavors.length" class="spec-note">
          这道菜不需要选择规格
        </div>

        <van-button type="danger" block round @click="confirmAdd">加入购物车</van-button>
      </div>
    </van-popup>
  </div>
</template>

<script>
import { getDishList } from '../api/menu'

export default {
  name: 'DishList',
  props: {
    categoryId: {
      type: [Number, String],
      default: null
    }
  },
  data() {
    return {
      list: [],
      show: false,
      current: {},
      selected: {}
    }
  },
  watch: {
    categoryId: {
      immediate: true,
      handler() {
        this.loadList()
      }
    }
  },
  methods: {
    formatPrice(price) {
      return ((price || 0) / 100).toFixed(2)
    },
    async loadList() {
      if (!this.categoryId) {
        this.list = []
        return
      }
      try {
        this.list = (await getDishList(this.categoryId)) || []
      } catch (e) {
        this.list = []
        this.$toast(e.message || '菜品加载失败')
      }
    },
    hasFlavor(dish) {
      return !!(dish.flavors && dish.flavors.length)
    },
    splitValue(value) {
      return (value || '').split(',').filter(item => item.trim())
    },
    openSpec(dish) {
      if (!this.hasFlavor(dish)) {
        // 无口味直接加购：接口归 E，这里只把参数抛出去
        this.$emit('add-cart', { dishId: dish.id, dishFlavor: '' })
        return
      }
      this.current = dish
      this.selected = {}
      this.show = true
    },
    confirmAdd() {
      const flavors = this.current.flavors || []
      const missing = flavors.find(flavor => !this.selected[flavor.name])
      if (missing) {
        this.$toast(`请选择${missing.name}`)
        return
      }
      const dishFlavor = flavors.map(flavor => this.selected[flavor.name]).join(',')
      this.$emit('add-cart', { dishId: this.current.id, dishFlavor })
      this.show = false
    }
  }
}
</script>

<style scoped>
.dish-list {
  flex: 1;
  min-width: 0;
  padding: 8px 12px 80px;
  overflow-y: auto;
}

.empty {
  padding: 48px 0;
  color: #969799;
  font-size: 13px;
  text-align: center;
}

.dish-card {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f2f3f5;
}

.thumb {
  flex: 0 0 88px;
  width: 88px;
  height: 88px;
  margin-right: 12px;
  overflow: hidden;
  border-radius: 8px;
}

.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumb-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: #c8c9cc;
  font-size: 12px;
  background: #f7f8fa;
}

.info {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
  justify-content: space-between;
}

.name {
  font-size: 15px;
  font-weight: 500;
  color: #323233;
}

.desc {
  margin-top: 2px;
  color: #969799;
  font-size: 12px;
}

.bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.price {
  color: #ee0a24;
  font-size: 16px;
  font-weight: 500;
}

.spec-panel {
  padding: 22px 16px 28px;
}

.spec-title {
  margin-bottom: 16px;
  font-size: 17px;
  font-weight: 500;
}

.spec-group {
  margin-bottom: 16px;
}

.spec-label {
  margin-bottom: 8px;
  color: #646566;
  font-size: 13px;
}

.spec-tag {
  display: inline-block;
  padding: 5px 14px;
  margin: 0 8px 8px 0;
  color: #323233;
  font-size: 13px;
  background: #f7f8fa;
  border-radius: 14px;
}

.spec-tag.active {
  color: #ee0a24;
  background: #ffe1e1;
}

.spec-note {
  margin-bottom: 18px;
  color: #969799;
  font-size: 12px;
}
</style>
