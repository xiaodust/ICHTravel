<template>
  <div class="map-wrapper">
    <header class="map-header">
      <div class="map-header-content">
        <h1 class="map-title">江苏非遗文化地图</h1>
        <p class="map-subtitle">云锦 · 昆曲 · 苏绣 · 灯会 · 评弹</p>
        <div class="heritage-badges">
          <span class="badge badge-craft">传统技艺</span>
          <span class="badge badge-art">民间艺术</span>
          <span class="badge badge-music">传统音乐</span>
          <span class="badge badge-folk">民俗活动</span>
        </div>
      </div>
    </header>
    
    <div class="main-content">
      <!-- 中间地图区域 -->
      <div class="map-area">
        <div id="map-container">
          <div v-if="loading" class="loading">地图加载中...</div>
          <div v-if="loadError" class="error">{{ loadError }}</div>
        </div>
      </div>
      
      <!-- 右侧控制面板 -->
      <div class="control-panel">
        <!-- 图层切换 -->
        <section class="panel-section">
          <h2 class="section-title">地图图层</h2>
          <div class="layer-buttons">
            <button 
              v-for="layer in layers" 
              :key="layer.value"
              :class="['layer-btn', { active: currentLayer === layer.value }]"
              @click="changeLayer(layer.value)"
              :aria-label="`切换到${layer.label}图层`"
            >
              {{ layer.label }}
            </button>
          </div>
        </section>
        
        <!-- 路线规划 -->
        <section class="panel-section">
          <h2 class="section-title">路线规划</h2>
          <div class="route-container">
            <div class="route-input-group">
              <div class="input-with-icon">
                <input 
                  type="text" 
                  v-model="routeFrom" 
                  placeholder="输入起点"
                  class="route-input"
                  :aria-label="'起点输入框'"
                >
              </div>
              <div class="input-with-icon">
                <input 
                  type="text" 
                  v-model="routeTo" 
                  placeholder="输入终点"
                  class="route-input"
                  :aria-label="'终点输入框'"
                >
              </div>
            </div>
            <button @click="planRoute" class="route-btn primary-btn">
              规划路线
            </button>
            <p class="route-hint">提示：点击地图上的地点标记可快速设置起终点</p>
          </div>
        </section>
        
        <!-- 非遗地标 -->
        <section class="panel-section">
          <h2 class="section-title">非物质文化遗产</h2>
          <div class="heritage-container">
            <select v-model="selectedHeritageType" class="heritage-select" :aria-label="'非遗类型筛选'">
              <option value="all">全部类型</option>
              <option v-for="type in heritageTypes" :key="type.value" :value="type.value">
                {{ type.label }}
              </option>
            </select>
            <button @click="showHeritageMarkers" class="heritage-btn secondary-btn">
              显示地标
            </button>
          </div>
        </section>
      </div>
    </div>
    
    </div>
</template>

<script setup>
import { onMounted, ref, onUnmounted } from 'vue'

// 状态管理
const mapInstance = ref(null)
const loading = ref(true)
const loadError = ref(null)
const geolocationInstance = ref(null)

// 图层相关
const layers = [
  { label: '默认', value: 'default' },
  { label: '卫星', value: 'satellite' },
  { label: '道路', value: 'road' },
  { label: '夜景', value: 'night' }
]
const currentLayer = ref('default')
const layerInstances = ref({})

// 路线规划相关
const routeFrom = ref('')
const routeTo = ref('')
const currentRoute = ref(null)

// 预设地点数据
const presetLocations = [
  { name: '南京站', location: [118.79294, 32.0853], type: 'station', image: '/image/微信图片_20251010131844_42_4.jpg' },
  { name: '南京南站', location: [118.7962, 31.9968], type: 'station', image: '/image/微信图片_20251010141229_50_4.jpg' },
  { name: '夫子庙秦淮风光带', location: [118.7931, 32.0329], type: 'attraction', image: '/image/微信图片_20251010141521_51_4.jpg' },
  { name: '中山陵', location: [118.8490, 32.0617], type: 'attraction', image: '/image/62160623bd6efd38fashow.jpg' },
  { name: '拙政园（苏州）', location: [120.6446, 31.3215], type: 'attraction', image: '/image/62160623bd6efd38fashow.jpg' },
  { name: '瘦西湖（扬州）', location: [119.4379, 32.4042], type: 'attraction', image: '/image/4DB13A92315B31C38E217C83580ED86B564E3E4D_size155_w1080_h810.jpg' },
  { name: '鼋头渚（无锡）', location: [120.2099, 31.5604], type: 'attraction', image: '/image/微信图片_20251010131844_42_4.jpg' },
  { name: '南京禄口国际机场', location: [118.861, 31.993], type: 'airport', image: '/image/62160623bd6efd38fashow.jpg' }
]
const locationMarkers = ref([])

// 非遗相关
const heritageTypes = [
  { label: '传统技艺', value: 'craft' },
  { label: '民间艺术', value: 'art' },
  { label: '传统音乐', value: 'music' },
  { label: '民俗活动', value: 'folk' }
]
const selectedHeritageType = ref('all')
const heritageMarkers = ref([])

// 非遗数据（示例）
const heritageData = [
  { 
    name: '南京云锦织造技艺', 
    type: 'craft', 
    location: [118.7965, 32.0603],
    desc: '中国传统丝织工艺的巅峰代表，南京云锦具有独特的织造技艺。',
    image: '/image/62160623bd6efd38fashow.jpg'
  },
  { 
    name: '昆曲艺术（苏州）', 
    type: 'art', 
    location: [120.6196, 31.3110],
    desc: '中国古老戏曲剧种之一，发源并兴盛于苏州，被判为“百戏之祖”。',
    image: '/image/微信图片_20251010141229_50_4.jpg'
  },
  { 
    name: '苏绣技艺（苏州刺绣）', 
    type: 'craft', 
    location: [120.6230, 31.3120],
    desc: '以细腻、雅致著称的中国四大名绣之一，针法繁多、色彩丰富。',
    image: '/image/微信图片_20251010141521_51_4.jpg'
  },
  { 
    name: '扬州剪纸', 
    type: 'craft', 
    location: [119.4210, 32.3936],
    desc: '以线条流畅、构图精巧著称的传统民间剪纸艺术。',
    image: '/image/微信图片_20251010131844_42_4.jpg'
  },
  { 
    name: '惠山泥人（无锡）', 
    type: 'craft', 
    location: [120.2980, 31.5910],
    desc: '以写实与夸张相结合的传统泥塑艺术，色彩鲜艳、形象生动。',
    image: '/image/4DB13A92315B31C38E217C83580ED86B564E3E4D_size155_w1080_h810.jpg'
  },
  { 
    name: '蓝印花布印染技艺（南通）', 
    type: 'craft', 
    location: [120.8640, 32.0160],
    desc: '以植物染料与手工刻版相结合的传统印染工艺，蓝白相间清新雅致。',
    image: '/image/62160623bd6efd38fashow.jpg'
  },
  { 
    name: '秦淮灯会（南京）', 
    type: 'folk', 
    location: [118.7930, 32.0340],
    desc: '以精巧灯彩与节庆氛围著称的传统民俗活动，源远流长。',
    image: '/image/微信图片_20251010131844_42_4.jpg'
  },
  { 
    name: '苏州评弹', 
    type: 'music', 
    location: [120.6280, 31.3090],
    desc: '以说唱与弹唱结合的传统曲艺形式，曲调婉转、风味悠长。',
    image: '/image/微信图片_20251010141521_51_4.jpg'
  }
]

// 地图初始化函数
const initMap = () => {
  try {
    // 设置安全密钥
    window._AMapSecurityConfig = {
      securityJsCode: "f3483cc8214e0a74539148f543b5ea37"
    };
    
    // 检查是否已经加载了loader
    if (window.AMapLoader) {
      loadMap()
    } else {
      // 动态加载loader脚本
      const script = document.createElement('script')
      script.src = 'https://webapi.amap.com/loader.js'
      script.onload = loadMap
      script.onerror = () => {
        loading.value = false
        loadError.value = '地图加载器加载失败'
        console.error('高德地图loader加载失败')
      }
      document.head.appendChild(script)
    }
  } catch (e) {
    loading.value = false
    loadError.value = '地图初始化异常'
    console.error('地图初始化错误:', e)
  }
}

// 加载地图实例
const loadMap = () => {
  window.AMapLoader.load({
    key: "0035e805721abb4c2e32fe392aa9f290",
    version: "2.0",
    plugins: [
      'AMap.ToolBar',
      'AMap.DistrictSearch',
      'AMap.PlaceSearch',
      'AMap.Driving',
      'AMap.Heatmap',
      'AMap.CustomLayer',
      'AMap.Geolocation'
    ] // 添加必要的插件
  })
    .then((AMap) => {
      // 确保容器已渲染
      const container = document.getElementById('map-container')
      if (!container) {
        throw new Error('地图容器不存在')
      }
      
      // 创建地图实例
      mapInstance.value = new AMap.Map("map-container", {
        viewMode: '2D',
        zoom: 15,
        center: [118.796877, 32.060255],
        mapStyle: "amap://styles/whitesmoke"
      })
      
      // 添加工具栏与定位控件
      mapInstance.value.plugin(['AMap.ToolBar', 'AMap.Geolocation'], function() {
        const toolbar = new AMap.ToolBar()
        mapInstance.value.addControl(toolbar)
        
        geolocationInstance.value = new AMap.Geolocation({
          enableHighAccuracy: true,
          timeout: 10000,
          showCircle: true,
          zoomToAccuracy: true,
          buttonPosition: 'RB',
          buttonOffset: new AMap.Pixel(10, 20)
        })
        mapInstance.value.addControl(geolocationInstance.value)
        
        // 初始化时尝试定位一次，并将起点设为当前位置
        geolocationInstance.value.getCurrentPosition((status, result) => {
          if (status === 'complete') {
            const pos = result && (result.position || result.lnglat)
            if (pos) {
              const lng = pos.lng || (pos.getLng && pos.getLng())
              const lat = pos.lat || (pos.getLat && pos.getLat())
              if (lng != null && lat != null) {
                routeFrom.value = `${lng},${lat}`
                mapInstance.value.setCenter([lng, lat])
              }
            }
            console.log('定位成功', result)
          } else {
            console.warn('定位失败', result)
          }
        })
      })
      
      // 初始化图层
      initLayers(AMap)
      
      // 默认展示江苏非遗地标
      showHeritageMarkers()
      
      console.log('高德地图初始化成功')
      loading.value = false
    })
    .catch((e) => {
      loading.value = false
      loadError.value = '地图API加载失败'
      console.error('高德地图API加载失败:', e)
    })
}

// 初始化地图图层
const initLayers = (AMap) => {
  // 添加卫星图层
  layerInstances.value.satellite = new AMap.TileLayer.Satellite()
  layerInstances.value.satellite.hide()
  
  // 添加路网图层
  layerInstances.value.road = new AMap.TileLayer.RoadNet()
  layerInstances.value.road.hide()
  
  // 将所有图层添加到地图上
  for (const key in layerInstances.value) {
    mapInstance.value.add(layerInstances.value[key])
  }
  
  // 添加预设地点标记
  addPresetLocationMarkers(AMap)
}

// 添加预设地点标记
const addPresetLocationMarkers = (AMap) => {
  if (!mapInstance.value) return
  
  // 清除现有标记
  if (locationMarkers.value.length > 0) {
    mapInstance.value.remove(locationMarkers.value)
    locationMarkers.value = []
  }
  
  // 为每个预设地点创建标记
  presetLocations.forEach(location => {
    // 使用AMap内置标记样式，根据地点类型设置不同颜色的label
    const getMarkerColor = (type) => {
      switch (type) {
        case 'station': return '#e4393c' // 红色 - 火车站
        case 'attraction': return '#009933' // 绿色 - 景点
        case 'airport': return '#9900ff' // 紫色 - 机场
        case 'shopping': return '#ffcc00' // 黄色 - 购物
        default: return '#1890ff' // 蓝色 - 默认
      }
    }
    
    // 使用默认标记样式（移除emoji标签）
    const marker = new AMap.Marker({
      position: location.location,
      title: location.name,
      // 设置标记颜色
      strokeColor: getMarkerColor(location.type),
      strokeWeight: 2
    })
    
    // 创建信息窗口
    const infoWindow = new AMap.InfoWindow({
      title: location.name,
      content: `<div style="padding: 10px; max-width:260px;">
              <img src="${location.image || '/vite.svg'}" alt="${location.name}" style="width: 100%; height: auto; border-radius: 8px; margin-bottom: 8px; object-fit: cover;" />
              <p style="margin:0 0 8px 0;">${location.name}</p>
              <div style="display:flex; gap:8px;">
                <button onclick="setAsStartPoint('${location.name}');return false;" style="padding:6px 10px; border:none; border-radius:6px; background:#3a86ff; color:#fff; cursor:pointer;">设为起点</button>
                <button onclick="setAsEndPoint('${location.name}');return false;" style="padding:6px 10px; border:none; border-radius:6px; background:#80ffdb; color:#2d3748; cursor:pointer;">设为终点</button>
              </div>
            </div>`,
      offset: new AMap.Pixel(0, -30)
    })
    
    // 绑定点击事件
    marker.on('click', () => {
      infoWindow.open(mapInstance.value, marker.getPosition())
    })
    
    // 添加到地图并保存引用
    mapInstance.value.add(marker)
    locationMarkers.value.push(marker)
  })
  
  // 将信息窗口中调用的函数挂载到window对象，以便点击事件可以访问
  window.setAsStartPoint = (locationName) => {
    routeFrom.value = locationName
  }
  
  window.setAsEndPoint = (locationName) => {
    routeTo.value = locationName
  }
}

// 切换地图图层
const changeLayer = (layerType) => {
  if (!mapInstance.value) return
  
  currentLayer.value = layerType
  
  // 隐藏所有图层
  for (const key in layerInstances.value) {
    layerInstances.value[key].hide()
  }
  
  // 根据选择显示对应图层
  switch (layerType) {
    case 'satellite':
      layerInstances.value.satellite.show()
      mapInstance.value.setMapStyle('') // 清除自定义样式
      break
    case 'road':
      layerInstances.value.road.show()
      mapInstance.value.setMapStyle('')
      break
    case 'night':
      mapInstance.value.setMapStyle('amap://styles/dark')
      break
    case 'default':
    default:
      mapInstance.value.setMapStyle('amap://styles/whitesmoke')
      break
  }
}

// 路线规划
const planRoute = () => {
  if (!mapInstance.value || !routeFrom.value || !routeTo.value) return
  
  // 移除已有路线
  if (currentRoute.value) {
    mapInstance.value.remove(currentRoute.value)
    currentRoute.value = null
  }
  
  window.AMapLoader.load({
    plugins: ['AMap.Driving', 'AMap.Geocoder']
  }).then((AMap) => {
    const driving = new AMap.Driving({
      map: mapInstance.value,
      panel: ''
    })
    
    // 检查起点是否是预设地点名称
    const startLocation = presetLocations.find(loc => loc.name === routeFrom.value)
    // 检查终点是否是预设地点名称
    const endLocation = presetLocations.find(loc => loc.name === routeTo.value)
    
    // 设置起终点
    const startLngLat = startLocation 
      ? startLocation.location
      : (routeFrom.value.includes(',') 
        ? routeFrom.value.split(',').map(Number)
        : routeFrom.value)
    
    const endLngLat = endLocation
      ? endLocation.location
      : (routeTo.value.includes(',')
        ? routeTo.value.split(',').map(Number)
        : routeTo.value)
    
    // 规划驾车路线
    driving.search(startLngLat, endLngLat, (status, result) => {
      if (status === 'complete') {
        console.log('路线规划成功', result)
        // 保存当前路线实例，用于后续移除
        currentRoute.value = result.routes[0].path
        
        // 可以在这里添加成功提示
        console.log(`从 ${routeFrom.value} 到 ${routeTo.value} 的路线已规划`)
      } else {
        console.error('路线规划失败', result)
        alert('路线规划失败，请检查起终点是否正确')
      }
    })
  })
}

// 显示非遗地标
const showHeritageMarkers = () => {
  if (!mapInstance.value) return
  
  // 移除现有标记
  if (heritageMarkers.value.length > 0) {
    mapInstance.value.remove(heritageMarkers.value)
    heritageMarkers.value = []
  }
  
  // 筛选非遗数据
  const filteredData = selectedHeritageType.value === 'all'
    ? heritageData
    : heritageData.filter(item => item.type === selectedHeritageType.value)
  
  // 加载标记所需的插件
  window.AMapLoader.load({
    plugins: ['AMap.Marker', 'AMap.InfoWindow']
  }).then((AMap) => {
    // 获取非遗类型的emoji和颜色
    const getHeritageTypeInfo = (type) => {
      switch (type) {
        case 'craft':
          return { emoji: '🧵', color: '#e91e63' } // 传统技艺 - 粉色
        case 'art':
          return { emoji: '🎨', color: '#2196f3' } // 民间艺术 - 蓝色
        case 'music':
          return { emoji: '🎵', color: '#4caf50' } // 传统音乐 - 绿色
        case 'folk':
          return { emoji: '🎭', color: '#ff9800' } // 民俗活动 - 橙色
        default:
          return { emoji: '🏛️', color: '#9c27b0' } // 默认 - 紫色
      }
    }
    
    // 创建标记
    filteredData.forEach(item => {
      const heritageInfo = getHeritageTypeInfo(item.type)
      
      // 使用默认标记样式（移除emoji标签）
      const marker = new AMap.Marker({
        position: item.location,
        title: item.name,
        strokeColor: heritageInfo.color,
        strokeWeight: 2,
        animation: 'AMAP_ANIMATION_DROP' // 添加动画效果
      })
      
      // 创建信息窗口，显示清晰的非遗名称、类型与简介（移除emoji）
      const infoWindow = new AMap.InfoWindow({
        title: item.name,
        content: `
          <div style="padding: 10px; min-width: 220px; line-height: 1.5;">
            <div style="margin-bottom: 8px;">
              <div style="font-size: 16px; font-weight: 700; color: #2d3748;">${item.name}</div>
              <div style="font-size: 13px; color: ${heritageInfo.color};">${getHeritageTypeName(item.type)}</div>
            </div>
            <div style="border-top: 1px solid #eee; padding-top: 8px;">
              <p style="margin: 0; color: #555;">${item.desc}</p>
            </div>
            <div style="display:flex; gap:8px; margin-top:10px;">
              <button style="padding:6px 10px; border:none; border-radius:6px; background:#3a86ff; color:#fff; cursor:pointer;" onclick="setAsStartPoint('${item.location[0]},${item.location[1]}');return false;">设为起点</button>
              <button style="padding:6px 10px; border:none; border-radius:6px; background:#80ffdb; color:#2d3748; cursor:pointer;" onclick="setAsEndPoint('${item.location[0]},${item.location[1]}');return false;">设为终点</button>
            </div>
          </div>
        `,
        offset: new AMap.Pixel(0, -30),
        autoMove: true
      })
      
      // 绑定点击事件
      marker.on('click', () => {
        infoWindow.open(mapInstance.value, marker.getPosition())
      })
      
      // 添加到地图并保存引用
      mapInstance.value.add(marker)
      heritageMarkers.value.push(marker)
    })
    
    // 调整地图视野以显示所有标记
    if (filteredData.length > 0) {
      const positions = filteredData.map(item => item.location)
      mapInstance.value.setFitView(positions, false, [50])
    }
  })
}

// 获取非遗类型名称
const getHeritageTypeName = (typeValue) => {
  const type = heritageTypes.find(t => t.value === typeValue)
  return type ? type.label : '未知类型'
}

// 组件挂载时初始化地图
onMounted(() => {
  initMap()
})

// 组件卸载时清理资源
onUnmounted(() => {
  if (mapInstance.value) {
    // 移除路线
    if (currentRoute.value) {
      mapInstance.value.remove(currentRoute.value)
    }
    
    // 移除地标标记
    if (heritageMarkers.value.length > 0) {
      mapInstance.value.remove(heritageMarkers.value)
    }
    
    // 移除预设地点标记
    if (locationMarkers.value.length > 0) {
      mapInstance.value.remove(locationMarkers.value)
    }
    
    // 移除图层
    for (const key in layerInstances.value) {
      mapInstance.value.remove(layerInstances.value[key])
    }

    // 清理定位控件引用
    if (geolocationInstance.value) {
      geolocationInstance.value = null
    }
    
    // 清理window对象上的函数
    delete window.setAsStartPoint
    delete window.setAsEndPoint
    
    // 销毁地图实例
    mapInstance.value.destroy()
    mapInstance.value = null
  }
})
</script>

<style scoped>
/* 基础重置与全局样式 */
.map-wrapper {
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 0;
  margin: 0;
  background-color: #f8f9fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', sans-serif;
}

/* 标题栏 */
.map-header {
  background: linear-gradient(135deg, #3a86ff, #8338ec);
  color: white;
  padding: 0.75rem 0;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.map-title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
  text-align: center;
  letter-spacing: 0.5px;
}

/* 主内容区域 - 横向布局 */
.main-content {
  flex: 1;
  display: flex;
  gap: 1rem;
  padding: 1rem;
  min-height: 0; /* 解决flex子元素溢出问题 */
}

/* 中间地图区域 */
.map-area {
  flex: 1;
  position: relative;
  min-width: 0;
}

/* 右侧控制面板 */
.control-panel {
  width: 320px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  padding: 1.5rem;
  height: fit-content;
  max-height: calc(100vh - 100px);
  overflow-y: auto;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  position: sticky;
  top: 1rem;
}

.control-panel:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.08);
}

/* 面板区块 */
.panel-section {
  margin-bottom: 1.5rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #f0f0f0;
}

.panel-section:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.section-title {
  margin: 0 0 1rem 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: #2d3748;
  display: flex;
  align-items: center;
}

.section-title::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 20px;
  background: #3a86ff;
  border-radius: 2px;
  margin-right: 8px;
}

/* 图层按钮组 */
.layer-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.layer-btn {
  padding: 8px 16px;
  border: 2px solid #e2e8f0;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: #4a5568;
  transition: all 0.25s ease;
}

.layer-btn:hover {
  border-color: #3a86ff;
  color: #3a86ff;
  transform: translateY(-1px);
}

.layer-btn.active {
  background: #3a86ff;
  color: white;
  border-color: #3a86ff;
  box-shadow: 0 2px 8px rgba(58, 134, 255, 0.25);
}

/* 路线规划区域 */
.route-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.route-input-group {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.input-with-icon {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 12px;
  font-size: 18px;
  pointer-events: none;
}

.route-input {
  padding: 12px; 
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  width: 100%;
  transition: all 0.25s ease;
}

.route-input:focus {
  border-color: #3a86ff;
  outline: none;
  box-shadow: 0 0 0 3px rgba(58, 134, 255, 0.1);
}

/* 通用按钮样式 */
.primary-btn, .secondary-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.primary-btn {
  background: linear-gradient(135deg, #3a86ff, #8338ec);
  color: white;
}

.primary-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(58, 134, 255, 0.3);
}

.secondary-btn {
  background: #80ffdb;
  color: #2d3748;
}

.secondary-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(128, 255, 219, 0.3);
}

/* 路线规划提示 */
.route-hint {
  margin: 0;
  font-size: 13px;
  color: #718096;
  font-style: italic;
}

/* 非遗筛选器 */
.heritage-container {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.heritage-select {
  padding: 12px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  background: white;
  transition: all 0.25s ease;
}

.heritage-select:focus {
  border-color: #3a86ff;
  outline: none;
  box-shadow: 0 0 0 3px rgba(58, 134, 255, 0.1);
}

/* 地图容器 */
#map-container {
  width: 100%;
  height: 100%;
  min-height: 600px;
  background-color: #f8f9fa;
  border-radius: 12px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.05);
}

/* 加载和错误状态 */
.loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(255, 255, 255, 0.95);
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #4a5568;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(5px);
}

.error {
  color: #e53e3e;
  text-align: center;
  padding: 24px;
  font-size: 14px;
  background: rgba(239, 68, 68, 0.1);
  border-radius: 8px;
  margin: 1rem;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .main-content {
    flex-direction: column;
  }
  
  .control-panel {
    width: 100%;
    max-height: none;
    position: static;
  }
  
  #map-container {
    min-height: 500px;
  }
}

@media (max-width: 768px) {
  .route-input-group {
    flex-direction: column;
  }
  
  .heritage-container {
    flex-direction: column;
  }
  
  .primary-btn, .secondary-btn {
    width: 100%;
  }
  
  #map-container {
    min-height: 400px;
  }
  
  .main-content {
    padding: 0.5rem;
    gap: 0.5rem;
  }
}
</style>

<style scoped>
.map-header {
  position: relative;
  color: white;
  /* 与首页 Banner 保持一致的呈现：高度与居中布局 */
  height: 220px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  /* 改为与首页Banner一致的图片背景 */
  background-image: url('/image/微信图片_20251010141521_51_4.jpg');
  background-size: cover;
  background-position: center;
}
.map-header::after {
  content: '';
  position: absolute;
  inset: 0;
  background: none; /* 移除叠加纹理，保持与首页一致 */
  pointer-events: none;
}
.map-header-content {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 1rem;
  text-align: center;
}
.map-title {
  margin: 0;
  font-size: 1.9rem;
  font-weight: 700;
  letter-spacing: 1px;
}
.map-subtitle {
  margin: 6px 0 10px;
  font-size: 0.95rem;
  opacity: 0.92;
}
.heritage-badges {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}
.badge {
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  backdrop-filter: blur(4px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}
.badge-craft { background: rgba(233,30,99,0.15); border: 1px solid rgba(233,30,99,0.4); }
.badge-art   { background: rgba(33,150,243,0.15); border: 1px solid rgba(33,150,243,0.4); }
.badge-music { background: rgba(76,175,80,0.15); border: 1px solid rgba(76,175,80,0.4); }
.badge-folk  { background: rgba(255,152,0,0.15); border: 1px solid rgba(255,152,0,0.4); }

/* 控制面板顶部主题块 */
.panel-hero {
  background: linear-gradient(135deg, #3a86ff, #8338ec);
  color: white;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 1rem;
  box-shadow: 0 4px 18px rgba(0, 0, 0, 0.08);
}
.panel-hero-title {
  font-size: 1rem;
  font-weight: 700;
  margin-bottom: 8px;
}
.hero-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.chip {
  padding: 6px 10px;
  border: none;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
  color: #1a202c;
  background: #fff;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.chip:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}
.chip-craft { color: #e91e63; }
.chip-art   { color: #2196f3; }
.chip-music { color: #4caf50; }
.chip-folk  { color: #ff9800; }
.chip-all   { color: #6b7280; }
</style>
