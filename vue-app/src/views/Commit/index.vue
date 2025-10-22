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
        <div class="avatar" @click="$router.push('/user-center/profile')">
          <img :src="currentUserAvatar" alt="用户头像" @error="onAvatarError" />
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
            <div class="comment-item" v-for="(comment, i) in note.comments.slice(0, COMMENT_PREVIEW_COUNT)" :key="i">
              <span class="comment-user">{{ comment.userName || '匿名用户' }}:</span>
              <span class="comment-text">{{ comment.content }}</span>
            </div>
            <div class="view-more-comments" v-if="note.comments.length > 2" @click="showCommentModal(note)">
              查看全部评论
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
          <!-- 新增：上传中的提示 -->
          <div class="uploading-tip" v-if="isUploadingImages">图片上传中，请稍候...</div>
          <!-- 新增：已上传图片预览 -->
          <div class="uploaded-images-preview" v-if="uploadedImages.length">
            <div class="preview-title">已上传图片</div>
            <div class="preview-grid">
              <img 
                v-for="(img, idx) in uploadedImages" 
                :key="idx" 
                :src="img" 
                alt="已上传图片" 
                class="preview-img"
              />
            </div>
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
              <img :src="comment.avatar || 'https://picsum.photos/40/40?random=default'" alt="用户头像" class="comment-avatar" />
              <div class="comment-content">
                <div class="comment-user">{{ comment.userName || '匿名用户' }}</div>
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
import { ref, computed, nextTick, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

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
// 新增：上传中的状态与已上传图片的URL集合
const isUploadingImages = ref(false);
const uploadedImages = ref([]);

// 右侧栏：推荐传承人（示例数据）
const suggestedUsers = ref([
  { id: 'u-heritage-1', name: '苏绣传人', desc: '苏绣针法与配色分享', avatar: 'https://picsum.photos/seed/suxiu/80/80' },
  { id: 'u-heritage-2', name: '龙舟鼓手', desc: '端午龙舟训练记录', avatar: 'https://picsum.photos/seed/longzhou/80/80' },
  { id: 'u-heritage-3', name: '苗族银匠', desc: '银饰打磨与纹饰', avatar: 'https://picsum.photos/seed/miao/80/80' },
  { id: 'u-heritage-4', name: '古琴雅士', desc: '琴曲赏析与练习', avatar: 'https://picsum.photos/seed/guqin/80/80' },
  { id: 'u-heritage-5', name: '年画匠人', desc: '木版年画开版与套印', avatar: 'https://picsum.photos/seed/nianhua/80/80' },
  { id: 'u-heritage-6', name: '戏曲脸谱匠人', desc: '勾线与配色的气韵', avatar: 'https://picsum.photos/seed/lianpu/80/80' },
  { id: 'u-heritage-7', name: '茶艺师', desc: '水温与出汤的拿捏', avatar: 'https://picsum.photos/seed/tea/80/80' },
  { id: 'u-heritage-8', name: '唐卡画师', desc: '矿物颜料与起稿细节', avatar: 'https://picsum.photos/seed/tangka/80/80' },
  { id: 'u-heritage-9', name: '景泰蓝工艺师', desc: '掐丝点蓝的火候', avatar: 'https://picsum.photos/seed/jingtai/80/80' },
  { id: 'u-heritage-10', name: '剪纸艺人', desc: '窗花剪影里的年味', avatar: 'https://picsum.photos/seed/paper/80/80' }
]);

// 右侧栏：热门话题（示例数据）
const hotTopics = ref([
  { name: '苏绣', count: 26 },
  { name: '龙舟', count: 19 },
  { name: '苗族银饰', count: 14 },
  { name: '古琴', count: 17 },
  { name: '木版年画', count: 21 },
  { name: '戏曲脸谱', count: 12 },
  { name: '茶艺', count: 18 },
  { name: '唐卡', count: 13 },
  { name: '景泰蓝', count: 11 },
  { name: '剪纸', count: 20 }
]);
const notes = ref([]);
const COMMENT_PREVIEW_COUNT = 2;

// 加载说说（后端分页接口）
const isLoadingNotes = ref(false);
const pageParam = ref({ pagination: 0, pageSize: 10 });
// 用户头像缓存，避免重复请求
const userAvatarCache = new Map();
const getUserAvatar = async (userId) => {
  if (!userId) return 'https://picsum.photos/50/50?random=default';
  if (userAvatarCache.has(userId)) return userAvatarCache.get(userId);
  try {
    const res = await axios.get(`http://localhost:8080/api/user/${userId}`);
    const avatar = res?.data?.data?.avatar || 'https://picsum.photos/50/50?random=default';
    userAvatarCache.set(userId, avatar);
    return avatar;
  } catch {
    return 'https://picsum.photos/50/50?random=default';
  }
};

// 新增：用户信息缓存（昵称/名称 + 头像）
const userProfileCache = new Map();
const getUserProfile = async (userId) => {
  if (!userId) return { userName: '匿名用户', avatar: 'https://picsum.photos/50/50?random=default' };
  if (userProfileCache.has(userId)) return userProfileCache.get(userId);
  try {
    const res = await axios.get(`http://localhost:8080/api/user/${userId}`);
    const data = res?.data?.data || {};
    const userName = data?.nickName || data?.name || '匿名用户';
    const avatar = data?.avatar || 'https://picsum.photos/50/50?random=default';
    const profile = { userName, avatar };
    userProfileCache.set(userId, profile);
    return profile;
  } catch (e) {
    console.error('获取用户信息失败:', e);
    return { userName: '匿名用户', avatar: 'https://picsum.photos/50/50?random=default' };
  }
};
const loadNotes = async () => {
  if (isLoadingNotes.value) return;
  isLoadingNotes.value = true;
  try {
    const res = await axios.get('http://localhost:8080/api/note/page', {
      params: {
        pagination: pageParam.value.pagination,
        pageSize: pageParam.value.pageSize
      }
    });
    if (res.data && res.data.success && res.data.code === '200') {
      const paging = res.data.data || {};
      const serverNotes = (paging.data || []).map(n => ({
        id: n.id,
        userId: n.userId,
        category: 'all',
        authorAvatar: 'https://picsum.photos/50/50?random=default',
        authorName: n.userName || '未知用户',
        authorTag: '非遗爱好者',
        isFollowing: false,
        content: n.context || '',
        images: Array.isArray(n.images) ? n.images : [],
        likeCount: typeof n.liked === 'number' ? n.liked : 0,
        isLiked: false,
        commentCount: typeof n.commentCount === 'number' ? n.commentCount : 0,
        isCollected: false,
        comments: []
      }));
      notes.value = serverNotes;
      // 拉取真实头像
      await Promise.all(notes.value.map(async (note) => {
        note.authorAvatar = await getUserAvatar(note.userId);
      }));
      // 拉取每条说说的评论用于预览（完整列表，模板内截取前N条）
      await Promise.all(notes.value.map(async (note) => {
        const comments = await fetchComments(note.id);
        note.comments = comments;
      }));
      // 同步分页信息（可选）
      if (typeof paging.pageNum === 'number') pageParam.value.pagination = paging.pageNum;
      if (typeof paging.pageSize === 'number') pageParam.value.pageSize = paging.pageSize;
    } else {
      console.warn('加载说说失败:', res.data?.message);
    }
  } catch (err) {
    console.error('请求/api/note/page失败:', err);
  } finally {
    isLoadingNotes.value = false;
  }
};

const currentUserAvatar = ref('https://picsum.photos/40/40?random=default');
const onAvatarError = () => {
  currentUserAvatar.value = 'https://picsum.photos/40/40?random=default';
};
const loadCurrentUserAvatar = async () => {
  try {
    let avatar = null;
    // 优先从localStorage读取
    const savedUserInfo = localStorage.getItem('userInfo');
    if (savedUserInfo) {
      const parsed = JSON.parse(savedUserInfo);
      avatar = parsed?.avatar || null;
      // 若本地无头像但有id，则从后端拉取
      if (!avatar && parsed?.id) {
        const res = await axios.get(`http://localhost:8080/api/user/${parsed.id}`);
        avatar = res?.data?.data?.avatar || null;
      }
    } else {
      // 尝试从session拿userId再请求后端
      const userId = sessionStorage.getItem('userId');
      if (userId) {
        const res = await axios.get(`http://localhost:8080/api/user/${userId}`);
        avatar = res?.data?.data?.avatar || null;
      }
    }
    currentUserAvatar.value = avatar || 'https://picsum.photos/40/40?random=default';
  } catch (e) {
    currentUserAvatar.value = 'https://picsum.photos/40/40?random=default';
  }
};

onMounted(() => {
  loadNotes();
  loadCurrentUserAvatar();
});

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
const likeInFlight = new Set();
const toggleLike = async (note) => {
  if (!note || !note.id) return;
  if (likeInFlight.has(note.id)) return; // 防重复点击
  likeInFlight.add(note.id);
  try {
    if (note.isLiked) {
      // 取消点赞
      const res = await axios.post('http://localhost:8080/api/note/unlike', { id: note.id }, {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        transformRequest: [(data) => `id=${encodeURIComponent(data.id)}`]
      });
      if (res.data && res.data.success && res.data.code === '200') {
        const updated = res.data.data;
        note.likeCount = typeof updated?.liked === 'number' ? updated.liked : Math.max(0, note.likeCount - 1);
        note.isLiked = false;
      } else {
        // 后端失败时回退本地
        note.likeCount = Math.max(0, note.likeCount - 1);
        note.isLiked = false;
      }
    } else {
      // 点赞
      const res = await axios.post('http://localhost:8080/api/note/like', { id: note.id }, {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        transformRequest: [(data) => `id=${encodeURIComponent(data.id)}`]
      });
      if (res.data && res.data.success && res.data.code === '200') {
        const updated = res.data.data;
        note.likeCount = typeof updated?.liked === 'number' ? updated.liked : note.likeCount + 1;
        note.isLiked = true;
      } else {
        note.likeCount += 1;
        note.isLiked = true;
      }
    }
  } catch (e) {
    // 网络错误时采用本地回退策略
    if (note.isLiked) {
      note.likeCount = Math.max(0, note.likeCount - 1);
      note.isLiked = false;
    } else {
      note.likeCount += 1;
      note.isLiked = true;
    }
    console.error('点赞接口异常:', e);
  } finally {
    likeInFlight.delete(note.id);
  }
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

// 从后端获取评论列表
const fetchComments = async (noteId) => {
  try {
    const res = await axios.get(`http://localhost:8080/api/comment/${noteId}`);
    if (res.data && res.data.success && res.data.code === '200') {
      const list = Array.isArray(res.data.data) ? res.data.data : [];
      // 并发拉取用户信息，填充昵称与头像
      const enriched = await Promise.all(list.map(async (c) => {
        const profile = await getUserProfile(c.userId);
        return {
          userId: c.userId || '',
          userName: c.userName || profile.userName,
          avatar: profile.avatar,
          content: c.content || '',
          gmtCreated: c.gmtCreated || null
        };
      }));
      return enriched;
    }
  } catch (e) {
    console.error('获取评论失败:', e);
  }
  return [];
};

// 方法：查看评论（打开弹窗并拉取后端评论）
const showCommentModal = async (note) => {
  currentNote.value = JSON.parse(JSON.stringify(note));
  newComment.value = '';
  // 拉取后端评论
  const comments = await fetchComments(note.id);
  currentNote.value.comments = comments;
  currentNote.value.commentCount = comments.length;
  showCommentBox.value = true;
  nextTick(() => {
    const commentList = document.querySelector('.comment-list');
    if (commentList) commentList.scrollTop = commentList.scrollHeight;
  });
};

// 方法：提交评论（调用后端接口）
const submitComment = async () => {
  if (!newComment.value.trim() || !currentNote.value) return;
  // 获取当前用户ID
  let userId = sessionStorage.getItem('userId');
  if (!userId) {
    try {
      const savedUserInfo = localStorage.getItem('userInfo');
      if (savedUserInfo) {
        const parsed = JSON.parse(savedUserInfo);
        userId = parsed?.id || parsed?.userId || '';
      }
    } catch {}
  }
  if (!userId) {
    alert('请先登录后再评论');
    return;
  }
  // 构造后端Comment对象
  const payload = {
    noteId: currentNote.value.id,
    userId: userId,
    content: newComment.value
  };
  try {
    const res = await axios.post('http://localhost:8080/api/comment/addcomment', payload, {
      headers: { 'Content-Type': 'application/json' }
    });
    if (res.data && res.data.success && res.data.code === '200') {
      // 重新拉取最新评论
      const comments = await fetchComments(currentNote.value.id);
      currentNote.value.comments = comments;
      currentNote.value.commentCount = comments.length;
      // 同步到原列表中的笔记
      const index = notes.value.findIndex(n => n.id === currentNote.value.id);
      if (index !== -1) {
        notes.value[index].comments = comments;
        notes.value[index].commentCount = comments.length;
      }
      newComment.value = '';
      nextTick(() => {
        const commentList = document.querySelector('.comment-list');
        if (commentList) commentList.scrollTop = commentList.scrollHeight;
      });
    } else {
      alert(res.data?.message || '评论失败');
    }
  } catch (e) {
    console.error('提交评论失败:', e);
    alert('提交评论失败，请稍后重试');
  }
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
const publishNote = async () => {
  if (!newNoteContent.value.trim()) {
    alert('请输入笔记内容');
    return;
  }

  // 组装上下文（文本 + 话题）
  const contextText = newNoteContent.value + (newNoteTopic.value ? ` ${newNoteTopic.value}` : '');

  // 获取用户信息（userId / userName）
  let userId = sessionStorage.getItem('userId') || '';
  let userName = '';
  try {
    const savedUserInfo = localStorage.getItem('userInfo');
    if (savedUserInfo) {
      const parsedUserInfo = JSON.parse(savedUserInfo);
      // 如果 session 中没有，则从 localStorage 兜底 userId
      if (!userId) {
        userId = parsedUserInfo.id || parsedUserInfo.userId || '';
      }
      // 始终从 localStorage 提取用户昵称/名称用于 userName
      userName = parsedUserInfo.nickName || parsedUserInfo.name || '';
    }
  } catch (e) {
    console.error('解析localStorage用户信息失败:', e);
  }

  try {
    // 向后端提交笔记
    const response = await axios.post('http://localhost:8080/api/note/add', {
      userId: userId || '',
      userName: userName || '我',
      context: contextText,
      images: uploadedImages.value
    });

    if (response.data && response.data.success) {
      // 创建前端展示用笔记对象
      const newNote = {
        id: (notes.value.length + 1).toString(),
        category: activeCategory.value !== 'recommend' ? activeCategory.value : 'all',
        authorAvatar: 'https://picsum.photos/40/40?random=user',
        authorName: userName || '我',
        authorTag: '非遗爱好者',
        isFollowing: true,
        content: contextText,
        images: [...uploadedImages.value],
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
      uploadedImages.value = [];
      showPublishModal.value = false;

      alert('笔记发布成功！');
    } else {
      alert(response.data?.message || '发布失败，请稍后重试');
    }
  } catch (error) {
    console.error('发布笔记异常:', error);
    alert(error.response?.data?.message || '网络错误，请检查后端服务是否启动');
  }
};

// 方法：触发图片上传
const triggerImageUpload = () => {
  if (imageUpload.value) {
    imageUpload.value.click();
  }
};

// 方法：处理图片上传
const handleImageUpload = async (e) => {
  const files = e.target.files;
  if (!files || !files.length) return;

  isUploadingImages.value = true;
  alert(`已选择 ${files.length} 张图片，开始上传...`);

  const uploadOne = (file) => new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = async (event) => {
      try {
        const base64Image = event.target.result;
        const fileExtension = (file.type && file.type.split('/')[1]) || 'jpg';
        // 调用后端上传接口
        const res = await axios.post('http://localhost:8080/api/posts/upload', {
          base64Image,
          fileExtension
        });
        if (res.data && res.data.success) {
          const imageUrl = res.data.data; // 后端返回的可访问URL
          uploadedImages.value.push(imageUrl);
          resolve(imageUrl);
        } else {
          reject(new Error(res.data?.message || '上传失败'));
        }
      } catch (err) {
        reject(err);
      }
    };
    reader.onerror = (err) => reject(err);
    reader.readAsDataURL(file);
  });

  try {
    await Promise.all(Array.from(files).map(uploadOne));
    alert('所有图片上传完成');
  } catch (err) {
    console.error('上传图片出错:', err);
    alert(err.message || '部分图片上传失败');
  } finally {
    isUploadingImages.value = false;
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
.comment-modal .modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
}

.comment-modal .modal-body {
  max-height: 60vh;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  padding: 12px 16px;
}

.comment-list {
  flex: 1;
  margin-bottom: 10px;
  overflow-y: auto;
  max-height: 40vh;
}

.comment-item {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px dashed #f0f0f0;
}

.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 1px solid #f2f2f2;
  flex-shrink: 0;
  object-fit: cover;
}

.comment-content {
  flex: 1;
}

.comment-user {
  font-weight: 600;
  margin-bottom: 4px;
  font-size: 14px;
  color: #333;
}

.comment-text {
  font-size: 14px;
  line-height: 1.6;
  color: #555;
  word-break: break-word;
}

.comment-input-area {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

.comment-input-area input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}

.comment-input-area input:focus {
  border-color: #4c9cff;
  box-shadow: 0 0 0 3px rgba(76, 156, 255, 0.15);
}

.send-comment {
  padding: 10px 14px;
  background-color: #4c9cff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.send-comment:hover {
  background-color: #3a89e6;
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
.uploading-tip {
  margin-top: 8px;
  font-size: 13px;
  color: #999;
}
.uploaded-images-preview {
  margin-top: 10px;
}
.preview-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 6px;
}
.preview-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.preview-img {
  width: 80px;
  height: 80px;
  border-radius: 6px;
  object-fit: cover;
}
</style>