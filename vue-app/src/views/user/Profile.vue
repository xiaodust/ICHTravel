<template>
  <section class="content-section">
    <!-- 个人信息卡片 -->
    <div class="card">
      <div class="profile-header">
        <!-- 头像区域 -->
        <div class="avatar-container">
          <img 
            :src="userInfo.avatar" 
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
          注册时间：{{ userInfo.registerTime }} 加入点苏记<br>
          <span v-if="userInfo.signature">个人签名：{{ userInfo.signature }}</span>
        </p>
        
        <!-- 成长数据 -->
        <div class="growth-data">
          累计点亮非遗点：<span class="highlight">{{ userInfo.growth.points }}</span>个 | 
          创作路线数：<span class="highlight">{{ userInfo.growth.routes }}</span>条 | 
          获得勋章：<span class="highlight">{{ userInfo.growth.medals }}</span>枚 | 
          可用积分：<span class="highlight">{{ userInfo.growth.score }}</span>分
        </div>
        
        <!-- 操作按钮区域 - 已清空 -->
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
      
      <!-- 勋章悬浮提示已移除 -->
    </div>
  </section>
</template>

<script setup>
import { reactive, onMounted } from 'vue';
import axios from 'axios';

// 移除模态框相关代码

// 用户信息 - 从localStorage读取后端返回的userInfo数据
const userInfo = reactive({
  // 默认值
  nickname: '苏韵非遗爱好者',
  name: '',
  phone: '',
  registerTime: '2024.06.18',
  Signature: '',
  avatar: 'https://q8.itc.cn/q_70/images03/20250304/f5873423f8b044d78aa8cf036bc132e0.jpeg',
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

// 移除了不需要的功能函数



// 从后端获取用户信息
const fetchUserInfo = async () => {
  try {
    console.log('开始从后端获取用户信息...');
    
    // 优先从sessionStorage获取userId（与后端保持一致）
    let userId = sessionStorage.getItem('userId');
    console.log('从sessionStorage获取的userId:', userId);
    
    // 如果sessionStorage中没有，再从localStorage获取
    if (!userId) {
      const savedUserInfo = localStorage.getItem('userInfo');
      if (savedUserInfo) {
        const parsedUserInfo = JSON.parse(savedUserInfo);
        // 优先使用id字段（后端使用的字段名）
        userId = parsedUserInfo.id;
        console.log('从localStorage获取的用户id:', userId);
      }
    }
    
    if (!userId) {
      console.error('未找到用户ID，无法获取用户信息。请确认用户已正确登录，并且session中保存了userId。');
      alert('用户身份验证失败，请重新登录');
      return;
    }
    
    console.log('准备请求用户信息，userId:', userId);
    
    // 调用后端接口 - 直接指向后端8080端口，避免代理配置问题
    console.log('发起API请求:', `http://localhost:8080/api/user/${userId}`);
    const response = await axios.get(`http://localhost:8080/api/user/${userId}`);
    
    console.log('API响应数据:', response.data);
    
    if (response.data && response.data.success) {
      const userData = response.data.data;
      console.log('从后端获取的用户信息:', userData);
      
      // 更新用户信息
      // 更新个人名称（优先使用nickName，其次使用name）
      if (userData.nickName) {
        userInfo.nickname = userData.nickName;
      } else if (userData.name) {
        userInfo.nickname = userData.name;
      }
      
      // 更新手机号 - 优先使用number字段（与后端保持一致）
      if (userData.number) {
        console.log('使用number字段的手机号:', userData.number);
        // 检查是否是有效的手机号格式
        if (/^1[3-9]\d{9}$/.test(userData.number)) {
          // 手机号脱敏显示
          userInfo.phone = userData.number.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
          console.log('脱敏后显示的手机号:', userInfo.phone);
        } else {
          console.warn('number字段包含无效的手机号格式:', userData.number);
          userInfo.phone = userData.number; // 原样显示，避免正则错误
        }
      }
      
      // 更新注册时间 - 使用正确的字段名gmtCreated
      if (userData.gmtCreated) {
        try {
          // 格式化时间戳为YYYY.MM.DD格式
          const date = new Date(userData.gmtCreated);
          if (!isNaN(date.getTime())) {
            userInfo.registerTime = `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
          }
        } catch (dateError) {
          console.warn('日期格式化失败:', dateError);
        }
      }
      
      // 更新个人签名
      if (userData.signature) {
        userInfo.signature = userData.signature;
      }
      
      // 更新头像
      if (userData.avatar) {
        userInfo.avatar = userData.avatar;
      }
      
      // 将最新的用户信息保存到localStorage，以便其他地方使用
      try {
        localStorage.setItem('userInfo', JSON.stringify(userData));
      } catch (storageError) {
        console.warn('保存用户信息到localStorage失败:', storageError);
      }
      
    } else {
      console.error('获取用户信息失败:', response.data?.message || '未知错误');
      console.error('完整响应内容:', response.data);
    }
  } catch (error) {
    console.error('获取用户信息时发生异常:', error);
    console.error('错误详情:', error.response || error.message || error);
    
    // 检查是否是网络或连接问题
    if (error.code === 'ERR_NETWORK' || error.message?.includes('Network')) {
      console.warn('网络连接问题，尝试使用缓存数据');
    }
    
    // 如果后端请求失败，尝试从localStorage获取（作为降级方案）
    loadUserInfoFromLocalStorage();
  } finally {
    // 打印加载完成后的userInfo状态
    console.log('用户信息加载完成，最终状态:', userInfo);
  }
};

// 从localStorage读取用户信息（作为降级方案）
const loadUserInfoFromLocalStorage = () => {
  try {
    console.log('开始从localStorage读取用户信息(降级方案)...');
    const savedUserInfo = localStorage.getItem('userInfo');
    
    if (savedUserInfo) {
      const parsedUserInfo = JSON.parse(savedUserInfo);
      
      // 更新个人名称
      if (parsedUserInfo.nickName) {
        userInfo.nickname = parsedUserInfo.nickName;
      } else if (parsedUserInfo.name) {
        userInfo.nickname = parsedUserInfo.name;
      }
      
      // 更新手机号
      if (parsedUserInfo.number) {
        if (/^1[3-9]\d{9}$/.test(parsedUserInfo.number)) {
          userInfo.phone = parsedUserInfo.number.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
        } else {
          userInfo.phone = parsedUserInfo.number;
        }
      } else if (parsedUserInfo.phone) {
        userInfo.phone = parsedUserInfo.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
      }
      
      // 更新注册时间
      if (parsedUserInfo.gmtCreated) {
        try {
          const date = new Date(parsedUserInfo.gmtCreated);
          if (!isNaN(date.getTime())) {
            userInfo.registerTime = `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
          }
        } catch (dateError) {
          console.warn('日期格式化失败:', dateError);
        }
      }
      
      // 更新个人签名
      if (parsedUserInfo.Signature) {
        userInfo.Signature = parsedUserInfo.Signature;
      }
      
      // 更新头像
      if (parsedUserInfo.avatar) {
        userInfo.avatar = parsedUserInfo.avatar;
      }
    }
  } catch (error) {
    console.error('读取localStorage用户信息失败:', error);
  }
};

// 页面加载时初始化
onMounted(() => {
  // 加载用户信息 - 从后端获取
  fetchUserInfo();
});

// 移除了积分兑换功能
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

/* 按钮样式已移除 */

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

/* 移除了操作按钮相关样式 */

/* 已清空操作按钮区域 */

/* 表单样式已移除 */

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

/* 悬浮提示样式已移除 */

/* 多余表单样式和列表样式已移除 */

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