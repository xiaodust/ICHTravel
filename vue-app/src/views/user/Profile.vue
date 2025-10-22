<template>
  <section class="content-section">
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
</template>

<script setup>
import { reactive, ref, inject } from 'vue';

// 注入弹窗方法
const modal = inject('modal');

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

// 勋章提示
const tooltipVisible = ref(false);
const tooltipText = ref('');
const tooltipLeft = ref(0);
const tooltipTop = ref(0);

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

// 编辑个人信息
const handleEditProfile = () => {
  modal.open('编辑个人信息', `
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
  modal.open('非遗守护官等级规则', `
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
  modal.open('积分兑换', `
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
</script>

<style scoped>
.content-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
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
  color: #1E90FF;
  font-weight: bold;
}

.exchange-btn {
  padding: 2px 8px;
  background-color: #1E90FF;
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

.medal-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.medal-time {
  font-size: 12px;
  color: #999;
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

/* 表单样式 */
.form-group {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
}

.form-input {
  padding: 6px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  width: 100%;
}

.form-input:focus {
  border-color: #1E90FF;
}

.form-select {
  padding: 6px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  background-color: white;
  width: 100%;
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

.mt-2 {
  margin-top: 8px;
}

.pl-5 {
  padding-left: 20px;
}

/* 响应式样式 */
@media (max-width: 768px) {
  .medal-item {
    min-width: calc(50% - 16px);
  }
}

@media (max-width: 480px) {
  .medal-item {
    min-width: 100%;
  }
}
</style>