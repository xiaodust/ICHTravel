<template>
  <div class="product-list-page">
    <!-- 1. 页面标题与面包屑导航 -->
    <section class="page-header">
      <div class="container">
        <div class="breadcrumb">
          <a href="#" @click="$router.push('/heritage-mall')">首页</a>
          <span class="separator">/</span>
          <span class="current">{{ pageTitle }}</span>
        </div>
        <h1 class="page-title">
          <i class="title-icon">{{ sectionIcon }}</i> {{ pageTitle }}
        </h1>
      </div>
    </section>

    <!-- 2. 筛选与排序区域 -->
    <section class="filter-section">
      <div class="container">
        <div class="filter-container">
          <!-- 左侧筛选条件 -->
          <div class="filter-left">
            <!-- 价格区间筛选 -->
            <div class="filter-group">
              <h3 class="filter-title">价格区间</h3>
              <div class="price-filter">
                <input 
                  type="number" 
                  v-model.number="priceMin" 
                  placeholder="最低价" 
                  class="price-input"
                >
                <span class="price-separator">-</span>
                <input 
                  type="number" 
                  v-model.number="priceMax" 
                  placeholder="最高价" 
                  class="price-input"
                >
                <button class="price-btn" @click="filterByPrice">确定</button>
              </div>
              <div class="price-tags">
                <span 
                  class="price-tag" 
                  v-for="(range, idx) in priceRanges" 
                  :key="idx"
                  @click="selectPriceRange(range)"
                >
                  {{ range.text }}
                </span>
              </div>
            </div>

            <!-- 销量/评分筛选 -->
            <div class="filter-group">
              <h3 class="filter-title">筛选条件</h3>
              <div class="filter-tags">
                <span 
                  class="filter-tag" 
                  :class="{ active: activeFilter === 'all' }"
                  @click="setActiveFilter('all')"
                >
                  全部商品
                </span>
                <span 
                  class="filter-tag" 
                  :class="{ active: activeFilter === 'hot' }"
                  @click="setActiveFilter('hot')"
                >
                  热销商品
                </span>
                <span 
                  class="filter-tag" 
                  :class="{ active: activeFilter === 'new' }"
                  @click="setActiveFilter('new')"
                >
                  新品上市
                </span>
                <span 
                  class="filter-tag" 
                  :class="{ active: activeFilter === 'highRate' }"
                  @click="setActiveFilter('highRate')"
                >
                  高评分
                </span>
              </div>
            </div>
          </div>

          <!-- 右侧排序条件 -->
          <div class="filter-right">
            <h3 class="sort-title">排序方式</h3>
            <select 
              v-model="sortType" 
              class="sort-select"
              @change="sortProducts"
            >
              <option value="default">默认排序</option>
              <option value="priceAsc">价格从低到高</option>
              <option value="priceDesc">价格从高到低</option>
              <option value="salesDesc">销量从高到低</option>
              <option value="rateDesc">评分从高到低</option>
            </select>
          </div>
        </div>
      </div>
    </section>

    <!-- 3. 商品列表区域 -->
    <section class="product-list-section">
      <div class="container">
        <!-- 商品网格 -->
        <div class="product-grid">
          <!-- 商品卡片（循环渲染） -->
          <div 
            class="product-card" 
            v-for="(product, idx) in paginatedProducts" 
            :key="idx"
          >
            <div class="card-img-container">
              <img 
                :src="product.imgUrl" 
                :alt="product.name" 
                class="product-img"
              >
              <!-- 商品标签（热销/新品） -->
              <div 
                class="card-tag hot-tag" 
                v-if="product.tag === 'hot'"
              >
                热销
              </div>
              <div 
                class="card-tag new-tag" 
                v-if="product.tag === 'new'"
              >
                新品
              </div>
            </div>
            <div class="card-content">
              <h3 class="product-name">{{ product.name }}</h3>
              <!-- 商品评分 -->
              <div class="product-rating">
                <span 
                  class="star" 
                  v-for="star in 5" 
                  :key="star"
                  :class="{ active: star <= product.rate }"
                >
                  ★
                </span>
                <span class="rating-count">({{ product.rateCount }})</span>
              </div>
              <!-- 商品价格 -->
              <div class="product-price">
                <span class="current-price">¥{{ product.currentPrice }}</span>
                <span class="original-price">¥{{ product.originalPrice }}</span>
              </div>
              <button class="add-cart-btn">加入购物车</button>
            </div>
          </div>

          <!-- 空数据提示 -->
          <div class="empty-tip" v-if="paginatedProducts.length === 0">
            <img 
              src="https://picsum.photos/id/237/100/100" 
              alt="暂无数据" 
              class="empty-img"
            >
            <p class="empty-text">暂无符合条件的商品~</p>
            <button class="reset-btn" @click="resetFilter">重置筛选条件</button>
          </div>
        </div>

        <!-- 分页控件 -->
        <div class="pagination-container" v-if="filteredProducts.length > 0">
          <button 
            class="page-btn prev-page" 
            @click="changePage(currentPage - 1)"
            :disabled="currentPage === 1"
          >
            上一页
          </button>
          <div class="page-numbers">
            <span 
              class="page-number" 
              v-for="page in pageCount" 
              :key="page"
              :class="{ active: page === currentPage }"
              @click="changePage(page)"
            >
              {{ page }}
            </span>
          </div>
          <button 
            class="page-btn next-page" 
            @click="changePage(currentPage + 1)"
            :disabled="currentPage === pageCount"
          >
            下一页
          </button>
          <div class="page-info">
            共 {{ totalProducts }} 件商品，当前第 {{ currentPage }}/{{ pageCount }} 页
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';

// 1. 路由参数获取（区分食品/工艺专区）
const route = useRoute();
const sectionType = ref(route.params.type || 'food');

// 2. 页面基础配置（根据专区类型切换标题、图标、商品数据）
const pageConfig = computed(() => {
  return sectionType.value === 'food' 
    ? {
        title: '美味食品专区',
        icon: '🍽️',
        products: [
          {
            name: '农家手工腊肠 500g',
            imgUrl: 'https://picsum.photos/id/292/300/300',
            tag: 'hot',
            rate: 4,
            rateCount: 128,
            currentPrice: 59.9,
            originalPrice: 89.0,
            sales: 1200
          },
          {
            name: '东北有机五常大米 5kg',
            imgUrl: 'https://picsum.photos/id/139/300/300',
            tag: '',
            rate: 5,
            rateCount: 256,
            currentPrice: 89.0,
            originalPrice: 109.0,
            sales: 850
          },
          {
            name: '秦岭土蜂蜜 500g',
            imgUrl: 'https://picsum.photos/id/175/300/300',
            tag: 'new',
            rate: 4,
            rateCount: 64,
            currentPrice: 129.0,
            originalPrice: 159.0,
            sales: 320
          },
          {
            name: '无添加手工曲奇饼干 200g',
            imgUrl: 'https://picsum.photos/id/225/300/300',
            tag: '',
            rate: 4,
            rateCount: 98,
            currentPrice: 39.9,
            originalPrice: 59.0,
            sales: 560
          },
          {
            name: '新疆和田骏枣 400g',
            imgUrl: 'https://picsum.photos/id/152/300/300',
            tag: 'hot',
            rate: 5,
            rateCount: 189,
            currentPrice: 45.0,
            originalPrice: 68.0,
            sales: 920
          },
          {
            name: '手工牛肉酱 280g',
            imgUrl: 'https://picsum.photos/id/167/300/300',
            tag: 'new',
            rate: 4,
            rateCount: 76,
            currentPrice: 32.9,
            originalPrice: 49.0,
            sales: 280
          },
          {
            name: '云南野生菌菇包 300g',
            imgUrl: 'https://picsum.photos/id/188/300/300',
            tag: '',
            rate: 5,
            rateCount: 112,
            currentPrice: 79.0,
            originalPrice: 99.0,
            sales: 450
          },
          {
            name: '潮汕手打牛肉丸 500g',
            imgUrl: 'https://picsum.photos/id/201/300/300',
            tag: 'hot',
            rate: 4,
            rateCount: 203,
            currentPrice: 69.0,
            originalPrice: 88.0,
            sales: 1100
          }
        ]
      }
    : {
        title: '精品工艺专区',
        icon: '🎨',
        products: [
          {
            name: '景德镇手工陶瓷花瓶',
            imgUrl: 'https://picsum.photos/id/118/300/300',
            tag: 'hot',
            rate: 4,
            rateCount: 156,
            currentPrice: 299.0,
            originalPrice: 399.0,
            sales: 380
          },
          {
            name: '海南黄花梨手串 18mm',
            imgUrl: 'https://picsum.photos/id/177/300/300',
            tag: '',
            rate: 5,
            rateCount: 89,
            currentPrice: 599.0,
            originalPrice: 799.0,
            sales: 150
          },
          {
            name: '苏绣手工刺绣装饰画',
            imgUrl: 'https://picsum.photos/id/218/300/300',
            tag: 'new',
            rate: 4,
            rateCount: 45,
            currentPrice: 899.0,
            originalPrice: 1099.0,
            sales: 98
          },
          {
            name: '紫砂手工功夫茶具套装',
            imgUrl: 'https://picsum.photos/id/325/300/300',
            tag: '',
            rate: 4,
            rateCount: 112,
            currentPrice: 699.0,
            originalPrice: 899.0,
            sales: 210
          },
          {
            name: '东阳木雕挂件 平安符',
            imgUrl: 'https://picsum.photos/id/240/300/300',
            tag: 'hot',
            rate: 5,
            rateCount: 134,
            currentPrice: 199.0,
            originalPrice: 269.0,
            sales: 320
          },
          {
            name: '景泰蓝工艺手镯',
            imgUrl: 'https://picsum.photos/id/251/300/300',
            tag: 'new',
            rate: 4,
            rateCount: 67,
            currentPrice: 269.0,
            originalPrice: 359.0,
            sales: 120
          },
          {
            name: '龙泉青瓷茶杯 单杯',
            imgUrl: 'https://picsum.photos/id/267/300/300',
            tag: '',
            rate: 5,
            rateCount: 92,
            currentPrice: 129.0,
            originalPrice: 169.0,
            sales: 180
          },
          {
            name: '手工编织竹篮收纳筐',
            imgUrl: 'https://picsum.photos/id/289/300/300',
            tag: 'hot',
            rate: 4,
            rateCount: 145,
            currentPrice: 89.0,
            originalPrice: 119.0,
            sales: 290
          }
        ]
      };
});

// 页面标题和图标
const pageTitle = computed(() => pageConfig.value.title);
const sectionIcon = computed(() => pageConfig.value.icon);

// 3. 筛选与排序相关状态
const allProducts = computed(() => pageConfig.value.products);
const priceMin = ref('');
const priceMax = ref('');
const activeFilter = ref('all');
const sortType = ref('default');

// 价格区间预设
const priceRanges = [
  { text: '0-50元', min: 0, max: 50 },
  { text: '50-100元', min: 50, max: 100 },
  { text: '100-300元', min: 100, max: 300 },
  { text: '300元以上', min: 300, max: Infinity }
];

// 4. 分页相关状态
const pageSize = ref(8);
const currentPage = ref(1);
const totalProducts = computed(() => filteredProducts.value.length);
const pageCount = computed(() => Math.max(1, Math.ceil(totalProducts.value / pageSize.value)));

// 5. 筛选后的商品列表
const filteredProducts = computed(() => {
  let result = [...allProducts.value];

  // 价格筛选
  if (priceMin.value !== '' && !isNaN(priceMin.value)) {
    result = result.filter(p => p.currentPrice >= priceMin.value);
  }
  if (priceMax.value !== '' && !isNaN(priceMax.value)) {
    result = result.filter(p => p.currentPrice <= priceMax.value);
  }

  // 标签筛选
  if (activeFilter.value === 'hot') {
    result = result.filter(p => p.tag === 'hot');
  } else if (activeFilter.value === 'new') {
    result = result.filter(p => p.tag === 'new');
  } else if (activeFilter.value === 'highRate') {
    result = result.filter(p => p.rate >= 4.5);
  }

  // 排序处理
  return sortProducts(result);
});

// 6. 分页处理
const paginatedProducts = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  return filteredProducts.value.slice(startIndex, startIndex + pageSize.value);
});

// 7. 筛选与排序方法
const filterByPrice = () => {
  currentPage.value = 1; // 重置到第一页
};

const selectPriceRange = (range) => {
  priceMin.value = range.min;
  priceMax.value = range.max;
  currentPage.value = 1;
};

const setActiveFilter = (filterType) => {
  activeFilter.value = filterType;
  currentPage.value = 1;
};

const sortProducts = (products) => {
  const sorted = [...products];
  
  switch (sortType.value) {
    case 'priceAsc':
      return sorted.sort((a, b) => a.currentPrice - b.currentPrice);
    case 'priceDesc':
      return sorted.sort((a, b) => b.currentPrice - a.currentPrice);
    case 'salesDesc':
      return sorted.sort((a, b) => b.sales - a.sales);
    case 'rateDesc':
      return sorted.sort((a, b) => b.rate - a.rate);
    default:
      return sorted;
  }
};

// 8. 分页方法
const changePage = (page) => {
  if (page >= 1 && page <= pageCount.value) {
    currentPage.value = page;
    // 滚动到页面顶部
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
};

// 9. 重置筛选条件
const resetFilter = () => {
  priceMin.value = '';
  priceMax.value = '';
  activeFilter.value = 'all';
  sortType.value = 'default';
  currentPage.value = 1;
};

// 页面加载时初始化
onMounted(() => {
  // 确保路由参数变化时更新数据
  currentPage.value = 1;
});
</script>

<style scoped>
/* 页面基础样式 */
.product-list-page {
  background-color: #f9f9f9;
  min-height: 100vh;
  padding-bottom: 60px;
}

.container {
  width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
}

/* 1. 页面标题与面包屑 */
.page-header {
  padding: 30px 0;
  background-color: #fff;
  margin-bottom: 20px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.breadcrumb {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  font-size: 14px;
  color: #666;
}

.breadcrumb a {
  color: #666;
  text-decoration: none;
}

.breadcrumb a:hover {
  color: #1E90FF;
  text-decoration: underline;
}

.separator {
  margin: 0 10px;
  color: #ccc;
}

.current {
  color: #333;
  font-weight: 500;
}

.page-title {
  font-size: 28px;
  color: #333;
  display: flex;
  align-items: center;
}

.title-icon {
  margin-right: 10px;
  font-size: 32px;
}

/* 2. 筛选区域样式 */
.filter-section {
  background-color: #fff;
  padding: 20px 0;
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.filter-container {
  display: flex;
  justify-content: space-between;
}

.filter-left {
  flex: 1;
}

.filter-group {
  margin-bottom: 15px;
}

.filter-title {
  font-size: 16px;
  color: #333;
  margin-bottom: 10px;
  font-weight: 500;
}

/* 价格筛选 */
.price-filter {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.price-input {
  width: 100px;
  height: 32px;
  padding: 0 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.price-separator {
  margin: 0 10px;
  color: #999;
}

.price-btn {
  margin-left: 10px;
  padding: 0 15px;
  height: 32px;
  background-color: #1E90FF;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.price-btn:hover {
  background-color: blue;
}

.price-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.price-tag {
  padding: 4px 12px;
  background-color: #f5f5f5;
  color: #666;
  border-radius: 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.price-tag:hover {
  background-color: #1E90FF;
  color: #fff;
}

/* 筛选标签 */
.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-tag {
  padding: 4px 15px;
  background-color: #f5f5f5;
  color: #666;
  border-radius: 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-tag.active {
  background-color: #1E90FF;
  color: #fff;
}

.filter-tag:hover:not(.active) {
  background-color: #eee;
}

/* 排序区域 */
.filter-right {
  display: flex;
  align-items: center;
}

.sort-title {
  font-size: 16px;
  color: #333;
  margin-right: 15px;
  font-weight: 500;
}

.sort-select {
  padding: 8px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  background-color: #fff;
  cursor: pointer;
}

/* 3. 商品列表样式 */
.product-list-section {
  padding: 20px 0;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

/* 商品卡片样式（复用原页面风格） */
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

/* 空数据提示 */
.empty-tip {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 0;
}

.empty-img {
  width: 100px;
  height: 100px;
  object-fit: contain;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-text {
  font-size: 16px;
  color: #999;
  margin-bottom: 20px;
}

.reset-btn {
  padding: 8px 20px;
  background-color: #1E90FF;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.reset-btn:hover {
  background-color: blue;
}

/* 分页样式 */
.pagination-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  margin-top: 30px;
}

.page-btn {
  padding: 6px 15px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  background-color: #1E90FF;
  color: #fff;
  border-color: #1E90FF;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 5px;
}

.page-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 4px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.page-number:hover:not(.active) {
  background-color: #f5f5f5;
}

.page-number.active {
  background-color: #1E90FF;
  color: #fff;
}

.page-info {
  font-size: 14px;
  color: #666;
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
  
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  }
}

@media (max-width: 768px) {
  .container {
    width: 100%;
  }
  
  .filter-container {
    flex-direction: column;
    gap: 15px;
  }
  
  .filter-right {
    justify-content: flex-start;
  }
  
  .page-title {
    font-size: 24px;
  }
}

@media (max-width: 576px) {
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  }
  
  .card-img-container {
    height: 180px;
  }
  
  .product-name {
    font-size: 14px;
  }
  
  .pagination-container {
    flex-wrap: wrap;
  }
}
</style>
