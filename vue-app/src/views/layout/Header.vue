<template>
  <header class="header">
    <div class="logo">点苏记</div>
    
    <!-- 搜索框 -->
    <div class="search-container">
      <input 
        type="text" 
        placeholder="搜索非遗商品/路线"
        class="search-input"
      >
      <button class="search-btn">搜索</button>
    </div>
    
    <!-- 用户操作区 -->
    <div class="user-actions">
      <!-- <button class="notify-btn" title="消息通知">
        <svg class="icon" viewBox="0 0 24 24">
          <path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.18C9.63 5.36 8 7.92 8 11v5l-2 2v1h16v-1l-2-2z"/>
        </svg>
        <span class="notification-badge">3</span>
      </button> -->
      <button class="logout-btn" @click="handleBack">返回</button>
    </div>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const handleBack = () => {
  router.push('/');
}
</script>

<style scoped>
.header {
  height: 60px;
  padding: 0 20px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 10;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: #1E90FF;
}

.search-container {
  display: flex;
  flex: 1;
  max-width: 400px;
  margin: 0 20px;
}

.search-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-right: none;
  border-radius: 4px 0 0 4px;
  outline: none;
}

.search-input:focus {
  border-color: #1E90FF;
}

.search-btn {
  padding: 0 16px;
  background-color: #1E90FF;
  color: white;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
}

.search-btn:hover {
  background-color: #187bcd;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.notify-btn {
  position: relative;
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.3s ease;
  color: #1E3A8A;
}

.notify-btn:hover {
  background-color: rgba(30, 58, 138, 0.1);
  transform: translateY(-2px);
}

.notify-btn .icon {
  width: 22px;
  height: 22px;
  fill: currentColor;
  transition: transform 0.3s ease;
}

.notify-btn:hover .icon {
  transform: scale(1.1);
}

.notification-badge {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 16px;
  height: 16px;
  background-color: #ff4d4f;
  color: white;
  border-radius: 50%;
  font-size: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logout-btn {
  padding: 6px 12px;
  background-color: #1E90FF;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.logout-btn:hover {
  background-color: #187bcd;
}

/* 响应式样式 */
@media (max-width: 768px) {
  .search-container {
    max-width: none;
    margin: 0 10px;
  }
}

@media (max-width: 480px) {
  .header {
    padding: 0 10px;
  }
  
  .logo {
    font-size: 18px;
  }
  
  .search-container {
    display: none;
  }
}
</style>