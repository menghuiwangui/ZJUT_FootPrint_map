<template>
  <!-- 登录页 -->
  <LoginView 
    v-if="!isLoggedIn" 
    @login-success="handleLoginSuccess" 
  />

  <!-- 主界面 -->
  <div v-else>
    <!-- 顶部导航 -->
    <div style="position: fixed; top: 0; left: 0; right: 0; z-index: 1000; background: rgba(255,255,255,0.9); padding: 10px; text-align: center; backdrop-filter: blur(5px); box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
      <button @click="currentView = 'map'" :style="{ fontWeight: currentView === 'map' ? 'bold' : 'normal' }">🗺️ 我的足迹地图</button>
      <span style="margin: 0 20px; color: #ccc;">|</span>
      <button @click="currentView = 'community'" :style="{ fontWeight: currentView === 'community' ? 'bold' : 'normal' }">🌍 足迹社区</button>
    </div>

    <!-- 地图页 -->
    <MapContainer v-if="currentView === 'map'" style="padding-top: 50px;" />
    
    <!-- 社区页 -->
    <CommunityView v-else style="padding-top: 50px;" />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import LoginView from './components/LoginView.vue'; // 确保路径正确
import MapContainer from './components/MapContainer.vue'; // 确保路径正确
import CommunityView from './components/CommunityView.vue'


// 定义登录状态，默认为 false (未登录)
const isLoggedIn = ref(false);
const currentView = ref('map');

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
