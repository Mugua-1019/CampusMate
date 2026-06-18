<template>
  <main class="detail-page">
    <section class="detail-shell">
      <aside class="sidebar">
        <div class="brand">
          <img :src="logoImage" alt="星伴 CampusMate" />
          <div>
            <strong>星伴</strong>
            <span>CampusMate</span>
          </div>
        </div>

        <nav class="nav-list" aria-label="主导航">
          <button v-for="item in navItems" :key="item.label" class="nav-item" :class="{ active: item.active }">
            <component :is="item.icon" />
            <span>{{ item.label }}</span>
          </button>
        </nav>

        <section class="verify-card">
          <div class="mini-scene">
            <img :src="verifyImage" alt="" />
          </div>
          <h2>完善资料，匹配更精准</h2>
          <button>去完善 <ArrowRight /></button>
        </section>
      </aside>

      <section class="page-body">
        <header class="topbar">
          <button class="back-button">
            <ArrowLeft />
            返回匹配广场
          </button>

          <div class="top-actions">
            <label class="search-box">
              <input placeholder="搜索需求、活动或用户" />
              <Search />
            </label>
            <button class="plain-icon" aria-label="我的聊天"><Message /></button>
            <button class="plain-icon notice-button" aria-label="通知">
              <Bell />
              <span>3</span>
            </button>
            <button class="profile-chip">
              <span class="profile-avatar">小</span>
              <span>待认证</span>
              <ArrowDown />
            </button>
          </div>
        </header>

        <section class="content-layout">
          <div class="main-column">
            <article class="hero-card">
              <div class="cover-placeholder" aria-label="活动头图留空"></div>
              <div class="hero-copy">
                <span class="category-pill">运动健身</span>
                <h1>周末篮球同约队友 🏀</h1>
                <p>想找几个靠谱队友，一起打球流汗，享受周末的快乐时光！</p>
                <div class="tag-row">
                  <span>校内活动</span>
                  <span>篮球</span>
                  <span>周末</span>
                  <span>新手友好</span>
                </div>
                <div class="meta-row">
                  <span>发布者：风一样的少年</span>
                  <span class="verified-dot">学生认证</span>
                  <span>发布于 2天前</span>
                  <span>浏览 128次</span>
                </div>
              </div>
              <button class="share-button"><Share /> 分享</button>
            </article>

            <section class="detail-card">
              <div class="tab-row">
                <button class="active">详情介绍</button>
                <button>安全提醒</button>
              </div>

              <section class="info-block">
                <h2><Document /> 详细说明</h2>
                <p>
                  周末想组织一场篮球局，地点在学校体育馆或者附近的球场都可以。水平不限，主要开心和锻炼身体，
                  不追求胜负，希望大家能互相尊重、积极配合，一起进步！有意向的小伙伴可以留意活动信息。
                </p>
              </section>

              <section class="schedule-grid">
                <div>
                  <strong><Clock /> 时间安排</strong>
                  <span>每周六 下午 15:00 - 17:00</span>
                </div>
                <div>
                  <strong><Location /> 活动地点</strong>
                  <span>校体育馆篮球场（室内）</span>
                </div>
                <div>
                  <strong><Wallet /> 消费预算</strong>
                  <span><b>30-50元</b> / 人</span>
                </div>
              </section>

              <section class="map-row">
                <div>
                  <h2><MapLocation /> 地点地图</h2>
                  <p>校体育馆篮球场</p>
                  <span>距离你约 320m</span>
                </div>
                <div class="map-placeholder">
                  <MapLocation />
                </div>
              </section>
            </section>

            <section class="safety-panel">
              <div class="section-title">
                <h2>安全提醒</h2>
                <span>平台守护，放心匹配</span>
              </div>
              <div class="safety-grid">
                <article v-for="item in safetyItems" :key="item.title">
                  <component :is="item.icon" />
                  <h3>{{ item.title }}</h3>
                  <p>{{ item.text }}</p>
                </article>
              </div>
            </section>
          </div>

          <aside class="right-column">
            <section class="status-card">
              <div class="status-head">
                <span>活动状态</span>
                <strong>待匹配中</strong>
              </div>
              <div class="progress-count">
                <span>当前进度</span>
                <strong>3<em>/5 人</em></strong>
              </div>
              <div class="people-row">
                <UserFilled v-for="index in 3" :key="`active-${index}`" class="active-person" />
                <UserFilled v-for="index in 2" :key="`empty-${index}`" />
              </div>
              <p>还差 <b>2</b> 人即可成局</p>
              <button class="primary-action"><Promotion /> 申请加入</button>
              <button class="secondary-action"><Message /> 私信发布者</button>
            </section>

            <section class="author-card">
              <h2>发布者信誉</h2>
              <div class="author-row">
                <span class="author-avatar">风</span>
                <div>
                  <strong>风一样的少年</strong>
                  <p>大二 · 计算机学院</p>
                </div>
                <em>已认证</em>
              </div>
              <div class="author-stats">
                <div><strong>6</strong><span>发布数</span></div>
                <div><strong>4.6</strong><span>信誉评分</span></div>
                <div><strong>92%</strong><span>响应率</span></div>
              </div>
            </section>

            <section class="report-card">
              <div class="blank-shield" aria-label="安全与举报盾牌留空"></div>
              <h2>安全与举报</h2>
              <p>如遇虚假信息、骚扰或不良行为，请及时举报，我们会尽快处理。</p>
              <button>举报此帖子</button>
            </section>
          </aside>
        </section>

        <section class="recommend-section">
          <div class="section-title">
            <h2>更多推荐</h2>
            <button>换一换 <Refresh /></button>
          </div>
          <div class="recommend-grid">
            <article v-for="item in recommendations" :key="item.title" class="recommend-card">
              <span>{{ item.type }}</span>
              <h3>{{ item.title }}</h3>
              <p>{{ item.meta }}</p>
              <footer>
                <small>发布者：{{ item.author }}</small>
                <button>去看看</button>
              </footer>
            </article>
          </div>
        </section>
      </section>
    </section>
  </main>
</template>

<script setup>
import {
  ArrowLeft,
  ArrowDown,
  ArrowRight,
  Bell,
  Clock,
  Document,
  Flag,
  Headset,
  HomeFilled,
  Location,
  Lock,
  MapLocation,
  Message,
  Promotion,
  Refresh,
  Search,
  Share,
  StarFilled,
  User,
  UserFilled,
  Wallet,
  Warning
} from '@element-plus/icons-vue'
import logoImage from '../../../assets/images/logo-star-mascot.png'
import verifyImage from '../../../assets/images/renzheng.png'

const navItems = [
  { label: '广场首页', icon: HomeFilled, active: true },
  { label: '发布需求', icon: Promotion },
  { label: '我的聊天', icon: Message },
  { label: '我的匹配', icon: StarFilled },
  { label: '倾诉广场', icon: Headset },
  { label: '认证中心', icon: Lock },
  { label: '安全反馈', icon: Flag },
  { label: '个人中心', icon: User }
]

const safetyItems = [
  {
    title: '优先公开场地',
    text: '篮球、跑步、自习等线下活动建议选择校内或人流稳定的公开区域。',
    icon: Location
  },
  {
    title: '保护个人信息',
    text: '不要在帖子或私信中过早提供手机号、宿舍号、证件号等敏感信息。',
    icon: Lock
  },
  {
    title: '异常及时反馈',
    text: '若发现虚假邀约、骚扰或财产风险，请保留证据并使用举报入口。',
    icon: Warning
  }
]

const recommendations = [
  { type: '羽毛球', title: '周末羽毛球双打找搭档', meta: '2/4人 · 周日 14:00 · 校羽毛球馆', author: '球场小太阳' },
  { type: '学习打卡', title: '一起自习，互相监督学习', meta: '3/6人 · 工作日晚上 · 图书馆自习室', author: '早起冠军' },
  { type: '跑步', title: '夜跑搭子，操场慢跑三公里', meta: '1/3人 · 周三 20:00 · 东区操场', author: '晚风同学' }
]
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  padding: 10px 18px;
  background:
    radial-gradient(circle at 8% 4%, rgba(255, 207, 233, 0.58), transparent 28%),
    radial-gradient(circle at 92% 5%, rgba(220, 214, 255, 0.72), transparent 34%),
    linear-gradient(135deg, #fbf8ff 0%, #f5f7ff 52%, #fff9fc 100%);
}

.detail-shell {
  display: grid;
  grid-template-columns: 245px minmax(0, 1fr);
  max-width: 1780px;
  min-height: calc(100vh - 20px);
  margin: 0 auto;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: 0 22px 52px rgba(105, 82, 166, 0.16);
  overflow: hidden;
}

.sidebar {
  display: flex;
  flex-direction: column;
  padding: 34px 20px 36px 24px;
  background: linear-gradient(180deg, rgba(253, 250, 255, 0.96), rgba(247, 244, 255, 0.9));
  box-shadow: inset -1px 0 0 rgba(224, 216, 246, 0.64);
}

.brand,
.nav-item,
.topbar,
.top-actions,
.back-button,
.search-box,
.hero-card,
.tag-row,
.meta-row,
.tab-row,
.schedule-grid strong,
.map-row,
.section-title,
.author-row,
.recommend-card footer {
  display: flex;
  align-items: center;
}

.brand {
  gap: 10px;
  margin-bottom: 28px;
}

.brand img {
  width: 72px;
  height: 58px;
  object-fit: contain;
  transform: scale(1.22);
}

.brand strong {
  display: block;
  color: #6853f1;
  font-size: 29px;
  line-height: 1;
}

.brand span {
  color: #6853f1;
  font-size: 16px;
  font-weight: 800;
  font-style: italic;
}

.nav-list {
  display: grid;
  gap: 8px;
}

.nav-item {
  gap: 18px;
  height: 56px;
  padding: 0 18px;
  border-radius: 10px;
  color: #9592a8;
  background: transparent;
  font-size: 16px;
  font-weight: 800;
}

.nav-item svg {
  width: 22px;
  height: 22px;
}

.nav-item.active {
  color: #6c55f0;
  background: linear-gradient(100deg, rgba(120, 99, 246, 0.18), rgba(232, 211, 255, 0.55));
}

.verify-card {
  margin-top: auto;
  padding: 16px;
  border: 1px solid #eee4ff;
  border-radius: 18px;
  background: linear-gradient(145deg, #fff, #f5eeff);
  box-shadow: 0 12px 26px rgba(118, 103, 201, 0.12);
}

.mini-scene {
  display: grid;
  height: 82px;
  place-items: center;
  margin-bottom: 12px;
  border-radius: 16px;
  background: linear-gradient(160deg, #f4edff, #fff9fd);
}

.mini-scene img {
  width: 118px;
}

.verify-card h2 {
  margin: 0 0 14px;
  color: #6b56ed;
  font-size: 16px;
}

.verify-card button,
.recommend-card button,
.report-card button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-radius: 999px;
  font-weight: 900;
}

.verify-card button svg,
.share-button svg,
.primary-action svg,
.secondary-action svg,
.section-title button svg,
.recommend-card button svg {
  width: 18px;
  height: 18px;
  flex: 0 0 18px;
  display: block;
}

.verify-card button {
  width: 100%;
  height: 44px;
  color: #fff;
  background: linear-gradient(135deg, #7460f4, #9d7bff);
}

.page-body {
  min-width: 0;
  padding: 28px 28px 26px;
}

.topbar {
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 20px;
}

.back-button {
  gap: 8px;
  color: #74708c;
  background: transparent;
  font-size: 15px;
  font-weight: 800;
}

.back-button svg {
  width: 18px;
}

.top-actions {
  gap: 18px;
}

.search-box {
  gap: 10px;
  width: 320px;
  height: 46px;
  padding: 0 15px 0 22px;
  border: 1px solid #eceaf5;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: inset 0 1px 0 #fff, 0 8px 20px rgba(111, 105, 148, 0.07);
}

.search-box input {
  width: 100%;
  border: 0;
  outline: 0;
  color: #4b4961;
  background: transparent;
}

.search-box svg {
  width: 22px;
  color: #8f8aa8;
}

.plain-icon {
  position: relative;
  display: grid;
  width: 34px;
  height: 34px;
  place-items: center;
  color: #111;
  background: transparent;
}

.plain-icon svg {
  width: 27px;
  height: 27px;
}

.notice-button span {
  position: absolute;
  top: -8px;
  right: -7px;
  display: grid;
  width: 22px;
  height: 22px;
  place-items: center;
  border-radius: 999px;
  color: #fff;
  background: #ef5b75;
  font-size: 12px;
  font-weight: 900;
}

.profile-chip {
  display: grid;
  grid-template-columns: 50px 58px 18px;
  align-items: center;
  gap: 10px;
  color: #b9802e;
  background: transparent;
  font-size: 14px;
  font-weight: 800;
}

.profile-avatar {
  display: grid;
  width: 50px;
  height: 50px;
  place-items: center;
  border-radius: 50%;
  color: #6853f1;
  background: #dcd5ff;
  font-size: 22px;
  font-weight: 900;
}

.profile-chip span:nth-child(2) {
  padding: 7px 12px;
  border-radius: 999px;
  background: #fff0d9;
}

.profile-chip svg {
  width: 18px;
  height: 18px;
  color: #c48a39;
}

.content-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 370px;
  gap: 20px;
}

.main-column,
.right-column {
  display: grid;
  align-content: start;
  gap: 18px;
}

.hero-card,
.detail-card,
.safety-panel,
.status-card,
.author-card,
.report-card,
.recommend-section {
  border: 1px solid #ece7f8;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.72);
  box-shadow: 0 10px 28px rgba(112, 104, 151, 0.08);
}

.hero-card {
  position: relative;
  align-items: stretch;
  gap: 28px;
  min-height: 245px;
  padding: 24px;
}

.cover-placeholder {
  width: 166px;
  min-height: 166px;
  flex: 0 0 166px;
  border: 1px dashed #d9cef9;
  border-radius: 18px;
  background: rgba(248, 244, 255, 0.74);
}

.hero-copy {
  min-width: 0;
  padding: 16px 130px 0 0;
}

.category-pill,
.tag-row span {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 13px;
  border-radius: 999px;
  color: #7a61d9;
  background: #f0ebff;
  font-size: 13px;
  font-weight: 900;
}

.category-pill {
  color: #f05d92;
  background: #fff0f6;
}

.hero-copy h1 {
  margin: 16px 0 10px;
  color: #11132d;
  font-size: 31px;
  line-height: 1.16;
}

.hero-copy p {
  margin: 0 0 16px;
  color: #7f7b95;
  font-size: 15px;
  font-weight: 700;
}

.tag-row {
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 22px;
}

.meta-row {
  flex-wrap: wrap;
  gap: 22px;
  color: #74708c;
  font-size: 14px;
  font-weight: 800;
}

.verified-dot::before {
  content: "";
  display: inline-block;
  width: 8px;
  height: 8px;
  margin-right: 7px;
  border-radius: 999px;
  background: #32c776;
}

.share-button {
  position: absolute;
  top: 38px;
  right: 34px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 98px;
  height: 42px;
  justify-content: center;
  border: 1px solid #ded3ff;
  border-radius: 999px;
  color: #745cf2;
  background: #fff;
  font-weight: 900;
}

.detail-card {
  padding: 0 18px 18px;
}

.tab-row {
  gap: 58px;
  height: 58px;
  border-bottom: 1px solid #ece7f8;
}

.tab-row button {
  position: relative;
  height: 100%;
  color: #64627a;
  background: transparent;
  font-size: 16px;
  font-weight: 900;
}

.tab-row button.active {
  color: #6853f1;
}

.tab-row button.active::after {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  bottom: -1px;
  height: 3px;
  border-radius: 999px;
  background: #6853f1;
}

.info-block {
  padding: 18px 0 16px;
  border-bottom: 1px solid #ece7f8;
}

.info-block h2,
.map-row h2,
.section-title h2,
.author-card h2,
.report-card h2 {
  margin: 0;
  color: #20233f;
  font-size: 17px;
}

.info-block h2,
.map-row h2 {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-block h2 svg,
.map-row h2 svg,
.schedule-grid svg {
  width: 20px;
  color: #6f57f0;
}

.info-block p {
  margin: 14px 0 0 28px;
  color: #5d6178;
  font-size: 14px;
  line-height: 1.8;
}

.schedule-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  padding: 18px 0;
  border-bottom: 1px solid #ece7f8;
}

.schedule-grid div {
  display: grid;
  gap: 10px;
}

.schedule-grid strong {
  gap: 8px;
  color: #20233f;
  font-size: 15px;
}

.schedule-grid span {
  color: #666b84;
  font-size: 15px;
}

.schedule-grid b {
  color: #11132d;
  font-size: 22px;
}

.map-row {
  justify-content: space-between;
  gap: 24px;
  padding-top: 18px;
}

.map-row p,
.map-row span {
  margin: 10px 0 0 28px;
  color: #666b84;
  font-size: 14px;
}

.map-placeholder {
  display: grid;
  width: 320px;
  height: 92px;
  place-items: center;
  border-radius: 12px;
  background:
    linear-gradient(90deg, rgba(148, 193, 255, 0.22) 1px, transparent 1px),
    linear-gradient(rgba(148, 193, 255, 0.22) 1px, transparent 1px),
    linear-gradient(135deg, #eef5ff, #f6f0ff);
  background-size: 36px 36px;
}

.map-placeholder svg {
  width: 42px;
  height: 42px;
  color: #735cf1;
}

.safety-panel,
.recommend-section {
  padding: 18px;
}

.section-title {
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title span,
.section-title button {
  color: #766f95;
  font-size: 13px;
  font-weight: 900;
}

.section-title button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: transparent;
}

.safety-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.safety-grid article {
  min-height: 142px;
  padding: 18px;
  border: 1px solid #eee8fb;
  border-radius: 14px;
  background: linear-gradient(145deg, #fff, #f8f4ff);
}

.safety-grid svg {
  width: 26px;
  color: #6f57f0;
}

.safety-grid h3 {
  margin: 10px 0 8px;
  color: #20233f;
  font-size: 15px;
}

.safety-grid p {
  margin: 0;
  color: #666b84;
  font-size: 13px;
  line-height: 1.65;
}

.status-card,
.author-card,
.report-card {
  padding: 20px;
}

.status-head {
  margin: -20px -20px 18px;
  padding: 22px 20px;
  border-radius: 18px 18px 0 0;
  color: #fff;
  background: linear-gradient(105deg, #7460f4, #f07aa9);
  text-align: center;
}

.status-head span {
  display: block;
  font-weight: 800;
}

.status-head strong {
  display: block;
  margin-top: 6px;
  font-size: 26px;
}

.progress-count {
  text-align: center;
}

.progress-count span {
  color: #77738b;
  font-size: 14px;
}

.progress-count strong {
  display: block;
  margin-top: 4px;
  color: #12121d;
  font-size: 36px;
}

.progress-count em {
  color: #77738b;
  font-size: 24px;
  font-style: normal;
}

.people-row {
  display: flex;
  justify-content: center;
  gap: 17px;
  margin: 14px 0;
  color: #c8cad4;
}

.people-row svg {
  width: 32px;
  height: 32px;
}

.people-row .active-person {
  color: #6f57f0;
}

.status-card p {
  margin: 0 0 20px;
  color: #555b73;
  text-align: center;
  font-size: 14px;
}

.status-card p b {
  color: #f05d92;
}

.primary-action,
.secondary-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 100%;
  height: 50px;
  border-radius: 999px;
  font-size: 16px;
  font-weight: 900;
  overflow: hidden;
}

.primary-action {
  color: #fff;
  background: linear-gradient(105deg, #7460f4, #f07aa9);
  box-shadow: 0 14px 28px rgba(129, 91, 244, 0.24);
}

.secondary-action {
  margin-top: 12px;
  color: #745cf2;
  border: 1px solid #d7ccfb;
  background: #fff;
}

.author-card h2 {
  margin-bottom: 16px;
}

.author-row {
  gap: 12px;
}

.author-avatar {
  display: grid;
  width: 58px;
  height: 58px;
  place-items: center;
  border-radius: 18px;
  color: #6853f1;
  background: linear-gradient(145deg, #ded7ff, #f3edff);
  font-size: 24px;
  font-weight: 900;
}

.author-row div {
  flex: 1;
}

.author-row strong {
  color: #20233f;
}

.author-row p {
  margin: 6px 0 0;
  color: #77738b;
  font-size: 13px;
}

.author-row em {
  padding: 5px 9px;
  border-radius: 999px;
  color: #32a86a;
  background: #e7f8ee;
  font-size: 12px;
  font-style: normal;
  font-weight: 900;
}

.author-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
  margin-top: 18px;
}

.author-stats div {
  padding: 10px;
  border-radius: 10px;
  background: #f5f1ff;
  text-align: center;
}

.author-stats strong,
.author-stats span {
  display: block;
}

.author-stats strong {
  color: #745cf2;
  font-size: 20px;
}

.author-stats span {
  color: #7e71ad;
  font-size: 12px;
  font-weight: 800;
}

.report-card {
  display: grid;
  justify-items: start;
}

.blank-shield {
  width: 38px;
  height: 38px;
  margin-bottom: 12px;
  border: 1px dashed #d9cef9;
  border-radius: 12px;
  background: rgba(248, 244, 255, 0.64);
}

.report-card p {
  margin: 10px 0 18px;
  color: #666b84;
  font-size: 14px;
  line-height: 1.7;
}

.report-card button {
  width: 100%;
  height: 44px;
  color: #745cf2;
  border: 1px solid #d7ccfb;
  background: #fff;
}

.recommend-section {
  margin-top: 18px;
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

.recommend-card {
  min-height: 148px;
  padding: 18px;
  border: 1px solid #f0d9df;
  border-radius: 14px;
  background: linear-gradient(145deg, #fff6f8, #fff);
}

.recommend-card span {
  display: inline-flex;
  min-height: 25px;
  align-items: center;
  padding: 0 10px;
  border-radius: 999px;
  color: #7a61d9;
  background: #f0ebff;
  font-size: 12px;
  font-weight: 900;
}

.recommend-card h3 {
  margin: 12px 0 8px;
  color: #20233f;
  font-size: 17px;
}

.recommend-card p {
  margin: 0;
  color: #77738b;
  font-size: 13px;
}

.recommend-card footer {
  justify-content: space-between;
  gap: 12px;
  margin-top: 18px;
}

.recommend-card small {
  color: #6a6d82;
  font-weight: 800;
}

.recommend-card button {
  width: 78px;
  height: 32px;
  color: #745cf2;
  border: 1px solid #d7ccfb;
  background: #fff;
  font-size: 13px;
}

@media (max-width: 1320px) {
  .content-layout {
    grid-template-columns: 1fr;
  }

  .right-column {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 980px) {
  .detail-shell {
    display: block;
  }

  .sidebar {
    padding-bottom: 18px;
  }

  .verify-card {
    display: none;
  }

  .nav-list {
    display: flex;
    overflow-x: auto;
  }

  .nav-item {
    flex: 0 0 auto;
  }

  .topbar,
  .top-actions,
  .hero-card,
  .map-row {
    align-items: flex-start;
    flex-direction: column;
  }

  .search-box,
  .map-placeholder {
    width: 100%;
  }

  .cover-placeholder {
    width: 100%;
    flex-basis: 190px;
  }

  .hero-copy {
    padding-right: 0;
  }

  .schedule-grid,
  .safety-grid,
  .right-column,
  .recommend-grid {
    grid-template-columns: 1fr;
  }
}
</style>
