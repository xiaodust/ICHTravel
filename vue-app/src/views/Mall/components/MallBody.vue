<template>
  <div class="product-page">
    <!-- 1. 商品图片轮播图区域 -->
    <section class="carousel-section">
      <div class="container">
        <div class="carousel-container">
          <div class="carousel-wrapper" @mouseenter="stopCarousel" @mouseleave="startCarousel">
            <div 
              class="carousel-slides" 
              :style="{ transform: `translateX(-${currentSlide * 100}%)`, transition: 'transform 0.5s ease' }"
            >
              <div class="carousel-slide">
                <img 
                  src="https://picsum.photos/id/292/1200/400" 
                  alt="优选美食专场" 
                  class="carousel-img"
                >
                <div class="carousel-caption">
                  <h3>优选美食专场</h3>
                  <p>源自大自然的馈赠，品味地道风味</p>
                  <!-- <button class="carousel-btn-shop" @click="$router.push('/food')">立即选购</button> -->
                </div>
              </div>
              <div class="carousel-slide">
                <img 
                  src="https://picsum.photos/id/118/1200/400" 
                  alt="手工工艺品特惠" 
                  class="carousel-img"
                >
                <div class="carousel-caption">
                  <h3>手工工艺品特惠</h3>
                  <p>匠心之作，传承文化之美</p>
                  <!-- <button class="carousel-btn-shop" @click="$router.push('/craft')">探索工艺</button> -->
                </div>
              </div>
              <div class="carousel-slide">
                <img 
                  src="https://picsum.photos/id/431/1200/400" 
                  alt="新品上市专区" 
                  class="carousel-img"
                >
                <div class="carousel-caption">
                  <h3>新品上市</h3>
                  <p>全新体验，抢先拥有</p>
                </div>
              </div>
            </div>

            <button class="carousel-btn prev-btn" @click="prevSlide" aria-label="上一页">
              <span class="btn-icon">←</span>
            </button>
            <button class="carousel-btn next-btn" @click="nextSlide" aria-label="下一页">
              <span class="btn-icon">→</span>
            </button>

            <div class="carousel-indicators">
              <span 
                v-for="(item, index) in slideCount" 
                :key="index"
                class="indicator-dot"
                :class="{ active: index === currentSlide }"
                @click="goToSlide(index)"
                :aria-label="`切换到幻灯片 ${index + 1}`"
              ></span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 2. 全部商品专区（分页查询） -->
    <section class="product-section all-products-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">
            <i class="title-icon">📦</i> 全部商品
          </h2>
          <div class="sort-container">
            <select class="sort-select" v-model="sortType" @change="handleSort">
              <option value="default">默认排序</option>
              <option value="price-asc">价格从低到高</option>
              <option value="price-desc">价格从高到低</option>
              <option value="hot">热销优先</option>
              <option value="new">新品优先</option>
            </select>
          </div>
        </div>

        <!-- 商品网格布局（分页展示） -->
        <div class="product-grid">
          <div class="product-card" v-for="(item, index) in currentPageProducts" :key="index">
            <div class="card-img-container">
              <img 
                :src="item.imgUrl" 
                :alt="item.name" 
                class="product-img"
              >
              <div class="card-tag hot-tag" v-if="item.tag === 'hot'">热销</div>
              <div class="card-tag new-tag" v-if="item.tag === 'new'">新品</div>
            </div>
            <div class="card-content">
              <h3 class="product-name">{{ item.name }}</h3>
              <div class="product-rating">
                <span class="star active" v-for="star in item.rate" :key="star">★</span>
                <span class="star" v-for="star in (5 - item.rate)" :key="star + 10">★</span>
                <span class="rating-count">({{ item.rateCount }})</span>
              </div>
              <div class="product-price">
                <span class="current-price">¥{{ item.currentPrice }}</span>
                <span class="original-price">¥{{ item.originalPrice }}</span>
              </div>
              <button class="add-cart-btn" @click="addToCart(item)">加入购物车</button>
            </div>
          </div>
        </div>

        <!-- 加载状态提示 -->
        <div class="loading-container" v-if="isLoading">
          <div class="spinner"></div>
          <p class="loading-text">加载中...</p>
        </div>

        <!-- 分页控件 -->
        <div class="pagination-container" v-if="!isLoading && totalProducts > 0">
          <div class="pagination-info">
            共 {{ totalProducts }} 件商品，当前第 {{ currentPage }} / {{ totalPages }} 页
          </div>
          <div class="pagination-controls">
            <button 
              class="page-btn prev-page" 
              @click="changePage(currentPage - 1)"
              :disabled="currentPage === 1 || isLoading"
            >
              上一页
            </button>
            
            <div class="page-numbers">
              <button 
                class="page-number" 
                v-for="pageNum in visiblePageNumbers" 
                :key="pageNum"
                @click="changePage(pageNum)"
                :class="{ active: pageNum === currentPage }"
              >
                {{ pageNum }}
              </button>
              
              <span class="page-ellipsis" v-if="showFirstEllipsis">...</span>
              <span class="page-ellipsis" v-if="showLastEllipsis">...</span>
            </div>
            
            <button 
              class="page-btn next-page" 
              @click="changePage(currentPage + 1)"
              :disabled="currentPage === totalPages || isLoading"
            >
              下一页
            </button>
          </div>
        </div>

        <!-- 无商品提示 -->
        <div class="no-products-container" v-if="!isLoading && totalProducts === 0">
          <p class="no-products-text">暂无商品数据</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';

// 轮播图相关
const currentSlide = ref(0);
const slideCount = 3;
let carouselInterval = null;

// 分页相关配置 - 改为每页9个商品
const currentPage = ref(1);
const pageSize = ref(9); // 核心修改：每页显示9个商品
const totalProducts = ref(0);
const totalPages = ref(0);
const isLoading = ref(false);
const sortType = ref('default');

// 商品数据
const allProducts = ref([]);
const foodProducts = ref([
  {
    name: '农家手工腊肠 500g',
    imgUrl: 'https://picsum.photos/id/292/300/300',
    tag: 'hot',
    rate: 4,
    rateCount: 128,
    currentPrice: 59.9,
    originalPrice: 89.0,
    type: 'food'
  },
  {
    name: '东北有机五常大米 5kg',
    imgUrl: 'https://picsum.photos/id/139/300/300',
    tag: '',
    rate: 5,
    rateCount: 256,
    currentPrice: 89.0,
    originalPrice: 109.0,
    type: 'food'
  },
  {
    name: '秦岭土蜂蜜 500g',
    imgUrl: 'https://picsum.photos/id/175/300/300',
    tag: 'new',
    rate: 4,
    rateCount: 64,
    currentPrice: 129.0,
    originalPrice: 159.0,
    type: 'food'
  },
  {
    name: '无添加手工曲奇饼干 200g',
    imgUrl: 'https://picsum.photos/id/225/300/300',
    tag: '',
    rate: 4,
    rateCount: 98,
    currentPrice: 39.9,
    originalPrice: 59.0,
    type: 'food'
  },
  {
    name: '新疆和田骏枣 1kg',
    imgUrl: 'https://picsum.photos/id/132/300/300',
    tag: 'hot',
    rate: 5,
    rateCount: 215,
    currentPrice: 69.0,
    originalPrice: 89.0,
    type: 'food'
  }
]);

const craftProducts = ref([
  {
    name: '景德镇手工陶瓷花瓶',
    imgUrl: 'https://picsum.photos/id/118/300/300',
    tag: 'hot',
    rate: 4,
    rateCount: 156,
    currentPrice: 299.0,
    originalPrice: 399.0,
    type: 'craft'
  },
  {
    name: '海南黄花梨手串 18mm',
    imgUrl: 'https://picsum.photos/id/177/300/300',
    tag: '',
    rate: 5,
    rateCount: 89,
    currentPrice: 599.0,
    originalPrice: 799.0,
    type: 'craft'
  },
  {
    name: '苏绣手帕 牡丹图',
    imgUrl: 'https://picsum.photos/id/218/300/300',
    tag: 'new',
    rate: 5,
    rateCount: 42,
    currentPrice: 159.0,
    originalPrice: 199.0,
    type: 'craft'
  },
  {
    name: '宜兴紫砂壶 西施壶',
    imgUrl: 'https://picsum.photos/id/30/300/300',
    tag: '',
    rate: 4,
    rateCount: 76,
    currentPrice: 699.0,
    originalPrice: 899.0,
    type: 'craft'
  },
  {
    name: '手工木雕摆件 松鹤延年',
    imgUrl: 'https://picsum.photos/id/129/300/300',
    tag: 'hot',
    rate: 5,
    rateCount: 108,
    currentPrice: 899.0,
    originalPrice: 1299.0,
    type: 'craft'
  }
]);

// 生成随机商品数据（模拟后端数据）
const generateRandomProducts = (count) => {
  const productTypes = ['food', 'craft'];
  const tags = ['', 'hot', 'new'];
  const foodNames = [
    '手工牛肉干 200g', '云南过桥米线 5包装', '古法红糖 300g', 
    '手工酸辣粉 6桶装', '新疆葡萄干 500g', '农家土鸡蛋 30枚',
    '手工挂面 1kg', '东北黑木耳 250g', '野生香菇 100g', '蜂蜜柚子茶 500ml'
  ];
  const craftNames = [
    '手工编织竹篮', '青花瓷茶杯套装', '木雕茶盘', '手工蜡染布',
    '紫砂茶叶罐', '手工银饰吊坠', '刺绣钱包', '竹制书签',
    '手工纸灯笼', '陶瓷香薰炉'
  ];

  const randomProducts = [];
  for (let i = 0; i < count; i++) {
    const type = productTypes[Math.floor(Math.random() * productTypes.length)];
    const tag = tags[Math.floor(Math.random() * tags.length)];
    const rate = Math.floor(Math.random() * 3) + 3; // 3-5星
    const rateCount = Math.floor(Math.random() * 300) + 20;
    const id = Math.floor(Math.random() * 500); // 随机图片ID
    let name, currentPrice, originalPrice;

    if (type === 'food') {
      name = foodNames[Math.floor(Math.random() * foodNames.length)];
      currentPrice = (Math.random() * 100 + 20).toFixed(1);
      originalPrice = (Math.random() * 50 + parseFloat(currentPrice)).toFixed(1);
    } else {
      name = craftNames[Math.floor(Math.random() * craftNames.length)];
      currentPrice = (Math.random() * 800 + 100).toFixed(1);
      originalPrice = (Math.random() * 300 + parseFloat(currentPrice)).toFixed(1);
    }

    randomProducts.push({
      name,
      imgUrl: `https://picsum.photos/id/${id}/300/300`,
      tag,
      rate,
      rateCount,
      currentPrice: parseFloat(currentPrice),
      originalPrice: parseFloat(originalPrice),
      type
    });
  }
  return randomProducts;
};

// 初始化商品数据
const initProducts = () => {
  // 合并初始商品并添加随机数据，模拟有多个分页
  const initialProducts = [...foodProducts.value, ...craftProducts.value];
  const extraProducts = generateRandomProducts(85); // 额外生成85个商品，总共90个
  allProducts.value = [...initialProducts, ...extraProducts];
  totalProducts.value = allProducts.value.length;
  totalPages.value = Math.ceil(totalProducts.value / pageSize.value); // 基于每页9个计算总页数
};

// 获取当前页商品（核心修改：基于每页9个计算）
const currentPageProducts = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  return allProducts.value.slice(startIndex, endIndex);
});

// 处理分页显示逻辑（最多显示5个页码）
const visiblePageNumbers = computed(() => {
  const pages = [];
  const maxVisible = 5;
  
  // 总页数小于等于最大显示数，全部显示
  if (totalPages.value <= maxVisible) {
    for (let i = 1; i <= totalPages.value; i++) {
      pages.push(i);
    }
    return pages;
  }
  
  // 总页数大于最大显示数，显示当前页附近的页码
  if (currentPage.value <= 3) {
    return [1, 2, 3, 4, 5];
  }
  
  if (currentPage.value >= totalPages.value - 2) {
    return [
      totalPages.value - 4,
      totalPages.value - 3,
      totalPages.value - 2,
      totalPages.value - 1,
      totalPages.value
    ];
  }
  
  return [
    currentPage.value - 2,
    currentPage.value - 1,
    currentPage.value,
    currentPage.value + 1,
    currentPage.value + 2
  ];
});

// 是否显示省略号
const showFirstEllipsis = computed(() => {
  return totalPages.value > 5 && currentPage.value > 3;
});

const showLastEllipsis = computed(() => {
  return totalPages.value > 5 && currentPage.value < totalPages.value - 2;
});

// 切换页码
const changePage = (pageNum) => {
  if (pageNum < 1 || pageNum > totalPages.value || pageNum === currentPage.value || isLoading.value) {
    return;
  }
  
  isLoading.value = true;
  // 模拟接口请求延迟
  setTimeout(() => {
    currentPage.value = pageNum;
    // 滚动到商品区域顶部
    document.querySelector('.product-grid').scrollIntoView({ behavior: 'smooth' });
    isLoading.value = false;
  }, 500);
};

// 处理排序
const handleSort = () => {
  isLoading.value = true;
  // 模拟排序延迟
  setTimeout(() => {
    switch (sortType.value) {
      case 'price-asc':
        allProducts.value.sort((a, b) => a.currentPrice - b.currentPrice);
        break;
      case 'price-desc':
        allProducts.value.sort((a, b) => b.currentPrice - a.currentPrice);
        break;
      case 'hot':
        allProducts.value.sort((a, b) => b.rateCount - a.rateCount);
        break;
      case 'new':
        allProducts.value.sort((a, b) => (a.tag === 'new' ? -1 : b.tag === 'new' ? 1 : 0));
        break;
      default:
        // 恢复默认排序
        initProducts(); // 重新初始化以恢复原始顺序
        break;
    }
    currentPage.value = 1; // 排序后回到第一页
    isLoading.value = false;
  }, 500);
};

// 加入购物车
const addToCart = (product) => {
  console.log('加入购物车:', product);
  alert(`${product.name} 已加入购物车`);
};

// 轮播图控制
const startCarousel = () => {
  carouselInterval = setInterval(() => {
    currentSlide.value = (currentSlide.value + 1) % slideCount;
  }, 5000);
};

const stopCarousel = () => {
  if (carouselInterval) {
    clearInterval(carouselInterval);
  }
};

const prevSlide = () => {
  currentSlide.value = (currentSlide.value - 1 + slideCount) % slideCount;
};

const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % slideCount;
};

const goToSlide = (index) => {
  currentSlide.value = index;
};

onMounted(() => {
  startCarousel();
  initProducts();
});

onUnmounted(() => {
  stopCarousel();
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

a {
  text-decoration: none;
  color: inherit;
}

/* 轮播图区域 */
.carousel-section {
  margin: 20px auto 50px;
  max-width: 1400px;
  overflow: hidden;
  position: relative;
}

.carousel-container {
  position: relative;
  height: 450px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.carousel-wrapper {
  height: 100%;
  position: relative;
}

.carousel-slides {
  display: flex;
  height: 100%;
}

.carousel-slide {
  min-width: 100%;
  position: relative;
}

.carousel-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-caption {
  position: absolute;
  left: 8%;
  top: 50%;
  transform: translateY(-50%);
  color: white;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
  max-width: 500px;
}

.carousel-caption h3 {
  font-size: 42px;
  margin-bottom: 18px;
  animation: fadeInUp 0.8s;
}

.carousel-caption p {
  font-size: 20px;
  margin-bottom: 24px;
  animation: fadeInUp 1s;
}

.carousel-btn-shop {
  background-color: #1E90FF;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  animation: fadeInUp 1.2s;
}

.carousel-btn-shop:hover {
  background-color: #0d84e3;
  transform: translateY(-3px);
  box-shadow: 0 4px 15px rgba(30, 144, 255, 0.4);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.9);
  border: none;
  color: #333;
  font-size: 22px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  z-index: 10;
}

.carousel-btn:hover {
  background-color: white;
  transform: translateY(-50%) scale(1.1);
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.2);
}

.prev-btn {
  left: 25px;
}

.next-btn {
  right: 25px;
}

.carousel-indicators {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
}

.indicator-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s;
}

.indicator-dot.active {
  background-color: white;
  width: 36px;
  border-radius: 6px;
}

/* 全部商品专区样式 */
.product-section {
  padding: 50px 0;
  margin: 0 auto 40px;
  max-width: 1200px;
  background-color: #f9f9f9;
  border-radius: 12px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.03);
  position: relative;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 35px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
}

.section-header::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 80px;
  height: 3px;
  background-color: #1E90FF;
}

.section-title {
  font-size: 28px;
  color: #333;
  display: flex;
  align-items: center;
}

.title-icon {
  margin-right: 12px;
  font-size: 32px;
}

.sort-container {
  margin-left: auto;
}

.sort-select {
  padding: 8px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background-color: white;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: all 0.3s;
}

.sort-select:focus {
  outline: none;
  border-color: #1E90FF;
  box-shadow: 0 0 0 2px rgba(30, 144, 255, 0.1);
}

/* 商品网格布局 - 9个商品更适合3列布局 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* 3列布局更适合9个商品 */
  gap: 25px;
  margin-bottom: 40px;
}

/* 商品卡片 */
.product-card {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  position: relative;
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 20px rgba(0, 0, 0, 0.12);
}

.product-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 50%;
  height: 100%;
  background: linear-gradient(
    to right,
    rgba(255, 255, 255, 0) 0%,
    rgba(255, 255, 255, 0.3) 100%
  );
  transform: skewX(-25deg);
  transition: all 0.6s ease;
  z-index: 1;
}

.product-card:hover::after {
  left: 125%;
}

.card-img-container {
  position: relative;
  height: 240px;
  overflow: hidden;
}

.product-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.product-card:hover .product-img {
  transform: scale(1.08);
}

.card-tag {
  position: absolute;
  top: 15px;
  left: 15px;
  padding: 4px 12px;
  font-size: 13px;
  color: #fff;
  border-radius: 6px;
  z-index: 2;
}

.hot-tag {
  background-color: #ff4d4f;
}

.new-tag {
  background-color: #52c41a;
}

/* 商品卡片内容 */
.card-content {
  padding: 20px;
  position: relative;
  z-index: 2;
  background-color: white;
}

.product-name {
  font-size: 18px;
  color: #333;
  margin-bottom: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color 0.3s;
}

.product-card:hover .product-name {
  color: #1E90FF;
}

/* 评分样式 */
.product-rating {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.star {
  color: #ddd;
  font-size: 15px;
  margin-right: 2px;
}

.star.active {
  color: #faad14;
}

.rating-count {
  font-size: 13px;
  color: #999;
  margin-left: 5px;
}

/* 价格样式 */
.product-price {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.current-price {
  color: #ff4d4f;
  font-size: 20px;
  font-weight: bold;
  margin-right: 10px;
}

.original-price {
  color: #999;
  font-size: 15px;
  text-decoration: line-through;
}

/* 加入购物车按钮 */
.add-cart-btn {
  width: 100%;
  height: 40px;
  background-color: #1E90FF;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-cart-btn:hover {
  background-color: #0d84e3;
  box-shadow: 0 3px 10px rgba(30, 144, 255, 0.3);
}

.add-cart-btn::after {
  content: '🛒';
  margin-left: 5px;
  transition: transform 0.3s;
}

.add-cart-btn:hover::after {
  transform: rotate(15deg);
}

/* 分页控件样式 */
.pagination-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.pagination-info {
  color: #666;
  font-size: 14px;
  margin-bottom: 15px;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-btn {
  padding: 6px 15px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 6px;
  color: #333;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.page-btn:hover:not(:disabled) {
  background-color: #f0f7ff;
  border-color: #1E90FF;
  color: #1E90FF;
}

.page-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background-color: #f5f5f5;
}

.page-numbers {
  display: flex;
  align-items: center;
  gap: 5px;
}

.page-number {
  width: 36px;
  height: 36px;
  border-radius: 6px;
  border: 1px solid #ddd;
  background-color: white;
  color: #333;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.page-number:hover:not(.active) {
  border-color: #1E90FF;
  color: #1E90FF;
}

.page-number.active {
  background-color: #1E90FF;
  color: white;
  border-color: #1E90FF;
}

.page-ellipsis {
  color: #999;
  padding: 0 5px;
}

/* 加载状态样式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
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

/* 无商品提示 */
.no-products-container {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}

.no-products-text {
  color: #999;
  font-size: 16px;
  padding: 15px 30px;
  background-color: #f5f5f5;
  border-radius: 8px;
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
  
  .carousel-container {
    height: 400px;
  }
  
  .product-grid {
    grid-template-columns: repeat(2, 1fr); /* 中等屏幕2列 */
  }
}

@media (max-width: 768px) {
  .container {
    width: 100%;
  }
  
  .carousel-container {
    height: 320px;
  }
  
  .carousel-caption h3 {
    font-size: 32px;
  }
  
  .carousel-caption p {
    font-size: 16px;
  }
  
  .section-title {
    font-size: 24px;
  }
  
  .page-number {
    width: 32px;
    height: 32px;
  }
}

@media (max-width: 576px) {
  .carousel-container {
    height: 250px;
  }
  
  .carousel-caption {
    left: 5%;
  }
  
  .carousel-caption h3 {
    font-size: 24px;
  }
  
  .carousel-btn {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }
  
  .product-grid {
    grid-template-columns: 1fr; /* 小屏幕1列 */
  }
  
  .card-img-container {
    height: 180px;
  }
  
  .product-name {
    font-size: 16px;
  }
  
  .current-price {
    font-size: 18px;
  }
  
  .pagination-controls {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>