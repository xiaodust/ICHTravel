<template>
  <!-- 根容器 -->
  <div id="app">
    <!-- 加载指示器 -->
    <LoadingIndicator v-if="isLoading" @load-complete="isLoading = false" />

    <!-- 主内容区 -->
    <div v-else class="app-container">
      <!-- 头部导航：包含优化后的下拉框 -->
      <Header 
        :is-scrolled="isScrolled" 
        @toggle-mobile-nav="isMobileNavOpen = !isMobileNavOpen"
        :is-mobile-nav-open="isMobileNavOpen"
        @change-workshop="changeWorkshop"
        :current-workshop="currentWorkshopKey"
        @go-home="handleGoHome"
        :is-home-page="isHomePage"
      />

      <!-- 视频与评论区域 -->
      <VideoComment 
        :current-workshop="currentWorkshop"
        :like-count="likeCount"
        :is-liked="isLiked"
        :comments="comments[currentWorkshopKey] || []"
        @toggle-like="toggleLike"
        @add-comment="addComment"
      />

      <!-- 3D全景区域 -->
      <Panorama 
        :panorama-url="currentWorkshop.panoramaUrl"
        :hotspots="currentWorkshop.hotspots"
        :scale="scale"
        :current-x="currentX"
        :is-gyro-enabled="isGyroEnabled"
        @update-scale="scale = $event"
        @update-current-x="currentX = $event"
        @toggle-gyro="toggleGyro"
        @open-detail-modal="openDetailModal"
      />

      <!-- 非遗介绍区域 -->
      <IntroSection 
        :workshop="currentWorkshop"
        :is-audio-playing="isAudioPlaying"
        :audio-progress="audioProgress"
        @play-audio="playAudio"
        @pause-audio="pauseAudio"
        @seek-audio="seekAudio"
        @checkin="handleCheckin"
        @open-reserve-modal="isReserveModalOpen = true"
      />

      <!-- 非遗商品区域 -->
      <Products :products="currentWorkshop.products" @buy="handleBuy" />

      <!-- 底部信息 -->
      <Footer />

      <!-- 工艺细节模态框 -->
      <DetailModal 
        :is-open="isDetailModalOpen"
        :detail="currentDetail"
        @close="isDetailModalOpen = false"
      />

      <!-- 打卡记录模态框 -->
      <CheckinModal 
        :is-open="isCheckinModalOpen"
        :checkin-list="checkinList"
        :workshop-list="workshopList"
        @close="isCheckinModalOpen = false"
      />

      <!-- 预约模态框 -->
      <ReserveModal 
        :is-open="isReserveModalOpen"
        :workshop-name="currentWorkshop.title"
        @close="isReserveModalOpen = false"
        @submit-reserve="handleReserveSubmit"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, onUnmounted } from 'vue'

// 本地存储工具函数
const getStorage = (key) => {
  const value = localStorage.getItem(key)
  if (!value) return null
  try {
    return JSON.parse(value)
  } catch (e) {
    return value
  }
}

const setStorage = (key, value) => {
  const strValue = typeof value === 'object' ? JSON.stringify(value) : value
  localStorage.setItem(key, strValue)
}

// 加载指示器
const LoadingIndicator = {
  template: `
    <div class="loading-container">
      <div class="loading-logo">
        <i class="fa fa-university" aria-hidden="true"></i>
        <span>江苏非遗3D云游</span>
      </div>
      <div class="loading-spinner"></div>
      <p class="loading-text">正在加载非遗文化资源...</p>
      <div class="loading-progress">
        <div 
          class="progress-bar"
          :style="{ width: progress + '%' }"
        ></div>
      </div>
    </div>
  `,
  setup(props, { emit }) {
    const progress = ref(0)
    let interval = null

    onMounted(() => {
      interval = setInterval(() => {
        progress.value += Math.random() * 5
        if (progress.value >= 100) {
          clearInterval(interval)
          emit('load-complete')
        }
      }, 200)
    })

    onUnmounted(() => {
      if (interval) clearInterval(interval)
    })

    return { progress }
  }
}

// 头部导航
const Header = {
  props: ['isScrolled', 'isMobileNavOpen', 'currentWorkshop', 'isHomePage'],
  emits: ['toggle-mobile-nav', 'change-workshop', 'go-home'],
  template: `
    <header 
      class="header"
      :class="{ 'header-scrolled': isScrolled }"
    >
      <div class="container">
        <div class="header-content">
          <div class="logo">
            <i class="fa fa-university" aria-hidden="true"></i>
            <span>江苏非遗3D云游</span>
          </div>
          
          <!-- 桌面端导航：首页按钮 + 下拉选择框 -->
          <nav class="main-nav desktop-nav">
            <ul>
              <!-- 首页按钮 -->
              <li>
                <button 
                  class="home-btn" 
                  @click="$router.push('/home')"
                  :class="{ 'active': isHomePage }"
                >
                  <i class="fa fa-home"></i> 首页
                </button>
              </li>
              <!-- 非遗工坊下拉选择框 -->
              <li class="workshop-selector">
                <select 
                  v-model="selectedWorkshop"
                  @change="handleWorkshopChange"
                  class="workshop-select"
                  :class="{ 'changed': selectedWorkshop !== initialWorkshop }"
                >
                  <option value="" disabled>选择非遗工坊</option>
                  <option value="yunjin">南京云锦</option>
                  <option value="suxiu">苏州苏绣</option>
                  <option value="zisha">宜兴紫砂</option>
                  <option value="qiqi">扬州漆器</option>
                </select>
                <div class="select-indicator" :class="{ 'visible': selectedWorkshop }"></div>
              </li>
            </ul>
          </nav>
          
          <!-- 移动端菜单按钮 -->
          <button class="mobile-menu-btn" @click="$emit('toggle-mobile-nav')">
            <i class="fa" :class="isMobileNavOpen ? 'fa-times' : 'fa-bars'"></i>
          </button>
        </div>
      </div>
      
      <!-- 移动端导航菜单 -->
      <div class="mobile-nav" :class="{ open: isMobileNavOpen }">
        <ul>
          <!-- 移动端首页项 -->
          <li @click="$emit('go-home'); $emit('toggle-mobile-nav')" :class="{ 'active': isHomePage }">
            <i class="fa fa-home"></i> 首页
          </li>
          <!-- 非遗工坊列表 -->
          <li :class="{ active: currentWorkshop === 'yunjin' }" @click="$emit('change-workshop', 'yunjin'); $emit('toggle-mobile-nav')">南京云锦</li>
          <li :class="{ active: currentWorkshop === 'suxiu' }" @click="$emit('change-workshop', 'suxiu'); $emit('toggle-mobile-nav')">苏州苏绣</li>
          <li :class="{ active: currentWorkshop === 'zisha' }" @click="$emit('change-workshop', 'zisha'); $emit('toggle-mobile-nav')">宜兴紫砂</li>
          <li :class="{ active: currentWorkshop === 'qiqi' }" @click="$emit('change-workshop', 'qiqi'); $emit('toggle-mobile-nav')">扬州漆器</li>
        </ul>
      </div>
    </header>
  `,
  setup(props, { emit }) {
    // 下拉框选中值
    const selectedWorkshop = ref(props.currentWorkshop);
    const initialWorkshop = ref(props.currentWorkshop);

    // 同步下拉框选中状态
    watch(() => props.currentWorkshop, (newVal) => {
      selectedWorkshop.value = newVal;
      // 添加切换动画类
      const selectEl = document.querySelector('.workshop-select');
      if (selectEl) {
        selectEl.classList.add('transitioning');
        setTimeout(() => selectEl.classList.remove('transitioning'), 300);
      }
    });

    // 切换工坊事件
    const handleWorkshopChange = () => {
      if (selectedWorkshop.value && selectedWorkshop.value !== props.currentWorkshop) {
        emit('change-workshop', selectedWorkshop.value);
        // 滚动到顶部，增强页面切换体验
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    };

    onMounted(() => {
      // 初始化状态
      initialWorkshop.value = props.currentWorkshop;
    });

    return { selectedWorkshop, initialWorkshop, handleWorkshopChange };
  }
}

// 视频与评论
const VideoComment = {
  props: ['currentWorkshop', 'likeCount', 'isLiked', 'comments'],
  emits: ['toggle-like', 'add-comment'],
  setup() {
    const commentContent = ref('')
    
    const handleAddComment = () => {
      if (!commentContent.value.trim()) {
        alert('请输入评论内容')
        return
      }
      this.$emit('add-comment', commentContent.value)
      commentContent.value = ''
    }
    
    return { commentContent, handleAddComment }
  },
  template: `
    <section class="video-comment-section">
      <div class="container">
        <div class="video-container">
          <video 
            :src="currentWorkshop.videoUrl" 
            controls
            poster="https://picsum.photos/1200/675?random=1"
            class="main-video"
          >
            您的浏览器不支持视频播放
          </video>
          <div class="video-info">
            <h2>{{ currentWorkshop.videoTitle }}</h2>
            <p class="video-subtitle">{{ currentWorkshop.videoSubtitle }}</p>
            <div class="video-actions">
              <button 
                class="like-btn"
                :class="{ liked: isLiked }"
                @click="$emit('toggle-like')"
              >
                <i class="fa" :class="isLiked ? 'fa-heart' : 'fa-heart-o'"></i>
                <span>{{ likeCount }} 喜欢</span>
              </button>
            </div>
          </div>
        </div>
        
        <!-- 评论区 -->
        <div class="comments-section">
          <h3>用户评论</h3>
          <div class="comment-input">
            <textarea 
              v-model="commentContent"
              placeholder="分享您对这项非遗技艺的看法..."
              @keydown.enter.exact="handleAddComment"
            ></textarea>
            <button @click="handleAddComment">发表评论</button>
          </div>
          
          <div class="comments-list">
            <div 
              class="comment-item"
              v-for="(comment, index) in comments"
              :key="index"
            >
              <div class="comment-header">
                <span class="comment-author">{{ comment.author }}</span>
                <span class="comment-time">{{ comment.time }}</span>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
            </div>
          </div>
        </div>
      </div>
    </section>
  `
}

// 3D全景组件
const Panorama = {
  props: ['panoramaUrl', 'hotspots', 'scale', 'currentX', 'isGyroEnabled'],
  emits: ['update-scale', 'update-current-x', 'toggle-gyro', 'open-detail-modal'],
  setup(props, { emit }) {
    const isDragging = ref(false)
    const startX = ref(0)
    let gyroWatchId = null

    const startDrag = (e) => {
      if (props.isGyroEnabled) return
      isDragging.value = true
      const clientX = e.type.includes('touch') ? e.touches[0].clientX : e.clientX
      startX.value = clientX - props.currentX
    }

    const drag = (e) => {
      if (!isDragging.value || props.isGyroEnabled) return
      e.preventDefault()
      const clientX = e.type.includes('touch') ? e.touches[0].clientX : e.clientX
      const newX = clientX - startX.value
      
      const imgEl = document.querySelector('.panorama-img')
      if (!imgEl) return
      
      const maxX = 0
      const minX = -(imgEl.offsetWidth * props.scale - imgEl.parentElement.offsetWidth)
      const finalX = Math.max(minX, Math.min(maxX, newX))
      emit('update-current-x', finalX)
    }

    const endDrag = () => {
      isDragging.value = false
    }

    const zoomIn = () => {
      const newScale = Math.min(1.8, props.scale + 0.1)
      emit('update-scale', newScale)
    }

    const zoomOut = () => {
      const newScale = Math.max(0.7, props.scale - 0.1)
      emit('update-scale', newScale)
    }

    const resetView = () => {
      emit('update-scale', 1)
      emit('update-current-x', 0)
    }

    const toggleGyro = () => {
      if (!window.DeviceOrientationEvent) {
        alert('您的设备不支持陀螺仪功能')
        return
      }
      emit('toggle-gyro')
      
      if (!props.isGyroEnabled) {
        gyroWatchId = window.addEventListener('deviceorientation', handleGyro)
      } else {
        window.removeEventListener('deviceorientation', handleGyro)
      }
    }

    const handleGyro = (e) => {
      if (!e.gamma || !props.isGyroEnabled) return
      const imgEl = document.querySelector('.panorama-img')
      if (!imgEl) return
      
      const gammaRange = 90
      const moveRange = imgEl.offsetWidth * props.scale - imgEl.parentElement.offsetWidth
      const moveX = (e.gamma / gammaRange) * moveRange
      const finalX = Math.max(-moveRange, Math.min(0, -moveX))
      emit('update-current-x', finalX)
    }

    const handleHotspotClick = (hotspot) => {
      emit('open-detail-modal', hotspot)
    }

    onMounted(() => {
      document.addEventListener('mousemove', drag)
      document.addEventListener('mouseup', endDrag)
      document.addEventListener('touchmove', drag, { passive: false })
      document.addEventListener('touchend', endDrag)
    })

    onUnmounted(() => {
      document.removeEventListener('mousemove', drag)
      document.removeEventListener('mouseup', endDrag)
      document.removeEventListener('touchmove', drag)
      document.removeEventListener('touchend', endDrag)
      if (gyroWatchId) {
        window.removeEventListener('deviceorientation', handleGyro)
      }
    })

    return {
      isDragging,
      startDrag,
      drag,
      endDrag,
      zoomIn,
      zoomOut,
      resetView,
      toggleGyro,
      handleHotspotClick
    }
  },
  template: `
    <section class="panorama-section">
      <div class="container">
        <h2 class="section-title">3D全景漫游</h2>
        <div 
          class="panorama-container"
          @mousedown="startDrag"
          @touchstart="startDrag"
        >
          <img 
            :src="panoramaUrl"
            alt="非遗工坊全景"
            class="panorama-img"
            :style="{ 
              transform: \`translateX(\${currentX}px) scale(\${scale})\`
            }"
          >
          <div 
            v-for="(hotspot, index) in hotspots"
            :key="index"
            class="hotspot"
            :style="{
              top: hotspot.position.top,
              left: hotspot.position.left
            }"
            @click="handleHotspotClick(hotspot)"
          ></div>
        </div>
        <div class="panorama-controls">
          <button class="panorama-btn" @click="zoomIn">+</button>
          <button class="panorama-btn" @click="zoomOut">-</button>
          <button class="panorama-btn" @click="resetView">⟲</button>
          <button 
            class="panorama-btn" 
            @click="toggleGyro"
            :style="{ backgroundColor: isGyroEnabled ? 'rgba(74, 144, 226, 0.7)' : '' }"
          >⟳</button>
        </div>
      </div>
    </section>
  `
}

// 非遗介绍组件
const IntroSection = {
  props: ['workshop', 'isAudioPlaying', 'audioProgress'],
  emits: ['play-audio', 'pause-audio', 'seek-audio', 'checkin', 'open-reserve-modal'],
  template: `
    <section class="intro-section">
      <div class="container">
        <h2 class="section-title">非遗介绍</h2>
        <div class="intro-content">
          <div class="intro-text">
            <p>{{ workshop.description }}</p>
            <div class="heritage-fact">
              <h3>非遗小知识</h3>
              <p>{{ workshop.heritageFact }}</p>
            </div>
          </div>
          
          <div class="intro-sidebar">
            <div class="audio-player">
              <h3>语音导览</h3>
              <div class="audio-controls">
                <button 
                  class="play-btn"
                  @click="isAudioPlaying ? $emit('pause-audio') : $emit('play-audio')"
                >
                  <i class="fa" :class="isAudioPlaying ? 'fa-pause' : 'fa-play'"></i>
                </button>
                <div class="progress-container">
                  <div 
                    class="audio-progress-bar"
                    @click="handleAudioSeek"
                  >
                    <div 
                      class="progress-fill"
                      :style="{ width: audioProgress + '%' }"
                    ></div>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="action-buttons">
              <button class="checkin-btn" @click="$emit('checkin')">
                <i class="fa fa-map-marker"></i> 打卡参观
              </button>
              <button class="reserve-btn" @click="$emit('open-reserve-modal')">
                <i class="fa fa-calendar"></i> 预约体验
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>
  `,
  methods: {
    handleAudioSeek(e) {
      const rect = e.currentTarget.getBoundingClientRect()
      const percent = ((e.clientX - rect.left) / rect.width) * 100
      this.$emit('seek-audio', percent)
    }
  }
}

// 非遗商品组件
const Products = {
  props: ['products'],
  emits: ['buy'],
  template: `
    <section class="products-section">
      <div class="container">
        <h2 class="section-title">非遗文创</h2>
        <div class="products-grid">
          <div 
            class="product-card"
            v-for="(product, index) in products"
            :key="index"
          >
            <div class="product-image">
              <img :src="product.image" :alt="product.name">
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-price">{{ product.price }}</p>
              <p class="product-desc">{{ product.description }}</p>
              <button 
                class="buy-btn"
                @click="$emit('buy', product)"
              >
                立即购买
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>
  `
}

// 底部组件
const Footer = {
  template: `
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-logo">
            <i class="fa fa-university" aria-hidden="true"></i>
            <span>江苏非遗3D云游平台</span>
          </div>
          <div class="footer-links">
            <div class="link-group">
              <h4>关于我们</h4>
              <ul>
                <li><a href="#">平台介绍</a></li>
                <li><a href="#">联系我们</a></li>
                <li><a href="#">加入我们</a></li>
              </ul>
            </div>
            <div class="link-group">
              <h4>非遗资源</h4>
              <ul>
                <li><a href="#">非遗名录</a></li>
                <li><a href="#">传承人</a></li>
                <li><a href="#">保护中心</a></li>
              </ul>
            </div>
            <div class="link-group">
              <h4>关注我们</h4>
              <div class="social-icons">
                <a href="#"><i class="fa fa-weixin"></i></a>
                <a href="#"><i class="fa fa-weibo"></i></a>
                <a href="#"><i class="fa fa-youtube-play"></i></a>
              </div>
            </div>
          </div>
        </div>
        <div class="copyright">
          © 2023 江苏非遗3D云游平台 版权所有 | 苏ICP备12345678号
        </div>
      </div>
    </footer>
  `
}

// 工艺细节模态框
const DetailModal = {
  props: ['isOpen', 'detail'],
  emits: ['close'],
  template: `
    <div 
      class="modal-overlay"
      :class="{ 'modal-visible': isOpen }"
      @click="$emit('close')"
    >
      <div 
        class="modal-content"
        @click.stop
      >
        <div class="modal-header">
          <h3>{{ detail.title }}</h3>
          <button class="close-btn" @click="$emit('close')">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <img :src="detail.image" :alt="detail.title" class="detail-image">
          <div class="detail-text">
            <p>{{ detail.description }}</p>
            <div v-if="detail.process" class="detail-process">
              <h4>工艺流程</h4>
              <ol>
                <li v-for="(step, index) in detail.process" :key="index">{{ step }}</li>
              </ol>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
}

// 打卡记录模态框
const CheckinModal = {
  props: ['isOpen', 'checkinList', 'workshopList'],
  emits: ['close'],
  template: `
    <div 
      class="modal-overlay"
      :class="{ 'modal-visible': isOpen }"
      @click="$emit('close')"
    >
      <div 
        class="modal-content checkin-modal"
        @click.stop
      >
        <div class="modal-header">
          <h3>我的非遗打卡记录</h3>
          <button class="close-btn" @click="$emit('close')">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="checkinList.length === 0" class="no-checkin">
            <i class="fa fa-map-marker"></i>
            <p>您还没有打卡任何非遗工坊</p>
            <p>快去探索并打卡吧！</p>
          </div>
          <div v-else class="checkin-list">
            <div class="checkin-item" v-for="(item, index) in checkinList" :key="index">
              <div class="checkin-icon">
                <i class="fa fa-check-circle"></i>
              </div>
              <div class="checkin-info">
                <h4>{{ item.name }}</h4>
                <p>打卡日期：{{ item.date }}</p>
              </div>
            </div>
            <div class="checkin-stats">
              <p>已打卡 {{ checkinList.length }} / {{ Object.keys(workshopList).length }} 个非遗工坊</p>
              <div class="progress-container">
                <div class="progress-bar">
                  <div 
                    class="progress-fill"
                    :style="{ width: (checkinList.length / Object.keys(workshopList).length) * 100 + '%' }"
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
}

// 预约模态框
const ReserveModal = {
  props: ['isOpen', 'workshopName'],
  emits: ['close', 'submit-reserve'],
  setup() {
    const form = reactive({
      name: '',
      phone: '',
      date: '',
      time: '09:00-11:00',
      note: ''
    })

    onMounted(() => {
      // 设置默认日期为今天
      const today = new Date()
      form.date = today.toISOString().split('T')[0]
    })

    const handleSubmit = () => {
      // 简单验证
      if (!form.name.trim()) {
        alert('请输入您的姓名')
        return
      }
      if (!form.phone.trim() || !/^\d{11}$/.test(form.phone)) {
        alert('请输入有效的手机号码')
        return
      }
      if (!form.date) {
        alert('请选择预约日期')
        return
      }
      
      this.$emit('submit-reserve', { ...form })
    }

    return { form, handleSubmit }
  },
  template: `
    <div 
      class="modal-overlay"
      :class="{ 'modal-visible': isOpen }"
      @click="$emit('close')"
    >
      <div 
        class="modal-content reserve-modal"
        @click.stop
      >
        <div class="modal-header">
          <h3>{{ workshopName }} - 体验预约</h3>
          <button class="close-btn" @click="$emit('close')">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <form class="reserve-form">
            <div class="form-group">
              <label for="name">姓名</label>
              <input 
                type="text" 
                id="name" 
                v-model="form.name"
                placeholder="请输入您的姓名"
              >
            </div>
            <div class="form-group">
              <label for="phone">手机号码</label>
              <input 
                type="tel" 
                id="phone" 
                v-model="form.phone"
                placeholder="请输入您的手机号码"
                maxlength="11"
              >
            </div>
            <div class="form-group">
              <label for="date">预约日期</label>
              <input 
                type="date" 
                id="date" 
                v-model="form.date"
                min="2023-01-01"
              >
            </div>
            <div class="form-group">
              <label for="time">预约时段</label>
              <select id="time" v-model="form.time">
                <option value="09:00-11:00">09:00-11:00</option>
                <option value="13:00-15:00">13:00-15:00</option>
                <option value="15:30-17:30">15:30-17:30</option>
              </select>
            </div>
            <div class="form-group">
              <label for="note">备注信息</label>
              <textarea 
                id="note" 
                v-model="form.note"
                placeholder="请输入您的特殊需求或疑问（选填）"
                rows="3"
              ></textarea>
            </div>
            <div class="form-actions">
              <button type="button" class="cancel-btn" @click="$emit('close')">取消</button>
              <button type="button" class="submit-btn" @click="handleSubmit">提交预约</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `
}

// 主应用逻辑
const isLoading = ref(true)
const currentWorkshopKey = ref('yunjin')
const workshopList = ref({
  yunjin: {
    title: "南京云锦工坊",
    videoUrl: "/videos/yunjin-weaving.mp4",
    audioUrl: "/audio/yunjin-intro.mp3",
    videoTitle: "南京云锦织造工艺",
    videoSubtitle: "2人协作1天仅织5-6厘米，'寸锦寸金'的皇家技艺",
    likeCount: "1.2万",
    panoramaUrl: "https://picsum.photos/3000/1000?random=1",
    description: "南京云锦有'寸锦寸金'之称，是中国传统的丝制工艺品，有近1600年历史。因其色泽光丽灿烂，美如天上云霞而得名，至今已有1580年历史。云锦与成都的蜀锦、苏州的宋锦、广西的壮锦并称'中国四大名锦'。在古代丝织物中'锦'是代表最高技术水平的织物，而南京云锦则集历代织绵工艺艺术之大成，列中国四大名锦之首，元、明、清三朝均为皇家御用品贡品。",
    heritageFact: "2009年，南京云锦织造技艺被列入《人类非物质文化遗产代表作名录》。南京云锦的传统木机妆花手工织造技艺是中国4700多年丝绸织造史的技艺巅峰，至今无法用现代机器替代。",
    hotspots: [
      {
        id: 1,
        position: { top: '30%', left: '25%' },
        title: "大花楼木织机",
        image: "https://picsum.photos/600/400?random=10",
        description: "云锦织造使用的大花楼木织机长5.6米，高4米，宽1.4米，由两人配合操作，楼上的拽花工提花，楼下的织手织造，一天最多只能织5-6厘米。",
        process: [
          "拽花工按花纹要求提拉经线",
          "织手在下面投梭、打纬",
          "两人配合完成复杂纹样织造"
        ]
      },
      {
        id: 2,
        position: { top: '60%', left: '65%' },
        title: "云锦纹样设计",
        image: "https://picsum.photos/600/400?random=11",
        description: "云锦纹样设计融合了中国传统吉祥图案和宫廷审美，讲究'图必有意，意必吉祥'，常用龙、凤、花卉等图案，体现了中国传统文化的精髓。"
      }
    ],
    products: [
      {
        name: "云锦卷轴画",
        price: "¥680",
        image: "https://picsum.photos/400/300?random=20",
        description: "采用传统云锦工艺制作的卷轴画，图案精美，具有极高的收藏价值。"
      },
      {
        name: "云锦围巾",
        price: "¥380",
        image: "https://picsum.photos/400/300?random=21",
        description: "精选真丝底料，结合云锦工艺织成，保暖又美观，是送礼佳品。"
      },
      {
        name: "云锦书签套装",
        price: "¥128",
        image: "https://picsum.photos/400/300?random=22",
        description: "小巧精致的云锦书签，一套3枚，图案分别为龙、凤、牡丹，寓意吉祥。"
      }
    ],
    comments: [
      {
        author: "非遗爱好者",
        time: "2023-06-15 14:30",
        content: "太震撼了！云锦的织造工艺真是太复杂了，难怪被称为寸锦寸金。"
      },
      {
        author: "文化探索者",
        time: "2023-06-10 09:45",
        content: "3D全景很赞，可以近距离观察每一个细节，希望能有机会去现场看看。"
      }
    ]
  },
  suxiu: {
    title: "苏州苏绣工坊",
    videoUrl: "/videos/suxiu-embroidery.mp4",
    audioUrl: "/audio/suxiu-intro.mp3",
    videoTitle: "苏绣精细绣制技艺展示",
    videoSubtitle: "一根丝线可劈成1/128，绣制出栩栩如生的画面",
    likeCount: "0.9万",
    panoramaUrl: "https://picsum.photos/3000/1000?random=2",
    description: "苏绣是中国优秀的民族传统工艺之一，是苏州地区刺绣产品的总称，其发源地在苏州吴县一带，现已遍衍无锡、常州等地。刺绣与养蚕，缫丝分不开，所以刺绣，又称丝绣。清代确立了'苏绣、湘绣、粤绣、蜀绣'为中国四大名绣。",
    heritageFact: "2006年，苏绣被列入第一批国家级非物质文化遗产名录。苏绣中最精细的'平针绣'一根丝线可劈成1/128，其精细程度令人叹为观止。",
    hotspots: [],
    products: [],
    comments: []
  },
  zisha: {
    title: "宜兴紫砂工坊",
    videoUrl: "/videos/zisha-pottery.mp4",
    audioUrl: "/audio/zisha-intro.mp3",
    videoTitle: "宜兴紫砂壶制作全过程",
    videoSubtitle: "72道工序，纯手工打造的泡茶神器",
    likeCount: "1.5万",
    panoramaUrl: "https://picsum.photos/3000/1000?random=3",
    description: "宜兴紫砂陶制作技艺是指分布于江苏省宜兴市的一种传统制陶技艺。紫砂陶制作技艺以特产于宜兴的一种具有特殊团粒结构和双重气孔结构的紫砂泥料为原料，采用百种以上的自制工具，经过复杂步骤最终完成陶制品。",
    heritageFact: "2006年，宜兴紫砂陶制作技艺被列入第一批国家级非物质文化遗产名录。好的紫砂壶需要经过72道工序，完全手工制作，且具有良好的透气性和吸附性。",
    hotspots: [],
    products: [],
    comments: []
  },
  qiqi: {
    title: "扬州漆器工坊",
    videoUrl: "/videos/qiqi-craft.mp4",
    audioUrl: "/audio/qiqi-intro.mp3",
    videoTitle: "扬州漆器点螺工艺展示",
    videoSubtitle: "千年不褪色，用贝壳镶嵌出绚丽图案",
    likeCount: "0.7万",
    panoramaUrl: "https://picsum.photos/3000/1000?random=4",
    description: "扬州漆器是中国特色传统工艺品种之一。起源于战国，兴旺于汉唐，鼎盛于明清。其工艺齐全、技艺精湛、风格独特、驰名中外。扬州漆器生产历史悠久，早在两千多年前的汉代，就饮誉海内。",
    heritageFact: "2006年，扬州漆器髹饰技艺被列入第一批国家级非物质文化遗产名录。其中点螺工艺是扬州漆器中最具特色的品种之一，采用贝壳薄片镶嵌，千年不褪色。",
    hotspots: [],
    products: [],
    comments: []
  }
})

const currentWorkshop = ref(workshopList.value[currentWorkshopKey.value])
const isMobileNavOpen = ref(false)
const isScrolled = ref(false)
const likeCount = ref(currentWorkshop.value.likeCount)
const isLiked = ref(false)
const comments = ref(getStorage('workshopComments') || {})
const scale = ref(1)
const currentX = ref(0)
const isGyroEnabled = ref(false)
const isAudioPlaying = ref(false)
const audioProgress = ref(0)
const audio = ref(null)
const isDetailModalOpen = ref(false)
const currentDetail = ref({})
const checkinList = ref(getStorage('checkinList') || [])
const isCheckinModalOpen = ref(false)
const isReserveModalOpen = ref(false)
const isHomePage = ref(true) // 新增首页状态标识

// 初始化
onMounted(() => {
  // 初始化音频实例
  audio.value = new Audio(currentWorkshop.value.audioUrl)
  audio.value.addEventListener('timeupdate', updateAudioProgress)
  
  // 初始化评论数据
  initComments()
  
  // 监听滚动事件
  window.addEventListener('scroll', handleScroll)
  
  // 监听滚动位置，更新首页状态
  window.addEventListener('scroll', updateHomePageStatus)
})

// 监听当前工坊变化
watch(currentWorkshopKey, (newKey) => {
  currentWorkshop.value = workshopList.value[newKey]
  
  // 重置音频
  if (audio.value) {
    audio.value.pause()
    isAudioPlaying.value = false
    audioProgress.value = 0
    audio.value = new Audio(currentWorkshop.value.audioUrl)
    audio.value.addEventListener('timeupdate', updateAudioProgress)
  }
  
  // 重置点赞状态
  isLiked.value = false
  likeCount.value = currentWorkshop.value.likeCount
  
  // 更新首页状态
  updateHomePageStatus()
})

// 更新首页状态（是否在页面顶部）
const updateHomePageStatus = () => {
  isHomePage.value = window.scrollY < 50
}

// 初始化评论数据
const initComments = () => {
  for (const key in workshopList.value) {
    if (!comments.value[key]) {
      comments.value[key] = [...workshopList.value[key].comments]
    }
  }
  setStorage('workshopComments', comments.value)
}

// 切换工坊
const changeWorkshop = (key) => {
  currentWorkshopKey.value = key
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 首页按钮：滚动到顶部
const handleGoHome = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 视频点赞切换
const toggleLike = () => {
  isLiked.value = !isLiked.value
  const count = parseFloat(likeCount.value)
  if (isLiked.value) {
    likeCount.value = `${(count + 0.1).toFixed(1)}万`
  } else {
    likeCount.value = `${(count - 0.1).toFixed(1)}万`
  }
}

// 添加评论
const addComment = (content) => {
  const time = new Date().toLocaleString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit', 
    hour: '2-digit', 
    minute: '2-digit' 
  })
  const newComment = {
    author: '我',
    time,
    content
  }
  if (!comments.value[currentWorkshopKey.value]) {
    comments.value[currentWorkshopKey.value] = []
  }
  comments.value[currentWorkshopKey.value].push(newComment)
  setStorage('workshopComments', comments.value)
}

// 音频播放
const playAudio = () => {
  audio.value.play().then(() => {
    isAudioPlaying.value = true
  }).catch(() => {
    alert('请点击页面后再尝试播放（浏览器安全限制）')
  })
}

// 音频暂停
const pauseAudio = () => {
  audio.value.pause()
  isAudioPlaying.value = false
}

// 更新音频进度
const updateAudioProgress = () => {
  if (!audio.value.duration) return
  audioProgress.value = (audio.value.currentTime / audio.value.duration) * 100
  // 音频结束重置
  if (audio.value.ended) {
    isAudioPlaying.value = false
    audioProgress.value = 0
  }
}

// 音频跳转
const seekAudio = (percent) => {
  audio.value.currentTime = (percent / 100) * audio.value.duration
  audioProgress.value = percent
}

// 打开工艺细节模态框
const openDetailModal = (detail) => {
  currentDetail.value = detail
  isDetailModalOpen.value = true
}

// 处理打卡
const handleCheckin = () => {
  const isChecked = checkinList.value.some(item => item.workshop === currentWorkshopKey.value)
  if (isChecked) {
    isCheckinModalOpen.value = true
    return
  }
  
  // 添加打卡记录
  const newCheckin = {
    workshop: currentWorkshopKey.value,
    name: currentWorkshop.value.title,
    date: new Date().toLocaleDateString()
  }
  checkinList.value.push(newCheckin)
  setStorage('checkinList', checkinList.value)
  
  // 显示打卡成功动画
  const checkinToast = document.createElement('div')
  checkinToast.className = 'checkin-toast'
  checkinToast.innerHTML = `
    <i class="fa fa-check-circle"></i>
    <p>打卡成功！</p>
    <p>您已打卡 ${checkinList.value.length} 个非遗工坊</p>
  `
  document.body.appendChild(checkinToast)
  
  // 3秒后移除提示并打开打卡记录
  setTimeout(() => {
    document.body.removeChild(checkinToast)
    isCheckinModalOpen.value = true
  }, 3000)
}

// 处理商品购买
const handleBuy = (product) => {
  alert(`已加入购物车：${product.name}（${product.price}）`)
}

// 处理预约提交
const handleReserveSubmit = (form) => {
  // 保存预约记录
  const reserveList = getStorage('reserveList') || []
  reserveList.push({
    ...form,
    workshop: currentWorkshopKey.value,
    workshopName: currentWorkshop.value.title,
    reserveTime: new Date().toLocaleString()
  })
  setStorage('reserveList', reserveList)
  
  // 关闭模态框并提示
  isReserveModalOpen.value = false
  alert(`预约成功！\n您已预约【${currentWorkshop.value.title}】\n日期：${form.date} ${form.time}\n我们将在24小时内发送确认短信至 ${form.phone}`)
}

// 处理滚动事件
const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

// 切换陀螺仪
const toggleGyro = () => {
  isGyroEnabled.value = !isGyroEnabled.value
  if (isGyroEnabled.value) {
    alert('已开启陀螺仪全景漫游，请水平持握设备移动视角')
  }
}

// 组件卸载时清理
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('scroll', updateHomePageStatus)
  if (audio.value) {
    audio.value.removeEventListener('timeupdate', updateAudioProgress)
  }
})
</script>

<style >
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Arial, sans-serif;
  background-color: #f8f9fa;
  color: #333;
  line-height: 1.6;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-title {
  text-align: center;
  margin-bottom: 40px;
  color: #333;
  font-size: 32px;
  position: relative;
  padding-bottom: 15px;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 3px;
  background-color: #4A90E2;
}

/* 加载指示器样式 */
.loading-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.loading-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 30px;
  color: #4A90E2;
}

.loading-logo i {
  font-size: 40px;
}

.loading-logo span {
  font-size: 28px;
  font-weight: bold;
}

.loading-spinner {
  width: 60px;
  height: 60px;
  border: 5px solid #f0f0f0;
  border-radius: 50%;
  border-top-color: #4A90E2;
  animation: spin 1.5s ease-in-out infinite;
  margin-bottom: 20px;
}

.loading-text {
  color: #666;
  font-size: 18px;
  margin-bottom: 15px;
}

.loading-progress {
  width: 300px;
  height: 6px;
  background-color: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background-color: #4A90E2;
  transition: width 0.3s ease;
}

/* 头部导航样式 */
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  transition: all 0.3s ease;
}

.header-scrolled {
  background-color: white;
  padding: 10px 0;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #4A90E2;
  font-size: 20px;
  font-weight: bold;
}

.logo i {
  font-size: 28px;
}

.main-nav ul {
  display: flex;
  list-style: none;
  gap: 30px;
}

.main-nav a, .main-nav li {
  color: #333;
  text-decoration: none;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: color 0.3s ease;
}

.main-nav li:hover, .main-nav li.active {
  color: #4A90E2;
}

.mobile-menu-btn {
  display: none;
  background: none;
  border: none;
  font-size: 24px;
  color: #333;
  cursor: pointer;
}

/* 首页按钮样式 */
.home-btn {
  background: none;
  border: none;
  color: #333;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 10px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.home-btn:hover {
  background-color: #f0f5ff;
  color: #4A90E2;
}

.home-btn.active {
  background-color: #e6f7ff;
  color: #1890ff;
  font-weight: 600;
}

/* 下拉框容器样式 */
.workshop-selector {
  position: relative;
}

/* 下拉选择框样式 */
.workshop-select {
  padding: 8px 30px 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 16px;
  color: #333;
  cursor: pointer;
  background-color: white;
  appearance: none; /* 移除默认箭头 */
  width: 180px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);
}

/* 自定义下拉箭头指示器 */
.select-indicator {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  border-top: 5px solid #666;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.select-indicator.visible {
  opacity: 1;
}

/* 下拉框交互状态样式 */
.workshop-select:focus {
  outline: none;
  border-color: #4A90E2;
  box-shadow: 0 0 0 2px rgba(74, 144, 226, 0.2);
}

.workshop-select:hover {
  border-color: #4A90E2;
}

/* 切换时的动画效果 */
.workshop-select.transitioning {
  animation: pulse 0.3s ease;
}

/* 已选择状态样式 */
.workshop-select.changed {
  color: #4A90E2;
  font-weight: 500;
}

/* 选项样式（部分浏览器支持） */
.workshop-select option {
  padding: 8px;
  background-color: white;
  color: #333;
}

.workshop-select option:checked {
  background-color: #f0f5ff;
  color: #4A90E2;
}

.workshop-select option:hover {
  background-color: #f5f8ff;
}

/* 移动端导航菜单样式增强 */
.mobile-nav {
  background-color: white;
  border-top: 1px solid #eee;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  transition: max-height 0.3s ease, opacity 0.3s ease;
  max-height: 0;
  opacity: 0;
  overflow: hidden;
}

.mobile-nav.open {
  max-height: 500px;
  opacity: 1;
}

.mobile-nav ul {
  list-style: none;
}

.mobile-nav li {
  padding: 15px 20px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.mobile-nav li:hover {
  background-color: #f5f8ff;
  color: #4A90E2;
  padding-left: 25px;
}

.mobile-nav li.active {
  background-color: #e6f7ff;
  color: #1890ff;
  font-weight: 500;
  border-left: 3px solid #1890ff;
}

/* 视频与评论区域样式 */
.video-comment-section {
  padding: 120px 0 60px;
  background-color: white;
}

.video-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 60px;
}

.main-video {
  width: 100%;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.video-info h2 {
  font-size: 24px;
  margin-bottom: 10px;
  color: #333;
}

.video-subtitle {
  color: #666;
  margin-bottom: 20px;
  font-size: 16px;
}

.video-actions {
  display: flex;
  gap: 20px;
}

.like-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: #f5f8ff;
  color: #4A90E2;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.like-btn:hover {
  background-color: #e6edff;
}

.like-btn.liked {
  background-color: #4A90E2;
  color: white;
}

.comments-section {
  background-color: #f9f9f9;
  padding: 30px;
  border-radius: 10px;
}

.comments-section h3 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
}

.comment-input {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
}

.comment-input textarea {
  flex: 1;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  resize: none;
  font-family: inherit;
  font-size: 14px;
  height: 80px;
}

.comment-input button {
  background-color: #4A90E2;
  color: white;
  border: none;
  padding: 0 20px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  white-space: nowrap;
}

.comment-input button:hover {
  background-color: #3a80d2;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  background-color: white;
  padding: 15px;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.comment-author {
  font-weight: 600;
  color: #333;
}

.comment-time {
  color: #999;
}

.comment-content {
  color: #666;
  font-size: 15px;
}

/* 3D全景区域样式 */
.panorama-section {
  background-color: #222;
  color: white;
  padding: 80px 0;
}

.panorama-section .section-title {
  color: white;
}

.panorama-container {
  position: relative;
  width: 100%;
  height: 500px;
  overflow: hidden;
  border-radius: 10px;
  cursor: grab;
}

.panorama-container:active {
  cursor: grabbing;
}

.panorama-img {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  width: auto;
  transition: transform 0.1s ease;
}

.panorama-controls {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
}

.panorama-btn {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.panorama-btn:hover {
  background-color: rgba(255, 255, 255, 0.3);
}

.hotspot {
  position: absolute;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: rgba(74, 144, 226, 0.9);
  border: 2px solid white;
  cursor: pointer;
  z-index: 10;
  transform-origin: center;
  transition: all 0.3s ease;
  animation: pulse 2s infinite;
}

.hotspot::after {
  content: '';
  position: absolute;
  top: -8px;
  left: -8px;
  right: -8px;
  bottom: -8px;
  border-radius: 50%;
  border: 2px solid rgba(74, 144, 226, 0.5);
}

.hotspot:hover {
  animation: pulse-active 1.5s infinite;
  transform: scale(1.2);
  background-color: rgba(255, 215, 0, 0.9);
}

/* 非遗介绍区域样式 */
.intro-section {
  padding: 80px 0;
  background-color: white;
}

.intro-content {
  display: flex;
  gap: 40px;
}

.intro-text {
  flex: 2;
}

.intro-text p {
  font-size: 16px;
  line-height: 1.8;
  margin-bottom: 20px;
  color: #555;
}

.heritage-fact {
  background-color: #f5f8ff;
  border-left: 4px solid #4A90E2;
  padding: 20px;
  margin-top: 30px;
  border-radius: 0 6px 6px 0;
}

.heritage-fact h3 {
  color: #4A90E2;
  margin-bottom: 10px;
  font-size: 18px;
}

.intro-sidebar {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.audio-player {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 10px;
}

.audio-player h3 {
  margin-bottom: 15px;
  font-size: 18px;
  color: #333;
}

.audio-controls {
  display: flex;
  align-items: center;
  gap: 15px;
}

.play-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #4A90E2;
  color: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.play-btn:hover {
  background-color: #3a80d2;
}

.progress-container {
  flex: 1;
}

.audio-progress-bar {
  height: 6px;
  background-color: #ddd;
  border-radius: 3px;
  cursor: pointer;
}

.progress-fill {
  height: 100%;
  background-color: #4A90E2;
  border-radius: 3px;
  transition: width 0.1s ease;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.checkin-btn, .reserve-btn {
  padding: 15px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.3s ease;
}

.checkin-btn {
  background-color: #4A90E2;
  color: white;
}

.checkin-btn:hover {
  background-color: #3a80d2;
}

.reserve-btn {
  background-color: #f5f5f5;
  color: #333;
  border: 1px solid #ddd;
}

.reserve-btn:hover {
  background-color: #eee;
}

/* 非遗商品区域样式 */
.products-section {
  padding: 80px 0;
  background-color: #f9f9f9;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 30px;
}

.product-card {
  background-color: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.product-image {
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-info {
  padding: 20px;
}

.product-name {
  font-size: 18px;
  margin-bottom: 10px;
  color: #333;
}

.product-price {
  color: #e53e3e;
  font-weight: 600;
  margin-bottom: 10px;
  font-size: 17px;
}

.product-desc {
  color: #666;
  font-size: 14px;
  margin-bottom: 20px;
  line-height: 1.6;
}

.buy-btn {
  width: 100%;
  padding: 10px;
  background-color: #4A90E2;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  font-size: 15px;
  font-weight: 500;
}

.buy-btn:hover {
  background-color: #3a80d2;
}

/* 底部样式 */
.footer {
  background-color: #333;
  color: white;
  padding: 60px 0 30px;
}

.footer-content {
  display: flex;
  flex-direction: column;
  gap: 40px;
  margin-bottom: 40px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
  font-size: 22px;
  font-weight: bold;
}

.footer-logo i {
  font-size: 32px;
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 30px;
}

.link-group h4 {
  font-size: 18px;
  margin-bottom: 20px;
  color: #ddd;
}

.link-group ul {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.link-group a {
  color: #aaa;
  text-decoration: none;
  transition: color 0.3s ease;
}

.link-group a:hover {
  color: white;
}

.social-icons {
  display: flex;
  gap: 15px;
  margin-top: 10px;
}

.social-icons a {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #444;
  color: white;
  font-size: 18px;
  transition: background-color 0.3s ease;
}

.social-icons a:hover {
  background-color: #4A90E2;
}

.copyright {
  text-align: center;
  color: #888;
  font-size: 14px;
  padding-top: 30px;
  border-top: 1px solid #444;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.modal-visible {
  opacity: 1;
  visibility: visible;
}

.modal-content {
  background-color: white;
  border-radius: 10px;
  width: 90%;
  max-width: 800px;
  max-height: 80vh;
  overflow-y: auto;
  transform: translateY(-20px);
  transition: transform 0.3s ease;
}

.modal-visible .modal-content {
  transform: translateY(0);
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  font-size: 20px;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  color: #999;
  cursor: pointer;
  transition: color 0.3s ease;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

/* 工艺细节模态框 */
.detail-image {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
  border-radius: 6px;
  margin-bottom: 20px;
}

.detail-text p {
  margin-bottom: 20px;
  line-height: 1.8;
  color: #555;
}

.detail-process h4 {
  font-size: 18px;
  margin-bottom: 15px;
  color: #333;
}

.detail-process ol {
  padding-left: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  color: #555;
}

/* 打卡模态框 */
.checkin-modal .modal-content {
  max-width: 600px;
}

.no-checkin {
  text-align: center;
  padding: 40px 20px;
  color: #666;
}

.no-checkin i {
  font-size: 60px;
  color: #ddd;
  margin-bottom: 20px;
}

.checkin-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.checkin-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 6px;
}

.checkin-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #e6f7ed;
  color: #2a9d54;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.checkin-info h4 {
  color: #333;
  margin-bottom: 3px;
}

.checkin-info p {
  color: #666;
  font-size: 14px;
}

.checkin-stats {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

/* 预约模态框 */
.reserve-modal .modal-content {
  max-width: 500px;
}

.reserve-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 500;
  color: #333;
}

.form-group input, .form-group select, .form-group textarea {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-family: inherit;
  font-size: 14px;
}

.form-actions {
  display: flex;
  gap: 15px;
  margin-top: 10px;
}

.cancel-btn {
  flex: 1;
  padding: 10px;
  background-color: #f5f5f5;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.cancel-btn:hover {
  background-color: #eee;
}

.submit-btn {
  flex: 1;
  padding: 10px;
  background-color: #4A90E2;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.submit-btn:hover {
  background-color: #3a80d2;
}

/* 打卡提示 */
.checkin-toast {
  position: fixed;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  z-index: 9999;
  animation: slide-up 0.5s ease, slide-down 0.5s ease 2.5s forwards;
}

.checkin-toast i {
  font-size: 36px;
  color: #2a9d54;
}

.checkin-toast p {
  color: #333;
  font-size: 16px;
}

/* 动画 */
@keyframes spin {
  to { transform: rotate(360deg); }
}

@keyframes pulse {
  0% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.7; }
  100% { transform: scale(1); opacity: 1; }
}

@keyframes pulse-active {
  0% { transform: scale(1.2); opacity: 1; }
  50% { transform: scale(1.4); opacity: 0.7; }
  100% { transform: scale(1.2); opacity: 1; }
}

@keyframes slide-up {
  from { transform: translate(-50%, 100px); opacity: 0; }
  to { transform: translate(-50%, 0); opacity: 1; }
}

@keyframes slide-down {
  from { transform: translate(-50%, 0); opacity: 1; }
  to { transform: translate(-50%, 100px); opacity: 0; }
}

/* 下拉框切换动画 */
@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.02); }
  100% { transform: scale(1); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .desktop-nav {
    display: none;
  }
  
  .mobile-menu-btn {
    display: block;
  }
  
  .intro-content {
    flex-direction: column;
  }
  
  .products-grid {
    grid-template-columns: 1fr;
  }
  
  .comment-input {
    flex-direction: column;
  }
  
  .panorama-container {
    height: 300px;
  }
  
  .workshop-select {
    width: 150px;
    font-size: 14px;
    padding: 6px 25px 6px 10px;
  }
  
  .home-btn {
    font-size: 14px;
    padding: 5px 8px;
  }
}
</style>