<template>
  <div class="friend-container">
    <h2 class="title">👥 好友</h2>

    <!-- 标签栏 -->
    <div class="tab-bar">
      <button
        class="tab-btn"
        :class="{ active: activeTab === 'list' }"
        @click="switchTab('list')"
      >
        我的好友 <span v-if="friends.length" class="badge">{{ friends.length }}</span>
      </button>
      <button
        class="tab-btn"
        :class="{ active: activeTab === 'pending' }"
        @click="switchTab('pending')"
      >
        好友请求 <span v-if="pendingCount" class="badge red">{{ pendingCount }}</span>
      </button>
      <button
        class="tab-btn"
        :class="{ active: activeTab === 'add' }"
        @click="switchTab('add')"
      >
        添加好友
      </button>
    </div>

    <!-- Tab 1: 好友列表 -->
    <div v-if="activeTab === 'list'" class="tab-content">
      <div v-if="loading" class="loading-state">加载中...</div>
      <div v-else-if="friends.length === 0" class="empty-state">
        你还没有好友，快去添加吧！
      </div>
      <div v-else class="friend-grid">
        <div
          v-for="friend in friends"
          :key="friend.id"
          class="friend-card"
          @click="openFriendDetail(friend)"
        >
          <div class="friend-avatar">
            <img
              v-if="friend.avatar"
              :src="friend.avatar"
              :alt="friend.username"
              class="avatar-img"
            />
            <span v-else class="avatar-placeholder">
              {{ friend.nickname ? friend.nickname[0] : friend.username ? friend.username[0] : 'U' }}
            </span>
          </div>
          <div class="friend-name">{{ friend.nickname || friend.username || '未知用户' }}</div>
          <div class="friend-bio">{{ friend.bio || '暂无简介' }}</div>
        </div>
      </div>
    </div>

    <!-- Tab 2: 好友请求 -->
    <div v-if="activeTab === 'pending'" class="tab-content">
      <div v-if="loading" class="loading-state">加载中...</div>
      <div v-else-if="pendingRequests.length === 0" class="empty-state">
        暂无待处理的好友请求
      </div>
      <div v-else class="request-list">
        <div v-for="req in pendingRequests" :key="req.id" class="request-card">
          <div class="request-avatar">
            <img v-if="req.avatar" :src="req.avatar" class="avatar-img" :alt="req.username" />
            <span v-else class="avatar-placeholder">
              {{ req.nickname ? req.nickname[0] : req.username ? req.username[0] : 'U' }}
            </span>
          </div>
          <div class="request-info">
            <div class="request-name">{{ req.nickname || req.username }}</div>
            <div class="request-time">{{ formatTime(req.createdTime) }}</div>
          </div>
          <div class="request-actions">
            <button class="btn-accept" @click="handleRequest(req.username, 1)">接受</button>
            <button class="btn-reject" @click="handleRequest(req.username, 2)">拒绝</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Tab 3: 添加好友 -->
    <div v-if="activeTab === 'add'" class="tab-content">
      <div class="add-friend-form">
        <input
          v-model="searchUsername"
          placeholder="输入用户名搜索..."
          class="search-input"
          @keyup.enter="sendRequest"
        />
        <button class="btn-send" @click="sendRequest" :disabled="!searchUsername.trim()">
          发送好友请求
        </button>
      </div>
      <div v-if="addResultMsg" class="result-msg" :class="{ success: addResultOk, error: !addResultOk }">
        {{ addResultMsg }}
      </div>
    </div>

    <!-- 好友详情弹窗 -->
    <div v-if="showDetail" class="modal-overlay" @click.self="closeDetail">
      <div class="detail-modal">
        <div class="modal-header">
          <span class="modal-title">好友信息</span>
          <button class="modal-close" @click="closeDetail">×</button>
        </div>
        <div class="modal-body">
          <!-- 用户信息 -->
          <div class="user-info-section" v-if="detailUser">
            <div class="detail-avatar">
              <img v-if="detailUser.avatar" :src="detailUser.avatar" class="avatar-img-lg" />
              <span v-else class="avatar-placeholder-lg">
                {{ detailUser.nickname ? detailUser.nickname[0] : detailUser.username ? detailUser.username[0] : 'U' }}
              </span>
            </div>
            <div class="user-meta">
              <div class="user-name">{{ detailUser.nickname || detailUser.username }}</div>
              <div class="user-username">@{{ detailUser.username }}</div>
              <div v-if="detailUser.bio" class="user-bio">{{ detailUser.bio }}</div>
              <div v-if="detailUser.email" class="user-email">邮箱: {{ detailUser.email }}</div>
            </div>
          </div>

          <!-- 分隔线 -->
          <div class="section-divider"></div>

          <!-- 好友的动态 -->
          <h3 class="section-title">📝 TA的动态</h3>
          <div v-if="detailLoading" class="loading-state">加载中...</div>
          <div v-else-if="detailPosts.length === 0" class="empty-inline">
            TA还没有发布过公开动态
          </div>
          <div v-else class="post-list">
            <div v-for="post in detailPosts" :key="post.id" class="post-item" @click="viewPost(post)">
              <div class="post-item-title">{{ post.title || '无标题游记' }}</div>
              <div class="post-item-content">{{ post.content || '暂无内容' }}</div>
              <div class="post-item-meta">
                📍 {{ post.visitTime ? formatTime(post.visitTime) : '未知日期' }}
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-danger" @click="deleteFriend">删除好友</button>
          <button class="btn-cancel" @click="closeDetail">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import {
  getFriendListApi,
  getPendingRequestsApi,
  sendFriendRequestApi,
  handleFriendRequestApi,
  getFriendInfoApi,
  getFriendPostsApi
} from '../api/friend';

// === 状态 ===
const activeTab = ref('list');
const loading = ref(false);
const friends = ref([]);
const pendingRequests = ref([]);
const pendingCount = computed(() => pendingRequests.value.length);

// 添加好友
const searchUsername = ref('');
const addResultMsg = ref('');
const addResultOk = ref(false);

// 详情弹窗
const showDetail = ref(false);
const detailUser = ref(null);
const detailPosts = ref([]);
const detailLoading = ref(false);
const currentFriend = ref(null);

// === 初始化 ===
onMounted(() => {
  loadFriends();
  loadPending();
});

function switchTab(tab) {
  activeTab.value = tab;
  if (tab === 'list') loadFriends();
  if (tab === 'pending') loadPending();
}

// === 好友列表 ===
async function loadFriends() {
  loading.value = true;
  try {
    const res = await getFriendListApi();
    if (res.code === 200) {
      friends.value = res.data || [];
    }
  } catch (error) {
    console.error('获取好友列表失败:', error);
  } finally {
    loading.value = false;
  }
}

// === 好友请求 ===
async function loadPending() {
  try {
    const res = await getPendingRequestsApi();
    if (res.code === 200) {
      pendingRequests.value = res.data || [];
    }
  } catch (error) {
    console.error('获取好友请求失败:', error);
  }
}

async function handleRequest(username, status) {
  try {
    const res = await handleFriendRequestApi({
      friendUsername: username,
      status: status
    });
    if (res.code === 200) {
      pendingRequests.value = pendingRequests.value.filter(r => r.username !== username);
      if (status === 1) {
        loadFriends();
      }
    } else {
      alert('操作失败：' + (res.message || '未知错误'));
    }
  } catch (error) {
    console.error('处理请求失败:', error);
    alert('操作失败，请重试');
  }
}

// === 添加好友 ===
async function sendRequest() {
  if (!searchUsername.value.trim()) return;
  addResultMsg.value = '';
  try {
    const res = await sendFriendRequestApi({ friendUsername: searchUsername.value.trim() });
    if (res.code === 200) {
      addResultMsg.value = '好友请求已发送！';
      addResultOk.value = true;
      searchUsername.value = '';
    } else {
      addResultMsg.value = res.message || '发送失败';
      addResultOk.value = false;
    }
  } catch (error) {
    addResultMsg.value = error.message || '网络错误，请重试';
    addResultOk.value = false;
  }
}

// === 好友详情 ===
async function openFriendDetail(friend) {
  currentFriend.value = friend;
  showDetail.value = true;
  detailUser.value = null;
  detailPosts.value = [];
  detailLoading.value = true;

  try {
    // 并行获取好友信息和动态
    const [infoRes, postsRes] = await Promise.all([
      getFriendInfoApi(friend.friendId),
      getFriendPostsApi(friend.friendId)
    ]);

    if (infoRes.code === 200) {
      detailUser.value = infoRes.data;
    }
    if (postsRes.code === 200) {
      detailPosts.value = postsRes.data || [];
    }
  } catch (error) {
    console.error('获取好友详情失败:', error);
  } finally {
    detailLoading.value = false;
  }
}

function closeDetail() {
  showDetail.value = false;
  currentFriend.value = null;
  detailUser.value = null;
  detailPosts.value = [];
}

async function deleteFriend() {
  if (!currentFriend.value) return;
  if (!confirm('确定要删除这位好友吗？')) return;

  try {
    const res = await handleFriendRequestApi({
      friendUsername: currentFriend.value.username,
      status: 3
    });
    if (res.code === 200) {
      friends.value = friends.value.filter(f => f.id !== currentFriend.value.id);
      closeDetail();
      alert('已删除好友');
    } else {
      alert('删除失败：' + (res.message || '未知错误'));
    }
  } catch (error) {
    console.error('删除好友失败:', error);
    alert('删除失败，请重试');
  }
}

function viewPost(post) {
  // 这里可以跳转到社区并定位到该帖子，或弹出帖子详情
  // 暂时用 alert 提示
  alert(`游记标题：${post.title || '无标题'}\n\n${post.content || '暂无内容'}`);
}

// === 工具函数 ===
function formatTime(time) {
  if (!time) return '';
  const str = String(time);
  return str.includes('T') ? str.split('T')[0] : str;
}
</script>

<style scoped>
.friend-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f7f8fa;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
}

.title {
  text-align: center;
  color: #333;
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 500;
}

/* 标签栏 */
.tab-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  background: #fff;
  padding: 8px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.tab-btn {
  flex: 1;
  padding: 12px 16px;
  border: none;
  background: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #86909c;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.tab-btn:hover {
  background: #f2f3f5;
}

.tab-btn.active {
  background: #2d8cf0;
  color: #fff;
}

.badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  border-radius: 10px;
  background: #e8eaed;
  color: #333;
  font-size: 12px;
  font-weight: 500;
}

.tab-btn.active .badge {
  background: rgba(255, 255, 255, 0.3);
  color: #fff;
}

.badge.red {
  background: #f53f3f;
  color: #fff;
}

.tab-btn.active .badge.red {
  background: rgba(255, 255, 255, 0.4);
}

/* Tab 内容 */
.tab-content {
  min-height: 300px;
}

.loading-state {
  text-align: center;
  padding: 60px 0;
  color: #86909c;
  font-size: 14px;
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  color: #c9cdd4;
  font-size: 15px;
}

/* 好友列表 */
.friend-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 16px;
}

.friend-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.friend-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.friend-avatar {
  width: 64px;
  height: 64px;
  margin: 0 auto 12px;
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: #e8eaed;
  color: #333;
  font-weight: bold;
  font-size: 24px;
}

.friend-name {
  font-weight: 500;
  font-size: 15px;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.friend-bio {
  font-size: 12px;
  color: #c9cdd4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 好友请求 */
.request-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.request-card {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 12px;
  padding: 16px 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.request-avatar {
  width: 48px;
  height: 48px;
  margin-right: 16px;
  flex-shrink: 0;
}

.request-info {
  flex: 1;
}

.request-name {
  font-weight: 500;
  font-size: 15px;
  color: #333;
}

.request-time {
  font-size: 12px;
  color: #c9cdd4;
  margin-top: 2px;
}

.request-actions {
  display: flex;
  gap: 8px;
}

.btn-accept {
  padding: 8px 20px;
  border: none;
  border-radius: 6px;
  background: #2d8cf0;
  color: #fff;
  cursor: pointer;
  font-size: 13px;
  transition: background 0.2s;
}

.btn-accept:hover {
  background: #1a6fc9;
}

.btn-reject {
  padding: 8px 20px;
  border: 1px solid #e5e6eb;
  border-radius: 6px;
  background: #fff;
  color: #86909c;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s;
}

.btn-reject:hover {
  background: #f2f3f5;
  color: #f53f3f;
  border-color: #f53f3f;
}

/* 添加好友 */
.add-friend-form {
  display: flex;
  gap: 12px;
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.search-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #e5e6eb;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.search-input:focus {
  border-color: #2d8cf0;
}

.btn-send {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  background: #2d8cf0;
  color: #fff;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background 0.2s;
}

.btn-send:hover {
  background: #1a6fc9;
}

.btn-send:disabled {
  background: #c9cdd4;
  cursor: not-allowed;
}

.result-msg {
  margin-top: 16px;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
}

.result-msg.success {
  background: #e8ffea;
  color: #00b42a;
}

.result-msg.error {
  background: #ffece8;
  color: #f53f3f;
}

/* 弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.detail-modal {
  width: 480px;
  max-height: 85vh;
  background: #fff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
}

.modal-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  font-weight: 500;
  font-size: 16px;
}

.modal-close {
  background: none;
  border: none;
  font-size: 22px;
  cursor: pointer;
  color: #86909c;
  line-height: 1;
}

.modal-body {
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}

/* 用户信息 */
.user-info-section {
  display: flex;
  gap: 16px;
}

.detail-avatar {
  width: 72px;
  height: 72px;
  flex-shrink: 0;
}

.avatar-img-lg {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder-lg {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: #e8eaed;
  color: #333;
  font-weight: bold;
  font-size: 28px;
}

.user-meta {
  flex: 1;
}

.user-name {
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

.user-username {
  font-size: 13px;
  color: #86909c;
  margin-top: 2px;
}

.user-bio {
  font-size: 13px;
  color: #4e5969;
  margin-top: 8px;
  line-height: 1.5;
}

.user-email {
  font-size: 12px;
  color: #c9cdd4;
  margin-top: 6px;
}

.section-divider {
  height: 1px;
  background: #f0f0f0;
  margin: 20px 0;
}

.section-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0 0 12px 0;
}

.empty-inline {
  text-align: center;
  padding: 24px 0;
  color: #c9cdd4;
  font-size: 13px;
}

/* 帖子列表 */
.post-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.post-item {
  padding: 12px 16px;
  background: #f7f8fa;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.post-item:hover {
  background: #f2f3f5;
}

.post-item-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-item-content {
  font-size: 13px;
  color: #86909c;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-item-meta {
  font-size: 12px;
  color: #c9cdd4;
  margin-top: 6px;
}

/* 底部 */
.modal-footer {
  padding: 12px 20px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-danger {
  padding: 8px 20px;
  border: 1px solid #f53f3f;
  border-radius: 6px;
  background: #fff;
  color: #f53f3f;
  cursor: pointer;
  font-size: 13px;
  margin-right: auto;
  transition: all 0.2s;
}

.btn-danger:hover {
  background: #f53f3f;
  color: #fff;
}

.btn-cancel {
  padding: 8px 20px;
  border: 1px solid #e5e6eb;
  border-radius: 6px;
  background: #fff;
  cursor: pointer;
  font-size: 13px;
  color: #4e5969;
}

.btn-cancel:hover {
  background: #f2f3f5;
}
</style>
