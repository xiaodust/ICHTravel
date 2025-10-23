<template>
  <div class="home-container">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="logo" @click="$router.push('/home')">点苏记</div>
      <ul class="nav-menu">
        <li class="nav-item">
          <a 
            href="javascript:;" 
            class="nav-link" 
            :class="{ active: $route.path === '/home' }"
            @click="$router.push('/home')"
          >
            首页
          </a>
        </li>
        <li class="nav-item">
          <a href="javascript:;" class="nav-link" @click="$router.push('/heritage-map')">非遗地图</a>
        </li>
        <li class="nav-item">
          <a href="javascript:;" class="nav-link" @click="$router.push('/heritage-mall')">非遗商城</a>
        </li>
        <li class="nav-item">
          <a href="javascript:;" class="nav-link" @click="$router.push('/3d-workshop')">3D工坊</a>
        </li>
        <li class="nav-item">
          <a href="javascript:;" class="nav-link" @click="$router.push('/heritage-commit')">交流平台</a>
        </li>
        <li class="nav-item">
          <a href="javascript:;" class="nav-link" @click="gotoUserCenter">个人中心</a>
        </li>
      </ul>
      <div class="user-section">
        <template v-if="isLoggedIn">
          <div class="user-info" @click="gotoUserCenter">
            <img :src="userInfo.avatar" alt="用户头像" class="user-avatar">
            <span class="user-name">{{ userInfo.nickName || userInfo.name || '用户' }}</span>
          </div>
          <button class="logout-btn" @click="handleLogout">登出</button>
        </template>
        <button v-else class="login-btn" @click="$router.push('/login')">登录</button>
      </div>
    </nav>

    <!-- 顶部Banner -->
    <div class="banner">
      <img src="/image/微信图片_20251010141521_51_4.jpg" alt="江苏非遗Banner">
      <div class="banner-text">
        <h1>点苏记 · 江苏非遗</h1>
        <p>探索13市非遗瑰宝，定制专属文化之旅</p>
      </div>
    </div>

    <!-- 核心：全屏横向图片展示区（带多层动态效果） -->
    <div class="full-width-gallery">
      <div class="gallery-wrapper">
        <!-- 图片1：南京云锦 -->
        <div class="gallery-item" @click="$router.push('/3d-workshop')">
          <img src="/image/南京云锦.webp" alt="南京云锦" class="gallery-img">
          <div class="item-overlay">
            <div class="overlay-content">
              <h3>南京云锦</h3>
              <p>中国三大名锦之首 · 国家级非遗</p>
              <span class="view-btn" >查看详情 →</span>
            </div>
          </div>
        </div>
        <!-- 图片2：苏州苏绣 -->
        <div class="gallery-item" @click="$router.push('/3d-workshop')">
          <img src="/image/苏州苏绣.webp" alt="苏州苏绣" class="gallery-img">
          <div class="item-overlay">
            <div class="overlay-content">
              <h3>苏州苏绣</h3>
              <p>中国四大名绣之一 · 世界级非遗</p>
              <span class="view-btn">查看详情 →</span>
            </div>
          </div>
        </div>
        <!-- 图片3：扬州漆器 -->
        <div class="gallery-item" @click="$router.push('/3d-workshop')">
          <img src="/image/扬州瓷器.webp" alt="扬州漆器" class="gallery-img">
          <div class="item-overlay">
            <div class="overlay-content">
              <h3>扬州漆器</h3>
              <p>千年工艺传承 · 国家级非遗</p>
              <span class="view-btn">查看详情 →</span>
            </div>
          </div>
        </div>
        <!-- 图片4：无锡泥人 -->
        <div class="gallery-item" @click="$router.push('/3d-workshop')">
          <img src="/image/无锡泥人.jpg" alt="无锡泥人" class="gallery-img">
          <div class="item-overlay">
            <div class="overlay-content">
              <h3>无锡泥人</h3>
              <p>民间美术瑰宝 · 国家级非遗</p>
              <span class="view-btn">查看详情 →</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 地图区域 -->
    <!-- <div class="map-section">
      <div class="map-container">
        <div class="jiangsu-map"></div>
      </div>
    </div> -->

    <!-- 功能入口 -->
    <div class="func-entry">
      <div class="entry-card" @click="gotoPage('route')">
        <img src="https://picsum.photos/60/60?random=5" alt="路线规划">
        <h3>定制非遗路线</h3>
        <p>按兴趣生成专属游览计划</p>
      </div>
      <div class="entry-card" @click="gotoPage('mall')">
        <img src="https://picsum.photos/60/60?random=6" alt="非遗商城">
        <h3>非遗好物商城</h3>
        <p>正宗非遗产品直邮到家</p>
      </div>
      <div class="entry-card" @click="gotoPage('workshop')">
        <img src="https://picsum.photos/60/60?random=7" alt="3D工坊">
        <h3>3D云游工坊</h3>
        <p>沉浸式体验非遗制作过程</p>
      </div>
      <div class="entry-card" @click="gotoPage('user')">
        <img src="https://picsum.photos/60/60?random=8" alt="个人中心">
        <h3>非遗交流社区</h3>
        <p>在线实时探讨非遗文化</p>
      </div>
    </div>

    <!-- AI助手悬浮球 -->
    <div 
      id="float-ball"
      @mousedown="handleBallStart"
      @touchstart="handleBallStart"
      @click="togglePanel"
      :style="{ 
        left: ballLeft, 
        top: ballTop, 
        transition: isDragging ? 'none' : 'all 0.3s ease' 
      }"
    >
      🤖
    </div>

    <!-- AI对话面板 -->
    <div 
      id="ai-panel" 
      class="panel-slide"
      :style="{ 
        display: isPanelShow ? 'block' : 'none',
        left: panelLeft + 'px',
        top: panelTop + 'px'
      }"
    >
      <div class="panel-header">
        <div class="header-title">
          <span>💬</span>
          <span>非遗AI助手</span>
        </div>
        <button class="close-btn" @click="togglePanel">✕</button>
      </div>
      <div id="chat-messages" class="chat-messages">
        <div 
          class="message"
          :class="msg.isUser ? 'user-message' : 'ai-message'"
          v-for="(msg, index) in chatMessages"
          :key="index"
        >
          <div v-if="!msg.isUser" class="avatar ai-avatar">🤖</div>
          <div class="message-content" :class="msg.isUser ? 'user-content' : 'ai-content'">
            <p>{{ msg.content }}</p>
          </div>
          <div v-if="msg.isUser" class="avatar user-avatar">👤</div>
        </div>
      </div>
      <div class="input-area">
        <div class="input-container">
          <input 
            type="text" 
            id="chat-input" 
            placeholder="输入你的问题（如：南京有哪些非遗？）..."
            v-model="inputMessage"
            @keypress="handleEnterSend"
          >
          <button class="send-btn" @click="sendMessage">→</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';

// 用户登录状态
const isLoggedIn = ref(false);
// 用户信息
const userInfo = ref({
  name: '',
  avatar: 'https://q8.itc.cn/q_70/images03/20250304/f5873423f8b044d78aa8cf036bc132e0.jpeg' // 默认头像
});

// 检测登录状态 - 依赖localStorage
const checkLoginStatus = () => {
  try {
    // 从localStorage检查是否有用户信息
    const savedUserInfo = localStorage.getItem('userInfo');
    if (savedUserInfo) {
      // 有用户信息则认为已登录
      isLoggedIn.value = true;
      userInfo.value = JSON.parse(savedUserInfo);
      // 如果有userId，获取最新用户信息
      if (userInfo.value.id) {
        fetchUserInfo(userInfo.value.id);
      }
    } else {
      // 无用户信息则认为未登录
      isLoggedIn.value = false;
    }
  } catch (error) {
    console.error('检查登录状态异常:', error);
    isLoggedIn.value = false;
  }
};

// 获取用户详细信息
const fetchUserInfo = async (userId) => {
  try {
    const response = await axios.get(`/api/user/${userId}`);
    if (response.data && response.data.success && response.data.data) {
      const userData = response.data.data;
      // 更新用户信息，只使用User对象中有的字段
      const updatedUserInfo = {
        ...userInfo.value,
        name: userData.name || userInfo.value.name,
        nickName: userData.nickName || userInfo.value.nickName || '',
        number: userData.number || userInfo.value.number || '',
        Signature: userData.Signature || userInfo.value.Signature || '',
        avatar: userData.avatar || userInfo.value.avatar,
        gmtCreated: userData.gmtCreated || userInfo.value.gmtCreated,
       
      };
      
      userInfo.value = updatedUserInfo;
      localStorage.setItem('userInfo', JSON.stringify(updatedUserInfo));
    }
  } catch (error) {
    console.error('获取用户信息异常:', error);
    // 错误时保留本地缓存的用户信息
  }
};

// 跳转到个人中心
const gotoUserCenter = () => {
  if (isLoggedIn.value) {
    // 已登录跳转到个人信息界面
    router.push('/user-center/profile');
  } else {
    // 未登录跳转到登录界面
    router.push('/login');
  }
};

// 处理登出
const handleLogout = async () => {
  try {
    // 清理本地登录状态
    isLoggedIn.value = false;
    localStorage.removeItem('userInfo');
    
    // 尝试调用后端登出接口（可选）
    try {
      await axios.get('/api/user/logout');
    } catch (error) {
      console.log('后端登出接口调用失败，但不影响前端登出');
    }
    
    alert('登出成功');
  } catch (error) {
    console.error('登出处理异常:', error);
  }
};

// 组件挂载时检查登录状态
onMounted(() => {
  checkLoginStatus();
});

const route = useRoute();
const router = useRouter();

// 页面刷新控制
// if (!localStorage.getItem('hasRefreshed')) {
//   localStorage.setItem('hasRefreshed', 'true');
//   window.location.reload();
// } else {
//   localStorage.removeItem('hasRefreshed');
// }

// // 新增：图片详情跳转
// const gotoDetail = (id) => {
//   router.push(`/heritage-detail?id=${id}`);
// };

// 原有：地标弹窗控制（保留，如需启用可取消注释）
const showPopup = (city) => {
  const popup = document.getElementById(`popup-${city}`);
  if (popup) {
    popup.style.display = "block";
    if (city === "nanjing") {
      popup.style.top = "calc(35% + 40px)";
      popup.style.left = "calc(42% - 100px)";
    } else if (city === "suzhou") {
      popup.style.top = "calc(40% + 40px)";
      popup.style.left = "calc(55% - 100px)";
    }
  }
};
const hidePopup = (city) => {
  const popup = document.getElementById(`popup-${city}`);
  if (popup) popup.style.display = "none";
};

// 原有：生成路线逻辑
const gotoRoute = (city) => {
  const cityName = city === 'nanjing' ? '南京' : '苏州';
  alert(`生成${cityName}非遗路线`);
  router.push({ path: '/route', query: { city } });
};

// 原有：功能入口跳转逻辑
const gotoPage = (pageUrl) => {
  const pageName = pageUrl.split('.')[0];
  const routeMap = {
    route: '/heritage-map',
    mall: '/heritage-mall',
    workshop: '/3d-workshop',
    user: '/heritage-commit'
  };
  if (routeMap[pageName]) {
    router.push(routeMap[pageName]);
  }
};

// WebSocket连接实现的AI助手逻辑
const ballLeft = ref('');
const ballTop = ref('');
const isDragging = ref(false);
const startX = ref(0);
const startY = ref(0);
const offsetX = ref(0);
const offsetY = ref(0);
const isPanelShow = ref(false);
const panelLeft = ref(0);
const panelTop = ref(0);
const chatMessages = ref([
  { content: '你好！我是非遗AI助手，可帮你查询江苏非遗信息、规划游览路线～', isUser: false }
]);
const inputMessage = ref('');

// WebSocket相关变量
const ws = ref(null);
const wsConnected = ref(false);
const wsUrl = ref('ws://localhost:8080/ws');
const reconnectTimer = ref(null);
const reconnectInterval = 3000; // 重连间隔(毫秒)
const pingInterval = 15000; // 心跳间隔(毫秒)
const pingTimer = ref(null);
const currentChatId = ref('');
// 根据后端配置调整

// AI悬浮球初始化
const initBallPosition = () => {
  const savedLeft = localStorage.getItem('floatBallLeft') || `${window.innerWidth - 70}px`;
  const savedTop = localStorage.getItem('floatBallTop') || `${window.innerHeight - 70}px`;
  ballLeft.value = savedLeft;
  ballTop.value = savedTop;
};

// AI悬浮球拖动事件
const handleBallStart = (e) => {
  isDragging.value = true;
  const rect = document.getElementById('float-ball').getBoundingClientRect();
  const client = e.touches ? e.touches[0] : e;
  startX.value = client.clientX;
  startY.value = client.clientY;
  offsetX.value = client.clientX - rect.left;
  offsetY.value = client.clientY - rect.top;
};
const handleBallMove = (e) => {
  if (!isDragging.value) return;
  e.preventDefault();
  const client = e.touches ? e.touches[0] : e;
  const newLeft = Math.max(10, Math.min(client.clientX - offsetX.value, window.innerWidth - 56 - 10));
  const newTop = Math.max(10, Math.min(client.clientY - offsetY.value, window.innerHeight - 56 - 10));
  ballLeft.value = `${newLeft}px`;
  ballTop.value = `${newTop}px`;
  if (isPanelShow.value) {
    togglePanel();
    togglePanel();
  }
};
const handleBallEnd = () => {
  if (!isDragging.value) return;
  isDragging.value = false;
  localStorage.setItem('floatBallLeft', ballLeft.value);
  localStorage.setItem('floatBallTop', ballTop.value);
};

// AI面板切换
const togglePanel = () => {
  if (isDragging.value) return;
  isPanelShow.value = !isPanelShow.value;
  if (isPanelShow.value) {
    const ballRect = document.getElementById('float-ball').getBoundingClientRect();
    const panelWidth = 400;
    const panelHeight = 700;
    let newLeft = ballRect.left - panelWidth - 10;
    if (newLeft < 10) newLeft = ballRect.right + 10;
    let newTop = ballRect.top;
    if (newTop + panelHeight > window.innerHeight - 10) {
      newTop = window.innerHeight - panelHeight - 10;
    }
    panelLeft.value = newLeft;
    panelTop.value = newTop;
    
    // 面板打开时连接WebSocket
    connectWebSocket();
  } else {
    // 面板关闭时断开WebSocket连接
    disconnectWebSocket();
  }
};

// WebSocket连接实现的AI助手逻辑
// WebSocket连接函数
const connectWebSocket = () => {
  try {
    // 关闭现有连接（如果存在）
    if (ws.value) {
      ws.value.close();
    }
    
    // 创建新的WebSocket连接
    ws.value = new WebSocket(wsUrl.value);
    
    // 连接打开事件
    ws.value.onopen = () => {
      console.log('WebSocket连接已打开');
      wsConnected.value = true;
      
      // 清除重连计时器
      if (reconnectTimer.value) {
        clearTimeout(reconnectTimer.value);
        reconnectTimer.value = null;
      }
      
      // 启动心跳机制
      startPingInterval();
      
      // 如果没有对话ID，生成一个新的
      if (!currentChatId.value) {
        currentChatId.value = generateChatId();
      }
    };
    
    // 接收消息事件
    ws.value.onmessage = (event) => {
      handleWebSocketMessage(event.data);
    };
    
    // 连接关闭事件
    ws.value.onclose = () => {
      console.log('WebSocket连接已关闭');
      wsConnected.value = false;
      
      // 停止心跳计时器
      stopPingInterval();
      
      // 如果面板是打开的，尝试重新连接
      if (isPanelShow.value) {
        attemptReconnect();
      }
    };
    
    // 连接错误事件
    ws.value.onerror = (error) => {
      console.error('WebSocket错误:', error);
    };
    
  } catch (error) {
    console.error('WebSocket连接失败:', error);
    // 尝试重新连接
    attemptReconnect();
  }
};

// 断开WebSocket连接
const disconnectWebSocket = () => {
  if (ws.value) {
    ws.value.close();
    ws.value = null;
  }
  wsConnected.value = false;
  stopPingInterval();
  
  if (reconnectTimer.value) {
    clearTimeout(reconnectTimer.value);
    reconnectTimer.value = null;
  }
};

// 尝试重新连接
const attemptReconnect = () => {
  if (reconnectTimer.value) {
    return; // 已经有重连计时器在运行
  }
  
  console.log(`将在${reconnectInterval/1000}秒后尝试重新连接WebSocket...`);
  reconnectTimer.value = setTimeout(() => {
    connectWebSocket();
  }, reconnectInterval);
};

// 启动心跳间隔
const startPingInterval = () => {
  stopPingInterval(); // 确保之前的心跳已停止
  
  pingTimer.value = setInterval(() => {
    if (wsConnected.value && ws.value && ws.value.readyState === WebSocket.OPEN) {
      sendPing();
    }
  }, pingInterval);
};

// 停止心跳间隔
const stopPingInterval = () => {
  if (pingTimer.value) {
    clearInterval(pingTimer.value);
    pingTimer.value = null;
  }
};

// 发送心跳
const sendPing = () => {
  try {
    const pingMsg = {
      type: 'ping',
      content: '',
      chatId: currentChatId.value
    };
    ws.value.send(JSON.stringify(pingMsg));
  } catch (error) {
    console.error('发送心跳失败:', error);
  }
};

// 生成聊天ID
const generateChatId = () => {
  return 'chat_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
};

// 处理WebSocket消息
const handleWebSocketMessage = (data) => {
  try {
    const message = JSON.parse(data);
    
    // 处理不同类型的消息
    if (message.type === 'ping') {
      // 心跳响应，无需特殊处理
      console.log('收到心跳响应');
    } else if (message.type === 'llm_QueryStream_Answer') {
      // 处理AI回复
      if (message.data) {
        // 检查是否有正在接收中的AI消息
        let lastMessage = chatMessages.value[chatMessages.value.length - 1];
        if (!lastMessage || lastMessage.isUser) {
          // 创建新消息
          chatMessages.value.push({ 
            content: message.data, 
            isUser: false,
            isStreaming: true
          });
        } else {
          // 更新现有消息
          lastMessage.content += message.data;
          // 如果是流式响应结束标记，清除streaming状态
          if (message.isEnd === true) {
            lastMessage.isStreaming = false;
          }
        }
        
        // 滚动到底部
        setTimeout(() => {
          const chatContainer = document.getElementById('chat-messages');
          if (chatContainer) {
            chatContainer.scrollTop = chatContainer.scrollHeight;
          }
        }, 50);
      }
    } else if (message.type === 'error') {
      // 处理错误消息
      chatMessages.value.push({ 
        content: '错误: ' + message.data, 
        isUser: false 
      });
    }
  } catch (error) {
    console.error('解析WebSocket消息失败:', error);
  }
};

// 发送用户消息
const sendMessage = () => {
  const message = inputMessage.value.trim();
  if (!message) return;
  
  // 添加用户消息到聊天记录
  chatMessages.value.push({ content: message, isUser: true });
  inputMessage.value = '';
  
  // 确保WebSocket连接已建立
  if (!wsConnected.value || !ws.value || ws.value.readyState !== WebSocket.OPEN) {
    connectWebSocket();
    // 显示等待连接的提示
    chatMessages.value.push({ 
      content: '正在连接AI助手...', 
      isUser: false 
    });
    return;
  }
  
  // 发送消息到后端
  try {
    const chatMsg = {
      type: 'chat',
      content: message,
      chatId: currentChatId.value
    };
    ws.value.send(JSON.stringify(chatMsg));
  } catch (error) {
    console.error('发送消息失败:', error);
    chatMessages.value.push({ 
      content: '消息发送失败，请重试', 
      isUser: false 
    });
  }
};
// 已在WebSocket部分实现
const handleEnterSend = (e) => {
  if (e.key === 'Enter') sendMessage();
};

// 窗口 resize 处理
const handleWindowResize = () => {
  if (isPanelShow.value) {
    togglePanel();
    togglePanel();
  }
};

// 生命周期钩子
onMounted(() => {
  initBallPosition();
  document.addEventListener('mousemove', handleBallMove);
  document.addEventListener('touchmove', handleBallMove, { passive: false });
  document.addEventListener('mouseup', handleBallEnd);
  document.addEventListener('touchend', handleBallEnd);
  window.addEventListener('resize', handleWindowResize);
  // 检查登录状态
  checkLoginStatus();
});
onUnmounted(() => {
  document.removeEventListener('mousemove', handleBallMove);
  document.removeEventListener('touchmove', handleBallMove);
  document.removeEventListener('mouseup', handleBallEnd);
  document.removeEventListener('touchend', handleBallEnd);
  window.removeEventListener('resize', handleWindowResize);
});
</script>

<style scoped>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: "Microsoft YaHei", sans-serif;
}

/* 导航栏样式 */
.navbar {
  width: 100%;
  height: 60px;
  background-color: #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  position: sticky;
  top: 0;
  z-index: 999;
}

/* 用户区域样式 */
.user-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.user-name {
  font-size: 14px;
  color: #333;
}

.logout-btn {
  background-color: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.logout-btn:hover {
  background-color: #e5e5e5;
  color: #333;
}
.logo {
  font-size: 24px;
  font-weight: bold;
  color: #1E90FF;
  cursor: pointer;
}
.nav-menu {
  display: flex;
  list-style: none;
}
.nav-item {
  margin: 0 15px;
}
.nav-link {
  text-decoration: none;
  color: #333;
  font-size: 16px;
  transition: color 0.3s;
}
.nav-link.active, .nav-link:hover {
  color: #1E90FF;
}
.login-btn {
  background-color: #1E90FF;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}
.login-btn:hover {
  background-color: #0d76d0;
}

/* 顶部Banner样式 */
.banner {
  width: 100%;
  height: 280px;
  position: relative;
  overflow: hidden;
  /* margin-top: 10px; */
}
.banner img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.banner-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: #fff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}
.banner-text h1 {
  font-size: 36px;
  margin-bottom: 12px;
}
.banner-text p {
  font-size: 18px;
}

/* 核心：全屏横向图片区样式 */
.full-width-gallery {
  width: 100%;
  padding: 50px 0;
  background-color: #f8f9fa;
  overflow: hidden;
}
/* 图片容器：横向撑满，无留白 */
.gallery-wrapper {
  display: flex;
  width: 100%;
  gap: 8px; /* 图片间窄缝，更紧凑 */
  padding: 0 12px; /* 左右仅留12px，接近全屏 */
}
/* 单张图片项：基础样式 */
.gallery-item {
  flex: 1; /* 4张图片均分宽度，横向铺满 */
  height: 450px; /* 图片高度放大，视觉更突出 */
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.6s cubic-bezier(0.25, 0.1, 0.25, 1); /* 丝滑缓动 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}
/* 悬停效果1：图片项宽度增加20%，突出显示 */
.gallery-item:hover {
  flex: 1.2;
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15); /* 阴影加深 */
  z-index: 20; /* 避免被其他图片遮挡 */
}
/* 悬停效果2：图片局部放大，露出更多细节 */
.gallery-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.7s cubic-bezier(0.25, 0.1, 0.25, 1);
}
.gallery-item:hover .gallery-img {
  transform: scale(1.08); /* 图片放大8% */
}
/* 悬停效果3：文字渐变层渐显 */
.item-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8) 70%); /* 底部渐变 */
  opacity: 0;
  transition: opacity 0.5s cubic-bezier(0.25, 0.1, 0.25, 1);
  display: flex;
  align-items: flex-end;
  padding: 40px 30px;
}
.gallery-item:hover .item-overlay {
  opacity: 1;
}
/* 文字内容样式 */
.overlay-content {
  color: #fff;
}
.overlay-content h3 {
  font-size: 26px;
  margin-bottom: 8px;
  transition: transform 0.5s cubic-bezier(0.25, 0.1, 0.25, 1);
  transform: translateY(20px);
}
.gallery-item:hover .overlay-content h3 {
  transform: translateY(0);
}
.overlay-content p {
  font-size: 16px;
  margin-bottom: 15px;
  opacity: 0.9;
  transition: transform 0.5s cubic-bezier(0.25, 0.1, 0.25, 1) 0.1s;
  transform: translateY(20px);
}
.gallery-item:hover .overlay-content p {
  transform: translateY(0);
}
/* 查看按钮样式 */
.view-btn {
  display: inline-block;
  font-size: 14px;
  padding: 8px 16px;
  background-color: #1E90FF;
  border-radius: 4px;
  transition: all 0.3s;
  transition: transform 0.5s cubic-bezier(0.25, 0.1, 0.25, 1) 0.2s;
  transform: translateY(20px);
}
.gallery-item:hover .view-btn {
  transform: translateY(0);
}
.view-btn:hover {
  background-color: #0d76d0;
}

/* 地图区域样式 */
.map-section {
  width: 100%;
  padding: 40px 20px;
  background-color: #f5f5f5;
}
.map-container {
  width: 1200px;
  height: 600px;
  margin: 0 auto;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
}
.jiangsu-map {
  width: 100%;
  height: 100%;
  background-image: url("/image/4DB13A92315B31C38E217C83580ED86B564E3E4D_size155_w1080_h810.jpg");
  background-size: cover;
  background-position: center;
}

/* 功能入口样式 */
.func-entry {
  width: 1200px;
  margin: 30px auto;
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 20px;
}
.entry-card {
  width: 280px;
  height: 180px;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: box-shadow 0.3s;
}
.entry-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
.entry-card img {
  width: 60px;
  height: 60px;
  margin: 0 auto 15px;
  border-radius: 50%;
  background-color: #f0f0f0;
  padding: 10px;
  object-fit: cover;
}
.entry-card h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 8px;
}
.entry-card p {
  font-size: 14px;
  color: #666;
}

/* AI助手悬浮球样式 */
#float-ball {
  position: fixed;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6366F1, #8B5CF6);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: move;
  z-index: 50;
  font-size: 24px;
  user-select: none;
}
#float-ball:hover {
  transform: scale(1.1);
}

/* AI对话面板样式 */
#ai-panel {
  width: 400px;
  height: 700px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  z-index: 40;
  overflow: hidden;
  border: 1px solid #e5e7eb;
  position: fixed;
}
.panel-header {
  background: linear-gradient(90deg, #6366F1, #8B5CF6);
  color: white;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  font-size: 16px;
}
.close-btn {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  color: white;
  cursor: pointer;
  transition: background-color 0.2s;
}
.close-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
}
.chat-messages {
  height: calc(100% - 90px);
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.message {
  animation: fadeIn 0.3s ease-out;
}
.ai-message {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}
.user-message {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  justify-content: flex-end;
}
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 16px;
}
.ai-avatar {
  background-color: rgba(99, 102, 241, 0.1);
  color: #6366F1;
}
.user-avatar {
  background-color: #e5e7eb;
  color: #1F2937;
}
.message-content {
  max-width: 80%;
  padding: 14px 18px;
  border-radius: 20px;
  font-size: 16px;
  line-height: 1.6;
}
.ai-content {
  background-color: #f3f4f6;
  border-top-left-radius: 4px;
}
.user-content {
  background-color: #6366F1;
  color: white;
  border-top-right-radius: 4px;
}
.input-area {
  border-top: 1px solid #e5e7eb;
  padding: 8px 12px;
  position: absolute;
  bottom: 0;
  width: 100%;
  background-color: white;
}
.input-container {
  display: flex;
  gap: 8px;
}
#chat-input {
  flex: 1;
  padding: 12px 18px;
  border-radius: 24px;
  border: 1px solid #e5e7eb;
  outline: none;
  transition: all 0.2s;
  font-size: 16px;
}
#chat-input:focus {
  border-color: #6366F1;
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.2);
}
.send-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background-color: #6366F1;
  color: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.2s;
  font-size: 18px;
}
.send-btn:hover {
  background-color: #5254e0;
}

/* 动画效果 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
@keyframes slideIn {
  from { transform: translateX(20px); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
}
.panel-slide {
  animation: slideIn 0.3s ease-out;
}

/* 响应式适配：保证移动端也有好效果 */
@media (max-width: 1200px) {
  .map-container, .func-entry {
    width: 90%;
  }
}
@media (max-width: 992px) {
  /* 平板端：图片区改为2张一排 */
  .gallery-wrapper {
    flex-wrap: wrap;
    gap: 15px;
    padding: 0 20px;
  }
  .gallery-item {
    flex: 0 0 calc(50% - 7.5px);
    height: 380px;
  }
  .gallery-item:hover {
    flex: 0 0 calc(50% - 7.5px); /* 平板端悬停不改变宽度，只保留其他动画 */
  }
}
@media (max-width: 768px) {
  .navbar {
    padding: 0 20px;
  }
  .nav-menu {
    display: none;
  }
  .banner {
    height: 200px;
  }
  .banner-text h1 {
    font-size: 28px;
  }
  .map-container {
    height: 400px;
  }
  .func-entry {
    justify-content: center;
  }
  .entry-card {
    width: 45%;
    max-width: 300px;
  }
  #ai-panel {
    width: 90%;
    height: 500px;
  }
}
@media (max-width: 576px) {
  /* 移动端：图片区改为1张一排 */
  .gallery-item {
    flex: 0 0 100%;
    height: 320px;
  }
  .gallery-item:hover {
    flex: 0 0 100%;
  }
  .entry-card {
    width: 100%;
  }
  .banner-text h1 {
    font-size: 24px;
  }
}
</style>