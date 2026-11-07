CREATE DATABASE IF NOT EXISTS sky_take_out
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE sky_take_out;

CREATE TABLE IF NOT EXISTS employee (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    username VARCHAR(32) NOT NULL COMMENT '用户名',
    name VARCHAR(32) NOT NULL COMMENT '姓名',
    password VARCHAR(100) NOT NULL COMMENT 'BCrypt 密码',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    sex VARCHAR(8) NOT NULL COMMENT '性别',
    id_number VARCHAR(32) NOT NULL COMMENT '身份证号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1 启用，0 禁用',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    create_user BIGINT DEFAULT NULL COMMENT '创建人',
    update_user BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_employee_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

CREATE TABLE IF NOT EXISTS category (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    type TINYINT NOT NULL COMMENT '1 菜品分类，2 套餐分类',
    name VARCHAR(32) NOT NULL COMMENT '分类名称',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1 启用，0 禁用',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    create_user BIGINT DEFAULT NULL,
    update_user BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_category_name_type (name, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

CREATE TABLE IF NOT EXISTS dish (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    name VARCHAR(64) NOT NULL COMMENT '菜品名称',
    category_id BIGINT NOT NULL COMMENT '分类 ID',
    price DECIMAL(10, 2) NOT NULL COMMENT '价格，单位分',
    image VARCHAR(255) DEFAULT NULL COMMENT '图片地址',
    description VARCHAR(255) DEFAULT NULL COMMENT '描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1 起售，0 停售',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    create_user BIGINT DEFAULT NULL,
    update_user BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_dish_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

CREATE TABLE IF NOT EXISTS dish_flavor (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    dish_id BIGINT NOT NULL COMMENT '菜品 ID',
    name VARCHAR(32) NOT NULL COMMENT '口味名称',
    value VARCHAR(255) NOT NULL COMMENT '口味值，逗号分隔',
    PRIMARY KEY (id),
    KEY idx_flavor_dish (dish_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品口味表';

CREATE TABLE IF NOT EXISTS setmeal (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    category_id BIGINT NOT NULL COMMENT '分类 ID',
    name VARCHAR(64) NOT NULL COMMENT '套餐名称',
    price DECIMAL(10, 2) NOT NULL COMMENT '价格，单位分',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1 起售，0 停售',
    description VARCHAR(255) DEFAULT NULL COMMENT '描述',
    image VARCHAR(255) DEFAULT NULL COMMENT '图片地址',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    create_user BIGINT DEFAULT NULL,
    update_user BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_setmeal_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐表';

CREATE TABLE IF NOT EXISTS setmeal_dish (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    setmeal_id BIGINT NOT NULL COMMENT '套餐 ID',
    dish_id BIGINT NOT NULL COMMENT '菜品 ID',
    name VARCHAR(64) NOT NULL COMMENT '菜品名称快照',
    price DECIMAL(10, 2) NOT NULL COMMENT '菜品价格快照',
    copies INT NOT NULL DEFAULT 1 COMMENT '份数',
    PRIMARY KEY (id),
    KEY idx_setmeal_dish_setmeal (setmeal_id),
    KEY idx_setmeal_dish_dish (dish_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐菜品关联表';

CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    name VARCHAR(32) DEFAULT NULL COMMENT '姓名',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

CREATE TABLE IF NOT EXISTS shopping_cart (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '客户 ID',
    dish_id BIGINT DEFAULT NULL COMMENT '菜品 ID',
    setmeal_id BIGINT DEFAULT NULL COMMENT '套餐 ID',
    dish_flavor VARCHAR(64) DEFAULT NULL COMMENT '菜品口味',
    number INT NOT NULL DEFAULT 1 COMMENT '数量',
    amount DECIMAL(10, 2) NOT NULL COMMENT '单价快照，单位分',
    name VARCHAR(64) NOT NULL COMMENT '商品名称快照',
    image VARCHAR(255) DEFAULT NULL COMMENT '图片快照',
    create_time DATETIME NOT NULL,
    PRIMARY KEY (id),
    KEY idx_cart_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

CREATE TABLE IF NOT EXISTS address_book (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '客户 ID',
    consignee VARCHAR(32) NOT NULL COMMENT '收货人',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    sex VARCHAR(8) DEFAULT NULL COMMENT '性别',
    province_code VARCHAR(16) DEFAULT NULL,
    province_name VARCHAR(32) DEFAULT NULL,
    city_code VARCHAR(16) DEFAULT NULL,
    city_name VARCHAR(32) DEFAULT NULL,
    district_code VARCHAR(16) DEFAULT NULL,
    district_name VARCHAR(32) DEFAULT NULL,
    detail VARCHAR(255) NOT NULL COMMENT '详细地址',
    label VARCHAR(32) DEFAULT NULL COMMENT '标签',
    is_default TINYINT NOT NULL DEFAULT 0 COMMENT '1 默认，0 非默认',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    PRIMARY KEY (id),
    KEY idx_address_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地址簿表';

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    number VARCHAR(32) NOT NULL COMMENT '订单号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1 待接单，2 已接单，3 派送中，4 已完成，5 已取消',
    user_id BIGINT NOT NULL COMMENT '客户 ID',
    address_book_id BIGINT NOT NULL COMMENT '地址 ID',
    pay_status TINYINT NOT NULL DEFAULT 0 COMMENT '0 未支付，1 已支付，2 退款',
    pay_method TINYINT NOT NULL DEFAULT 1 COMMENT '1 模拟支付',
    amount DECIMAL(10, 2) NOT NULL COMMENT '订单金额，单位分',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话快照',
    address VARCHAR(255) NOT NULL COMMENT '地址快照',
    consignee VARCHAR(32) NOT NULL COMMENT '收货人快照',
    estimate_delivery_time DATETIME DEFAULT NULL COMMENT '预计送达时间',
    delivery_status TINYINT DEFAULT NULL COMMENT '配送状态',
    order_time DATETIME NOT NULL COMMENT '下单时间',
    checkout_time DATETIME DEFAULT NULL COMMENT '结账时间',
    cancel_reason VARCHAR(255) DEFAULT NULL,
    rejection_reason VARCHAR(255) DEFAULT NULL,
    cancel_time DATETIME DEFAULT NULL,
    delivery_time DATETIME DEFAULT NULL,
    pack_amount DECIMAL(10, 2) DEFAULT 0 COMMENT '打包费',
    tableware_number INT DEFAULT NULL COMMENT '餐具数量',
    tableware_status TINYINT DEFAULT NULL COMMENT '餐具状态',
    PRIMARY KEY (id),
    UNIQUE KEY uk_orders_number (number),
    KEY idx_orders_user (user_id),
    KEY idx_orders_status_time (status, order_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE IF NOT EXISTS order_detail (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    name VARCHAR(64) NOT NULL COMMENT '商品名称快照',
    image VARCHAR(255) DEFAULT NULL COMMENT '图片快照',
    order_id BIGINT NOT NULL COMMENT '订单 ID',
    dish_id BIGINT DEFAULT NULL COMMENT '菜品 ID',
    setmeal_id BIGINT DEFAULT NULL COMMENT '套餐 ID',
    dish_flavor VARCHAR(64) DEFAULT NULL COMMENT '口味',
    number INT NOT NULL DEFAULT 1 COMMENT '数量',
    amount DECIMAL(10, 2) NOT NULL COMMENT '单价快照，单位分',
    PRIMARY KEY (id),
    KEY idx_order_detail_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';
