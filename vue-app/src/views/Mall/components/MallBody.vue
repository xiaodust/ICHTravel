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
                  <button class="carousel-btn-shop" @click="$router.push('/food')">立即选购</button>
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
                  <button class="carousel-btn-shop" @click="$router.push('/craft')">探索工艺</button>
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

    <!-- 2. 特色分类区域 -->
    <section class="categories-section">
      <div class="container">
        <div class="category-items">
          <a href="#" @click.prevent="$router.push('/food')" class="category-item">
            <div class="category-icon">🍽️</div>
            <div class="category-name">美味食品</div>
          </a>
          <a href="#" @click.prevent="$router.push('/craft')" class="category-item">
            <div class="category-icon">🎨</div>
            <div class="category-name">精品工艺</div>
          </a>
          <a href="#" @click.prevent="$router.push('/food')" class="category-item" >
            <div class="category-icon">🎁</div>
            <div class="category-name">礼品套装</div>
          </a>
          <a href="#" @click.prevent="$router.push('/craft')" class="category-item">
            <div class="category-icon">🔥</div>
            <div class="category-name">热销榜单</div>
          </a>
        </div>
      </div>
    </section>

    <!-- 3. 美味食品专区 -->
    <section class="product-section food-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">
            <i class="title-icon">🍽️</i> 美味食品专区
          </h2>
          <a href="#" class="more-link" @click="$router.push('/food')">查看全部 <span>→</span></a>
        </div>

        <!-- 横向滚动容器 -->
        <div class="horizontal-scroll-container">
          <div class="scroll-content">
            <div class="product-grid">
              <div class="product-card" v-for="(item, index) in foodProducts" :key="index">
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
          </div>
          
          <!-- 滚动控制按钮 -->
          <button class="scroll-btn left-btn" @click="scrollLeft('food')" aria-label="向左滚动">
            <span class="btn-icon">←</span>
          </button>
          <button class="scroll-btn right-btn" @click="scrollRight('food')" aria-label="向右滚动">
            <span class="btn-icon">→</span>
          </button>
        </div>
      </div>
    </section>

    <!-- 4. 精品工艺专区 -->
    <section class="product-section craft-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">
            <i class="title-icon">🎨</i> 精品工艺专区
          </h2>
          <a href="#" class="more-link" @click="$router.push('/craft')">查看全部 <span>→</span></a>
        </div>

        <!-- 横向滚动容器 -->
        <div class="horizontal-scroll-container">
          <div class="scroll-content">
            <div class="product-grid">
              <div class="product-card" v-for="(item, index) in craftProducts" :key="index">
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
          </div>
          
          <!-- 滚动控制按钮 -->
          <button class="scroll-btn left-btn" @click="scrollLeft('craft')" aria-label="向左滚动">
            <span class="btn-icon">←</span>
          </button>
          <button class="scroll-btn right-btn" @click="scrollRight('craft')" aria-label="向右滚动">
            <span class="btn-icon">→</span>
          </button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue';

const currentSlide = ref(0);
const slideCount = 3;
let carouselInterval = null;
const scrollContainers = ref({
  food: null,
  craft: null
});

// 食品商品数据
const foodProducts = ref([
  {
    name: '农家手工腊肠 500g',
    imgUrl: 'https://picsum.photos/id/292/300/300',
    tag: 'hot',
    rate: 4,
    rateCount: 128,
    currentPrice: 59.9,
    originalPrice: 89.0
  },
  {
    name: '东北有机五常大米 5kg',
    imgUrl: 'https://picsum.photos/id/139/300/300',
    tag: '',
    rate: 5,
    rateCount: 256,
    currentPrice: 89.0,
    originalPrice: 109.0
  },
  {
    name: '秦岭土蜂蜜 500g',
    imgUrl: 'https://picsum.photos/id/175/300/300',
    tag: 'new',
    rate: 4,
    rateCount: 64,
    currentPrice: 129.0,
    originalPrice: 159.0
  },
  {
    name: '无添加手工曲奇饼干 200g',
    imgUrl: 'https://picsum.photos/id/225/300/300',
    tag: '',
    rate: 4,
    rateCount: 98,
    currentPrice: 39.9,
    originalPrice: 59.0
  },
  {
    name: '新疆和田骏枣 1kg',
    imgUrl: 'https://picsum.photos/id/132/300/300',
    tag: 'hot',
    rate: 5,
    rateCount: 215,
    currentPrice: 69.0,
    originalPrice: 89.0
  }
]);

// 工艺品数据
const craftProducts = ref([
  {
    name: '景德镇手工陶瓷花瓶',
    imgUrl: 'https://picsum.photos/id/118/300/300',
    tag: 'hot',
    rate: 4,
    rateCount: 156,
    currentPrice: 299.0,
    originalPrice: 399.0
  },
  {
    name: '海南黄花梨手串 18mm',
    imgUrl: 'https://picsum.photos/id/177/300/300',
    tag: '',
    rate: 5,
    rateCount: 89,
    currentPrice: 599.0,
    originalPrice: 799.0
  },
  {
    name: '苏绣手帕 牡丹图',
    imgUrl: 'https://picsum.photos/id/218/300/300',
    tag: 'new',
    rate: 5,
    rateCount: 42,
    currentPrice: 159.0,
    originalPrice: 199.0
  },
  {
    name: '宜兴紫砂壶 西施壶',
    imgUrl: 'https://picsum.photos/id/30/300/300',
    tag: '',
    rate: 4,
    rateCount: 76,
    currentPrice: 699.0,
    originalPrice: 899.0
  },
  {
    name: '手工木雕摆件 松鹤延年',
    imgUrl: 'https://picsum.photos/id/129/300/300',
    tag: 'hot',
    rate: 5,
    rateCount: 108,
    currentPrice: 899.0,
    originalPrice: 1299.0
  }
]);

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

// 横向滚动控制
const scrollLeft = (type) => {
  if (scrollContainers.value[type]) {
    scrollContainers.value[type].scrollBy({ left: -300, behavior: 'smooth' });
  }
};

const scrollRight = (type) => {
  if (scrollContainers.value[type]) {
    scrollContainers.value[type].scrollBy({ left: 300, behavior: 'smooth' });
  }
};

// 加入购物车
const addToCart = (product) => {
  console.log('加入购物车:', product);
  alert(`${product.name} 已加入购物车`);
};

onMounted(() => {
  startCarousel();
  
  // 获取滚动容器引用
  nextTick(() => {
    scrollContainers.value.food = document.querySelector('.food-section .scroll-content');
    scrollContainers.value.craft = document.querySelector('.craft-section .scroll-content');
  });
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

/* 分类区域 */
.categories-section {
  padding: 40px 0;
  margin: 0 auto 60px;
  max-width: 1200px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.03);
}

.category-items {
  display: flex;
  justify-content: center;
  gap: 60px;
  flex-wrap: wrap;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 25px 20px;
  border-radius: 12px;
  transition: all 0.3s;
  width: 140px;
  text-align: center;
}

.category-item:hover {
  background-color: #f0f7ff;
  transform: translateY(-8px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.07);
}

.category-icon {
  font-size: 50px;
  margin-bottom: 18px;
}

.category-name {
  font-size: 18px;
  color: #333;
  font-weight: 500;
}

/* 商品专区通用样式 */
.product-section {
  padding: 50px 0;
  margin: 0 auto 40px;
  max-width: 1200px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.03);
  position: relative;
}

/* 区分两个专区的背景色 */
.food-section {
  background-color: #fff8f5;
}

.craft-section {
  background-color: #f5f8ff;
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

.more-link {
  color: #1E90FF;
  font-size: 16px;
  display: flex;
  align-items: center;
  transition: all 0.3s;
}

.more-link span {
  margin-left: 5px;
  transition: transform 0.3s;
}

.more-link:hover {
  color: #0d84e3;
}

.more-link:hover span {
  transform: translateX(3px);
}

/* 横向滚动容器样式 */
.horizontal-scroll-container {
  position: relative;
  overflow: hidden;
  padding: 10px 0;
}

.scroll-content {
  display: flex;
  overflow-x: auto;
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
  padding: 5px 0;
  scroll-behavior: smooth;
}

.scroll-content::-webkit-scrollbar {
  display: none; /* Chrome, Safari and Opera */
}

/* 滚动按钮样式 */
.scroll-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.9);
  border: none;
  color: #333;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  z-index: 5;
}

.scroll-btn:hover {
  background-color: white;
  transform: translateY(-50%) scale(1.1);
  box-shadow: 0 3px 15px rgba(0, 0, 0, 0.15);
}

.left-btn {
  left: -15px;
}

.right-btn {
  right: -15px;
}

/* 商品网格 - 横向排列 */
.product-grid {
  display: flex;
  gap: 25px;
  width: max-content; /* 让内容撑开容器宽度 */
}

/* 商品卡片 */
.product-card {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  position: relative;
  min-width: 280px; /* 固定卡片宽度 */
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
  
  .category-items {
    gap: 40px;
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
  
  .product-card {
    min-width: 220px;
  }
  
  .category-items {
    gap: 25px;
  }
  
  .category-item {
    width: 120px;
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
  
  .carousel-btn, .scroll-btn {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }
  
  .product-card {
    min-width: 160px;
  }
  
  .section-title {
    font-size: 24px;
  }
  
  .category-icon {
    font-size: 40px;
  }
}
</style>