<template>
  <div class="community-container">
    <h2 class="title">🌍 足迹社区广场</h2>
    
    <!-- 发帖按钮 -->
    <div class="share-bar">
      <span>分享你的最新足迹标签吧！</span>
      <button class="share-btn">+ 发布足迹</button>
    </div>

    <!-- 帖子列表 -->
    <div class="post-list">
      <div v-for="post in posts" :key="post.id" class="post-card">
        <!-- 作者信息 -->
        <div class="post-header">
          <span class="avatar">{{ post.avatar }}</span>
          <div class="author-info">
            <div class="author-name">{{ post.author }}</div>
            <div class="post-tags">
              <span v-for="tag in post.tags" :key="tag" class="tag">#{{ tag }}</span>
            </div>
          </div>
        </div>

        <!-- 帖子内容 -->
        <p class="post-text">{{ post.note }}</p>
        <div v-if="post.photos.length > 0" class="post-photos">
          <img v-for="(photo, index) in post.photos" :key="index" :src="photo" class="post-photo" />
        </div>

        <!-- 互动区域：点赞和评论 -->
        <div class="post-actions">
          <button 
            class="action-btn" 
            :class="{ liked: post.isLiked }"
            @click="toggleLike(post)"
          >
            👍 {{ post.likes }}
          </button>
          <button class="action-btn" @click="toggleComment(post)">
            💬 {{ post.comments.length }}
          </button>
        </div>

        <!-- 评论区 -->
        <div v-if="post.showComment" class="comment-section">
          <div v-for="(comment, idx) in post.comments" :key="idx" class="comment-item">
            <strong>{{ comment.user }}:</strong> {{ comment.text }}
          </div>
          
          <div class="comment-input-area">
            <input 
              v-model="post.newComment" 
              placeholder="说点什么..." 
              class="comment-input"
              @keyup.enter="submitComment(post)"
            />
            <button class="send-btn" @click="submitComment(post)">发送</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 模拟从后端获取的社区数据
const posts = ref([
  {
    id: 1,
    author: '背包客老李',
    avatar: '🧔',
    tags: ['西藏', '拉萨', '布达拉宫'],
    note: '终于来到了离天空最近的地方，心灵得到了净化！',
    photos: [],
    likes: 128,
    isLiked: false,
    showComment: false,
    newComment: '',
    comments: [
      { user: '旅行者A', text: '太羡慕了！我明年也要去！' },
      { user: '摄影爱好者', text: '这光线绝了，带了什么镜头？' }
    ]
  },
  {
    id: 2,
    author: '吃货小王',
    avatar: '👧',
    tags: ['成都', '美食', '火锅'],
    note: '成都的火锅真的是吃一次爱一次，微辣已经是我的极限了！',
    photos: [],
    likes: 56,
    isLiked: true,
    showComment: true,
    newComment: '',
    comments: [
      { user: '重庆网友', text: '来重庆试试老火锅，绝对更巴适！' }
    ]
  }
])

// 点赞/取消点赞
function toggleLike(post) {
  post.isLiked = !post.isLiked
  if (post.isLiked) {
    post.likes++
  } else {
    post.likes--
  }
  // TODO: 未来这里需要调用后端 API 同步点赞数到数据库
}

// 显示/隐藏评论
function toggleComment(post) {
  post.showComment = !post.showComment
}

// 提交评论
function submitComment(post) {
  if (post.newComment.trim() === '') return
  
  post.comments.push({
    user: '我', // 当前登录用户
    text: post.newComment
  })
  post.newComment = ''
  // TODO: 未来这里需要调用后端 API 把评论存到数据库
}
</script>

<style scoped>
.community-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}
.title {
  text-align: center;
  color: #333;
  margin-bottom: 20px;
}
.share-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  padding: 15px 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.share-btn {
  background: #FF8C00;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  font-weight: bold;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.post-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}
.avatar {
  font-size: 40px;
  margin-right: 12px;
  background: #f0f0f0;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.author-name {
  font-weight: bold;
  font-size: 16px;
}
.post-tags {
  margin-top: 4px;
}
.tag {
  font-size: 12px;
  color: #FF8C00;
  background: rgba(255, 140, 0, 0.1);
  padding: 2px 8px;
  border-radius: 10px;
  margin-right: 6px;
}
.post-text {
  color: #444;
  line-height: 1.6;
  margin-bottom: 15px;
}
.post-photos {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}
.post-photo {
  width: 100%;
  max-width: 200px;
  border-radius: 8px;
}

.post-actions {
  display: flex;
  gap: 20px;
  border-top: 1px solid #f0f0f0;
  padding-top: 15px;
}
.action-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  display: flex;
  align-items: center;
}
.action-btn.liked {
  color: #FF4500;
  font-weight: bold;
}

.comment-section {
  margin-top: 15px;
  background: #f9f9f9;
  padding: 15px;
  border-radius: 8px;
}
.comment-item {
  margin-bottom: 8px;
  font-size: 14px;
  color: #555;
}
.comment-input-area {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}
.comment-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 20px;
  outline: none;
}
.send-btn {
  background: #FF8C00;
  color: #fff;
  border: none;
  padding: 0 20px;
  border-radius: 20px;
  cursor: pointer;
}
</style>
