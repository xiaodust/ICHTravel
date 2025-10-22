<template>
  <section class="settings-container">
    <div class="settings-header">
      <h2 class="settings-title">设置中心</h2>
      <div class="user-profile-preview">
        <img 
          :src="userInfo.avatar" 
          alt="用户头像" 
          class="avatar-img"
          @error="userInfo.avatar = defaultAvatar"
        >
        <div class="user-info">
          <h3 class="username">{{ userInfo.nickname }}</h3>
          <p class="user-id">ID: {{ userInfo.userId }}</p>
        </div>
      </div>
    </div>

    <!-- 设置选项卡 -->
    <div class="settings-tabs">
      <button 
        v-for="tab in settingsTabs" 
        :key="tab.value"
        @click="activeTab = tab.value"
        class="tab-button"
        :class="{ active: activeTab === tab.value }"
      >
        <svg :viewBox="tab.icon.viewBox" width="18" height="18" class="tab-icon">
          <path :d="tab.icon.path" fill="currentColor"></path>
        </svg>
        <span class="tab-label">{{ tab.label }}</span>
      </button>
    </div>

    <!-- 个人信息设置 -->
    <div v-if="activeTab === 'profile'" class="settings-content">
      <form class="profile-form">
        <div class="form-group">
          <label class="form-label">头像</label>
          <div class="avatar-upload">
            <img 
              :src="userInfo.avatar" 
              alt="当前头像" 
              class="current-avatar"
              @error="userInfo.avatar = defaultAvatar"
            >
            <div class="upload-actions">
              <button type="button" class="btn btn-outline" @click="handleAvatarUpload">
                更换头像
              </button>
              <input type="file" class="avatar-file" accept="image/*" ref="avatarInput" @change="handleAvatarChange">
            </div>
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">昵称</label>
          <input 
            type="text" 
            v-model="userInfo.nickname" 
            class="form-control"
            maxlength="16"
          >
          <p class="form-hint">最多16个字符，可包含中英文、数字和下划线</p>
        </div>

        <div class="form-group">
          <label class="form-label">性别</label>
          <div class="radio-group">
            <label class="radio-item">
              <input type="radio" name="gender" value="male" v-model="userInfo.gender">
              <span>男</span>
            </label>
            <label class="radio-item">
              <input type="radio" name="gender" value="female" v-model="userInfo.gender">
              <span>女</span>
            </label>
            <label class="radio-item">
              <input type="radio" name="gender" value="secret" v-model="userInfo.gender">
              <span>保密</span>
            </label>
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">个人简介</label>
          <textarea 
            v-model="userInfo.bio" 
            class="form-control form-textarea"
            rows="3"
            maxlength="100"
          ></textarea>
          <p class="form-hint">{{ userInfo.bio.length }}/100 字符</p>
        </div>

        <div class="form-group">
          <label class="form-label">地区</label>
          <div class="region-selector">
            <select v-model="userInfo.province" class="form-control region-select" @change="loadCities">
              <option value="">选择省份</option>
              <option v-for="province in provinces" :value="province">{{ province }}</option>
            </select>
            <select v-model="userInfo.city" class="form-control region-select">
              <option value="">选择城市</option>
              <option v-for="city in cities" :value="city">{{ city }}</option>
            </select>
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="btn btn-cancel" @click="resetProfile">取消</button>
          <button type="button" class="btn btn-primary" @click="saveProfile">保存修改</button>
        </div>
      </form>
    </div>

    <!-- 账号安全设置 -->
    <div v-if="activeTab === 'security'" class="settings-content">
      <div class="security-item">
        <div class="item-info">
          <h3 class="item-title">手机号码</h3>
          <p class="item-desc">{{ userInfo.phone ? formatPhone(userInfo.phone) : '未绑定' }}</p>
        </div>
        <button class="item-action" @click="handleBindPhone">
          {{ userInfo.phone ? '更换' : '绑定' }}
        </button>
      </div>

      <div class="security-item">
        <div class="item-info">
          <h3 class="item-title">电子邮箱</h3>
          <p class="item-desc">{{ userInfo.email ? userInfo.email : '未绑定' }}</p>
        </div>
        <button class="item-action" @click="handleBindEmail">
          {{ userInfo.email ? '更换' : '绑定' }}
        </button>
      </div>

      <div class="security-item">
        <div class="item-info">
          <h3 class="item-title">登录密码</h3>
          <p class="item-desc">设置密码以保护账号安全</p>
        </div>
        <button class="item-action" @click="handleChangePassword">修改</button>
      </div>

      <div class="security-item">
        <div class="item-info">
          <h3 class="item-title">第三方账号绑定</h3>
          <p class="item-desc">微信、QQ、微博账号绑定状态</p>
        </div>
        <button class="item-action" @click="handleThirdPartyBind">管理</button>
      </div>
    </div>

    <!-- 通知设置 -->
    <div v-if="activeTab === 'notifications'" class="settings-content">
      <div class="notification-group">
        <h3 class="group-title">非遗活动通知</h3>
        <div class="notification-item">
          <div class="item-info">
            <h4 class="item-title">活动开始提醒</h4>
            <p class="item-desc">预约的非遗活动开始前提醒</p>
          </div>
          <toggle-switch v-model="notificationSettings.eventReminder"></toggle-switch>
        </div>
        <div class="notification-item">
          <div class="item-info">
            <h4 class="item-title">新活动推荐</h4>
            <p class="item-desc">基于您的兴趣推荐新活动</p>
          </div>
          <toggle-switch v-model="notificationSettings.newEvents"></toggle-switch>
        </div>
      </div>

      <div class="notification-group">
        <h3 class="group-title">系统通知</h3>
        <div class="notification-item">
          <div class="item-info">
            <h4 class="item-title">账号安全提醒</h4>
            <p class="item-desc">账号登录、信息修改等安全通知</p>
          </div>
          <toggle-switch v-model="notificationSettings.securityAlerts" :disabled="true"></toggle-switch>
        </div>
        <div class="notification-item">
          <div class="item-info">
            <h4 class="item-title">勋章解锁通知</h4>
            <p class="item-desc">获得新勋章时收到通知</p>
          </div>
          <toggle-switch v-model="notificationSettings.medalUnlocked"></toggle-switch>
        </div>
      </div>

      <div class="notification-group">
        <h3 class="group-title">消息通知</h3>
        <div class="notification-item">
          <div class="item-info">
            <h4 class="item-title">他人回复通知</h4>
            <p class="item-desc">收到评论或回复时通知</p>
          </div>
          <toggle-switch v-model="notificationSettings.replies"></toggle-switch>
        </div>
        <div class="notification-item">
          <div class="item-info">
            <h4 class="item-title">好友请求通知</h4>
            <p class="item-desc">有人添加您为好友时通知</p>
          </div>
          <toggle-switch v-model="notificationSettings.friendRequests"></toggle-switch>
        </div>
      </div>

      <button class="btn btn-primary save-notifications" @click="saveNotifications">
        保存通知设置
      </button>
    </div>

    <!-- 隐私设置 -->
    <div v-if="activeTab === 'privacy'" class="settings-content">
      <div class="privacy-item">
        <div class="item-info">
          <h3 class="item-title">个人资料可见性</h3>
          <p class="item-desc">控制谁可以查看您的个人资料</p>
        </div>
        <select v-model="privacySettings.profileVisibility" class="form-select">
          <option value="public">所有人可见</option>
          <option value="friends">仅好友可见</option>
          <option value="private">仅自己可见</option>
        </select>
      </div>

      <div class="privacy-item">
        <div class="item-info">
          <h3 class="item-title">我的预约可见性</h3>
          <p class="item-desc">控制谁可以查看您的非遗活动预约</p>
        </div>
        <select v-model="privacySettings.appointmentVisibility" class="form-select">
          <option value="public">所有人可见</option>
          <option value="friends">仅好友可见</option>
          <option value="private">仅自己可见</option>
        </select>
      </div>

      <div class="privacy-item">
        <div class="item-info">
          <h3 class="item-title">允许陌生人私信</h3>
          <p class="item-desc">是否接收未添加好友的用户发来的消息</p>
        </div>
        <toggle-switch v-model="privacySettings.allowStrangerMessages"></toggle-switch>
      </div>

      <div class="privacy-item">
        <div class="item-info">
          <h3 class="item-title">活动推荐个性化</h3>
          <p class="item-desc">基于您的行为数据提供个性化推荐</p>
        </div>
        <toggle-switch v-model="privacySettings.personalizedRecommendations"></toggle-switch>
      </div>

      <button class="btn btn-primary save-privacy" @click="savePrivacy">
        保存隐私设置
      </button>
    </div>

    <!-- 关于设置 -->
    <div v-if="activeTab === 'about'" class="settings-content">
      <div class="about-item">
        <h3 class="item-title">版本信息</h3>
        <p class="item-value">v{{ appVersion }}</p>
      </div>

      <div class="about-item">
        <h3 class="item-title">检查更新</h3>
        <p class="item-value" v-if="checkingUpdate">检查中...</p>
        <p class="item-value" v-else-if="hasUpdate">有新版本可用</p>
        <p class="item-value" v-else>当前已是最新版本</p>
        <button 
          class="item-action" 
          @click="checkForUpdates"
          :disabled="checkingUpdate"
        >
          检查更新
        </button>
      </div>

      <div class="about-item">
        <h3 class="item-title">用户协议</h3>
        <button class="item-action" @click="openAgreement">查看</button>
      </div>

      <div class="about-item">
        <h3 class="item-title">隐私政策</h3>
        <button class="item-action" @click="openPrivacyPolicy">查看</button>
      </div>

      <div class="about-item">
        <h3 class="item-title">联系客服</h3>
        <button class="item-action" @click="contactSupport">联系我们</button>
      </div>

      <button class="btn btn-danger logout-btn" @click="handleLogout">
        退出登录
      </button>
    </div>
  </section>
</template>

<script setup>
import { ref, reactive, toRefs, onMounted, inject } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

// 注入弹窗工具
const modal = inject('modal');

// 路由实例
const router = useRouter();

// 默认头像
const defaultAvatar = 'https://picsum.photos/id/1005/200/200';

// 设置选项卡
const settingsTabs = [
  {
    label: '个人信息',
    value: 'profile',
    icon: {
      viewBox: '0 0 24 24',
      path: 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z'
    }
  },
  {
    label: '账号安全',
    value: 'security',
    icon: {
      viewBox: '0 0 24 24',
      path: 'M12 17c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm6-9h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zM8.9 6c0-1.71 1.39-3.1 3.1-3.1s3.1 1.39 3.1 3.1v2H8.9V6zM18 20H6V10h12v10z'
    }
  },
  {
    label: '通知设置',
    value: 'notifications',
    icon: {
      viewBox: '0 0 24 24',
      path: 'M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5H5v2h14v-2h-1zm-2 1H8v-6c0-2.48 1.51-4.5 4-4.5s4 2.02 4 4.5v6z'
    }
  },
  {
    label: '隐私设置',
    value: 'privacy',
    icon: {
      viewBox: '0 0 24 24',
      path: 'M12 1C8.13 1 5 4.13 5 8c0 2.38 1.19 4.47 3 5.74V17c0 1.1.9 2 2 2h4c1.1 0 2-.9 2-2v-3.26c1.81-1.27 3-3.36 3-5.74 0-3.87-3.13-7-7-7zm0 2c2.76 0 5 2.24 5 5s-2.24 5-5 5-5-2.24-5-5 2.24-5 5-5z'
    }
  },
  {
    label: '关于',
    value: 'about',
    icon: {
      viewBox: '0 0 24 24',
      path: 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 17h-2v-2h2v2zm0-4h-2V7h2v8z'
    }
  }
];

// 省份数据（简化版）
const provinces = [
  '北京市', '上海市', '广东省', '江苏省', '浙江省', 
  '四川省', '山东省', '河南省', '湖北省', '湖南省'
];

// 城市数据（简化版）
const cityMap = {
  '北京市': ['北京市'],
  '上海市': ['上海市'],
  '广东省': ['广州市', '深圳市', '佛山市', '东莞市'],
  '江苏省': ['南京市', '苏州市', '无锡市', '常州市'],
  '浙江省': ['杭州市', '宁波市', '温州市', '绍兴市'],
  '四川省': ['成都市', '绵阳市', '德阳市', '宜宾市'],
  '山东省': ['济南市', '青岛市', '烟台市', '潍坊市'],
  '河南省': ['郑州市', '洛阳市', '开封市', '安阳市'],
  '湖北省': ['武汉市', '宜昌市', '襄阳市', '荆州市'],
  '湖南省': ['长沙市', '岳阳市', '株洲市', '衡阳市']
};

// 用户信息
const userInfo = reactive({
  userId: '',
  nickname: '非遗爱好者',
  avatar: 'https://q8.itc.cn/q_70/images03/20250304/f5873423f8b044d78aa8cf036bc132e0.jpeg',
  gender: 'secret',
  bio: '',
  phone: '',
  email: '',
  province: '',
  city: ''
});

// 保存用户信息副本用于重置
const originalUserInfo = { ...JSON.parse(JSON.stringify(userInfo)) };

// 通知设置
const notificationSettings = reactive({
  eventReminder: true,
  newEvents: true,
  securityAlerts: true,
  medalUnlocked: true,
  replies: true,
  friendRequests: true
});

// 隐私设置
const privacySettings = reactive({
  profileVisibility: 'friends',
  appointmentVisibility: 'friends',
  allowStrangerMessages: false,
  personalizedRecommendations: true
});

// 应用信息
const appVersion = '2.3.0';
const checkingUpdate = ref(false);
const hasUpdate = ref(false);

// 状态管理
const activeTab = ref('profile');
const cities = ref([]);
const { province, city } = toRefs(userInfo);

// 从后端获取用户信息
const fetchUserInfo = async () => {
  try {
    // 优先从sessionStorage获取userId
    let userId = sessionStorage.getItem('userId');
    
    // 如果sessionStorage中没有，再从localStorage获取
    if (!userId) {
      const savedUserInfo = localStorage.getItem('userInfo');
      if (savedUserInfo) {
        const parsedUserInfo = JSON.parse(savedUserInfo);
        userId = parsedUserInfo.id;
      }
    }
    
    if (!userId) {
      console.error('未找到用户ID，无法获取用户信息');
      return;
    }
    
    // 调用后端接口获取用户信息
    const response = await axios.get(`http://localhost:8080/api/user/${userId}`);
    
    if (response.data && response.data.success) {
      const userData = response.data.data;
      
      // 更新用户信息
      // 优先使用nickName，其次使用name
      if (userData.nickName) {
        userInfo.nickname = userData.nickName;
      } else if (userData.name) {
        userInfo.nickname = userData.name;
      }
      
      // 更新手机号 - 使用number字段
      if (userData.number) {
        userInfo.phone = userData.number;
      }
      
      // 更新个人签名 - 使用signature字段
      if (userData.signature) {
        userInfo.bio = userData.signature;
      }
      
      // 更新头像
      if (userData.avatar) {
        userInfo.avatar = userData.avatar;
      }
      
      // 更新ID
      userInfo.userId = userData.id || userId;
      
      // 将最新的用户信息保存到localStorage
      localStorage.setItem('userInfo', JSON.stringify(userData));
    }
  } catch (error) {
    console.error('获取用户信息时发生错误：', error);
    // 如果API请求失败，尝试从localStorage加载
    tryLoadFromLocalStorage();
  }
};

// 从localStorage加载用户信息
const tryLoadFromLocalStorage = () => {
  try {
    const savedUserInfo = localStorage.getItem('userInfo');
    if (savedUserInfo) {
      const userData = JSON.parse(savedUserInfo);
      
      // 更新用户信息
      if (userData.nickName) {
        userInfo.nickname = userData.nickName;
      } else if (userData.name) {
        userInfo.nickname = userData.name;
      }
      
      if (userData.number) {
        userInfo.phone = userData.number;
      }
      
      if (userData.signature) {
        userInfo.bio = userData.signature;
      }
      
      if (userData.avatar) {
        userInfo.avatar = userData.avatar;
      }
      
      userInfo.userId = userData.id || userInfo.userId;
    }
  } catch (error) {
    console.error('从localStorage加载用户信息失败：', error);
  }
};

// 加载城市列表
const loadCities = () => {
  cities.value = province.value ? cityMap[province.value] || [] : [];
  if (cities.value.length && !cities.value.includes(city.value)) {
    city.value = '';
  }
};

// 格式化手机号
const formatPhone = (phone) => {
  return phone.replace(/(\d{3})(\d{4})(\d{4})/, '$1****$3');
};

// 头像上传处理
const avatarInput = ref(null);
const handleAvatarUpload = () => {
  avatarInput.value.click();
};

const handleAvatarChange = (e) => {
  const file = e.target.files[0];
  if (file) {
    // 模拟上传成功
    const reader = new FileReader();
    reader.onload = (event) => {
      userInfo.avatar = event.target.result;
      modal.toast('头像上传成功');
    };
    reader.readAsDataURL(file);
  }
};

// 保存个人资料 - 调用后端update接口
const saveProfile = async () => {
  try {
    // 从sessionStorage获取userId（与后端保持一致）
    const userId = sessionStorage.getItem('userId');
    
    if (!userId) {
      throw new Error('用户身份验证失败，请重新登录');
    }
    
    // 调用后端update接口 - 使用POST方法，与后端@PostMapping注解匹配
    const response = await axios.post('http://localhost:8080/api/user/update', {
      userId: userId,
      nickName: userInfo.nickname,  // 使用正确的参数名nickName
      signature: userInfo.bio       // 使用bio作为signature参数值
    }, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      transformRequest: [(data) => {
        let ret = '';
        for (let it in data) {
          ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&';
        }
        return ret.slice(0, -1);
      }]
    });
    
    if (response.data && response.data.success) {
      // 更新本地存储中的用户信息
      const savedUserInfo = localStorage.getItem('userInfo');
      if (savedUserInfo) {
        const parsedUserInfo = JSON.parse(savedUserInfo);
        parsedUserInfo.nickName = userInfo.nickname;
        parsedUserInfo.signature = userInfo.bio;
        if (userInfo.avatar) {
          parsedUserInfo.avatar = userInfo.avatar;
        }
        localStorage.setItem('userInfo', JSON.stringify(parsedUserInfo));
      }
      
      alert('个人信息保存成功');
      // 更新原始数据
      Object.assign(originalUserInfo, { ...JSON.parse(JSON.stringify(userInfo)) });
    } else {
      throw new Error('更新失败：' + (response.data?.message || '未知错误'));
    }
  } catch (error) {
    console.error('更新个人信息时发生错误：', error);
    alert('更新失败：' + error.message);
  }
};

// 重置个人资料
const resetProfile = () => {
  Object.assign(userInfo, originalUserInfo);
  modal.toast('已重置为修改前状态');
};

// 保存通知设置
const saveNotifications = () => {
  modal.toast('通知设置保存成功');
};

// 保存隐私设置
const savePrivacy = () => {
  modal.toast('隐私设置保存成功');
};

// 检查更新
const checkForUpdates = () => {
  checkingUpdate.value = true;
  // 模拟检查更新
  setTimeout(() => {
    checkingUpdate.value = false;
    hasUpdate.value = Math.random() > 0.7; // 70%概率没有更新
    if (hasUpdate.value) {
      modal.open('发现新版本', '有新版本v2.4.0可用，是否立即更新？', 'confirm', true, () => {
        modal.toast('开始下载更新...');
      });
    }
  }, 1500);
};

// 账号安全相关操作
const handleBindPhone = () => {
  modal.open(
    userInfo.phone ? '更换手机号码' : '绑定手机号码',
    `
      <div class="form-group">
        <label>手机号码</label>
        <input type="tel" class="form-control" placeholder="请输入手机号码">
      </div>
      <div class="form-group">
        <label>验证码</label>
        <div class="verify-code">
          <input type="text" class="form-control" placeholder="请输入验证码">
          <button class="btn btn-outline">获取验证码</button>
        </div>
      </div>
    `,
    'confirm',
    true,
    () => {
      modal.toast(`${userInfo.phone ? '更换' : '绑定'}手机号码成功`);
    }
  );
};

const handleBindEmail = () => {
  modal.open(
    userInfo.email ? '更换电子邮箱' : '绑定电子邮箱',
    `
      <div class="form-group">
        <label>电子邮箱</label>
        <input type="email" class="form-control" placeholder="请输入电子邮箱">
      </div>
      <div class="form-group">
        <label>验证码</label>
        <div class="verify-code">
          <input type="text" class="form-control" placeholder="请输入验证码">
          <button class="btn btn-outline">获取验证码</button>
        </div>
      </div>
    `,
    'confirm',
    true,
    () => {
      modal.toast(`${userInfo.email ? '更换' : '绑定'}电子邮箱成功`);
    }
  );
};

const handleChangePassword = () => {
  modal.open(
    '修改密码',
    `
      <div class="form-group">
        <label>原密码</label>
        <input type="password" class="form-control" placeholder="请输入原密码">
      </div>
      <div class="form-group">
        <label>新密码</label>
        <input type="password" class="form-control" placeholder="请输入新密码">
      </div>
      <div class="form-group">
        <label>确认新密码</label>
        <input type="password" class="form-control" placeholder="请再次输入新密码">
      </div>
    `,
    'confirm',
    true,
    () => {
      modal.toast('密码修改成功，请重新登录');
    }
  );
};

const handleThirdPartyBind = () => {
  modal.open(
    '第三方账号绑定',
    `
      <div class="third-party-list">
        <div class="third-party-item">
          <span class="platform-name">微信</span>
          <button class="bind-btn">已绑定</button>
        </div>
        <div class="third-party-item">
          <span class="platform-name">QQ</span>
          <button class="bind-btn">绑定</button>
        </div>
        <div class="third-party-item">
          <span class="platform-name">微博</span>
          <button class="bind-btn">绑定</button>
        </div>
      </div>
    `
  );
};

// 关于页面相关操作
const openAgreement = () => {
  modal.open('用户协议', '<div class="agreement-content"><p>非遗探索平台用户服务协议...</p><p>1. 服务条款...</p><p>2. 用户权利与义务...</p></div>', 'text');
};

const openPrivacyPolicy = () => {
  modal.open('隐私政策', '<div class="privacy-content"><p>非遗探索平台隐私政策...</p><p>1. 信息收集与使用...</p><p>2. 信息保护措施...</p></div>', 'text');
};

const contactSupport = () => {
  modal.open('联系客服', `
    <div class="contact-info">
      <p>客服电话：400-123-4567</p>
      <p>工作时间：9:00-18:00（周一至周日）</p>
      <p>邮箱：support@feiyi.com</p>
    </div>
  `);
};

const handleLogout = async () => {
  try {
    // 清理本地登录状态
    localStorage.removeItem('userInfo');
    sessionStorage.removeItem('userId');
    
    // 尝试调用后端登出接口（可选）
    try {
      await axios.get('http://localhost:8080/api/user/logout');
    } catch (error) {
      console.log('后端登出接口调用失败，但不影响前端登出');
    }
    
    alert('登出成功');
    // 退出后跳转到主页
    router.push('/home');
  } catch (error) {
    console.error('登出处理异常:', error);
    alert('登出失败，请重试');
  }
};

// 组件挂载时加载城市和用户信息
onMounted(async () => {
  loadCities();
  // 从后端或localStorage获取用户信息
  await fetchUserInfo();
  // 更新原始数据用于重置
  Object.assign(originalUserInfo, { ...JSON.parse(JSON.stringify(userInfo)) });
});
</script>

<style scoped>
/* 基础样式 */
.settings-container {
  padding: 20px;
  background-color: #f9f9f9;
  min-height: calc(100vh - 60px);
}

.settings-header {
  margin-bottom: 30px;
}

.settings-title {
  margin: 0 0 20px 0;
  font-size: 22px;
  color: #333;
}

/* 用户信息预览 */
.user-profile-preview {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.avatar-img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f0f0;
}

.user-info .username {
  margin: 0 0 5px 0;
  font-size: 18px;
  color: #333;
}

.user-info .user-id {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* 选项卡样式 */
.settings-tabs {
  display: flex;
  gap: 5px;
  margin-bottom: 25px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  overflow-x: auto;
}

.tab-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background-color: transparent;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.tab-button.active {
  color: #1E90FF;
  background-color: rgba(30, 144, 255, 0.1);
}

.tab-button:hover:not(.active) {
  color: #333;
  background-color: #f0f0f0;
}

.tab-icon {
  flex-shrink: 0;
}

/* 设置内容区域 */
.settings-content {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

/* 表单样式 */
.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.form-control {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-control:focus {
  outline: none;
  border-color: #1E90FF;
}

.form-textarea {
  resize: vertical;
}

.form-hint {
  margin: 5px 0 0 0;
  font-size: 12px;
  color: #999;
}

/* 头像上传 */
.avatar-upload {
  display: flex;
  align-items: center;
  gap: 15px;
}

.current-avatar {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  object-fit: cover;
}

.avatar-file {
  display: none;
}

/* 性别选择 */
.radio-group {
  display: flex;
  gap: 20px;
}

.radio-item {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
}

/* 地区选择 */
.region-selector {
  display: flex;
  gap: 10px;
}

.region-select {
  flex: 1;
}

/* 表单按钮 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 30px;
}

.btn {
  padding: 10px 20px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-outline {
  background-color: transparent;
  border: 1px solid #1E90FF;
  color: #1E90FF;
}

.btn-outline:hover {
  background-color: rgba(30, 144, 255, 0.1);
}

.btn-primary {
  background-color: #1E90FF;
  color: white;
  border: none;
}

.btn-primary:hover {
  background-color: #0b7dda;
}

.btn-cancel {
  background-color: transparent;
  border: 1px solid #ddd;
  color: #666;
}

.btn-cancel:hover {
  background-color: #f5f5f5;
}

.btn-danger {
  background-color: #ff4d4f;
  color: white;
  border: none;
}

.btn-danger:hover {
  background-color: #d91117;
}

/* 安全设置项 */
.security-item, .privacy-item, .about-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.security-item:last-child, 
.privacy-item:last-child, 
.about-item:last-child {
  border-bottom: none;
}

.item-info .item-title {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #333;
}

.item-info .item-desc {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.item-action {
  padding: 6px 12px;
  background-color: transparent;
  border: 1px solid #1E90FF;
  color: #1E90FF;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.item-action:hover {
  background-color: rgba(30, 144, 255, 0.1);
}

.item-value {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* 通知设置 */
.notification-group {
  margin-bottom: 25px;
}

.group-title {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.notification-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item .item-title {
  margin: 0 0 3px 0;
  font-size: 15px;
  color: #333;
}

.notification-item .item-desc {
  margin: 0;
  font-size: 13px;
  color: #666;
}

.save-notifications, .save-privacy {
  margin-top: 15px;
}

/* 关于页面 */
.logout-btn {
  width: 100%;
  margin-top: 20px;
  padding: 12px;
}

/* 切换开关组件样式 */
.toggle-switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 24px;
}

.toggle-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: .4s;
  border-radius: 24px;
}

.toggle-slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .toggle-slider {
  background-color: #1E90FF;
}

input:checked + .toggle-slider:before {
  transform: translateX(26px);
}

input:disabled + .toggle-slider {
  background-color: #e0e0e0;
  cursor: not-allowed;
}

/* 弹窗内样式 */
.verify-code {
  display: flex;
  gap: 10px;
}

.verify-code .form-control {
  flex: 1;
}

.third-party-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.third-party-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.third-party-item:last-child {
  border-bottom: none;
}

.platform-name {
  font-size: 15px;
}

.bind-btn {
  padding: 5px 15px;
  font-size: 14px;
}

.agreement-content, .privacy-content {
  max-height: 400px;
  overflow-y: auto;
  font-size: 14px;
  line-height: 1.6;
}

.contact-info {
  font-size: 14px;
  line-height: 1.8;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .settings-container {
    padding: 15px;
  }
  
  .region-selector {
    flex-direction: column;
    gap: 10px;
  }
  
  .avatar-upload {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
  }
}
</style>