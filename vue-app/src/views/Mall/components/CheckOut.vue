<template>
  <div class="app-container">
    <!-- 顶部通栏（复用原项目头部样式） -->
    <header class="header">
      <div class="logo">点苏记</div>
      
      <!-- 搜索框（仅展示，支付页可简化） -->
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
        <!-- <button class="notify-btn" title="消息通知">
          <svg class="icon" viewBox="0 0 24 24">
            <path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.18C9.63 5.36 8 7.92 8 11v5l-2 2v1h16v-1l-2-2z"/>
          </svg>
        </button> -->
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

      <!-- 支付内容区（根据支付状态切换） -->
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
            <!-- 商品信息 -->
            <div class="goods-item">
              <img :src="order.goodsImg" alt="商品图片" class="goods-img">
              <div class="goods-info">
                <h4 class="goods-name">{{ order.goodsName }}</h4>
                <p class="goods-spec">{{ order.goodsSpec }}</p>
              </div>
              <div class="goods-price">¥{{ order.amount }}</div>
            </div>
            
            <!-- 订单其他信息 -->
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
                <span class="meta-label">支付超时：</span>
                <span class="meta-value warn-text" v-if="order.status === 'pending'">
                  剩余 {{ countDown }} 分钟，请尽快支付
                </span>
                <span class="meta-value" v-else>已超时</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 支付方式选择卡片 -->
        <div class="card payment-method-card">
          <div class="card-header">
            <h3 class="card-title">选择支付方式</h3>
          </div>
          
          <div class="payment-methods">
            <!-- 支付宝支付选项（默认选中） -->
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
            
            <!-- 其他支付方式（灰色不可选，突出支付宝） -->
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
        
        <!-- 订单简要信息 -->
        <div class="success-order-info">
          <p>订单编号：{{ order.orderNo }}</p>
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

    <!-- 支付弹窗（模拟支付宝跳转确认） -->
    <div class="modal-backdrop" v-if="showAlipayModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">支付宝支付确认</h3>
          <button class="modal-close" @click="closeAlipayModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="alipay-qrcode">
            <!-- 模拟支付宝付款码（实际项目中应从后端获取真实二维码） -->
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
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
const receiver = reactive({
  name: '',
  phone: '',
  address: ''
});

// 新增：定位状态管理
const isLocating = ref(false);
let geolocation = null;
let geocoder = null;

// 原有订单数据和状态管理不变

// ...其他原有状态

// 新增：初始化高德地图工具
onMounted(() => {
  initCountdown();
  
  // 初始化定位和地理编码工具
  if (window.AMap) {
    // 初始化地理编码（经纬度转地址）
    geocoder = new AMap.Geocoder({
      city: '全国',
      radius: 1000
    });
  } else {
    console.warn('https://webapi.amap.com/loader.js?v=2.0&key=0035e805721abb4c2e32fe392aa9f290');
  }
});

// 新增：获取当前位置并解析为地址
const getCurrentLocation = () => {
  if (!window.AMap) {
    showTip('地图服务加载失败，请稍后重试');
    return;
  }

  isLocating.value = true;
  showTip('正在获取您的位置...', false);

  // 销毁之前的定位实例
  if (geolocation) {
    geolocation.destroy();
  }

  // 创建定位实例
  geolocation = new AMap.Geolocation({
    enableHighAccuracy: true, // 高精度定位
    timeout: 10000, // 超时时间
    maximumAge: 0, // 不使用缓存
    showButton: false, // 不显示默认定位按钮
    panToLocation: false, // 定位成功后不自动移动地图
    zoomToAccuracy: false // 不调整地图视野
  });

  // 绑定定位事件
  geolocation.getCurrentPosition((status, result) => {
    isLocating.value = false;
    closeTipModal();

    if (status === 'complete') {
      // 定位成功，解析地址
      const { lng, lat } = result.position;
      geocoder.getAddress([lng, lat], (geoStatus, geoResult) => {
        if (geoStatus === 'complete' && geoResult.regeocode) {
          receiver.address = geoResult.regeocode.formattedAddress;
          showTip('地址获取成功');
        } else {
          showTip('地址解析失败，请手动输入');
        }
      });
    } else {
      // 定位失败处理
      switch (status) {
        case 'error':
          showTip('定位错误，请检查设备定位功能');
          break;
        case 'timeout':
          showTip('定位超时，请重试');
          break;
        case 'noPermission':
          showTip('请允许定位权限后重试');
          break;
        default:
          showTip('定位失败，请手动输入地址');
      }
    }
  });
};

// 新增：提示工具函数
const showTip = (content, autoClose = true) => {
  tipContent.value = content;
  showTipModal.value = true;
  
  if (autoClose) {
    clearTimeout(tipTimer);
    tipTimer = setTimeout(closeTipModal, 2000);
  }
};
let tipTimer = null;

onUnmounted(() => {
  if (countdownTimer) clearInterval(countdownTimer);
  if (tipTimer) clearTimeout(tipTimer);
  if (geolocation) geolocation.destroy(); // 销毁定位实例
});

const router = useRouter();
const route = useRoute();

// 订单数据（从路由参数获取，与原订单列表关联）
const order = reactive({
  // 默认值，实际项目中应从路由参数或接口获取
  id: route.query.orderId || 1,
  orderNo: route.query.orderNo || 'DD202409035678',
  createTime: route.query.createTime || '2024-09-03 15:40',
  amount: route.query.amount || '176.00',
  freight: '0.00', // 运费
  discount: '0.00', // 优惠金额
  status: 'pending', // 订单状态：pending-待支付
  goodsName: route.query.goodsName || '南京樱桃鸭（礼盒装）',
  goodsSpec: route.query.goodsSpec || '规格：1kg | 单价：¥88.00 | 数量：2',
  goodsImg: route.query.goodsImg || 'https://img.alicdn.com/i2/3913611788/O1CN01UWkMK61P4wliRakpz_!!3913611788.jpg'
});

// 支付状态：pending-待支付，success-支付成功，fail-支付失败
const paymentStatus = ref('pending');
// 选中的支付方式：alipay-支付宝，wechat-微信（当前仅支付宝可用）
const selectedMethod = ref('alipay');
// 支付加载状态
const isPaying = ref(false);
// 倒计时（单位：分钟，模拟支付超时）
const countDown = ref(29);
// 支付宝支付弹窗显示状态
const showAlipayModal = ref(false);
// 提示弹窗状态
const showTipModal = ref(false);
// 提示内容
const tipContent = ref('');
// 支付失败原因
const failReason = ref('');
// 倒计时计时器
let countdownTimer = null;

// 计算实付金额
const calculateActualAmount = () => {
  const amount = parseFloat(order.amount);
  const freight = parseFloat(order.freight || '0');
  const discount = parseFloat(order.discount || '0');
  return (amount + freight - discount).toFixed(2);
};

// 选择支付方式
const selectPaymentMethod = (method) => {
  if (method === 'alipay') {
    selectedMethod.value = 'alipay';
  }
};

// 显示不可用提示
const showUnavailableTip = () => {
  tipContent.value = '当前仅支持支付宝支付';
  showTipModal.value = true;
};

// 关闭提示弹窗
const closeTipModal = () => {
  showTipModal.value = false;
  tipContent.value = '';
};

// 打开支付宝支付弹窗
const handleAlipaySubmit = () => {
  if (selectedMethod.value === 'alipay') {
    showAlipayModal.value = true;
  }
};

// 关闭支付宝支付弹窗
const closeAlipayModal = () => {
  showAlipayModal.value = false;
};

// 模拟支付宝支付过程
const simulateAlipayPayment = () => {
  isPaying.value = true;
  closeAlipayModal();
  
  // 模拟支付过程（2秒后随机成功/失败）
  setTimeout(() => {
    isPaying.value = false;
    // 实际项目中应根据接口返回结果判断
    const isSuccess = Math.random() > 0.2; // 80%成功率
    
    if (isSuccess) {
      paymentStatus.value = 'success';
      // 实际项目中应调用后端接口更新订单状态
      order.status = 'paid';
    } else {
      paymentStatus.value = 'fail';
      failReason.value = '支付过程中断，请重试';
    }
  }, 2000);
};

// 重新支付
const retryPayment = () => {
  paymentStatus.value = 'pending';
  failReason.value = '';
};

// 初始化倒计时
const initCountdown = () => {
  countdownTimer = setInterval(() => {
    if (countDown.value > 0) {
      countDown.value--;
    } else {
      // 倒计时结束，自动跳回订单页
      clearInterval(countdownTimer);
      router.push('/user/orders');
    }
  }, 60000); // 每分钟减1
};

// 组件挂载时初始化
onMounted(() => {
  initCountdown();
});

// 组件卸载时清理
onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer);
  }
});
</script>

<style scoped>
.address-card {
  background-color: white;
  margin-bottom: 20px;
}

.address-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.address-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.address-label {
  width: 80px;
  font-size: 14px;
  color: #666;
  flex-shrink: 0;
}

.address-input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.address-input:focus {
  border-color: #1E90FF;
}

.address-input-group {
  flex: 1;
  display: flex;
  gap: 10px;
}

.w-full {
  width: 100%;
}

.locate-btn {
  white-space: nowrap;
  padding: 0 16px;
  min-width: 120px;
}

/* 适配响应式 */
@media (max-width: 768px) {
  .address-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .address-label {
    width: auto;
  }
  
  .address-input-group {
    width: 100%;
    flex-direction: column;
  }
  
  .locate-btn {
    width: 100%;
    padding: 10px 0;
  }
}
/* 基础样式复用原项目风格 */
.app-container {
  min-height: 100vh;
  background-color: #f5f7fa;
  color: #333;
}

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
  background-color: #f5f5f5;
}

.search-btn {
  padding: 0 16px;
  background-color: #e0e0e0;
  color: #999;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: not-allowed;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.notify-btn {
  background: transparent;
  border: none;
  cursor: pointer;
}

.icon {
  width: 24px;
  height: 24px;
  fill: #1E90FF;
}

.logout-btn {
  padding: 6px 12px;
  background-color: transparent;
  color: #1E90FF;
  border: 1px solid #1E90FF;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.logout-btn:hover {
  background-color: rgba(30, 144, 255, 0.1);
}

/* 支付页面主体样式 */
.payment-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

/* 支付步骤 */
.payment-steps {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30px;
  padding: 0 20px;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 2;
}

.step-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #e0e0e0;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-bottom: 8px;
}

.step-text {
  font-size: 14px;
  color: #999;
}

.step-item.active .step-icon {
  background-color: #1E90FF;
}

.step-item.active .step-text {
  color: #1E90FF;
  font-weight: 500;
}

.step-line {
  flex: 1;
  height: 2px;
  background-color: #e0e0e0;
  margin: 0 10px;
}

.step-line.active {
  background-color: #1E90FF;
}

/* 卡片样式 */
.card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 20px;
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-title {
  font-size: 16px;
  color: #1E90FF;
  font-weight: 500;
}

/* 订单卡片 */
.order-card .card-header {
  margin-bottom: 20px;
}

.order-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.tag-pending {
  background-color: rgba(255, 153, 0, 0.1);
  color: #FAAD14;
}

.tag-normal {
  background-color: rgba(82, 196, 26, 0.1);
  color: #52C41A;
}

.goods-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 16px;
}

.goods-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.goods-info {
  flex: 1;
}

.goods-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.goods-spec {
  font-size: 12px;
  color: #666;
}

.goods-price {
  font-size: 14px;
  font-weight: 500;
  color: #1E90FF;
}

.order-meta {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.meta-item {
  display: flex;
}

.meta-label {
  width: 80px;
  font-size: 14px;
  color: #666;
  flex-shrink: 0;
}

.meta-value {
  font-size: 14px;
  flex: 1;
}

.warn-text {
  color: #FAAD14;
  font-weight: 500;
}

/* 支付方式卡片 */
.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.method-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-radius: 6px;
  border: 1px solid #eee;
  cursor: pointer;
  transition: all 0.2s;
}

.method-item.active {
  border-color: #1E90FF;
  background-color: rgba(30, 144, 255, 0.05);
}

.method-item.disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.method-icon {
  margin-right: 16px;
}

.method-info {
  flex: 1;
}

.method-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 2px;
}

.method-desc {
  font-size: 12px;
  color: #666;
}

.method-check {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 支付金额卡片 */
.payment-amount-card {
  background-color: #f9fafb;
  border: 1px dashed #e0e0e0;
}

.amount-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 14px;
}

.amount-label {
  color: #666;
}

.amount-value {
  color: #333;
}

.discount {
  color: #FF4D4F;
}

.amount-total {
  display: flex;
  justify-content: space-between;
  padding: 12px 0 8px;
  margin-top: 8px;
  border-top: 1px solid #eee;
}

.total-label {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.total-value {
  font-size: 18px;
  font-weight: 500;
  color: #1E90FF;
}

/* 支付按钮区域 */
.payment-footer {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.pay-btn {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
  font-weight: 500;
}

.payment-note {
  font-size: 12px;
  color: #999;
  text-align: center;
}

.link {
  color: #1E90FF;
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}

/* 支付成功/失败状态样式 */
.success-content, .fail-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 40px 20px;
}

.success-icon, .fail-icon {
  margin-bottom: 24px;
}

.success-title {
  font-size: 24px;
  font-weight: 500;
  color: #52C41A;
  margin-bottom: 12px;
}

.fail-title {
  font-size: 24px;
  font-weight: 500;
  color: #FF4D4F;
  margin-bottom: 12px;
}

.success-desc, .fail-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 30px;
  max-width: 400px;
}

.success-actions, .fail-actions {
  display: flex;
  gap: 16px;
  margin-bottom: 30px;
}

.success-order-info {
  background-color: #f9fafb;
  border-radius: 8px;
  padding: 16px;
  width: 100%;
  max-width: 400px;
  text-align: left;
  font-size: 14px;
}

.success-order-info p {
  margin-bottom: 8px;
}

.success-order-info p:last-child {
  margin-bottom: 0;
}

/* 支付宝支付弹窗 */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  padding: 20px;
}

.modal {
  background-color: white;
  border-radius: 8px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  animation: modalFadeIn 0.3s ease;
}

.modal-sm {
  max-width: 300px;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
}

.modal-title {
  font-size: 16px;
  font-weight: 500;
}

.modal-close {
  background: transparent;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #666;
}

.modal-body {
  padding: 20px;
}

.alipay-qrcode {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px dashed #eee;
}

.qrcode-img {
  width: 200px;
  height: 200px;
  margin-bottom: 12px;
}

.qrcode-note {
  font-size: 14px;
  color: #666;
}

.alipay-alt {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.alipay-alt p {
  font-size: 14px;
  color: #333;
}

.alipay-btn {
  width: 100%;
  padding: 10px 0;
}

/* 提示弹窗 */
.tip-content {
  font-size: 14px;
  text-align: center;
  margin-bottom: 20px;
  padding: 10px 0;
}

.tip-confirm-btn {
  width: 100%;
}

/* 加载动画 */
.loading-spinner {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255,255,255,.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 按钮样式复用原项目 */
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
  background-color: #096dd9;
  border-color: #096dd9;
}

.btn-outline {
  background-color: transparent;
  color: #1E90FF;
  border: 1px solid #1E90FF;
}

.btn-outline:hover {
  background-color: rgba(30, 144, 255, 0.1);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .search-container {
    max-width: none;
    margin: 0 10px;
  }
  
  .payment-steps {
    padding: 0;
  }
  
  .step-text {
    font-size: 12px;
  }
  
  .goods-item {
    flex-wrap: wrap;
  }
  
  .goods-price {
    width: 100%;
    text-align: right;
  }
  
  .success-actions, .fail-actions {
    flex-direction: column;
    width: 100%;
  }
  
  .success-actions .btn, .fail-actions .btn {
    width: 100%;
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
  
  .step-text {
    display: none;
  }
  
  .qrcode-img {
    width: 160px;
    height: 160px;
  }
}
</style>
