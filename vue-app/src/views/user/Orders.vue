<template>
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
</template>

<script setup>
import { ref, reactive, computed, inject } from 'vue';

// 注入弹窗方法
const modal = inject('modal');

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
const activeTab = ref('orders');

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
  modal.open('订单支付', `
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
  
  modal.open('取消订单', message, 'cancel-order', true, () => {
    // 从列表中移除订单
    orders.value = orders.value.filter(o => o.id !== order.id);
    alert(hoursDiff < 24 ? '订单已取消，扣除50积分' : '订单已取消');
  });
};

// 查看物流
const handleViewLogistics = (order) => {
  modal.open('物流信息', `
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
  modal.open('申请售后', `
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
</script>

<style scoped>
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
  background-color: #187bcd;
  border-color: #187bcd;
}

.btn-outline {
  background-color: transparent;
  color: #1E90FF;
  border: 1px solid #1E90FF;
}

.btn-outline:hover {
  background-color: rgba(30, 144, 255, 0.1);
}

.btn-sm {
  padding: 4px 8px;
  font-size: 12px;
}

/* 订单筛选样式 */
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

/* 订单列表样式 */
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
  background-color: #FF9800;
}

.status-completed {
  background-color: #48bb78;
}

/* 分页样式 */
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

/* 表单样式 */
.form-group {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
}

.highlight {
  color: #1E90FF;
  font-weight: bold;
}

/* 响应式样式 */
@media (max-width: 480px) {
  .order-actions {
    align-items: stretch;
  }
  
  .order-actions .btn {
    flex: 1;
  }
}
</style>