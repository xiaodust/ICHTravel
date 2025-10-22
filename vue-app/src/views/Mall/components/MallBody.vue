  <template>
  <div class="product-page">
    <!-- 引入导航栏组件 -->
    <!-- <MallHead /> -->

    <!-- 1. 商品图片轮播图区域 -->
    <section class="carousel-section">
      <div class="container">
        <div class="carousel-container">
          <!-- 轮播图主体 -->
          <div class="carousel-wrapper" @mouseenter="stopCarousel" @mouseleave="startCarousel">
            <div 
              class="carousel-slides" 
              :style="{ transform: `translateX(-${currentSlide * 100}%)`, transition: 'transform 0.5s ease' }"
            >
              <!-- 轮播图1 -->
              <div class="carousel-slide">
                <img 
                  src="https://picsum.photos/id/292/1200/400" 
                  alt="优选美食专场" 
                  class="carousel-img"
                >
              </div>
              <!-- 轮播图2 -->
              <div class="carousel-slide">
                <img 
                  src="https://picsum.photos/id/118/1200/400" 
                  alt="手工工艺品特惠" 
                  class="carousel-img"
                >
              </div>
              <!-- 轮播图3 -->
              <div class="carousel-slide">
                <img 
                  src="https://picsum.photos/id/431/1200/400" 
                  alt="新品上市专区" 
                  class="carousel-img"
                >
              </div>
            </div>

            <!-- 轮播图左右按钮 -->
            <button class="carousel-btn prev-btn" @click="prevSlide">
              <span class="btn-icon">←</span>
            </button>
            <button class="carousel-btn next-btn" @click="nextSlide">
              <span class="btn-icon">→</span>
            </button>

            <!-- 轮播图指示器 -->
            <div class="carousel-indicators">
              <span 
                v-for="(item, index) in slideCount" 
                :key="index"
                class="indicator-dot"
                :class="{ active: index === currentSlide }"
                @click="goToSlide(index)"
              ></span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 2. 美味食品专区 -->
    <section class="product-section food-section">
      <div class="container">
        <!-- 专区标题 -->
        <div class="section-header">
          <h2 class="section-title">
            <i class="title-icon">🍽️</i> 美味食品专区
          </h2>
          <a href="#" class="more-link" @click="$router.push('/food')">查看全部 →</a>
        </div>

        <!-- 商品列表（网格布局） -->
        <div class="product-grid">
          <!-- 商品卡片1 -->
          <div class="product-card">
            <div class="card-img-container">
              <img 
                src="https://picsum.photos/id/292/300/300" 
                alt="手工腊肠" 
                class="product-img"
              >
              <div class="card-tag hot-tag">热销</div>
            </div>
            <div class="card-content">
              <h3 class="product-name">农家手工腊肠 500g</h3>
              <div class="product-rating">
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star">★</span>
                <span class="rating-count">(128)</span>
              </div>
              <div class="product-price">
                <span class="current-price">¥59.9</span>
                <span class="original-price">¥89.0</span>
              </div>
              <button class="add-cart-btn" >加入购物车</button>
            </div>
          </div>

          <!-- 商品卡片2 -->
          <div class="product-card">
            <div class="card-img-container">
              <img 
                src="https://picsum.photos/id/139/300/300" 
                alt="有机大米" 
                class="product-img"
              >
            </div>
            <div class="card-content">
              <h3 class="product-name">东北有机五常大米 5kg</h3>
              <div class="product-rating">
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="rating-count">(256)</span>
              </div>
              <div class="product-price">
                <span class="current-price">¥89.0</span>
                <span class="original-price">¥109.0</span>
              </div>
              <button class="add-cart-btn">加入购物车</button>
            </div>
          </div>

          <!-- 商品卡片3 -->
          <div class="product-card">
            <div class="card-img-container">
              <img 
                src="https://picsum.photos/id/175/300/300" 
                alt="手工蜂蜜" 
                class="product-img"
              >
              <div class="card-tag new-tag">新品</div>
            </div>
            <div class="card-content">
              <h3 class="product-name">秦岭土蜂蜜 500g</h3>
              <div class="product-rating">
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star">★</span>
                <span class="rating-count">(64)</span>
              </div>
              <div class="product-price">
                <span class="current-price">¥129.0</span>
                <span class="original-price">¥159.0</span>
              </div>
              <button class="add-cart-btn">加入购物车</button>
            </div>
          </div>

          <!-- 商品卡片4 -->
          <div class="product-card">
            <div class="card-img-container">
              <img 
                src="https://picsum.photos/id/225/300/300" 
                alt="手工饼干" 
                class="product-img"
              >
            </div>
            <div class="card-content">
              <h3 class="product-name">无添加手工曲奇饼干 200g</h3>
              <div class="product-rating">
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star">★</span>
                <span class="rating-count">(98)</span>
              </div>
              <div class="product-price">
                <span class="current-price">¥39.9</span>
                <span class="original-price">¥59.0</span>
              </div>
              <button class="add-cart-btn">加入购物车</button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 3. 精品工艺专区 -->
    <section class="product-section craft-section">
      <div class="container">
        <!-- 专区标题 -->
        <div class="section-header">
          <h2 class="section-title">
            <i class="title-icon">🎨</i> 精品工艺专区
          </h2>
          <a href="#" class="more-link" @click="$router.push('/craft')">查看全部 →</a>
        </div>

        <!-- 商品列表（网格布局） -->
        <div class="product-grid">
          <!-- 商品卡片1 -->
          <div class="product-card">
            <div class="card-img-container">
              <img 
                src="https://picsum.photos/id/118/300/300" 
                alt="陶瓷花瓶" 
                class="product-img"
              >
              <div class="card-tag hot-tag">热销</div>
            </div>
            <div class="card-content">
              <h3 class="product-name">景德镇手工陶瓷花瓶</h3>
              <div class="product-rating">
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star">★</span>
                <span class="rating-count">(156)</span>
              </div>
              <div class="product-price">
                <span class="current-price">¥299.0</span>
                <span class="original-price">¥399.0</span>
              </div>
              <button class="add-cart-btn">加入购物车</button>
            </div>
          </div>

          <!-- 商品卡片2 -->
          <div class="product-card">
            <div class="card-img-container">
              <img 
                src="https://picsum.photos/id/177/300/300" 
                alt="木质手串" 
                class="product-img"
              >
            </div>
            <div class="card-content">
              <h3 class="product-name">海南黄花梨手串 18mm</h3>
              <div class="product-rating">
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="rating-count">(89)</span>
              </div>
              <div class="product-price">
                <span class="current-price">¥599.0</span>
                <span class="original-price">¥799.0</span>
              </div>
              <button class="add-cart-btn">加入购物车</button>
            </div>
          </div>

          <!-- 商品卡片3 -->
          <div class="product-card">
            <div class="card-img-container">
              <img 
                src="https://picsum.photos/id/218/300/300" 
                alt="手工刺绣" 
                class="product-img"
              >
              <div class="card-tag new-tag">新品</div>
            </div>
            <div class="card-content">
              <h3 class="product-name">苏绣手工刺绣装饰画</h3>
              <div class="product-rating">
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star">★</span>
                <span class="rating-count">(45)</span>
              </div>
              <div class="product-price">
                <span class="current-price">¥899.0</span>
                <span class="original-price">¥1099.0</span>
              </div>
              <button class="add-cart-btn">加入购物车</button>
            </div>
          </div>

          <!-- 商品卡片4 -->
          <div class="product-card">
            <div class="card-img-container">
              <img 
                src="https://picsum.photos/id/325/300/300" 
                alt="手工茶具" 
                class="product-img"
              >
            </div>
            <div class="card-content">
              <h3 class="product-name">紫砂手工功夫茶具套装</h3>
              <div class="product-rating">
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star active">★</span>
                <span class="star">★</span>
                <span class="rating-count">(112)</span>
              </div>
              <div class="product-price">
                <span class="current-price">¥699.0</span>
                <span class="original-price">¥899.0</span>
              </div>
              <button class="add-cart-btn">加入购物车</button>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
// 导入导航栏组件（请根据实际文件路径调整）
// import MallHead from './components/MallHead.vue';

// 轮播图相关状态与方法
const currentSlide = ref(0); // 当前轮播图索引
const slideCount = ref(3); // 轮播图总数
const carouselTimer = ref(null); // 轮播定时器

// 上一张轮播图
const prevSlide = () => {
  currentSlide.value = currentSlide.value === 0 ? slideCount.value - 1 : currentSlide.value - 1;
};

// 下一张轮播图
const nextSlide = () => {
  currentSlide.value = currentSlide.value === slideCount.value - 1 ? 0 : currentSlide.value + 1;
};

// 跳转到指定轮播图
const goToSlide = (index) => {
  currentSlide.value = index;
};

// 停止轮播
const stopCarousel = () => {
  if (carouselTimer.value) clearInterval(carouselTimer.value);
};

// 开始轮播（自动切换）
const startCarousel = () => {
  carouselTimer.value = setInterval(() => {
    nextSlide();
  }, 3000); // 3秒自动切换一次
};

// 组件挂载时启动轮播
onMounted(() => {
  startCarousel();
});

// 组件卸载时清除定时器（防止内存泄漏）
onUnmounted(() => {
  stopCarousel();
});
</script>

<style scoped>
/* 页面整体样式 */
.product-page {
  background-color: #f9f9f9;
  min-height: 100vh;
}

/* 1. 轮播图区域样式 */
.carousel-section {
  background-color: #fff;
  padding: 20px 0;
  margin-bottom: 30px;
}

.carousel-container {
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.carousel-slides {
  display: flex;
  width: 100%;
}

.carousel-slide {
  flex: 0 0 100%;
  width: 100%;
}

.carousel-img {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

/* 轮播图按钮样式 */
.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 60px;
  background-color: rgba(0, 0, 0, 0.3);
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 18px;
  cursor: pointer;
  transition: background-color 0.3s;
  z-index: 10;
}

.carousel-btn:hover {
  background-color: rgba(63, 158, 216, 0.8);
}

.prev-btn {
  left: 20px;
}

.next-btn {
  right: 20px;
}

/* 轮播图指示器样式 */
.carousel-indicators {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  z-index: 10;
}

.indicator-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  transition: all 0.3s;
}

.indicator-dot.active {
  background-color: #1E90FF;
  width: 30px;
  border-radius: 6px;
}

/* 2. 商品专区通用样式 */
.product-section {
  padding: 30px 0;
  margin-bottom: 30px;
}

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
  margin-bottom: 25px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.section-title {
  font-size: 24px;
  color: #333;
  display: flex;
  align-items: center;
}

.title-icon {
  margin-right: 10px;
  font-size: 28px;
}

.more-link {
  color: #1E90FF;
  font-size: 14px;
  transition: color 0.3s;
}

.more-link:hover {
  color: #3794e1;
  text-decoration: underline;
}

/* 商品网格布局 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

/* 商品卡片样式 */
.product-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.card-img-container {
  position: relative;
  height: 220px;
  overflow: hidden;
}

.product-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.product-card:hover .product-img {
  transform: scale(1.05);
}

/* 商品标签 */
.card-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 3px 10px;
  font-size: 12px;
  color: #fff;
  border-radius: 4px;
}

.hot-tag {
  background-color: #1E90FF;
}

.new-tag {
  background-color: #00b42a;
}

/* 商品卡片内容 */
.card-content {
  padding: 15px;
}

.product-name {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 评分样式 */
.product-rating {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.star {
  color: #ddd;
  font-size: 14px;
  margin-right: 2px;
}

.star.active {
  color: #ffb800;
}

.rating-count {
  font-size: 12px;
  color: #999;
  margin-left: 5px;
}

/* 价格样式 */
.product-price {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.current-price {
  color: #1E90FF;
  font-size: 18px;
  font-weight: bold;
  margin-right: 10px;
}

.original-price {
  color: #999;
  font-size: 14px;
  text-decoration: line-through;
}

/* 加入购物车按钮 */
.add-cart-btn {
  width: 100%;
  height: 36px;
  background-color: #1E90FF;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.add-cart-btn:hover {
  background-color: blue;
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
  
  .carousel-img {
    height: 350px;
  }
}

@media (max-width: 768px) {
  .container {
    width: 100%;
  }
  
  .carousel-img {
    height: 280px;
  }
  
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  }
  
  .section-title {
    font-size: 20px;
  }
}

@media (max-width: 576px) {
  .carousel-img {
    height: 200px;
  }
  
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  }
  
  .card-img-container {
    height: 180px;
  }
  
  .product-name {
    font-size: 14px;
  }
  
  .current-price {
    font-size: 16px;
  }
}
</style>
