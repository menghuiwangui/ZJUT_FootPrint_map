<template>
  <div class="login-container">
    <!-- 装饰性的背景光斑 -->
    <div class="bg-bubble bubble-1"></div>
    <div class="bg-bubble bubble-2"></div>

    <div class="login-card">
      <div class="login-header">
        <div class="logo">🌍</div>
        <h2>足迹地图</h2>
        <p>记录你的每一段旅程</p>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="input-group">
          <span class="icon">👤</span>
          <input type="text" v-model="username" placeholder="请输入用户名" required />
        </div>
        
        <div class="input-group">
          <span class="icon">🔒</span>
          <input type="password" v-model="password" placeholder="请输入密码" required />
        </div>

        <div class="form-options">
          <label class="remember-me">
            <input type="checkbox" v-model="rememberMe" />
            <span>记住我</span>
          </label>
          <a href="#" class="forgot-pwd">忘记密码？</a>
        </div>

        <button type="submit" class="btn-login">
          登 录
        </button>
      </form>

      <div class="login-footer">
        还没有账号？ <a href="#">立即注册</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const username = ref('');
const password = ref('');
const rememberMe = ref(false);

const emit = defineEmits(['login-success']);

function handleLogin() {
  if (!username.value || !password.value) {
    alert('请输入用户名和密码');
    return;
  }
  // 这里后续可以替换为真实的 API 请求
  console.log('登录信息:', { 
    username: username.value, 
    password: password.value, 
    rememberMe: rememberMe.value 
  });
  //alert('登录成功！');
  emit('login-success');
}
</script>

<style scoped>

.login-container {
  position: relative;
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #f8b1eb, #e989c9 50%, #ec8d9a); /* 修改为暖色背景 */
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

.bg-bubble {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.5; /* 稍微提亮一点透明度 */
  animation: float 8s infinite ease-in-out;
}
.bubble-1 {
  width: 300px;
  height: 300px;
  background: #fcf4b9; 
  top: -50px;
  left: -50px;
}
.bubble-2 {
  width: 400px;
  height: 400px;
  background: #FF4500; /* 橘红 */
  bottom: -100px;
  right: -100px;
  animation-delay: -4s;
}

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-30px) scale(1.1); }
}

/* 登录卡片 (毛玻璃效果) */
.login-card {
  position: relative;
  z-index: 10;
  width: 380px;
  padding: 40px 35px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #fff;
}

/* 头部 */
.login-header {
  text-align: center;
  margin-bottom: 30px;
}
.logo {
  font-size: 40px;
  margin-bottom: 10px;
}
.login-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 1px;
}
.login-header p {
  margin: 8px 0 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
}

/* 表单 */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.input-group {
  position: relative;
  display: flex;
  align-items: center;
}
.input-group .icon {
  position: absolute;
  left: 15px;
  font-size: 16px;
  opacity: 0.7;
}
.input-group input {
  width: 100%;
  padding: 12px 15px 12px 45px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  color: #fff;
  font-size: 14px;
  transition: all 0.3s;
  box-sizing: border-box;
}
.input-group input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}
/* 输入框聚焦颜色 - 改为橘色 */
.input-group input:focus {
  outline: none;
  background: rgba(255, 255, 255, 0.2);
  border-color: #FF8C00; 
  box-shadow: 0 0 0 3px rgba(255, 140, 0, 0.25); 
}

/* 选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
}
.remember-me {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.8);
}
.remember-me input {
  margin-right: 6px;
  accent-color: #FF8C00; /* 复选框颜色改为橘色 */
}
.forgot-pwd {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: color 0.3s;
}
.forgot-pwd:hover {
  color: #fff;
}

/* 登录按钮 - 改为橘红色渐变 */
.btn-login {
  width: 100%;
  padding: 12px;
  margin-top: 10px;
  background: linear-gradient(90deg, #FF8C00, #FF5722); /* 橘黄到橘红 */
  border: none;
  border-radius: 10px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  letter-spacing: 2px;
}
.btn-login:hover {
  background: linear-gradient(90deg, #FF5722, #E64A19);
  box-shadow: 0 8px 20px rgba(255, 87, 34, 0.4); /* 橘色光晕 */
  transform: translateY(-2px);
}
.btn-login:active {
  transform: translateY(0);
}

/* 底部 */
.login-footer {
  text-align: center;
  margin-top: 25px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
}
.login-footer a {
  color: #FF8C00; /* 链接颜色改为橘色 */
  text-decoration: none;
  font-weight: 600;
}
.login-footer a:hover {
  text-decoration: underline;
}
</style>
