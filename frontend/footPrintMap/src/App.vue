<template>
  <!-- 登录页 -->
  <template v-if="!isLoggedIn">
    <!-- 登录页 -->
    <LoginView 
      v-if="authView === 'login'" 
      @login-success="handleLoginSuccess" 
      @go-register="authView = 'register'"
      @go-password="authView = 'password'"
    />
    <!-- 注册页 -->
    <RegisterView 
      v-else-if="authView === 'register'" 
      @back-login="authView = 'login'"
      @register-success="authView = 'login'"
    />
    <!-- 修改密码页 -->
    <UpdatePasswordView 
      v-else-if="authView === 'password'" 
      @back-login="authView = 'login'"
    />
  </template>

  <!-- 主界面 -->
  <div v-else class="main-layout">
    
    <!-- 1. 左侧可折叠侧边栏 -->
    <aside class="sidebar">
      <div class="menu-item" :class="{ active: currentView === 'map' }" @click="currentView = 'map'">
        <span class="icon">🗺️</span>
        <span class="text">我的足迹地图</span>
      </div>
      <div class="menu-item" :class="{ active: currentView === 'community' }" @click="currentView = 'community'">
        <span class="icon">🌍</span>
        <span class="text">足迹社区</span>
      </div>
      <div class="menu-item" :class="{ active: currentView === 'friends' }" @click="currentView = 'friends'">
        <span class="icon">👥</span>
        <span class="text">好友</span>
      </div>
    </aside>

    <!-- 2. 右上角个人中心 + 退出登录 -->
    <div class="top-right-menu">
      <button class="profile-btn" @click="currentView = 'profile'">
        👤 个人中心
      </button>
      <button class="logout-btn" @click="handleLogout">
        🚪 退出登录
      </button>
    </div>

    <!-- 3. 主内容区域 -->
    <main class="content-area">
      <!-- 地图页 -->
      <MapContainer v-if="currentView === 'map'" />
      
      <!-- 社区页 -->
      <CommunityView v-else-if="currentView === 'community'" />
      
      <FriendsView v-else-if="currentView === 'friends'" />

      <!-- 个人中心页 -->
      <PensonalCenter v-else-if="currentView === 'profile'" />
    </main>

  </div>
</template>

<script setup>
import { ref } from 'vue';
import LoginView from './components/LoginView.vue';
import RegisterView from './components/RegisterView.vue'
import UpdatePasswordView from './components/UpdatePasswordView.vue';
import MapContainer from './components/MapContainer.vue';
import CommunityView from './components/CommunityView.vue';
import FriendsView from './components/FriendView.vue';
import PensonalCenter from './components/PensonalCenter.vue';
import { logoutApi } from './api/user';

// 初始化时检查本地是否有 Token
const isLoggedIn = ref(localStorage.getItem('token') !== null);
// 默认显示地图页
const currentView = ref('map');

const authView = ref('login'); // 默认显示登录页

function handleLoginSuccess() {
  isLoggedIn.value = true;
}

async function handleLogout(){
  try {
    await logoutApi();
  } catch (e) {
    // 即使后端报错也继续清除本地状态
  }
  localStorage.removeItem('token');
  localStorage.removeItem('username');
  localStorage.removeItem('userId');
  localStorage.removeItem('avatar');
  isLoggedIn.value = false;
  authView.value = 'login';
  currentView.value = 'map';
}
</script>

<style>
/* 全局重置 */
body, html {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  font-family: sans-serif;
  overflow: hidden; /* 防止主页面整体滚动 */
}
#app {
  width: 100%;
  height: 100vh;
}

/* --- 布局结构 --- */
.main-layout {
  position: relative;
  width: 100%;
  height: 100vh;
  background-color: #f5f7fa;
}

/* --- 1. 左侧侧边栏样式 --- */
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  width: 60px; /* 默认折叠状态宽度 */
  background-color: #2c3e50; /* 深色背景 */
  color: white;
  padding-top: 20px;
  z-index: 1001;
  overflow: hidden; /* 隐藏超出文字 */
  transition: width 0.3s ease; /* 平滑过渡动画 */
}

/* 鼠标移上去展开 */
.sidebar:hover {
  width: 200px;
}

/* 菜单项样式 */
.menu-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  cursor: pointer;
  white-space: nowrap; /* 防止文字换行 */
  transition: background 0.2s;
}

.menu-item:hover {
  background-color: #34495e;
}

/* 选中的菜单项高亮 */
.menu-item.active {
  background-color: #FF5722; /* 主题色高亮 */
}

.menu-item .icon {
  font-size: 20px;
  margin-right: 15px; /* 图标和文字保持固定距离，即使折叠也不变 */
}

.menu-item .text {
  font-size: 16px;
  opacity: 0; /* 折叠时文字隐藏（实际是被 overflow 裁掉了） */
  transition: opacity 0.3s ease;
}

.sidebar:hover .menu-item .text {
  opacity: 1; /* 展开时文字显现 */
}

/* --- 2. 右上角个人中心样式 --- */
.top-right-menu {
  position: fixed;
  top: 15px;
  right: 20px;
  z-index: 1002;
}

.profile-btn {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #ddd;
  border-radius: 20px;
  padding: 8px 15px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  transition: all 0.3s;
  margin-right: 8px;
}

.profile-btn:hover {
  background: #FF8C00;
  color: white;
  border-color: #FF8C00;
}

.logout-btn {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #ddd;
  border-radius: 20px;
  padding: 8px 15px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  transition: all 0.3s;
}

.logout-btn:hover {
  background: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
}

/* --- 3. 主内容区域样式 --- */
.content-area {
  margin-left: 60px; /* 留出左侧折叠侧边栏的宽度 */
  width: calc(100% - 60px);
  height: 100vh;
  overflow-y: auto; /* 内容过长时允许滚动 */
  box-sizing: border-box;
}

/* 占位页面的简单居中样式 */
.placeholder-view {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #555;
}
</style>
