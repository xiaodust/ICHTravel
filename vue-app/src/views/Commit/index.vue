<template>
  <div class="heritage-community">
    <!-- 顶部导航栏 -->
    <nav class="navbar">
      <div class="logo" @click="$router.push('/home')">点苏记</div>
      <div class="search-bar">
        <input type="text" placeholder="搜索非遗技艺、传承人、作品..." @keyup.enter="handleSearch" />
        <i class="search-icon" @click="handleSearch">🔍</i>
      </div>
      <div class="nav-actions">
        <button class="publish-btn" @click="showPublishModal = true">发布</button>
        <div class="avatar" @click="$router.push('/user-center')">
          <img src="https://picsum.photos/40/40?random=user" alt="用户头像" />
        </div>
      </div>
    </nav>

    <!-- 主体内容区 -->
    <div class="main-content">
      <!-- 左侧边栏 - 分类导航 -->
      <aside class="sidebar">
        <ul class="category-list">
          <li class="category-item" :class="{ active: activeCategory === 'recommend' }" @click="changeCategory('recommend')">推荐</li>
          <li class="category-item" :class="{ active: activeCategory === 'all' }" @click="changeCategory('all')">全部</li>
          <li class="category-item" :class="{ active: activeCategory === 'suxiu' }" @click="changeCategory('suxiu')">苏绣</li>
          <li class="category-item" :class="{ active: activeCategory === 'yunjin' }" @click="changeCategory('yunjin')">云锦</li>
          <li class="category-item" :class="{ active: activeCategory === 'zisha' }" @click="changeCategory('zisha')">紫砂</li>
          <li class="category-item" :class="{ active: activeCategory === 'jianzhi' }" @click="changeCategory('jianzhi')">剪纸</li>
          <li class="category-item" :class="{ active: activeCategory === 'mudiao' }" @click="changeCategory('mudiao')">木雕</li>
          <li class="category-item" :class="{ active: activeCategory === 'taoci' }" @click="changeCategory('taoci')">陶瓷</li>
          <li class="category-item" :class="{ active: activeCategory === 'music' }" @click="changeCategory('music')">传统音乐</li>
          <li class="category-item" :class="{ active: activeCategory === 'opera' }" @click="changeCategory('opera')">传统戏剧</li>
        </ul>
      </aside>

      <!-- 中间内容区 - 笔记流 -->
      <div class="note-feed">
        <div class="note-card" v-for="(note, index) in filteredNotes" :key="note.id">
          <!-- 作者信息 -->
          <div class="note-author">
            <img :src="note.authorAvatar" alt="作者头像" class="author-avatar" @click="viewUser(note.authorName)" />
            <div class="author-info">
              <div class="author-name" @click="viewUser(note.authorName)">{{ note.authorName }}</div>
              <div class="author-tag">{{ note.authorTag }}</div>
            </div>
            <button class="follow-btn" v-if="!note.isFollowing" @click.stop="toggleFollow(note)">关注</button>
            <button class="following-btn" v-else @click.stop="toggleFollow(note)">已关注</button>
          </div>

          <!-- 笔记内容 -->
          <div class="note-content">
            <p class="note-text">{{ note.content }}</p>
            <div class="note-images" :class="`image-count-${note.images.length}`">
              <img 
                v-for="(img, i) in note.images" 
                :key="i" 
                :src="img" 
                alt="笔记图片" 
                class="note-img"
                @click="viewImage(img)"
              />
            </div>
          </div>

          <!-- 互动区域 -->
          <div class="note-actions">
            <button class="action-btn" :class="{ liked: note.isLiked }" @click="toggleLike(note)">
              <i>{{ note.isLiked ? '❤️' : '♡' }}</i>
              <span>{{ note.likeCount }}</span>
            </button>
            <button class="action-btn" @click="showCommentModal(note)">
              <i>💬</i>
              <span>{{ note.commentCount }}</span>
            </button>
            <button class="action-btn" @click="toggleCollect(note)">
              <i>{{ note.isCollected ? '📌' : '📎' }}</i>
              <span>收藏</span>
            </button>
            <button class="action-btn" @click="shareNote(note)">
              <i>🔗</i>
              <span>分享</span>
            </button>
          </div>

          <!-- 评论预览 -->
          <div class="comment-preview" v-if="note.comments.length > 0">
            <div class="comment-item" v-for="(comment, i) in note.comments.slice(0, 2)" :key="i">
              <span class="comment-user">{{ comment.user }}:</span>
              <span class="comment-text">{{ comment.content }}</span>
            </div>
            <div class="view-more-comments" v-if="note.comments.length > 2" @click="showCommentModal(note)">
              查看全部 {{ note.commentCount }} 条评论
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧边栏 - 推荐关注 -->
      <aside class="right-sidebar">
        <div class="sidebar-card">
          <h3 class="sidebar-title">推荐传承人</h3>
          <div class="suggested-user" v-for="(user, index) in suggestedUsers" :key="index">
            <img :src="user.avatar" alt="推荐用户" class="user-avatar" @click="viewUser(user.name)" />
            <div class="user-info">
              <div class="user-name" @click="viewUser(user.name)">{{ user.name }}</div>
              <div class="user-desc">{{ user.desc }}</div>
            </div>
            <button class="follow-btn-sm" @click="followSuggestedUser(user)">关注</button>
          </div>
        </div>

        <div class="sidebar-card">
          <h3 class="sidebar-title">热门话题</h3>
          <div class="topic-item" v-for="(topic, index) in hotTopics" :key="index" @click="viewTopic(topic.name)">
            <span class="topic-hash">#</span>
            <span class="topic-name">{{ topic.name }}</span>
            <span class="topic-count">{{ topic.count }} 篇笔记</span>
          </div>
        </div>
      </aside>
    </div>

    <!-- 发布笔记弹窗 -->
    <div class="modal-backdrop" v-if="showPublishModal">
      <div class="publish-modal">
        <div class="modal-header">
          <h3>发布非遗笔记</h3>
          <button class="close-btn" @click="showPublishModal = false">×</button>
        </div>
        <div class="modal-body">
          <textarea 
            class="note-input" 
            placeholder="分享你的非遗体验、技艺心得或作品展示..."
            v-model="newNoteContent"
          ></textarea>
          <div class="image-upload-area" @click="triggerImageUpload">
            <div class="upload-btn">+ 上传图片</div>
            <input type="file" class="image-upload-input" ref="imageUpload" multiple accept="image/*" @change="handleImageUpload" />
          </div>
          <div class="topic-selector">
            <input type="text" placeholder="添加话题，如 #苏绣#" v-model="newNoteTopic" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="showPublishModal = false">取消</button>
          <button class="confirm-btn" @click="publishNote">发布</button>
        </div>
      </div>
    </div>

    <!-- 评论弹窗 -->
    <div class="modal-backdrop" v-if="showCommentBox">
      <div class="publish-modal comment-modal">
        <div class="modal-header">
          <h3>评论 ({{ currentNote?.commentCount || 0 }})</h3>
          <button class="close-btn" @click="showCommentBox = false">×</button>
        </div>
        <div class="modal-body comment-body">
          <div class="comment-list">
            <div class="comment-item" v-for="(comment, i) in currentNote?.comments || []" :key="i">
              <img :src="`https://picsum.photos/40/40?random=${i}`" alt="用户头像" class="comment-avatar" />
              <div class="comment-content">
                <div class="comment-user">{{ comment.user }}</div>
                <div class="comment-text">{{ comment.content }}</div>
              </div>
            </div>
          </div>
          <div class="comment-input-area">
            <input 
              type="text" 
              placeholder="写下你的评论..." 
              v-model="newComment"
              @keyup.enter="submitComment"
            />
            <button class="send-comment" @click="submitComment">发送</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 图片预览弹窗 -->
    <div class="modal-backdrop" v-if="showImagePreview" @click="showImagePreview = false">
      <div class="image-preview-container" @click.stop>
        <img :src="previewImageUrl" alt="图片预览" class="preview-image" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, getCurrentInstance } from 'vue';
import { useRouter } from 'vue-router';

// 路由实例
const router = useRouter();

// 状态管理
const showPublishModal = ref(false);
const showCommentBox = ref(false);
const showImagePreview = ref(false);
const previewImageUrl = ref('');
const activeCategory = ref('recommend');
const newNoteContent = ref('');
const newNoteTopic = ref('');
const newComment = ref('');
const currentNote = ref(null);
const imageUpload = ref(null);

// 笔记数据
const notes = ref([
  {
    id: 1,
    category: 'suxiu',
    authorAvatar: 'https://picsum.photos/50/50?random=1',
    authorName: '苏绣传承人李老师',
    authorTag: '苏绣 · 国家级传承人',
    isFollowing: false,
    content: '今天分享一幅新完成的苏绣作品《江南春色》，采用了传统平针绣和乱针绣结合的手法，耗时三个月完成。苏绣的魅力在于它的细腻和灵动，每一针都凝聚着匠人的心血。',
    images: [
      'https://picsum.photos/600/400?random=10',
      'https://picsum.photos/600/400?random=11'
    ],
    likeCount: 1243,
    isLiked: false,
    commentCount: 89,
    isCollected: false,
    comments: [
      { user: '非遗爱好者', content: '太美了！请问李老师收徒吗？' },
      { user: '手工达人', content: '这种配色太高级了，学习了！' }
    ]
  },
  {
    id: 2,
    category: 'zisha',
    authorAvatar: 'https://picsum.photos/50/50?random=2',
    authorName: '紫砂匠人王师傅',
    authorTag: '紫砂壶 · 工艺大师',
    isFollowing: true,
    content: '新出窑的一批紫砂壶，采用黄龙山原矿紫泥，全手工制作。每一把壶都有自己的特点，欢迎品鉴。#紫砂艺术 #手工制作',
    images: [
      'https://picsum.photos/600/400?random=12',
      'https://picsum.photos/600/400?random=13',
      'https://picsum.photos/600/400?random=14'
    ],
    likeCount: 856,
    isLiked: true,
    commentCount: 42,
    isCollected: false,
    comments: [
      { user: '茶文化爱好者', content: '第三把壶的器型太漂亮了，请问出吗？' }
    ]
  },
  {
    id: 3,
    category: 'yunjin',
    authorAvatar: 'https://picsum.photos/50/50?random=3',
    authorName: '云锦大师张',
    authorTag: '云锦织造 · 非遗传承人',
    isFollowing: false,
    content: '云锦的织造工艺极为复杂，一天只能织出几厘米。这是新完成的"孔雀羽"纹样，采用了真金线织造，耗时半年才完成。#云锦 #传统织物',
    images: [
      'https://picsum.photos/600/400?random=15',
    ],
    likeCount: 987,
    isLiked: false,
    commentCount: 56,
    isCollected: true,
    comments: [
      { user: '历史系学生', content: '太精美了！这和故宫收藏的那件很像' },
      { user: '设计师', content: '这种配色太惊艳了，现代设计也能借鉴' }
    ]
  }
]);

// 推荐用户
const suggestedUsers = ref([
  {
    id: 1,
    avatar: 'https://picsum.photos/50/50?random=6',
    name: '剪纸艺人刘',
    desc: '非遗剪纸艺术创作者'
  },
  {
    id: 2,
    avatar: 'https://picsum.photos/50/50?random=7',
    name: '陶瓷大师陈',
    desc: '景德镇陶瓷技艺传承人'
  },
  {
    id: 3,
    avatar: 'https://picsum.photos/50/50?random=8',
    name: '古琴演奏家吴',
    desc: '传统古琴艺术传承人'
  }
]);

// 热门话题
const hotTopics = ref([
  { name: '非遗传承', count: 2453 },
  { name: '传统手工艺', count: 1876 },
  { name: '苏绣之美', count: 1245 },
  { name: '非遗新体验', count: 987 },
  { name: '匠人精神', count: 856 }
]);

// 根据分类过滤笔记
const filteredNotes = computed(() => {
  if (activeCategory.value === 'all' || activeCategory.value === 'recommend') {
    return notes.value;
  }
  return notes.value.filter(note => note.category === activeCategory.value);
});

// 方法：左侧分类导航切换
const changeCategory = (category) => {
  activeCategory.value = category;
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

// 方法：关注/取消关注作者
const toggleFollow = (note) => {
  note.isFollowing = !note.isFollowing;
  if (note.isFollowing) {
    alert(`已关注 ${note.authorName}`);
  } else {
    alert(`已取消关注 ${note.authorName}`);
  }
};

// 方法：点赞/取消点赞
const toggleLike = (note) => {
  if (note.isLiked) {
    note.likeCount -= 1;
  } else {
    note.likeCount += 1;
  }
  note.isLiked = !note.isLiked;
};

// 方法：收藏/取消收藏
const toggleCollect = (note) => {
  note.isCollected = !note.isCollected;
  if (note.isCollected) {
    alert('收藏成功');
  } else {
    alert('已取消收藏');
  }
};

// 方法：分享笔记
const shareNote = (note) => {
  alert(`分享笔记：${note.content.substring(0, 20)}...`);
  // 实际项目中这里会调用分享API
};

// 方法：查看评论
const showCommentModal = (note) => {
  currentNote.value = JSON.parse(JSON.stringify(note)); // 深拷贝避免直接修改原数据
  newComment.value = '';
  showCommentBox.value = true;
  
  // 等待弹窗渲染完成后滚动到底部
  nextTick(() => {
    const commentList = document.querySelector('.comment-list');
    if (commentList) commentList.scrollTop = commentList.scrollHeight;
  });
};

// 方法：提交评论
const submitComment = () => {
  if (!newComment.value.trim() || !currentNote.value) return;
  
  currentNote.value.comments.push({
    user: '我',
    content: newComment.value
  });
  currentNote.value.commentCount += 1;
  
  // 更新原笔记数据
  const index = notes.value.findIndex(n => n.id === currentNote.value.id);
  if (index !== -1) {
    notes.value[index].comments = [...currentNote.value.comments];
    notes.value[index].commentCount = currentNote.value.commentCount;
  }
  
  newComment.value = '';
  
  // 自动滚动到底部
  nextTick(() => {
    const commentList = document.querySelector('.comment-list');
    if (commentList) commentList.scrollTop = commentList.scrollHeight;
  });
};

// 方法：查看用户主页
const viewUser = (username) => {
  router.push(`/user/${encodeURIComponent(username)}`);
};

// 方法：查看话题
const viewTopic = (topicName) => {
  router.push(`/topic/${encodeURIComponent(topicName)}`);
};

// 方法：查看图片
const viewImage = (imgUrl) => {
  previewImageUrl.value = imgUrl;
  showImagePreview.value = true;
};

// 方法：搜索功能
const handleSearch = () => {
  const searchInput = document.querySelector('.search-bar input');
  const keyword = searchInput.value.trim();
  if (keyword) {
    router.push(`/search?keyword=${encodeURIComponent(keyword)}`);
  }
};

// 方法：关注推荐用户
const followSuggestedUser = (user) => {
  alert(`已关注 ${user.name}`);
  // 从推荐列表中移除已关注用户
  const index = suggestedUsers.value.findIndex(u => u.id === user.id);
  if (index !== -1) {
    suggestedUsers.value.splice(index, 1);
  }
};

// 方法：发布笔记
const publishNote = () => {
  if (!newNoteContent.value.trim()) {
    alert('请输入笔记内容');
    return;
  }
  
  // 创建新笔记对象
  const newNote = {
    id: notes.value.length + 1,
    category: activeCategory.value !== 'recommend' ? activeCategory.value : 'all',
    authorAvatar: 'https://picsum.photos/40/40?random=user',
    authorName: '我',
    authorTag: '非遗爱好者',
    isFollowing: true,
    content: newNoteContent.value + (newNoteTopic.value ? ` ${newNoteTopic.value}` : ''),
    images: [],
    likeCount: 0,
    isLiked: false,
    commentCount: 0,
    isCollected: false,
    comments: []
  };
  
  // 添加到笔记列表最前面
  notes.value.unshift(newNote);
  
  // 重置表单并关闭弹窗
  newNoteContent.value = '';
  newNoteTopic.value = '';
  showPublishModal.value = false;
  
  alert('笔记发布成功！');
};

// 方法：触发图片上传
const triggerImageUpload = () => {
  if (imageUpload.value) {
    imageUpload.value.click();
  }
};

// 方法：处理图片上传
const handleImageUpload = (e) => {
  const files = e.target.files;
  if (files && files.length) {
    alert(`已选择 ${files.length} 张图片，准备上传`);
    // 实际项目中这里会处理图片上传逻辑
    // 简单演示：将图片转为base64预览
    Array.from(files).forEach(file => {
      const reader = new FileReader();
      reader.onload = function(event) {
        // 这里可以添加到预览列表
        console.log('图片预览地址:', event.target.result);
      };
      reader.readAsDataURL(file);
    });
    // 清空input值，允许重复选择同一张图片
    e.target.value = '';
  }
};
</script>

<style scoped>
/* 基础样式 */
.heritage-community {
  background-color: #f8f9fa;
  min-height: 100vh;
}

/* 导航栏样式 */
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
  background-color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo {
  font-size: 22px;
  font-weight: bold;
  color: #1E90FF;
  cursor: pointer;
}

.search-bar {
  flex: 1;
  max-width: 600px;
  margin: 0 20px;
  position: relative;
}

.search-bar input {
  width: 100%;
  padding: 10px 15px 10px 40px;
  border-radius: 20px;
  border: 1px solid #eee;
  background-color: #f5f7fa;
  font-size: 14px;
}

.search-icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  cursor: pointer;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.publish-btn {
  background-color: #1E90FF;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 6px 15px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.publish-btn:hover {
  background-color: #0d76d0;
}

.avatar img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  cursor: pointer;
}

/* 主体内容区 */
.main-content {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 10px;
  gap: 20px;
}

/* 左侧边栏 */
.sidebar {
  width: 200px;
  flex-shrink: 0;
}

.category-list {
  list-style: none;
  padding: 0;
  margin: 0;
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.category-item {
  padding: 12px 20px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.category-item:hover {
  background-color: #f5f7fa;
}

.category-item.active {
  background-color: #e6f0ff;
  color: #1E90FF;
  font-weight: 500;
  position: relative;
}

.category-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 4px;
  background-color: #1E90FF;
}

/* 笔记流区域 */
.note-feed {
  flex: 1;
  max-width: 600px;
}

.note-card {
  background-color: white;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.note-author {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  gap: 10px;
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  cursor: pointer;
}

.author-info {
  flex: 1;
}

.author-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 2px;
  cursor: pointer;
}

.author-tag {
  font-size: 12px;
  color: #666;
}

.follow-btn {
  background-color: white;
  color: #1E90FF;
  border: 1px solid #1E90FF;
  border-radius: 12px;
  padding: 3px 10px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.follow-btn:hover {
  background-color: #e6f0ff;
}

.following-btn {
  background-color: #e6f0ff;
  color: #1E90FF;
  border: 1px solid #1E90FF;
  border-radius: 12px;
  padding: 3px 10px;
  font-size: 12px;
  cursor: pointer;
}

.note-content {
  margin-bottom: 10px;
}

.note-text {
  font-size: 14px;
  margin-bottom: 10px;
  line-height: 1.5;
}

.note-images {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  margin-bottom: 10px;
}

.image-count-1 .note-img {
  width: 100%;
  height: 300px;
}

.image-count-2 .note-img,
.image-count-3 .note-img {
  width: calc(50% - 2.5px);
  height: 200px;
}

.image-count-3 .note-img:nth-child(1) {
  width: 100%;
  height: 200px;
}

.note-img {
  border-radius: 4px;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.2s;
}

.note-img:hover {
  transform: scale(1.01);
}

.note-actions {
  display: flex;
  justify-content: space-between;
  padding: 5px 0;
  border-top: 1px solid #f5f5f5;
  border-bottom: 1px solid #f5f5f5;
  margin-bottom: 10px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  background: none;
  border: none;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  padding: 5px 0;
  transition: color 0.2s;
}

.action-btn:hover {
  color: #1E90FF;
}

.action-btn.liked {
  color: #ff4444;
}

.comment-preview {
  font-size: 14px;
}

.comment-item {
  margin-bottom: 5px;
}

.comment-user {
  font-weight: 500;
  margin-right: 5px;
}

.view-more-comments {
  color: #1E90FF;
  font-size: 13px;
  cursor: pointer;
  margin-top: 5px;
}

/* 右侧边栏 */
.right-sidebar {
  width: 280px;
  flex-shrink: 0;
}

.sidebar-card {
  background-color: white;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.sidebar-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 15px;
  color: #333;
}

.suggested-user {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  cursor: pointer;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 2px;
  cursor: pointer;
}

.user-desc {
  font-size: 12px;
  color: #666;
}

.follow-btn-sm {
  background-color: white;
  color: #1E90FF;
  border: 1px solid #1E90FF;
  border-radius: 12px;
  padding: 2px 8px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.follow-btn-sm:hover {
  background-color: #e6f0ff;
}

.topic-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  cursor: pointer;
  transition: background-color 0.2s;
  padding: 5px;
  border-radius: 4px;
}

.topic-item:hover {
  background-color: #f5f7fa;
}

.topic-hash {
  color: #1E90FF;
  margin-right: 5px;
}

.topic-name {
  flex: 1;
  font-size: 14px;
}

.topic-count {
  font-size: 12px;
  color: #999;
}

/* 发布弹窗样式 */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.publish-modal {
  width: 100%;
  max-width: 500px;
  background-color: white;
  border-radius: 8px;
  animation: modalFadeIn 0.3s ease;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.close-btn {
  background: transparent;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #666;
}

.modal-body {
  padding: 20px;
}

.note-input {
  width: 100%;
  min-height: 120px;
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 10px;
  resize: none;
  font-size: 14px;
  margin-bottom: 15px;
}

.image-upload-area {
  border: 1px dashed #ddd;
  border-radius: 4px;
  padding: 20px;
  text-align: center;
  margin-bottom: 15px;
  cursor: pointer;
  transition: border-color 0.2s;
}

.image-upload-area:hover {
  border-color: #1E90FF;
}

.upload-btn {
  color: #1E90FF;
  font-size: 14px;
}

.topic-selector input {
  width: 100%;
  padding: 10px;
  border: 1px solid #eee;
  border-radius: 4px;
  font-size: 14px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #eee;
}

.cancel-btn {
  padding: 6px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
  transition: background-color 0.2s;
}

.cancel-btn:hover {
  background-color: #f5f5f5;
}

.confirm-btn {
  padding: 6px 15px;
  background-color: #1E90FF;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.confirm-btn:hover {
  background-color: #0d76d0;
}

/* 评论弹窗样式 */
.comment-modal .modal-body {
  max-height: 60vh;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.comment-list {
  flex: 1;
  margin-bottom: 15px;
  overflow-y: auto;
  max-height: 40vh;
}

.comment-item {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f5f5f5;
}

.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-user {
  font-weight: 500;
  margin-bottom: 5px;
  font-size: 13px;
}

.comment-text {
  font-size: 14px;
  line-height: 1.5;
}

.comment-input-area {
  display: flex;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #f5f5f5;
}

.comment-input-area input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 14px;
}

.send-comment {
  padding: 5px 15px;
  background-color: #1E90FF;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.send-comment:hover {
  background-color: #0d76d0;
}

/* 图片上传隐藏input */
.image-upload-input {
  display: none;
}

/* 图片预览样式 */
.image-preview-container {
  max-width: 90%;
  max-height: 90%;
}

.preview-image {
  max-width: 100%;
  max-height: 80vh;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.3);
}

/* 响应式调整 */
@media (max-width: 992px) {
  .right-sidebar {
    display: none;
  }
  
  .main-content {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .sidebar {
    display: none;
  }
  
  .search-bar {
    max-width: none;
    margin: 0 10px;
  }
  
  .logo {
    font-size: 18px;
  }
  
  .publish-btn {
    padding: 5px 10px;
    font-size: 13px;
  }
}
</style>