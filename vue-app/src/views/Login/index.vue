<template>
  <div class="login-container">
    <!-- 左侧风景图区域 -->
    <div class="left-section">
      <img 
        src="https://aigc-image.bj.bcebos.com/miaobi/5mao/b%2754%2BN5a6d6aaG6YeM6Z2i5pyJ5LuA5LmIXzE3MjQ4MTc3MTYuMjMzMjk2Nl8xNzI0ODE3NzE2LjkwMTU4MTU%3D%27/1.png" 
        alt="旅游风景" 
        class="scenery-img"
      >
    </div>

    <!-- 右侧表单区域（淡蓝色模块） -->
    <div class="right-section">
      <h2>欢迎登录旅游网</h2>
      
      <!-- 选项卡切换（登录/注册） -->
      <div class="tabs">
        <button 
          class="tab" 
          :class="{ active: activeTab === 'account' }" 
          @click="switchTab('account')"
        >
          账号密码登录
        </button>
        <button 
          class="tab" 
          :class="{ active: activeTab === 'phone' }" 
          @click="switchTab('phone')"
        >
          手机验证登录
        </button>
        <button 
          class="tab" 
          :class="{ active: activeTab === 'register' }" 
          @click="switchTab('register')"
        >
          立即注册
        </button>
      </div>

      <!-- 账号密码登录表单 -->
      <form 
        class="login-form" 
        :class="{ active: activeTab === 'account' }" 
        @submit.prevent="handleAccountLogin"
      >
        <div class="input-group">
          <i class="fas fa-user"></i>
          <input 
            type="text" 
            placeholder="请输入您的账号" 
            v-model.trim="accountForm.username"
          >
        </div>
        <div class="input-group">
          <i class="fas fa-lock"></i>
          <input 
            type="password" 
            placeholder="请输入您的密码" 
            v-model.trim="accountForm.password"
          >
        </div>
        <button type="button" class="login-btn" @click="handleAccountLogin">登录</button>
        <div class="extra-options">
          <label>
            <input 
              type="checkbox" 
              v-model="accountForm.remember"
            > 
            记住密码
          </label>
          <a class="forgot-link" :class="{ 'link-active': isForgotLinkActive }" @mousedown="handleForgotPassword" @mouseup="isForgotLinkActive = false">忘记密码?</a>
        </div>
      </form>

      <!-- 手机验证登录表单 -->
      <form 
        class="login-form" 
        :class="{ active: activeTab === 'phone' }" 
        @submit.prevent="handlePhoneLogin"
      >
        <div class="input-group">
          <i class="fas fa-mobile-alt"></i>
          <input 
            type="tel" 
            placeholder="请输入您的手机号" 
            v-model.trim="phoneForm.phone"
          >
        </div>
        <div class="input-group">
          <i class="fas fa-code"></i>
          <input 
            type="text" 
            placeholder="请输入验证码" 
            v-model.trim="phoneForm.code"
          >
          <button 
            type="button" 
            class="get-code-btn"
            @click="getVerificationCode"
            :disabled="isCodeDisabled"
          >
            {{ codeBtnText }}
          </button>
        </div>
        <button type="button" class="login-btn" @click="handlePhoneLogin">登录</button>
      </form>

      <!-- 注册表单 -->
      <form 
        class="login-form" 
        :class="{ active: activeTab === 'register' }" 
        @submit.prevent="handleRegister"
      >
        <div class="input-group">
          <i class="fas fa-user"></i>
          <input 
            type="text" 
            placeholder="请设置用户名" 
            v-model.trim="registerForm.username"
          >
        </div>
        <div class="input-group">
          <i class="fas fa-mobile-alt"></i>
          <input 
            type="tel" 
            placeholder="请输入您的手机号" 
            v-model.trim="registerForm.phone"
          >
        </div>
        <div class="input-group">
          <i class="fas fa-lock"></i>
          <input 
            type="password" 
            placeholder="请设置密码" 
            v-model.trim="registerForm.password"
          >
        </div>
        <div class="input-group">
          <i class="fas fa-lock"></i>
          <input 
            type="password" 
            placeholder="请确认密码" 
            v-model.trim="registerForm.confirmPassword"
          >
        </div>
        <!-- 错误提示 -->
        <div v-if="registerError" class="error-message">
          {{ registerError }}
        </div>
        <button 
          type="submit" 
          class="login-btn"
          :disabled="isRegistering"
        >
          {{ isRegistering ? '注册中...' : '注册' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'; // Vue 3 组合式 API，用于创建响应式数据
// 首先需要导入useRouter
import { useRouter } from 'vue-router';
// 导入axios用于发送HTTP请求
import axios from 'axios';

// 在setup中获取路由实例
const router = useRouter();

// 忘记密码链接的响应式状态
const isForgotLinkActive = ref(false);

// 处理忘记密码点击事件
const handleForgotPassword = () => {
  isForgotLinkActive.value = true;
  // 添加短暂的延迟以显示视觉反馈
  setTimeout(() => {
    router.push('/forget');
  }, 100);
};

// 实现doLogin方法 - 调用handleAccountLogin来处理登录
const doLogin = () => {
  handleAccountLogin();
};
// 1. 响应式状态管理
// 当前激活的选项卡（默认：账号密码登录）
const activeTab = ref('account');

// 账号密码登录表单数据
const accountForm = ref({
  username: '',
  password: '',
  remember: false // 记住密码复选框
});

// 手机验证登录表单数据
const phoneForm = ref({
  phone: '',
  code: ''
});

// 注册状态管理
const registerForm = ref({
  username: '',
  phone: '',
  password: '',
  confirmPassword: ''
});

// 注册相关状态
const isRegistering = ref(false); // 注册中状态
const registerError = ref(''); // 注册错误信息

// 验证码按钮状态（禁用/文本）
const isCodeDisabled = ref(false);
const codeBtnText = ref('获取验证码');
const countdownTimer = ref(null); // 倒计时定时器（用于清除）


// 2. 核心交互方法
// 切换选项卡
const switchTab = (tabName) => {
  activeTab.value = tabName;
  // 切换时清除验证码倒计时（避免切换后定时器仍运行）
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value);
    isCodeDisabled.value = false;
    codeBtnText.value = '获取验证码';
    countdownTimer.value = null;
  }
};

// 获取验证码（含倒计时逻辑）
const getVerificationCode = () => {
  const phone = phoneForm.value.phone;
  // 手机号格式验证（11位，以13-9开头）
  const phoneReg = /^1[3-9]\d{9}$/;
  
  if (!phoneReg.test(phone)) {
    alert('请输入有效的手机号');
    return;
  }

  // 启动倒计时（60秒）
  let countdown = 60;
  isCodeDisabled.value = true;
  codeBtnText.value = `重新发送(${countdown}s)`;

  // 定时器逻辑
  countdownTimer.value = setInterval(() => {
    countdown--;
    if (countdown <= 0) {
      clearInterval(countdownTimer.value);
      countdownTimer.value = null;
      isCodeDisabled.value = false;
      codeBtnText.value = '获取验证码';
    } else {
      codeBtnText.value = `重新发送(${countdown}s)`;
    }
  }, 1000);

  // 模拟发送验证码（实际项目中替换为接口请求）
  console.log(`向手机号 ${phone} 发送验证码`);
  alert('验证码已发送，请注意查收');
};


// 3. 表单提交验证（与原JS逻辑一致）
// 账号密码登录提交
const handleAccountLogin = async () => {
  const { username, password } = accountForm.value;
  
  if (!username) {
    alert('请输入账号');
    return;
  }
  if (!password) {
    alert('请输入密码');
    return;
  }

  try {
    // 调用后端登录API - 直接指向后端8080端口
    const response = await axios.get('http://localhost:8080/api/user/login', {
      params: {
        name: username, // 后端API参数名是name
        password: password // 后端API参数名是password
      },
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    
    // 处理响应
    if (response.data && response.data.success) {
      // 登录成功 - 获取完整的用户信息
      const userData = response.data.data || {};
      console.log('登录成功，用户信息:', userData);
      
      // 保存用户ID到sessionStorage（与后端保持一致）
      if (userData.id) {
        sessionStorage.setItem('userId', userData.id);
        console.log('已保存userId到sessionStorage:', userData.id);
      }
      
      // 补充默认头像（如果后端没有返回）
      // 使用与Profile.vue相同的默认头像URL
      if (!userData.avatar) {
        userData.avatar = 'https://q8.itc.cn/q_70/images03/20250304/f5873423f8b044d78aa8cf036bc132e0.jpeg';
        console.log('已设置默认头像');
      }
      
      // 保存完整用户信息到localStorage
      localStorage.setItem('userInfo', JSON.stringify(userData));
      
      alert('登录成功！');
      // 登录成功后跳转到主页面
      router.push('/home');
    } else {
      // 登录失败，显示错误信息
      alert(response.data.message || '登录失败，请检查账号密码');
    }
  } catch (error) {
    // 错误处理
    console.error('登录请求出错：', error);
    alert(error.response?.data?.message || '网络错误，请检查您的连接');
  }
};

// 手机验证登录提交
const handlePhoneLogin = () => {
  const { phone, code } = phoneForm.value;
  const phoneReg = /^1[3-9]\d{9}$/;
  
  if (!phoneReg.test(phone)) {
    alert('请输入有效的手机号');
    return;
  }
  if (!code || code.length !== 6) {
    alert('请输入有效的6位验证码');
    return;
  }

  // 模拟登录成功
  alert('登录成功！');
};

// 注册提交
const handleRegister = async () => {
  const { username, phone, password, confirmPassword } = registerForm.value;
  const phoneReg = /^1[3-9]\d{9}$/;
  
  // 重置错误信息
  registerError.value = '';
  
  // 表单验证逻辑
  if (!username) {
    registerError.value = '请设置用户名';
    return;
  }
  if (!phoneReg.test(phone)) {
    registerError.value = '请输入有效的手机号';
    return;
  }
  if (password.length < 8) {
    registerError.value = '密码长度不能少于8位';
    return;
  }
  if (password !== confirmPassword) {
    registerError.value = '两次输入的密码不一致';
    return;
  }

  try {
    // 设置注册中状态
    isRegistering.value = true;
    
    // 调用后端注册API - 直接指向后端8080端口，避免代理配置问题
    // 注意：后端使用@RequestParam，需要传递查询参数
    const response = await axios.post('http://localhost:8080/api/user/reg', null, {
      params: {
        name: username, // 用户名
        password: password, // 密码
        number: phone // 手机号 - 后端需要number字段名
      },
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    
    // 处理响应 - 根据Result<User>的结构
    if (response.data && response.data.success) {
      // 注册成功 - 获取完整的用户信息
      const userData = response.data.data || {
        name: username, // 用户名
        number: phone // 手机号 - 与后端字段名保持一致
      };
      console.log('注册成功，用户信息:', userData);
      
      // 保存用户ID到sessionStorage（与后端保持一致）
      if (userData.id) {
        sessionStorage.setItem('userId', userData.id);
        console.log('已保存userId到sessionStorage:', userData.id);
      }
      
      // 补充默认头像（如果后端没有返回）
      // 使用与Profile.vue相同的默认头像URL
      if (!userData.avatar) {
        userData.avatar = 'https://q8.itc.cn/q_70/images03/20250304/f5873423f8b044d78aa8cf036bc132e0.jpeg';
        console.log('已设置默认头像');
      }
      
      // 保存完整用户信息到localStorage
      localStorage.setItem('userInfo', JSON.stringify(userData));
      
      alert('注册成功！');
      // 注册成功后自动切换到账号登录
      switchTab('account');
      // 可自动填充用户名（提升用户体验）
      accountForm.value.username = username;
      // 清空注册表单
      registerForm.value = {
        username: '',
        phone: '',
        password: '',
        confirmPassword: ''
      };
    } else {
      // 注册失败，显示错误信息
      registerError.value = response.data.message || '注册失败，请稍后重试';
    }
  } catch (error) {
    // 错误处理
    console.error('注册请求出错：', error);
    registerError.value = error.response?.data?.message || '网络错误，请检查您的连接';
  } finally {
    // 无论成功失败，都重置注册中状态
    isRegistering.value = false;
  }
};
</script>

<style scoped>
/* 全局样式重置（与原CSS一致） */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: "Microsoft Yahei", sans-serif;
}

/* 登录容器（白色卡片+阴影） */
.login-container {
  margin: auto ;
  margin-top:150px;
  background-color: #fff;
  width: 100%;
  max-width: 1000px;
  border-radius: 12px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  overflow: hidden;
}

/* 左侧图片区域 */
.left-section {
  flex: 1;
  min-width: 300px;
}

.scenery-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* 右侧表单区域（淡蓝色背景） */
.right-section {
  flex: 1;
  min-width: 300px;
  background-color: #e6f2ff;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
}

.right-section h2 {
  color: #1a56db;
  margin-bottom: 30px;
  font-weight: 600;
  font-size: 24px;
}

/* 选项卡样式 */
.tabs {
  display: flex;
  margin-bottom: 30px;
  border-bottom: 1px solid #bdd7ee;
}

.tab {
  background: none;
  border: none;
  padding: 10px 15px;
  font-size: 15px;
  color: #4b5563;
  cursor: pointer;
  position: relative;
  transition: color 0.3s;
}

.tab.active {
  color: #1a56db;
  font-weight: 500;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #1a56db;
}

.tab:hover:not(.active) {
  color: #1a56db;
}

/* 表单样式（默认隐藏，active时显示） */
.login-form {
  display: none;
  flex-direction: column;
  gap: 20px;
}

.login-form.active {
  display: flex;
  animation: fadeIn 0.3s ease;
}

/* 输入框组（带图标） */
.input-group {
  position: relative;
}

.input-group input {
  width: 100%;
  padding: 14px 15px 14px 45px;
  border: 1px solid #bdd7ee;
  border-radius: 6px;
  font-size: 15px;
  background-color: #ffffff;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.input-group input:focus {
  outline: none;
  border-color: #1a56db;
  box-shadow: 0 0 0 3px rgba(26, 86, 219, 0.1);
}

.input-group i {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #64748b;
}

/* 验证码按钮 */
.get-code-btn {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background-color: #1a56db;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.get-code-btn:hover {
  background-color: #0f46b8;
}

.get-code-btn:disabled {
  background-color: #94a3b8;
  cursor: not-allowed;
}

/* 登录/注册按钮 */
.login-btn {
  padding: 14px;
  background-color: #1a56db;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s, transform 0.1s;
}

.login-btn:hover {
  background-color: #0f46b8;
}

.login-btn:active {
  transform: scale(0.99);
}

.login-btn:disabled {
  background-color: #a0c4f1;
  cursor: not-allowed;
  opacity: 0.6;
}

/* 错误提示样式 */
.error-message {
  color: #e53e3e;
  font-size: 14px;
  margin-top: 10px;
  margin-bottom: 10px;
  text-align: center;
}

/* 额外选项（记住密码+忘记密码） */
.extra-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #475569;
  margin-top: 10px;
}

.extra-options label {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
}

.forgot-link {
  color: #1a56db;
  text-decoration: none;
  transition: all 0.2s ease;
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
}

.forgot-link:hover {
  text-decoration: underline;
  background-color: rgba(26, 86, 219, 0.05);
  transform: translateY(-1px);
}

.forgot-link.link-active {
  background-color: rgba(26, 86, 219, 0.1);
  transform: scale(0.98);
  transition: transform 0.05s ease;
}

/* 表单切换动画 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 响应式调整（适配手机端） */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }
  
  .left-section {
    height: 200px; /* 手机端图片区域高度固定 */
  }
  
  .right-section {
    padding: 30px 25px;
  }
  
  .tabs {
    flex-wrap: wrap; /* 选项卡换行 */
  }
  
  .tab {
    flex: 1;
    text-align: center; /* 选项卡文本居中 */
  }
}
</style>