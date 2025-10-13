<template>
  <div class="app-container">
    <!-- 顶部通栏 -->
    <header class="header">
      <div class="logo">点苏记</div>
      
      <!-- 搜索框 -->
      <div class="search-container">
        <input 
          type="text" 
          placeholder="搜索非遗商品/路线"
          class="search-input"
        >
        <button class="search-btn">搜索</button>
      </div>
      
      <!-- 用户操作区 -->
      <div class="user-actions">
        <button class="notify-btn" title="消息通知">
  <!-- <svg class="icon" viewBox="0 0 24 24">
    <path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.18C9.63 5.36 8 7.92 8 11v5l-2 2v1h16v-1l-2-2z"/>
  </svg> -->
  <!-- <span class="notification-badge">3</span> -->
</button>
        <button class="logout-btn" @click="router.push('/home')">返回</button>
      </div>
    </header>

    <!-- 主体容器 -->
    <div class="main-container">
      <!-- 左侧导航 -->
      <aside class="sidebar">
        <nav class="nav-list">
          <button 
            v-for="(item, index) in navList" 
            :key="index"
            @click="handleNavClick(item.target)"
            class="nav-item"
            :class="{ 'nav-item-active': activeTab === item.target }"
          >
            <svg class="nav-icon" :viewBox="item.iconViewBox">
              <path :d="item.iconPath"/>
            </svg>
            <span class="nav-text">{{ item.label }}</span>
          </button>
        </nav>
      </aside>

      <!-- 右侧内容区 -->
      <main class="content">
        <!-- 个人信息模块 -->
        <section v-if="activeTab === 'profile'" class="content-section">
          <!-- 个人信息卡片 -->
          <div class="card">
            <div class="profile-header">
              <!-- 头像区域 -->
              <div class="avatar-container">
                <img 
                  src="https://q8.itc.cn/q_70/images03/20250304/f5873423f8b044d78aa8cf036bc132e0.jpeg" 
                  alt="用户头像" 
                  class="avatar"
                >
              </div>
              
              <!-- 等级区域 -->
              <div class="level-container">
                <div class="level-badge">
                  青铜守护官
                </div>
              </div>
            </div>
            
            <!-- 个人信息详情 -->
            <div class="profile-info">
              <h2 class="profile-name">{{ userInfo.nickname }}</h2>
              <p class="profile-detail">
                手机号：{{ userInfo.phone }}<br>
                注册时间：{{ userInfo.registerTime }} 加入点苏记
              </p>
              
              <!-- 成长数据 -->
              <div class="growth-data">
                累计点亮非遗点：<span class="highlight">{{ userInfo.growth.points }}</span>个 | 
                创作路线数：<span class="highlight">{{ userInfo.growth.routes }}</span>条 | 
                获得勋章：<span class="highlight">{{ userInfo.growth.medals }}</span>枚 | 
                可用积分：<span class="highlight">{{ userInfo.growth.score }}</span>分 
                <button @click="handleExchangeScore" class="exchange-btn">兑换</button>
              </div>
              
              <!-- 操作按钮 -->
              <div class="profile-actions">
                <button @click="handleEditProfile" class="btn btn-outline">
                  编辑个人信息
                </button>
                <button @click="handleShowLevelRule" class="btn btn-outline">
                  查看等级规则
                </button>
              </div>
            </div>
          </div>
          
          <!-- 勋章墙 -->
          <div class="card">
            <div class="card-header">
              <h3 class="card-title">我的非遗勋章</h3>
              <a href="#" class="card-more">全部勋章</a>
            </div>
            
            <div class="medals-grid">
              <div 
                v-for="(medal, index) in userInfo.medals" 
                :key="index"
                class="medal-item"
                @mouseenter="handleMedalTooltipShow(medal.tooltip, $event)"
                @mouseleave="handleMedalTooltipHide()"
              >
                <div class="medal-icon" :class="medal.active ? 'medal-active' : ''">
                  <div 
                    class="medal-symbol"
                    :class="'medal-' + medal.type"
                    v-if="!medal.active"
                  ></div>
                </div>
                <div class="medal-info">
                  <h4 class="medal-name">{{ medal.name }}</h4>
                  <p class="medal-time">{{ medal.time }}</p>
                </div>
              </div>
            </div>
            
            <!-- 勋章悬浮提示 -->
            <div 
              v-if="tooltipVisible"
              class="tooltip"
              :style="{ left: tooltipLeft + 'px', top: tooltipTop + 'px' }"
            >
              {{ tooltipText }}
            </div>
          </div>
        </section>

        <!-- 我的订单模块 -->
        <section v-if="activeTab === 'orders'" class="content-section">
          <h3 class="section-title">我的商品购买记录</h3>
          
          <!-- 订单筛选 -->
          <div class="card">
            <div class="filter-group">
              <!-- 订单状态筛选 -->
              <div class="filter-item">
                <label class="filter-label">订单状态：</label>
                <div class="radio-group">
                  <label class="radio-item">
                    <input 
                      type="radio" 
                      name="orderStatus" 
                      value="all" 
                      v-model="orderFilter.status"
                    > 全部
                  </label>
                  <label class="radio-item">
                    <input 
                      type="radio" 
                      name="orderStatus" 
                      value="pending" 
                      v-model="orderFilter.status"
                    > 待付款
                  </label>
                  <label class="radio-item">
                    <input 
                      type="radio" 
                      name="orderStatus" 
                      value="shipping" 
                      v-model="orderFilter.status"
                    > 待发货
                  </label>
                  <label class="radio-item">
                    <input 
                      type="radio" 
                      name="orderStatus" 
                      value="received" 
                      v-model="orderFilter.status"
                    > 待收货
                  </label>
                  <label class="radio-item">
                    <input 
                      type="radio" 
                      name="orderStatus" 
                      value="completed" 
                      v-model="orderFilter.status"
                    > 已完成
                  </label>
                  <label class="radio-item">
                    <input 
                      type="radio" 
                      name="orderStatus" 
                      value="aftersale" 
                      v-model="orderFilter.status"
                    > 售后中
                  </label>
                </div>
              </div>
              
              <!-- 时间范围筛选 -->
              <div class="filter-item">
                <label class="filter-label">时间范围：</label>
                <select 
                  v-model="orderFilter.timeRange"
                  class="form-select"
                  @change="handleTimeRangeChange"
                >
                  <option value="30">近30天</option>
                  <option value="90">近90天</option>
                  <option value="custom">自定义</option>
                </select>
                <input 
                  type="date" 
                  v-model="orderFilter.startDate"
                  class="form-input date-input"
                  :style="{ display: orderFilter.timeRange === 'custom' ? 'inline-block' : 'none' }"
                >
                <input 
                  type="date" 
                  v-model="orderFilter.endDate"
                  class="form-input date-input"
                  :style="{ display: orderFilter.timeRange === 'custom' ? 'inline-block' : 'none' }"
                >
              </div>
              
              <!-- 品类筛选 -->
              <div class="filter-item">
                <label class="filter-label">非遗品类：</label>
                <div class="checkbox-group">
                  <label class="checkbox-item">
                    <input 
                      type="checkbox" 
                      name="category" 
                      value="crafts" 
                      v-model="orderFilter.categories"
                    > 传统工艺品
                  </label>
                  <label class="checkbox-item">
                    <input 
                      type="checkbox" 
                      name="category" 
                      value="food" 
                      v-model="orderFilter.categories"
                    > 特色美食
                  </label>
                  <label class="checkbox-item">
                    <input 
                      type="checkbox" 
                      name="category" 
                      value="cultural" 
                      v-model="orderFilter.categories"
                    > 文创衍生品
                  </label>
                </div>
              </div>
              
              <!-- 订单搜索 -->
              <div class="filter-item search-filter">
                <input 
                  type="text" 
                  placeholder="搜索订单号/商品名称"
                  v-model="orderFilter.searchKey"
                  class="form-input search-input"
                >
                <button @click="handleSearchOrder" class="btn btn-primary">搜索</button>
              </div>
            </div>
          </div>
          
          <!-- 订单列表 -->
          <div class="order-list">
            <div 
              v-for="(order, index) in filteredOrders" 
              :key="index"
              class="order-item"
            >
              <!-- 商品信息 -->
              <div class="order-goods">
                <img 
                  :src="order.goodsImg" 
                  alt="商品图片" 
                  class="goods-img"
                >
                <div class="goods-info">
                  <h4 class="goods-name">{{ order.goodsName }}</h4>
                  <p class="goods-spec">{{ order.goodsSpec }}</p>
                </div>
              </div>
              
              <!-- 订单元信息 -->
              <div class="order-meta">
                <p>订单号：{{ order.orderNo }}</p>
                <p>下单时间：{{ order.createTime }}</p>
                <p>支付方式：{{ order.payType }}</p>
                <p>订单金额：¥{{ order.amount }}</p>
              </div>
              
              <!-- 订单状态与操作 -->
              <div class="order-actions">
                <span 
                  class="order-status"
                  :class="order.status === 'pending' ? 'status-pending' : 'status-completed'"
                >
                  {{ order.statusText }}
                </span>
                
                <button 
                  v-if="order.status === 'pending'"
                  @click="handlePayNow(order)"
                  class="btn btn-outline btn-sm"
                >
                  立即付款
                </button>
                <button 
                  v-if="order.status === 'pending'"
                  @click="handleCancelOrder(order, index)"
                  class="btn btn-outline btn-sm"
                >
                  取消订单
                </button>
                
                <button 
                  v-if="order.status === 'completed'"
                  @click="handleViewLogistics(order)"
                  class="btn btn-outline btn-sm"
                >
                  查看物流
                </button>
                <button 
                  v-if="order.status === 'completed'"
                  @click="handleApplyAftersale(order)"
                  class="btn btn-outline btn-sm"
                >
                  申请售后
                </button>
                <button 
                  v-if="order.status === 'completed'"
                  @click="handleBuyAgain(order)"
                  class="btn btn-outline btn-sm"
                >
                  再次购买
                </button>
              </div>
            </div>
          </div>
          
          <!-- 分页 -->
          <div class="pagination">
            <button 
              @click="handlePageChange('prev')" 
              class="pagination-btn"
              :disabled="currentPage === 1"
              :class="{ 'pagination-disabled': currentPage === 1 }"
            >
              上一页
            </button>
            <span class="pagination-current">{{ currentPage }}</span>
            <button 
              @click="handlePageChange('next')" 
              class="pagination-btn"
              :disabled="currentPage >= totalPages"
              :class="{ 'pagination-disabled': currentPage >= totalPages }"
            >
              下一页
            </button>
            <span class="pagination-total">共 {{ totalOrders }} 条订单</span>
          </div>
        </section>

        <!-- 历史浏览模块 -->
        <section v-if="activeTab === 'browsing'" class="content-section">
          <!-- 标签切换 -->
          <div class="tabs">
            <button 
              @click="handleBrowsingTabChange('goods')"
              class="tab-item"
              :class="{ 'tab-active': browsingActiveTab === 'goods' }"
            >
              浏览过的非遗商品
            </button>
            <button 
              @click="handleBrowsingTabChange('routes')"
              class="tab-item"
              :class="{ 'tab-active': browsingActiveTab === 'routes' }"
            >
              浏览过的非遗路线/景点
            </button>
          </div>
          
          <!-- 商品浏览记录 -->
          <div 
            v-if="browsingActiveTab === 'goods'"
            class="card tab-content"
          >
            <button 
              @click="handleClearGoodsHistory"
              class="clear-history"
            >
              清空记录
            </button>
            
            <div class="goods-grid">
              <div 
                v-for="(item, index) in browsingHistory.goods" 
                :key="index"
                class="goods-card"
              >
                <img 
                  :src="item.img" 
                  alt="商品图片" 
                  class="goods-card-img"
                >
                <div class="goods-card-info">
                  <h4 class="goods-card-name">{{ item.name }}</h4>
                  <p class="goods-card-price">售价：¥{{ item.price }}</p>
                  <p class="goods-card-time">{{ item.time }}</p>
                </div>
                <div class="goods-card-actions">
                  <button class="btn btn-outline btn-xs">加入购物车</button>
                  <button class="btn btn-outline btn-xs">立即购买</button>
                  <button 
                    @click="handleToggleCollect(item)"
                    class="btn btn-outline btn-xs"
                    :class="{ 'btn-collected': item.collected }"
                  >
                    {{ item.collected ? '已收藏' : '收藏' }}
                  </button>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 路线浏览记录 -->
          <div 
            v-if="browsingActiveTab === 'routes'"
            class="card tab-content"
          >
            <div class="route-list">
              <div 
                v-for="(item, index) in browsingHistory.routes" 
                :key="index"
                class="route-item"
              >
                <img 
                  :src="item.img" 
                  alt="路线图片" 
                  class="route-img"
                >
                <div class="route-info">
                  <h4 class="route-name">{{ item.name }}</h4>
                  <p class="route-detail">包含非遗点：{{ item.spots }}个</p>
                  <p class="route-detail">浏览时间：{{ item.time }}</p>
                  <p class="route-detail">点亮状态：已点亮 {{ item.lighted }}/{{ item.spots }} 个非遗点</p>
                </div>
                <div class="route-actions">
                  <button class="btn btn-outline btn-xs">继续点亮</button>
                  <button 
                    @click="handleToggleRouteCollect(item)"
                    class="btn btn-outline btn-xs"
                    :class="{ 'btn-collected': item.collected }"
                  >
                    {{ item.collected ? '已收藏' : '收藏路线' }}
                  </button>
                  <button class="btn btn-outline btn-xs">分享路线</button>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 我的路线模块 -->
        <section v-if="activeTab === 'routes-section'" class="content-section">
          <!-- 标签切换 -->
          <div class="tabs">
            <button 
              @click="handleRouteTabChange('my-create')"
              class="tab-item"
              :class="{ 'tab-active': routeActiveTab === 'my-create' }"
            >
              我的创作
            </button>
            <button 
              @click="handleRouteTabChange('my-collect')"
              class="tab-item"
              :class="{ 'tab-active': routeActiveTab === 'my-collect' }"
            >
              我的收藏
            </button>
            <button 
              @click="handleRouteTabChange('draft')"
              class="tab-item"
              :class="{ 'tab-active': routeActiveTab === 'draft' }"
            >
              草稿箱
            </button>
          </div>
          
          <!-- 我的创作 -->
          <div 
            v-if="routeActiveTab === 'my-create'"
            class="card tab-content"
          >
            <div class="route-manage-list">
              <div 
                v-for="(route, index) in routes.myCreate" 
                :key="index"
                class="route-manage-item"
              >
                <img 
                  :src="route.img" 
                  alt="路线图片" 
                  class="route-manage-img"
                >
                <div class="route-manage-info">
                  <h4 class="route-manage-name">{{ route.name }}</h4>
                  <p class="route-manage-detail">时长：{{ route.duration }} | 非遗点：{{ route.spots }}</p>
                  <p class="route-manage-detail">创作时间：{{ route.createTime }} | 浏览量：{{ route.views }}次</p>
                </div>
                <div class="route-manage-actions">
                  <button @click="handleEditRoute(route)" class="btn btn-outline btn-xs">编辑路线</button>
                  <button @click="handlePublishRoute(route)" class="btn btn-outline btn-xs">
                    {{ route.published ? '下架' : '发布' }}
                  </button>
                  <button @click="handleDeleteRoute('myCreate', index)" class="btn btn-outline btn-xs">删除</button>
                  <button class="btn btn-outline btn-xs">分享</button>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 我的收藏 -->
          <div 
            v-if="routeActiveTab === 'my-collect'"
            class="card tab-content"
          >
            <div class="route-manage-list">
              <div 
                v-for="(route, index) in routes.myCollect" 
                :key="index"
                class="route-manage-item"
              >
                <img 
                  :src="route.img" 
                  alt="路线图片" 
                  class="route-manage-img"
                >
                <div class="route-manage-info">
                  <h4 class="route-manage-name">{{ route.name }}</h4>
                  <p class="route-manage-detail">时长：{{ route.duration }} | 非遗点：{{ route.spots }}</p>
                  <p class="route-manage-detail">收藏时间：{{ route.collectTime }} | 浏览量：{{ route.views }}次</p>
                </div>
                <div class="route-manage-actions">
                  <button @click="handleCancelRouteCollect(index)" class="btn btn-outline btn-xs">取消收藏</button>
                  <button class="btn btn-outline btn-xs">查看路线</button>
                  <button class="btn btn-outline btn-xs">生成攻略</button>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 草稿箱 -->
          <div 
            v-if="routeActiveTab === 'draft'"
            class="card tab-content"
          >
            <div class="route-manage-list">
              <div 
                v-for="(draft, index) in routes.draft" 
                :key="index"
                class="route-manage-item"
              >
                <img 
                  :src="draft.img" 
                  alt="路线图片" 
                  class="route-manage-img"
                >
                <div class="route-manage-info">
                  <h4 class="route-manage-name">{{ draft.name }}</h4>
                  <p class="route-manage-detail">时长：{{ draft.duration }} | 非遗点：{{ draft.spots }}</p>
                  <p class="route-manage-detail">保存时间：{{ draft.saveTime }}</p>
                </div>
                <div class="route-manage-actions">
                  <button @click="handleEditDraft(draft)" class="btn btn-outline btn-xs">继续编辑</button>
                  <button @click="handleDeleteDraft(index)" class="btn btn-outline btn-xs">删除草稿</button>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 我的勋章模块 -->
        <section v-if="activeTab === 'medals'" class="content-section">
          <h3 class="section-title">我的全部勋章</h3>
          
          <div class="card">
            <div class="all-medals-grid">
              <div 
                v-for="(medal, index) in allMedals" 
                :key="index"
                class="medal-item"
                @mouseenter="handleMedalTooltipShow(medal.tooltip, $event)"
                @mouseleave="handleMedalTooltipHide()"
              >
                <div class="medal-icon" :class="medal.active ? 'medal-active' : ''">
                  <div 
                    class="medal-symbol"
                    :class="'medal-' + medal.type"
                  ></div>
                </div>
                <div class="medal-info">
                  <h4 class="medal-name">{{ medal.name }}</h4>
                  <p class="medal-time">
                    {{ medal.active ? `获得于 ${medal.time}` : `进度：${medal.progress}` }}
                  </p>
                  <p class="medal-action" v-if="!medal.active">去点亮 →</p>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 我的预约模块 -->
        <section v-if="activeTab === 'appointments'" class="content-section">
          <h3 class="section-title">我的预约体验</h3>
          
          <div class="appointment-list">
            <div 
              v-for="(appointment, index) in appointments" 
              :key="index"
              class="appointment-item"
            >
              <div class="appointment-info">
                <h4 class="appointment-name">{{ appointment.name }}</h4>
                <p class="appointment-detail">时间：{{ appointment.time }}</p>
                <p class="appointment-detail">
                  地点：{{ appointment.location }} 
                  <button class="btn btn-primary btn-xs">导航</button>
                </p>
                <p class="appointment-detail">状态：{{ appointment.statusText }}</p>
              </div>
              
              <div class="appointment-actions">
                <button 
                  v-if="appointment.status === 'pending'"
                  @click="handleViewAppointmentDetails(appointment)"
                  class="btn btn-outline"
                >
                  查看详情
                </button>
                <button 
                  v-if="appointment.status === 'pending'"
                  @click="handleCancelAppointment(appointment, index)"
                  class="btn btn-outline"
                >
                  取消预约
                </button>
                
                <button 
                  v-if="appointment.status === 'completed'"
                  @click="handleExperienceReview(appointment)"
                  class="btn btn-outline"
                >
                  体验评价
                </button>
              </div>
            </div>
          </div>
        </section>

        <!-- 设置中心模块 -->
        <section v-if="activeTab === 'settings'" class="content-section">
          <!-- 账户安全 -->
          <div class="card">
            <h4 class="card-subtitle">账户安全</h4>
            
            <div class="settings-list">
              <div class="setting-item">
                <span class="setting-label">修改密码</span>
                <button @click="handleChangePassword" class="btn btn-outline btn-sm">修改</button>
              </div>
              
              <div class="setting-item">
                <span class="setting-label">绑定手机号</span>
                <div class="setting-value">
                  <span>{{ userInfo.phone }}</span>
                  <button @click="handleChangePhone" class="btn btn-outline btn-sm">更换绑定</button>
                </div>
              </div>
              
              <div class="setting-item">
                <span class="setting-label">登录保护</span>
                <label class="switch">
                  <input 
                    type="checkbox" 
                    v-model="settings.loginProtection"
                    @change="saveSettings"
                  >
                  <span class="slider"></span>
                </label>
              </div>
            </div>
          </div>
          
          <!-- 消息通知 -->
          <div class="card">
            <h4 class="card-subtitle">消息通知</h4>
            
            <div class="settings-list">
              <div class="setting-item">
                <span class="setting-label">订单通知</span>
                <label class="switch">
                  <input 
                    type="checkbox" 
                    v-model="settings.notifications.order"
                    @change="saveSettings"
                  >
                  <span class="slider"></span>
                </label>
              </div>
              
              <div class="setting-item">
                <span class="setting-label">活动提醒</span>
                <label class="switch">
                  <input 
                    type="checkbox" 
                    v-model="settings.notifications.event"
                    @change="saveSettings"
                  >
                  <span class="slider"></span>
                </label>
              </div>
              
              <div class="setting-item">
                <span class="setting-label">非遗资讯</span>
                <label class="switch">
                  <input 
                    type="checkbox" 
                    v-model="settings.notifications.news"
                    @change="saveSettings"
                  >
                  <span class="slider"></span>
                </label>
              </div>
            </div>
          </div>
          
          <!-- 隐私设置 -->
          <div class="card">
            <h4 class="card-subtitle">隐私设置</h4>
            
            <div class="settings-list">
              <div class="setting-item">
                <span class="setting-label">个人路线可见范围</span>
                <div class="radio-group inline-radio">
                  <label class="radio-item">
                    <input 
                      type="radio" 
                      name="routeVisible" 
                      value="private" 
                      v-model="settings.privacy.routeVisible"
                      @change="saveSettings"
                    > 仅自己可见
                  </label>
                  <label class="radio-item">
                    <input 
                      type="radio" 
                      name="routeVisible" 
                      value="friends" 
                      v-model="settings.privacy.routeVisible"
                      @change="saveSettings"
                    > 好友可见
                  </label>
                  <label class="radio-item">
                    <input 
                      type="radio" 
                      name="routeVisible" 
                      value="public" 
                      v-model="settings.privacy.routeVisible"
                      @change="saveSettings"
                    > 公开
                  </label>
                </div>
              </div>
              
              <div class="setting-item">
                <span class="setting-label">浏览记录可见范围</span>
                <div class="radio-group inline-radio">
                  <label class="radio-item">
                    <input 
                      type="radio" 
                      name="browseVisible" 
                      value="private" 
                      v-model="settings.privacy.browseVisible"
                      @change="saveSettings"
                    > 仅自己可见
                  </label>
                  <label class="radio-item">
                    <input 
                      type="radio" 
                      name="browseVisible" 
                      value="friends" 
                      v-model="settings.privacy.browseVisible"
                      @change="saveSettings"
                    > 好友可见
                  </label>
                  <label class="radio-item">
                    <input 
                      type="radio" 
                      name="browseVisible" 
                      value="public" 
                      v-model="settings.privacy.browseVisible"
                      @change="saveSettings"
                    > 公开
                  </label>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 帮助与反馈 -->
          <div class="card">
            <h4 class="card-subtitle">帮助与反馈</h4>
            
            <div class="settings-list">
              <div class="setting-item">
                <span class="setting-label">售后帮助中心</span>
                <button @click="handleOpenHelpCenter" class="btn btn-outline btn-sm">查看</button>
              </div>
              
              <div class="setting-item">
                <span class="setting-label">意见反馈</span>
                <button @click="handleOpenFeedback" class="btn btn-outline btn-sm">反馈</button>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>

    <!-- 通用弹窗 -->
    <div 
      v-if="modalVisible"
      class="modal-backdrop"
      @click="handleModalClose"
    >
      <div 
        class="modal"
        @click.stop
      >
        <div class="modal-header">
          <h3 class="modal-title">{{ modalTitle }}</h3>
          <button @click="handleModalClose" class="modal-close">&times;</button>
        </div>
        
        <div class="modal-body">
          <div v-html="modalContent"></div>
          
          <!-- 星级评分（评价专用） -->
          <div v-if="modalType === 'review'">
            <div class="rating">
              <span 
                v-for="i in 5" 
                :key="i"
                @click="setRating(i)"
                class="star"
                :style="{ color: i <= currentRating ? '#FF9800' : '#CCCCCC' }"
              >★</span>
            </div>
          </div>
        </div>
        
        <div v-if="modalShowFooter" class="modal-footer">
          <button @click="handleModalClose" class="btn btn-outline">取消</button>
          <button @click="handleModalConfirm" class="btn btn-primary">确认</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import router from '../../router';

// 导航数据
const navList = [
  {
    label: '个人信息',
    target: 'profile',
    iconViewBox: '0 0 24 24',
    iconPath: 'M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z'
  },
  {
    label: '我的订单',
    target: 'orders',
    iconViewBox: '0 0 24 24',
    iconPath: 'M17 18c-.56 0-1.07-.24-1.43-.63l-3.33-3.33-1.27 1.27c-.11.11-.25.17-.39.17-.14 0-.28-.06-.39-.17L6.43 12.63C6.07 12.24 5.56 12 5 12c-.55 0-1 .45-1 1v6c0 .55.45 1 1 1h12c.55 0 1-.45 1-1v-6c0-.55-.45-1-1-1zm-3.5-6l1.27-1.27c.11-.11.25-.17.39-.17.14 0 .28.06.39.17L15.5 12h-3z'
  },
  {
    label: '历史浏览',
    target: 'browsing',
    iconViewBox: '0 0 24 24',
    iconPath: 'M13 3h-2v10h2V3zm4.83 2.17l-1.42 1.42C17.99 6.96 19 8.46 19 10c0 1.66-1.34 3-3 3-.14 0-.27-.01-.4-.04l-1.46 1.46c.03.13.04.27.04.41 0 1.66-1.34 3-3 3s3-1.34 3-3c0-.14-.01-.27-.04-.4l1.46-1.46C8.34 9.99 7 11.46 7 13c0 1.66 1.34 3 3 3s3-1.34 3-3c0-.14-.01-.27-.04-.4l1.46-1.46c-.03-.13-.04-.27-.04-.41 0-1.66 1.34-3 3-3 .54 0 1.04.19 1.42.51l1.42-1.42A.996.996 0 0 0 20 4c0-.55-.45-1-1-1s-1 .45-1 1z'
  },
  {
    label: '我的路线',
    target: 'routes-section',
    iconViewBox: '0 0 24 24',
    iconPath: 'M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z'
  },
  {
    label: '我的勋章',
    target: 'medals',
    iconViewBox: '0 0 24 24',
    iconPath: 'M12 2l-5.5 9h11z M22 12l-5.5 9h-11L2 12l5.5-9h11z'
  },
  {
    label: '我的预约',
    target: 'appointments',
    iconViewBox: '0 0 24 24',
    iconPath: 'M19 4h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V10h14v10zm0-12H5V6h14v2z'
  },
  {
    label: '设置中心',
    target: 'settings',
    iconViewBox: '0 0 24 24',
    iconPath: 'M12 15c1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3 1.34 3 3 3zm0-9c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm0 13c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm1-11c.28 0 .5.22.5.5s-.22.5-.5.5-.5-.22-.5-.5.22-.5.5-.5zm0 11c-.28 0-.5-.22-.5-.5s.22-.5.5-.5.5.22.5.5-.22.5-.5.5z'
  }
];

// 状态管理
const activeTab = ref('profile');
const modalVisible = ref(false);
const modalTitle = ref('');
const modalContent = ref('');
const modalShowFooter = ref(true);
const modalType = ref('');
const modalCallback = ref(null);
const currentRating = ref(5);

// 勋章提示
const tooltipVisible = ref(false);
const tooltipText = ref('');
const tooltipLeft = ref(0);
const tooltipTop = ref(0);

// 用户信息
const userInfo = reactive({
  nickname: '苏韵非遗爱好者',
  phone: '138****5678',
  registerTime: '2024.06.18',
  growth: {
    points: 32,
    routes: 5,
    medals: 8,
    score: 1280
  },
  medals: [
    {
      name: '云锦制造师',
      time: '2024.08.05 获取',
      active: true,
      type: 'yunjin',
      tooltip: '点亮南京云锦博物馆获得'
    },
    {
      name: '苏绣鉴赏家',
      time: '未激活',
      active: false,
      type: 'suxiu',
      tooltip: '点亮5个苏州非遗点可激活'
    },
    {
      name: '紫砂体验者',
      time: '2024.07.12 获取',
      active: true,
      type: 'zisha',
      tooltip: '体验宜兴紫砂壶制作获得'
    }
  ]
});

// 所有勋章数据
const allMedals = ref([
  ...userInfo.medals,
  {
    name: '漆器工艺师',
    time: '2024.06.30 获取',
    active: true,
    type: 'lacquer',
    tooltip: '购买扬州漆器产品获得'
  },
  {
    name: '剪纸传承人',
    time: '未激活',
    active: false,
    type: 'paper',
    progress: '3/5 个点',
    tooltip: '点亮5个剪纸非遗点可激活'
  },
  {
    name: '美食探索家',
    time: '未激活',
    active: false,
    type: 'food',
    progress: '2/4 个点',
    tooltip: '品尝4种非遗美食可激活'
  }
]);

// 订单数据
const orders = ref([
  {
    id: 1,
    orderNo: 'DD202408151234',
    createTime: '2024-08-15 10:23',
    payType: '支付宝',
    amount: '298.00',
    status: 'completed',
    statusText: '已完成',
    category: 'crafts',
    goodsName: '扬州漆器牡丹纹首饰盒',
    goodsSpec: '规格：中号 | 单价：¥298.00 | 数量：1',
    goodsImg: 'https://ts1.tc.mm.bing.net/th/id/R-C.c7087c8ead1ddb7d20400826c70185cc?rik=GzQWm5bCTy%2fcYQ&riu=http%3a%2f%2fimg.alicdn.com%2fimgextra%2fi2%2f4289884728%2fO1CN01Oolmcn1knT9jyjiME_!!4289884728.jpg&ehk=P94V%2b8Rag1SzQq%2fGUsjneqioEMutBto8kPdFik%2fFXLU%3d&risl=&pid=ImgRaw&r=0'
  },
  {
    id: 2,
    orderNo: 'DD202409035678',
    createTime: '2024-09-03 15:40',
    payType: '微信支付',
    amount: '176.00',
    status: 'pending',
    statusText: '待付款',
    category: 'food',
    goodsName: '南京樱桃鸭（礼盒装）',
    goodsSpec: '规格：1kg | 单价：¥88.00 | 数量：2',
    goodsImg: 'https://img.alicdn.com/i2/3913611788/O1CN01UWkMK61P4wliRakpz_!!3913611788.jpg'
  }
]);

// 订单筛选条件
const orderFilter = reactive({
  status: 'all',
  timeRange: '30',
  startDate: '',
  endDate: '',
  categories: [],
  searchKey: ''
});

// 分页控制
const currentPage = ref(1);
const pageSize = ref(5);

// 浏览历史
const browsingHistory = reactive({
  goods: [
    {
      id: 1,
      name: '惠山泥人Q版玩偶',
      price: '88.00',
      time: '2小时前',
      img: 'https://ts1.tc.mm.bing.net/th/id/R-C.bc254598fc6d14332d6ba755c8d42772?rik=LCshYP77xHI1nw&riu=http%3a%2f%2fimg3.baa.bitautotech.com%2fimg%2fV2img3.baa.bitautotech.com%2fusergroup%2f2014%2f2%2f12%2fa29f1b02ac3244cbb44f34cb0da17e08_990_0_max_JPG.jpg&ehk=b8nab3sPCfCYAWwaOwehuh6hQ%2fegT390SxODc6jUk0I%3d&risl=&pid=ImgRaw&r=0',
      collected: false
    },
    {
      id: 2,
      name: '徐州香包（生肖虎）',
      price: '45.00',
      time: '昨天 14:30',
      img: 'https://ts2.tc.mm.bing.net/th/id/OIP-C.w4qHdpd_oF5wd3_iXXgpKQHaEI?cb=12&rs=1&pid=ImgDetMain&o=7&rm=3',
      collected: true
    }
  ],
  routes: [
    {
      id: 1,
      name: '苏州非遗一日游',
      spots: 5,
      time: '1天前',
      lighted: 3,
      img: 'https://szfy.wglj.suzhou.com.cn/storage/wechat/b7e1db8da1860e07bed1b841ee71ef3a/6998bdbc8c735f632919c0b3055d5754.jpeg',
      collected: false
    },
    {
      id: 2,
      name: '宜兴紫砂文化游',
      spots: 3,
      time: '3天前',
      lighted: 2,
      img: 'https://ts2.tc.mm.bing.net/th/id/OIP-C.CfoaG9HCfM_jsGR6zAIL8wAAAA?cb=12&rs=1&pid=ImgDetMain&o=7&rm=3',
      collected: true
    }
  ]
});

// 路线数据
const routes = reactive({
  myCreate: [
    {
      id: 1,
      name: '南京非遗文化深度游',
      duration: '1天',
      spots: '云锦博物馆、金陵刻经处、老门东剪纸坊',
      createTime: '2024.08.20',
      views: 238,
      img: 'https://ts4.tc.mm.bing.net/th/id/OIP-C.c1Yw9VO_HD8IVru6o84jjQHaE8?cb=12&rs=1&pid=ImgDetMain&o=7&rm=3',
      published: false
    }
  ],
  myCollect: [
    {
      id: 1,
      name: '无锡非遗体验游',
      duration: '2天',
      spots: '惠山泥人馆、无锡精微绣工作室',
      collectTime: '2024.07.15',
      views: 156,
      img: 'https://imgbdb4.bendibao.com/wxbdb/xiuxian/20231/09/2023109180049_73467.png'
    }
  ],
  draft: [
    {
      id: 1,
      name: '宿迁非遗初探',
      duration: '半天',
      spots: '待完善',
      saveTime: '2024.09.01',
      img: 'https://ts1.tc.mm.bing.net/th/id/OIP-C.tG2w3_4jpUM9vQkQVcPAKgHaEb?cb=12&rs=1&pid=ImgDetMain&o=7&rm=3'
    }
  ]
});

// 预约数据
const appointments = ref([
  {
    id: 1,
    name: '宜兴紫砂壶制作体验课',
    time: '2024.09.15 14:00-16:00',
    location: '宜兴丁蜀镇紫砂工坊',
    status: 'pending',
    statusText: '待体验'
  },
  {
    id: 2,
    name: '南京云锦织造体验',
    time: '2024.08.20 09:30-11:30',
    location: '南京云锦博物馆',
    status: 'completed',
    statusText: '已完成'
  }
]);

// 设置数据
const settings = reactive({
  loginProtection: true,
  notifications: {
    order: true,
    event: true,
    news: false
  },
  privacy: {
    routeVisible: 'private',
    browseVisible: 'private'
  }
});

// 历史浏览标签切换
const browsingActiveTab = ref('goods');

// 路线标签切换
const routeActiveTab = ref('my-create');

// 计算属性：筛选后的订单
const filteredOrders = computed(() => {
  return orders.value.filter(order => {
    // 状态筛选
    if (orderFilter.status !== 'all' && order.status !== orderFilter.status) {
      return false;
    }
    
    // 品类筛选
    if (orderFilter.categories.length > 0 && !orderFilter.categories.includes(order.category)) {
      return false;
    }
    
    // 搜索筛选
    if (orderFilter.searchKey && !order.orderNo.includes(orderFilter.searchKey) && 
        !order.goodsName.includes(orderFilter.searchKey)) {
      return false;
    }
    
    return true;
  });
});

// 计算属性：总订单数
const totalOrders = computed(() => filteredOrders.value.length);

// 计算属性：总页数
const totalPages = computed(() => Math.ceil(totalOrders.value / pageSize.value));

// 页面加载时初始化
onMounted(() => {
  // 从URL哈希加载初始标签
  const hash = window.location.hash.substring(1);
  if (hash && navList.some(item => item.target === hash)) {
    activeTab.value = hash;
  }
  
  // 从本地存储加载设置
  const savedSettings = localStorage.getItem('diansuji-settings');
  if (savedSettings) {
    Object.assign(settings, JSON.parse(savedSettings));
  }
});

// 导航点击处理
const handleNavClick = (target) => {
  activeTab.value = target;
  window.location.hash = target; // 更新URL哈希
};

// 勋章提示显示
const handleMedalTooltipShow = (text, event) => {
  tooltipText.value = text;
  tooltipLeft.value = event.pageX + 10;
  tooltipTop.value = event.pageY;
  tooltipVisible.value = true;
};

// 勋章提示隐藏
const handleMedalTooltipHide = () => {
  tooltipVisible.value = false;
};

// 打开弹窗
const openModal = (title, content, type = '', showFooter = true, callback = null) => {
  modalTitle.value = title;
  modalContent.value = content;
  modalType.value = type;
  modalShowFooter.value = showFooter;
  modalCallback.value = callback;
  modalVisible.value = true;
};

// 关闭弹窗
const handleModalClose = () => {
  modalVisible.value = false;
  currentRating.value = 5; // 重置评分
};

// 确认弹窗
const handleModalConfirm = () => {
  if (modalCallback.value) {
    modalCallback.value();
  }
  handleModalClose();
};

// 编辑个人信息
const handleEditProfile = () => {
  openModal('编辑个人信息', `
    <div class="form-group">
      <label class="form-label" for="nickname">昵称：</label>
      <input type="text" id="nickname" value="${userInfo.nickname}" class="form-input">
    </div>
    <div class="form-group">
      <label class="form-label" for="signature">个人签名：</label>
      <textarea id="signature" rows="2" placeholder="分享你的非遗故事..." class="form-input"></textarea>
    </div>
  `, 'edit-profile', true, () => {
    const newNickname = document.getElementById('nickname').value;
    if (newNickname) {
      userInfo.nickname = newNickname;
    }
    alert('个人信息已更新');
  });
};

// 查看等级规则
const handleShowLevelRule = () => {
  openModal('非遗守护官等级规则', `
    <ul class="list-disc pl-5 space-y-2">
      <li><strong>青铜守护官</strong>：点亮10个非遗点</li>
      <li><strong>黄金守护官</strong>：点亮50个非遗点 + 创作3条路线</li>
      <li><strong>钻石守护官</strong>：点亮100个非遗点 + 创作10条路线 + 获得15枚勋章</li>
    </ul>
    <p class="mt-2 text-sm">等级权益：黄金及以上可解锁专属非遗体验课，钻石可获得非遗传承人一对一指导机会。</p>
  `);
};

// 积分兑换
const handleExchangeScore = () => {
  openModal('积分兑换', `
    <div class="form-group">
      <label class="form-label">可用积分：<span class="highlight">${userInfo.growth.score}</span>分</label>
    </div>
    <div class="form-group">
      <label class="form-label" for="exchange-goods">可兑换商品：</label>
      <select id="exchange-goods" class="form-select">
        <option value="bookmark">非遗书签（80积分）</option>
        <option value="postcard">非遗明信片（120积分）</option>
        <option value="ticket">体验课优惠券（300积分）</option>
      </select>
    </div>
  `, 'exchange', true, () => {
    const select = document.getElementById('exchange-goods');
    const goods = select.options[select.selectedIndex].text;
    alert(`已兑换${goods}，扣除对应积分`);
  });
};

// 时间范围变化
const handleTimeRangeChange = () => {
  if (orderFilter.timeRange !== 'custom') {
    orderFilter.startDate = '';
    orderFilter.endDate = '';
  }
};

// 搜索订单
const handleSearchOrder = () => {
  currentPage.value = 1; // 重置到第一页
};

// 分页切换
const handlePageChange = (type) => {
  if (type === 'prev' && currentPage.value > 1) {
    currentPage.value--;
  } else if (type === 'next' && currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

// 立即付款
const handlePayNow = (order) => {
  openModal('订单支付', `
    <p class="mb-2">订单号：${order.orderNo}</p>
    <p class="mb-3">应付金额：<span class="highlight">¥${order.amount}</span></p>
    <div class="space-y-2">
      <label class="radio-item-block">
        <input type="radio" name="payType" value="alipay" checked>
        <span>支付宝支付</span>
      </label>
      <label class="radio-item-block">
        <input type="radio" name="payType" value="wechat">
        <span>微信支付</span>
      </label>
    </div>
  `, 'payment', true, () => {
    // 更新订单状态
    const targetOrder = orders.value.find(o => o.id === order.id);
    if (targetOrder) {
      targetOrder.status = 'completed';
      targetOrder.statusText = '已完成';
    }
    alert('支付成功');
  });
};

// 取消订单
const handleCancelOrder = (order, index) => {
  const orderTime = new Date(order.createTime);
  const now = new Date();
  const hoursDiff = (now - orderTime) / (1000 * 60 * 60);
  
  let message = '确定要取消该订单吗？';
  if (hoursDiff < 24) {
    message += '<br>距支付截止时间不足24小时，取消将扣除50积分。';
  }
  
  openModal('取消订单', message, 'cancel-order', true, () => {
    // 从列表中移除订单
    orders.value = orders.value.filter(o => o.id !== order.id);
    alert(hoursDiff < 24 ? '订单已取消，扣除50积分' : '订单已取消');
  });
};

// 查看物流
const handleViewLogistics = (order) => {
  openModal('物流信息', `
    <p class="mb-2">订单号：${order.orderNo}</p>
    <div class="logistics-timeline">
      <div class="timeline-item">
        <p class="timeline-status">【已签收】</p>
        <p class="timeline-time">2024-08-18 15:30 已送达，收件人：张先生</p>
      </div>
      <div class="timeline-item">
        <p class="timeline-status">【运输中】</p>
        <p class="timeline-time">2024-08-17 09:15 正在派件，快递员：李师傅 138****5678</p>
      </div>
      <div class="timeline-item">
        <p class="timeline-status">【已发出】</p>
        <p class="timeline-time">2024-08-16 14:20 已从扬州仓库发出</p>
      </div>
    </div>
  `);
};

// 申请售后
const handleApplyAftersale = (order) => {
  openModal('申请售后', `
    <p class="mb-2">订单号：${order.orderNo}</p>
    <p class="mb-2">商品：${order.goodsName}</p>
    <div class="form-group">
      <label class="form-label" for="aftersale-type">售后类型：</label>
      <select id="aftersale-type" class="form-select">
        <option value="refund">仅退款</option>
        <option value="return">退货退款</option>
        <option value="exchange">换货</option>
      </select>
    </div>
    <div class="form-group">
      <label class="form-label" for="aftersale-reason">售后原因：</label>
      <textarea id="aftersale-reason" rows="2" class="form-input"></textarea>
    </div>
  `, 'aftersale', true, () => {
    alert('售后申请已提交，请等待处理');
  });
};

// 再次购买
const handleBuyAgain = (order) => {
  alert(`已将【${order.goodsName}】加入购物车`);
};

// 历史浏览标签切换
const handleBrowsingTabChange = (tab) => {
  browsingActiveTab.value = tab;
};

// 清空商品浏览记录
const handleClearGoodsHistory = () => {
  if (confirm('确定清空所有商品浏览记录吗？清空后不可恢复')) {
    browsingHistory.goods = [];
    alert('浏览记录已清空');
  }
};

// 切换商品收藏状态
const handleToggleCollect = (item) => {
  item.collected = !item.collected;
};

// 切换路线收藏状态
const handleToggleRouteCollect = (item) => {
  item.collected = !item.collected;
};

// 路线标签切换
const handleRouteTabChange = (tab) => {
  routeActiveTab.value = tab;
};

// 编辑路线
const handleEditRoute = (route) => {
  openModal('编辑路线', `
    <div class="form-group">
      <label class="form-label" for="route-name">路线名称：</label>
      <input type="text" id="route-name" value="${route.name}" class="form-input">
    </div>
    <div class="form-group">
      <label class="form-label" for="route-spots">包含非遗点：</label>
      <textarea id="route-spots" rows="3" class="form-input">${route.spots}</textarea>
    </div>
    <div class="form-group">
      <label class="form-label" for="route-duration">路线时长：</label>
      <input type="text" id="route-duration" value="${route.duration}" class="form-input">
    </div>
  `, 'edit-route', true, () => {
    const newName = document.getElementById('route-name').value;
    const newSpots = document.getElementById('route-spots').value;
    const newDuration = document.getElementById('route-duration').value;
    
    const targetRoute = routes.myCreate.find(r => r.id === route.id);
    if (targetRoute) {
      targetRoute.name = newName;
      targetRoute.spots = newSpots;
      targetRoute.duration = newDuration;
    }
    alert('路线已更新');
  });
};

// 发布/下架路线
const handlePublishRoute = (route) => {
  const isPublished = route.published;
  const title = isPublished ? '下架路线' : '发布路线';
  const message = isPublished 
    ? '确定下架这条路线吗？下架后仅自己可见。' 
    : '确定发布这条路线吗？发布后其他用户可看到并收藏。';
  
  openModal(title, message, 'publish-route', true, () => {
    route.published = !isPublished;
    alert(isPublished ? '路线已下架' : '路线已发布');
  });
};

// 删除路线
const handleDeleteRoute = (type, index) => {
  openModal('删除路线', '确定删除这条路线吗？删除后不可恢复。', 'delete-route', true, () => {
    routes[type].splice(index, 1);
    alert('路线已删除');
  });
};

// 取消路线收藏
const handleCancelRouteCollect = (index) => {
  routes.myCollect.splice(index, 1);
  alert('已取消收藏');
};

// 编辑草稿
const handleEditDraft = (draft) => {
  openModal('编辑草稿路线', `
    <div class="form-group">
      <label class="form-label" for="draft-name">路线名称：</label>
      <input type="text" id="draft-name" value="${draft.name}" class="form-input">
    </div>
    <div class="form-group">
      <label class="form-label" for="draft-spots">包含非遗点：</label>
      <textarea id="draft-spots" rows="3" class="form-input">${draft.spots}</textarea>
    </div>
  `, 'edit-draft', true, () => {
    const newName = document.getElementById('draft-name').value;
    const newSpots = document.getElementById('draft-spots').value;
    
    const targetDraft = routes.draft.find(d => d.id === draft.id);
    if (targetDraft) {
      targetDraft.name = newName;
      targetDraft.spots = newSpots;
    }
    alert('草稿已更新');
  });
};

// 删除草稿
const handleDeleteDraft = (index) => {
  if (confirm('确定删除这条草稿路线吗？删除后不可恢复')) {
    routes.draft.splice(index, 1);
    alert('草稿已删除');
  }
};

// 查看预约详情
const handleViewAppointmentDetails = (appointment) => {
  openModal('预约详情', `
    <p class="mb-2"><strong>体验项目：</strong>${appointment.name}</p>
    <p class="mb-2"><strong>体验时间：</strong>${appointment.time}</p>
    <p class="mb-2"><strong>体验地点：</strong>${appointment.location}</p>
    <p class="mb-2"><strong>预约人：</strong>${userInfo.nickname}</p>
    <p class="mb-2"><strong>联系电话：</strong>${userInfo.phone}</p>
    <p class="mb-2"><strong>注意事项：</strong></p>
    <ul class="list-disc pl-5 text-sm">
      <li>请提前15分钟到达现场签到</li>
      <li>体验需携带有效身份证件</li>
      <li>可免费携带1名同行人员</li>
    </ul>
  `);
};

// 取消预约
const handleCancelAppointment = (appointment, index) => {
  const experienceTime = new Date(appointment.time.split(' ')[0]);
  const now = new Date();
  const hoursDiff = (experienceTime - now) / (1000 * 60 * 60);
  
  let message = '确定取消该预约吗？';
  if (hoursDiff < 24) {
    message += '<br>距体验时间不足24小时，取消将扣除50积分。';
  }
  
  openModal('取消预约', message, 'cancel-appointment', true, () => {
    appointments.value.splice(index, 1);
    alert(hoursDiff < 24 ? '预约已取消，扣除50积分' : '预约已取消');
  });
};

// 体验评价
const handleExperienceReview = (appointment) => {
  openModal('体验评价', `
    <div class="form-group">
      <label class="form-label">请对本次体验评分：</label>
    </div>
    <div class="form-group">
      <label class="form-label" for="review-content">评价内容：</label>
      <textarea id="review-content" rows="3" placeholder="分享你的体验感受..." class="form-input"></textarea>
    </div>
  `, 'review', true, () => {
    const content = document.getElementById('review-content').value;
    alert(`评价已提交，评分：${currentRating.value}星，内容：${content}`);
  });
};

// 设置评分
const setRating = (rating) => {
  currentRating.value = rating;
};

// 修改密码
const handleChangePassword = () => {
  openModal('修改密码', `
    <div class="form-group">
      <label class="form-label" for="old-password">原密码：</label>
      <input type="password" id="old-password" class="form-input">
    </div>
    <div class="form-group">
      <label class="form-label" for="new-password">新密码：</label>
      <input type="password" id="new-password" class="form-input">
    </div>
    <div class="form-group">
      <label class="form-label" for="confirm-password">确认新密码：</label>
      <input type="password" id="confirm-password" class="form-input">
    </div>
  `, 'change-password', true, () => {
    const newPassword = document.getElementById('new-password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    
    if (newPassword !== confirmPassword) {
      alert('两次输入的密码不一致');
      return;
    }
    
    alert('密码修改成功，请重新登录');
  });
};

// 更换手机号
const handleChangePhone = () => {
  openModal('更换绑定手机号', `
    <div class="form-group">
      <label class="form-label" for="new-phone">新手机号：</label>
      <input type="tel" id="new-phone" class="form-input">
    </div>
    <div class="form-row">
      <input type="text" placeholder="验证码" class="form-input flex-1">
      <button class="btn btn-primary ml-2">获取验证码</button>
    </div>
  `, 'change-phone', true, () => {
    const newPhone = document.getElementById('new-phone').value;
    if (newPhone) {
      userInfo.phone = newPhone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
      alert('手机号更换成功');
    }
  });
};

// 保存设置到本地存储
const saveSettings = () => {
  localStorage.setItem('diansuji-settings', JSON.stringify(settings));
};

// 打开帮助中心
const handleOpenHelpCenter = () => {
  openModal('售后帮助中心', `
    <div class="space-y-3">
      <div>
        <h4 class="font-medium">订单相关</h4>
        <p class="text-sm text-gray-600 mt-1">Q：如何查询我的订单状态？<br>A：在"我的订单"页面可以查看所有订单的当前状态。</p>
      </div>
      <div>
        <h4 class="font-medium">预约相关</h4>
        <p class="text-sm text-gray-600 mt-1">Q：预约可以取消吗？<br>A：可以取消，但体验前24小时内取消将扣除50积分。</p>
      </div>
      <div>
        <h4 class="font-medium">积分相关</h4>
        <p class="text-sm text-gray-600 mt-1">Q：积分如何获取和使用？<br>A：点亮非遗点、发布路线可获得积分，积分可兑换非遗周边产品。</p>
      </div>
    </div>
  `);
};

// 打开意见反馈
const handleOpenFeedback = () => {
  openModal('意见反馈', `
    <div class="form-group">
      <label class="form-label" for="feedback-type">反馈类型：</label>
      <select id="feedback-type" class="form-select">
        <option value="goods">商品问题</option>
        <option value="route">路线问题</option>
        <option value="tech">技术故障</option>
        <option value="other">其他建议</option>
      </select>
    </div>
    <div class="form-group">
      <label class="form-label" for="feedback-content">反馈内容：</label>
      <textarea id="feedback-content" rows="4" placeholder="请详细描述您的问题或建议..." class="form-input"></textarea>
    </div>
    <div class="form-group">
      <label class="form-label">上传图片（选填）：</label>
      <input type="file" multiple accept="image/*" class="file-input">
    </div>
  `, 'feedback', true, () => {
    const type = document.getElementById('feedback-type').value;
    const content = document.getElementById('feedback-content').value;
    alert(`反馈已提交，感谢您的建议！类型：${type}`);
  });
};
</script>

<style>
/* 基础样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* 全局样式 */
.app-container {
  min-height: 100vh;
  background-color: #f5f7fa;
  color: #333;
}

/* 布局组件 */
.header {
  height: 60px;
  padding: 0 20px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 10;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: #1E90FF;
}

.search-container {
  display: flex;
  flex: 1;
  max-width: 400px;
  margin: 0 20px;
}

.search-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-right: none;
  border-radius: 4px 0 0 4px;
  outline: none;
}

.search-input:focus {
  border-color: #1E90FF;
}

.search-btn {
  padding: 0 16px;
  background-color: #1E90FF;
  color: white;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
}

.search-btn:hover {
  background-color: #1E90FF;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.notify-btn {
  background: transparent;
  border: none;
  cursor: pointer;
}

.icon {
  width: 24px;
  height: 24px;
  fill: #1E90FF;
}

.logout-btn {
  padding: 6px 12px;
  background-color: #1E90FF;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.logout-btn:hover {
  background-color: #1E90FF;
}

.main-container {
  display: flex;
  min-height: calc(100vh - 60px);
}

.sidebar {
  width: 220px;
  background-color: white;
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 60px;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.nav-list {
  display: flex;
  flex-direction: column;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  text-align: left;
  width: 100%;
  background: transparent;
  border: none;
  cursor: pointer;
  color: #333;
  font-size: 14px;
  transition: background-color 0.2s;
}

.nav-item:hover {
  background-color: #c2dcf6;
}

.nav-item-active {
  background-color: #c2dcf6;
  color: #1E90FF;
  font-weight: 500;
}

.nav-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.content-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section-title {
  font-size: 18px;
  color: #1E90FF;
  font-weight: 500;
  padding-bottom: 8px;
  border-bottom: 2px solid #1E90FF;
}

.card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-title {
  font-size: 18px;
  color: #1E90FF;
  font-weight: 500;
  padding-bottom: 8px;
  border-bottom: 2px solid #1E90FF;
}

.card-more {
  color: #1E90FF;
  font-size: 14px;
  text-decoration: none;
}

.card-more:hover {
  text-decoration: underline;
}

.card-subtitle {
  font-size: 16px;
  color: #1E90FF;
  font-weight: 500;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}

/* 按钮样式 */
.btn {
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.btn-primary {
  background-color: #1E90FF;
  color: white;
  border: 1px solid #1E90FF;
}

.btn-primary:hover {
  background-color: blue;
  border-color: blue;
}

.btn-outline {
  background-color: transparent;
  color: #1E90FF;
  border: 1px solid #1E90FF;
}

.btn-outline:hover {
  background-color: rgba(229, 62, 62, 0.1);
}

.btn-sm {
  padding: 4px 8px;
  font-size: 12px;
}

.btn-xs {
  padding: 2px 6px;
  font-size: 12px;
}

.btn-collected {
  background-color: #36c5ed;
  color: white;
  border-color: #3691ed;
}

/* 个人信息样式 */
.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.avatar-container {
  position: relative;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  border: 2px solid #1E90FF;
  object-fit: cover;
}

.level-container {
  flex: 1;
}

.level-badge {
  display: inline-block;
  padding: 6px 16px;
  border-radius: 20px;
  font-weight: bold;
  color: white;
  background: linear-gradient(to right, #3297cd, #36a4ed);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.profile-name {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 8px;
}

.profile-detail {
  font-size: 14px;
  color: #666;
  margin-bottom: 16px;
}

.growth-data {
  font-size: 14px;
  color: #666;
  margin-bottom: 16px;
}

.highlight {
  color: #36c5ed;
  font-weight: bold;
}

.exchange-btn {
  padding: 2px 8px;
  background-color: #36b9ed;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}

.profile-actions {
  display: flex;
  gap: 12px;
}

/* 勋章样式 */
.medals-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 16px;
}

.all-medals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.medal-item {
  flex: 1;
  min-width: calc(33.333% - 16px);
  background-color: white;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.medal-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.medal-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin: 0 auto 8px;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.medal-active {
  background-color: #1E90FF;
}

.medal-symbol {
  width: 20px;
  height: 20px;
}

.medal-yunjin {
  background-color: #369bed;
  clip-path: polygon(50% 0%, 61% 35%, 98% 35%, 68% 57%, 79% 91%, 50% 70%, 21% 91%, 32% 57%, 2% 35%, 39% 35%);
}

.medal-suxiu {
  background-color: #36c5ed;
  border-radius: 2px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.medal-zisha {
  background-color: #2d46a0;
  border-radius: 60% 60% 40% 40%;
}

.medal-lacquer {
  background-color: #131b8b;
  border-radius: 4px;
}

.medal-paper {
  background-color: #b3f5e8;
  border-radius: 2px;
}

.medal-food {
  background-color: #5c95cd;
  border-radius: 50%;
}

.medal-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.medal-time {
  font-size: 12px;
  color: #999;
}

.medal-action {
  font-size: 12px;
  color: #1E90FF;
  margin-top: 8px;
}

/* 悬浮提示 */
.tooltip {
  position: absolute;
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  z-index: 100;
  pointer-events: none;
}

/* 订单样式 */
.filter-group {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-item {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.filter-label {
  font-size: 14px;
  white-space: nowrap;
}

.search-filter {
  margin-top: 12px;
}

.form-input {
  padding: 6px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
}

.form-input:focus {
  border-color: #1E90FF;
}

.date-input {
  width: 140px;
}

.search-filter .form-input {
  flex: 1;
  max-width: 300px;
}

.form-select {
  padding: 6px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  background-color: white;
}

.radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.inline-radio {
  gap: 20px;
}

.radio-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  cursor: pointer;
}

.radio-item-block {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  cursor: pointer;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.checkbox-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  cursor: pointer;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-item {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 16px;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 16px;
}

.order-goods {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  flex: 1;
  min-width: 250px;
}

.goods-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.goods-info {
  flex: 1;
}

.goods-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.goods-spec {
  font-size: 12px;
  color: #666;
}

.order-meta {
  flex: 1;
  min-width: 200px;
  font-size: 12px;
  color: #666;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.order-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  min-width: 120px;
}

.order-status {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: white;
}

.status-pending {
  background-color: #36deed;
}

.status-completed {
  background-color: #48bb78;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 24px;
}

.pagination-btn {
  padding: 4px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
  font-size: 14px;
}

.pagination-btn:hover {
  background-color: #f5f5f5;
}

.pagination-disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-disabled:hover {
  background-color: white;
}

.pagination-current {
  padding: 4px 12px;
  font-size: 14px;
}

.pagination-total {
  font-size: 14px;
  color: #666;
}

/* 历史浏览样式 */
.tabs {
  display: flex;
  border-bottom: 1px solid #ddd;
  margin-bottom: -1px;
}

.tab-item {
  padding: 10px 16px;
  background-color: white;
  border: 1px solid #ddd;
  border-bottom: none;
  margin-right: 4px;
  cursor: pointer;
  font-size: 14px;
  border-radius: 8px 8px 0 0;
  transition: all 0.2s;
}

.tab-item:hover {
  background-color: #f5f5f5;
}

.tab-active {
  border-color: #1E90FF;
  border-bottom: 1px solid white;
  color: #1E90FF;
  font-weight: 500;
}

.tab-content {
  margin-top: 0;
  border-top-left-radius: 0;
  border-top-right-radius: 0;
}

.clear-history {
  float: right;
  padding: 4px 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
  font-size: 12px;
}

.clear-history:hover {
  background-color: #f5f5f5;
}

.goods-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 24px;
}

.goods-card {
  width: calc(33.333% - 16px);
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
}

.goods-card-img {
  width: 100%;
  height: 120px;
  object-fit: cover;
}

.goods-card-info {
  padding: 12px;
}

.goods-card-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.goods-card-price {
  font-size: 12px;
  color: #1E90FF;
  margin-bottom: 4px;
}

.goods-card-time {
  font-size: 12px;
  color: #999;
}

.goods-card-actions {
  display: flex;
  gap: 8px;
  padding: 0 12px 12px;
}

.goods-card-actions .btn {
  flex: 1;
}

.route-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.route-item {
  display: flex;
  gap: 16px;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 12px;
}

.route-img {
  width: 128px;
  height: 96px;
  object-fit: cover;
  border-radius: 4px;
}

.route-info {
  flex: 1;
}

.route-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
}

.route-detail {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.route-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 路线管理样式 */
.route-manage-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.route-manage-item {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 12px;
}

.route-manage-img {
  width: 120px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.route-manage-info {
  flex: 1;
  min-width: 250px;
}

.route-manage-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
}

.route-manage-detail {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.route-manage-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 预约样式 */
.appointment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.appointment-item {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 16px;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 16px;
}

.appointment-info {
  flex: 1;
  min-width: 250px;
}

.appointment-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 12px;
}

.appointment-detail {
  font-size: 14px;
  margin-bottom: 8px;
}

.appointment-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 120px;
}

/* 设置样式 */
.settings-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-label {
  font-size: 14px;
}

.setting-value {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #666;
}

/* 开关样式 */
.switch {
  position: relative;
  display: inline-block;
  width: 40px;
  height: 20px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: .4s;
  border-radius: 20px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 16px;
  width: 16px;
  left: 2px;
  bottom: 2px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #1E90FF;
}

input:checked + .slider:before {
  transform: translateX(20px);
}

/* 弹窗样式 */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  padding: 20px;
}

.modal {
  background-color: white;
  border-radius: 8px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  animation: modalFadeIn 0.3s ease;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
}

.modal-title {
  font-size: 18px;
  font-weight: 500;
}

.modal-close {
  background: transparent;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #666;
}

.modal-body {
  padding: 20px;
  max-height: 400px;
  overflow-y: auto;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #eee;
}

/* 表单样式 */
.form-group {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
}

.form-row {
  display: flex;
  gap: 8px;
  align-items: center;
}

.file-input {
  font-size: 14px;
  color: #666;
}

/* 物流时间线 */
.logistics-timeline {
  padding-left: 20px;
  position: relative;
}

.logistics-timeline:before {
  content: '';
  position: absolute;
  left: 6px;
  top: 0;
  bottom: 0;
  width: 2px;
  background-color: #1E90FF;
}

.timeline-item {
  position: relative;
  padding-bottom: 16px;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-item:before {
  content: '';
  position: absolute;
  left: -24px;
  top: 4px;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background-color: #1E90FF;
}

.timeline-status {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.timeline-time {
  font-size: 12px;
  color: #666;
}

/* 评分样式 */
.rating {
  margin: 16px 0;
}

.star {
  font-size: 24px;
  cursor: pointer;
  margin-right: 4px;
}

/* 列表样式 */
.list-disc {
  list-style-type: disc;
}

.space-y-2 > * {
  margin-bottom: 8px;
}

.space-y-2 > *:last-child {
  margin-bottom: 0;
}

.text-sm {
  font-size: 14px;
}

.text-gray-600 {
  color: #666;
}

.font-medium {
  font-weight: 500;
}

.mt-1 {
  margin-top: 4px;
}

.mt-2 {
  margin-top: 8px;
}

.mb-2 {
  margin-bottom: 8px;
}

.mb-3 {
  margin-bottom: 12px;
}

.pl-5 {
  padding-left: 20px;
}
.goods-grid{
  width: 888px;
}

/* 响应式样式 */
@media (max-width: 768px) {
  .sidebar {
    width: 60px;
  }
  
  .nav-text {
    display: none;
  }
  
  .nav-item {
    justify-content: center;
    padding: 12px;
  }
  
  .search-container {
    max-width: none;
    margin: 0 10px;
  }
  
  .medal-item {
    min-width: calc(50% - 16px);
  }
  
  .goods-card {
    width: calc(50% - 16px);
  }
}

@media (max-width: 480px) {
  .header {
    padding: 0 10px;
  }
  
  .logo {
    font-size: 18px;
  }
  
  .search-container {
    display: none;
  }
  
  .medal-item {
    min-width: 100%;
  }
  
  .goods-card {
    width: 100%;
  }
  
  .route-item, .route-manage-item {
    flex-direction: column;
  }
  
  .route-img, .route-manage-img {
    width: 100%;
    height: auto;
  }
  
  .route-actions, .route-manage-actions {
    flex-direction: row;
    justify-content: space-between;
  }
  
  .order-actions {
    align-items: stretch;
  }
  
  .order-actions .btn {
    flex: 1;
  }
}
.notify-btn {
  position: relative;
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.3s ease;
  color: #1E3A8A;
}

.notify-btn:hover {
  background-color: rgba(30, 58, 138, 0.1);
  transform: translateY(-2px);
}

.notify-btn .icon {
  width: 22px;
  height: 22px;
  fill: currentColor;
  transition: transform 0.3s ease;
}

.notify-btn:hover .icon {
  transform: scale(1.1);
}


</style>
