<template>
    <div class="shopping-cart-page">
        <!-- 顶部导航 -->
        <header class="top-bar">
            <div class="logo" @click="$router.push('/home')">点苏记</div>
            <div class="search-box">
                <input type="text" placeholder="搜索非遗商品/路线" v-model="searchQuery">
                <button class="search-btn" @click="search">搜索</button>
            </div>
            <div class="user-actions">
                <button class="action-btn" title="返回" @click="router.push('/heritage-mall')">
                  返回Mall
                    <i class="icon icon-notify"></i>
                </button>
                <button class="action-btn" title="个人中心" @click="goPersonCenter">
                    <i class="icon icon-user"></i>
                </button>
            </div>
        </header>

        <!-- 主体内容 -->
        <main class="main-content">
            <div class="page-title">我的购物车</div>
            
            <!-- 购物车为空状态 -->
            <div class="empty-cart" v-if="cartItems.length === 0">
                <div class="empty-icon">
                    <i class="icon icon-cart-empty"></i>
                </div>
                <p class="empty-text">您的购物车还是空的</p>
                <button class="go-shopping-btn" @click="goShopping">去逛逛非遗好物</button>
            </div>
            
            <!-- 购物车列表 -->
            <div class="cart-container" v-else>
                <div class="cart-header">
                    <label class="checkbox-all">
                        <input type="checkbox" v-model="selectAll" @change="handleSelectAll">
                        <span>全选</span>
                    </label>
                    <div class="col-goods">商品信息</div>
                    <div class="col-price">单价</div>
                    <div class="col-quantity">数量</div>
                    <div class="col-subtotal">小计</div>
                    <div class="col-action">操作</div>
                </div>
                
                <div class="cart-list">
                    <div class="cart-item" v-for="(item, index) in cartItems" :key="getCartItemId(item) || index">
                        <label class="item-checkbox">
                            <input type="checkbox" v-model="item.selected" @change="handleItemSelect">
                        </label>
                        <div class="item-goods">
                            <img :src="item.image" :alt="item.name" class="goods-image">
                            <div class="goods-info">
                                <h4 class="goods-name">{{ item.name }}</h4>
                                <p class="goods-spec">{{ item.spec }}</p>
                                <p class="goods-origin">非遗产地: {{ item.origin }}</p>
                            </div>
                        </div>
                        <div class="item-price">¥{{ item.price.toFixed(2) }}</div>
                        <div class="item-quantity">
                            <button class="quantity-btn minus" @click="decreaseQuantity(index)" :disabled="item.quantity <= 1">-</button>
                            <input type="number" v-model.number="item.quantity" @change="handleQuantityChange(index)" min="1">
                            <button class="quantity-btn plus" @click="increaseQuantity(index)">+</button>
                        </div>
                        <div class="item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
                        <div class="item-action">
                            <button class="action-btn remove" @click="removeItemById(getCartItemId(item))">删除</button>
                            <button class="action-btn favorite" :class="{ active: item.isFavorite }" @click="toggleFavorite(index)">
                                <i class="icon icon-favorite"></i>
                            </button>
                        </div>
                    </div>
                </div>
                
                <!-- 推荐商品 -->
                <div class="recommend-section">
                    <h3 class="section-title">为您推荐</h3>
                    <div class="recommend-list">
                        <div class="recommend-item" v-for="item in recommendItems" :key="item.id" @click="addRecommendToCart(item)">
                            <img :src="item.image" :alt="item.name" class="recommend-image">
                            <h4 class="recommend-name">{{ item.name }}</h4>
                            <p class="recommend-price">¥{{ item.price.toFixed(2) }}</p>
                            <button class="add-to-cart-btn">加入购物车</button>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- 结算栏 -->
            <div class="checkout-bar" v-if="cartItems.length > 0">
                <div class="checkout-left">
                    <label class="checkbox-all">
                        <input type="checkbox" v-model="selectAll" @change="handleSelectAll">
                        <span>全选</span>
                    </label>
                    <button class="remove-selected" @click="removeSelectedItems">删除选中商品</button>
                </div>
                <div class="checkout-right">
                    <div class="summary">
                        <p>已选 <span class="selected-count">{{ selectedCount }}</span> 件商品</p>
                        <p>合计: <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span></p>
                        <p class="freight">运费: {{ totalPrice >= 99 ? '免费' : '¥10.00' }}</p>
                        <p class="payable">应付金额: <span class="payable-price">¥{{ payablePrice.toFixed(2) }}</span></p>
                    </div>
                    <button class="checkout-btn" :disabled="selectedCount === 0" @click="goCheckout">
                        结算
                    </button>
                </div>
            </div>
        </main>
        
        <!-- 确认删除弹窗 -->
        <div class="modal-overlay" v-if="showDeleteModal">
            <div class="modal">
                <div class="modal-header">
                    <h3>确认删除</h3>
                    <button class="close-btn" @click="showDeleteModal = false">&times;</button>
                </div>
                <div class="modal-body">
                    <p>确定要删除这件商品吗？</p>
                </div>
                <div class="modal-footer">
                    <button class="cancel-btn" @click="showDeleteModal = false">取消</button>
                    <button class="confirm-btn" @click="confirmDelete">确认</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

// 使用顶部已声明的 router 实例

// 搜索查询
const searchQuery = ref('');

// 购物车商品数据
const cartItems = ref([]);

// 推荐商品
const recommendItems = ref([
    {
        id: 101,
        name: '南京云锦围巾',
        image: 'https://picsum.photos/seed/yunjin/200/200',
        price: 198
    },
    {
        id: 102,
        name: '宜兴紫砂壶（迷你）',
        image: 'https://picsum.photos/seed/zisha/200/200',
        price: 368
    },
    {
        id: 103,
        name: '苏绣手帕',
        image: 'https://picsum.photos/seed/suxiu/200/200',
        price: 68
    }
]);

// 全选状态
const selectAll = ref(true);

// 删除相关
const showDeleteModal = ref(false);
const currentDeleteIndex = ref(-1);
const currentDeleteId = ref(null);

// 计算属性：选中商品数量
const selectedCount = computed(() => {
    return cartItems.value.reduce((count, item) => {
        return item.selected ? count + item.quantity : count;
    }, 0);
});

// 计算属性：选中商品总价
const totalPrice = computed(() => {
    return cartItems.value.reduce((sum, item) => {
        return item.selected ? sum + (item.price * item.quantity) : sum;
    }, 0);
});

// 计算属性：应付金额（含运费）
const payablePrice = computed(() => {
    // 满99元免运费
    const freight = totalPrice.value >= 99 ? 0 : 10;
    return totalPrice.value + freight;
});

// 生命周期钩子
onMounted(() => {
    // 初始化全选状态
    updateSelectAllStatus();
    // 加载后端购物车数据
    loadCartItems();
});

// 更新全选状态
const updateSelectAllStatus = () => {
    if (cartItems.value.length === 0) {
        selectAll.value = false;
        return;
    }
    selectAll.value = cartItems.value.every(item => item.selected);
};

// 处理全选
const handleSelectAll = () => {
    cartItems.value.forEach(item => {
        item.selected = selectAll.value;
    });
};

// 处理单个商品选择
const handleItemSelect = () => {
    updateSelectAllStatus();
};

// 增加数量
const increaseQuantity = (index) => {
    cartItems.value[index].quantity++;
};

// 减少数量
const decreaseQuantity = (index) => {
    if (cartItems.value[index].quantity > 1) {
        cartItems.value[index].quantity--;
    }
};

// 处理数量变化
const handleQuantityChange = (index) => {
    // 确保数量不为负数
    if (cartItems.value[index].quantity < 1) {
        cartItems.value[index].quantity = 1;
    }
};

// 准备删除商品（改为直接删除，无确认弹窗）
const removeItem = async (index) => {
  const id = cartItems.value[index]?.id;
  console.log('[Cart] Immediate delete index:', index, 'id:', id);
  if (id) {
    await deleteCartItemById(id);
  } else {
    console.warn('[Cart] No id found for deletion at index:', index);
  }
};

// 确认删除
const confirmDelete = async () => {
  const id = currentDeleteId.value || cartItems.value[currentDeleteIndex.value]?.id;
  console.log('[Cart] Confirm delete id:', id, 'index:', currentDeleteIndex.value);
  if (id) {
    await deleteCartItemById(id);
  }
  showDeleteModal.value = false;
  currentDeleteIndex.value = -1;
  currentDeleteId.value = null;
};

// 删除选中商品
// 统一提取后端返回的购物车条目ID，兼容多种命名/大小写
const getCartItemId = (ci) => ci?.cartItemId || ci?.cartItemID || ci?.CartItemId || ci?.CartItemID || ci?.id || ci?.itemId || null;
const removeSelectedItems = async () => {
    const ids = cartItems.value.filter(item => item.selected).map(i => getCartItemId(i)).filter(Boolean);
     for (const id of ids) {
         await deleteCartItemById(id);
     }
     // 单项删除已刷新，这里不重复reload
     updateSelectAllStatus();
};

// 切换收藏状态
const toggleFavorite = (index) => {
    cartItems.value[index].isFavorite = !cartItems.value[index].isFavorite;
};

// 添加推荐商品到购物车
const addRecommendToCart = (item) => {
    addCartItemToBackend({
        productId: item.id,
        name: item.name,
        price: item.price,
        image: item.image,
        quantity: 1
    });
};

// 搜索功能
const search = () => {
    if (searchQuery.value.trim()) {
        console.log('搜索:', searchQuery.value);
        // 实际项目中这里会跳转到搜索结果页
    }
};

// 页面跳转
const goHome = () => {
    router.push('/');
};

const goPersonCenter = () => {
    router.push('/user-center');
};

const goShopping = () => {
    router.push('/goods-list');
};

const goCheckout = () => {
    router.push('/checkout');
};

// 路由实例
const router = useRouter();

// API 基础配置
const apiBase = 'http://localhost:8080';
// 从登录态获取用户ID作为购物车ID（不再使用 demo-user）
const getUserId = () => {
  // 优先 sessionStorage（登录页已写入）
  const sid = sessionStorage.getItem('userId');
  if (sid) return sid;
  // 其次 localStorage 的 userInfo.id
  try {
    const ui = localStorage.getItem('userInfo');
    if (ui) {
      const parsed = JSON.parse(ui);
      if (parsed && parsed.id) return parsed.id;
    }
  } catch (_) {}
  return null;
};
const cartId = getUserId();

// 若未登录，提示并引导登录
if (!cartId) {
  console.warn('[Cart] 未检测到登录用户ID，购物车数据将无法加载');
  alert('请先登录后再查看购物车');
}

const resolveImg = (url) => {
  if (!url) return 'https://picsum.photos/seed/cart/200/200';
  if (url.startsWith('/static')) return apiBase + url.replace('/static', '');
  if (url.startsWith('/uploads')) return apiBase + url;
  return url;
};

const normalizeItem = (ci) => {
  const cid = getCartItemId(ci);
  return {
    id: cid,
    cartItemId: cid,
    productId: ci.productId ?? ci.pid ?? null,
    name: ci.productName || ci.name,
    image: resolveImg(ci.productImage || ci.image),
    spec: ci.spec || '默认规格',
    origin: ci.origin || '非遗产地',
    price: Number(ci.productPrice ?? ci.price ?? 0),
    quantity: (() => { const q = Number(ci.number ?? ci.quantity ?? 1); return q > 0 ? q : 1; })(),
    selected: true,
    isFavorite: false,
    raw: ci
  };
};
const loadCartItems = async () => {
  try {
    if (!cartId) return; // 未登录不请求
    const res = await axios.get(`${apiBase}/api/cart/items/${cartId}`);
    const result = res.data || {};
    const ok = result.success === true || result.isSuccess === true || Array.isArray(result.data);
    const items = ok && Array.isArray(result.data) ? result.data : [];
    cartItems.value = items.map(normalizeItem);
    console.log('[Cart] Loaded raw items:', items);
    console.log('[Cart] Normalized items:', cartItems.value.map(i => ({ id: i.id, cartItemId: i.cartItemId, productId: i.productId })));
    updateSelectAllStatus();
  } catch (e) {
    console.error('加载购物车失败:', e);
  }
};

const uuid = () => (window.crypto?.randomUUID?.() || ('ci-' + Date.now() + '-' + Math.floor(Math.random()*1e6)));

const addCartItemToBackend = async (payload) => {
  try {
    if (!cartId) {
      alert('请先登录后再添加到购物车');
      return;
    }
    const body = {
      id: payload.id || uuid(),
      cartId,
      productId: payload.productId,
      productName: payload.name,
      productPrice: payload.price,
      productImage: payload.image,
      number: payload.quantity || 1,
      totalPrice: Number(((payload.price || 0) * (payload.quantity || 1)).toFixed(2))
    };
    const res = await axios.post(`${apiBase}/api/cart/add`, body);
    const result = res.data || {};
    // 以HTTP 2xx返回视为成功，避免误判
    await loadCartItems();
  } catch (e) {
    console.error('添加购物车失败:', e);
    alert('添加购物车失败，请稍后重试');
  }
};

const deleteCartItemById = async (id) => {
  try {
    console.log('[Cart] Deleting item id:', id, 'via', `${apiBase}/api/cart/${id}`);
    const res = await axios.delete(`${apiBase}/api/cart/${id}`);
    const result = res.data || {};
    // 以HTTP 2xx返回视为成功，避免误判
    await loadCartItems();
  } catch (e) {
    console.error('删除购物车项失败:', e);
  }
};
const removeItemById = async (id) => {
  console.log('[Cart] removeItemById called with id:', id);
  if (!id) {
    console.warn('[Cart] Missing cart item id for deletion');
    alert('删除失败：未找到购物车条目的唯一ID');
    return;
  }
  await deleteCartItemById(id);
};
</script>

<style scoped>
/* 基础样式 */
.shopping-cart-page {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    background-color: #F5F5F0;
    color: #333333;
    font-family: "Microsoft Yahei", sans-serif;
}

/* 顶部导航 */
.top-bar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 60px;
    padding: 0 20px;
    background-color: white;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    position: sticky;
    top: 0;
    z-index: 100;
}

.logo {
    font-size: 24px;
    font-weight: bold;
    color: #1E3A8A;
    cursor: pointer;
}

.search-box {
    display: flex;
    flex: 1;
    max-width: 400px;
    margin: 0 40px;
}

.search-box input {
    flex: 1;
    padding: 8px 12px;
    border: 1px solid #DCDCDC;
    border-radius: 4px 0 0 4px;
    outline: none;
}

.search-box input:focus {
    border-color: #1E3A8A;
}

.search-btn {
    padding: 8px 16px;
    background-color: #1E3A8A;
    color: white;
    border: none;
    border-radius: 0 4px 4px 0;
    cursor: pointer;
}

.user-actions {
    display: flex;
    align-items: center;
    gap: 16px;
}

.action-btn {
    background: none;
    border: none;
    cursor: pointer;
    color: #1E3A8A;
    font-size: 18px;
    position: relative;
}

/* 主体内容 */
.main-content {
    flex: 1;
    padding: 20px;
    max-width: 1200px;
    margin: 0 auto;
    width: 100%;
}

.page-title {
    font-size: 22px;
    font-weight: bold;
    margin-bottom: 20px;
    color: #1E3A8A;
}

/* 空购物车 */
.empty-cart {
    background-color: white;
    border-radius: 8px;
    padding: 60px 20px;
    text-align: center;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.empty-icon {
    font-size: 60px;
    color: #DCDCDC;
    margin-bottom: 20px;
}

.empty-text {
    font-size: 16px;
    color: #666;
    margin-bottom: 30px;
}

.go-shopping-btn {
    padding: 10px 20px;
    background-color: #1E3A8A;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    transition: background-color 0.3s;
}

.go-shopping-btn:hover {
    background-color: #16306e;
}

/* 购物车容器 */
.cart-container {
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    overflow: hidden;
}

/* 购物车头部 */
.cart-header {
    display: flex;
    align-items: center;
    padding: 15px 20px;
    background-color: #f9f9f9;
    border-bottom: 1px solid #DCDCDC;
    font-weight: bold;
}

.checkbox-all {
    display: flex;
    align-items: center;
    gap: 8px;
    width: 60px;
}

.checkbox-all input {
    width: 16px;
    height: 16px;
    accent-color: #1E3A8A;
}

.col-goods {
    flex: 1;
}

.col-price, .col-subtotal {
    width: 120px;
    text-align: center;
}

.col-quantity {
    width: 150px;
    text-align: center;
}

.col-action {
    width: 100px;
    text-align: center;
}

/* 购物车列表 */
.cart-list {
    max-height: 500px;
    overflow-y: auto;
}

.cart-item {
    display: flex;
    align-items: center;
    padding: 15px 20px;
    border-bottom: 1px solid #DCDCDC;
    transition: background-color 0.2s;
}

.cart-item:hover {
    background-color: #f9f9f9;
}

.item-checkbox {
    width: 60px;
}

.item-checkbox input {
    margin-left: 8px;
    width: 16px;
    height: 16px;
    accent-color: #1E3A8A;
}

.item-goods {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 15px;
}

.goods-image {
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
    margin-bottom: 8px;
    line-height: 1.4;
}

.goods-spec, .goods-origin {
    font-size: 12px;
    color: #666;
    margin-bottom: 4px;
}

.item-price, .item-subtotal {
    width: 120px;
    text-align: center;
}

.item-quantity {
    width: 150px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.quantity-btn {
    width: 28px;
    height: 28px;
    border: 1px solid #DCDCDC;
    background-color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    font-size: 16px;
}

.quantity-btn:disabled {
    background-color: #f1f1f1;
    cursor: not-allowed;
    color: #999;
}

.item-quantity input {
    width: 40px;
    height: 28px;
    border-top: 1px solid #DCDCDC;
    border-bottom: 1px solid #DCDCDC;
    border-left: none;
    border-right: none;
    text-align: center;
    outline: none;
}

.item-action {
    width: 100px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
}

.item-action .action-btn {
    font-size: 12px;
    color: #666;
    padding: 4px 8px;
}

.item-action .action-btn.remove:hover {
    color: #e53935;
}

.item-action .action-btn.favorite {
    display: flex;
    align-items: center;
    gap: 4px;
}

.item-action .action-btn.favorite.active {
    color: #D4AF37;
}

/* 推荐商品 */
.recommend-section {
    padding: 20px;
    border-top: 1px solid #DCDCDC;
}

.section-title {
    font-size: 16px;
    margin-bottom: 15px;
    color: #1E3A8A;
}

.recommend-list {
    display: flex;
    gap: 15px;
    overflow-x: auto;
    padding-bottom: 10px;
}

.recommend-item {
    min-width: 180px;
    border: 1px solid #DCDCDC;
    border-radius: 4px;
    padding: 10px;
    cursor: pointer;
    transition: transform 0.3s, box-shadow 0.3s;
}

.recommend-item:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.recommend-image {
    width: 100%;
    height: 120px;
    object-fit: cover;
    border-radius: 4px;
    margin-bottom: 10px;
}

.recommend-name {
    font-size: 14px;
    margin-bottom: 8px;
    line-height: 1.3;
    height: 2.6em;
    overflow: hidden;
}

.recommend-price {
    font-size: 14px;
    color: #e53935;
    margin-bottom: 10px;
}

.add-to-cart-btn {
    width: 100%;
    padding: 6px 0;
    background-color: #1E3A8A;
    color: white;
    border: none;
    border-radius: 4px;
    font-size: 12px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.add-to-cart-btn:hover {
    background-color: #16306e;
}

/* 结算栏 */
.checkout-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
    background-color: white;
    border-top: 1px solid #DCDCDC;
    margin-top: 10px;
    border-radius: 8px;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.checkout-left {
    display: flex;
    align-items: center;
    gap: 20px;
}

.remove-selected {
    background: none;
    border: none;
    color: #666;
    cursor: pointer;
    font-size: 14px;
}

.remove-selected:hover {
    color: #e53935;
}

.checkout-right {
    display: flex;
    align-items: center;
    gap: 20px;
}

.summary {
    text-align: right;
}

.summary p {
    margin-bottom: 5px;
    font-size: 14px;
}

.selected-count {
    color: #1E3A8A;
    font-weight: bold;
}

.total-price, .payable-price {
    color: #e53935;
    font-weight: bold;
}

.freight {
    color: #666;
}

.checkout-btn {
    padding: 10px 30px;
    background-color: #1E3A8A;
    color: white;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.checkout-btn:disabled {
    background-color: #999;
    cursor: not-allowed;
}

.checkout-btn:not(:disabled):hover {
    background-color: #16306e;
}

/* 弹窗样式 */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0,0,0,0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

.modal {
    width: 300px;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.modal-header {
    padding: 15px 20px;
    border-bottom: 1px solid #DCDCDC;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.modal-header h3 {
    font-size: 16px;
    font-weight: bold;
}

.close-btn {
    background: none;
    border: none;
    font-size: 20px;
    cursor: pointer;
    color: #666;
}

.modal-body {
    padding: 20px;
    text-align: center;
}

.modal-footer {
    padding: 15px 20px;
    border-top: 1px solid #DCDCDC;
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

.cancel-btn, .confirm-btn {
    padding: 6px 16px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
}

.cancel-btn {
    background-color: white;
    border: 1px solid #DCDCDC;
    color: #333;
}

.confirm-btn {
    background-color: #e53935;
    border: 1px solid #e53935;
    color: white;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .top-bar {
        flex-wrap: wrap;
        height: auto;
        padding: 10px;
    }
    
    .search-box {
        order: 3;
        margin: 10px 0 0;
        width: 100%;
        max-width: none;
    }
    
    .cart-header {
        padding: 10px;
        font-size: 12px;
    }
    
    .col-price, .col-subtotal, .col-action {
        width: 80px;
    }
    
    .col-quantity {
        width: 100px;
    }
    
    .cart-item {
        padding: 10px;
    }
    
    .goods-image {
        width: 60px;
        height: 60px;
    }
    
    .goods-name, .goods-spec, .goods-origin {
        font-size: 12px;
    }
    
    .checkout-bar {
        flex-wrap: wrap;
        padding: 10px;
    }
    
    .checkout-left, .checkout-right {
        width: 100%;
        margin-bottom: 10px;
    }
    
    .checkout-right {
        flex-direction: column;
        align-items: flex-start;
    }
    
    .summary {
        text-align: left;
        width: 100%;
    }
    
    .checkout-btn {
        width: 100%;
        padding: 10px 0;
        text-align: center;
    }
    
    .recommend-item {
        min-width: 140px;
    }
}

/* 图标样式 */
.icon {
    display: inline-block;
    width: 24px;
    height: 24px;
    background-size: contain;
    background-repeat: no-repeat;
    background-position: center;
}

/* .icon-notify {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath fill='%231E3A8A' d='M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.18C9.63 5.36 8 7.92 8 11v5l-2 2v1h16v-1l-2-2z'/%3E%3C/svg%3E");
} */

.icon-user {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath fill='%231E3A8A' d='M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z'/%3E%3C/svg%3E");
}

.icon-cart-empty {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath fill='%23DCDCDC' d='M7 18c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zM1 4h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.13 0-.25-.11-.25-.25l.03-.12.9-1.63h7.45c.75 0 1.41-.41 1.75-1.03l3.58-6.49c.37-.66-.11-1.48-.87-1.48H5.21l-.94-2H1v2zm16 14c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2z'/%3E%3C/svg%3E");
}

.icon-favorite {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath fill='currentColor' d='M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z'/%3E%3C/svg%3E");
    width: 16px;
    height: 16px;
}
/* 返回按钮样式 */
.action-btn {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 8px 14px;
    background-color: #f5f5f5;
    color: #1E3A8A;
    border: 1px solid #DCDCDC;
    border-radius: 4px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.action-btn:hover {
    background-color: #e9e9e9;
    border-color: #1E3A8A;
    transform: translateY(-1px);
}

.action-btn:active {
    transform: translateY(0);
    box-shadow: inset 0 2px 3px rgba(0,0,0,0.1);
}

/* 图标调整 - 使用更美观的返回箭头 */
.action-btn .icon-notify {
    width: 18px;
    height: 18px;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath fill='%231E3A8A' d='M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12l4.58-4.59z'/%3E%3C/svg%3E");
}

/* 增强按钮整体美感 */
.action-btn {
    /* 保持原有样式... */
    gap: 8px; /* 增加图标与文字间距 */
    font-weight: 500; /* 略微加粗文字 */
}

/* 悬停时图标颜色变化，增强交互感 */
.action-btn:hover .icon-notify {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath fill='%2316306e' d='M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12l4.58-4.59z'/%3E%3C/svg%3E");
}

/* 响应式调整 */
@media (max-width: 768px) {
    .action-btn {
        padding: 6px 10px;
        font-size: 12px;
    }
}
</style>
