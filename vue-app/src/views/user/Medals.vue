<template>
  <section class="medals-container">
    <!-- 页面标题与统计信息 -->
    <div class="section-header">
      <h2 class="page-title">我的非遗勋章</h2>
      <div class="stats-panel">
        <div class="stat-item">
          <span class="stat-value">{{ unlockedCount }}</span>
          <span class="stat-label">已解锁</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ totalCount }}</span>
          <span class="stat-label">总勋章</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ userLevel }}</span>
          <span class="stat-label">当前等级</span>
        </div>
      </div>
    </div>

    <!-- 等级进度条 -->
    <div class="level-progress-card">
      <div class="level-info">
        <span class="level-title">非遗文化探索者 Lv.{{ userLevel }}</span>
        <span class="progress-text">{{ currentPoints }}/{{ nextLevelPoints }} 积分</span>
      </div>
      <div class="progress-container">
        <div 
          class="progress-fill" 
          :style="{ width: progressPercent + '%' }"
        ></div>
      </div>
      <div class="level-hint">
        距离升级还需 {{ nextLevelPoints - currentPoints }} 积分
      </div>
    </div>

    <!-- 勋章分类标签 -->
    <div class="medal-categories">
      <button 
        v-for="category in categories" 
        :key="category.value"
        @click="activeCategory = category.value"
        class="category-btn"
        :class="{ active: activeCategory === category.value }"
      >
        {{ category.label }}
      </button>
    </div>

    <!-- 勋章展示网格 -->
    <div class="medals-grid">
      <div 
        v-for="medal in filteredMedals" 
        :key="medal.id"
        class="medal-card"
        :class="{ 'locked': !medal.unlocked }"
        @click="handleMedalClick(medal)"
      >
        <!-- 勋章图标区域 -->
        <div class="medal-icon-wrapper">
          <div class="medal-frame" :class="medal.rarity">
            <img 
              :src="medal.unlocked ? medal.icon : lockedIcon" 
              :alt="medal.name" 
              class="medal-icon"
              @error="medal.icon = defaultIcon"
            >
            <div v-if="!medal.unlocked" class="lock-overlay">
              <svg viewBox="0 0 24 24" width="24" height="24" fill="white">
                <path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zM9 6c0-1.66 1.34-3 3-3s3 1.34 3 3v2H9V6zm9 14H6V10h12v10zm-6-3c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2z"/>
              </svg>
            </div>
          </div>
          <div class="medal-rarity-badge" :class="medal.rarity">
            {{ getRarityText(medal.rarity) }}
          </div>
        </div>

        <!-- 勋章信息 -->
        <div class="medal-info">
          <h3 class="medal-name">{{ medal.name }}</h3>
          <p class="medal-desc">
            {{ medal.unlocked ? medal.description : '完成特定任务即可解锁' }}
          </p>
          
          <!-- 已解锁勋章的额外信息 -->
          <div v-if="medal.unlocked" class="medal-meta">
            <span class="unlock-date">获得于 {{ formatDate(medal.unlockDate) }}</span>
            <span class="medal-points">+{{ medal.points }} 积分</span>
          </div>
          
          <!-- 未解锁勋章的提示 -->
          <div v-if="!medal.unlocked && showHintId === medal.id" class="unlock-hint">
            解锁条件：{{ medal.unlockCondition }}
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态提示（当前分类无解锁勋章） -->
    <div v-if="isEmptyState" class="empty-state">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" width="64" height="64" fill="#ccc">
          <path d="M12 7.27l4.28 10.43-3.47-1.53L12 .69 11.19 6.14 7.72 17.7 12 7.27M22 12l-4.24 10.19L12 18.42 6.24 22.19 2 12l4.24-10.19L12 5.58l5.76 3.73L22 12z"/>
        </svg>
      </div>
      <h3 class="empty-title">暂无解锁勋章</h3>
      <p class="empty-desc">完成对应非遗体验任务即可解锁该分类勋章</p>
    </div>
  </section>
</template>

<script setup>
import { ref, computed } from 'vue';

// 静态资源
const lockedIcon = 'https://picsum.photos/id/1062/100/100'; // 锁定状态图标
const defaultIcon = 'https://picsum.photos/id/1071/100/100'; // 默认勋章图标

// 勋章分类
const categories = [
  { label: '全部勋章', value: 'all' },
  { label: '探索类', value: 'exploration' },
  { label: '技艺类', value: 'skill' },
  { label: '收藏类', value: 'collection' },
  { label: '社交类', value: 'social' }
];

// 勋章数据
const medals = ref([
  {
    id: 1,
    name: '非遗初探者',
    icon: 'https://picsum.photos/id/119/100/100',
    description: '完成首次非遗项目体验，开启文化探索之旅',
    category: 'exploration',
    rarity: 'common',
    points: 10,
    unlocked: true,
    unlockDate: '2025-09-10',
    unlockCondition: '完成任意1个非遗项目体验'
  },
  {
    id: 2,
    name: '刺绣匠人',
    icon: 'https://picsum.photos/id/129/100/100',
    description: '掌握3种以上传统刺绣针法并获得传承人认可',
    category: 'skill',
    rarity: 'rare',
    points: 30,
    unlocked: true,
    unlockDate: '2025-10-05',
    unlockCondition: '完成3次刺绣类非遗体验并获得认证'
  },
  {
    id: 3,
    name: '路线规划师',
    icon: 'https://picsum.photos/id/131/100/100',
    description: '创建5条优质非遗探索路线，帮助他人发现非遗之美',
    category: 'exploration',
    rarity: 'epic',
    points: 50,
    unlocked: false,
    unlockDate: null,
    unlockCondition: '创建并发布5条非遗路线'
  },
  {
    id: 4,
    name: '非遗收藏家',
    icon: 'https://picsum.photos/id/145/100/100',
    description: '收藏10个不同类别的非遗项目，构建个人文化宝库',
    category: 'collection',
    rarity: 'rare',
    points: 25,
    unlocked: true,
    unlockDate: '2025-09-22',
    unlockCondition: '收藏10个不同类别的非遗项目'
  },
  {
    id: 5,
    name: '文化传播者',
    icon: 'https://picsum.photos/id/152/100/100',
    description: '邀请5位新用户加入平台，共同传播非遗文化',
    category: 'social',
    rarity: 'epic',
    points: 40,
    unlocked: false,
    unlockDate: null,
    unlockCondition: '成功邀请5位新用户注册'
  },
  {
    id: 6,
    name: '戏曲鉴赏家',
    icon: 'https://picsum.photos/id/169/100/100',
    description: '体验3种不同剧种的非遗表演，感受传统戏曲魅力',
    category: 'exploration',
    rarity: 'common',
    points: 15,
    unlocked: true,
    unlockDate: '2025-10-12',
    unlockCondition: '体验3种不同的戏曲类非遗项目'
  },
  {
    id: 7,
    name: '匠心传承者',
    icon: 'https://picsum.photos/id/175/100/100',
    description: '掌握1种非遗核心技艺，达到独立创作水平',
    category: 'skill',
    rarity: 'legendary',
    points: 100,
    unlocked: false,
    unlockDate: null,
    unlockCondition: '通过传承人认证的非遗技艺考核'
  },
  {
    id: 8,
    name: '非遗挚友',
    icon: 'https://picsum.photos/id/180/100/100',
    description: '与其他非遗爱好者建立连接，共同探索文化魅力',
    category: 'social',
    rarity: 'common',
    points: 10,
    unlocked: true,
    unlockDate: '2025-09-18',
    unlockCondition: '添加3位平台好友'
  }
]);

// 用户等级与积分数据
const userLevel = ref(4);
const currentPoints = ref(85);
const nextLevelPoints = ref(120);

// 状态管理
const activeCategory = ref('all');
const showHintId = ref(null);

// 计算属性
// 筛选当前分类的勋章
const filteredMedals = computed(() => {
  return activeCategory.value === 'all'
    ? medals.value
    : medals.value.filter(medal => medal.category === activeCategory.value);
});

// 已解锁勋章数量
const unlockedCount = computed(() => {
  return medals.value.filter(medal => medal.unlocked).length;
});

// 总勋章数量
const totalCount = computed(() => medals.value.length);

// 进度百分比
const progressPercent = computed(() => {
  return Math.min((currentPoints.value / nextLevelPoints.value) * 100, 100);
});

// 是否显示空状态
const isEmptyState = computed(() => {
  return filteredMedals.value.length > 0 && 
         filteredMedals.value.every(medal => !medal.unlocked);
});

// 工具函数
// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });
};

// 获取稀有度文本
const getRarityText = (rarity) => {
  const rarityMap = {
    common: '普通',
    rare: '稀有',
    epic: '史诗',
    legendary: '传说'
  };
  return rarityMap[rarity] || rarity;
};

// 事件处理
// 点击勋章
const handleMedalClick = (medal) => {
  if (!medal.unlocked) {
    // 显示解锁提示，3秒后自动隐藏
    showHintId.value = medal.id;
    setTimeout(() => {
      showHintId.value = null;
    }, 3000);
  }
};
</script>

<style scoped>
.medals-container {
  padding: 20px;
  background-color: #f8f9fa;
  min-height: calc(100vh - 60px);
}

/* 头部区域 */
.section-header {
  margin-bottom: 25px;
}

.page-title {
  margin: 0 0 15px 0;
  font-size: 22px;
  color: #2c3e50;
  font-weight: 600;
}

.stats-panel {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.stat-item {
  background-color: white;
  padding: 12px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.05);
  text-align: center;
  min-width: 100px;
}

.stat-value {
  display: block;
  font-size: 18px;
  font-weight: bold;
  color: #1E90FF;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 13px;
  color: #666;
}

/* 等级进度卡片 */
.level-progress-card {
  background-color: white;
  border-radius: 8px;
  padding: 15px 20px;
  margin-bottom: 25px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.05);
}

.level-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.level-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.progress-text {
  font-size: 14px;
  color: #666;
}

.progress-container {
  height: 8px;
  background-color: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-fill {
  height: 100%;
  background-color: #1E90FF;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.level-hint {
  font-size: 13px;
  color: #666;
}

/* 分类标签 */
.medal-categories {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  overflow-x: auto;
  padding-bottom: 5px;
}

.category-btn {
  padding: 6px 16px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.category-btn.active {
  background-color: #1E90FF;
  color: white;
  border-color: #1E90FF;
}

.category-btn:hover:not(.active) {
  border-color: #1E90FF;
  color: #1E90FF;
}

/* 勋章网格 */
.medals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.medal-card {
  background-color: white;
  border-radius: 10px;
  padding: 18px;
  box-shadow: 0 3px 10px rgba(0,0,0,0.07);
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
}

.medal-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 15px rgba(0,0,0,0.1);
}

.medal-card.locked {
  opacity: 0.8;
  filter: grayscale(1);
}

/* 勋章图标区域 */
.medal-icon-wrapper {
  position: relative;
  width: 100px;
  height: 100px;
  margin: 0 auto 15px;
}

.medal-frame {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

/* 不同稀有度的勋章边框 */
.medal-frame.common {
  background-color: #f5f5f5;
  border: 2px solid #a0a0a0;
}

.medal-frame.rare {
  background-color: #f0f9f0;
  border: 2px solid #14b866;
}

.medal-frame.epic {
  background-color: #f7f3ff;
  border: 2px solid #7928ca;
}

.medal-frame.legendary {
  background-color: #fff9e6;
  border: 2px solid #ffb100;
  box-shadow: 0 0 15px rgba(255, 177, 0, 0.3);
}

.medal-icon {
  width: 65px;
  height: 65px;
  object-fit: contain;
}

.lock-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0,0,0,0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.medal-rarity-badge {
  position: absolute;
  bottom: -5px;
  right: -5px;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
  color: white;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

/* 稀有度徽章颜色 */
.medal-rarity-badge.common {
  background-color: #a0a0a0;
}

.medal-rarity-badge.rare {
  background-color: #14b866;
}

.medal-rarity-badge.epic {
  background-color: #7928ca;
}

.medal-rarity-badge.legendary {
  background-color: #ffb100;
}

/* 勋章信息区域 */
.medal-info {
  text-align: center;
}

.medal-name {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.medal-desc {
  margin: 0 0 10px 0;
  font-size: 13px;
  color: #666;
  line-height: 1.5;
  min-height: 40px;
}

.medal-meta {
  display: flex;
  justify-content: center;
  gap: 15px;
  font-size: 12px;
  color: #999;
}

.unlock-hint {
  margin-top: 10px;
  padding: 6px 8px;
  background-color: #fff8e6;
  border-radius: 4px;
  font-size: 12px;
  color: #fa8c16;
  line-height: 1.4;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 3px 10px rgba(0,0,0,0.07);
}

.empty-icon {
  margin-bottom: 20px;
}

.empty-title {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
}

.empty-desc {
  margin: 0;
  font-size: 14px;
  color: #666;
  max-width: 300px;
  margin: 0 auto;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .medals-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  }
  
  .medal-name {
    font-size: 14px;
  }
  
  .medal-desc {
    font-size: 12px;
    min-height: auto;
  }
  
  .medal-icon-wrapper {
    width: 80px;
    height: 80px;
  }
  
  .medal-icon {
    width: 50px;
    height: 50px;
  }
  
  .stat-item {
    flex: 1;
    min-width: auto;
  }
}
</style>