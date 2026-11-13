<template>
  <section class="category-page">
    <div class="page-heading">
      <div>
        <h2>分类管理</h2>
        <p>菜品分类决定 H5 首页左侧导航，套餐分类供 C 模块使用；排序数字越小越靠前。</p>
      </div>
      <el-button type="primary" icon="el-icon-plus" @click="openCreate">新增分类</el-button>
    </div>

    <div class="page-card toolbar">
      <el-input
        v-model.trim="query.name"
        clearable
        placeholder="按分类名称搜索"
        prefix-icon="el-icon-search"
        style="max-width: 240px"
        @keyup.enter.native="handleSearch"
        @clear="handleSearch"
      />
      <el-select v-model="query.type" clearable placeholder="全部类型" style="width: 150px">
        <el-option label="菜品分类" :value="1" />
        <el-option label="套餐分类" :value="2" />
      </el-select>
      <el-button type="primary" plain @click="handleSearch">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <div class="page-card table-card">
      <el-table v-loading="loading" :data="records" row-key="id" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="76" align="center" />
        <el-table-column prop="name" label="分类名称" min-width="160" />
        <el-table-column label="类型" width="120" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.type === 1 ? '' : 'success'" size="small">
              {{ row.type === 1 ? '菜品分类' : '套餐分类' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="90" align="center" />
        <el-table-column prop="updateTime" label="更新时间" min-width="170" />
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" @click="openEdit(row)">编辑</el-button>
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
      :title="form.id ? '编辑分类' : '新增分类'"
      :visible.sync="dialogVisible"
      width="460px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="88px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model.trim="form.name" maxlength="32" show-word-limit />
        </el-form-item>
        <el-form-item label="分类类型" prop="type">
          <el-radio-group v-model="form.type" :disabled="!!form.id">
            <el-radio :label="1">菜品分类</el-radio>
            <el-radio :label="2">套餐分类</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" controls-position="right" />
          <span class="hint">数字越小越靠前</span>
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
  getCategoryPage,
  createCategory,
  updateCategory,
  deleteCategory
} from '../../api/category'

function emptyForm() {
  return { id: null, name: '', type: 1, sort: 0 }
}

export default {
  name: 'CategoryManage',
  data() {
    return {
      loading: false,
      submitting: false,
      dialogVisible: false,
      query: { page: 1, pageSize: 10, name: '', type: null },
      records: [],
      total: 0,
      form: emptyForm(),
      rules: {
        name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择分类类型', trigger: 'change' }]
      }
    }
  },
  created() {
    this.loadList()
  },
  methods: {
    async loadList() {
      this.loading = true
      try {
        const page = await getCategoryPage(this.query)
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
      this.query = { page: 1, pageSize: 10, name: '', type: null }
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
    openCreate() {
      this.form = emptyForm()
      this.dialogVisible = true
    },
    openEdit(row) {
      this.form = {
        id: row.id,
        name: row.name,
        type: row.type,
        sort: row.sort == null ? 0 : row.sort
      }
      this.dialogVisible = true
    },
    resetForm() {
      this.form = emptyForm()
      if (this.$refs.form) {
        this.$refs.form.clearValidate()
      }
    },
    submit() {
      this.$refs.form.validate(async valid => {
        if (!valid) {
          return
        }
        this.submitting = true
        try {
          if (this.form.id) {
            await updateCategory(this.form)
            this.$message.success('修改成功')
          } else {
            await createCategory(this.form)
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
    handleDelete(row) {
      this.$confirm(`确认删除分类「${row.name}」吗？`, '提示', {
        type: 'warning',
        confirmButtonText: '删除',
        cancelButtonText: '取消'
      })
        .then(async () => {
          try {
            await deleteCategory(row.id)
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
.category-page {
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

.hint {
  margin-left: 10px;
  color: var(--sky-muted, #7b8492);
  font-size: 12px;
}

.danger {
  color: #f56c6c;
}
</style>
