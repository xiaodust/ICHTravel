<template>
  <div class="user-commit-page">
    <!-- 顶部：头像/昵称/签名（纵向布局） -->
    <div class="user-profile">
      <img :src="profile.avatar" alt="头像" class="profile-avatar" />
      <div class="profile-name">{{ profile.userName }}</div>
      <div class="profile-signature">{{ profile.signature }}</div>
    </div>

    <!-- 说说列表（参考交流社区展示） -->
    <div class="note-feed">
      <div class="note-card" v-for="note in notes" :key="note.id">
        <div class="note-author">
          <img :src="note.authorAvatar" alt="作者头像" class="author-avatar" />
          <div class="author-info">
            <div class="author-name">{{ note.authorName }}</div>
            <div class="author-tag">{{ note.authorTag }}</div>
          </div>
        </div>
        <div class="note-content">
          <p class="note-text">{{ note.content }}</p>
          <div class="note-images" :class="`image-count-${note.images.length}`">
            <img v-for="(img, i) in note.images" :key="i" :src="img" alt="笔记图片" class="note-img" />
          </div>
        </div>
        <div class="note-actions">
          <button class="action-btn" :class="{ liked: note.isLiked }" @click="toggleLike(note)">
            <i>{{ note.isLiked ? '❤️' : '♡' }}</i>
            <span>{{ note.likeCount }}</span>
          </button>
          <button class="action-btn" @click="showCommentModal(note)">
            <i>💬</i>
          </button>
          <button class="action-btn" @click="shareNote(note)">
            <i>🔗</i>
            <span>分享</span>
          </button>
        </div>
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
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const userId = route.params.userId;

// 个人信息（仅头像、昵称、签名）
const profile = ref({
  userName: '未知用户',
  avatar: 'https://picsum.photos/80/80?random=default',
  signature: '这个用户很低调，暂未填写个性签名'
});

const notes = ref([]);
const showCommentBox = ref(false);
const currentNote = ref(null);
const COMMENT_PREVIEW_COUNT = 2;

// 获取用户资料
const loadUserProfile = async () => {
  try {
    const res = await axios.get(`http://localhost:8080/api/user/${userId}`);
    const data = res?.data?.data || {};
    profile.value.userName = data?.nickName || data?.name || '未知用户';
    profile.value.avatar = data?.avatar || 'https://picsum.photos/80/80?random=default';
    profile.value.signature = data?.signature || data?.intro || '这个用户很低调，暂未填写个性签名';
  } catch (e) {
    console.warn('加载用户信息失败：', e);
  }
};

// 获取该用户的说说
const loadUserNotes = async () => {
  try {
    const res = await axios.get(`http://localhost:8080/api/note/user/${userId}`);
    const list = Array.isArray(res?.data?.data) ? res.data.data : [];
    notes.value = list.map(n => ({
      id: n.id,
      userId: n.userId,
      authorAvatar: profile.value.avatar,
      authorName: profile.value.userName,
      authorTag: '非遗爱好者',
      content: n.context || '',
      images: Array.isArray(n.images) ? n.images : [],
      likeCount: typeof n.liked === 'number' ? n.liked : 0,
      isLiked: false,
      commentCount: typeof n.commentCount === 'number' ? n.commentCount : 0,
      isCollected: false,
      comments: []
    }));
    // 拉取评论用于预览
    await Promise.all(notes.value.map(async (note) => {
      const comments = await fetchComments(note.id);
      note.comments = comments;
    }));
  } catch (e) {
    console.error('加载用户说说失败：', e);
  }
};

// 评论相关：补齐用户昵称与头像
const userProfileCache = new Map();
const getUserProfile = async (uid) => {
  if (!uid) return { userName: '匿名用户', avatar: 'https://picsum.photos/50/50?random=default' };
  if (userProfileCache.has(uid)) return userProfileCache.get(uid);
  try {
    const res = await axios.get(`http://localhost:8080/api/user/${uid}`);
    const data = res?.data?.data || {};
    const userName = data?.nickName || data?.name || '匿名用户';
    const avatar = data?.avatar || 'https://picsum.photos/50/50?random=default';
    const p = { userName, avatar };
    userProfileCache.set(uid, p);
    return p;
  } catch (e) {
    return { userName: '匿名用户', avatar: 'https://picsum.photos/50/50?random=default' };
  }
};

const fetchComments = async (noteId) => {
  try {
    const res = await axios.get(`http://localhost:8080/api/comment/${noteId}`);
    if (res.data && res.data.success && res.data.code === '200') {
      const list = Array.isArray(res.data.data) ? res.data.data : [];
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

// 点赞/取消点赞（与社区页一致）
const likeInFlight = new Set();
const toggleLike = async (note) => {
  if (!note || !note.id) return;
  if (likeInFlight.has(note.id)) return;
  likeInFlight.add(note.id);
  try {
    if (note.isLiked) {
      const res = await axios.post('http://localhost:8080/api/note/unlike', { id: note.id }, {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        transformRequest: [(data) => `id=${encodeURIComponent(data.id)}`]
      });
      if (res.data && res.data.success && res.data.code === '200') {
        const updated = res.data.data;
        note.likeCount = typeof updated?.liked === 'number' ? updated.liked : Math.max(0, note.likeCount - 1);
        note.isLiked = false;
      } else {
        note.likeCount = Math.max(0, note.likeCount - 1);
        note.isLiked = false;
      }
    } else {
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

const showCommentModal = async (note) => {
  currentNote.value = JSON.parse(JSON.stringify(note));
  const comments = await fetchComments(note.id);
  currentNote.value.comments = comments;
  showCommentBox.value = true;
};

const shareNote = (note) => {
  alert(`分享笔记：${note.content.substring(0, 20)}...`);
};

onMounted(async () => {
  await loadUserProfile();
  await loadUserNotes();
});
</script>

<style>
.user-commit-page { width: 100%; min-height: 100vh; background: url('/image/Person页面背景图.jpg') center / cover no-repeat fixed; }
.user-profile { display: flex; flex-direction: column; align-items: center; padding: 20px 0; }
.profile-avatar { width: 88px; height: 88px; border-radius: 50%; object-fit: cover; }
.profile-name { margin-top: 10px; font-size: 18px; font-weight: 600; }
.profile-signature { margin-top: 6px; font-size: 14px; color: #666; }

.note-feed { width: 90%; max-width: 900px; margin: 0 auto; }
.note-card { background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); padding: 12px; margin-bottom: 16px; }
.note-author { display: flex; align-items: center; }
.author-avatar { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; }
.author-info { margin-left: 10px; }
.author-name { font-weight: 600; }
.author-tag { font-size: 12px; color: #999; }
.note-text { margin: 10px 0; font-size: 14px; color: #333; }
.note-images { display: grid; grid-template-columns: repeat(3, 1fr); gap: 6px; }
.note-img { width: 100%; border-radius: 6px; }
.note-actions { display: flex; gap: 10px; margin-top: 8px; }
.action-btn { display: inline-flex; align-items: center; gap: 6px; border: none; background: transparent; cursor: pointer; color: #555; }
.action-btn.liked { color: #e74c3c; }
.comment-preview { margin-top: 10px; }
.comment-item { font-size: 13px; color: #555; margin-bottom: 6px; }
.comment-user { font-weight: 600; margin-right: 4px; }
.view-more-comments { color: #2C6ED6; cursor: pointer; font-size: 13px; }

.modal-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.45); display: flex; align-items: center; justify-content: center; }
.publish-modal { background: #fff; width: 520px; border-radius: 8px; overflow: hidden; }
.modal-header { display: flex; align-items: center; justify-content: space-between; padding: 10px 12px; border-bottom: 1px solid #eee; }
.modal-body { padding: 12px; max-height: 420px; overflow-y: auto; }
.comment-avatar { width: 32px; height: 32px; border-radius: 50%; object-fit: cover; margin-right: 8px; }
.comment-content { display: inline-block; vertical-align: middle; }
.close-btn { border: none; background: transparent; font-size: 20px; cursor: pointer; }
</style>