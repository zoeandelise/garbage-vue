-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('垃圾分类管理', '0', '10', 'garbage', null, 1, 0, 'M', '0', '0', '', 'tree', 'admin', sysdate(), '', null, '垃圾分类管理菜单');

-- 二级菜单
INSERT INTO sys_menu(menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES ('垃圾投递记录', (SELECT menu_id FROM sys_menu WHERE menu_name = '垃圾分类管理'), 1, 'record', 'garbage/record/index', 1, 0, 'C', '0', '0', 'garbage:record:list', 'form', 'admin', sysdate(), '', null, '垃圾投递记录菜单');

INSERT INTO sys_menu(menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES ('积分管理', (SELECT menu_id FROM sys_menu WHERE menu_name = '垃圾分类管理'), 2, 'points', 'garbage/points/index', 1, 0, 'C', '0', '0', 'garbage:points:list', 'money', 'admin', sysdate(), '', null, '积分管理菜单');

INSERT INTO sys_menu(menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES ('垃圾分类指南', (SELECT menu_id FROM sys_menu WHERE menu_name = '垃圾分类管理'), 3, 'guide', 'garbage/guide/index', 1, 0, 'C', '0', '0', 'garbage:guide:list', 'guide', 'admin', sysdate(), '', null, '垃圾分类指南菜单');

INSERT INTO sys_menu(menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES ('数据统计分析', (SELECT menu_id FROM sys_menu WHERE menu_name = '垃圾分类管理'), 4, 'statistics', 'garbage/statistics/index', 1, 0, 'C', '0', '0', 'garbage:statistics:view', 'chart', 'admin', sysdate(), '', null, '数据统计分析菜单');

-- 垃圾投递记录按钮
INSERT INTO sys_menu(menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES ('垃圾投递记录查询', (SELECT menu_id FROM sys_menu WHERE menu_name = '垃圾投递记录'), 1, '#', '', 1, 0, 'F', '0', '0', 'garbage:record:query', '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu(menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES ('垃圾投递记录新增', (SELECT menu_id FROM sys_menu WHERE menu_name = '垃圾投递记录'), 2, '#', '', 1, 0, 'F', '0', '0', 'garbage:record:add', '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu(menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES ('垃圾投递记录修改', (SELECT menu_id FROM sys_menu WHERE menu_name = '垃圾投递记录'), 3, '#', '', 1, 0, 'F', '0', '0', 'garbage:record:edit', '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu(menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES ('垃圾投递记录删除', (SELECT menu_id FROM sys_menu WHERE menu_name = '垃圾投递记录'), 4, '#', '', 1, 0, 'F', '0', '0', 'garbage:record:remove', '#', 'admin', sysdate(), '', null, ''); 