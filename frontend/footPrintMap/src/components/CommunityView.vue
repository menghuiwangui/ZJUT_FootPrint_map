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
      <!-- 如果没有帖子，显示占位提示 -->
      <div v-if="posts.length === 0" style="text-align: center; color: #888; padding: 40px;">
        社区还没有动态，快来发布第一条足迹吧！
      </div>

      <div v-for="post in posts" :key="post.id" class="post-card">
        <!-- 作者信息 -->
        <div class="post-header">
          <span class="avatar">👤</span>
          <div class="author-info">
            <!-- 暂时显示用户ID，后续可改为用户昵称 -->
            <div class="author-name">用户: {{ post.userId }}</div>
            <div class="post-tags">
              <span class="tag">#足迹{{ post.locationId }}</span>
              <span class="tag">#{{ post.visitTime }}</span>
            </div>
          </div>
        </div>

        <!-- 帖子内容 -->
        <p class="post-text">{{ post.note }}</p>
        
        <!-- 假设后端 photos 是逗号分隔的字符串，或者为空 -->
        <div v-if="post.photos" class="post-photos">
          <img :src="post.photos" class="post-photo" />
        </div>

        <!-- 互动区域：点赞和评论 -->
        <div class="post-actions">
          <button 
            class="action-btn" 
            :class="{ liked: post.isLiked }"
            @click="toggleLike(post)"
          >
            👍 {{ post.likeCount }}
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
import { ref, onMounted } from 'vue';
import { pagePostApi, updatePostApi } from '../api/community'; 

const posts = ref([]);

// 获取社区帖子列表
onMounted(async () => {
  await fetchPosts();
})

async function fetchPosts() {
  try {
    // 调用分页查询接口
    const res = await pagePostApi({ current: 1, size: 10 });
    if (res.code === 200) {
      posts.value = res.data.records.map(item => ({
        id: item.id,
        userId: item.userId,
        locationId: item.locationId || '未知',
        visitTime: item.visitTime || '',
        note: item.note || '该用户没有留下文字...',
        photos: item.photos || '', 
        likeCount: item.likeCount || 0, // 注意：后端 Travel 实体需要加这个字段
        isLiked: false,                 // 前端默认未点赞
        showComment: false,
        comments: [],                   // 前端暂存评论
        newComment: ''
      }));
    }
  } catch (error){
    console.error('获取帖子失败: ' + error.message);
  }
}

// 点赞功能
async function toggleLike(post) {
  // 如果已经点赞了，则取消点赞，否则点赞数+1
  const newLikeCount = post.isLiked ? post.likeCount - 1 : post.likeCount + 1;
  
  try {
    // 复用 updateTravel 接口
    // 发送给后端的是游记ID 和 更新后的点赞数
    const res = await updatePostApi({
      travelId: post.id,
      likeCount: newLikeCount
    });
    
    if (res.code === 200) {
      post.isLiked = !post.isLiked;
      post.likeCount = newLikeCount;
    }
  } catch (error) {
    console.error('点赞失败: ' + error.message);
  }
}

// 显示/隐藏评论
function toggleComment(post) {
  post.showComment = !post.showComment
}

// 提交评论
async function submitComment(post) {
  if (post.newComment.trim() === '') return;

  const commentText = post.newComment;

  // 后端暂时没有评论接口，这里只做前端模拟展示
  post.comments.push({
    user: '我',
    text: commentText
  })
  post.newComment = ''; 
  alert('评论成功（注：后端暂未实现评论保存接口，刷新后评论会消失）');
  
  /* 
  // 当你后端写好了评论接口后，使用下面这段代码：
  try {
    const res = await submitCommentApi({ postId: post.id, comment: commentText })
    if (res.code === 200) {
      post.comments.push({ user: '我', text: commentText })
      post.newComment = '' 
    }
  } catch (error) {
    console.error('提交评论失败: ' + error.message)
  }
  */
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
