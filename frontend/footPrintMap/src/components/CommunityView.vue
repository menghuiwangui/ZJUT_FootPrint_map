<template>
  <div class="community-container">
    <h2 class="title">🌍 足迹社区广场</h2>
    
    <!-- 顶部操作栏 -->
    <div class="action-bar">
      <span>分享你的最新足迹，记录美好瞬间</span>
      <button class="publish-btn" @click="showForm = true">+ 发布足迹</button>
    </div>

    <!-- 帖子列表 -->
    <div class="post-list">
      <!-- 空状态 -->
      <div v-if="posts.length === 0 && !loading" class="empty-state">
        社区还没有动态，快来发布第一条足迹吧！
      </div>

      <!-- 帖子卡片 -->
      <div v-for="post in posts" :key="post.id" class="post-card">
        <!-- 头部信息 -->
        <div class="post-header">
          <div class="author-avatar">{{ post.username ? post.username[0] : 'U' }}</div>
          <div class="author-meta">
            <div class="author-name">{{ post.username || '用户' + post.userId }}</div>
            <div class="post-location">📍 {{ post.locationName || '未知地点' }} · {{ formatTime(post.visitTime) }}</div>
          </div>
          
          <!-- 作者操作按钮 (仅作者可见) -->
          <div v-if="isOwner(post)" class="author-actions">
            <button class="action-icon" @click="openEditForm(post)" title="编辑">✏️</button>
            <button class="action-icon" @click="handleHide(post)" title="切换公开/私密">
              {{ post.visibility === 1 ? '🔒' : '🔓' }}
            </button>
            <button class="action-icon" @click="handleDelete(post.id)" title="删除">🗑️</button>
          </div>
        </div>

        <!-- 内容区域 (支持折叠) -->
        <div class="post-body">
          <h3 class="post-title">{{ post.title || '无标题游记' }}</h3>
          
          <!-- 折叠控制 -->
          <div class="content-wrapper" :class="{ 'collapsed': !post.expanded }">
            <p class="post-content">{{ post.content }}</p>
            
            <!-- 标签 -->
            <div v-if="post.tags && post.tags.length" class="tags-row">
              <span v-for="(tag, idx) in post.tags" :key="idx" class="tag-item">#{{ tag }}</span>
            </div>

            <!-- 图片展示 -->
            <div v-if="post.photos && post.photos.length" class="photos-grid">
              <img v-for="(photo, idx) in post.photos" :key="idx" :src="photo" class="photo-item" alt="游记图片" />
            </div>
          </div>

          <!-- 展开/折叠按钮 -->
          <div v-if="isContentLong(post)" class="expand-btn" @click="post.expanded = !post.expanded">
            {{ post.expanded ? '收起全文' : '展开全文' }}
          </div>
        </div>

        <!-- 底部互动栏 -->
        <div class="post-footer">
          <button 
            class="interact-btn" 
            :class="{ 'active': post.isLiked }"
            @click="toggleLike(post)"
          >
            <span class="icon">👍</span> 
            <span>{{ post.likeCount || 0 }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 分页组件 -->
    <div v-if="pagination.total > 0" class="pagination-bar">
      <button 
        class="page-btn" 
        :disabled="pagination.current === 1" 
        @click="changePage(pagination.current - 1)"
      >上一页</button>
      <span class="page-info">第 {{ pagination.current }} 页</span>
      <button 
        class="page-btn" 
        :disabled="pagination.current * pagination.size >= pagination.total" 
        @click="changePage(pagination.current + 1)"
      >下一页</button>
    </div>

    <!-- 发布足迹弹窗 -->
    <div v-if="showForm" class="modal-overlay" @click.self="showForm = false">
      <div class="modal">
        <div class="modal-header">
          <span class="modal-title">✈️ 发布新足迹</span>
          <button class="modal-close" @click="showForm = false">×</button>
        </div>

        <div class="modal-body">
          <div class="form-group">
            <label>📝 游记标题</label>
            <input v-model="formData.title" placeholder="给你的足迹起个名字" />
          </div>

          <!-- 修改：改为输入地点详情 -->
          <div class="form-group">
            <label>📍 地点名称</label>
            <input v-model="formData.locationName" placeholder="如：西湖" />
          </div>

          <div class="form-row">
            <div class="form-group half">
              <label>纬度</label>
              <input v-model.number="formData.lat" placeholder="30.259" type="number" step="any" />
            </div>
            <div class="form-group half">
              <label>经度</label>
              <input v-model.number="formData.lng" placeholder="120.150" type="number" step="any" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group half">
              <label>📅 日期</label>
              <input v-model="formData.visitTime" type="date" />
            </div>
            <div class="form-group half">
              <label>🌍 国家/地区</label>
              <input v-model="formData.country" placeholder="中国" />
            </div>
          </div>

          <div class="form-group">
            <label>📝 游记内容</label>
            <textarea v-model="formData.content" placeholder="写点什么…" rows="4"></textarea>
          </div>

          <div class="form-group">
            <label>📸 照片上传</label>
            <div class="photo-upload-area" @click="triggerFileInput">
              <span>+ 点击上传照片</span>
              <input 
                type="file" 
                accept="image/*" 
                multiple 
                ref="fileInput" 
                @change="handlePhotoUpload" 
                style="display:none;" 
              />
            </div>
            <div v-if="formData.previewPhotos.length > 0" class="photo-preview">
              <div v-for="(photo, index) in formData.previewPhotos" :key="index" class="photo-item-wrapper">
                <img :src="photo" class="preview-img" alt="预览" />
                <button class="remove-btn" @click="removePhoto(index)">×</button>
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <label>🏷️ 标签</label>
            <input v-model="formData.tagsStr" placeholder="输入标签，用逗号分隔 (如: 风景,美食)" />
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn-cancel" @click="showForm = false">取消</button>
          <button class="btn-save" @click="handleSubmit">发布足迹</button>
        </div>
      </div>
    </div>

    <!-- 编辑足迹弹窗 -->
    <div v-if="showEditForm" class="modal-overlay" @click.self="showEditForm = false">
      <div class="modal">
        <div class="modal-header">
          <span class="modal-title">✏️ 编辑足迹</span>
          <button class="modal-close" @click="showEditForm = false">×</button>
        </div>

        <div class="modal-body">
          <div class="form-group">
            <label>📝 游记标题</label>
            <input v-model="editForm.title" placeholder="给你的足迹起个名字" />
          </div>

          <div class="form-group">
            <label>📍 地点名称</label>
            <input v-model="editForm.locationName" placeholder="如：西湖" />
          </div>

          <div class="form-row">
            <div class="form-group half">
              <label>纬度</label>
              <input v-model.number="editForm.lat" type="number" step="any" />
            </div>
            <div class="form-group half">
              <label>经度</label>
              <input v-model.number="editForm.lng" type="number" step="any" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group half">
              <label>📅 日期</label>
              <input v-model="editForm.visitTime" type="date" />
            </div>
            <div class="form-group half">
              <label>🌍 国家/地区</label>
              <input v-model="editForm.country" placeholder="中国" />
            </div>
          </div>

          <div class="form-group">
            <label>📝 游记内容</label>
            <textarea v-model="editForm.content" placeholder="写点什么…" rows="4"></textarea>
          </div>

          <div class="form-group">
            <label>🏷️ 标签</label>
            <input v-model="editForm.tagsStr" placeholder="输入标签，用逗号分隔 (如: 风景,美食)" />
          </div>

          <div class="form-group">
            <label>🔒 可见性</label>
            <select v-model.number="editForm.visibility">
              <option :value="0">🌍 公开</option>
              <option :value="1">🔒 仅自己</option>
              <option :value="2">👥 仅好友</option>
            </select>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn-cancel" @click="showEditForm = false">取消</button>
          <button class="btn-save" @click="handleUpdate">保存修改</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
// 引入游记和地点的 API
import { createPostApi, pagePostApi, deletePostApi, updatePostApi } from '../api/community'; 
import { addLocationApi } from '../api/location'; 
import { getPersonalInfoApi } from '../api/user'; 

// 状态管理
const posts = ref([]);
const loading = ref(false);
const showForm = ref(false);
const showEditForm = ref(false);       // 编辑弹窗开关
const editingPostId = ref(null);        // 正在编辑的帖子 ID
// 👇 当前登录用户完整信息（含 id + username），onMounted 时从后端 /user/currentUser 拉取
//    之前用 localStorage.getItem('userId')，但登录时根本没存这个 key → 永远为空 → 作者按钮永远不显示
const currentUser = ref({ id: '', username: '', nickname: '' });

// 编辑表单
const editForm = reactive({
  title: '',
  locationName: '',
  lat: null,
  lng: null,
  country: '',
  province: '',
  content: '',
  visitTime: '',
  tagsStr: '',
  visibility: 0
});

// 分页数据
const pagination = reactive({
  current: 1,
  size: 5,
  total: 0
});

// 表单数据 - 修改为包含地点详情
const formData = reactive({
  title: '',
  locationName: '',
  lat: null,
  lng: null,
  country: '',
  province: '',
  content: '',
  visitTime: '',
  tagsStr: '',
  previewPhotos: [], 
  visibility: 0 
});

// 初始化：先拉当前用户信息（拿到 id 用于作者判断 + username 用于显示），再加载帖子
onMounted(async () => {
  await fetchCurrentUser();
  fetchPosts();
});

// 获取当前登录用户完整信息 —— 这是修复的关键
// 后端 GET /user/currentUser 返回完整 User 实体（id, username, nickname ...）
async function fetchCurrentUser() {
  try {
    const res = await getPersonalInfoApi();
    if (res.code === 200 && res.data) {
      currentUser.value = res.data;
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
  }
}

// 获取帖子列表
async function fetchPosts() {
  loading.value = true;
  try {
    const res = await pagePostApi({ 
      current: pagination.current, 
      size: pagination.size 
    });
    
    if (res.code === 200) {
      // 后端 /travel/page 只返回 Travel 实体（含 userId 不含 username）
      // 这里用当前登录用户的 username 补上，解决作者名显示为 "用户 xxx" 的问题
      posts.value = res.data.records.map(item => ({
        ...item,
        username: currentUser.value.nickname || currentUser.value.username || '',
        likeCount: item.likesCount || 0,  // 后端字段是 likesCount，前端用 likeCount
        expanded: false,
        isLiked: false,
        photos: item.photos ? item.photos.split(',') : []
      }));
      pagination.total = res.data.total;
    }
  } catch (error) {
    console.error('获取列表失败:', error);
  } finally {
    loading.value = false;
  }
}

// 切换点赞
async function toggleLike(post) {
  const newCount = post.isLiked ? post.likeCount - 1 : post.likeCount + 1;
  // 乐观更新 UI
  post.isLiked = !post.isLiked;
  post.likeCount = newCount;

  try {
    // 👇 修复：必须接收返回值
    const res = await updatePostApi({
      travelId: post.id,
      likesCount: newCount
    });
    if (res && res.code !== 200) {
      throw new Error(res.message || '点赞失败');
    }
  } catch (error) {
    console.error('点赞失败:', error);
    // 回滚 UI
    post.isLiked = !post.isLiked;
    post.likeCount = post.isLiked ? newCount + 1 : newCount - 1;
    alert('操作失败：' + (error.message || '请重试'));
  }
}

// 删除足迹
async function handleDelete(id) {
  if (!confirm('确定要删除这条足迹吗？')) return;
  try {
    const res = await deletePostApi(id);
    if (res && res.code === 200) {
      // 仅在成功时从列表移除
      posts.value = posts.value.filter(p => p.id !== id);
      pagination.total = Math.max(0, pagination.total - 1);
    } else {
      alert('删除失败：' + (res?.message || '未知错误'));
    }
  } catch (error) {
    console.error('删除出错', error);
    alert('删除失败，请重试');
  }
}

// 隐藏/公开足迹
async function handleHide(post) {
  const newVisibility = post.visibility === 1 ? 0 : 1;
  try {
    // 👇 修复：之前根本没接 res，整个函数直接 ReferenceError
    const res = await updatePostApi({
      travelId: post.id,
      visibility: newVisibility
    });
    if (res && res.code === 200) {
      post.visibility = newVisibility;
      alert(newVisibility === 1 ? '已设为私密' : '已设为公开');
    } else {
      alert('操作失败：' + (res?.message || '未知错误'));
    }
  } catch (error) {
    console.error('隐藏出错', error);
    alert('操作失败，请重试');
  }
}

// 打开编辑弹窗，把当前帖子数据填进 editForm
function openEditForm(post) {
  editingPostId.value = post.id;
  editForm.title = post.title || '';
  editForm.locationName = post.locationName || '';
  editForm.lat = post.lat;
  editForm.lng = post.lng;
  editForm.country = post.country || '';
  editForm.province = post.province || '';
  editForm.content = post.content || '';
  editForm.visitTime = post.visitTime ? post.visitTime.split('T')[0] : '';
  // 后端 tags 可能是 JSON 数组或字符串，统一转成字符串方便编辑
  if (Array.isArray(post.tags)) {
    editForm.tagsStr = post.tags.join(',');
  } else if (typeof post.tags === 'string') {
    editForm.tagsStr = post.tags;
  } else {
    editForm.tagsStr = '';
  }
  editForm.visibility = post.visibility ?? 0;
  showEditForm.value = true;
}

// 提交编辑
async function handleUpdate() {
  if (!editForm.title || !editForm.locationName) {
    alert('请填写标题和地点名称');
    return;
  }

  try {
    const tags = editForm.tagsStr
      .split(',').map(t => t.trim()).filter(t => t);

    const payload = {
      travelId: editingPostId.value,
      title: editForm.title,
      content: editForm.content,
      visitTime: editForm.visitTime || null,
      tags: tags,
      visibility: editForm.visibility
    };

    const res = await updatePostApi(payload);
    if (res && res.code === 200) {
      alert('修改成功！');
      showEditForm.value = false;
      // 局部更新列表项，避免重新拉接口
      const target = posts.value.find(p => p.id === editingPostId.value);
      if (target) {
        target.title = editForm.title;
        target.content = editForm.content;
        target.tags = tags;
        target.visibility = editForm.visibility;
        target.locationName = editForm.locationName;
        target.lat = editForm.lat;
        target.lng = editForm.lng;
        target.country = editForm.country;
        target.visitTime = editForm.visitTime;
      }
    } else {
      alert('修改失败：' + (res?.message || '未知错误'));
    }
  } catch (error) {
    console.error('编辑出错', error);
    alert('编辑失败，请重试');
  }
}

// 核心修改：提交逻辑
async function handleSubmit() {
  // 1. 简单校验
  if(!formData.title || !formData.locationName) {
    alert('请填写标题和地点名称');
    return;
  }

  try {
    // 2. 步骤一：先添加地点，获取 locationId
    const locationPayload = {
      locationName: formData.locationName,
      lat: formData.lat,
      lng: formData.lng,
      country: formData.country,
      province: formData.province,
      // 根据后端 AddLocationRequest 需要的字段填充，没有的填空字符串
      continent: '', 
      address: '' 
    };

    console.log("正在创建地点...", locationPayload);
    const locRes = await addLocationApi(locationPayload);

    // 判断地点是否创建成功
    if (locRes.code !== 200 || !locRes.data.locationId) {
      alert('地点创建失败：' + (locRes.message || '未知错误'));
      return;
    }

    const newLocationId = locRes.data.locationId;
    console.log("地点创建成功，ID:", newLocationId);

    // 3. 步骤二：使用返回的 ID 创建游记
    const tags = formData.tagsStr.split(',').map(t => t.trim()).filter(t => t);
    
    const travelPayload = {
      title: formData.title,
      locationId: newLocationId, // 使用新获取的 ID
      content: formData.content,
      visitTime: formData.visitTime,
      tags: tags,
      visibility: formData.visibility
    };

    console.log("正在创建游记...", travelPayload);
    await createPostApi(travelPayload);

    alert('发布成功！');
    showForm.value = false;
    resetForm();
    fetchPosts(); 

  } catch (error) {
    console.error('发布流程出错', error);
    alert('发布失败，请检查网络或控制台日志');
  }
}

// 翻页
function changePage(page) {
  pagination.current = page;
  fetchPosts();
}

// 工具函数
// 判断当前用户是否是该帖子的作者
// 关键修复：之前用 localStorage.getItem('userId')，但登录时从未存过这个 key
// 现在改用从后端 /user/currentUser 实时拿到的 currentUser.value.id
function isOwner(post) {
  if (!post || !post.userId) return false;
  if (!currentUser.value.id) return false;
  return String(post.userId) === String(currentUser.value.id);
}

// 判断是否需要"展开/收起"按钮 —— 阈值调小一点，便于短内容也能展开
function isContentLong(post) {
  const textLen = post.content ? post.content.length : 0;
  const imgLen = post.photos ? post.photos.length : 0;
  return textLen > 30 || imgLen > 0;
}

function formatTime(time) {
  if (!time) return '';
  return time.split('T')[0];
}

const fileInput = ref(null);
function triggerFileInput() {
  fileInput.value.click();
}

function handlePhotoUpload(event) {
  const files = event.target.files;
  if (!files) return;
  
  for (let file of files) {
    const reader = new FileReader();
    reader.onload = (e) => {
      formData.previewPhotos.push(e.target.result);
    };
    reader.readAsDataURL(file);
  }
}

function removePhoto(index) {
  formData.previewPhotos.splice(index, 1);
}

function resetForm() {
  formData.title = '';
  formData.locationName = '';
  formData.lat = null;
  formData.lng = null;
  formData.country = '';
  formData.province = '';
  formData.content = '';
  formData.visitTime = '';
  formData.tagsStr = '';
  formData.previewPhotos = [];
}
</script>

<style scoped>
/* 保持原有的样式不变 */
.community-container {
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
  font-weight: 600;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  padding: 16px 24px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  margin-bottom: 20px;
}

.publish-btn {
  background-color: #2d8cf0;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.3s;
}

.publish-btn:hover {
  background-color: #1a6fc9;
}

.post-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  position: relative;
}

.author-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: #e8eaed;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 18px;
  margin-right: 12px;
}

.author-name {
  font-weight: 600;
  font-size: 16px;
  color: #333;
}

.post-location {
  font-size: 13px;
  color: #86909c;
  margin-top: 2px;
}

.author-actions {
  position: absolute;
  right: 0;
  top: 0;
}

.action-icon {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  margin-left: 10px;
  opacity: 0.6;
}

.action-icon:hover {
  opacity: 1;
}

.post-title {
  margin: 0 0 12px 0;
  font-size: 18px;
  color: #333;
}

.content-wrapper {
  overflow: hidden;
  transition: max-height 0.3s ease;
}

.content-wrapper.collapsed {
  max-height: 80px;
  position: relative;
}

.content-wrapper.collapsed::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 40px;
  background: linear-gradient(transparent, #fff);
}

.post-content {
  color: #4e5969;
  line-height: 1.6;
  margin-bottom: 12px;
  white-space: pre-wrap;
}

.tags-row {
  margin-bottom: 12px;
}

.tag-item {
  display: inline-block;
  padding: 4px 10px;
  margin-right: 8px;
  background: #f2f3f5;
  color: #2d8cf0;
  border-radius: 4px;
  font-size: 13px;
}

.photos-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin-top: 12px;
}

.photo-item {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 6px;
  cursor: pointer;
}

.expand-btn {
  color: #2d8cf0;
  font-size: 14px;
  cursor: pointer;
  margin-top: 8px;
  display: inline-block;
}

.post-footer {
  border-top: 1px solid #f2f3f5;
  padding-top: 12px;
  margin-top: 12px;
  display: flex;
}

.interact-btn {
  background: none;
  border: none;
  color: #86909c;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  margin-right: 20px;
}

.interact-btn.active {
  color: #f53f3f;
}

.interact-btn .icon {
  margin-right: 4px;
}

.pagination-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  gap: 20px;
}

.page-btn {
  padding: 8px 16px;
  border: 1px solid #e5e6eb;
  background: #fff;
  border-radius: 6px;
  cursor: pointer;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  width: 500px;
  max-height: 90vh;
  background: #fff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 10px 20px rgba(0,0,0,0.1);
}

.modal-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  font-weight: 600;
  font-size: 18px;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #86909c;
}

.modal-body {
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.form-group input, 
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #e5e6eb;
  border-radius: 6px;
  box-sizing: border-box;
  transition: border-color 0.2s;
  background: #fff;
  font-size: 14px;
}

.form-group input:focus, 
.form-group textarea:focus,
.form-group select:focus {
  border-color: #2d8cf0;
  outline: none;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-group.half {
  flex: 1;
}

.photo-upload-area {
  border: 1px dashed #e5e6eb;
  padding: 20px;
  text-align: center;
  color: #86909c;
  cursor: pointer;
  border-radius: 6px;
}

.photo-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 12px;
}

.photo-item-wrapper {
  position: relative;
  width: 80px;
  height: 80px;
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.remove-btn {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #ff4d4f;
  color: white;
  border: none;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  line-height: 18px;
  cursor: pointer;
  font-size: 12px;
}

.modal-footer {
  padding: 12px 20px;
  border-top: 1px solid #f0f0f0;
  text-align: right;
}

.btn-cancel {
  padding: 8px 20px;
  border: 1px solid #e5e6eb;
  background: #fff;
  border-radius: 6px;
  margin-right: 10px;
  cursor: pointer;
}

.btn-save {
  padding: 8px 20px;
  background: #2d8cf0;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
</style>
