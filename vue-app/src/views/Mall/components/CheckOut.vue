<template>
  <div class="app-container">
    <!-- 顶部通栏 -->
    <header class="header">
      <div class="logo">点苏记</div>
      <!-- 搜索框（仅展示） -->
      <div class="search-container">
        <input 
          type="text" 
          placeholder="搜索非遗商品/路线"
          class="search-input"
          disabled
        >
        <button class="search-btn" disabled>搜索</button>
      </div>
      <!-- 用户操作区 -->
      <div class="user-actions">
        <button class="logout-btn" @click="router.push('/shop-car')">返回购物车</button>
      </div>
    </header>

    <!-- 主体支付容器 -->
    <main class="payment-container">
      <!-- 支付步骤指示器 -->
      <div class="payment-steps">
        <div class="step-item active">
          <div class="step-icon">1</div>
          <div class="step-text">确认订单</div>
        </div>
        <div class="step-line active"></div>
        <div class="step-item active">
          <div class="step-icon">2</div>
          <div class="step-text">选择支付方式</div>
        </div>
        <div class="step-line" :class="{ active: paymentStatus === 'success' }"></div>
        <div class="step-item" :class="{ active: paymentStatus === 'success' }">
          <div class="step-icon">3</div>
          <div class="step-text">支付完成</div>
        </div>
      </div>

      <!-- 待支付内容区 -->
      <div class="payment-content" v-if="paymentStatus === 'pending'">
        <!-- 订单信息卡片 -->
        <div class="card order-card">
          <div class="card-header">
            <h3 class="card-title">订单信息</h3>
            <span class="order-tag" :class="order.status === 'pending' ? 'tag-pending' : 'tag-normal'">
              {{ order.status === 'pending' ? '待支付' : '正常订单' }}
            </span>
          </div>
          <div class="order-detail">
            <!-- 多个商品列表 -->
            <div class="goods-list" v-if="order.selectedItems.length > 0">
              <div class="goods-item" v-for="(item, index) in order.selectedItems" :key="index">
                <img :src="item.image" :alt="item.name" class="goods-img">
                <div class="goods-info">
                  <h4 class="goods-name">{{ item.name }}</h4>
                  <p class="goods-spec">{{ item.spec || '默认规格' }}</p>
                  <p class="goods-quantity">数量：{{ item.quantity }}</p>
                </div>
                <div class="goods-price">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
              </div>
            </div>
            
            <!-- 商品为空时的提示 -->
            <div class="empty-goods" v-else>
              <p>暂无商品信息</p>
            </div>
            
            <div class="order-meta">
              <div class="meta-item">
                <span class="meta-label">订单编号：</span>
                <span class="meta-value">{{ order.orderNo }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">创建时间：</span>
                <span class="meta-value">{{ order.createTime }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">商品总数：</span>
                <span class="meta-value">{{ calculateTotalQuantity() }} 件</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">支付超时：</span>
                <span class="meta-value warn-text" v-if="order.status === 'pending'">
                  剩余 {{ countDown }} 分钟，请尽快支付
                </span>
                <span class="meta-value" v-else>已超时</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 支付方式卡片 -->
        <div class="card payment-method-card">
          <div class="card-header">
            <h3 class="card-title">选择支付方式</h3>
          </div>
          <div class="payment-methods">
            <label class="method-item active" @click="selectPaymentMethod('alipay')">
              <div class="method-icon alipay-icon">
                <svg viewBox="0 0 24 24" width="28" height="28">
                  <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1.99 16.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z" fill="#1677FF"/>
                </svg>
              </div>
              <div class="method-info">
                <div class="method-name">支付宝支付</div>
                <div class="method-desc">推荐使用，安全快捷</div>
              </div>
              <div class="method-check">
                <svg class="check-icon" viewBox="0 0 24 24" width="20" height="20" v-if="selectedMethod === 'alipay'">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" fill="#1677FF"/>
                </svg>
              </div>
            </label>
            <label class="method-item disabled" @click="showUnavailableTip">
              <div class="method-icon wechat-icon">
                <svg viewBox="0 0 24 24" width="28" height="28">
                  <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5.99 6c-.29 0-.56.1-.78.26L12 11.54l-5.21-3.28c-.23-.15-.49-.26-.79-.26-.84 0-1.53.64-1.61 1.46L4 12.22l1.12 6.69c.08.47.5.83.99.83.62 0 1.08-.53 1-1.15l-.66-3.93 4.8 2.98c.26.16.57.25.9.25.84 0 1.53-.64 1.61-1.46l1.12-6.72c.08-.82-.67-1.46-1.5-1.46z" fill="#7BB32E"/>
                </svg>
              </div>
              <div class="method-info">
                <div class="method-name">微信支付</div>
                <div class="method-desc">维护中，暂不可用</div>
              </div>
              <div class="method-check"></div>
            </label>
          </div>
        </div>

        <!-- 收货地址卡片 -->
        <div class="card address-card">
          <div class="card-header">
            <h3 class="card-title">收货地址</h3>
          </div>
          <div class="address-form">
            <div class="address-item">
              <label class="address-label">收货人</label>
              <input 
                v-model="receiver.name" 
                placeholder="请填写收货人姓名"
                class="address-input"
              >
            </div>
            <div class="address-item">
              <label class="address-label">联系电话</label>
              <input 
                v-model="receiver.phone" 
                placeholder="请填写联系电话"
                class="address-input"
                maxlength="11"
              >
            </div>
            <div class="address-item">
              <label class="address-label">详细地址</label>
              <div class="address-input-group">
                <input 
                  v-model="receiver.address" 
                  placeholder="请填写详细地址"
                  class="address-input w-full"
                >
                <button 
                  class="btn btn-outline locate-btn" 
                  @click="getCurrentLocation"
                  :disabled="isLocating"
                >
                  <span v-if="!isLocating">获取当前地址</span>
                  <span v-if="isLocating" class="loading-spinner"></span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 支付金额卡片 -->
        <div class="card payment-amount-card">
          <div class="amount-item">
            <span class="amount-label">商品金额：</span>
            <span class="amount-value">¥{{ order.amount }}</span>
          </div>
          <div class="amount-item">
            <span class="amount-label">运费：</span>
            <span class="amount-value">¥{{ order.freight || '0.00' }}</span>
          </div>
          <div class="amount-item">
            <span class="amount-label">优惠：</span>
            <span class="amount-value discount">¥{{ order.discount || '0.00' }}</span>
          </div>
          <div class="amount-total">
            <span class="total-label">实付金额：</span>
            <span class="total-value">¥{{ calculateActualAmount() }}</span>
          </div>
        </div>

        <!-- 支付按钮 -->
        <div class="payment-footer">
          <button class="btn btn-primary pay-btn" @click="handleAlipaySubmit" :disabled="isPaying">
            <span v-if="!isPaying">立即支付 ¥{{ calculateActualAmount() }}</span>
            <span v-if="isPaying" class="loading-spinner"></span>
          </button>
          <p class="payment-note">
            点击"立即支付"，即表示你同意<a href="#" class="link">《支付服务协议》</a>
          </p>
        </div>
      </div>

      <!-- 支付成功状态 -->
      <div class="payment-content success-content" v-if="paymentStatus === 'success'">
        <div class="success-icon">
          <svg viewBox="0 0 24 24" width="60" height="60">
            <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" fill="#52C41A"/>
          </svg>
        </div>
        <h2 class="success-title">支付成功</h2>
        <p class="success-desc">你的订单已支付完成，可前往订单列表查看详情</p>
        <div class="success-actions">
          <button class="btn btn-primary" @click="router.push('/user-center#orders')">查看订单</button>
          <button class="btn btn-outline" @click="router.push('/heritage-mall')">返回首页</button>
        </div>
        <div class="success-order-info">
          <p>订单编号：{{ order.orderNo }}</p>
          <p>商品总数：{{ calculateTotalQuantity() }} 件</p>
          <p>支付方式：支付宝支付</p>
          <p>支付金额：¥{{ calculateActualAmount() }}</p>
          <p>支付时间：{{ new Date().toLocaleString() }}</p>
        </div>
      </div>

      <!-- 支付失败状态 -->
      <div class="payment-content fail-content" v-if="paymentStatus === 'fail'">
        <div class="fail-icon">
          <svg viewBox="0 0 24 24" width="60" height="60">
            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" fill="#FF4D4F"/>
          </svg>
        </div>
        <h2 class="fail-title">支付失败</h2>
        <p class="fail-desc">{{ failReason || '支付过程中出现错误，请重试或选择其他支付方式' }}</p>
        <div class="fail-actions">
          <button class="btn btn-primary" @click="retryPayment">重新支付</button>
          <button class="btn btn-outline" @click="router.push('/user/orders')">返回订单</button>
        </div>
      </div>
    </main>

    <!-- 支付宝弹窗 -->
    <div class="modal-backdrop" v-if="showAlipayModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">支付宝支付确认</h3>
          <button class="modal-close" @click="closeAlipayModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="alipay-qrcode">
            <img src="https://gw.alipayobjects.com/zos/rmsportal/XuVpGqBFxXplzvLjJBZB.svg" alt="支付宝付款码" class="qrcode-img">
            <p class="qrcode-note">请使用支付宝扫码支付</p>
          </div>
          <div class="alipay-alt">
            <p>或点击以下按钮跳转支付宝APP支付</p>
            <button class="btn btn-primary alipay-btn" @click="simulateAlipayPayment">
              打开支付宝APP支付
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 提示弹窗 -->
    <div class="modal-backdrop" v-if="showTipModal">
      <div class="modal modal-sm" @click.stop>
        <div class="modal-body">
          <p class="tip-content">{{ tipContent }}</p>
          <button class="btn btn-primary tip-confirm-btn" @click="closeTipModal">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 1. 引入必要依赖（核心：引入高德地图Loader）
import { ref, reactive, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import AMapLoader from '@amap/amap-jsapi-loader'; // 缺少这行！加载SDK的关键
import axios from 'axios';

// 2. 基础配置
// 高德地图安全配置（必须与申请的Key/安全密钥匹配）
window._AMapSecurityConfig = {
  securityJsCode: "f3483cc8214e0a74539148f543b5ea37", // 替换为你的安全密钥
  key: "0035e805721abb4c2e32fe392aa9f290", // 替换为你的Web端Key（确保是JS API类型）
};

// 收货地址信息
const receiver = reactive({
  name: '',
  phone: '',
  address: ''
});

// 定位相关状态
const isLocating = ref(false);
let geolocation = null;
let geocoder = null;

// 路由相关
const router = useRouter();
const route = useRoute();

// 订单数据
const order = reactive({
  id: generateOrderId(),
  orderNo: generateOrderNo(),
  createTime: new Date().toLocaleString('zh-CN'),
  amount: route.query.totalPrice || '0.00',
  freight: route.query.totalPrice && parseFloat(route.query.totalPrice) >= 99 ? '0.00' : '10.00',
  discount: '0.00',
  status: 'pending',
  selectedItems: []
});

// 生成订单ID
function generateOrderId() {
  return Date.now() + Math.floor(Math.random() * 1000);
}

// 生成订单编号
function generateOrderNo() {
  const date = new Date();
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0');
  return `DD${year}${month}${day}${random}`;
}

// 从URL参数加载购物车数据
function loadCartData() {
  try {
    if (route.query.selectedItems) {
      order.selectedItems = JSON.parse(route.query.selectedItems);
      // 如果没有传递总价，重新计算
      if (!route.query.totalPrice) {
        let total = 0;
        order.selectedItems.forEach(item => {
          total += item.price * item.quantity;
        });
        order.amount = total.toFixed(2);
      }
    }
  } catch (error) {
    console.error('加载购物车数据失败:', error);
  }
}

// 计算商品总数量
function calculateTotalQuantity() {
  return order.selectedItems.reduce((sum, item) => sum + item.quantity, 0);
}

// 支付状态管理
const paymentStatus = ref('pending');
const selectedMethod = ref('alipay');
const isPaying = ref(false);
const countDown = ref(29);
const showAlipayModal = ref(false);
const showTipModal = ref(false);
const tipContent = ref('');
const failReason = ref('');
let countdownTimer = null;
let tipTimer = null;

// 3. 核心方法
// 计算实付金额
const calculateActualAmount = () => {
  const amount = parseFloat(order.amount);
  const freight = parseFloat(order.freight || '0');
  const discount = parseFloat(order.discount || '0');
  return (amount + freight - discount).toFixed(2);
};

// 选择支付方式
const selectPaymentMethod = (method) => {
  if (method === 'alipay') selectedMethod.value = 'alipay';
};

// 提示工具
const showTip = (content, autoClose = true) => {
  tipContent.value = content;
  showTipModal.value = true;
  if (autoClose) {
    clearTimeout(tipTimer);
    tipTimer = setTimeout(closeTipModal, 2000);
  }
};
const closeTipModal = () => {
  showTipModal.value = false;
  tipContent.value = '';
};

// 支付宝弹窗控制
const handleAlipaySubmit = () => {
  if (selectedMethod.value === 'alipay') showAlipayModal.value = true;
};
const closeAlipayModal = () => {
  showAlipayModal.value = false;
};

// API 基础配置
const apiBase = 'http://localhost:8080';

// 获取用户ID
const getUserId = () => {
  // 优先从sessionStorage获取
  const sid = sessionStorage.getItem('userId');
  if (sid) return sid;
  // 其次从localStorage的userInfo中获取
  try {
    const ui = localStorage.getItem('userInfo');
    if (ui) {
      const parsed = JSON.parse(ui);
      if (parsed && parsed.id) return parsed.id;
    }
  } catch (_) {}
  return null;
};

// 创建订单 - 调用OrderApi生成订单并存入数据库
const createOrder = async () => {
  try {
    const userId = getUserId();
    if (!userId) {
      throw new Error('未登录或用户信息丢失');
    }
    
    // 确保有选中商品
    if (!order.selectedItems || order.selectedItems.length === 0) {
      throw new Error('没有选中的商品');
    }

    console.log(`开始创建订单，订单号: ${order.orderNo}，用户ID: ${userId}`);
    
    // 为每个商品创建一个订单
    const orderPromises = order.selectedItems.map(async (item, index) => {
      // 重要修正：使用正确的商品详情ID
      // 打印完整的商品对象结构以便调试
      console.log(`商品${index + 1}完整信息:`, JSON.stringify(item));
      
      // 尝试从多个可能的字段获取商品详情ID
      // 根据后端日志，商品详情ID应该是类似'p001'这样的格式
      let productDetailId = item.productDetailId || item.detailId || (item.product && item.product.detailId);
      
      // 如果没有找到商品详情ID，尝试从后端API动态获取
      if (!productDetailId && item.productId) {
        console.log(`商品${index + 1}没有商品详情ID，尝试从后端获取`);
        try {
          const res = await axios.get('http://localhost:8080/api/productdetail/get', {
            params: { productId: item.productId }
          });
          const result = res.data || {};
          const ok = result.isSuccess === true || result.success === true;
          const details = ok && Array.isArray(result.data) ? result.data : [];
          
          // 如果有商品详情，使用第一个作为默认选择
          if (details.length > 0) {
            productDetailId = details[0].id;
            console.log(`动态获取到的商品详情ID: ${productDetailId}`);
          }
        } catch (e) {
          console.error('获取商品详情失败:', e);
        }
      }
      
      console.log(`商品${index + 1}ID映射 - 传递给后端的productDetailId: ${productDetailId}, 原productId: ${item.productId}, 原itemId: ${item.id}`);
      
      if (!productDetailId) {
        console.error('商品缺少必要的商品详情ID信息，无法创建订单:', item);
        return Promise.reject(new Error('商品详情信息不完整，无法创建订单'));
      }
      
      // 构建订单数据，严格按照后端API要求
      // 注意：后端会自己生成订单号，前端传递的可能会被覆盖
      const orderData = {
        productDetailId: productDetailId, // 必须字段
        // 不需要传递orderNumber，让后端生成
        // 不需要设置userId，由URL参数传递
        // 不需要设置status，后端会自己处理
        totalPrice: (item.price * item.quantity).toFixed(2),
        quantity: item.quantity
      };

      console.log(`商品${index + 1}订单请求数据:`, orderData);
      
      // 构建完整的请求URL
      const requestUrl = `${apiBase}/api/order/add`;
      console.log(`准备发送POST请求到: ${requestUrl}?userId=${userId}`);
      
      // 调用后端OrderApi的add方法，userId作为URL参数
      return axios.post(requestUrl, orderData, {
        params: { userId: userId },
        headers: {
          'Content-Type': 'application/json'
        }
      }).then(response => {
        console.log(`商品${index + 1}订单创建响应:`, response.data);
        return response;
      }).catch(error => {
        console.error(`商品${index + 1}订单创建失败:`, error.response ? error.response.data : error.message);
        throw error;
      });
    });

    console.log('等待所有订单创建完成...');
    const results = await Promise.all(orderPromises);
    console.log('所有订单创建完成，总数量:', results.length);
    
    // 验证订单创建结果
    const successResults = results.filter(r => r && r.data && r.data.success !== false);
    if (successResults.length !== results.length) {
      console.warn('部分订单可能创建失败，成功数:', successResults.length, '总数:', results.length);
    }
    
    return results;
  } catch (error) {
    console.error('创建订单失败:', error.response ? error.response.data : error.message);
    throw error;
  }
};

// 调用支付接口 - 根据订单号生成支付流水
const processPayment = async (orderResults) => {
  try {
    const userId = getUserId();
    if (!userId) {
      throw new Error('未登录或用户信息丢失');
    }
    
    // 验证订单结果是否有效
    if (!orderResults || !Array.isArray(orderResults) || orderResults.length === 0) {
      throw new Error('无效的订单结果');
    }
    
    // 安全地获取后端返回的第一个订单的订单号，确保与数据库中存储的一致
    // 注意：根据后端OrderServiceImpl的实现，订单号在data对象中
    let backendOrderNumber = order.orderNo; // 默认值
    
    // 检查第一个订单响应
    const firstOrder = orderResults[0];
    if (firstOrder && firstOrder.data) {
      // 尝试从不同位置获取订单号，适配后端可能的不同返回格式
      if (firstOrder.data.orderNumber) {
        backendOrderNumber = firstOrder.data.orderNumber;
      } else if (firstOrder.data.data && firstOrder.data.data.orderNumber) {
        backendOrderNumber = firstOrder.data.data.orderNumber;
      } else if (firstOrder.data.orderNo) {
        backendOrderNumber = firstOrder.data.orderNo;
      }
    }
    
    console.log('获取到的后端订单号:', backendOrderNumber);
    if (backendOrderNumber === order.orderNo) {
      console.warn('使用了前端生成的订单号，可能未成功获取后端生成的订单号');
    }
                               
    console.log('准备生成支付流水，使用订单号:', backendOrderNumber);

    // 计算支付金额
    const actualAmount = parseFloat(calculateActualAmount());
    
    // 构建支付参数
    const paymentParam = {
      userId: userId,
      orderNumber: backendOrderNumber,
      totalAmount: actualAmount,
      payAmount: actualAmount,
      payType: 'ALIPAY' // 确保使用大写的支付类型
    };

    console.log('支付请求参数:', paymentParam);
    
    // 调用支付接口生成支付流水
    const paymentUrl = `${apiBase}/api/alipay/pay`;
    console.log(`发送支付请求到: ${paymentUrl}`);
    
    const response = await axios.post(paymentUrl, paymentParam, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    
    console.log('支付流水生成成功，响应:', response.data);
    
    return response.data;
  } catch (error) {
    console.error('支付流水生成失败:', error.response ? error.response.data : error.message);
    throw error;
  }
};

// 实际支付宝支付处理
const simulateAlipayPayment = async () => {
  isPaying.value = true;
  closeAlipayModal();
  showTip('正在处理支付，请稍候...', false);
  console.log('=== 开始支付流程 ===');

  try {
    // 1. 先创建订单 - 调用OrderApi生成订单并存入数据库
    console.log('步骤1: 创建订单');
    showTip('正在创建订单...', false);
    
    const orderResults = await createOrder();
    
    // 验证订单创建结果
    if (!orderResults || orderResults.length === 0) {
      throw new Error('订单创建失败，未返回任何订单数据');
    }
    
    console.log('订单创建完成，继续支付流程');
    
    // 2. 然后根据订单号生成支付流水
    console.log('步骤2: 生成支付流水');
    showTip('正在处理支付...', false);
    
    const paymentResult = await processPayment(orderResults);
    
    // 3. 处理支付结果
    console.log('步骤3: 处理支付结果');
    isPaying.value = false;
    closeTipModal();
    
    if (paymentResult && paymentResult.success) {
      console.log('支付成功!');
      paymentStatus.value = 'success';
      order.status = 'paid';
      
      // 支付成功后，调用API删除购物车中已购买的商品
      try {
        console.log('开始清理购物车已购买商品');
        // 获取购物车中已选中的商品ID
        const selectedItemsJson = route.query.selectedItems || '[]';
        console.log('购物车选中商品JSON:', selectedItemsJson);
        
        const selectedItemIds = JSON.parse(selectedItemsJson)
          .map(item => item.id || item.cartItemId)
          .filter(Boolean);
        
        console.log('需要删除的购物车商品ID列表:', selectedItemIds);
        
        // 批量删除已购买的商品
        if (selectedItemIds.length > 0) {
          for (const itemId of selectedItemIds) {
            const deleteUrl = `${apiBase}/api/cart/${itemId}`;
            console.log(`删除购物车商品: ${itemId}`);
            await axios.delete(deleteUrl);
          }
          console.log('购物车已购买商品已清空');
        } else {
          console.log('没有需要删除的购物车商品');
        }
      } catch (error) {
        console.error('清空购物车商品失败:', error.response ? error.response.data : error.message);
        // 这里不影响支付结果，只是记录错误
      }
    } else {
      console.log('支付失败!', paymentResult);
      paymentStatus.value = 'fail';
      failReason.value = paymentResult?.message || '支付失败，请重试';
    }
  } catch (error) {
    console.error('支付过程中发生错误:', error.response ? error.response.data : error.message);
    isPaying.value = false;
    closeTipModal();
    paymentStatus.value = 'fail';
    failReason.value = error.message || '支付处理失败，请稍后重试';
  } finally {
    console.log('=== 支付流程结束 ===');
  }
};

// 重新支付
const retryPayment = () => {
  paymentStatus.value = 'pending';
  failReason.value = '';
};

// 显示支付方式不可用提示
const showUnavailableTip = () => {
  showTip('当前仅支持支付宝支付');
};

// 倒计时初始化
const initCountdown = () => {
  countdownTimer = setInterval(() => {
    if (countDown.value > 0) countDown.value--;
    else {
      clearInterval(countdownTimer);
      router.push('/user/orders');
    }
  }, 60000);
};

// 组件挂载时加载数据
onMounted(() => {
  loadCartData();
  initCountdown();
});

// 定位与地址解析（核心功能）
const getCurrentLocation = () => {
  if (!window.AMap || !geocoder) {
    showTip('地图服务未加载，请稍后重试');
    return;
  }

  isLocating.value = true;
  showTip('正在获取您的位置...', false);

  // 销毁旧实例，避免内存泄漏
  if (geolocation) geolocation.destroy();

  // 初始化定位
  geolocation = new window.AMap.Geolocation({
    enableHighAccuracy: true, // 高精度定位
    timeout: 10000, // 超时时间10秒
    maximumAge: 0, // 不使用缓存
    showButton: false, // 隐藏默认定位按钮
    panToLocation: false, // 不自动移动地图
    zoomToAccuracy: false // 不调整地图视野
  });

  // 定位回调
  geolocation.getCurrentPosition((status, result) => {
    isLocating.value = false;
    closeTipModal();

    if (status === 'complete') {
      const { lng, lat } = result.position;
      // 经纬度转具体地址（地理编码）
      geocoder.getAddress([lng, lat], (geoStatus, geoResult) => {
        if (geoStatus === 'complete' && geoResult.regeocode) {
          receiver.address = geoResult.regeocode.formattedAddress;
          showTip('地址获取成功');
        } else {
          showTip('地址解析失败，请手动输入');
        }
      });
    } else {
      // 定位失败提示
      const errMsg = {
        error: '定位错误，请检查设备定位功能',
        timeout: '定位超时，请重试',
        noPermission: '请允许定位权限后重试'
      }[status] || '定位失败，请手动输入地址';
      showTip(errMsg);
    }
  });
};

// 4. 生命周期（初始化高德地图）
onMounted(() => {
  initCountdown();

  // 加载高德地图SDK（核心步骤）
  AMapLoader.load({
    key: window._AMapSecurityConfig.key, // 传入Key
    version: "2.0", // 指定SDK版本
    plugins: ['AMap.Geolocation', 'AMap.Geocoder'] // 仅加载定位和地理编码插件（按需加载）
  }).then((AMap) => {
    window.AMap = AMap; // 挂载到window，方便全局使用
    // 初始化地理编码实例（用于地址解析）
    geocoder = new AMap.Geocoder({
      city: '全国', // 全国范围解析
      radius: 1000 // 解析半径
    });
    showTip('地图服务加载成功');
  }).catch((e) => {
    console.error('高德地图加载失败：', e);
    showTip('地图服务加载失败，定位功能不可用');
  });
});

// 组件卸载清理
onUnmounted(() => {
  if (countdownTimer) clearInterval(countdownTimer);
  if (tipTimer) clearTimeout(tipTimer);
  if (geolocation) geolocation.destroy();
});
</script>

<style scoped>
/* 地址卡片样式 */
.address-card { background: #fff; margin-bottom: 20px; }
.address-form { display: flex; flex-direction: column; gap: 16px; }
.address-item { display: flex; align-items: center; gap: 12px; }
.address-label { width: 80px; font-size: 14px; color: #666; flex-shrink: 0; }
.address-input { flex: 1; padding: 10px 12px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; outline: none; transition: border .2s; }
.address-input:focus { border-color: #1E90FF; }
.address-input-group { flex: 1; display: flex; gap: 10px; }
.w-full { width: 100%; }
.locate-btn { white-space: nowrap; padding: 0 16px; min-width: 120px; }

/* 基础样式 */
.app-container { min-height: 100vh; background: #f5f7fa; color: #333; }
.header { height: 60px; padding: 0 20px; background: #fff; box-shadow: 0 2px 4px rgba(0,0,0,.1); display: flex; align-items: center; justify-content: space-between; position: sticky; top: 0; z-index: 10; }
.logo { font-size: 24px; font-weight: bold; color: #1E90FF; }
.search-container { display: flex; flex: 1; max-width: 400px; margin: 0 20px; }
.search-input { flex: 1; padding: 8px 12px; border: 1px solid #ddd; border-right: none; border-radius: 4px 0 0 4px; outline: none; background: #f5f5f5; }
.search-btn { padding: 0 16px; background: #e0e0e0; color: #999; border: none; border-radius: 0 4px 4px 0; cursor: not-allowed; }
.user-actions { display: flex; align-items: center; gap: 16px; }
.logout-btn { padding: 6px 12px; background: transparent; color: #1E90FF; border: 1px solid #1E90FF; border-radius: 4px; cursor: pointer; transition: all .2s; }
.logout-btn:hover { background: rgba(30,144,255,.1); }

/* 支付页面主体 */
.payment-container { max-width: 800px; margin: 0 auto; padding: 20px; }

/* 支付步骤 */
.payment-steps { display: flex; align-items: center; justify-content: space-between; margin-bottom: 30px; padding: 0 20px; }
.step-item { display: flex; flex-direction: column; align-items: center; position: relative; z-index: 2; }
.step-icon { width: 36px; height: 36px; border-radius: 50%; background: #e0e0e0; color: #fff; display: flex; align-items: center; justify-content: center; font-weight: bold; margin-bottom: 8px; }
.step-text { font-size: 14px; color: #999; }
.step-item.active .step-icon { background: #1E90FF; }
.step-item.active .step-text { color: #1E90FF; font-weight: 500; }
.step-line { flex: 1; height: 2px; background: #e0e0e0; margin: 0 10px; }
.step-line.active { background: #1E90FF; }

/* 卡片通用样式 */
.card { background: #fff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,.08); padding: 20px; margin-bottom: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.card-title { font-size: 16px; color: #1E90FF; font-weight: 500; }

/* 订单卡片 */
.order-card .card-header { margin-bottom: 20px; }
.order-tag { padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 500; }
.tag-pending { background: rgba(255,153,0,.1); color: #FAAD14; }
.tag-normal { background: rgba(82,196,26,.1); color: #52C41A; }
.goods-list { margin-bottom: 15px; }
.goods-item { display: flex; align-items: center; gap: 16px; padding-bottom: 16px; border-bottom: 1px solid #f0f0f0; margin-bottom: 16px; }
.goods-item:last-child { border-bottom: none; margin-bottom: 0; }
.goods-img { width: 80px; height: 80px; object-fit: cover; border-radius: 4px; }
.goods-info { flex: 1; }
.goods-name { font-size: 14px; font-weight: 500; margin-bottom: 4px; }
.goods-spec { font-size: 12px; color: #666; }
.goods-price { font-size: 14px; font-weight: 500; color: #1E90FF; }
.goods-quantity { font-size: 12px; color: #666; margin-top: 4px; }
.empty-goods { text-align: center; padding: 20px 0; color: #999; }
.order-meta { display: flex; flex-direction: column; gap: 12px; }
.meta-item { display: flex; }
.meta-label { width: 80px; font-size: 14px; color: #666; flex-shrink: 0; }
.meta-value { font-size: 14px; flex: 1; }
.warn-text { color: #FAAD14; font-weight: 500; }

/* 支付方式卡片 */
.payment-methods { display: flex; flex-direction: column; gap: 10px; }
.method-item { display: flex; align-items: center; padding: 14px 16px; border-radius: 6px; border: 1px solid #eee; cursor: pointer; transition: all .2s; }
.method-item.active { border-color: #1E90FF; background: rgba(30,144,255,.05); }
.method-item.disabled { cursor: not-allowed; opacity: .7; }
.method-icon { margin-right: 16px; }
.method-info { flex: 1; }
.method-name { font-size: 14px; font-weight: 500; margin-bottom: 2px; }
.method-desc { font-size: 12px; color: #666; }
.method-check { display: flex; align-items: center; justify-content: center; }

/* 支付金额卡片 */
.payment-amount-card { background: #f9fafb; border: 1px dashed #e0e0e0; }
.amount-item { display: flex; justify-content: space-between; padding: 8px 0; font-size: 14px; }
.amount-label { color: #666; }
.amount-value { color: #333; }
.discount { color: #FF4D4F; }
.amount-total { display: flex; justify-content: space-between; padding: 12px 0 8px; margin-top: 8px; border-top: 1px solid #eee; }
.total-label { font-size: 16px; font-weight: 500; color: #333; }
.total-value { font-size: 18px; font-weight: 500; color: #1E90FF; }

/* 支付按钮区域 */
.payment-footer { display: flex; flex-direction: column; gap: 12px; }
.pay-btn { width: 100%; padding: 12px 0; font-size: 16px; font-weight: 500; }
.payment-note { font-size: 12px; color: #999; text-align: center; }
.link { color: #1E90FF; text-decoration: none; }
.link:hover { text-decoration: underline; }

/* 支付成功/失败状态 */
.success-content, .fail-content { display: flex; flex-direction: column; align-items: center; text-align: center; padding: 40px 20px; }
.success-icon, .fail-icon { margin-bottom: 24px; }
.success-title { font-size: 24px; font-weight: 500; color: #52C41A; margin-bottom: 12px; }
.fail-title { font-size: 24px; font-weight: 500; color: #FF4D4F; margin-bottom: 12px; }
.success-desc, .fail-desc { font-size: 14px; color: #666; margin-bottom: 30px; max-width: 400px; }
.success-actions, .fail-actions { display: flex; gap: 16px; margin-bottom: 30px; }
.success-order-info { background: #f9fafb; border-radius: 8px; padding: 16px; width: 100%; max-width: 400px; text-align: left; font-size: 14px; }
.success-order-info p { margin-bottom: 8px; }
.success-order-info p:last-child { margin-bottom: 0; }

/* 弹窗样式 */
.modal-backdrop { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,.5); display: flex; align-items: center; justify-content: center; z-index: 100; padding: 20px; }
.modal { background: #fff; border-radius: 8px; width: 100%; max-width: 400px; box-shadow: 0 4px 20px rgba(0,0,0,.15); animation: modalFadeIn .3s ease; }
.modal-sm { max-width: 300px; }
@keyframes modalFadeIn { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid #eee; }
.modal-title { font-size: 16px; font-weight: 500; }
.modal-close { background: transparent; border: none; font-size: 20px; cursor: pointer; color: #666; }
.modal-body { padding: 20px; }
.alipay-qrcode { display: flex; flex-direction: column; align-items: center; margin-bottom: 24px; padding-bottom: 24px; border-bottom: 1px dashed #eee; }
.qrcode-img { width: 200px; height: 200px; margin-bottom: 12px; }
.qrcode-note { font-size: 14px; color: #666; }
.alipay-alt { display: flex; flex-direction: column; align-items: center; gap: 16px; }
.alipay-alt p { font-size: 14px; color: #333; }
.alipay-btn { width: 100%; padding: 10px 0; }

/* 提示弹窗 */
.tip-content { font-size: 14px; text-align: center; margin-bottom: 20px; padding: 10px 0; }
.tip-confirm-btn { width: 100%; }

/* 加载动画 */
.loading-spinner { display: inline-block; width: 20px; height: 20px; border: 3px solid rgba(255,255,255,.3); border-radius: 50%; border-top-color: #fff; animation: spin 1s ease-in-out infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

/* 按钮通用样式 */
.btn { padding: 6px 12px; border-radius: 4px; cursor: pointer; font-size: 14px; transition: all .2s; }
.btn-primary { background: #1E90FF; color: #fff; border: 1px solid #1E90FF; }
.btn-primary:hover { background: #096dd9; border-color: #096dd9; }
.btn-outline { background: transparent; color: #1E90FF; border: 1px solid #1E90FF; }
.btn-outline:hover { background: rgba(30,144,255,.1); }

/* 响应式调整 */
@media (max-width: 768px) {
  .search-container { max-width: none; margin: 0 10px; }
  .payment-steps { padding: 0; }
  .step-text { font-size: 12px; }
  .goods-item { flex-wrap: wrap; }
  .goods-price { width: 100%; text-align: right; }
  .success-actions, .fail-actions { flex-direction: column; width: 100%; }
  .success-actions .btn, .fail-actions .btn { width: 100%; }
  .address-item { flex-direction: column; align-items: flex-start; gap: 8px; }
  .address-label { width: auto; }
  .address-input-group { width: 100%; flex-direction: column; }
  .locate-btn { width: 100%; padding: 10px 0; }
}
@media (max-width: 480px) {
  .header { padding: 0 10px; }
  .logo { font-size: 18px; }
  .search-container { display: none; }
  .step-text { display: none; }
  .qrcode-img { width: 160px; height: 160px; }
}
</style>