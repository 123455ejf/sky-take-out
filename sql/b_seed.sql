-- ============================================================
-- B 模块演示数据（分类 / 菜品 / 口味）
-- 用法：建库建表之后（sql/init.sql 已含三张表结构），再执行本文件
-- 注意：price 单位是「分」，600 表示 6.00 元
-- 如果重复执行会插入重复数据，先清空再跑：
--   DELETE FROM dish_flavor; DELETE FROM dish; DELETE FROM category;
-- ============================================================

USE sky_take_out;

INSERT INTO category (type, name, sort, status, create_time, update_time) VALUES
(1, '酒水饮料', 1, 1, NOW(), NOW()),
(1, '传统主食', 2, 1, NOW(), NOW()),
(1, '川湘菜',   3, 1, NOW(), NOW()),
(2, '人气套餐', 1, 1, NOW(), NOW()),
(2, '商务套餐', 2, 1, NOW(), NOW());

INSERT INTO dish (name, category_id, price, image, description, status, create_time, update_time) VALUES
('冰镇可乐', (SELECT id FROM category WHERE name = '酒水饮料' AND type = 1), 600,  NULL, '冰爽解渴', 1, NOW(), NOW()),
('鲜榨橙汁', (SELECT id FROM category WHERE name = '酒水饮料' AND type = 1), 1200, NULL, '当日鲜榨', 1, NOW(), NOW()),
('扬州炒饭', (SELECT id FROM category WHERE name = '传统主食' AND type = 1), 1800, NULL, '粒粒分明', 1, NOW(), NOW()),
('葱油拌面', (SELECT id FROM category WHERE name = '传统主食' AND type = 1), 1500, NULL, '葱香浓郁', 1, NOW(), NOW()),
('鱼香肉丝', (SELECT id FROM category WHERE name = '川湘菜'   AND type = 1), 3800, NULL, '经典川菜', 1, NOW(), NOW()),
('水煮牛肉', (SELECT id FROM category WHERE name = '川湘菜'   AND type = 1), 5800, NULL, '麻辣鲜香', 1, NOW(), NOW()),
('麻婆豆腐', (SELECT id FROM category WHERE name = '川湘菜'   AND type = 1), 2800, NULL, '停售状态示例', 0, NOW(), NOW());

INSERT INTO dish_flavor (dish_id, name, value)
SELECT id, '温度', '常温,冰镇' FROM dish WHERE name = '冰镇可乐'
UNION ALL SELECT id, '温度', '常温,冰镇' FROM dish WHERE name = '鲜榨橙汁'
UNION ALL SELECT id, '辣度', '微辣,中辣,特辣' FROM dish WHERE name = '鱼香肉丝'
UNION ALL SELECT id, '忌口', '不要香菜,不要葱' FROM dish WHERE name = '鱼香肉丝'
UNION ALL SELECT id, '辣度', '微辣,中辣,特辣' FROM dish WHERE name = '水煮牛肉';

-- 图片先留空，联调时在「菜品管理」页用 /admin/common/upload 传一张，MinIO 已通
