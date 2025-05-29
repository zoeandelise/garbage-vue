-- 菜单 SQL
-- 消息队列监控菜单
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息队列监控', '2', '11', 'mq', 'monitor/mq/index', 1, 0, 'C', '0', '0', 'monitor:mq:list', 'message', 'admin', sysdate(), '', null, '消息队列监控菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 消息队列监控按钮
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息队列监控查询', @parentId, '1', '#', '', 1, 0, 'F', '0', '0', 'mq:status:query', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('队列管理', @parentId, '2', '#', '', 1, 0, 'F', '0', '0', 'mq:queue:list', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('队列详情查询', @parentId, '3', '#', '', 1, 0, 'F', '0', '0', 'mq:queue:query', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('队列清空', @parentId, '4', '#', '', 1, 0, 'F', '0', '0', 'mq:queue:clear', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('队列重启', @parentId, '5', '#', '', 1, 0, 'F', '0', '0', 'mq:queue:restart', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('错误消息查询', @parentId, '6', '#', '', 1, 0, 'F', '0', '0', 'mq:error:list', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('错误消息详情', @parentId, '7', '#', '', 1, 0, 'F', '0', '0', 'mq:error:query', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('错误消息重试', @parentId, '8', '#', '', 1, 0, 'F', '0', '0', 'mq:error:retry', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('错误消息删除', @parentId, '9', '#', '', 1, 0, 'F', '0', '0', 'mq:error:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('错误消息导出', @parentId, '10', '#', '', 1, 0, 'F', '0', '0', 'mq:error:export', '#', 'admin', sysdate(), '', null, '');

-- 子菜单：队列状态
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('队列状态', @parentId, '11', 'queue-status', 'mq-monitor/queue-status', 1, 0, 'C', '0', '0', 'mq:queue:list', 'job', 'admin', sysdate(), '', null, '队列状态菜单');

-- 子菜单：任务日志
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('任务日志', @parentId, '12', 'task-log', 'mq-monitor/task-log', 1, 0, 'C', '0', '0', 'mq:error:list', 'log', 'admin', sysdate(), '', null, '任务日志菜单'); 