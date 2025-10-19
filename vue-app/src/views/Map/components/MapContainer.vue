<script setup>
import { onMounted, onUnmounted, ref } from "vue";
import AMapLoader from "@amap/amap-jsapi-loader";

let map = null;
const loading = ref(true);
const errorMsg = ref('');

onMounted(() => {
  console.log('开始加载地图...');
  
  // 确保容器有高度
  const container = document.getElementById('container');
  if (container) {
    console.log('地图容器已找到，尺寸:', container.offsetWidth, 'x', container.offsetHeight);
  } else {
    console.error('未找到地图容器!');
    errorMsg.value = '未找到地图容器';
    loading.value = false;
    return;
  }
  
  AMapLoader.load({
    key: "0035e805721abb4c2e32fe392aa9f290", // 申请好的Web端开发者Key
    version: "2.0", 
    plugins: [], 
  })
    .then((AMap) => {
      console.log('高德地图API加载成功');
      try {
        map = new AMap.Map("container", {
          viewMode: "3D",
          zoom: 11,
          center: [118.899714, 31.903970],
        });
        console.log('地图实例创建成功');
        loading.value = false;
      } catch (mapError) {
        console.error('地图创建失败:', mapError);
        errorMsg.value = '地图初始化失败';
        loading.value = false;
      }
    })
    .catch((e) => {
      console.error('高德地图API加载失败:', e);
      errorMsg.value = '地图API加载失败';
      loading.value = false;
    });
});

onUnmounted(() => {
  console.log('销毁地图实例');
  map?.destroy();
});
</script>

<template>
  <div class="map-wrapper">
    <div v-if="loading" class="loading-tip">地图加载中...</div>
    <div v-else-if="errorMsg" class="error-tip">{{ errorMsg }}</div>
    <div id="container"></div>
  </div>
</template>

<style>
/* 确保外层容器也有样式 */
.map-wrapper {
  position: relative;
  width: 100%;
  height: 800px;
}

#container {
  width: 100%;
  height: 100%;
  background-color: #f0f0f0;
}

.loading-tip, .error-tip {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  padding: 10px 20px;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  border-radius: 4px;
  z-index: 9999;
}

.error-tip {
  background-color: rgba(255, 0, 0, 0.7);
}
</style>
