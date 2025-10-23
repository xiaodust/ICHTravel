<template>
  <div class="product-detail-page">
    <!-- 导航路径 -->
    <div class="breadcrumbs">
      <div class="container">
        <a href="/">首页</a> &gt; 
        <a href="/heritage-mall">全部商品</a> &gt; 
        <span>{{ product?.name || '商品详情' }}</span>
      </div>
    </div>

    <!-- 商品详情详情主体 -->
    <div class="product-detail-container container">
      <!-- 加载状态 -->
      <div class="loading-container" v-if="isLoading">
        <div class="spinner"></div>
        <p class="loading-text">加载商品详情中...</p>
      </div>

      <!-- 商品不存在提示 -->
      <div class="not-found" v-if="!isLoading && !product">
        <p>抱歉，未找到该商品信息</p>
        <button class="back-btn" @click="$router.push('/products')">返回商品列表</button>
      </div>

      <!-- 商品详情内容 -->
      <div class="product-detail-content" v-if="!isLoading && product">
        <!-- 左侧商品图片 -->
        <div class="product-images">
          <div class="main-image">
            <img :src="product.imgUrl" :alt="product.name" class="zoom-image">
          </div>
          <!-- <div class="thumbnail-images">
            <div class="thumbnail-item" v-for="(img, idx) in product.imageList" :key="idx">
              <img :src="img" :alt="`${product.name} 图片${idx+1}`" @click="changeMainImage(img)">
            </div>
          </div> -->
        </div>

        <!-- 右侧商品信息 -->
        <div class="product-info">
          <h1 class="product-title">{{ product.name }}</h1>
          
          <!-- 标签和评分 -->
          <div class="product-meta">
            <div class="tags">
              <span class="tag hot-tag" v-if="product.tag === 'hot'">热销</span>
              <span class="tag new-tag" v-if="product.tag === 'new'">新品</span>
            </div>
            <div class="rating">
              <span class="star active" v-for="star in product.rate" :key="star">★</span>
              <span class="star" v-for="star in (5 - product.rate)" :key="star + 10">★</span>
              <span class="rating-count">({{ product.rateCount }} 评价)</span>
            </div>
          </div>

          <!-- 价格信息 -->
          <div class="price-section">
            <div class="price-label">售价：</div>
            <div class="price-values">
              <span class="current-price">¥{{ product.currentPrice.toFixed(2) }}</span>
              <span class="original-price">¥{{ product.originalPrice.toFixed(2) }}</span>
              <span class="discount" v-if="product.discount">
                {{ product.discount }}折
              </span>
            </div>
          </div>

          <!-- 商品介绍 -->
          <div class="product-description">
            <h3>商品介绍</h3>
            <p>{{ product.description }}</p>
          </div>

          <!-- 规格选择 -->
          <div class="specification" v-if="product.specs && product.specs.length">
            <h3>规格选择</h3>
            <div class="spec-options">
              <button 
                class="spec-btn" 
                v-for="(spec, idx) in product.specs" 
                :key="idx"
                :class="{ active: selectedSpec === spec.value }"
                @click="selectedSpec = spec.value"
              >
                {{ spec.label }}
              </button>
            </div>
          </div>

          <!-- 数量选择 -->
          <div class="quantity-selector">
            <h3>购买数量</h3>
            <div class="quantity-controls">
              <button class="quantity-btn minus" @click="decreaseQuantity" :disabled="quantity <= 1">
                -
              </button>
              <input 
                type="number" 
                v-model.number="quantity" 
                min="1" 
                :max="product.stock"
                class="quantity-input"
              >
              <button class="quantity-btn plus" @click="increaseQuantity" :disabled="quantity >= product.stock">
                +
              </button>
              <span class="stock-info">库存: {{ product.stock }} 件</span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="action-buttons">
            <button class="add-to-cart" @click="addToCart">
              <i>🛒</i> 加入购物车
            </button>
            <button class="buy-now">
              <i>💰</i> 立即购买
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

// 路由相关
const route = useRoute();
const router = useRouter();

// 状态管理
const isLoading = ref(true);
const product = ref(null);
const currentImage = ref('');
const quantity = ref(1);
const selectedSpec = ref('');

// 计算商品折扣
const productDiscount = computed(() => {
  if (!product.value) return null;
  return ((product.value.currentPrice / product.value.originalPrice) * 10).toFixed(1);
});

// 切换主图
const changeMainImage = (imgUrl) => {
  currentImage.value = imgUrl;
};

// 调整数量
const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--;
  }
};

const increaseQuantity = () => {
  if (quantity.value < product.value.stock) {
    quantity.value++;
  }
};

// 加入购物车
const addToCart = () => {
  const cartItem = {
    id: product.value.id,
    name: product.value.name,
    price: product.value.currentPrice,
    image: product.value.imgUrl,
    quantity: quantity.value,
    spec: selectedSpec.value || '默认规格'
  };
  
  // 这里可以替换为实际的购物车存储逻辑（如Vuex/Pinia）
  let cart = JSON.parse(localStorage.getItem('cart') || '[]');
  cart.push(cartItem);
  localStorage.setItem('cart', JSON.stringify(cart));
  
  alert(`${product.value.name} 已加入购物车，数量：${quantity.value}`);
};

// 获取商品详情数据
const fetchProductDetail = async () => {
  try {
    isLoading.value = true;
    const productId = route.params.id; // 从路由参数获取商品ID
    
    // 模拟API请求延迟
    await new Promise(resolve => setTimeout(resolve, 800));
    
    // 这里是模拟数据，实际项目中应替换为真实API请求
    const mockProducts = {
      // 农家手工腊肠（product-1001）
      'product-1001': {
        id: 'product-1001',
        name: '农家手工腊肠 500g',
        imgUrl: 'https://picsum.photos/id/292/600/600',
        imageList: [
          'https://picsum.photos/id/292/600/600',
          'https://picsum.photos/id/293/600/600',
          'https://picsum.photos/id/294/600/600'
        ],
        detailImage: 'https://picsum.photos/id/292/1200/800',
        tag: 'hot',
        rate: 4,
        rateCount: 128,
        currentPrice: 59.9,
        originalPrice: 89.0,
        stock: 120,
        description: '精选农家土猪肉，传统工艺制作，自然风干，肉质紧实，咸香可口，无添加剂，真空包装，安全卫生。',
        specs: [
          { label: '500g/袋', value: '500g' },
          { label: '1000g/袋', value: '1000g' }
        ],
        features: [
          '精选农家土猪肉制作',
          '传统工艺，自然风干',
          '无防腐剂，健康安全',
          '真空包装，锁住新鲜',
          '炒菜、蒸饭皆宜'
        ],
        parameters: [
          { name: '品牌', value: '乡味园' },
          { name: '产地', value: '广东梅州' },
          { name: '保质期', value: '180天' },
          { name: '储存方式', value: '阴凉干燥处存放，开封后冷藏' },
          { name: '配料', value: '猪肉、食用盐、白砂糖、白酒、香辛料' }
        ]
      },
      // 可添加其他商品的详情数据...
    };
    
    // 获取对应ID的商品数据
    product.value = mockProducts[productId] || null;
    
    // 初始化图片和规格
    if (product.value) {
      currentImage.value = product.value.imgUrl;
      product.value.discount = productDiscount.value;
      if (product.value.specs && product.value.specs.length) {
        selectedSpec.value = product.value.specs[0].value;
      }
    }
  } catch (error) {
    console.error('获取商品详情失败:', error);
    alert('加载商品详情失败，请稍后重试');
  } finally {
    isLoading.value = false;
  }
};

// 页面加载时获取商品详情
onMounted(() => {
  fetchProductDetail();
});
</script>

<style scoped>
/* 基础样式 */
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

.product-detail-page {
  background-color: #f9f9f9;
  padding-bottom: 50px;
}

/* 面包屑导航 */
.breadcrumbs {
  background-color: #f1f1f1;
  padding: 15px 0;
  font-size: 14px;
  color: #666;
}

.breadcrumbs a {
  color: #666;
  text-decoration: none;
}

.breadcrumbs a:hover {
  color: #1E90FF;
  text-decoration: underline;
}

.breadcrumbs span {
  color: #333;
  font-weight: 500;
}

/* 商品详情容器 */
.product-detail-container {
  padding: 30px 0;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #1E90FF;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  color: #666;
  font-size: 16px;
}

/* 商品不存在 */
.not-found {
  text-align: center;
  padding: 100px 0;
}

.not-found p {
  font-size: 18px;
  color: #666;
  margin-bottom: 20px;
}

.back-btn {
  padding: 8px 20px;
  background-color: #1E90FF;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.back-btn:hover {
  background-color: #0d84e3;
}

/* 商品详情内容 */
.product-detail-content {
  display: flex;
  gap: 40px;
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

/* 商品图片区 */
.product-images {
  flex: 0 0 50%;
}

.main-image {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 20px;
  background-color: #f9f9f9;
}

.zoom-image {
  width: 100%;
  height: auto;
  object-fit: contain;
  transition: transform 0.3s;
}

.zoom-image:hover {
  transform: scale(1.05);
}

.thumbnail-images {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding-bottom: 10px;
}

.thumbnail-item {
  width: 80px;
  height: 80px;
  border: 2px solid transparent;
  border-radius: 4px;
  cursor: pointer;
  overflow: hidden;
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-item:hover {
  border-color: #1E90FF;
}

/* 商品信息区 */
.product-info {
  flex: 0 0 calc(50% - 40px);
}

.product-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
  line-height: 1.3;
}

.product-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #eee;
}

.tags {
  display: flex;
  gap: 10px;
}

.tag {
  padding: 3px 10px;
  border-radius: 4px;
  font-size: 12px;
  color: white;
}

.hot-tag {
  background-color: #ff4d4f;
}

.new-tag {
  background-color: #52c41a;
}

.rating {
  display: flex;
  align-items: center;
}

.star {
  color: #ddd;
  font-size: 16px;
  margin-right: 2px;
}

.star.active {
  color: #faad14;
}

.rating-count {
  font-size: 14px;
  color: #666;
  margin-left: 8px;
}

/* 价格区域 */
.price-section {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #eee;
}

.price-label {
  font-size: 16px;
  color: #666;
  margin-right: 15px;
}

.price-values {
  display: flex;
  align-items: center;
  gap: 15px;
}

.current-price {
  font-size: 28px;
  color: #ff4d4f;
  font-weight: bold;
}

.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
}

.discount {
  font-size: 16px;
  color: white;
  background-color: #ff4d4f;
  padding: 2px 8px;
  border-radius: 4px;
}

/* 商品介绍 */
.product-description {
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #eee;
}

.product-description h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 10px;
  font-weight: 500;
}

.product-description p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

/* 规格选择 */
.specification {
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #eee;
}

.specification h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 10px;
  font-weight: 500;
}

.spec-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.spec-btn {
  padding: 8px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.spec-btn:hover {
  border-color: #1E90FF;
}

.spec-btn.active {
  border-color: #1E90FF;
  background-color: #f0f7ff;
  color: #1E90FF;
  font-weight: 500;
}

/* 数量选择 */
.quantity-selector {
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #eee;
}

.quantity-selector h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 10px;
  font-weight: 500;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity-btn {
  width: 36px;
  height: 36px;
  border: 1px solid #ddd;
  background-color: white;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.3s;
}

.quantity-btn:hover:not(:disabled) {
  border-color: #1E90FF;
  color: #1E90FF;
}

.quantity-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background-color: #f5f5f5;
}

.quantity-input {
  width: 60px;
  height: 36px;
  border: 1px solid #ddd;
  border-radius: 4px;
  text-align: center;
  font-size: 16px;
}

.quantity-input:focus {
  outline: none;
  border-color: #1E90FF;
}

.stock-info {
  font-size: 14px;
  color: #666;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 20px;
}

.add-to-cart, .buy-now {
  flex: 1;
  height: 50px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.add-to-cart {
  background-color: #1E90FF;
  color: white;
}

.add-to-cart:hover {
  background-color: #0d84e3;
  box-shadow: 0 4px 12px rgba(30, 144, 255, 0.3);
}

.buy-now {
  background-color: #ff4d4f;
  color: white;
}

.buy-now:hover {
  background-color: #f5222d;
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.3);
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .container {
    width: 960px;
  }
}

@media (max-width: 992px) {
  .container {
    width: 720px;
  }
  
  .product-detail-content {
    flex-direction: column;
  }
  
  .product-images, .product-info {
    flex: none;
    width: 100%;
  }
}

@media (max-width: 768px) {
  .container {
    width: 100%;
  }
  
  .product-title {
    font-size: 20px;
  }
  
  .current-price {
    font-size: 24px;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 10px;
  }
  
  .add-to-cart, .buy-now {
    width: 100%;
  }
}

@media (max-width: 576px) {
  .product-detail-content {
    padding: 15px;
  }
  
  .thumbnail-item {
    width: 60px;
    height: 60px;
  }
  
  .tab-btn {
    padding: 10px 15px;
    font-size: 14px;
  }
}
</style>