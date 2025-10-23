<!-- MallHead.vue -->
<template>
  <header 
    ref="navRef"
    class="navbar-container" 
    :style="isFixed ? fixedNavStyle : {}"
  >
    <!-- 主导航 -->
    <div class="main-nav">
      <div class="container">
        <div class="logo">
          <a href="#" @click.prevent="$router.push('/heritage-mall')">点苏记Mall</a>
        </div>
        <nav class="category-nav">
          <ul class="main-menu">
            <li class="menu-item"><a href="#" class="menu-link" @click.prevent="$router.push('/home')">首页</a></li>
          </ul>
        </nav>
        <div class="search-box">
          <input type="text" placeholder="搜索商品、品牌..." class="search-input">
          <button class="search-btn">搜索</button>
        </div>
        <div class="user-actions">
          <a href="#" class="action-link cart">
            <span class="icon">🛒</span>
            <span class="text"@click="$router.push('/shop-car')">购物车</span>
            
          </a>
          <a href="#" class="action-link user">
            <span class="icon">👤</span>
            <span class="text" @click="$router.push('/user-center/profile')">我的账户</span>
          </a>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue';

// 接收外部传参：是否需要固定导航栏（由父组件控制）
const props = defineProps({
  // 是否固定导航栏
  fixedNav: {
    type: Boolean,
    default: true
  }
});

// 导航栏吸顶效果
const navRef = ref(null);
const isFixed = ref(false);
const fixedNavStyle = {
  position: 'fixed',
  top: 0,
  left: 0,
  right: 0,
  zIndex: 9999,
  boxShadow: '0 2px 8px rgba(0,0,0,0.08)'
};

let lastScrollTop = 0;
const handleScroll = () => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;
  // 根据滚动方向和距离控制固定状态
  if (scrollTop > 200 && scrollTop > lastScrollTop) {
    isFixed.value = true;
  } else if (scrollTop < 100) {
    isFixed.value = false;
  }
  lastScrollTop = scrollTop;
};

onMounted(() => {
  if (props.fixedNav) {
    window.addEventListener('scroll', handleScroll, { passive: true });
  }
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});

watch(() => props.fixedNav, async (val) => {
  await nextTick();
  if (val) {
    window.addEventListener('scroll', handleScroll, { passive: true });
  } else {
    window.removeEventListener('scroll', handleScroll);
    isFixed.value = false;
  }
});
</script>

<style scoped>
/* 通用样式重置 */
* {
  box-sizing: border-box;
}

.container {
  width: 1200px;
  margin: 0 auto;
}

.navbar-container {
  width: 100%;
}

.clearfix::after {
  content: "";
  display: table;
  clear: both;
}

img {
  max-width: 100%;
  display: block;
}

button,
input,
select,
textarea {
  border: none;
  outline: none;
}

button {
  cursor: pointer;
}

h1, h2, h3, h4, h5, h6, p, ul, li {
  margin: 0;
  padding: 0;
}

.container-fluid {
  width: 100%;
  padding: 0 15px;
}

a {
  text-decoration: none;
  color: inherit;
}

ul {
  list-style: none;
}

/* 顶部快捷导航 */
.top-nav {
  background-color: #f5f5f5;
  height: 36px;
  line-height: 36px;
  font-size: 12px;
  color: #666;
}

.top-nav .container {
  display: flex;
  justify-content: space-between;
}

.top-nav-link {
  margin-right: 20px;
  transition: color 0.3s;
}

.top-nav-link:hover {
  color: #1E90FF;
}

/* 主导航 */
.main-nav {
  background-color: #fff;
  padding: 15px 0;
}

.main-nav .container {
  display: flex;
  align-items: center;
}

.logo {
  font-size: 28px;
  font-weight: bold;
  color: #1E90FF;
  margin-right: 30px;
  letter-spacing: -1px;
}

.category-nav {
  display: flex;
  align-items: center;
  margin-right: 30px;
}

.category-dropdown {
  position: relative;
  margin-right: 20px;
}

.category-title {
  display: block;
  width: 120px;
  height: 40px;
  line-height: 40px;
  background-color: #1E90FF;
  color: #fff;
  text-align: center;
  font-weight: 500;
  border-radius: 4px 4px 0 0;
}

.category-menu {
  position: absolute;
  top: 40px;
  left: 0;
  width: 120px;
  background-color: #fff;
  border: 1px solid #1E90FF;
  border-top: none;
  border-radius: 0 0 4px 4px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  display: none;
  z-index: 9999;
}

.category-dropdown:hover .category-menu {
  display: block;
}

.category-item {
  padding: 10px 12px;
  border-bottom: 1px solid #f0f0f0;
}

.category-item:last-child {
  border-bottom: none;
}

.category-item a {
  display: block;
  color: #333;
}

.category-item a:hover {
  color: #1E90FF;
}

.main-menu {
  display: flex;
  align-items: center;
}

.menu-item {
  margin-right: 20px;
}

.menu-link {
  color: #333;
  font-weight: 500;
}

.menu-link:hover {
  color: #1E90FF;
}

.search-box {
  margin-left: auto;
  margin-right: 20px;
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  padding: 5px;
  border-radius: 4px;
}

.search-input {
  width: 250px;
  height: 32px;
  padding: 0 15px;
  outline: none;
  border: none;
  background: transparent;
}

.search-btn {
  width: 80px;
  height: 32px;
  background-color: #1E90FF;
  color: #fff;
  border-radius: 4px;
}

.user-actions {
  display: flex;
  align-items: center;
}

.action-link {
  display: inline-flex;
  align-items: center;
  margin-left: 20px;
  color: #333;
}

.action-link .icon {
  margin-right: 8px;
  font-size: 18px;
}

.action-link .text {
  font-size: 14px;
}

.action-link:hover {
  color: #1E90FF;
}

/* 购物车数量徽标（可选） */
.cart-count {
  position: relative;
  top: -10px;
  right: 10px;
  width: 18px;
  height: 18px;
  line-height: 18px;
  background-color: #1E90FF;
  color: #fff;
  font-size: 12px;
  text-align: center;
  border-radius: 50%;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .container {
    width: 960px;
  }
  .main-menu {
    display: none;
  }
}

@media (max-width: 992px) {
  .container {
    width: 720px;
  }
  .search-box {
    max-width: 300px;
  }
  .top-nav-left {
    display: none;
  }
}

@media (max-width: 768px) {
  .container {
    width: 100%;
  }
  .logo {
    font-size: 22px;
    margin-right: 15px;
  }
  .search-box {
    max-width: 200px;
    margin-right: 15px;
  }
  .action-link .text {
    display: none;
  }
  .action-link {
    margin-left: 15px;
  }
}

@media (max-width: 576px) {
  .category-nav {
    margin-right: 10px;
  }
  .search-box {
    max-width: 150px;
  }
  .search-input {
    padding: 0 10px;
    font-size: 12px;
  }
  .search-btn {
    width: 60px;
    font-size: 14px;
  }
}
</style>