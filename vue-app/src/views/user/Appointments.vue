<template>
  <section class="appointments-container">
    <div class="section-header">
      <h2 class="section-title">我的非遗预约</h2>
      <button @click="handleCreateAppointment" class="btn btn-primary">
        + 新增预约
      </button>
    </div>

    <!-- 预约筛选 -->
    <div class="filter-bar">
      <div class="filter-group">
        <label class="filter-label">预约状态：</label>
        <select v-model="statusFilter" class="form-select" @change="handleStatusChange">
          <option value="all">全部状态</option>
          <option value="pending">待确认</option>
          <option value="confirmed">已确认</option>
          <option value="completed">已完成</option>
          <option value="cancelled">已取消</option>
        </select>
      </div>

      <div class="filter-group">
        <label class="filter-label">非遗类型：</label>
        <select v-model="typeFilter" class="form-select" @change="handleTypeChange">
          <option value="all">全部类型</option>
          <option value="crafts">传统工艺</option>
          <option value="performance">表演艺术</option>
          <option value="food">传统美食</option>
          <option value="custom">民俗活动</option>
        </select>
      </div>

      <div class="search-box">
        <input
          type="text"
          v-model="searchKeyword"
          placeholder="搜索预约项目/传承人..."
          class="search-input"
          @input="handleSearch"
        >
        <button class="search-btn" @click="handleSearch">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="#666">
            <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- 预约列表 -->
    <div class="appointments-list">
      <div 
        v-for="appointment in filteredAppointments" 
        :key="appointment.id"
        class="appointment-card"
        :class="{'highlight': appointment.isSoon}"
      >
        <div class="appointment-header">
          <div class="appointment-status" :class="appointment.status">
            {{ formatStatus(appointment.status) }}
          </div>
          <button 
            v-if="canCancel(appointment)"
            @click="handleCancelAppointment(appointment)"
            class="cancel-btn"
          >
            取消预约
          </button>
        </div>

        <div class="appointment-body">
          <img 
            :src="appointment.image" 
            alt="非遗项目图片" 
            class="appointment-img"
            @error="appointment.image = defaultImg"
          >
          
          <div class="appointment-info">
            <h3 class="appointment-title">{{ appointment.title }}</h3>
            
            <div class="info-row">
              <span class="info-label">传承人：</span>
              <span class="info-value">{{ appointment.inheritor }}</span>
            </div>
            
            <div class="info-row">
              <span class="info-label">时间：</span>
              <span class="info-value">{{ formatDate(appointment.date) }} {{ appointment.time }}</span>
            </div>
            
            <div class="info-row">
              <span class="info-label">地点：</span>
              <span class="info-value">{{ appointment.location }}</span>
            </div>
            
            <div class="info-row">
              <span class="info-label">参与人数：</span>
              <span class="info-value">{{ appointment.peopleCount }}人</span>
            </div>
          </div>
        </div>

        <div class="appointment-footer">
          <div class="appointment-type" :class="appointment.category">
            {{ formatCategory(appointment.category) }}
          </div>
          
          <div class="appointment-actions">
            <button 
              @click="handleViewDetails(appointment)"
              class="detail-btn"
            >
              查看详情
            </button>
            
            <button 
              v-if="appointment.status === 'completed'"
              @click="handleWriteReview(appointment)"
              class="review-btn"
            >
              写评价
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="filteredAppointments.length === 0" class="empty-state">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" width="64" height="64" fill="#ccc">
          <path d="M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 0c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm2 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
        </svg>
      </div>
      <p class="empty-text">暂无预约记录</p>
      <button @click="handleCreateAppointment" class="btn btn-primary">
        立即预约非遗体验
      </button>
    </div>
  </section>
</template>

<script setup>
import { ref, reactive, computed, inject } from 'vue';

// 注入弹窗工具
const modal = inject('modal');

// 默认图片
const defaultImg = 'https://picsum.photos/id/237/200/150';

// 预约数据
const appointments = ref([
  {
    id: 1,
    title: '剪纸技艺体验课',
    image: 'https://picsum.photos/id/119/300/200',
    inheritor: '张桂英',
    date: '2025-10-25',
    time: '14:00-16:00',
    location: '民俗文化中心2楼',
    peopleCount: 2,
    status: 'confirmed',
    category: 'crafts',
    contact: '138****5678',
    notes: '请提前15分钟到场，材料已准备',
    isSoon: true
  },
  {
    id: 2,
    title: '京剧脸谱绘制体验',
    image: 'https://picsum.photos/id/169/300/200',
    inheritor: '李明远',
    date: '2025-10-20',
    time: '10:00-12:00',
    location: '戏曲博物馆',
    peopleCount: 1,
    status: 'completed',
    category: 'performance',
    contact: '139****8765',
    notes: '适合初学者参与',
    isSoon: false
  },
  {
    id: 3,
    title: '传统月饼制作工坊',
    image: 'https://picsum.photos/id/292/300/200',
    inheritor: '王秀兰',
    date: '2025-10-28',
    time: '09:30-11:30',
    location: '非遗美食城',
    peopleCount: 3,
    status: 'pending',
    category: 'food',
    contact: '136****2345',
    notes: '需提前确认食物过敏信息',
    isSoon: false
  },
  {
    id: 4,
    title: '端午龙舟制作技艺',
    image: 'https://picsum.photos/id/135/300/200',
    inheritor: '陈建国',
    date: '2025-10-15',
    time: '全天',
    location: '水乡民俗村',
    peopleCount: 5,
    status: 'cancelled',
    category: 'custom',
    contact: '135****6789',
    notes: '团体预约项目',
    isSoon: false
  }
]);

// 筛选条件
const statusFilter = ref('all');
const typeFilter = ref('all');
const searchKeyword = ref('');

// 格式化状态文本
const formatStatus = (status) => {
  const statusMap = {
    pending: '待确认',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  };
  return statusMap[status] || status;
};

// 格式化分类文本
const formatCategory = (category) => {
  const categoryMap = {
    crafts: '传统工艺',
    performance: '表演艺术',
    food: '传统美食',
    custom: '民俗活动'
  };
  return categoryMap[category] || category;
};

// 格式化日期
const formatDate = (dateStr) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'short'
  });
};

// 检查是否可以取消预约
const canCancel = (appointment) => {
  // 只有待确认和已确认的预约可以取消
  if (!['pending', 'confirmed'].includes(appointment.status)) {
    return false;
  }
  
  // 提前24小时以上可以取消
  const appointmentDate = new Date(`${appointment.date}T${appointment.time.split('-')[0]}`);
  const now = new Date();
  const hoursDiff = (appointmentDate - now) / (1000 * 60 * 60);
  
  return hoursDiff > 24;
};

// 筛选后的预约列表
const filteredAppointments = computed(() => {
  return appointments.value.filter(appt => {
    // 状态筛选
    if (statusFilter.value !== 'all' && appt.status !== statusFilter.value) {
      return false;
    }
    
    // 类型筛选
    if (typeFilter.value !== 'all' && appt.category !== typeFilter.value) {
      return false;
    }
    
    // 搜索筛选
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase();
      if (
        !appt.title.toLowerCase().includes(keyword) &&
        !appt.inheritor.toLowerCase().includes(keyword)
      ) {
        return false;
      }
    }
    
    return true;
  });
});

// 筛选事件处理
const handleStatusChange = () => {};
const handleTypeChange = () => {};
const handleSearch = () => {};

// 创建新预约
const handleCreateAppointment = () => {
  modal.open('新增非遗预约', `
    <div class="appointment-form">
      <div class="form-group">
        <label>非遗项目：</label>
        <select class="form-select">
          <option value="">请选择项目</option>
          <option value="crafts">传统工艺 - 剪纸技艺</option>
          <option value="performance">表演艺术 - 京剧体验</option>
          <option value="food">传统美食 - 月饼制作</option>
          <option value="custom">民俗活动 - 龙舟制作</option>
        </select>
      </div>
      <div class="form-group">
        <label>预约日期：</label>
        <input type="date" class="form-select" min="${new Date().toISOString().split('T')[0]}">
      </div>
      <div class="form-group">
        <label>预约时间：</label>
        <select class="form-select">
          <option value="09:00-11:00">09:00-11:00</option>
          <option value="14:00-16:00">14:00-16:00</option>
          <option value="16:30-18:30">16:30-18:30</option>
        </select>
      </div>
      <div class="form-group">
        <label>参与人数：</label>
        <input type="number" class="form-select" min="1" value="1">
      </div>
      <div class="form-group">
        <label>联系电话：</label>
        <input type="tel" class="form-select" placeholder="用于接收预约通知">
      </div>
      <div class="form-group">
        <label>备注信息：</label>
        <textarea class="form-textarea" rows="2" placeholder="如有特殊需求请说明"></textarea>
      </div>
    </div>
  `, 'confirm', true, () => {
    // 模拟创建成功
    const newId = Math.max(...appointments.value.map(a => a.id)) + 1;
    appointments.value.unshift({
      id: newId,
      title: '新预约的非遗体验',
      image: 'https://picsum.photos/id/175/300/200',
      inheritor: '非遗传承人',
      date: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      time: '14:00-16:00',
      location: '非遗文化中心',
      peopleCount: 1,
      status: 'pending',
      category: 'crafts',
      contact: '138****1234',
      notes: '新预约项目',
      isSoon: false
    });
    modal.close();
    modal.toast('预约申请已提交，等待确认');
  });
};

// 取消预约
const handleCancelAppointment = (appt) => {
  modal.open(
    '取消预约',
    `确定要取消"${appt.title}"的预约吗？\n预约时间：${formatDate(appt.date)} ${appt.time}`,
    'confirm',
    true,
    () => {
      appt.status = 'cancelled';
      modal.toast('预约已取消');
    }
  );
};

// 查看详情
const handleViewDetails = (appt) => {
  modal.open(appt.title + ' - 预约详情', `
    <div class="appointment-detail">
      <img src="${appt.image}" class="detail-img" alt="${appt.title}">
      
      <div class="detail-info">
        <div class="detail-row">
          <span class="detail-label">状态：</span>
          <span class="detail-value status-${appt.status}">${formatStatus(appt.status)}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">传承人：</span>
          <span class="detail-value">${appt.inheritor}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">时间：</span>
          <span class="detail-value">${formatDate(appt.date)} ${appt.time}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">地点：</span>
          <span class="detail-value">${appt.location}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">参与人数：</span>
          <span class="detail-value">${appt.peopleCount}人</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">联系方式：</span>
          <span class="detail-value">${appt.contact}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">备注：</span>
          <span class="detail-value">${appt.notes}</span>
        </div>
      </div>
    </div>
  `);
};

// 写评价
const handleWriteReview = (appt) => {
  modal.open(`评价 - ${appt.title}`, `
    <div class="review-form">
      <div class="form-group">
        <label>评分：</label>
        <div class="star-rating">
          <span class="star" v-for="i in 5" :key="i" @click="setRating(i)">★</span>
        </div>
      </div>
      <div class="form-group">
        <label>评价内容：</label>
        <textarea class="form-textarea" rows="4" placeholder="分享您的体验感受..."></textarea>
      </div>
      <div class="form-group">
        <label>上传图片（可选）：</label>
        <input type="file" multiple accept="image/*" class="file-upload">
      </div>
    </div>
  `, 'confirm', true, () => {
    modal.toast('评价提交成功，感谢您的反馈');
  });
};
</script>

<style scoped>
.appointments-container {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.section-title {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.btn {
  padding: 8px 16px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.btn-primary {
  background-color: #1E90FF;
  color: white;
}

.btn-primary:hover {
  background-color: #0b7dda;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 20px;
  align-items: center;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: #666;
}

.form-select {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  background-color: white;
}

.search-box {
  display: flex;
  margin-left: auto;
}

.search-input {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 4px 0 0 4px;
  font-size: 14px;
  width: 240px;
}

.search-btn {
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  border-left: none;
  border-radius: 0 4px 4px 0;
  padding: 0 12px;
  cursor: pointer;
}

/* 预约列表 */
.appointments-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.appointment-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transition: transform 0.2s;
}

.appointment-card:hover {
  transform: translateY(-2px);
}

.appointment-card.highlight {
  border: 1px solid #ff7d00;
}

/* 预约头部 */
.appointment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  background-color: #f5f5f5;
  border-bottom: 1px solid #eee;
}

.appointment-status {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.appointment-status.pending {
  background-color: #faad14;
}

.appointment-status.confirmed {
  background-color: #52c41a;
}

.appointment-status.completed {
  background-color: #1890ff;
}

.appointment-status.cancelled {
  background-color: #f5222d;
}

.cancel-btn {
  padding: 4px 12px;
  background-color: #f5222d;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.cancel-btn:hover {
  background-color: #d91117;
}

/* 预约主体 */
.appointment-body {
  display: flex;
  gap: 15px;
  padding: 15px;
}

.appointment-img {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.appointment-info {
  flex-grow: 1;
}

.appointment-title {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #333;
}

.info-row {
  display: flex;
  margin-bottom: 6px;
  font-size: 13px;
}

.info-label {
  color: #666;
  width: 70px;
  flex-shrink: 0;
}

.info-value {
  color: #333;
  word-break: break-all;
}

/* 预约底部 */
.appointment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background-color: #fafafa;
  border-top: 1px solid #eee;
}

.appointment-type {
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: white;
}

.appointment-type.crafts {
  background-color: #722ed1;
}

.appointment-type.performance {
  background-color: #eb2f96;
}

.appointment-type.food {
  background-color: #fa8c16;
}

.appointment-type.custom {
  background-color: #13c2c2;
}

.appointment-actions {
  display: flex;
  gap: 8px;
}

.detail-btn, .review-btn {
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.detail-btn {
  background-color: transparent;
  border: 1px solid #1E90FF;
  color: #1E90FF;
}

.detail-btn:hover {
  background-color: rgba(30, 144, 255, 0.1);
}

.review-btn {
  background-color: transparent;
  border: 1px solid #fa8c16;
  color: #fa8c16;
}

.review-btn:hover {
  background-color: rgba(250, 140, 22, 0.1);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 50px 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.empty-icon {
  margin-bottom: 20px;
}

.empty-text {
  color: #999;
  margin-bottom: 25px;
  font-size: 16px;
}

/* 弹窗表单样式 */
.appointment-form, .review-form {
  min-width: 400px;
}

.form-group {
  margin-bottom: 15px;
}

.form-textarea {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
}

.file-upload {
  margin-top: 5px;
  padding: 5px;
}

/* 详情弹窗样式 */
.appointment-detail {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.detail-img {
  width: 100%;
  max-height: 200px;
  object-fit: cover;
  border-radius: 4px;
}

.detail-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-row {
  display: flex;
  font-size: 14px;
}

.detail-label {
  width: 80px;
  flex-shrink: 0;
  color: #666;
}

.detail-value {
  flex-grow: 1;
  color: #333;
}

.status-pending {
  color: #faad14;
}

.status-confirmed {
  color: #52c41a;
}

.status-completed {
  color: #1890ff;
}

.status-cancelled {
  color: #f5222d;
}

/* 评分星星 */
.star-rating {
  display: flex;
  gap: 5px;
  margin-top: 5px;
}

.star {
  font-size: 24px;
  color: #ddd;
  cursor: pointer;
  transition: color 0.2s;
}

.star:hover, .star.active {
  color: #faad14;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-box {
    margin-left: 0;
    width: 100%;
  }
  
  .search-input {
    width: 100%;
  }
  
  .appointment-body {
    flex-direction: column;
  }
  
  .appointment-img {
    width: 100%;
    height: auto;
  }
  
  .appointment-form, .review-form {
    min-width: 0;
    width: 100%;
  }
}
</style>