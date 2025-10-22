<template>
  <div class="reset-password-page">
    <!-- 主容器 -->
    <div class="reset-container">
      <!-- 头部标题 -->
      <div class="reset-header">
        <h1 class="reset-title">找回密码</h1>
        <p class="reset-desc">请填写以下信息重置您的密码</p>
      </div>

      <!-- 表单内容容器 -->
      <div class="step-content">
        <!-- 一次性展示所有表单字段 -->
        <div v-if="currentStep !== 3" class="form-step animate-form">
          <div class="form-hint form-animate" style="animation-delay: 0s;">
            <p>请输入您注册的用户名、手机号、验证码并设置新密码</p>
          </div>

          <!-- 用户名输入 -->
          <div class="form-group form-animate" style="animation-delay: 0.1s;">
            <label for="username" class="form-label">用户名</label>
            <div class="input-wrapper">
              <span class="input-icon user-icon">👤</span>
              <input 
                type="text" 
                id="username" 
                v-model.trim="username"
                class="form-input"
                :class="usernameErrorShow ? 'form-input--error' : ''"
                placeholder="请输入用户名" 
                @input="usernameErrorShow = false"
              >
            </div>
            <p 
              class="form-error"
              v-show="usernameErrorShow"
            >
              请输入用户名
            </p>
          </div>

          <!-- 手机号输入 -->
          <div class="form-group form-animate" style="animation-delay: 0.2s;">
            <label for="phone" class="form-label">手机号</label>
            <div class="input-wrapper">
              <span class="input-icon phone-icon">📱</span>
              <input 
                type="tel" 
                id="phone" 
                v-model.trim="phone"
                class="form-input"
                :class="phoneErrorShow ? 'form-input--error' : ''"
                placeholder="请输入手机号" 
                maxlength="11"
                @input="phoneErrorShow = false"
              >
            </div>
            <p 
              class="form-error"
              v-show="phoneErrorShow"
            >
              请输入有效的手机号
            </p>
          </div>

          <!-- 验证码输入 -->
          <div class="form-group form-animate" style="animation-delay: 0.3s;">
            <label for="verify-code" class="form-label">验证码</label>
            <div class="input-wrapper input-wrapper--code">
              <span class="input-icon code-icon">🔒</span>
              <input 
                type="text" 
                id="verify-code" 
                v-model.trim="verifyCode"
                class="form-input"
                placeholder="请输入验证码"
              >
              <button 
                @click="sendCode"
                :disabled="isCodeDisabled"
                class="code-btn"
                :class="isCodeDisabled ? 'code-btn--disabled' : 'code-btn--active'"
              >
                {{ codeBtnText }}
              </button>
            </div>
          </div>

          <!-- 新密码输入 -->
          <div class="form-group form-animate" style="animation-delay: 0.4s;">
            <label for="new-password" class="form-label">新密码</label>
            <div class="input-wrapper">
              <span class="input-icon pwd-icon">🔐</span>
              <input 
                :type="newPwdVisible ? 'text' : 'password'" 
                id="new-password" 
                v-model="newPassword"
                @input="checkPasswordStrength"
                class="form-input"
                placeholder="请输入新密码"
              >
              <button 
                @click="toggleNewPwdVisible"
                class="input-toggle"
              >
                {{ newPwdVisible ? '🙈' : '👁️' }}
              </button>
            </div>
          </div>

          <!-- 密码强度指示器 -->
          <div class="pwd-strength" v-show="newPassword.length > 0">
            <div class="strength-bars">
              <div 
                v-for="(item, index) in 4" 
                :key="index"
                class="strength-bar"
                :class="getStrengthClass(index + 1)"
              ></div>
            </div>
            <p 
              class="strength-text"
              :class="getStrengthTextClass()"
            >
              密码强度：{{ strengthText }}
            </p>
          </div>

          <!-- 确认密码输入 -->
          <div class="form-group form-animate" style="animation-delay: 0.5s;">
            <label for="confirm-password" class="form-label">确认新密码</label>
            <div class="input-wrapper">
              <span class="input-icon pwd-icon">🔐</span>
              <input 
                :type="confirmPwdVisible ? 'text' : 'password'" 
                id="confirm-password" 
                v-model="confirmPassword"
                @input="checkPasswordMatch"
                class="form-input"
                :class="pwdMatchError ? 'form-input--error' : ''"
                placeholder="请再次输入新密码"
              >
              <button 
                @click="toggleConfirmPwdVisible"
                class="input-toggle"
              >
                {{ confirmPwdVisible ? '🙈' : '👁️' }}
              </button>
            </div>
            <p 
              class="form-error"
              v-show="pwdMatchError"
            >
              两次输入的密码不一致
            </p>
          </div>

          <!-- 提交按钮 -->
          <button 
            @click="submitReset"
            class="btn btn--primary btn--submit form-animate"
            style="animation-delay: 0.6s;"
          >
            确认重置
          </button>
        </div>

        <!-- 重置成功提示 -->
        <div v-else class="success-page">
          <div class="success-icon">✅</div>
          <h3 class="success-title">密码重置成功！</h3>
          <p class="success-desc">您的密码已成功更新，请使用新密码登录</p>
          <button 
            @click="goToLogin"
            class="btn btn--primary btn--login"
          >
            返回登录
          </button>
        </div>

        <!-- 返回登录链接 -->
        <div class="back-login back-login-animate" v-if="currentStep !== 3">
          <button 
            @click="goToLogin"
            class="back-login-btn"
          >
            ← 返回登录
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

// 1. 路由初始化（跳转登录页）
const router = useRouter();
const goToLogin = () => {
  router.push('/login'); // 按实际项目的登录页路由调整
};

// 2. 核心响应式状态
const currentStep = ref(1); // 1-表单页面，3-重置成功
const username = ref(''); // 用户名
const phone = ref(''); // 手机号
const verifyCode = ref(''); // 验证码
const phoneErrorShow = ref(false); // 手机号错误提示
const usernameErrorShow = ref(false); // 用户名错误提示
const isCodeDisabled = ref(false); // 验证码按钮禁用状态
const codeBtnText = ref('获取验证码'); // 验证码按钮文本
const countdownTimer = ref(null); // 验证码倒计时定时器

// 密码相关状态
const newPassword = ref(''); // 新密码
const confirmPassword = ref(''); // 确认密码
const newPwdVisible = ref(false); // 新密码可见性
const confirmPwdVisible = ref(false); // 确认密码可见性
const pwdMatchError = ref(false); // 密码不匹配提示
const passwordStrength = ref(0); // 密码强度（0-4级）
const strengthText = ref('弱'); // 密码强度文本


// 3. 验证码发送逻辑（与原逻辑完全一致）
const sendCode = () => {
  // 手机号格式验证（11位，以13-9开头）
  const phoneReg = /^1[3-9]\d{9}$/;
  if (!phoneReg.test(phone.value)) {
    phoneErrorShow.value = true;
    return;
  }
  phoneErrorShow.value = false;

  // 倒计时逻辑（60秒）
  let countdown = 60;
  isCodeDisabled.value = true;
  codeBtnText.value = `重新发送(${countdown}s)`;

  // 启动定时器
  countdownTimer.value = setInterval(() => {
    countdown--;
    if (countdown <= 0) {
      clearInterval(countdownTimer.value);
      isCodeDisabled.value = false;
      codeBtnText.value = '获取验证码';
    } else {
      codeBtnText.value = `重新发送(${countdown}s)`;
    }
  }, 1000);

  // 模拟发送验证码（实际项目替换为后端接口请求）
  console.log(`向手机号 ${phone.value} 发送验证码`);
  alert('验证码已发送，请注意查收');
};

// 组件卸载时清除定时器（避免内存泄漏）
onUnmounted(() => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value);
  }
});


// 4. 移除步骤切换逻辑，因为现在是一次性表单


// 5. 密码相关交互（与原逻辑完全一致）
// 切换新密码可见性
const toggleNewPwdVisible = () => {
  newPwdVisible.value = !newPwdVisible.value;
};

// 切换确认密码可见性
const toggleConfirmPwdVisible = () => {
  confirmPwdVisible.value = !confirmPwdVisible.value;
};

// 检查密码强度
const checkPasswordStrength = () => {
  const pwd = newPassword.value;
  let strength = 0;

  // 强度规则：长度+字符类型
  if (pwd.length >= 8) strength++; // 长度≥8
  if (pwd.length >= 12) strength++; // 长度≥12
  if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) strength++; // 包含大小写字母
  if (/[0-9]/.test(pwd) && /[^A-Za-z0-9]/.test(pwd)) strength++; // 包含数字+特殊字符

  // 更新强度状态和文本
  passwordStrength.value = strength;
  switch (strength) {
    case 0:
    case 1:
      strengthText.value = '弱';
      break;
    case 2:
      strengthText.value = '中';
      break;
    case 3:
      strengthText.value = '良好';
      break;
    case 4:
      strengthText.value = '强';
      break;
  }

  // 同步检查密码匹配
  checkPasswordMatch();
};

// 检查密码是否匹配
const checkPasswordMatch = () => {
  pwdMatchError.value = newPassword.value !== confirmPassword.value && confirmPassword.value.length > 0;
};
  
  // 提交密码重置
const submitReset = async () => {
  // 1. 验证用户名
  if (!username.value.trim()) {
    usernameErrorShow.value = true;
    return;
  }
  usernameErrorShow.value = false;

  // 2. 验证手机号
  const phoneReg = /^1[3-9]\d{9}$/;
  if (!phoneReg.test(phone.value)) {
    phoneErrorShow.value = true;
    return;
  }
  phoneErrorShow.value = false;

  // 3. 验证验证码（6位数字）
  const codeReg = /^\d{6}$/;
  if (!codeReg.test(verifyCode.value)) {
    alert('请输入有效的6位验证码');
    return;
  }

  // 4. 密码长度验证
  if (newPassword.value.length < 8) {
    alert('密码长度至少8位');
    return;
  }

  // 5. 密码格式验证（含字母和数字）
  const pwdReg = /(?=.*[A-Za-z])(?=.*\d)/;
  if (!pwdReg.test(newPassword.value)) {
    alert('密码必须包含字母和数字');
    return;
  }

  // 6. 密码匹配验证
  if (newPassword.value !== confirmPassword.value) {
    pwdMatchError.value = true;
    return;
  }

  try {
    // 调用后端密码重置API
    const response = await axios.post('http://localhost:8080/api/user/regPwd', null, {
      params: {
        name: username.value, // 后端API参数名是name
        number: phone.value, // 后端API参数名是number
        password: newPassword.value // 后端API参数名是password
      },
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    
    // 处理响应
    if (response.data && response.data.success) {
      // 密码重置成功，切换到成功页面
      currentStep.value = 3;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } else {
      // 密码重置失败，显示错误信息
      alert(response.data.message || '密码重置失败，请稍后重试');
    }
  } catch (error) {
    // 错误处理
    console.error('密码重置请求出错：', error);
    alert(error.response?.data?.message || '网络错误，请检查您的连接');
  }
};


// 6. 辅助函数：获取密码强度指示器样式
const getStrengthClass = (index) => {
  if (index > passwordStrength.value) return 'strength-bar--gray';
  // 按强度设置颜色：弱(红) → 中(橙) → 良好(黄) → 强(绿)
  if (passwordStrength.value <= 2) return 'strength-bar--red';
  if (passwordStrength.value === 3) return 'strength-bar--yellow';
  return 'strength-bar--green';
};

// 辅助函数：获取密码强度文本样式
const getStrengthTextClass = () => {
  if (passwordStrength.value <= 2) return 'strength-text--red';
  if (passwordStrength.value === 3) return 'strength-text--yellow';
  return 'strength-text--green';
};
</script>

<style scoped>
/* 1. 页面基础样式 */
.reset-password-page {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  background: linear-gradient(135deg, #eff6ff, #e0e7ff); /* 渐变背景 */
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  margin: 0;
  box-sizing: border-box;
}

/* 2. 主容器样式 */
.reset-container {
  width: 100%;
  max-width: 480px;
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08); /* 阴影效果 */
  overflow: hidden;
}

/* 3. 头部标题样式 */
.reset-header {
  background-color: #3B82F6; /* 主色调：蓝色 */
  padding: 24px;
  color: #ffffff;
}

.reset-title {
  font-size: clamp(1.5rem, 3vw, 1.8rem); /* 响应式字体 */
  font-weight: 700;
  margin: 0 0 4px 0;
}

.reset-desc {
  font-size: 14px;
  color: #dbeafe; /* 浅蓝文字 */
  margin: 0;
}

/* 4. 步骤指示器样式 */
/* 表单内容上边距调整 */
.step-content {
  padding-top: 24px;
}

/* 5. 表单步骤样式 */
.step-content {
  padding: 0 24px 24px;
}

.form-step {
  display: flex;
  flex-direction: column;
  gap: 20px; /* 表单元素间距 */
}

/* 表单提示文本 */
.form-hint {
  text-align: center;
  margin-bottom: 8px;
}

.form-hint p {
  font-size: 14px;
  color: #4b5563;
  margin: 0;
}

/* 表单组样式 */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

/* 输入框容器 */
.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

/* 带验证码按钮的输入框容器 */
.input-wrapper--code {
  padding-right: 110px; /* 给验证码按钮留空间 */
}

/* 输入框图标 */
.input-icon {
  position: absolute;
  left: 12px;
  font-size: 18px;
  color: #9ca3af;
}

/* 用户图标样式补充 */
.user-icon {
  font-size: 16px;
}

/* 输入框样式 */
.form-input {
  width: 100%;
  padding: 12px 12px 12px 40px; /* 左侧给图标留空间 */
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

/* 输入框聚焦样式 */
.form-input:focus {
  outline: none;
  border-color: #3B82F6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2); /* 聚焦阴影 */
}

/* 输入框错误样式 */
.form-input--error {
  border-color: #ef4444;
}

.form-input--error:focus {
  border-color: #ef4444;
  box-shadow: 0 0 0 2px rgba(239, 68, 68, 0.2);
}

/* 错误提示文本 */
.form-error {
  font-size: 12px;
  color: #ef4444;
  margin: 0;
}

/* 输入框可见性切换按钮 */
.input-toggle {
  position: absolute;
  right: 12px;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 18px;
  color: #9ca3af;
  padding: 4px;
}

/* 验证码按钮 */
.code-btn {
  position: absolute;
  right: 4px;
  top: 50%;
  transform: translateY(-50%);
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

/* 验证码按钮-激活状态 */
.code-btn--active {
  background-color: #3B82F6;
  color: #ffffff;
}

.code-btn--active:hover {
  background-color: #2563eb;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
  transform: translateY(-50%) translateY(-2px);
}

/* 验证码按钮-禁用状态 */
.code-btn--disabled {
  background-color: #e5e7eb;
  color: #ffffff;
  cursor: not-allowed;
}

/* 按钮基础样式 */
.btn {
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

/* 主按钮样式 */
.btn--primary {
  background-color: #3B82F6;
  color: #ffffff;
}

.btn--primary:hover {
  background-color: #2563eb;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
  transform: translateY(-2px);
}

/* 按钮尺寸调整 */
.btn--next, .btn--submit {
  width: 100%;
  margin-top: 8px;
}

.btn--login {
  padding: 12px 32px;
  margin-top: 8px;
}

/* 6. 密码强度指示器 */
.pwd-strength {
  margin-top: -12px;
  margin-bottom: 8px;
}

.strength-bars {
  display: flex;
  gap: 4px;
  margin-bottom: 4px;
}

.strength-bar {
  flex: 1;
  height: 4px;
  border-radius: 2px;
}

.strength-bar--gray {
  background-color: #e5e7eb;
}

.strength-bar--red {
  background-color: #ef4444;
}

.strength-bar--yellow {
  background-color: #f59e0b;
}

.strength-bar--green {
  background-color: #22c55e;
}

.strength-text {
  font-size: 12px;
  margin: 0;
}

.strength-text--red {
  color: #ef4444;
}

.strength-text--yellow {
  color: #b45309;
}

.strength-text--green {
  color: #166534;
}

/* 7. 重置成功页面 */
.success-page {
  text-align: center;
  padding: 24px 0;
}

.success-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.success-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.success-desc {
  font-size: 14px;
  color: #4b5563;
  margin: 0 0 24px 0;
}

/* 8. 返回登录按钮 */
.back-login {
  text-align: center;
  margin-top: 24px;
}

.back-login-btn {
  background: transparent;
  border: none;
  font-size: 14px;
  color: #4b5563;
  cursor: pointer;
  transition: color 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 4px 8px;
}

.back-login-btn:hover {
  color: #3B82F6;
}

/* 10. 页面加载动画 */
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

.form-animate {
  opacity: 0;
  animation: fadeInUp 0.5s ease forwards;
}

/* 返回登录按钮的特殊动画 */
@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.back-login-animate {
  opacity: 0;
  animation: slideInLeft 0.5s ease forwards;
  animation-delay: 0.7s;
}

/* 密码强度指示器的特殊动画 */
@keyframes pulseIn {
  0% {
    opacity: 0;
    transform: scale(0.95);
  }
  50% {
    transform: scale(1.02);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

.pwd-strength {
  animation: pulseIn 0.3s ease-out;
}

/* 9. 全局样式重置（避免浏览器默认样式干扰） */
button {
  cursor: pointer;
  border: none;
  background: transparent;
  padding: 0;
  margin: 0;
  font-family: inherit;
  font-size: inherit;
  color: inherit;
}

input {
  font-family: inherit;
  margin: 0;
}

p {
  margin: 0;
}
</style>