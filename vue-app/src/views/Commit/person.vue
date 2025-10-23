<template>
  <div class="heritage-homepage" :style="{ fontFamily: 'PingFang SC, Microsoft YaHei, sans-serif', backgroundColor: '#F0F7FF' }">
    <!-- 1. 顶部导航栏（蓝色系渐变：主蓝→深蓝） -->
    <nav class="page-nav" :style="{ 
      background: 'linear-gradient(120deg, #4A90E2, #2C6ED6)', 
      padding: '16px 24px', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'space-between',
      boxShadow: '0 2px 10px rgba(74,144,226,0.15)'
    }">
      <!-- 返回按钮 -->
      <div class="nav-left" @click="showBackTip" :style="{ cursor: 'pointer', transition: 'transform 0.2s', '&:hover': { transform: 'scale(1.1)' } }">
        <span :style="{ fontSize: '22px', color: '#fff' }">返回</span>
      </div>
      <!-- 标题（贴合点苏记项目） -->
      <h1 class="nav-title" :style="{ 
        fontSize: '20px', 
        fontWeight: '600', 
        color: '#fff', 
        margin: 0,
        textShadow: '0 1px 2px rgba(0,0,0,0.1)'
      }">点苏记-非遗探官</h1>
      <!-- 设置按钮 -->
      <div class="nav-right" @click="showSettingTip" :style="{ cursor: 'pointer', transition: 'transform 0.2s', '&:hover': { transform: 'scale(1.1)' } }">
        <span :style="{ fontSize: '22px', color: '#fff' }">⚙️</span>
      </div>
    </nav>

    <!-- 2. 个人信息栏（淡蓝背景+蓝色元素） -->
    <div class="profile-section" :style="{ 
      width: '90%', 
      maxWidth: '1200px', 
      margin: '20px auto', 
      backgroundColor: '#fff', 
      borderRadius: '12px', 
      padding: '20px', 
      display: 'flex', 
      alignItems: 'center', 
      gap: '20px',
      boxShadow: '0 3px 15px rgba(74,144,226,0.08)'
    }">
      <!-- 头像（蓝色系：淡蓝背景+蓝色图标） -->
      <div class="avatar" :style="{ 
        width: '80px', 
        height: '80px', 
        borderRadius: '50%', 
        background: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', 
        display: 'flex', 
        alignItems: 'center', 
        justifyContent: 'center',
        boxShadow: '0 2px 8px rgba(74,144,226,0.2)'
      }" @click="isEditing && (editForm.avatarIcon = editForm.avatarIcon === '🧵' ? '🎨' : '🧵')">
        <span :style="{ fontSize: '32px', color: '#fff' }">{{ userInfo.avatarIcon }}</span>
      </div>
      <!-- 个人信息详情 -->
      <div class="profile-info" :style="{ flex: 1 }">
        <!-- 昵称+官方标签（蓝色标签） -->
        <div :style="{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '8px' }">
          <h2 :style="{ fontSize: '20px', fontWeight: '600', color: '#2C6ED6', margin: 0 }">{{ userInfo.nickname }}</h2>
          <span :style="{ 
            backgroundColor: '#E1F0FF', 
            color: '#2C6ED6', 
            fontSize: '12px', 
            padding: '3px 10px', 
            borderRadius: '15px',
            border: '1px solid #C9E2FF'
          }">点苏记官方认证</span>
        </div>
        <!-- 简介（蓝色文字强调非遗关键词） -->
        <p :style="{ 
          fontSize: '15px', 
          color: '#666', 
          lineHeight: '1.6', 
          margin: '0 0 10px',
          maxWidth: '800px'
        }">
          走遍江苏13市非遗工坊｜<span :style="{ color: '#2C6ED6' }">UGC非遗路线创作者</span>｜<span :style="{ color: '#2C6ED6' }">非遗好物严选官</span>｜跟着我解锁吴越大地非遗魅力，响应《关于推进非物质文化遗产与旅游深度融合发展的实施意见》
        </p>
        <!-- 数据统计（蓝色数字+hover效果） -->
        <div :style="{ display: 'flex', gap: '24px', fontSize: '15px', color: '#999' }">
          <span :style="{ cursor: 'pointer', '&:hover': { color: '#2C6ED6' } }">
            <b :style="{ color: '#2C6ED6', fontSize: '16px' }">{{ userInfo.follow }}</b> 关注
          </span>
          <span :style="{ cursor: 'pointer', '&:hover': { color: '#2C6ED6' } }">
            <b :style="{ color: '#2C6ED6', fontSize: '16px' }">{{ userInfo.fans }}</b> 粉丝
          </span>
          <span :style="{ cursor: 'pointer', '&:hover': { color: '#2C6ED6' } }">
            <b :style="{ color: '#2C6ED6', fontSize: '16px' }">{{ userInfo.likes }}</b> 获赞收藏
          </span>
        </div>
      </div>
      <!-- 编辑资料按钮（蓝色渐变+hover强化） -->
      
    </div>

    <!-- 3. 分类标签栏（蓝色系：未选中淡蓝/选中深蓝） -->
    <div class="tag-nav" :style="{ 
      width: '90%', 
      maxWidth: '1200px', 
      margin: '0 auto 20px', 
      backgroundColor: '#fff', 
      borderRadius: '8px', 
      padding: '12px 20px', 
      overflowX: 'auto', 
      whiteSpace: 'nowrap',
      boxShadow: '0 2px 8px rgba(74,144,226,0.08)'
    }">
      <button 
        v-for="(tag, index) in tags" 
        :key="index" 
        @click="activeTag = index"
        :style="activeTag === index 
          ? { 
            backgroundColor: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', 
            color: '#fff', 
            border: 'none', 
            borderRadius: '25px', 
            padding: '10px 20px', 
            fontSize: '15px', 
            marginRight: '12px', 
            cursor: 'pointer', 
            outline: 'none',
            boxShadow: '0 2px 8px rgba(74,144,226,0.2)',
            transition: 'all 0.3s',
            transform: 'scale(1.05)' // 选中后轻微放大，视觉更明显
          }
          : { 
            backgroundColor: '#F0F7FF', 
            color: '#2C6ED6', 
            border: '1px solid #E1F0FF', 
            borderRadius: '25px', 
            padding: '10px 20px', 
            fontSize: '15px', 
            marginRight: '12px', 
            cursor: 'pointer', 
            outline: 'none',
            transition: 'all 0.3s',
            '&:hover': { 
              backgroundColor: '#E1F0FF',
              color: '#1A5BC8'
            }
          }"
      >
        {{ tag }}
      </button>
    </div>

    <!-- 4. 说说列表（一行两条+蓝色系美化） -->
    <div class="posts-list" :style="{ 
      width: '90%', 
      maxWidth: '1200px', 
      margin: '0 auto 40px', 
      display: 'grid',
      gridTemplateColumns: 'repeat(2, 1fr)',
      gap: '24px',
      padding: '10px 0'
    }">
      <!-- 说说1：南京云锦（计划书2.1.1核心非遗） -->
      <div class="post-card" :style="postCardStyle" v-if="activeTag === 0 || activeTag === 3">
        <div class="post-img-wrapper" :style="{ 
          width: '100%', 
          aspectRatio: '1/1', 
          borderRadius: '8px', 
          overflow: 'hidden', 
          position: 'relative',
          marginBottom: '12px'
        }">
          <img :src="`https://picsum.photos/seed/yunjin1/600/600`" :alt="`南京云锦博物馆`" :style="{ 
            width: '100%', 
            height: '100%', 
            objectFit: 'cover',
            transition: 'transform 0.5s',
            '&:hover': {
              transform: 'scale(1.05)'
            }
          }">
          <!-- 蓝色标签 -->
          <div :style="{ 
            position: 'absolute', 
            top: '12px', 
            left: '12px', 
            backgroundColor: 'rgba(74,144,226,0.8)', 
            color: '#fff', 
            fontSize: '12px', 
            padding: '4px 10px', 
            borderRadius: '15px'
          }">
            南京云锦
          </div>
        </div>
        <div :style="{ padding: '0 4px' }">
          <h3 :style="{ 
            fontSize: '16px', 
            fontWeight: '600', 
            color: '#2C6ED6', 
            margin: '0 0 8px',
            display: '-webkit-box',
            WebkitLineClamp: '2',
            WebkitBoxOrient: 'vertical',
            overflow: 'hidden'
          }">南京云锦博物馆探秘｜1人1机1天仅织5cm的"寸锦寸金"</h3>
          <p :style="{ 
            fontSize: '14px', 
            color: '#666', 
            lineHeight: '1.5', 
            margin: '0 0 12px',
            display: '-webkit-box',
            WebkitLineClamp: '3',
            WebkitBoxOrient: 'vertical',
            overflow: 'hidden'
          }">
            今天在南京云锦博物馆蹲了一下午！终于看到了传承人手把手演示"挑花结本"——先在纸上画纹样，再用线穿成"花本"，织的时候要2人配合：1人提花1人织造，1天只能织5-6cm…最后入手了迷你云锦披肩，手感绝了！#点苏记UGC路线 #南京非遗
          </p>
          <!-- 蓝色互动图标 -->
          <div class="post-actions" :style="postActionsStyle">
            <button :style="actionBtnStyle" @click="likePost(0)">
              <span :style="{ marginRight: '6px', background: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', WebkitBackgroundClip: 'text', color: 'transparent' }">❤️</span>
              <span>248</span>
            </button>
            <button :style="actionBtnStyle" @click="showComments(0)">
              <span :style="{ marginRight: '6px', background: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', WebkitBackgroundClip: 'text', color: 'transparent' }">💬</span>
              <span>36</span>
            </button>
            <button :style="actionBtnStyle" @click="sharePost(0)">
              <span :style="{ marginRight: '6px', background: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', WebkitBackgroundClip: 'text', color: 'transparent' }">🔗</span>
              <span>19</span>
            </button>
          </div>
        </div>
      </div>

      <!-- 说说2：苏州缂丝（计划书3.1.3非遗好物） -->
      <div class="post-card" :style="postCardStyle" v-if="activeTag === 0 || activeTag === 2">
        <div class="post-img-wrapper" :style="{ 
          width: '100%', 
          aspectRatio: '1/1', 
          borderRadius: '8px', 
          overflow: 'hidden', 
          position: 'relative',
          marginBottom: '12px'
        }">
          <img :src="`https://picsum.photos/seed/keshi1/600/600`" :alt="`苏州缂丝团扇`" :style="{ 
            width: '100%', 
            height: '100%', 
            objectFit: 'cover',
            transition: 'transform 0.5s',
            '&:hover': {
              transform: 'scale(1.05)'
            }
          }">
          <div :style="{ 
            position: 'absolute', 
            top: '12px', 
            left: '12px', 
            backgroundColor: 'rgba(74,144,226,0.8)', 
            color: '#fff', 
            fontSize: '12px', 
            padding: '4px 10px', 
            borderRadius: '15px'
          }">
            苏州缂丝
          </div>
        </div>
        <div :style="{ padding: '0 4px' }">
          <h3 :style="{ 
            fontSize: '16px', 
            fontWeight: '600', 
            color: '#2C6ED6', 
            margin: '0 0 8px',
            display: '-webkit-box',
            WebkitLineClamp: '2',
            WebkitBoxOrient: 'vertical',
            overflow: 'hidden'
          }">苏州缂丝团扇｜把"透空花"戴在身上的非遗美物</h3>
          <p :style="{ 
            fontSize: '14px', 
            color: '#666', 
            lineHeight: '1.5', 
            margin: '0 0 12px',
            display: '-webkit-box',
            WebkitLineClamp: '3',
            WebkitBoxOrient: 'vertical',
            overflow: 'hidden'
          }">
            点苏记商城新上的苏州缂丝团扇太绝了！师傅说这是"通经断纬"工艺，花纹像凭空"织"在扇面上，透光看能看到细微的空隙。买了牡丹纹样的，夏天配汉服正好～现在下单还能获赠缂丝纹样贴纸！#非遗好物 #苏州缂丝
          </p>
          <div class="post-actions" :style="postActionsStyle">
            <button :style="actionBtnStyle" @click="likePost(1)">
              <span :style="{ marginRight: '6px', background: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', WebkitBackgroundClip: 'text', color: 'transparent' }">❤️</span>
              <span>189</span>
            </button>
            <button :style="actionBtnStyle" @click="showComments(1)">
              <span :style="{ marginRight: '6px', background: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', WebkitBackgroundClip: 'text', color: 'transparent' }">💬</span>
              <span>24</span>
            </button>
            <button :style="actionBtnStyle" @click="sharePost(1)">
              <span :style="{ marginRight: '6px', background: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', WebkitBackgroundClip: 'text', color: 'transparent' }">🔗</span>
              <span>12</span>
            </button>
          </div>
        </div>
      </div>

      <!-- 说说3：无锡惠山泥人（计划书4.1.1路线） -->
      <div class="post-card" :style="postCardStyle" v-if="activeTag === 0 || activeTag === 1">
        <div class="post-img-wrapper" :style="{ 
          width: '100%', 
          aspectRatio: '1/1', 
          borderRadius: '8px', 
          overflow: 'hidden', 
          position: 'relative',
          marginBottom: '12px'
        }">
          <img :src="`https://picsum.photos/seed/huishan1/600/600`" :alt="`惠山泥人DIY`" :style="{ 
            width: '100%', 
            height: '100%', 
            objectFit: 'cover',
            transition: 'transform 0.5s',
            '&:hover': {
              transform: 'scale(1.05)'
            }
          }">
          <div :style="{ 
            position: 'absolute', 
            top: '12px', 
            left: '12px', 
            backgroundColor: 'rgba(74,144,226,0.8)', 
            color: '#fff', 
            fontSize: '12px', 
            padding: '4px 10px', 
            borderRadius: '15px'
          }">
            惠山泥人
          </div>
        </div>
        <div :style="{ padding: '0 4px' }">
          <h3 :style="{ 
            fontSize: '16px', 
            fontWeight: '600', 
            color: '#2C6ED6', 
            margin: '0 0 8px',
            display: '-webkit-box',
            WebkitLineClamp: '2',
            WebkitBoxOrient: 'vertical',
            overflow: 'hidden'
          }">无锡惠山泥人DIY路线｜亲手捏个"阿福阿喜"带回家</h3>
          <p :style="{ 
            fontSize: '14px', 
            color: '#666', 
            lineHeight: '1.5', 
            margin: '0 0 12px',
            display: '-webkit-box',
            WebkitLineClamp: '3',
            WebkitBoxOrient: 'vertical',
            overflow: 'hidden'
          }">
            按点苏记UGC路线去了无锡惠山泥人工坊！师傅教我们揉泥、捏形、上色，原来阿福的红脸蛋要用天然朱砂调，衣服纹样要先画底稿再上色。路线还包含泥人博物馆，能看到清代的老泥人！#非遗路线 #惠山泥人
          </p>
          <div class="post-actions" :style="postActionsStyle">
            <button :style="actionBtnStyle" @click="likePost(2)">
              <span :style="{ marginRight: '6px', background: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', WebkitBackgroundClip: 'text', color: 'transparent' }">❤️</span>
              <span>215</span>
            </button>
            <button :style="actionBtnStyle" @click="showComments(2)">
              <span :style="{ marginRight: '6px', background: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', WebkitBackgroundClip: 'text', color: 'transparent' }">💬</span>
              <span>42</span>
            </button>
            <button :style="actionBtnStyle" @click="sharePost(2)">
              <span :style="{ marginRight: '6px', background: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', WebkitBackgroundClip: 'text', color: 'transparent' }">🔗</span>
              <span>28</span>
            </button>
          </div>
        </div>
      </div>

      <!-- 说说4：扬州漆器（计划书附录1） -->
      <div class="post-card" :style="postCardStyle" v-if="activeTag === 0 || activeTag === 3">
        <div class="post-img-wrapper" :style="{ 
          width: '100%', 
          aspectRatio: '1/1', 
          borderRadius: '8px', 
          overflow: 'hidden', 
          position: 'relative',
          marginBottom: '12px'
        }">
          <img :src="`https://picsum.photos/seed/qiqi1/600/600`" :alt="`扬州漆器工坊`" :style="{ 
            width: '100%', 
            height: '100%', 
            objectFit: 'cover',
            transition: 'transform 0.5s',
            '&:hover': {
              transform: 'scale(1.05)'
            }
          }">
          <div :style="{ 
            position: 'absolute', 
            top: '12px', 
            left: '12px', 
            backgroundColor: 'rgba(74,144,226,0.8)', 
            color: '#fff', 
            fontSize: '12px', 
            padding: '4px 10px', 
            borderRadius: '15px'
          }">
            扬州漆器
          </div>
        </div>
        <div :style="{ padding: '0 4px' }">
          <h3 :style="{ 
            fontSize: '16px', 
            fontWeight: '600', 
            color: '#2C6ED6', 
            margin: '0 0 8px',
            display: '-webkit-box',
            WebkitLineClamp: '2',
            WebkitBoxOrient: 'vertical',
            overflow: 'hidden'
          }">扬州漆器工坊｜一件茶具要经历"百层髹漆"有多难</h3>
          <p :style="{ 
            fontSize: '14px', 
            color: '#666', 
            lineHeight: '1.5', 
            margin: '0 0 12px',
            display: '-webkit-box',
            WebkitLineClamp: '3',
            WebkitBoxOrient: 'vertical',
            overflow: 'hidden'
          }">
            探访扬州漆器非遗工坊，师傅展示了"点螺"工艺——把贝壳磨成细片，一点点嵌在漆面上，还要反复髹漆、打磨，一件茶具要做3个月！入手了小尺寸的漆器首饰盒，木纹和螺钿搭配绝了～#非遗探店 #扬州漆器
          </p>
          <div class="post-actions" :style="postActionsStyle">
            <button :style="actionBtnStyle" @click="likePost(3)">
              <span :style="{ marginRight: '6px', background: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', WebkitBackgroundClip: 'text', color: 'transparent' }">❤️</span>
              <span>176</span>
            </button>
            <button :style="actionBtnStyle" @click="showComments(3)">
              <span :style="{ marginRight: '6px', background: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', WebkitBackgroundClip: 'text', color: 'transparent' }">💬</span>
              <span>29</span>
            </button>
            <button :style="actionBtnStyle" @click="sharePost(3)">
              <span :style="{ marginRight: '6px', background: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', WebkitBackgroundClip: 'text', color: 'transparent' }">🔗</span>
              <span>15</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 5. 编辑资料弹窗（蓝色系+功能实现） -->
    <div class="edit-modal" :style="editModalStyle" v-if="showEditModal">
      <div class="modal-content" :style="modalContentStyle">
        <!-- 弹窗标题 -->
        <div class="modal-header" :style="{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center', 
          paddingBottom: '16px', 
          borderBottom: '1px solid #E1F0FF',
          marginBottom: '16px'
        }">
          <h3 :style="{ fontSize: '18px', color: '#2C6ED6', margin: 0 }">编辑个人资料</h3>
          <span class="close-btn" @click="closeEditModal" :style="{ 
            fontSize: '20px', 
            color: '#999', 
            cursor: 'pointer',
            '&:hover': { color: '#2C6ED6' }
          }">×</span>
        </div>
        <!-- 编辑表单 -->
        <div class="edit-form" :style="{ display: 'flex', flexDirection: 'column', gap: '16px' }">
          <!-- 头像选择 -->
          <div class="form-item" :style="{ display: 'flex', alignItems: 'center', gap: '12px' }">
            <label :style="{ width: '80px', textAlign: 'right', fontSize: '14px', color: '#666' }">头像图标</label>
            <div :style="{ display: 'flex', gap: '12px', alignItems: 'center' }">
              <div 
  :style="editForm.avatarIcon === '🎨' 
    ? { width: '40px', height: '40px', borderRadius: '50%', background: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff', fontSize: '20px', border: '2px solid #2C6ED6' }
    : { width: '40px', height: '40px', borderRadius: '50%', background: '#F0F7FF', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#2C6ED6', fontSize: '20px', border: '2px solid transparent' }"
  @click="editForm.avatarIcon = '🎨'"
>
  🎨
</div>

<div 
  :style="editForm.avatarIcon === '🧵' 
    ? { width: '40px', height: '40px', borderRadius: '50%', background: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff', fontSize: '20px', border: '2px solid #2C6ED6' }
    : { width: '40px', height: '40px', borderRadius: '50%', background: '#F0F7FF', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#2C6ED6', fontSize: '20px', border: '2px solid transparent' }"
  @click="editForm.avatarIcon = '🧵'"
>
  🧵
</div>

<div 
  :style="editForm.avatarIcon === '🏺' 
    ? { width: '40px', height: '40px', borderRadius: '50%', background: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff', fontSize: '20px', border: '2px solid #2C6ED6' }
    : { width: '40px', height: '40px', borderRadius: '50%', background: '#F0F7FF', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#2C6ED6', fontSize: '20px', border: '2px solid transparent' }"
  @click="editForm.avatarIcon = '🏺'"
>
  🏺
</div>
            </div>
          </div>
          <!-- 昵称编辑 -->
          <div class="form-item" :style="{ display: 'flex', alignItems: 'center', gap: '12px' }">
            <label :style="{ width: '80px', textAlign: 'right', fontSize: '14px', color: '#666' }">昵称</label>
            <input 
              v-model="editForm.nickname" 
              :style="{ 
                flex: 1, 
                padding: '8px 12px', 
                border: '1px solid #E1F0FF', 
                borderRadius: '6px', 
                fontSize: '14px',
                '&:focus': {
                  outline: 'none',
                  borderColor: '#4A90E2',
                  boxShadow: '0 0 0 2px rgba(74,144,226,0.1)'
                }
              }"
              placeholder="请输入昵称（可包含非遗相关标识）"
              maxlength="16"
            >
          </div>
          <!-- 简介编辑 -->
          <div class="form-item" :style="{ display: 'flex', alignItems: 'flex-start', gap: '12px' }">
            <label :style="{ width: '80px', textAlign: 'right', fontSize: '14px', color: '#666', paddingTop: '8px' }">简介</label>
            <textarea 
              v-model="editForm.bio" 
              :style="{ 
                flex: 1, 
                padding: '8px 12px', 
                border: '1px solid #E1F0FF', 
                borderRadius: '6px', 
                fontSize: '14px',
                minHeight: '80px',
                resize: 'none',
                '&:focus': {
                  outline: 'none',
                  borderColor: '#4A90E2',
                  boxShadow: '0 0 0 2px rgba(74,144,226,0.1)'
                }
              }"
              placeholder="请输入简介（可介绍非遗相关身份或兴趣）"
              maxlength="200"
            ></textarea>
          </div>
          <!-- 数据展示（仅查看，不可编辑） -->
          <div class="form-item" :style="{ display: 'flex', alignItems: 'center', gap: '12px' }">
            <label :style="{ width: '80px', textAlign: 'right', fontSize: '14px', color: '#666' }">数据</label>
            <div :style="{ display: 'flex', gap: '16px', fontSize: '14px', color: '#666' }">
              <span>关注: <b :style="{ color: '#2C6ED6' }">{{ userInfo.follow }}</b></span>
              <span>粉丝: <b :style="{ color: '#2C6ED6' }">{{ userInfo.fans }}</b></span>
              <span>获赞: <b :style="{ color: '#2C6ED6' }">{{ userInfo.likes }}</b></span>
            </div>
          </div>
        </div>
        <!-- 弹窗按钮 -->
        <div class="modal-footer" :style="{ 
          display: 'flex', 
          justifyContent: 'flex-end', 
          gap: '12px', 
          marginTop: '20px',
          paddingTop: '16px',
          borderTop: '1px solid #E1F0FF'
        }">
          <button @click="closeEditModal" :style="{ 
            padding: '8px 20px', 
            border: '1px solid #E1F0FF', 
            borderRadius: '6px', 
            backgroundColor: '#fff', 
            color: '#666', 
            cursor: 'pointer',
            transition: 'all 0.2s',
            '&:hover': {
              borderColor: '#4A90E2',
              color: '#2C6ED6'
            }
          }">
            取消
          </button>
          <button @click="saveEditForm" :style="{ 
            padding: '8px 20px', 
            border: 'none', 
            borderRadius: '6px', 
            background: 'linear-gradient(45deg, #4A90E2, #2C6ED6)', 
            color: '#fff', 
            cursor: 'pointer',
            transition: 'all 0.2s',
            '&:hover': {
              boxShadow: '0 2px 8px rgba(74,144,226,0.2)'
            }
          }">
            保存修改
          </button>
        </div>
      </div>
    </div>

    <!-- 互动提示弹窗（蓝色系） -->
    <div class="toast" :style="toastStyle" v-if="showToast">
      {{ toastText }}
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, toRefs } from 'vue';

// 1. 标签数据（严格对应《改版计划书.docx》核心模块）
const tags = ref([
  "全部", 
  "非遗路线",  // 对应4.1.1个性化路线规划
  "非遗好物",  // 对应3.1.3非遗好物商城
  "工坊探店",  // 对应2.1.1非遗工坊打卡
  "UGC创作"    // 对应2.1.1用户生成内容（UGC路线）
]);
const activeTag = ref(0); // 当前激活标签

// 2. 用户信息数据（响应式，用于编辑功能）
const userInfo = reactive({
  nickname: "匠履寻踪-非遗探官",
  bio: "走遍江苏13市非遗工坊｜UGC非遗路线创作者｜非遗好物严选官｜跟着我解锁吴越大地非遗魅力，响应《关于推进非物质文化遗产与旅游深度融合发展的实施意见》",
  avatarIcon: "🧵", // 头像图标（非遗相关：🧵=纺织/🎨=工艺/🏺=器物）
  follow: 18,
  fans: "3.2k",
  likes: "2.5w"
});
// 编辑表单数据（初始同步用户信息）
const editForm = reactive({ ...toRefs(userInfo) });
// 编辑弹窗状态
const showEditModal = ref(false);

// 3. 样式变量（统一蓝色系：淡蓝#F0F7FF、主蓝#4A90E2、深蓝#2C6ED6）
const postCardStyle = reactive({
  backgroundColor: '#fff',
  borderRadius: '12px',
  padding: '16px',
  boxShadow: '0 3px 15px rgba(74,144,226,0.08)',
  transition: 'all 0.3s',
  '&:hover': {
    transform: 'translateY(-5px)',
    boxShadow: '0 8px 25px rgba(74,144,226,0.15)'
  }
});
const postActionsStyle = reactive({
  display: 'flex',
  justifyContent: 'space-between',
  paddingTop: '12px',
  borderTop: '1px solid #F0F7FF'
});
const actionBtnStyle = reactive({
  backgroundColor: 'transparent',
  border: 'none',
  color: '#666',
  fontSize: '14px',
  cursor: 'pointer',
  display: 'flex',
  alignItems: 'center',
  padding: '4px 0',
  '&:hover': {
    color: '#2C6ED6',
    fontWeight: '500'
  }
});
const toastStyle = reactive({
  position: 'fixed',
  bottom: '40px',
  left: '50%',
  transform: 'translateX(-50%)',
  backgroundColor: 'rgba(44,110,214,0.9)',
  color: '#fff',
  padding: '12px 24px',
  borderRadius: '30px',
  fontSize: '15px',
  zIndex: '9999',
  transition: 'all 0.3s',
  boxShadow: '0 4px 15px rgba(74,144,226,0.2)'
});
// 编辑弹窗样式
const editModalStyle = reactive({
  position: 'fixed',
  top: 0,
  left: 0,
  width: '100%',
  height: '100%',
  backgroundColor: 'rgba(0,0,0,0.5)',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  zIndex: '10000',
  backdropFilter: 'blur(2px)'
});
const modalContentStyle = reactive({
  width: '90%',
  maxWidth: '600px',
  backgroundColor: '#fff',
  borderRadius: '12px',
  padding: '24px',
  boxShadow: '0 5px 25px rgba(74,144,226,0.2)'
});

// 4. 弹窗提示数据
const showToast = ref(false);
const toastText = ref('');

// 5. 编辑资料功能实现
// 打开弹窗（同步最新用户信息到表单）
const openEditModal = () => {
  Object.assign(editForm, { ...userInfo }); // 同步数据
  showEditModal.value = true;
};
// 关闭弹窗（不保存）
const closeEditModal = () => {
  showEditModal.value = false;
  showTip('已取消编辑');
};
// 保存编辑（更新用户信息）
const saveEditForm = () => {
  // 简单验证：昵称不能为空
  if (!editForm.nickname.trim()) {
    showTip('昵称不能为空，请输入');
    return;
  }
  // 更新用户信息
  Object.assign(userInfo, {
    nickname: editForm.nickname.trim(),
    bio: editForm.bio.trim(),
    avatarIcon: editForm.avatarIcon
  });
  // 关闭弹窗+提示
  showEditModal.value = false;
  showTip('资料修改保存成功！');
};

// 6. 交互功能（点赞/评论/分享/提示）
const showTip = (text) => {
  showToast.value = true;
  toastText.value = text;
  setTimeout(() => {
    showToast.value = false;
  }, 1800);
};

// 针对性提示（贴合说说内容+非遗主题）
const showBackTip = () => showTip('返回上一页，可查看更多江苏非遗内容');
const showSettingTip = () => showTip('设置功能开发中，将支持非遗偏好个性化推荐');
const likePost = (index) => {
  const postThemes = ['南京云锦', '苏州缂丝', '无锡惠山泥人', '扬州漆器'];
  showTip(`已点赞「${postThemes[index]}」说说，支持非遗活态传承！`);
};
const showComments = (index) => {
  const postThemes = ['南京云锦', '苏州缂丝', '无锡惠山泥人', '扬州漆器'];
  showTip(`查看「${postThemes[index]}」的评论，发现更多非遗故事`);
};
const sharePost = (index) => {
  const postThemes = ['南京云锦', '苏州缂丝', '无锡惠山泥人', '扬州漆器'];
  showTip(`分享「${postThemes[index]}」说说成功，让更多人了解江苏非遗！`);
};
</script>