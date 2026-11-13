# B 分类与菜品模块交付说明

## 交付范围

B 模块负责分类与菜品：B 端 11 个管理接口、C 端 2 个浏览接口，B 端「分类管理」「菜品管理」两个页面，以及 H5 首页的分类导航与菜品列表组件。

按 A 已定的工程结构继续开发，接口契约已登记到 `docs/api.md` 的「B 端分类模块」「B 端菜品模块」「C 端分类与菜品浏览」三节。

## 后端文件

- `entity/Category`、`entity/Dish`、`entity/DishFlavor`：三张表的实体，时间与操作人靠 `MyMetaObjectHandler` 自动填充
- `dto/CategoryDTO`、`dto/DishDTO`：新增和修改的入参，带 `javax.validation` 校验
- `dto/CategoryPageQueryDTO`、`dto/DishPageQueryDTO`：分页查询入参
- `vo/DishVO`：菜品列表和详情返回，比实体多 `categoryName` 与 `flavors`
- `mapper/CategoryMapper`、`mapper/DishMapper`、`mapper/DishFlavorMapper`：继承 `BaseMapper`，跨模块的引用校验用 `@Select` 直接查 `setmeal`、`setmeal_dish`，不引 C 的 Mapper
- `service/impl/CategoryServiceImpl`：分类增删改查，删除前校验分类下有无菜品和套餐
- `service/impl/DishServiceImpl`：菜品增删改查、起售停售、C 端列表；口味「先删后插」；删除和停售前校验起售状态与套餐引用
- `controller/CategoryController`、`controller/DishController`：`/admin/category`、`/admin/dish`
- `controller/UserCategoryController`、`controller/UserDishController`：`/user/category`、`/user/dish`

## 前端页面

管理后台：

- `/category`：分类分页、按名称和类型筛选、新增、修改、删除
- `/dish`：菜品分页、按名称/分类/状态筛选、新增、修改、批量删除、起售停售、图片上传、口味动态行

客户 H5：

- `components/CategoryNav.vue`：左侧分类导航
- `components/DishList.vue`：菜品卡片 + 口味规格弹窗，选中后 `$emit('add-cart', { dishId, dishFlavor })`

## 数据库

三张表 `category`、`dish`、`dish_flavor` 在 `sql/init.sql` 中已存在，本模块**未改动表结构**。

`sql/b_seed.sql` 是演示数据（5 个分类、7 道菜、5 条口味），只在需要看效果时执行。`price` 存的是分。

## 改动的已有文件

Vue 的路由入口和 Java 的拦截器配置都只有一处，没法靠新增文件生效，所以下面几个文件需要在原文件里加行（不是重写）。改动量合计不到 40 行。

- `admin-web/src/router/index.js`：`children` 增加 `/category`、`/dish`
- `admin-web/src/views/layout/index.vue`：侧边栏增加两个入口，并把写死的 `nav-item active` 改为按 `$route.path` 判断
- `server/sky-server/.../config/WebMvcConfig.java`：C 端拦截器放行浏览类接口
- `docs/api.md`：登记本模块 13 个接口

### 1. router/index.js

`children` 里 `/employee` 那一项后面补逗号，再插入两条：

```js
      {
        path: 'category',
        name: 'Category',
        component: () => import('../views/category/index.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'dish',
        name: 'Dish',
        component: () => import('../views/dish/index.vue'),
        meta: { title: '菜品管理' }
      },
```

### 2. layout/index.vue

把写死高亮的那句换成按路由判断，否则三个菜单会同时高亮：

```vue
<router-link :class="['nav-item', { active: $route.path === '/employee' }]" to="/employee">
```

在它对应的 `</router-link>` 之后补两个入口：

```vue
<router-link :class="['nav-item', { active: $route.path === '/category' }]" to="/category">
  <i class="el-icon-menu" />
  <span>分类管理</span>
</router-link>
<router-link :class="['nav-item', { active: $route.path === '/dish' }]" to="/dish">
  <i class="el-icon-dish" />
  <span>菜品管理</span>
</router-link>
```

### 3. WebMvcConfig.java

把 `.excludePathPatterns("/user/user/login")` 换成：

```java
.excludePathPatterns(
        "/user/user/login",
        "/user/category/list",
        "/user/dish/list",
        "/user/setmeal/list",
        "/user/setmeal/dish/**"
);
```

这一条属于公共基座，按分工该由 E 改。不改的话客户未登录打开 H5 会被 401 踢去登录页。

## 自测清单

- Knife4j 出现「B端分类接口」「B端菜品接口」「C端分类接口」「C端菜品接口」四组
- 新增同名同类型分类返回「同类型下分类名称已存在」，同名不同类型可以共存
- 菜品带两组口味新增后，详情 `flavors` 长度为 2
- 编辑菜品删一组加一组后，口味数量仍为 2（先删后插生效）
- 起售中的菜品删除被拒绝
- 被起售套餐关联的菜品停售被拒绝
- 分类下还有菜品时删除分类被拒绝
- 菜品图片上传后能在表单预览，地址为 MinIO 返回的 url
- 未登录访问 `/user/dish/list` 能返回数据（依赖 C 端拦截器放行）
- 后台侧边栏两个菜单可切换，高亮正确

## 接口约定提醒

- `price` 单位是分，前端按元填写、提交时乘 100。全项目统一，报表和订单金额都依赖这条。
- 菜品删除是批量接口，`ids` 逗号分隔。
- 菜品分页返回 `categoryName` 是新增字段，C 的套餐页和 D 的订单页可以直接用。
- C 端浏览接口免登录；加购、下单仍然要 token。
- 加购接口 `POST /user/shoppingCart` 不属于本模块：`DishList.vue` 只抛出 `add-cart` 事件，由首页调用。
