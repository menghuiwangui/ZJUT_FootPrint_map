<template>
  <!-- 如果没登录，显示登录页，并监听 login-success 事件 -->
  <LoginView 
    v-if="!isLoggedIn" 
    @login-success="handleLoginSuccess" 
  />
  
  <!-- 如果登录了，显示地图组件 -->
  <MapContainer v-else />
</template>

<script setup>
import { ref } from 'vue';
import LoginView from './components/LoginView.vue'; // 确保路径正确
import MapContainer from './components/MapContainer.vue'; // 确保路径正确

// 定义登录状态，默认为 false (未登录)
const isLoggedIn = ref(false);

// 接收到子组件登录成功的信号后，将状态改为 true
function handleLoginSuccess() {
  isLoggedIn.value = true;
}
</script>

<style>
/* 全局重置样式，确保占满全屏 */
body, html {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  font-family: sans-serif;
}
#app {
  width: 100%;
  height: 100vh;
}
</style>
