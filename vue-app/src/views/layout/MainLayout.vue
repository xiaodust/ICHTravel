<template>
  <div class="app-container">
    <Header />
    <div class="main-container">
      <Sidebar />
      <main class="content">
        <router-view />
      </main>
    </div>
    
    <!-- 通用弹窗 -->
    <div 
      v-if="modalVisible"
      class="modal-backdrop"
      @click="handleModalClose"
    >
      <div 
        class="modal"
        @click.stop
      >
        <div class="modal-header">
          <h3 class="modal-title">{{ modalTitle }}</h3>
          <button @click="handleModalClose" class="modal-close">&times;</button>
        </div>
        
        <div class="modal-body">
          <div v-html="modalContent"></div>
          
          <!-- 星级评分（评价专用） -->
          <div v-if="modalType === 'review'">
            <div class="rating">
              <span 
                v-for="i in 5" 
                :key="i"
                @click="setRating(i)"
                class="star"
                :style="{ color: i <= currentRating ? '#FF9800' : '#CCCCCC' }"
              >★</span>
            </div>
          </div>
        </div>
        
        <div v-if="modalShowFooter" class="modal-footer">
          <button @click="handleModalClose" class="btn btn-outline">取消</button>
          <button @click="handleModalConfirm" class="btn btn-primary">确认</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, provide } from 'vue';
import Header from './Header.vue';
import Sidebar from './Sidebar.vue';

// 弹窗状态 - 提供给子组件使用
const modalVisible = ref(false);
const modalTitle = ref('');
const modalContent = ref('');
const modalShowFooter = ref(true);
const modalType = ref('');
const modalCallback = ref(null);
const currentRating = ref(5);

// 提供弹窗相关方法给子组件
provide('modal', {
  open: (title, content, type = '', showFooter = true, callback = null) => {
    modalTitle.value = title;
    modalContent.value = content;
    modalType.value = type;
    modalShowFooter.value = showFooter;
    modalCallback.value = callback;
    modalVisible.value = true;
  },
  close: handleModalClose
});

// 关闭弹窗
function handleModalClose() {
  modalVisible.value = false;
  currentRating.value = 5; // 重置评分
}

// 确认弹窗
function handleModalConfirm() {
  if (modalCallback.value) {
    modalCallback.value();
  }
  handleModalClose();
}

// 设置评分
function setRating(rating) {
  currentRating.value = rating;
}
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  background-color: #f5f7fa;
  color: #333;
}

.main-container {
  display: flex;
  min-height: calc(100vh - 60px);
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

/* 弹窗样式 */
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
  max-width: 500px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  animation: modalFadeIn 0.3s ease;
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
  font-size: 18px;
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
  max-height: 400px;
  overflow-y: auto;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #eee;
}

/* 评分样式 */
.rating {
  margin: 16px 0;
}

.star {
  font-size: 24px;
  cursor: pointer;
  margin-right: 4px;
}
</style>