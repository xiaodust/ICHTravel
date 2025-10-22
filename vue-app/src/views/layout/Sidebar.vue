<template>
  <aside class="sidebar">
    <nav class="nav-list">
      <button 
        v-for="(item, index) in navList" 
        :key="index"
        @click="handleNavClick(item.target)"
        class="nav-item"
        :class="{ 'nav-item-active': isActive(item.target) }"
      >
        <svg class="nav-icon" :viewBox="item.iconViewBox">
          <path :d="item.iconPath"/>
        </svg>
        <span class="nav-text">{{ item.label }}</span>
      </button>
    </nav>
  </aside>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { ref } from 'vue'

const router = useRouter()
const route = useRoute()

// 导航数据
const navList = ref([
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
]);

// 检查当前路由是否激活
const isActive = (target) => {
  return route.name === target;
};

// 导航点击处理
const handleNavClick = (target) => {
  router.push({ name: target });
};
</script>

<style scoped>
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
}
</style>