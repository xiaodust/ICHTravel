<template>
  <section class="content-section">
    <div class="section-header">
      <h3 class="section-title">我的非遗路线</h3>
      <button @click="handleCreateRoute" class="btn btn-primary create-btn">
        + 创建新路线
      </button>
    </div>
    
    <!-- 路线分类 -->
    <div class="route-filters">
      <div class="filter-group">
        <label class="filter-label">路线类型：</label>
        <div class="radio-group">
          <label class="radio-item">
            <input 
              type="radio" 
              name="routeType" 
              value="all" 
              v-model="routeFilter.type"
            > 全部
          </label>
          <label class="radio-item">
            <input 
              type="radio" 
              name="routeType" 
              value="self" 
              v-model="routeFilter.type"
            > 自创路线
          </label>
          <label class="radio-item">
            <input 
              type="radio" 
              name="routeType" 
              value="collected" 
              v-model="routeFilter.type"
            > 收藏路线
          </label>
        </div>
      </div>
      
      <div class="filter-group">
        <label class="filter-label">非遗类别：</label>
        <div class="checkbox-group">
          <label class="checkbox-item">
            <input 
              type="checkbox" 
              name="routeCategory" 
              value="crafts" 
              v-model="routeFilter.categories"
            > 传统工艺
          </label>
          <label class="checkbox-item">
            <input 
              type="checkbox" 
              name="routeCategory" 
              value="performance" 
              v-model="routeFilter.categories"
            > 表演艺术
          </label>
          <label class="checkbox-item">
            <input 
              type="checkbox" 
              name="routeCategory" 
              value="food" 
              v-model="routeFilter.categories"
            > 传统美食
          </label>
        </div>
      </div>
    </div>
    
    <!-- 路线列表 -->
    <div class="route-grid">
      <div 
        v-for="(route, index) in filteredRoutes" 
        :key="route.id"
        class="route-card"
      >
        <div class="route-img-container">
          <img 
            :src="route.coverImg" 
            alt="路线封面" 
            class="route-cover"
            @error="handleImgError(route)"
          >
          <span class="route-tag" v-if="route.isSelfCreated">自创路线</span>
          <button 
            @click="handleFavoriteRoute(route)"
            class="favorite-btn"
            :class="{ 'favorited': route.favorited }"
            aria-label="收藏路线"
          >
            <svg viewBox="0 0 24 24" width="18" height="18">
              <path d="M1 21h4V9H1v12zm22-11c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.59 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-1.91l-.01-.01L23 10z"/>
            </svg>
          </button>
        </div>
        
        <div class="route-info">
          <h4 class="route-title">{{ route.title }}</h4>
          
          <div class="route-meta">
            <span class="route-duration">{{ route.duration }}</span>
            <span class="route-distance">{{ route.distance }}</span>
            <span class="route-difficulty">{{ route.difficulty }}</span>
          </div>
          
          <div class="route-stops">
            <span class="stop-icon" v-for="n in route.stopCount" :key="n"></span>
            <span class="stop-count">{{ route.stopCount }}个非遗点</span>
          </div>
          
          <div class="route-actions">
            <button 
              @click="handleViewRouteDetail(route)"
              class="btn btn-outline btn-sm"
            >
              查看详情
            </button>
            
            <button 
              v-if="route.isSelfCreated"
              @click="handleEditRoute(route)"
              class="btn btn-outline btn-sm"
            >
              编辑
            </button>
            
            <button 
              v-if="route.isSelfCreated"
              @click="handleDeleteRoute(route)"
              class="btn btn-outline btn-sm delete-btn"
            >
              删除
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div v-if="filteredRoutes.length === 0" class="empty-state">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" width="48" height="48" fill="#ccc">
          <path d="M16.24 16.26c-.77-.82-.77-2.14 0-2.96l5.2-5.54c.78-.84.78-2.17 0-3-.78-.84-2.05-.84-2.83 0l-5.2 5.54L8.6 4.16c-.78-.84-2.05-.84-2.83 0-.78.84-.78 2.17 0 3l5.2 5.54-5.2 5.54c-.78.84-.78 2.17 0 3 .78.84 2.05.84 2.83 0l5.2-5.54 5.2 5.54c.78.84 2.05.84 2.83 0 .78-.84.78-2.17 0-3l-5.2-5.54zM12 14c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2z"/>
        </svg>
      </div>
      <p class="empty-text">暂无路线记录</p>
      <button @click="handleCreateRoute" class="btn btn-primary">创建第一条路线</button>
    </div>
  </section>
</template>

<script setup>
import { ref, reactive, computed, inject } from 'vue';

// 注入弹窗方法
const modal = inject('modal');

// 路线数据
const myRoutes = ref([
  {
    id: 101,
    title: '苏绣文化探寻之旅',
    coverImg: 'https://picsum.photos/id/1025/500/300',
    duration: '1日',
    distance: '25km',
    difficulty: '轻松',
    stopCount: 4,
    categories: ['crafts'],
    isSelfCreated: true,
    favorited: true,
    description: '探访苏绣博物馆，观摩非遗传承人刺绣技艺，亲手体验基础针法，选购正宗苏绣作品。'
  },
  {
    id: 102,
    title: '景德镇陶瓷艺术游',
    coverImg: 'https://picsum.photos/id/1074/500/300',
    duration: '2日',
    distance: '40km',
    difficulty: '中等',
    stopCount: 6,
    categories: ['crafts'],
    isSelfCreated: false,
    favorited: true,
    description: '参观古窑民俗博览区，体验拉坯、上釉等制瓷工序，走访陶瓷创意集市，参与陶艺大师工作坊。'
  },
  {
    id: 103,
    title: '成都川剧非遗体验',
    coverImg: 'https://picsum.photos/id/1060/500/300',
    duration: '1日',
    distance: '15km',
    difficulty: '轻松',
    stopCount: 3,
    categories: ['performance'],
    isSelfCreated: true,
    favorited: false,
    description: '观看正宗川剧变脸表演，学习川剧唱腔基础，体验勾画脸谱，探访川剧艺术博物馆。'
  },
  {
    id: 104,
    title: '广州早茶非遗寻味',
    coverImg: 'https://picsum.photos/id/292/500/300',
    duration: '半日',
    distance: '8km',
    difficulty: '轻松',
    stopCount: 5,
    categories: ['food'],
    isSelfCreated: false,
    favorited: true,
    description: '走访百年茶楼，学习广式点心制作技艺，了解早茶文化历史，品尝非遗茶点。'
  }
]);

// 筛选条件
const routeFilter = reactive({
  type: 'all',
  categories: []
});

// 处理图片加载失败
const handleImgError = (route) => {
  route.coverImg = 'https://picsum.photos/id/200/500/300';
};

// 筛选后的路线
const filteredRoutes = computed(() => {
  return myRoutes.value.filter(route => {
    // 类型筛选
    if (routeFilter.type === 'self' && !route.isSelfCreated) return false;
    if (routeFilter.type === 'collected' && !route.favorited) return false;
    
    // 类别筛选
    if (routeFilter.categories.length > 0) {
      const hasCommon = route.categories.some(cat => routeFilter.categories.includes(cat));
      if (!hasCommon) return false;
    }
    
    return true;
  });
});

// HTML特殊字符转义函数
const escapeHtml = (str) => {
  if (!str) return '';
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
};

// 创建新路线
const handleCreateRoute = () => {
  modal.open('创建非遗路线', `
    <div class="create-form">
      <div class="form-group">
        <label>路线名称：</label>
        <input type="text" class="form-control" placeholder="输入路线名称" />
      </div>
      <div class="form-group">
        <label>非遗类别：</label>
        <select class="form-control">
          <option value="crafts">传统工艺</option>
          <option value="performance">表演艺术</option>
          <option value="food">传统美食</option>
        </select>
      </div>
      <div class="form-group">
        <label>路线描述：</label>
        <textarea class="form-control" rows="3" placeholder="描述路线特色"></textarea>
      </div>
    </div>
  `, 'confirm', true, () => {
    // 模拟创建成功
    const newId = Math.max(...myRoutes.value.map(r => r.id)) + 1;
    myRoutes.value.push({
      id: newId,
      title: '新创建的路线',
      coverImg: 'https://picsum.photos/id/1040/500/300',
      duration: '1日',
      distance: '20km',
      difficulty: '轻松',
      stopCount: 3,
      categories: ['crafts'],
      isSelfCreated: true,
      favorited: false,
      description: '用户新创建的非遗探索路线'
    });
    modal.close();
  });
};

// 收藏路线
const handleFavoriteRoute = (route) => {
  route.favorited = !route.favorited;
  modal.toast(route.favorited ? '收藏成功' : '取消收藏');
};

// 查看路线详情
const handleViewRouteDetail = (route) => {
  const title = escapeHtml(route.title);
  const description = escapeHtml(route.description);
  modal.open(title, `
    <div class="route-detail">
      <img src="${route.coverImg}" class="detail-img" alt="${title}" />
      <div class="detail-content">
        <p><strong>路线特色：</strong>${description}</p>
        <p><strong>时长：</strong>${route.duration}</p>
        <p><strong>距离：</strong>${route.distance}</p>
        <p><strong>难度：</strong>${route.difficulty}</p>
        <p><strong>非遗点数量：</strong>${route.stopCount}个</p>
      </div>
    </div>
  `);
};

// 编辑路线
const handleEditRoute = (route) => {
  const title = escapeHtml(route.title);
  const description = escapeHtml(route.description);
  modal.open('编辑路线', `
    <div class="edit-form">
      <div class="form-group">
        <label>路线名称：</label>
        <input type="text" class="form-control" value="${title}" />
      </div>
      <div class="form-group">
        <label>路线描述：</label>
        <textarea class="form-control" rows="3">${description}</textarea>
      </div>
    </div>
  `, 'confirm', true, () => {
    modal.toast('路线编辑成功');
  });
};

// 删除路线
const handleDeleteRoute = (route) => {
  const title = escapeHtml(route.title);
  modal.open('删除路线', `确定要删除"${title}"吗？`, 'confirm', true, () => {
    myRoutes.value = myRoutes.value.filter(r => r.id !== route.id);
  });
};
</script>

<style scoped>
/* 样式部分保持不变 */
.content-section {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  color: #333;
  margin: 0;
}

.create-btn {
  padding: 6px 16px;
  background-color: #1E90FF;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

/* 筛选区域 */
.route-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
  padding: 15px;
  background-color: white;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-label {
  font-size: 14px;
  color: #666;
}

.radio-group, .checkbox-group {
  display: flex;
  gap: 15px;
}

.radio-item, .checkbox-item {
  font-size: 14px;
  cursor: pointer;
}

/* 路线列表 */
.route-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.route-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transition: transform 0.2s;
}

.route-card:hover {
  transform: translateY(-3px);
}

.route-img-container {
  position: relative;
  height: 180px;
}

.route-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.route-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  background-color: #ff7d00;
  color: white;
  font-size: 12px;
  padding: 3px 8px;
  border-radius: 4px;
}

.favorite-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: rgba(255,255,255,0.8);
  border: none;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.favorite-btn:hover {
  background-color: white;
}

.favorite-btn.favorited svg path {
  fill: #ff4d4f;
}

/* 路线信息 */
.route-info {
  padding: 15px;
}

.route-title {
  font-size: 16px;
  margin: 0 0 10px 0;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.route-meta {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  font-size: 12px;
  color: #666;
}

.route-duration, .route-distance, .route-difficulty {
  display: flex;
  align-items: center;
  gap: 4px;
}

.route-stops {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
}

.stop-icon {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #1E90FF;
}

.stop-count {
  font-size: 12px;
  color: #666;
}

.route-actions {
  display: flex;
  gap: 8px;
}

.btn {
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.btn-outline {
  background-color: transparent;
  border: 1px solid #1E90FF;
  color: #1E90FF;
}

.btn-outline:hover {
  background-color: rgba(30, 144, 255, 0.1);
}

.btn-sm {
  padding: 4px 8px;
  font-size: 12px;
}

.delete-btn {
  border-color: #ff4d4f;
  color: #ff4d4f;
}

.delete-btn:hover {
  background-color: rgba(255, 77, 79, 0.1);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.empty-icon {
  margin-bottom: 16px;
}

.empty-text {
  color: #999;
  margin-bottom: 20px;
}

/* 弹窗样式 */
.form-group {
  margin-bottom: 15px;
}

.form-control {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.route-detail {
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

.detail-content p {
  margin: 5px 0;
  font-size: 14px;
}
</style>