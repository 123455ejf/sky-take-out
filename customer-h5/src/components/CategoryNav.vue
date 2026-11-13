<template>
  <van-sidebar v-if="categories.length" class="category-nav" :active-key="activeIndex" @change="handleChange">
    <van-sidebar-item v-for="item in categories" :key="item.id" :title="item.name" />
  </van-sidebar>
  <div v-else class="category-nav category-empty">暂无分类</div>
</template>

<script>
import { getCategoryList } from '../api/menu'

export default {
  name: 'CategoryNav',
  data() {
    return {
      categories: [],
      activeIndex: 0
    }
  },
  async created() {
    try {
      this.categories = (await getCategoryList(1)) || []
      if (this.categories.length) {
        this.$emit('change', this.categories[0].id)
      }
    } catch (e) {
      this.categories = []
      this.$toast(e.message || '分类加载失败')
    }
  },
  methods: {
    handleChange(index) {
      this.activeIndex = index
      const category = this.categories[index]
      if (category) {
        this.$emit('change', category.id)
      }
    }
  }
}
</script>

<style scoped>
.category-nav {
  width: 92px;
  flex: 0 0 92px;
  height: 100%;
  background: #f7f8fa;
}

.category-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #969799;
  font-size: 12px;
}
</style>
