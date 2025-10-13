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
          <a href="javascript:;" class="nav-link" @click="$router.push('/user-center')">个人中心</a>
        </li>
      </ul>
      <button class="login-btn" @click="$router.push('/login')">登录</button>
    </nav>

    <!-- 顶部Banner -->
    <div class="banner">
      <img src="/image/微信图片_20251010141521_51_4.jpg" alt="江苏非遗Banner">
      <div class="banner-text">
        <h1>点苏记 · 江苏非遗地图</h1>
        <p>探索13市非遗瑰宝，定制专属文化之旅</p>
      </div>
    </div>

    <!-- 地图区域（含地标弹窗） -->
    <div class="map-section">
      <div class="map-container">
        <div class="jiangsu-map"></div>
        
        <!-- 南京地标 -->
        <!-- <div 
          class="landmark landmark-nanjing"
          @mouseenter="showPopup('nanjing')"
          @mouseleave="hidePopup('nanjing')"
        ></div> -->
        <!-- 南京地标弹窗 -->
        <!-- <div id="popup-nanjing" class="landmark-popup">
          <img src="https://picsum.photos/200/120?random=1" alt="南京非遗">
          <h3>南京非遗</h3>
          <p>云锦、金陵刻经、南京剪纸等国家级非遗</p>
          <button @click="gotoRoute('nanjing')">生成路线</button>
        </div> -->

        <!-- 苏州地标 -->
        <!-- <div 
          class="landmark landmark-suzhou"
          @mouseenter="showPopup('suzhou')"
          @mouseleave="hidePopup('suzhou')"
        ></div> -->
        <!-- 苏州地标弹窗 -->
        <!-- <div id="popup-suzhou" class="landmark-popup">
          <img src="https://picsum.photos/200/120?random=2" alt="苏州非遗">
          <h3>苏州非遗</h3>
          <p>苏绣、昆曲、苏州评弹等世界级非遗</p>
          <button @click="gotoRoute('suzhou')">生成路线</button>
        </div> -->
      </div>
    </div>

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
      <div class="entry-card" @click="gotoPage('3dworkshop')">
        <img src="https://picsum.photos/60/60?random=7" alt="3D工坊">
        <h3>3D云游工坊</h3>
        <p>沉浸式体验非遗制作过程</p>
      </div>
      <div class="entry-card" @click="gotoPage('user')">
        <img src="https://picsum.photos/60/60?random=8" alt="个人中心">
        <h3>我的非遗之旅</h3>
        <p>查看点亮记录与收藏</p>
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
      <!-- 面板头部 -->
      <div class="panel-header">
        <div class="header-title">
          <span>💬</span>
          <span>非遗AI助手</span>
        </div>
        <button class="close-btn" @click="togglePanel">✕</button>
      </div>

      <!-- 聊天消息区域 -->
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

      <!-- 输入区域 -->
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
import { ref, onMounted, onUnmounted, watch } from 'vue';
import { useRouter ,useRoute} from 'vue-router';
const route = useRoute();
// 初始化路由实例
const router = useRouter();
// 检查是否已经刷新过
if (!localStorage.getItem('hasRefreshed')) {
    // 标记为已刷新
    localStorage.setItem('hasRefreshed', 'true');
    // 执行刷新
    window.location.reload();
} else {
    // 如果已经刷新过，清除标记以便下次可能的使用
    localStorage.removeItem('hasRefreshed');
}
// ---------------------- 原有功能逻辑 ----------------------
// 1. 地标弹窗控制逻辑
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

// 2. 生成路线逻辑
const gotoRoute = (city) => {
  const cityName = city === 'nanjing' ? '南京' : '苏州';
  alert(`生成${cityName}非遗路线`);
  router.push({ path: '/route', query: { city } });
};

// 3. 功能入口跳转逻辑
const gotoPage = (pageUrl) => {
  const pageName = pageUrl.split('.')[0];
  alert(`跳转至${pageName}页面`);
  const routeMap = {
    route: '/route',
    mall: '/heritage-mall',
    dworkshop: '/3d-workshop',
    user: '/user-center'
  };
  if (routeMap[pageName]) {
    router.push(routeMap[pageName]);
  }
};

// ---------------------- AI助手逻辑 ----------------------
// 1. AI悬浮球状态管理
const ballLeft = ref('');
const ballTop = ref('');
const isDragging = ref(false);
const startX = ref(0);
const startY = ref(0);
const offsetX = ref(0);
const offsetY = ref(0);

// 2. AI面板状态管理
const isPanelShow = ref(false);
const panelLeft = ref(0);
const panelTop = ref(0);

// 3. 聊天数据管理
const chatMessages = ref([
  { content: '你好！我是非遗AI助手，可帮你查询江苏非遗信息、规划游览路线～', isUser: false }
]);
const inputMessage = ref('');

// 初始化悬浮球位置
const initBallPosition = () => {
  const savedLeft = localStorage.getItem('floatBallLeft') || `${window.innerWidth - 70}px`;
  const savedTop = localStorage.getItem('floatBallTop') || `${window.innerHeight - 70}px`;
  ballLeft.value = savedLeft;
  ballTop.value = savedTop;
};

// 悬浮球拖动开始
const handleBallStart = (e) => {
  isDragging.value = true;
  const rect = document.getElementById('float-ball').getBoundingClientRect();
  const client = e.touches ? e.touches[0] : e;
  startX.value = client.clientX;
  startY.value = client.clientY;
  offsetX.value = client.clientX - rect.left;
  offsetY.value = client.clientY - rect.top;
};

// 悬浮球拖动中
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

// 悬浮球拖动结束
const handleBallEnd = () => {
  if (!isDragging.value) return;
  isDragging.value = false;
  localStorage.setItem('floatBallLeft', ballLeft.value);
  localStorage.setItem('floatBallTop', ballTop.value);
};

// 切换AI面板显示/隐藏
const togglePanel = () => {
  if (isDragging.value) return;
  isPanelShow.value = !isPanelShow.value;
  
  if (isPanelShow.value) {
    const ballRect = document.getElementById('float-ball').getBoundingClientRect();
    const panelWidth = 320;
    const panelHeight = 600;
    
    let newLeft = ballRect.left - panelWidth - 10;
    if (newLeft < 10) newLeft = ballRect.right + 10;
    
    let newTop = ballRect.top;
    if (newTop + panelHeight > window.innerHeight - 10) {
      newTop = window.innerHeight - panelHeight - 10;
    }
    
    panelLeft.value = newLeft;
    panelTop.value = newTop;
  }
};

// 生成AI回复
const generateAiResponse = (message) => {
  const nanjingKeywords = ['南京', '云锦', '金陵刻经', '剪纸'];
  const suzhouKeywords = ['苏州', '苏绣', '昆曲', '评弹'];
  const routeKeywords = ['路线', '规划', '游览', '行程'];
  
  if (nanjingKeywords.some(key => message.includes(key))) {
    return '南京拥有多项国家级非遗：①云锦（中国三大名锦之首）、②金陵刻经（佛教典籍雕刻技艺）、③南京剪纸（传统民间艺术），可点击地图南京地标生成专属游览路线～';
  } else if (suzhouKeywords.some(key => message.includes(key))) {
    return '苏州是非遗重镇：①苏绣（中国四大名绣之一）、②昆曲（世界级非遗，“百戏之祖”）、③苏州评弹（曲艺形式），建议前往苏州非遗馆体验现场制作～';
  } else if (routeKeywords.some(key => message.includes(key))) {
    return '你可以通过首页“定制非遗路线”功能，选择感兴趣的城市（如南京、苏州）和非遗类型（如传统技艺、戏曲），系统会自动生成1-3日游览计划～';
  } else {
    const generalResponses = [
      `你提到的“${message}”，可点击首页功能入口进一步操作～`,
      `关于“${message}”的非遗信息，江苏13市共有300+项非遗，其中世界级4项、国家级100+项，你可以指定城市查询更详细内容～`,
      `如需了解“${message}”相关的非遗产品，可前往“非遗商城”板块，支持传承人直供，确保正品～`
    ];
    return generalResponses[Math.floor(Math.random() * generalResponses.length)];
  }
};

// 发送聊天消息
const sendMessage = () => {
  const message = inputMessage.value.trim();
  if (!message) return;
  
  chatMessages.value.push({ content: message, isUser: true });
  inputMessage.value = '';
  
  setTimeout(() => {
    const aiReply = generateAiResponse(message);
    chatMessages.value.push({ content: aiReply, isUser: false });
    
    setTimeout(() => {
      const chatContainer = document.getElementById('chat-messages');
      chatContainer.scrollTop = chatContainer.scrollHeight;
    }, 100);
  }, 800 + Math.random() * 500);
};

// 回车发送消息
const handleEnterSend = (e) => {
  if (e.key === 'Enter') sendMessage();
};

// 窗口大小变化处理
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
  /* width: 100px; */
}

.nav-link {
  text-decoration: none;
  color: #333;
  font-size: 16px;
  transition: color 0.3s;
}

.nav-link.active,
.nav-link:hover {
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
  margin-top: 60px;
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

/* 地图容器样式 */
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

/* 地标样式 */
.landmark {
  position: absolute;
  width: 36px;
  height: 36px;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%231E90FF'%3E%3Ccircle cx='12' cy='12' r='10'/%3E%3Ccircle cx='12' cy='12' r='5' fill='white'/%3E%3C/svg%3E");
  background-size: 100%;
  cursor: pointer;
  transition: transform 0.3s;
}

.landmark:hover {
  transform: scale(1.2);
}

.landmark-nanjing {
  top: 35%;
  left: 42%;
}

.landmark-suzhou {
  top: 40%;
  left: 55%;
}

/* 地标弹窗样式 */
.landmark-popup {
  position: absolute;
  width: 200px;
  padding: 15px;
  background-color: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  display: none;
  z-index: 10;
}

.landmark-popup img {
  width: 100%;
  height: 120px;
  border-radius: 4px;
  margin-bottom: 8px;
  object-fit: cover;
}

.landmark-popup h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 4px;
}

.landmark-popup p {
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
}

.landmark-popup button {
  width: 100%;
  height: 32px;
  background-color: #1E90FF;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.landmark-popup button:hover {
  background-color: #0d76d0;
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
  width: 320px;
  height: 600px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  z-index: 40;
  overflow: hidden;
  border: 1px solid #e5e7eb;
  position: fixed;
}

/* 面板头部 */
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

/* 聊天消息区域 */
.chat-messages {
  height: calc(100% - 90px);
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 消息样式 */
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
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
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
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.5;
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

/* 输入区域 */
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
  padding: 10px 16px;
  border-radius: 20px;
  border: 1px solid #e5e7eb;
  outline: none;
  transition: all 0.2s;
  font-size: 14px;
}

#chat-input:focus {
  border-color: #6366F1;
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.2);
}

.send-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #6366F1;
  color: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.2s;
}

.send-btn:hover {
  background-color: #5254e0;
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideIn {
  from {
    transform: translateX(20px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.panel-slide {
  animation: slideIn 0.3s ease-out;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .map-container,
  .func-entry {
    width: 90%;
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

  .banner-text p {
    font-size: 16px;
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

@media (max-width: 480px) {
  .entry-card {
    width: 100%;
  }

  .banner-text h1 {
    font-size: 24px;
  }
}
</style>