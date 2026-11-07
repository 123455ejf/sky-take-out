<template>
  <section class="employee-page">
    <div class="page-heading">
      <div>
        <h2>员工账号</h2>
        <p>管理后台登录账号和启用状态。新员工默认密码为 123456。</p>
      </div>
      <el-button type="primary" icon="el-icon-plus" @click="openCreate">新增员工</el-button>
    </div>

    <div class="page-card toolbar">
      <el-input
        v-model.trim="query.name"
        clearable
        placeholder="按员工姓名搜索"
        prefix-icon="el-icon-search"
        @keyup.enter.native="handleSearch"
        @clear="handleSearch"
      />
      <el-button type="primary" plain @click="handleSearch">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <div class="page-card table-card">
      <el-table
        v-loading="loading"
        :data="records"
        row-key="id"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="76" align="center" />
        <el-table-column prop="username" label="用户名" min-width="130" />
        <el-table-column prop="name" label="姓名" min-width="110" />
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column prop="sex" label="性别" width="80" align="center" />
        <el-table-column label="账号状态" width="110" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
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
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
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
      :title="form.id ? '编辑员工' : '新增员工'"
      :visible.sync="dialogVisible"
      width="560px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="88px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model.trim="form.username" maxlength="32" show-word-limit />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model.trim="form.name" maxlength="32" show-word-limit />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model.trim="form.phone" maxlength="11" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="form.sex">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="身份证号" prop="idNumber">
          <el-input v-model.trim="form.idNumber" maxlength="18" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
      </span>
    </el-dialog>
  </section>
</template>

<script>
import {
  changeEmployeeStatus,
  createEmployee,
  getEmployeeById,
  getEmployeePage,
  updateEmployee
} from '../../api/employee'

function createEmptyForm() {
  return {
    id: null,
    username: '',
    name: '',
    phone: '',
    sex: '男',
    idNumber: ''
  }
}

export default {
  name: 'EmployeePage',
  data() {
    return {
      loading: false,
      saving: false,
      dialogVisible: false,
      query: {
        page: 1,
        pageSize: 10,
        name: ''
      },
      total: 0,
      records: [],
      form: createEmptyForm(),
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 32, message: '用户名长度为 3 到 32 位', trigger: 'blur' }
        ],
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
        idNumber: [{ required: true, message: '请输入身份证号', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.loadEmployees()
  },
  methods: {
    async loadEmployees() {
      this.loading = true
      try {
        const data = await getEmployeePage(this.query)
        this.records = data.records || []
        this.total = data.total || 0
      } catch (error) {
        this.$message.error(error.message)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.query.page = 1
      this.loadEmployees()
    },
    resetSearch() {
      this.query.name = ''
      this.handleSearch()
    },
    handlePageChange(page) {
      this.query.page = page
      this.loadEmployees()
    },
    handleSizeChange(pageSize) {
      this.query.pageSize = pageSize
      this.query.page = 1
      this.loadEmployees()
    },
    openCreate() {
      this.form = createEmptyForm()
      this.dialogVisible = true
    },
    async openEdit(row) {
      try {
        this.form = await getEmployeeById(row.id)
        this.dialogVisible = true
      } catch (error) {
        this.$message.error(error.message)
      }
    },
    resetForm() {
      this.form = createEmptyForm()
      if (this.$refs.form) {
        this.$refs.form.clearValidate()
      }
    },
    submitForm() {
      this.$refs.form.validate(async valid => {
        if (!valid || this.saving) return
        this.saving = true
        try {
          if (this.form.id) {
            await updateEmployee(this.form)
            this.$message.success('员工信息已更新')
          } else {
            await createEmployee(this.form)
            this.$message.success('员工已新增，初始密码为 123456')
          }
          this.dialogVisible = false
          this.loadEmployees()
        } catch (error) {
          this.$message.error(error.message)
        } finally {
          this.saving = false
        }
      })
    },
    async toggleStatus(row) {
      const nextStatus = row.status === 1 ? 0 : 1
      const action = nextStatus === 1 ? '启用' : '禁用'
      try {
        await this.$confirm(`确定要${action}员工“${row.name}”吗？`, '确认操作', {
          type: 'warning'
        })
        await changeEmployeeStatus(row.id, nextStatus)
        this.$message.success(`已${action}`)
        this.loadEmployees()
      } catch (error) {
        if (error !== 'cancel' && error !== 'close') {
          this.$message.error(error.message || '操作失败')
        }
      }
    }
  }
}
</script>

<style scoped>
.employee-page {
  max-width: 1420px;
  margin: 0 auto;
}

.page-heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 22px;
}

.page-heading h2 {
  margin: 0;
  font-size: 22px;
}

.page-heading p {
  margin: 8px 0 0;
  color: #7b8492;
  font-size: 13px;
}

.toolbar {
  display: flex;
  gap: 10px;
  padding: 16px 18px;
  margin-bottom: 18px;
}

.toolbar .el-input {
  width: 280px;
}

.table-card {
  padding: 4px 0 18px;
  overflow: hidden;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  padding: 20px 18px 0;
}

.danger {
  color: #d14343;
}

@media (max-width: 700px) {
  .page-heading {
    align-items: flex-start;
    flex-direction: column;
  }

  .toolbar {
    flex-wrap: wrap;
  }
}
</style>
