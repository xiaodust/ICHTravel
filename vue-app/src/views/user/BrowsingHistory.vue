<template>
  <section class="content-section">
    <h3 class="section-title">我的浏览历史</h3>
    
    <!-- 历史分类标签 -->
    <div class="history-tabs">
      <button 
        v-for="(tab, index) in historyTabs" 
        :key="index"
        @click="handleTabChange(tab.value)"
        class="tab-btn"
        :class="{ 'tab-active': activeTab === tab.value }"
      >
        {{ tab.label }}
        <span v-if="tab.badge" class="tab-badge">{{ tab.badge }}</span>
      </button>
    </div>
    
    <!-- 时间筛选 -->
    <div class="time-filter">
      <button 
        v-for="(filter, index) in timeFilters" 
        :key="index"
        @click="handleTimeFilterChange(filter.value)"
        class="time-btn"
        :class="{ 'time-active': activeTimeFilter === filter.value }"
      >
        {{ filter.label }}
      </button>
    </div>
    
    <!-- 历史列表 -->
    <div class="history-list">
      <div 
        v-for="(item, index) in filteredHistory" 
        :key="index"
        class="history-item"
      >
        <img 
          :src="item.imgUrl" 
          alt="浏览内容" 
          class="history-img"
        >
        <div class="history-info">
          <h4 class="history-title">
            <a href="#" @click.prevent="handleViewDetail(item)">{{ item.title }}</a>
          </h4>
          <p class="history-meta">
            <span class="history-category">{{ item.category }}</span>
            <span class="history-time">{{ formatTime(item.viewTime) }}</span>
          </p>
          <p class="history-desc">{{ item.description }}</p>
        </div>
        <div class="history-actions">
          <button 
            @click="handleAddToFavorites(item, index)"
            class="action-btn"
            :class="{ 'favorited': item.favorited }"
          >
            {{ item.favorited ? '已收藏' : '收藏' }}
          </button>
          <button 
            @click="handleRemoveHistory(index)"
            class="action-btn remove-btn"
          >
            删除
          </button>
        </div>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div v-if="filteredHistory.length === 0" class="empty-state">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" width="48" height="48" fill="#ccc">
          <path d="M12 3c-4.97 0-9 4.03-9 9H0l4 4 4-4H5c0-3.87 3.13-7 7-7s7 3.13 7 7-3.13 7-7 7c-1.93 0-3.68-.79-4.94-2.06l-1.42 1.42C8.27 19.99 10.11 21 12 21c4.97 0 9-4.03 9-9s-4.03-9-9-9zm-1 5v5l4.28 2.54.72-1.21-3.5-2.08V8H11z"/>
        </svg>
      </div>
      <p class="empty-text">暂无浏览记录</p>
      <button @click="handleClearFilter" class="btn btn-outline">查看全部记录</button>
    </div>
    
    <!-- 批量操作 -->
    <div class="batch-actions" v-if="filteredHistory.length > 0">
      <button 
        @click="handleClearAll"
        class="btn btn-outline clear-all-btn"
      >
        清空历史记录
      </button>
      <div class="batch-info">
        共 {{ filteredHistory.length }} 条记录 | 
        上次清除：{{ lastClearTime }}
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, reactive, computed, inject } from 'vue';

// 注入弹窗方法
const modal = inject('modal');

// 历史记录数据
const historyItems = ref([
  {
    id: 1,
    title: '苏州园林非遗体验之旅',
    category: '非遗路线',
    description: '探访拙政园、留园，体验苏绣和评弹艺术',
    imgUrl: 'https://picsum.photos/id/1074/300/200',
    viewTime: '2024-10-20T15:30:00',
    favorited: false,
    type: 'route'
  },
  {
    id: 2,
    title: '宜兴紫砂壶制作工艺',
    category: '传统工艺',
    description: '国家级非遗，了解紫砂壶的制作流程与文化内涵',
    imgUrl: 'https://picsum.photos/id/1036/300/200',
    viewTime: '2024-10-19T09:15:00',
    favorited: true,
    type: 'craft'
  },
  {
    id: 3,
    title: '南京云锦博物馆',
    category: '非遗场馆',
    description: '中国唯一的云锦专业博物馆，展示云锦的历史与技艺',
    imgUrl: 'https://picsum.photos/id/1059/300/200',
    viewTime: '2024-10-18T14:45:00',
    favorited: false,
    type: 'venue'
  }
]);

// 分类标签
const historyTabs = ref([
  { label: '全部', value: 'all',  },
  { label: '非遗路线', value: 'route', },
  { label: '传统工艺', value: 'craft', },
  { label: '非遗场馆', value: 'venue', }
]);

// 时间筛选
const timeFilters = ref([
  { label: '全部时间', value: 'all' },
  { label: '今天', value: 'today' },
  { label: '近7天', value: '7days' },
  { label: '近30天', value: '30days' }
]);

// 状态管理
const activeTab = ref('all');
const activeTimeFilter = ref('all');
const lastClearTime = ref('2024-09-15');

// 格式化时间
const formatTime = (timeStr) => {
  const date = new Date(timeStr);
  const now = new Date();
  const diffMs = now - date;
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
  
  if (diffDays === 0) {
    return '今天 ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  } else if (diffDays === 1) {
    return '昨天 ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  } else if (diffDays < 7) {
    return diffDays + '天前';
  } else {
    return date.toLocaleDateString();
  }
};

// 计算属性：筛选后的历史记录
const filteredHistory = computed(() => {
  return historyItems.value.filter(item => {
    // 类型筛选
    if (activeTab.value !== 'all' && item.type !== activeTab.value) {
      return false;
    }
    
    // 时间筛选
    const itemDate = new Date(item.viewTime);
    const now = new Date();
    const diffMs = now - itemDate;
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
    
    if (activeTimeFilter.value === 'today' && diffDays > 0) {
      return false;
    } else if (activeTimeFilter.value === '7days' && diffDays > 7) {
      return false;
    } else if (activeTimeFilter.value === '30days' && diffDays > 30) {
      return false;
    }
    
    return true;
  }).sort((a, b) => new Date(b.viewTime) - new Date(a.viewTime));
});

// 切换标签
const handleTabChange = (value) => {
  activeTab.value = value;
};

// 切换时间筛选
const handleTimeFilterChange = (value) => {
  activeTimeFilter.value = value;
};

// 查看详情
const handleViewDetail = (item) => {
  modal.open(item.title, `
    <div class="detail-container">
      <img src="${item.imgUrl}" alt="${item.title}" class="detail-img">
      <div class="detail-content">
        <p><strong>类别：</strong>${item.category}</p>
        <p><strong>简介：</strong>${item.description}</p>
        <p><strong>浏览时间：</strong>${new Date(item.viewTime).toLocaleString()}</p>
      </div>
    </div>
  `, '', false);
};

// 添加到收藏
const handleAddToFavorites = (item, index) => {
  const targetItem = historyItems.value.find(i => i.id === item.id);
  if (targetItem) {
    targetItem.favorited = !targetItem.favorited;
    alert(targetItem.favorited ? '已添加到收藏' : '已取消收藏');
  }
};

// 删除单条记录
const handleRemoveHistory = (index) => {
  const item = filteredHistory.value[index];
  modal.open('删除记录', `确定要删除"${item.title}"的浏览记录吗？`, 'confirm', true, () => {
    historyItems.value = historyItems.value.filter(i => i.id !== item.id);
  });
};

// 清空所有记录
const handleClearAll = () => {
  modal.open('清空历史', '确定要删除所有浏览记录吗？此操作不可恢复。', 'confirm', true, () => {
    historyItems.value = [];
    lastClearTime.value = new Date().toLocaleDateString();
  });
};

// 清除筛选条件
const handleClearFilter = () => {
  activeTab.value = 'all';
  activeTimeFilter.value = 'all';
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

/* 标签样式 */
.history-tabs {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.tab-btn {
  padding: 6px 16px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 6px;
}

.tab-btn:hover {
  border-color: #1E90FF;
}

.tab-active {
  background-color: #1E90FF;
  color: white;
  border-color: #1E90FF;
}

.tab-badge {
  background-color: #ff4d4f;
  color: white;
  border-radius: 10px;
  padding: 0 6px;
  font-size: 12px;
}

/* 时间筛选 */
.time-filter {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.time-btn {
  padding: 4px 12px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}

.time-btn:hover {
  border-color: #1E90FF;
}

.time-active {
  color: #1E90FF;
  border-color: #1E90FF;
  background-color: rgba(30, 144, 255, 0.1);
}

/* 历史列表 */
.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-item {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

.history-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.history-info {
  flex: 1;
  min-width: 200px;
}

.history-title {
  font-size: 16px;
  margin-bottom: 8px;
}

.history-title a {
  color: #333;
  text-decoration: none;
}

.history-title a:hover {
  color: #1E90FF;
  text-decoration: underline;
}

.history-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
  margin-bottom: 6px;
}

.history-desc {
  font-size: 14px;
  color: #666;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.history-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 80px;
}

.action-btn {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  border: 1px solid #ddd;
  background-color: white;
}

.action-btn:hover {
  background-color: #f5f5f5;
}

.favorited {
  color: #1E90FF;
  border-color: #1E90FF;
}

.remove-btn {
  color: #ff4d4f;
  border-color: #ff4d4f;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.empty-icon {
  margin-bottom: 16px;
}

.empty-text {
  color: #999;
  margin-bottom: 20px;
}

/* 批量操作 */
.batch-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.clear-all-btn {
  color: #ff4d4f;
  border-color: #ff4d4f;
}

.clear-all-btn:hover {
  background-color: rgba(255, 77, 79, 0.1);
}

.batch-info {
  font-size: 12px;
  color: #666;
}

/* 详情弹窗样式 */
.detail-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-img {
  width: 100%;
  max-height: 200px;
  object-fit: cover;
  border-radius: 4px;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 按钮样式 */
.btn {
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.btn-outline {
  background-color: transparent;
  border: 1px solid;
}
</style>