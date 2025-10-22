<template>
  <div class="mall-container">
    <MallHead />
    <MallBody />
    <!-- 购物车和结算组件默认隐藏，通过路由或状态控制显示 -->
    <ShopCar v-if="$route.path === '/shop-car'" />
    <CheckOut v-if="$route.path === '/checkout'" />
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-logo">点苏记Mall</div>
          <div class="footer-links">
            <a href="#" @click.prevent="$router.push('/about')">关于我们</a>
            <a href="#" @click.prevent="$router.push('/contact')">联系客服</a>
            <a href="#" @click.prevent="$router.push('/policy')">隐私政策</a>
            <a href="#" @click.prevent="$router.push('/terms')">用户协议</a>
          </div>
          <!-- <div class="footer-social">
            <a href="#" class="social-icon">微信</a>
            <a href="#" class="social-icon">微博</a>
            <a href="#" class="social-icon">抖音</a>
          </div> -->
          <p class="copyright">© 2023 点苏记Mall 版权所有 | 苏ICP备12345678号</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import MallBody from './components/MallBody.vue';
import MallHead from './components/MallHead.vue';
import ShopCar from './components/ShopCar.vue';
import CheckOut from './components/CheckOut.vue';
import { useRoute } from 'vue-router';

const route = useRoute();
</script>

<style>
.mall-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f9fafb;
}

.footer {
  background-color: #1d2939;
  color: #ffffff80;
  padding: 40px 0 20px;
  margin-top: auto;
}

.footer-content {
  text-align: center;
}

.footer-logo {
  font-size: 24px;
  font-weight: bold;
  color: white;
  margin-bottom: 20px;
}

.footer-links {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
}

.footer-links a {
  color: #ffffff80;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: white;
}

.footer-social {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-bottom: 20px;
}

.social-icon {
  width: 36px;
  height: 36px;
  line-height: 36px;
  border-radius: 50%;
  background-color: #344054;
  display: inline-block;
  transition: all 0.3s;
}

.social-icon:hover {
  background-color: #1E90FF;
  transform: translateY(-3px);
}

.copyright {
  font-size: 12px;
  margin-top: 10px;
  color: #ffffff60;
}
</style>