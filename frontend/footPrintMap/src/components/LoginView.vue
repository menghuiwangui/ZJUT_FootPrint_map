<template>
  <div class="auth-container">
    <div class="bg-bubble bubble-1"></div>
    <div class="bg-bubble bubble-2"></div>

    <div class="auth-card">
      <div class="auth-header">
        <div class="logo">🌍</div>
        <h2>足迹地图</h2>
        <p>记录你的每一段旅程</p>
      </div>

      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="input-group">
          <span class="icon">👤</span>
          <input type="text" v-model="username" placeholder="请输入用户名" required />
        </div>
        <div class="input-group">
          <span class="icon">🔒</span>
          <input type="password" v-model="password" placeholder="请输入密码" required />
        </div>

        <div class="form-options">
          <label class="remember-me" style="color: rgba(255,255,255,0.8); display:flex; align-items:center; cursor:pointer;">
            <input type="checkbox" v-model="rememberMe" style="margin-right:6px; accent-color: #FF8C00;" />记住我
          </label>
          <a class="auth-link" @click="emit('go-password')">忘记密码？</a>
        </div>

        <button type="submit" class="btn-auth">登 录</button>
      </form>

      <div class="auth-footer">
        还没有账号？ <a @click="emit('go-register')">立即注册</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { login } from '../api/user';

const username = ref('');
const password = ref('');
const rememberMe = ref(false);

const emit = defineEmits(['login-success', 'go-register', 'go-password']);

async function handleLogin() {
  if (!username.value || !password.value) {
    alert('请输入用户名和密码');
    return;
  }

  try{
    const res = await login({
      username: username.value,
      password: password.value
    })
    if (res.token){
      localStorage.setItem('token', res.data.token);
      localStorage.setItem('username', res.username);
      emit('login-success');
    }
    else{
      alert(res.message || '登录失败，请检查用户名和密码')
    }
  }catch(error){
    console.error('登录请求失败: ' + error.message);
    alert('网络异常，请稍后再试');
  }
}

</script>