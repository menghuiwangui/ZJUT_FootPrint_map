<script setup>
import { ref } from 'vue';
import { registerApi } from '../api/user';

const formData = ref({
    username: '',
    password: '',
    nickname: ''
});

const emit = defineEmits(['back-login', 'register-success'])

async function handleRegister(){
    if (!formData.value.username || !formData.value.password || !formData.value.nickname) {
        alert('请填写完整信息');
        return;
    }

    try {
        const res = await registerApi(formData.value);
        if (res.code === 200){
            alert('注册成功！');
            emit('register-success');
        }else{
            alert('注册失败，用户名已存在: ' + res.message);
        }
    }catch (error){
        console.error('注册失败: ' + error.message);
    }
}
</script>

<template>
  <div class="auth-container">
    <div class="bg-bubble bubble-1"></div>
    <div class="bg-bubble bubble-2"></div>

    <div class="auth-card">
      <div class="auth-header">
        <div class="logo">📝</div>
        <h2>注册账号</h2>
        <p>加入足迹地图，开启记录</p>
      </div>

      <form @submit.prevent="handleRegister" class="auth-form">
        <div class="input-group">
          <span class="icon">👤</span>
          <input type="text" v-model="formData.username" placeholder="设置用户名" required />
        </div>
        <div class="input-group">
          <span class="icon">🔒</span>
          <input type="password" v-model="formData.password" placeholder="设置密码" required />
        </div>
        <div class="input-group">
          <span class="icon">🏷️</span>
          <input type="text" v-model="formData.nickname" placeholder="设置昵称" required />
        </div>

        <button type="submit" class="btn-auth">注 册</button>
      </form>

      <div class="auth-footer">
        已有账号？ <a @click="emit('back-login')">返回登录</a>
      </div>
    </div>
  </div>
</template>