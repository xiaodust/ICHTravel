<!-- MallHead.vue -->
<template>
  <header 
    ref="navRef"
    class="navbar-container" 
    :style="isFixed ? fixedNavStyle : {}"
  >
    <!-- 顶部快捷导航 -->
    <!-- <div class="top-nav">
      <div class="container">
        <div class="top-nav-left">
          <a href="#" class="top-nav-link" @click.prevent="$router.push('/home')">返回首页</a>
          <a href="#" class="top-nav-link">商家入驻</a>
          <a href="#" class="top-nav-link">关于我们</a>
          <a href="#" class="top-nav-link">联系客服</a>
        </div>
      </div>
    </div> -->

    <!-- 主导航 -->
    <div class="main-nav">
      <div class="container">
        <div class="logo">
          <a href="#" @click.prevent="$router.push('/heritage-mall')">点苏记Mall</a>
        </div>
        <nav class="category-nav">
          <div class="category-dropdown">
            <a href="#" class="category-title">全部分类</a>
            <div class="category-menu">
              <div class="category-item">
                <a href="#" class="category-link" @click.prevent="$router.push('/food')">美味食品</a>
              </div>
              <div class="category-item">
                <a href="#" class="category-link" @click.prevent="$router.push('/craft')">精品工艺</a>
              </div>
            </div>
          </div>
          <ul class="main-menu">
            <li class="menu-item"><a href="#" class="menu-link" @click.prevent="$router.push('/home')">首页</a></li>
            <li class="menu-item"><a href="#" class="menu-link" @click.prevent="$router.push('/food')">美味食品</a></li>
            <li class="menu-item"><a href="#" class="menu-link" @click.prevent="$router.push('/craft')">精品工艺</a></li>
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
            <span class="cart-count">3</span>
          </a>
          <a href="#" class="action-link user">
            <span class="icon">👤</span>
            <span class="text" @click="$router.push('/user-center#orders')">我的账户</span>
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
  isFixed: {
    type: Boolean,
    default: false
  }
});

const navRef = ref(null);
const fixedNavStyle = ref({});
let originalBodyPadding = '';

// 强制设置固定样式
const setFixedStyle = async () => {
  if (!props.isFixed || !navRef.value) return;
  
  await nextTick();
  const navHeight = navRef.value.offsetHeight;
  
  // 内联样式：优先级最高，确保不被覆盖
  fixedNavStyle.value = {
    position: 'fixed',
    top: '0',
    left: '0',
    width: '100%',
    zIndex: '9999',
    backgroundColor: '#fff',
    boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
    margin: '0',
    padding: '0'
  };

  // 保存原始padding，设置body间距（避免内容被遮挡）
  originalBodyPadding = document.body.style.paddingTop || '0px';
  document.body.style.paddingTop = `${navHeight}px`;
};

// 恢复样式
const resetStyle = () => {
  if (props.isFixed) return;
  
  fixedNavStyle.value = {};
  document.body.style.paddingTop = originalBodyPadding;
};

// 监听外部传参变化（父组件控制是否固定）
watch(() => props.isFixed, (newVal) => {
  newVal ? setFixedStyle() : resetStyle();
}, { immediate: true });

// 组件卸载时兜底恢复
onUnmounted(() => {
  resetStyle();
});
</script>

<style scoped>
/* 基础样式保持不变 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}

.container {
  width: 1200px;
  margin: 0 auto;
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
  border-bottom: 1px dotted #f1f1f1;
}

.category-item:last-child {
  border-bottom: none;
}

.category-link {
  display: block;
  padding: 10px 15px;
  font-size: 14px;
  transition: all 0.3s;
}

.category-link:hover {
  background-color: #f9f9f9;
  color: #1E90FF;
  padding-left: 20px;
}

.main-menu {
  display: flex;
}

.menu-item {
  margin-right: 25px;
}

.menu-link {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  transition: color 0.3s;
}

.menu-link:hover {
  color: #1E90FF;
}

.search-box {
  display: flex;
  flex: 1;
  max-width: 500px;
  margin-right: 30px;
}

.search-input {
  flex: 1;
  height: 40px;
  padding: 0 15px;
  border: 2px solid #1E90FF;
  border-right: none;
  border-radius: 4px 0 0 4px;
  outline: none;
  font-size: 14px;
}

.search-input:focus {
  border-color: blue;
}

.search-btn {
  width: 80px;
  height: 40px;
  background-color: #1E90FF;
  color: #fff;
  border: none;
  border-radius: 0 4px 4px 0;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-btn:hover {
  background-color: blue;
}

.user-actions {
  display: flex;
  align-items: center;
}

.action-link {
  display: flex;
  align-items: center;
  margin-left: 25px;
  font-size: 14px;
  color: #333;
  transition: color 0.3s;
}

.action-link:hover {
  color: #1E90FF;
}

.action-link .icon {
  font-size: 20px;
  margin-right: 5px;
}

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