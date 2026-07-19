<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { getPersonalInfoApi, updatePersonalInfoApi, deletePersonalInfoApi, updatePasswordApi, logoutApi } from '../api/user';
import { getPostListApi, deletePostApi } from '../api/community';
import { uploadAvatarImageApi } from '../api/upload';

// ======================== 状态 ========================
const userInfo = ref({
    id: '',
    username: '',
    nickname: '',
    avatar: '',
    bio: '',
    email: '',
    phone: '',
    role: 0,
    createTime: ''
});

const myPosts = ref([]);
const loadingPosts = ref(false);
const activeTab = ref('info');  // info | posts | edit | password | danger

// 编辑个人信息表单
const editForm = reactive({
    nickname: '',
    bio: '',
    email: '',
    phone: ''
});

// 修改密码表单
const passwordForm = reactive({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
});

const avatarFileInput = ref(null);
const uploadingAvatar = ref(false);
const savingInfo = ref(false);
const savingPassword = ref(false);

// ======================== 计算属性 ========================
const avatarUrl = computed(() => {
    return userInfo.value.avatar || '';
});

const roleText = computed(() => {
    return userInfo.value.role === 1 ? '管理员' : '普通用户';
});

const formatDate = (dateStr) => {
    if (!dateStr) return '未知';
    const d = new Date(dateStr);
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
};

const visibilityText = (v) => {
    return v === 0 ? '公开' : v === 1 ? '仅自己' : '仅好友';
};

// ======================== 初始化 ========================
onMounted(async () => {
    await fetchUserInfo();
    await fetchMyPosts();
});

// 获取用户信息
async function fetchUserInfo() {
    try {
        const res = await getPersonalInfoApi();
        if (res.code === 200 && res.data) {
            userInfo.value = res.data;
            // 同步到编辑表单
            editForm.nickname = res.data.nickname || '';
            editForm.bio = res.data.bio || '';
            editForm.email = res.data.email || '';
            editForm.phone = res.data.phone || '';
        }
    } catch (error) {
        console.error('获取用户信息失败', error);
    }
}

// 获取我的帖子
async function fetchMyPosts() {
    loadingPosts.value = true;
    try {
        const res = await getPostListApi();
        if (res.code === 200 && res.data) {
            myPosts.value = res.data.map(item => ({
                ...item,
                expanded: false,
                photos: item.photos ? item.photos.split(',') : []
            }));
        }
    } catch (error) {
        console.error('获取帖子失败', error);
    } finally {
        loadingPosts.value = false;
    }
}

// ======================== 头像上传 ========================
function triggerAvatarUpload() {
    avatarFileInput.value.click();
}

async function handleAvatarUpload(event) {
    const file = event.target.files[0];
    if (!file) return;

    // 校验文件大小（2MB）
    if (file.size > 2 * 1024 * 1024) {
        alert('头像图片不能超过 2MB');
        event.target.value = '';
        return;
    }

    // 校验文件类型
    if (!file.type.startsWith('image/')) {
        alert('只能上传图片文件');
        event.target.value = '';
        return;
    }

    uploadingAvatar.value = true;
    try {
        // 1. 上传图片到服务器
        const uploadRes = await uploadAvatarImageApi(file);
        if (uploadRes.code !== 200 || !uploadRes.data) {
            alert('头像上传失败：' + (uploadRes.message || '未知错误'));
            return;
        }

        const newAvatarUrl = uploadRes.data;

        // 2. 更新用户信息中的 avatar 字段
        const updateRes = await updatePersonalInfoApi({
            nickname: userInfo.value.nickname,
            avatar: newAvatarUrl,
            bio: userInfo.value.bio,
            email: userInfo.value.email,
            phone: userInfo.value.phone
        });

        if (updateRes.code === 200) {
            userInfo.value.avatar = newAvatarUrl;
            // 同步到 localStorage（其他组件可能用到）
            localStorage.setItem('avatar', newAvatarUrl);
            alert('头像更新成功！');
        } else {
            alert('头像更新失败：' + (updateRes.message || '未知错误'));
        }
    } catch (error) {
        console.error('头像上传出错', error);
        alert('头像上传失败，请重试');
    } finally {
        uploadingAvatar.value = false;
        event.target.value = '';
    }
}

// ======================== 保存个人信息 ========================
async function handleSaveInfo() {
    // 简单校验
    if (!editForm.nickname.trim()) {
        alert('昵称不能为空');
        return;
    }
    if (editForm.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(editForm.email)) {
        alert('邮箱格式不正确');
        return;
    }

    savingInfo.value = true;
    try {
        const res = await updatePersonalInfoApi({
            nickname: editForm.nickname,
            avatar: userInfo.value.avatar,
            bio: editForm.bio,
            email: editForm.email,
            phone: editForm.phone
        });
        if (res.code === 200) {
            // 同步到本地状态
            userInfo.value.nickname = editForm.nickname;
            userInfo.value.bio = editForm.bio;
            userInfo.value.email = editForm.email;
            userInfo.value.phone = editForm.phone;
            localStorage.setItem('username', editForm.nickname || userInfo.value.username);
            alert('个人信息保存成功！');
            activeTab.value = 'info';
        } else {
            alert('保存失败：' + (res.message || '未知错误'));
        }
    } catch (error) {
        console.error('保存出错', error);
        alert('保存失败，请重试');
    } finally {
        savingInfo.value = false;
    }
}

// ======================== 修改密码 ========================
async function handleChangePassword() {
    if (!passwordForm.oldPassword) {
        alert('请输入旧密码');
        return;
    }
    if (!passwordForm.newPassword) {
        alert('请输入新密码');
        return;
    }
    if (passwordForm.newPassword.length < 6) {
        alert('新密码长度不能少于 6 位');
        return;
    }
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
        alert('两次输入的密码不一致');
        return;
    }
    if (passwordForm.newPassword === passwordForm.oldPassword) {
        alert('新密码不能与旧密码相同');
        return;
    }

    savingPassword.value = true;
    try {
        const res = await updatePasswordApi({
            oldPassword: passwordForm.oldPassword,
            newPassword: passwordForm.newPassword
        });
        if (res.code === 200) {
            alert('密码修改成功！请重新登录');
            // 密码改了，JWT 还有效但让用户重新登录更安全
            handleLogout();
        } else {
            alert('密码修改失败：' + (res.message || '未知错误'));
        }
    } catch (error) {
        console.error('修改密码出错', error);
        alert('修改密码失败，请重试');
    } finally {
        savingPassword.value = false;
    }
}

// ======================== 删除帖子 ========================
async function handleDeletePost(id) {
    if (!confirm('确定要删除这条足迹吗？')) return;
    try {
        const res = await deletePostApi(id);
        if (res.code === 200) {
            myPosts.value = myPosts.value.filter(p => p.id !== id);
            alert('删除成功');
        } else {
            alert('删除失败：' + (res.message || '未知错误'));
        }
    } catch (error) {
        console.error('删除出错', error);
        alert('删除失败，请重试');
    }
}

// ======================== 注销账号 ========================
async function handleDeleteAccount() {
    const confirm1 = confirm('确定要注销账号吗？注销后所有数据将永久删除，无法恢复！');
    if (!confirm1) return;

    const confirm2 = prompt('请输入 "确认注销" 以继续：');
    if (confirm2 !== '确认注销') {
        alert('已取消注销');
        return;
    }

    try {
        const res = await deletePersonalInfoApi();
        if (res.code === 200) {
            alert('账号已注销');
            handleLogout();
        } else {
            alert('注销失败：' + (res.message || '未知错误'));
        }
    } catch (error) {
        console.error('注销出错', error);
        alert('注销失败，请重试');
    }
}

// ======================== 退出登录 ========================
async function handleLogout() {
    try {
        await logoutApi();
    } catch (e) {
        // 即使后端报错也继续清除本地状态
    }
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('userId');
    localStorage.removeItem('avatar');
    window.location.reload();
}

// ======================== 帖子展开/收起 ========================
function toggleExpand(post) {
    post.expanded = !post.expanded;
}
</script>

<template>
  <div class="personal-center">
    <input ref="avatarFileInput" type="file" accept="image/*" style="display:none" @change="handleAvatarUpload" />

    <!-- 顶部用户信息卡片 -->
    <div class="profile-header">
      <div class="avatar-wrapper" @click="triggerAvatarUpload" title="点击上传头像">
        <img v-if="avatarUrl" :src="avatarUrl" class="avatar" alt="头像" />
        <div v-else class="avatar-placeholder">
          {{ userInfo.nickname ? userInfo.nickname.charAt(0).toUpperCase() : '?' }}
        </div>
        <div class="avatar-overlay">
          <span v-if="uploadingAvatar">上传中...</span>
          <span v-else>更换头像</span>
        </div>
      </div>

      <div class="user-meta">
        <h2 class="username">{{ userInfo.nickname || userInfo.username || '未设置昵称' }}</h2>
        <p class="user-handle">@{{ userInfo.username }}</p>
        <div class="user-badges">
          <span class="badge">{{ roleText }}</span>
          <span class="badge">注册于 {{ formatDate(userInfo.createTime) }}</span>
        </div>
        <p v-if="userInfo.bio" class="user-bio">{{ userInfo.bio }}</p>
      </div>
    </div>

    <!-- 标签栏 -->
    <div class="tabs">
      <button :class="{ active: activeTab === 'info' }" @click="activeTab = 'info'">基本信息</button>
      <button :class="{ active: activeTab === 'posts' }" @click="activeTab = 'posts'">我的帖子</button>
      <button :class="{ active: activeTab === 'edit' }" @click="activeTab = 'edit'">修改信息</button>
      <button :class="{ active: activeTab === 'password' }" @click="activeTab = 'password'">修改密码</button>
      <button :class="{ active: activeTab === 'danger' }" @click="activeTab = 'danger'">注销账号</button>
    </div>

    <!-- 基本信息 -->
    <div v-if="activeTab === 'info'" class="tab-content">
      <div class="info-grid">
        <div class="info-item">
          <span class="label">用户名</span>
          <span class="value">{{ userInfo.username || '—' }}</span>
        </div>
        <div class="info-item">
          <span class="label">昵称</span>
          <span class="value">{{ userInfo.nickname || '—' }}</span>
        </div>
        <div class="info-item">
          <span class="label">邮箱</span>
          <span class="value">{{ userInfo.email || '—' }}</span>
        </div>
        <div class="info-item">
          <span class="label">手机号</span>
          <span class="value">{{ userInfo.phone || '—' }}</span>
        </div>
        <div class="info-item full-width">
          <span class="label">个人简介</span>
          <span class="value">{{ userInfo.bio || '这个人很懒，什么都没写' }}</span>
        </div>
        <div class="info-item">
          <span class="label">角色</span>
          <span class="value">{{ roleText }}</span>
        </div>
        <div class="info-item">
          <span class="label">注册时间</span>
          <span class="value">{{ formatDate(userInfo.createTime) }}</span>
        </div>
      </div>
    </div>

    <!-- 我的帖子 -->
    <div v-if="activeTab === 'posts'" class="tab-content">
      <div v-if="loadingPosts" class="loading">加载中...</div>
      <div v-else-if="myPosts.length === 0" class="empty-state">
        <p>你还没有发布过任何帖子</p>
      </div>
      <div v-else class="posts-list">
        <div v-for="post in myPosts" :key="post.id" class="post-card">
          <div class="post-header">
            <h3 class="post-title">{{ post.title || '无标题' }}</h3>
            <span class="post-visibility">{{ visibilityText(post.visibility) }}</span>
          </div>
          <div class="post-content" :class="{ collapsed: !post.expanded }">
            {{ post.content || '无内容' }}
          </div>
          <button v-if="post.content && post.content.length > 100" class="expand-btn" @click="toggleExpand(post)">
            {{ post.expanded ? '收起' : '展开全文' }}
          </button>
          <div v-if="post.tags && post.tags.length > 0" class="post-tags">
            <span v-for="tag in post.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
          <div class="post-footer">
            <span class="post-meta">
              📅 {{ formatDate(post.visitTime) }} · ❤️ {{ post.likesCount || 0 }} 次点赞
            </span>
            <button class="delete-post-btn" @click="handleDeletePost(post.id)">删除</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 修改信息 -->
    <div v-if="activeTab === 'edit'" class="tab-content">
      <div class="form-group">
        <label>昵称</label>
        <input v-model="editForm.nickname" type="text" placeholder="请输入昵称" />
      </div>
      <div class="form-group">
        <label>邮箱</label>
        <input v-model="editForm.email" type="email" placeholder="请输入邮箱" />
      </div>
      <div class="form-group">
        <label>手机号</label>
        <input v-model="editForm.phone" type="tel" placeholder="请输入手机号" />
      </div>
      <div class="form-group">
        <label>个人简介</label>
        <textarea v-model="editForm.bio" rows="4" placeholder="介绍一下自己吧"></textarea>
      </div>
      <div class="form-actions">
        <button class="btn-cancel" @click="activeTab = 'info'">取消</button>
        <button class="btn-save" :disabled="savingInfo" @click="handleSaveInfo">
          {{ savingInfo ? '保存中...' : '保存修改' }}
        </button>
      </div>
    </div>

    <!-- 修改密码 -->
    <div v-if="activeTab === 'password'" class="tab-content">
      <div class="password-warning">
        修改密码后需要重新登录。请确保记住新密码。
      </div>
      <div class="form-group">
        <label>当前密码</label>
        <input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码" />
      </div>
      <div class="form-group">
        <label>新密码</label>
        <input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码（至少6位）" />
      </div>
      <div class="form-group">
        <label>确认新密码</label>
        <input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" />
      </div>
      <div class="form-actions">
        <button class="btn-cancel" @click="activeTab = 'info'">取消</button>
        <button class="btn-save" :disabled="savingPassword" @click="handleChangePassword">
          {{ savingPassword ? '修改中...' : '确认修改' }}
        </button>
      </div>
    </div>

    <!-- 注销账号 -->
    <div v-if="activeTab === 'danger'" class="tab-content">
      <div class="danger-zone">
        <h3>注销账号</h3>
        <p>注销后，你的所有数据（个人信息、帖子、好友关系等）将被永久删除，无法恢复。</p>
        <p>此操作不可逆，请谨慎操作。</p>
        <button class="btn-danger" @click="handleDeleteAccount">永久注销账号</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.personal-center {
    max-width: 800px;
    margin: 0 auto;
    padding: 30px 20px;
}

/* 顶部用户信息卡片 */
.profile-header {
    display: flex;
    align-items: center;
    gap: 24px;
    background: white;
    padding: 30px;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    margin-bottom: 20px;
}

.avatar-wrapper {
    position: relative;
    width: 100px;
    height: 100px;
    border-radius: 50%;
    cursor: pointer;
    overflow: hidden;
    flex-shrink: 0;
    border: 3px solid #FF5722;
}

.avatar {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.avatar-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f0f0f0;
    font-size: 40px;
    font-weight: bold;
    color: #999;
}

.avatar-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(0, 0, 0, 0.6);
    color: white;
    text-align: center;
    padding: 4px;
    font-size: 12px;
    opacity: 0;
    transition: opacity 0.3s;
}

.avatar-wrapper:hover .avatar-overlay {
    opacity: 1;
}

.user-meta {
    flex: 1;
}

.username {
    margin: 0 0 4px 0;
    font-size: 24px;
    color: #333;
}

.user-handle {
    margin: 0 0 8px 0;
    color: #999;
    font-size: 14px;
}

.user-badges {
    display: flex;
    gap: 8px;
    margin-bottom: 8px;
}

.badge {
    background: #f0f2f5;
    color: #666;
    padding: 3px 10px;
    border-radius: 12px;
    font-size: 12px;
}

.user-bio {
    margin: 8px 0 0 0;
    color: #666;
    font-size: 14px;
    line-height: 1.5;
}

/* 标签栏 */
.tabs {
    display: flex;
    gap: 4px;
    background: white;
    padding: 6px;
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    margin-bottom: 20px;
    overflow-x: auto;
}

.tabs button {
    flex: 1;
    padding: 10px 16px;
    border: none;
    background: transparent;
    color: #666;
    cursor: pointer;
    border-radius: 8px;
    font-size: 14px;
    white-space: nowrap;
    transition: all 0.2s;
}

.tabs button:hover {
    background: #f5f5f5;
}

.tabs button.active {
    background: #FF5722;
    color: white;
}

/* Tab 内容区 */
.tab-content {
    background: white;
    border-radius: 12px;
    padding: 30px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    min-height: 300px;
}

/* 基本信息网格 */
.info-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;
}

.info-item {
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.info-item.full-width {
    grid-column: 1 / -1;
}

.info-item .label {
    font-size: 13px;
    color: #999;
}

.info-item .value {
    font-size: 15px;
    color: #333;
}

/* 帖子列表 */
.posts-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.post-card {
    border: 1px solid #eee;
    border-radius: 10px;
    padding: 20px;
    transition: box-shadow 0.2s;
}

.post-card:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.post-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
}

.post-title {
    margin: 0;
    font-size: 17px;
    color: #333;
}

.post-visibility {
    background: #f0f2f5;
    color: #666;
    padding: 2px 8px;
    border-radius: 10px;
    font-size: 12px;
}

.post-content {
    color: #555;
    font-size: 14px;
    line-height: 1.6;
    white-space: pre-wrap;
    word-break: break-word;
}

.post-content.collapsed {
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.expand-btn {
    background: none;
    border: none;
    color: #FF5722;
    cursor: pointer;
    font-size: 13px;
    padding: 4px 0;
}

.post-tags {
    display: flex;
    gap: 6px;
    margin-top: 10px;
    flex-wrap: wrap;
}

.tag {
    background: #e8f4ff;
    color: #1890ff;
    padding: 2px 8px;
    border-radius: 10px;
    font-size: 12px;
}

.post-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 12px;
    padding-top: 12px;
    border-top: 1px solid #f0f0f0;
}

.post-meta {
    color: #999;
    font-size: 13px;
}

.delete-post-btn {
    background: none;
    border: 1px solid #ff4d4f;
    color: #ff4d4f;
    padding: 4px 12px;
    border-radius: 6px;
    cursor: pointer;
    font-size: 13px;
    transition: all 0.2s;
}

.delete-post-btn:hover {
    background: #ff4d4f;
    color: white;
}

/* 表单 */
.form-group {
    margin-bottom: 20px;
}

.form-group label {
    display: block;
    margin-bottom: 6px;
    font-size: 14px;
    color: #333;
    font-weight: 500;
}

.form-group input,
.form-group textarea {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid #e5e6eb;
    border-radius: 8px;
    font-size: 14px;
    box-sizing: border-box;
    transition: border-color 0.2s;
    font-family: inherit;
}

.form-group input:focus,
.form-group textarea:focus {
    border-color: #FF5722;
    outline: none;
}

.form-actions {
    display: flex;
    gap: 12px;
    justify-content: flex-end;
    margin-top: 24px;
}

.btn-cancel {
    padding: 10px 24px;
    border: 1px solid #ddd;
    background: white;
    border-radius: 8px;
    cursor: pointer;
    font-size: 14px;
    color: #666;
    transition: all 0.2s;
}

.btn-cancel:hover {
    background: #f5f5f5;
}

.btn-save {
    padding: 10px 24px;
    border: none;
    background: #FF5722;
    color: white;
    border-radius: 8px;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.2s;
}

.btn-save:hover:not(:disabled) {
    background: #e64a19;
}

.btn-save:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

/* 修改密码警告 */
.password-warning {
    background: #fff7e6;
    border: 1px solid #ffd591;
    border-radius: 8px;
    padding: 12px 16px;
    margin-bottom: 24px;
    color: #d46b08;
    font-size: 14px;
}

/* 危险区域 */
.danger-zone {
    text-align: center;
    padding: 20px 0;
}

.danger-zone h3 {
    color: #ff4d4f;
    margin-bottom: 16px;
}

.danger-zone p {
    color: #666;
    margin-bottom: 8px;
    line-height: 1.6;
}

.btn-danger {
    margin-top: 20px;
    padding: 10px 32px;
    border: none;
    background: #ff4d4f;
    color: white;
    border-radius: 8px;
    cursor: pointer;
    font-size: 15px;
    transition: all 0.2s;
}

.btn-danger:hover {
    background: #cf1322;
}

/* 加载和空状态 */
.loading,
.empty-state {
    text-align: center;
    padding: 40px;
    color: #999;
}

@media (max-width: 600px) {
    .profile-header {
        flex-direction: column;
        text-align: center;
    }

    .info-grid {
        grid-template-columns: 1fr;
    }

    .tabs {
        flex-wrap: wrap;
    }

    .tabs button {
        flex: 1 1 40%;
    }
}
</style>
