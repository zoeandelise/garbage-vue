-- 积分管理菜单
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES (2000, '积分管理', 0, 5, 'points', NULL, 1, 0, 'M', '0', '0', '', 'star', 'admin', SYSDATE(), '', NULL, '积分管理菜单');

-- 积分记录列表
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES (2001, '积分记录', 2000, 1, 'list', 'points-management/points-list', 1, 0, 'C', '0', '0', 'points:list', 'list', 'admin', SYSDATE(), '', NULL, '积分记录菜单');

-- 积分记录查询
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES (2002, '积分记录查询', 2001, 1, '', '', 1, 0, 'F', '0', '0', 'points:query', '#', 'admin', SYSDATE(), '', NULL, '');

-- 积分记录导出
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES (2003, '积分记录导出', 2001, 2, '', '', 1, 0, 'F', '0', '0', 'points:export', '#', 'admin', SYSDATE(), '', NULL, '');

-- 用户积分管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES (2004, '用户积分', 2000, 2, 'edit', 'points-management/points-edit', 1, 0, 'C', '0', '0', 'points:users', 'edit', 'admin', SYSDATE(), '', NULL, '用户积分管理菜单');

-- 积分调整
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES (2005, '积分调整', 2004, 1, '', '', 1, 0, 'F', '0', '0', 'points:edit', '#', 'admin', SYSDATE(), '', NULL, '');

-- 积分排行榜
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES (2006, '积分排行榜', 2000, 3, 'ranking', 'points-management/points-ranking', 1, 0, 'C', '0', '0', 'points:ranking:list', 'chart', 'admin', SYSDATE(), '', NULL, '积分排行榜菜单');

-- 积分统计
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES (2007, '积分统计', 2006, 1, '', '', 1, 0, 'F', '0', '0', 'points:ranking:stats', '#', 'admin', SYSDATE(), '', NULL, '');

-- 系统工具菜单下添加数据初始化菜单
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES (2008, '数据初始化', 3, 9, 'data-init', 'tool/data-init/index', 1, 0, 'C', '0', '0', 'system:data:init', 'database', 'admin', SYSDATE(), '', NULL, '数据初始化操作');

-- 为管理员角色添加所有积分管理权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2000);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2001);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2002);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2003);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2004);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2005);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2006);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2007);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2008); 