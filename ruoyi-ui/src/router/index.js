import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: 路由配置项
 *
 * hidden: true                     // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true                 // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                  // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                  // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                  // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect             // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'               // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * query: '{"id": 1, "name": "ry"}' // 访问路由的默认传递参数
 * roles: ['admin', 'common']       // 访问路由的角色权限
 * permissions: ['a:a:a', 'b:b:b']  // 访问路由的菜单权限
 * meta : {
    noCache: true                   // 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)
    title: 'title'                  // 设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'                // 设置该路由的图标，对应路径src/assets/icons/svg
    breadcrumb: false               // 如果设置为false，则不会在breadcrumb面包屑中显示
    activeMenu: '/system/user'      // 当路由设置了该属性，则会高亮相对应的侧边栏。
  }
 */

// 公共路由
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: 'index',
    children: [
      {
        path: 'index',
        component: () => import('@/views/index'),
        name: 'Index',
        meta: { title: '仪表盘', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [
      {
        path: 'profile',
        component: () => import('@/views/system/user/profile/index'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  }
]

// 动态路由，基于用户权限动态去加载
export const dynamicRoutes = [
  {
    path: '/system/user-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:user:edit'],
    children: [
      {
        path: 'role/:userId(\\d+)',
        component: () => import('@/views/system/user/authRole'),
        name: 'AuthRole',
        meta: { title: '分配角色', activeMenu: '/system/user' }
      }
    ]
  },
  {
    path: '/system/role-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:role:edit'],
    children: [
      {
        path: 'user/:roleId(\\d+)',
        component: () => import('@/views/system/role/authUser'),
        name: 'AuthUser',
        meta: { title: '分配用户', activeMenu: '/system/role' }
      }
    ]
  },
  {
    path: '/system/dict-data',
    component: Layout,
    hidden: true,
    permissions: ['system:dict:list'],
    children: [
      {
        path: 'index/:dictId(\\d+)',
        component: () => import('@/views/system/dict/data'),
        name: 'Data',
        meta: { title: '字典数据', activeMenu: '/system/dict' }
      }
    ]
  },
  {
    path: '/monitor/job-log',
    component: Layout,
    hidden: true,
    permissions: ['monitor:job:list'],
    children: [
      {
        path: 'index/:jobId(\\d+)',
        component: () => import('@/views/monitor/job/log'),
        name: 'JobLog',
        meta: { title: '调度日志', activeMenu: '/monitor/job' }
      }
    ]
  },
  {
    path: '/tool/gen-edit',
    component: Layout,
    hidden: true,
    permissions: ['tool:gen:edit'],
    children: [
      {
        path: 'index/:tableId(\\d+)',
        component: () => import('@/views/tool/gen/editTable'),
        name: 'GenEdit',
        meta: { title: '修改生成配置', activeMenu: '/tool/gen' }
      }
    ]
  },
  
  // 垃圾分类管理
  {
    path: '/garbage',
    component: Layout,
    redirect: 'noredirect',
    name: 'GarbageManagement',
    meta: { title: '垃圾分类管理', icon: 'tree', permissions: ['garbage:manage'] },
    children: [
      {
        path: 'record',
        component: () => import('@/views/garbage/record/index'),
        name: 'GarbageRecord',
        meta: { title: '垃圾投递记录', icon: 'form', permissions: ['garbage:record:list'] }
      },
      {
        path: 'record/detail/:id',
        component: () => import('@/views/garbage/record/detail'),
        name: 'RecordDetail',
        meta: { title: '记录详情', activeMenu: '/garbage/record', permissions: ['garbage:record:query'] },
        hidden: true
      },
      {
        path: 'record/edit/:id',
        component: () => import('@/views/garbage/record/edit'),
        name: 'RecordEdit',
        meta: { title: '编辑投递记录', activeMenu: '/garbage/record', permissions: ['garbage:record:edit'] },
        hidden: true
      },
      {
        path: 'record/submit',
        component: () => import('@/views/garbage/record/submit'),
        name: 'SubmitRecord',
        meta: { title: '提交投递记录', icon: 'upload', permissions: ['garbage:record:add'] }
      },
      {
        path: 'image-audit',
        component: () => import('@/views/garbage/record/image-audit'),
        name: 'ImageAudit',
        meta: { title: '图片审核', icon: 'picture', permissions: ['garbage:record:audit'] }
      },
      {
        path: 'guide',
        component: () => import('@/views/garbage/guide/index'),
        name: 'GarbageGuide',
        meta: { title: '垃圾分类管理', icon: 'guide', permissions: ['garbage:guide:list'] }
      },
      {
        path: 'guide/add',
        component: () => import('@/views/garbage/guide/add'),
        name: 'GuideAdd',
        meta: { title: '添加分类指南', activeMenu: '/garbage/guide', permissions: ['garbage:guide:add'] },
        hidden: true
      },
      {
        path: 'guide/edit/:id',
        component: () => import('@/views/garbage/guide/edit'),
        name: 'GuideEdit',
        meta: { title: '编辑分类指南', activeMenu: '/garbage/guide', permissions: ['garbage:guide:edit'] },
        hidden: true
      },
      {
        path: 'guide/detail/:id',
        component: () => import('@/views/garbage/guide/detail'),
        name: 'GuideDetail',
        meta: { title: '分类指南详情', activeMenu: '/garbage/guide', permissions: ['garbage:guide:query'] },
        hidden: true
      },
      {
        path: 'search',
        component: () => import('@/views/garbage/search/index'),
        name: 'GarbageSearch',
        meta: { title: '垃圾分类查询', icon: 'search', permissions: ['garbage:guide:search'] }
      },
      {
        path: 'statistics',
        component: () => import('@/views/garbage/statistics/index'),
        name: 'GarbageStatistics',
        meta: { title: '垃圾分类统计', icon: 'chart', permissions: ['garbage:statistics:view'] }
      }
    ]
  },
  
  // 积分管理
  {
    path: '/points',
    component: Layout,
    redirect: 'noredirect',
    name: 'PointsManagement',
    meta: { title: '积分管理', icon: 'money', permissions: ['points:manage'] },
    children: [
      {
        path: 'list',
        component: () => import('@/views/points-management/points-list'),
        name: 'PointsList',
        meta: { title: '积分查询', icon: 'search', permissions: ['points:list'] }
      },
      {
        path: 'edit',
        component: () => import('@/views/points-management/points-edit'),
        name: 'PointsEdit',
        meta: { title: '积分调整', icon: 'edit', permissions: ['points:edit'] }
      },
      {
        path: 'ranking',
        component: () => import('@/views/points-management/points-ranking'),
        name: 'PointsRanking',
        meta: { title: '积分排行榜', icon: 'chart', permissions: ['points:ranking:list'] }
      },
      {
        path: 'rule',
        component: () => import('@/views/points-ranking/points-rule'),
        name: 'PointsRule',
        meta: { title: '积分规则配置', icon: 'edit', permissions: ['points:rule:edit'] }
      }
    ]
  },
  
  // 用户中心
  {
    path: '/user-center',
    component: Layout,
    redirect: 'noredirect',
    name: 'UserCenter',
    meta: { title: '用户中心', icon: 'user', permissions: ['user:center:view'] },
    children: [
      {
        path: 'my-record',
        component: () => import('@/views/garbage/my-record/index'),
        name: 'MyRecord',
        meta: { title: '我的投递记录', icon: 'form', permissions: ['user:record:view'] }
      },
      {
        path: 'my-points',
        component: () => import('@/views/garbage/my-points/index'),
        name: 'MyPoints',
        meta: { title: '我的积分', icon: 'money', permissions: ['user:points:view'] }
      }
    ]
  },
  
  // 用户管理
  {
    path: '/user-management',
    component: Layout,
    redirect: 'noredirect',
    name: 'UserManagement',
    meta: { title: '用户管理', icon: 'peoples', permissions: ['user:manage'] },
    children: [
      {
        path: 'user-list',
        component: () => import('@/views/user-management/user-list'),
        name: 'UserList',
        meta: { title: '用户列表', icon: 'list', permissions: ['user:list'] }
      }
    ]
  },
  
  // 系统监控
  {
    path: '/monitor',
    component: Layout,
    redirect: 'noredirect',
    name: 'Monitor',
    meta: { title: '系统监控', icon: 'monitor', permissions: ['monitor:view'] },
    children: [
      {
        path: 'mq',
        component: () => import('@/views/mq-monitor/monitor'),
        name: 'MQMonitor',
        meta: { title: '消息队列监控', icon: 'message', permissions: ['mq:monitor:view'] }
      },
      {
        path: 'queue-status',
        component: () => import('@/views/mq-monitor/queue-status'),
        name: 'QueueStatus',
        meta: { title: '队列状态', icon: 'dashboard', permissions: ['mq:monitor:view'] }
      },
      {
        path: 'task-log',
        component: () => import('@/views/mq-monitor/task-log'),
        name: 'TaskLog',
        meta: { title: '任务日志', icon: 'log', permissions: ['mq:log:view'] }
      },
      {
        path: 'operlog',
        component: () => import('@/views/monitor/operlog/index'),
        name: 'Operlog',
        meta: { title: '操作日志', icon: 'form', permissions: ['monitor:operlog:list'] }
      },
      {
        path: 'logininfor',
        component: () => import('@/views/monitor/logininfor/index'),
        name: 'Logininfor',
        meta: { title: '登录日志', icon: 'logininfor', permissions: ['monitor:logininfor:list'] }
      }
    ]
  },
  
  // 系统管理
  {
    path: '/system',
    component: Layout,
    redirect: 'noredirect',
    name: 'System',
    meta: { title: '系统管理', icon: 'system', permissions: ['system:manage'] },
    children: [
      {
        path: 'user',
        component: () => import('@/views/system/user/index'),
        name: 'User',
        meta: { title: '管理员账号', icon: 'user', permissions: ['system:user:list'] }
      },
      {
        path: 'role',
        component: () => import('@/views/system/role/index'),
        name: 'Role',
        meta: { title: '角色管理', icon: 'peoples', permissions: ['system:role:list'] }
      },
      {
        path: 'menu',
        component: () => import('@/views/system/menu/index'),
        name: 'Menu',
        meta: { title: '菜单管理', icon: 'tree-table', permissions: ['system:menu:list'] }
      },
      {
        path: 'dept',
        component: () => import('@/views/system/dept/index'),
        name: 'Dept',
        meta: { title: '部门管理', icon: 'tree', permissions: ['system:dept:list'] }
      },
      {
        path: 'post',
        component: () => import('@/views/system/post/index'),
        name: 'Post',
        meta: { title: '岗位管理', icon: 'post', permissions: ['system:post:list'] }
      },
      {
        path: 'dict',
        component: () => import('@/views/system/dict/index'),
        name: 'Dict',
        meta: { title: '字典管理', icon: 'dict', permissions: ['system:dict:list'] }
      },
      {
        path: 'config',
        component: () => import('@/views/system/config/index'),
        name: 'Config',
        meta: { title: '参数设置', icon: 'edit', permissions: ['system:config:list'] }
      },
      {
        path: 'notice',
        component: () => import('@/views/system/notice/index'),
        name: 'Notice',
        meta: { title: '通知公告', icon: 'message', permissions: ['system:notice:list'] }
      }
    ]
  }
]

// 防止连续点击多次路由报错
let routerPush = Router.prototype.push
let routerReplace = Router.prototype.replace
// push
Router.prototype.push = function push(location) {
  return routerPush.call(this, location).catch(err => err)
}
// replace
Router.prototype.replace = function push(location) {
  return routerReplace.call(this, location).catch(err => err)
}

export default new Router({
  mode: 'history', // 去掉url中的#
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})
