<template>
  <section class="dish-page">
    <div class="page-heading">
      <div>
        <h2>菜品管理</h2>
        <p>价格按「元」填写，保存时自动换算成分；起售中的菜品和被套餐关联的菜品不能删除。</p>
      </div>
      <el-button type="primary" icon="el-icon-plus" @click="openCreate">新增菜品</el-button>
    </div>

    <div class="page-card toolbar">
      <el-input
        v-model.trim="query.name"
        clearable
        placeholder="按菜品名称搜索"
        prefix-icon="el-icon-search"
        style="max-width: 220px"
        @keyup.enter.native="handleSearch"
        @clear="handleSearch"
      />
      <el-select v-model="query.categoryId" clearable placeholder="全部分类" style="width: 160px">
        <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-select v-model="query.status" clearable placeholder="全部状态" style="width: 140px">
        <el-option label="起售" :value="1" />
        <el-option label="停售" :value="0" />
      </el-select>
      <el-button type="primary" plain @click="handleSearch">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
      <el-button
        type="danger"
        plain
        icon="el-icon-delete"
        :disabled="!selectedIds.length"
        @click="handleBatchDelete"
      >批量删除</el-button>
    </div>

    <div class="page-card table-card">
      <el-table
        v-loading="loading"
        :data="records"
        row-key="id"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="46" align="center" />
        <el-table-column label="图片" width="92" align="center">
          <template slot-scope="{ row }">
            <el-image v-if="row.image" :src="row.image" fit="cover" class="dish-thumb" />
            <span v-else class="no-image">无图</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="菜品名称" min-width="140" />
        <el-table-column prop="categoryName" label="分类" width="120" align="center" />
        <el-table-column label="价格" width="100" align="center">
          <template slot-scope="{ row }">{{ formatPrice(row.price) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '起售' : '停售' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" min-width="170" />
        <el-table-column label="操作" width="190" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" @click="openEdit(row)">编辑</el-button>
            <el-button
              type="text"
              :class="{ danger: row.status === 1 }"
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '停售' : '起售' }}
            </el-button>
            <el-button type="text" class="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next"
          :current-page="query.page"
          :page-size="query.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <el-dialog
      :title="form.id ? '编辑菜品' : '新增菜品'"
      :visible.sync="dialogVisible"
      width="680px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="88px">
        <el-form-item label="菜品名称" prop="name">
          <el-input v-model.trim="form.name" maxlength="64" show-word-limit />
        </el-form-item>
        <el-form-item label="菜品分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格（元）" prop="priceYuan">
          <el-input-number
            v-model="form.priceYuan"
            :min="0"
            :max="99999"
            :precision="2"
            :step="1"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="菜品图片">
          <el-upload
            class="dish-uploader"
            :show-file-list="false"
            :http-request="handleUpload"
            :before-upload="beforeUpload"
            accept="image/*"
          >
            <img v-if="form.image" :src="form.image" class="uploader-image" alt="">
            <div v-else class="uploader-placeholder">
              <i :class="uploading ? 'el-icon-loading' : 'el-icon-plus'" />
              <span>上传图片</span>
            </div>
          </el-upload>
          <div class="hint">走公共接口 /admin/common/upload（MinIO），支持 jpg/png，≤ 2MB。</div>
        </el-form-item>
        <el-form-item label="口味">
          <div v-for="(flavor, index) in form.flavors" :key="index" class="flavor-row">
            <el-input
              v-model.trim="flavor.name"
              placeholder="口味名，如 辣度"
              style="width: 150px"
            />
            <el-input
              v-model.trim="flavor.value"
              placeholder="可选值，逗号分隔：微辣,中辣,特辣"
              style="width: 320px"
            />
            <el-button type="text" class="danger" @click="form.flavors.splice(index, 1)">
              删除
            </el-button>
          </div>
          <el-button type="text" icon="el-icon-plus" @click="addFlavor">添加口味</el-button>
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model.trim="form.description"
            type="textarea"
            :rows="2"
            maxlength="255"
            show-word-limit
            placeholder="一句话描述这道菜"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">起售</el-radio>
            <el-radio :label="0">停售</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
      </span>
    </el-dialog>
  </section>
</template>

<script>
import {
  getDishPage,
  getDishById,
  createDish,
  updateDish,
  deleteDish,
  changeDishStatus,
  uploadDishImage
} from '../../api/dish'
import { getCategoryList } from '../../api/category'

function emptyForm() {
  return {
    id: null,
    name: '',
    categoryId: null,
    priceYuan: 0,
    image: '',
    description: '',
    status: 1,
    flavors: []
  }
}

export default {
  name: 'DishManage',
  data() {
    return {
      loading: false,
      submitting: false,
      uploading: false,
      dialogVisible: false,
      query: { page: 1, pageSize: 10, name: '', categoryId: null, status: null },
      records: [],
      total: 0,
      categories: [],
      selectedIds: [],
      form: emptyForm(),
      rules: {
        name: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
        categoryId: [{ required: true, message: '请选择菜品分类', trigger: 'change' }],
        priceYuan: [{ required: true, message: '请输入菜品价格', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.loadCategories()
    this.loadList()
  },
  methods: {
    formatPrice(price) {
      return `￥${((price || 0) / 100).toFixed(2)}`
    },
    async loadCategories() {
      try {
        this.categories = (await getCategoryList(1)) || []
      } catch (e) {
        this.categories = []
      }
    },
    async loadList() {
      this.loading = true
      try {
        const page = await getDishPage(this.query)
        this.records = (page && page.records) || []
        this.total = (page && page.total) || 0
      } catch (e) {
        this.records = []
        this.total = 0
        this.$message.error(e.message || '查询失败')
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.query.page = 1
      this.loadList()
    },
    resetSearch() {
      this.query = { page: 1, pageSize: 10, name: '', categoryId: null, status: null }
      this.loadList()
    },
    handlePageChange(page) {
      this.query.page = page
      this.loadList()
    },
    handleSizeChange(size) {
      this.query.pageSize = size
      this.query.page = 1
      this.loadList()
    },
    handleSelectionChange(rows) {
      this.selectedIds = rows.map(row => row.id)
    },
    openCreate() {
      this.form = emptyForm()
      this.dialogVisible = true
    },
    async openEdit(row) {
      try {
        const detail = await getDishById(row.id)
        this.form = {
          id: detail.id,
          name: detail.name,
          categoryId: detail.categoryId,
          priceYuan: (detail.price || 0) / 100,
          image: detail.image || '',
          description: detail.description || '',
          status: detail.status,
          flavors: (detail.flavors || []).map(item => ({ name: item.name, value: item.value }))
        }
        this.dialogVisible = true
      } catch (e) {
        this.$message.error(e.message || '查询详情失败')
      }
    },
    resetForm() {
      this.form = emptyForm()
      if (this.$refs.form) {
        this.$refs.form.clearValidate()
      }
    },
    addFlavor() {
      this.form.flavors.push({ name: '', value: '' })
    },
    beforeUpload(file) {
      if (!/^image\//.test(file.type)) {
        this.$message.error('只能上传图片文件')
        return false
      }
      if (file.size / 1024 / 1024 > 2) {
        this.$message.error('图片不能超过 2MB')
        return false
      }
      return true
    },
    async handleUpload({ file }) {
      this.uploading = true
      try {
        const url = await uploadDishImage(file)
        this.form.image = url
        this.$message.success('上传成功')
      } catch (e) {
        this.$message.error(e.message || '上传失败')
      } finally {
        this.uploading = false
      }
    },
    submit() {
      this.$refs.form.validate(async valid => {
        if (!valid) {
          return
        }
        this.submitting = true
        try {
          const body = {
            id: this.form.id,
            name: this.form.name,
            categoryId: this.form.categoryId,
            price: Math.round((Number(this.form.priceYuan) || 0) * 100),
            image: this.form.image,
            description: this.form.description,
            status: this.form.status,
            flavors: this.form.flavors.filter(item => item.name && item.value)
          }
          if (body.id) {
            await updateDish(body)
            this.$message.success('修改成功')
          } else {
            await createDish(body)
            this.$message.success('新增成功')
          }
          this.dialogVisible = false
          this.loadList()
        } catch (e) {
          this.$message.error(e.message || '保存失败')
        } finally {
          this.submitting = false
        }
      })
    },
    async toggleStatus(row) {
      const target = row.status === 1 ? 0 : 1
      try {
        await changeDishStatus(row.id, target)
        this.$message.success(target === 1 ? '已起售' : '已停售')
      } catch (e) {
        this.$message.error(e.message || '状态修改失败')
      } finally {
        this.loadList()
      }
    },
    handleDelete(row) {
      this.$confirm(`确认删除菜品「${row.name}」吗？`, '提示', {
        type: 'warning',
        confirmButtonText: '删除',
        cancelButtonText: '取消'
      })
        .then(async () => {
          try {
            await deleteDish(String(row.id))
            this.$message.success('删除成功')
            this.loadList()
          } catch (e) {
            this.$message.error(e.message || '删除失败')
          }
        })
        .catch(() => {})
    },
    handleBatchDelete() {
      this.$confirm(`确认删除选中的 ${this.selectedIds.length} 个菜品吗？`, '提示', {
        type: 'warning',
        confirmButtonText: '删除',
        cancelButtonText: '取消'
      })
        .then(async () => {
          try {
            await deleteDish(this.selectedIds.join(','))
            this.$message.success('删除成功')
            this.loadList()
          } catch (e) {
            this.$message.error(e.message || '删除失败')
          }
        })
        .catch(() => {})
    }
  }
}
</script>

<style scoped>
.dish-page {
  max-width: 1420px;
  margin: 0 auto;
}

.page-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.page-heading h2 {
  margin: 0;
  font-size: 24px;
}

.page-heading p {
  margin: 8px 0 0;
  color: var(--sky-muted, #7b8492);
  font-size: 13px;
}

.page-card {
  padding: 16px 18px;
  margin-bottom: 16px;
  background: #fff;
  border: 1px solid var(--sky-line, #e7e9ec);
  border-radius: 12px;
}

.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}

.table-card {
  padding-bottom: 10px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  padding: 14px 0 6px;
}

.dish-thumb {
  width: 56px;
  height: 42px;
  border-radius: 6px;
}

.no-image {
  color: #c0c4cc;
  font-size: 12px;
}

.flavor-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.uploader-image {
  width: 120px;
  height: 90px;
  object-fit: cover;
  border-radius: 8px;
}

.uploader-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 90px;
  color: #8c939d;
  font-size: 12px;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
}

.uploader-placeholder i {
  margin-bottom: 6px;
  font-size: 20px;
}

.hint {
  margin-top: 4px;
  color: var(--sky-muted, #7b8492);
  font-size: 12px;
}

.danger {
  color: #f56c6c;
}
</style>
