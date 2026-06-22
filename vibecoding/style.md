# CampusMate 前端视觉规范（style.md）

> 适用项目：**星伴 CampusMate 大学生多场景伙伴匹配平台**  
> 适用范围：首页广场、发布需求、聊一聊、匹配确认、认证中心、管理员审核与事件管理页面。  
> 参考视觉：浅色背景、蓝紫粉渐变、玻璃拟态、圆角卡片、便利贴广场、轻量插画感、校园陪伴氛围。

---

## 1. 设计定位

CampusMate 的视觉目标不是传统后台系统，也不是强社交娱乐产品，而是一个面向大学生的**轻量陪伴型校园匹配平台**。

整体观感应当传达以下关键词：

- 温柔
- 清爽
- 年轻
- 可信
- 校园感
- 陪伴感
- 轻社交
- 不压迫
- 不商业化
- 不后台化

界面需要同时具备两种气质：

1. **用户端**：温暖、轻盈、像校园便利贴广场，适合学习搭子、饭搭子、运动搭子、倾诉陪伴等场景。
2. **管理端**：清晰、规范、可操作，但仍保持浅色、圆角、柔和的视觉语言，避免变成传统严肃后台。

---

## 2. 整体页面风格

### 2.1 主风格

页面采用 **Soft Dashboard + Glassmorphism + Sticky Note** 的组合风格。

具体表现为：

- 大面积浅色背景
- 白色或半透明卡片
- 柔和蓝紫粉渐变
- 大圆角组件
- 低强度阴影
- 轻量线性图标
- 胶囊按钮
- 便利贴式内容卡片
- 局部使用贴纸、胶带、图钉、星星等装饰元素
- 插画元素偏校园生活，不偏商务办公

### 2.2 视觉避免项

需要避免以下风格：

- 高饱和霓虹色
- 大面积纯黑或深灰
- 金融、支付、账单类视觉元素
- 传统后台管理表格感过强
- 信息密度过高
- 过多硬边框
- 过度拟物化
- 过度卡通化导致不专业

---

## 3. 色彩规范

### 3.1 主色

主色用于主按钮、当前选中状态、重要操作、品牌 Logo、关键标签。

```css
--primary-500: #7566F6;
--primary-600: #6554E8;
--primary-400: #8B7CFF;
--primary-100: #EFEAFE;
```

推荐主渐变：

```css
--gradient-primary: linear-gradient(135deg, #7566F6 0%, #9B7CFF 52%, #F59ACB 100%);
```

使用场景：

- 选中的一级 Tab
- 主操作按钮
- 发布星伴需求悬浮按钮
- 当前导航项背景
- 关键进度条
- 重要状态强调

---

### 3.2 辅助色

用于不同类型的需求卡片、标签和轻量状态区分。

```css
--blue-soft: #EAF5FF;
--blue-border: #BFDFFF;

--green-soft: #EAF8EF;
--green-border: #BFE8C9;

--yellow-soft: #FFF6D8;
--yellow-border: #F6D995;

--pink-soft: #FFEAF3;
--pink-border: #F6BFD5;

--purple-soft: #F0EAFE;
--purple-border: #D8C8FF;

--orange-soft: #FFF0DC;
--orange-border: #F7C28A;
```

分类建议：

| 类型 | 背景色 | 点缀色 | 使用说明 |
|---|---|---|---|
| 学习搭子 | `#EAF5FF` | `#7566F6` | 图书馆、自习、考试复习 |
| 饭搭子 | `#FFF6D8` | `#F5A623` | 食堂、聚餐、探店 |
| 运动搭子 | `#EAF8EF` | `#45B97C` | 跑步、羽毛球、健身 |
| 比赛组队 | `#FFF0DC` | `#F28C28` | 创赛、数学建模、项目组队 |
| 活动同行 | `#F0EAFE` | `#8B7CFF` | 校园活动、音乐节、讲座 |
| 倾诉陪伴 | `#FFEAF3` | `#EC6FA7` | 情绪表达、日常倾诉 |

---

### 3.3 背景色

页面背景应接近白色，但不能是纯白。建议使用浅灰蓝或浅紫白。

```css
--bg-page: #F7F8FF;
--bg-lilac: #F8F5FF;
--bg-card: rgba(255, 255, 255, 0.78);
--bg-card-solid: #FFFFFF;
```

可在页面右上、左下加入非常淡的渐变色块：

```css
--bg-blob-blue: rgba(181, 224, 255, 0.45);
--bg-blob-pink: rgba(255, 196, 224, 0.38);
--bg-blob-purple: rgba(212, 197, 255, 0.36);
```

---

### 3.4 文本色

```css
--text-title: #20233F;
--text-primary: #343753;
--text-secondary: #7B7F9A;
--text-tertiary: #A4A7BA;
--text-white: #FFFFFF;
```

使用原则：

- 大标题使用 `#20233F`，保证清晰度。
- 正文使用 `#343753` 或 `#555A77`。
- 辅助说明使用 `#7B7F9A`。
- 不建议使用纯黑 `#000000`。

---

### 3.5 状态色

```css
--success: #32C776;
--success-bg: #EAF8EF;

--warning: #F5A623;
--warning-bg: #FFF6D8;

--danger: #EF5B75;
--danger-bg: #FFEAF0;

--info: #5B8DFF;
--info-bg: #EAF3FF;

--disabled: #C8CBDD;
--disabled-bg: #F2F3F8;
```

状态对应：

| 状态 | 颜色建议 | 示例 |
|---|---|---|
| 已认证 | 绿色 | `已认证` |
| 待认证 | 橙色 | `待认证` |
| 待匹配 | 紫色 | `待匹配` |
| 聊天中 | 粉色 / 紫色 | `聊天中` |
| 已满员 | 灰色 | `已满员` |
| 待审核 | 橙色 | `待审核` |
| 已下架 | 灰色 | `已下架` |
| 已删除 | 红色弱提示 | `已删除` |
| 举报 / 异常 | 红色 | `举报`、`异常反馈` |

---

## 4. 字体规范

### 4.1 字体族

中文界面建议优先使用系统圆润字体。

```css
font-family:
  "PingFang SC",
  "Microsoft YaHei",
  "HarmonyOS Sans SC",
  "Noto Sans SC",
  system-ui,
  -apple-system,
  BlinkMacSystemFont,
  "Segoe UI",
  sans-serif;
```

### 4.2 字号层级

| 用途 | 字号 | 字重 | 行高 | 示例 |
|---|---:|---:|---:|---|
| 页面主标题 | 32px - 36px | 700 | 1.25 | `Hello，今天想找什么星伴？` |
| 一级模块标题 | 20px - 24px | 700 | 1.35 | `我的星伴状态` |
| 卡片标题 | 17px - 20px | 700 | 1.35 | `今晚 7 点图书馆自习搭子` |
| 正文描述 | 14px - 15px | 400 / 500 | 1.6 | 需求描述、说明文本 |
| 辅助信息 | 12px - 13px | 400 | 1.5 | 时间、地点、提示 |
| 标签文字 | 11px - 13px | 500 / 600 | 1 | `学习搭子`、`已认证` |
| 按钮文字 | 14px - 16px | 600 | 1 | `聊一聊` |

### 4.3 字体使用原则

- 中文标题可以稍粗，但不要过重。
- 页面主标题建议使用 700 字重。
- 正文不宜过细，避免浅色背景上可读性下降。
- 卡片内文字不要超过 3 层字号，避免视觉混乱。
- 重要数字可以适当放大，例如匹配人数、待确认数量。

---

## 5. 间距与布局规范

### 5.1 页面基础布局

推荐桌面端尺寸以 `1440px × 1024px` 或 `1366px × 900px` 为设计基准。

整体结构：

```text
┌─────────────────────────────────────────────┐
│ Sidebar │ Header + Main Content │ Right Panel │
└─────────────────────────────────────────────┘
```

建议比例：

| 区域 | 宽度建议 |
|---|---:|
| 左侧导航栏 | 220px - 240px |
| 主内容区 | 58% - 65% |
| 右侧辅助区 | 280px - 340px |
| 页面外边距 | 24px - 32px |
| 主内容与右侧间距 | 24px - 32px |

### 5.2 间距系统

使用 4px / 8px 倍数体系。

```css
--space-4: 4px;
--space-8: 8px;
--space-12: 12px;
--space-16: 16px;
--space-20: 20px;
--space-24: 24px;
--space-32: 32px;
--space-40: 40px;
--space-48: 48px;
```

### 5.3 常用间距建议

| 组件 | 内边距 | 外部间距 |
|---|---:|---:|
| 页面容器 | 24px - 32px | - |
| Sidebar 菜单项 | 14px 18px | 8px |
| 顶部 Header | 0 | 24px - 32px |
| 普通卡片 | 18px - 24px | 16px - 24px |
| 便利贴卡片 | 18px - 22px | 16px |
| 右侧信息卡片 | 20px - 24px | 16px - 20px |
| 标签之间 | 6px - 8px | - |
| 按钮之间 | 10px - 12px | - |
| 卡片标题到正文 | 8px - 12px | - |

---

## 6. 圆角规范

整体使用大圆角，营造柔和感。

```css
--radius-sm: 8px;
--radius-md: 12px;
--radius-lg: 18px;
--radius-xl: 24px;
--radius-xxl: 32px;
--radius-pill: 999px;
```

使用建议：

| 组件 | 圆角 |
|---|---:|
| 页面大容器 | 28px - 32px |
| Sidebar | 24px - 32px |
| 主卡片 | 22px - 28px |
| 便利贴卡片 | 18px - 24px |
| 按钮 | 14px - 999px |
| 胶囊标签 | 999px |
| 搜索框 | 18px - 999px |
| 头像 | 50% |
| 弹窗 | 24px - 28px |

---

## 7. 阴影与玻璃拟态

### 7.1 基础阴影

阴影必须轻、软、扩散，避免传统后台的硬阴影。

```css
--shadow-card: 0 12px 32px rgba(104, 112, 180, 0.12);
--shadow-soft: 0 8px 24px rgba(104, 112, 180, 0.10);
--shadow-button: 0 10px 24px rgba(117, 102, 246, 0.28);
--shadow-note: 0 10px 22px rgba(104, 112, 180, 0.14);
```

### 7.2 玻璃拟态卡片

```css
.glass-card {
  background: rgba(255, 255, 255, 0.76);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  border: 1px solid rgba(255, 255, 255, 0.68);
  box-shadow: 0 12px 32px rgba(104, 112, 180, 0.12);
  border-radius: 24px;
}
```

使用位置：

- 右侧状态卡片
- 个人信息卡片
- 发布表单容器
- 匹配确认卡片
- 管理端统计卡片

便利贴卡片可以弱化玻璃感，更偏纸张质感。

---

## 8. 组件规范

## 8.1 Sidebar 左侧导航栏

### 视觉特征

- 背景使用白色半透明或纯白。
- 整体为大圆角竖向面板。
- 当前菜单项使用浅紫背景或蓝紫渐变弱化背景。
- 图标使用线性图标，颜色统一为蓝紫或灰紫。
- 菜单文字保持 15px - 16px。

### 推荐样式

```css
.sidebar {
  width: 232px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.82);
  box-shadow: 0 16px 40px rgba(112, 114, 170, 0.12);
  padding: 28px 20px;
}

.nav-item {
  height: 52px;
  border-radius: 16px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  color: #747891;
  font-weight: 600;
}

.nav-item.active {
  color: #6554E8;
  background: linear-gradient(135deg, rgba(117,102,246,0.16), rgba(245,154,203,0.14));
}
```

### Sidebar 底部认证卡片

内容：

```text
完成认证，解锁更多功能
提升匹配可信度
可主动发起聊天
参与更多活动
去认证
```

卡片风格：

- 背景：浅紫白渐变
- 图案：小星星、纸张、轻量插画
- 按钮：紫色渐变
- 文案不可过长

---

## 8.2 顶部 Header

Header 由欢迎语、搜索框、通知、头像、认证状态组成。

### 推荐结构

```text
Hello，今天想找什么星伴？        搜索框  消息  通知  头像  认证状态
在校园里找到一起学习、运动、吃饭、倾诉的温暖伙伴 ✨
```

### 搜索框

```css
.search-box {
  height: 48px;
  min-width: 320px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(218, 220, 238, 0.8);
  box-shadow: 0 8px 22px rgba(104, 112, 180, 0.08);
}
```

搜索占位文案建议：

- `搜索需求、活动或用户`
- `搜索学习搭子、饭搭子、运动搭子、倾诉…`
- `搜索需求、地点、标签或用户`

---

## 8.3 Tab 切换

首页核心 Tab：

- 匹配广场
- 倾诉广场

选中态：

```css
.tab.active {
  color: #FFFFFF;
  background: linear-gradient(135deg, #7566F6, #F59ACB);
  box-shadow: 0 10px 24px rgba(117, 102, 246, 0.24);
}
```

未选中态：

```css
.tab {
  height: 48px;
  min-width: 160px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.74);
  color: #7B7F9A;
  font-weight: 700;
}
```

---

## 8.4 分类筛选胶囊按钮

### 匹配广场分类

- 全部
- 学习搭子
- 饭搭子
- 运动搭子
- 比赛组队
- 活动同行
- 闲聊陪伴
- 其他

### 倾诉广场分类

- 想找人聊聊
- 心情不好
- 学业压力
- 考试焦虑
- 生活分享
- 只想被听见

### 样式建议

```css
.filter-pill {
  height: 36px;
  padding: 0 16px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.78);
  border: 1px solid rgba(218, 220, 238, 0.8);
  color: #6E728A;
  font-size: 14px;
  font-weight: 600;
}

.filter-pill.active {
  background: #7566F6;
  color: #FFFFFF;
  border-color: transparent;
}
```

---

## 8.5 便利贴式需求卡片

便利贴卡片是 CampusMate 首页的视觉核心。

### 视觉特征

- 背景为低饱和 pastel 色块。
- 圆角 18px - 24px。
- 可使用轻微旋转，但不要影响阅读。
- 可加图钉、胶带、贴纸角标。
- 卡片应像贴在校园广场公告栏上的便签。
- 每张卡片的信息密度适中，避免堆满文字。

### 卡片信息结构

```text
标题 + 状态标签
类型标签 / 匿名状态 / 认证状态
简短描述
时间 / 地点
发布者昵称 / 当前人数
操作按钮：聊一聊 / 查看详情 / 举报
```

### 示例内容

```text
今晚 7 点图书馆自习搭子 📖
学习搭子 / 自习 / 专注互勉
一起到图书馆，互相监督，效率加倍！
明书馆三楼自习区 / 19:00
小星星不熬夜 / 已认证 / 3/4 人
聊一聊
```

### 卡片尺寸

推荐：

```css
.note-card {
  min-height: 170px;
  border-radius: 22px;
  padding: 20px;
  box-shadow: 0 10px 22px rgba(104, 112, 180, 0.14);
}
```

普通卡片：

- 宽度：250px - 310px
- 高度：160px - 210px

重点卡片：

- 宽度：420px - 520px
- 高度：260px - 320px

### 卡片颜色示例

```css
.note-study {
  background: linear-gradient(145deg, #EAF5FF 0%, #F7FBFF 100%);
  border: 1px solid #BFDFFF;
}

.note-food {
  background: linear-gradient(145deg, #FFF6D8 0%, #FFFDF3 100%);
  border: 1px solid #F6D995;
}

.note-sport {
  background: linear-gradient(145deg, #EAF8EF 0%, #F8FFF9 100%);
  border: 1px solid #BFE8C9;
}

.note-vent {
  background: linear-gradient(145deg, #FFEAF3 0%, #FFF8FB 100%);
  border: 1px solid #F6BFD5;
}

.note-activity {
  background: linear-gradient(145deg, #F0EAFE 0%, #FBF8FF 100%);
  border: 1px solid #D8C8FF;
}

.note-team {
  background: linear-gradient(145deg, #FFF0DC 0%, #FFF9F0 100%);
  border: 1px solid #F7C28A;
}
```

### 卡片装饰元素

可使用：

- 胶带
- 图钉
- 星星贴纸
- 爱心贴纸
- 书本图标
- 餐具图标
- 羽毛球图标
- 聊天气泡图标
- 小头像

装饰原则：

- 装饰只占辅助地位，不要遮挡正文。
- 每张卡片最多 1 - 2 个装饰元素。
- 装饰透明度可以控制在 60% - 90%。
- 卡片可使用 1° - 2° 的轻微旋转，但列表型页面不建议大面积旋转。

---

## 8.6 标签 Tag

### 基础样式

```css
.tag {
  display: inline-flex;
  align-items: center;
  height: 22px;
  padding: 0 8px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}
```

### 常用标签

| 标签 | 样式 |
|---|---|
| 学习搭子 | 蓝紫文字 + 浅蓝背景 |
| 饭搭子 | 橙色文字 + 浅黄背景 |
| 运动搭子 | 绿色文字 + 浅绿背景 |
| 活动同行 | 紫色文字 + 浅紫背景 |
| 倾诉陪伴 | 粉色文字 + 浅粉背景 |
| 已认证 | 绿色文字 + 浅绿背景 |
| 匿名发布 | 粉色文字 + 浅粉背景 |
| 待匹配 | 紫色文字 + 浅紫背景 |
| 聊天中 | 粉紫文字 + 浅粉紫背景 |
| 已满员 | 灰色文字 + 浅灰背景 |

---

## 8.7 按钮 Button

### 主按钮

用于主要动作，如发布、确认、去认证。

```css
.btn-primary {
  height: 44px;
  padding: 0 24px;
  border-radius: 999px;
  color: #FFFFFF;
  font-weight: 700;
  background: linear-gradient(135deg, #7566F6, #F59ACB);
  box-shadow: 0 10px 24px rgba(117, 102, 246, 0.28);
}
```

### 次按钮

用于查看详情、再聊一聊、取消等。

```css
.btn-secondary {
  height: 40px;
  padding: 0 22px;
  border-radius: 999px;
  color: #6554E8;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.75);
  border: 1px solid rgba(117, 102, 246, 0.28);
}
```

### 弱按钮

用于举报、更多、筛选。

```css
.btn-ghost {
  height: 36px;
  padding: 0 14px;
  border-radius: 999px;
  color: #8A8EA5;
  background: transparent;
}
```

### 禁用按钮

```css
.btn-disabled {
  color: #A4A7BA;
  background: #F2F3F8;
  box-shadow: none;
  cursor: not-allowed;
}
```

使用示例：

- `聊一聊`：主按钮或小型渐变按钮
- `查看详情`：次按钮
- `举报`：弱按钮
- `已满员`：禁用按钮
- `同意匹配`：主按钮
- `再聊一聊`：次按钮
- `去认证`：主按钮

---

## 8.8 右侧辅助卡片

右侧信息区用于放置用户状态、安全信息和引导信息。

### 卡片类型

1. 我的星伴状态
2. 待确认见面信息
3. 安全小贴士
4. 倾诉引导卡片

### 样式建议

```css
.side-card {
  border-radius: 24px;
  padding: 22px;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(18px);
  box-shadow: 0 12px 32px rgba(104, 112, 180, 0.12);
}
```

### 我的星伴状态

推荐采用 2×2 小卡片结构。

```text
今日可匹配次数 3
进行中的聊天 2
待确认匹配 1
已完成匹配 8
```

视觉：

- 每个指标用浅色小块承载。
- 数字要明显。
- 图标可以使用星星、聊天、爱心、勾选。

### 待确认见面信息

需要突出可操作性。

内容结构：

```text
待确认见面信息
小熊饼干 / 学习搭子
今晚 19:00
图书馆三楼
等待你确认
[同意匹配] [再聊一聊]
首次见面建议选择校园内公共区域。
```

按钮层级：

- `同意匹配` 为主按钮。
- `再聊一聊` 为次按钮。

### 安全小贴士

使用蓝紫安全图标或盾牌插画。

建议文案：

```text
首次见面优先选择校园公共场所
不要轻易透露手机号、宿舍号等隐私
遇到不适可及时举报或异常反馈
```

### 倾诉引导卡片

视觉应更柔和，可使用粉紫渐变。

文案：

```text
今天的心情需要被听见吗？
有时候，说出来就会轻一点。
去倾诉广场
平台仅提供同伴陪伴与日常交流，不替代专业心理咨询服务。
```

---

## 8.9 悬浮发布按钮

页面右下角固定显示。

```css
.float-publish {
  position: fixed;
  right: 48px;
  bottom: 36px;
  height: 64px;
  padding: 0 32px;
  border-radius: 999px;
  color: #FFFFFF;
  font-size: 18px;
  font-weight: 800;
  background: linear-gradient(135deg, #7566F6, #F59ACB);
  box-shadow: 0 16px 34px rgba(117, 102, 246, 0.34);
}
```

按钮文案：

```text
发布星伴需求
```

可搭配图标：

- 加号 `+`
- 纸飞机
- 星星
- 铅笔

---

## 9. 图片与插画元素规范

### 9.1 插画风格

插画应保持：

- 扁平化
- 轻 3D 感可接受
- 柔和描边
- 低饱和色彩
- 人物表情友好
- 校园生活场景明确

适合的插画主题：

- 两名学生一起自习
- 图书馆学习
- 操场跑步
- 食堂吃饭
- 音乐节同行
- 书本、笔记本、星星、聊天气泡
- 小云朵、小爱心、小盾牌
- 校园建筑剪影

不适合的插画主题：

- 金融账单
- 银行卡
- 支付码
- 商务图表
- 严肃会议
- 过度成人化社交
- 医疗心理咨询诊断场景

### 9.2 装饰元素

可使用以下轻量装饰：

```text
星星 / 胶带 / 图钉 / 便利贴 / 小爱心 / 云朵 / 纸飞机 / 聊天气泡 / 盾牌 / 书本 / 餐具 / 羽毛球 / 奖杯
```

使用规则：

- 单屏装饰元素不要超过 12 个。
- 重要信息区域周围不要堆叠装饰。
- 胶带和图钉主要用于便利贴卡片。
- 右侧安全卡片可以使用盾牌插画。
- 倾诉卡片可以使用耳机、心形、云朵插画。

---

## 10. 页面模块规范

## 10.1 首页广场

首页是产品主视觉页，应重点展示 CampusMate 的核心卖点。

必须包含：

- 左侧导航栏
- 顶部欢迎语
- 搜索框
- 用户头像和认证状态
- 匹配广场 / 倾诉广场 Tab
- 分类筛选
- 便利贴需求卡片
- 我的星伴状态
- 待确认见面信息
- 安全小贴士
- 倾诉引导
- 发布星伴需求按钮

首页信息密度控制：

- 首屏卡片数量建议 5 - 8 张。
- 每张卡片标题不超过 14 个中文字符。
- 描述不超过 2 行。
- 时间地点尽量一行展示。

---

## 10.2 发布需求页面 / 弹窗

### 布局建议

使用居中弹窗或右侧抽屉，保持与首页一致的玻璃拟态风格。

字段顺序：

```text
所属广场：匹配广场 / 倾诉广场
需求类型
标题
描述
期望时间
期望地点
最大匹配人数：1 到 5
是否匿名发布
安全确认勾选项
提交发布
```

### 表单视觉

```css
.form-control {
  height: 44px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(218, 220, 238, 0.9);
  padding: 0 14px;
}

.textarea {
  min-height: 108px;
  border-radius: 18px;
  padding: 14px;
}
```

### 安全确认文案

```text
我承诺发布真实、友善、合规的内容，并优先选择校园公共区域进行线下见面。
```

### 发布后状态

发布后需要有三种反馈：

| 状态 | 提示 |
|---|---|
| 正常展示 | `发布成功，已展示在广场中` |
| 待审核 | `内容可能包含敏感信息，已进入待审核` |
| 失败 | `发布失败，请稍后重试` |

---

## 10.3 聊一聊页面

聊天页应保持轻量、温柔，不做强 IM 商务风。

### 基础结构

```text
左侧：会话列表
中间：聊天窗口
右侧：需求信息 / 安全提醒 / 匹配操作
```

### 聊天气泡

自己发送：右侧蓝紫浅渐变。

对方发送：左侧白色卡片。

```css
.bubble-me {
  background: linear-gradient(135deg, #7566F6, #9B7CFF);
  color: #FFFFFF;
  border-radius: 18px 18px 4px 18px;
}

.bubble-other {
  background: rgba(255, 255, 255, 0.86);
  color: #343753;
  border-radius: 18px 18px 18px 4px;
}
```

### 一条消息限制状态

发起方在发布者回复前，只能发送一条消息。

界面体现：

```text
对方回复前只能发送一条消息
```

第二条发送失败时：

- 气泡旁显示红色感叹号。
- 气泡变浅或灰化。
- 显示提示：`对方回复前只能发送一条消息，多余消息不会展示给对方。`

### 发布者回复后

状态提示切换为：

```text
对方已回复，可以正常聊天
```

此时展示按钮：

```text
发起匹配
```

---

## 10.4 匹配确认页面

### 页面目标

让接收者清楚知道：谁、什么类型、什么时候、在哪里见面，以及安全提示。

### 内容结构

```text
待确认见面信息
对方昵称：小熊饼干
类型：学习搭子
时间：今晚 19:00
地点：图书馆三楼
备注：三楼靠窗区域，安静自习

[同意匹配] [再聊一聊]

首次见面建议选择校园内公共区域。
```

### 匹配成功后信息展示

成功后展示双方基础身份信息：

```text
姓名
性别
年级
学院
专业
```

禁止展示：

```text
手机号
完整学号
宿舍号
身份证号
家庭住址
其他敏感隐私
```

### 安全操作

匹配详情页需要提供：

- 紧急取消
- 异常反馈
- 举报
- 拉黑

这些操作应放在次级区域，不能比确认按钮更抢眼，但必须容易找到。

---

## 10.5 管理员认证审核页面

管理员页面保持清爽，不使用过重的后台风格。

### 视觉原则

- 背景仍为浅灰蓝或浅紫白。
- 表格区域使用白色卡片承载。
- 操作按钮使用柔和状态色。
- 不使用大面积深色侧栏。

### 左侧管理菜单

```text
审核概览
认证审核
事件管理
举报处理
敏感词管理
用户管理
系统设置
```

### 表格字段

```text
用户昵称
姓名
性别
年级
学院
专业
模拟学号
提交时间
审核状态
操作
```

### 操作按钮

- 审核通过：绿色弱按钮或主按钮
- 驳回：红色弱按钮
- 查看详情：紫色次按钮

驳回弹窗需包含：

```text
驳回原因
```

---

## 10.6 管理员事件管理页面

### 表格字段

```text
事件标题
发布者
所属广场
需求类型
状态
敏感词命中
举报次数
发布时间
操作
```

### 筛选项

```text
全部
正常展示
待审核
已下架
已删除
```

### 操作按钮

- 恢复展示
- 下架
- 删除
- 查看举报
- 查看详情

### 状态显示

| 状态 | 样式 |
|---|---|
| 正常展示 | 绿色标签 |
| 待审核 | 橙色标签 |
| 已下架 | 灰色标签 |
| 已删除 | 红色弱标签 |
| 敏感词命中 | 橙色 / 红色提示 |
| 多人举报 | 红色数字徽标 |

---

## 11. 交互状态规范

## 11.1 未认证用户状态

未认证用户可以浏览，但不能发布、聊天、匹配。

当点击以下按钮：

- 发布需求
- 聊一聊
- 参与匹配

弹出提示：

```text
完成校园认证后，才可以发布需求、发起聊天和参与匹配。
```

按钮：

```text
去认证
```

视觉建议：

- 弹窗使用温和提醒风格，不要强警告。
- 图标可以用盾牌、星星、锁。
- 主按钮为 `去认证`。
- 次按钮为 `稍后再说`。

---

## 11.2 满员状态

当人数达到上限，例如 `5/5`：

- 卡片状态变为 `已满员`。
- `聊一聊` 按钮禁用。
- 可显示 Tooltip：`已满员 5/5，暂不可聊`。

---

## 11.3 聊天限制状态

发送第二条失败消息时：

- 显示红色感叹号。
- 消息透明度降低。
- 错误提示使用 `#EF5B75`。
- 文案：`对方回复前只能发送一条消息`。

---

## 11.4 审核状态

发布内容可能出现：

| 状态 | 说明 |
|---|---|
| 正常展示 | 普通内容默认展示 |
| 待审核 | 命中敏感词或多人举报 |
| 已下架 | 管理员下架 |
| 已删除 | 管理员删除 |

---

## 12. 动效建议

动效应轻，不要炫技。

### 12.1 Hover

卡片 Hover：

```css
.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 36px rgba(104, 112, 180, 0.16);
}
```

按钮 Hover：

```css
.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 14px 28px rgba(117, 102, 246, 0.34);
}
```

### 12.2 页面切换

建议使用：

- 150ms - 240ms
- ease-out
- opacity + translateY

```css
.transition-soft {
  transition: all 0.22s ease-out;
}
```

### 12.3 弹窗

弹窗出现：

- 背景遮罩淡入
- 弹窗从下方轻微上浮
- 不使用剧烈缩放

---

## 13. 图标规范

图标风格：

- 线性图标优先
- 圆角线端
- 2px 线宽
- 可以局部填充渐变
- 不使用复杂写实图标

建议图标语义：

| 功能 | 图标建议 |
|---|---|
| 广场首页 | Home |
| 发布需求 | Paper Plane / Edit |
| 我的聊天 | Message Circle |
| 我的匹配 | Heart / Link |
| 倾诉广场 | Smile / Chat |
| 认证中心 | Shield Check |
| 安全反馈 | Shield Alert |
| 个人中心 | User |
| 学习搭子 | Book |
| 饭搭子 | Utensils |
| 运动搭子 | Shuttlecock / Running |
| 比赛组队 | Trophy |
| 活动同行 | Calendar / Flag |
| 倾诉陪伴 | Heart / Headphones |

---

## 14. 可访问性与可读性

### 14.1 文本可读性

- 正文文本不要低于 12px。
- 主要文字与背景对比度要足够。
- 浅色卡片上的文字不要使用过浅颜色。
- 标签文字需要保持清晰，不要只依靠颜色区分状态。

### 14.2 交互可点击区域

- 按钮高度不低于 36px。
- 常用按钮建议 40px - 44px。
- 移动端适配时点击区域不低于 44px。

### 14.3 状态提示

- 错误、警告、成功状态要同时使用颜色和文字。
- 已满员、待审核、待认证等状态必须有明确文字。

---

## 15. 响应式建议

### 桌面端

- 保持三栏布局。
- Sidebar 固定宽度。
- 右侧辅助栏固定宽度。
- 中间内容自适应。

### 平板端

- Sidebar 可收起为图标栏。
- 右侧辅助区移到主内容下方。
- 便利贴卡片改为 2 列。

### 移动端

- 左侧导航改为底部 TabBar。
- 搜索框独立成顶部一行。
- 便利贴卡片改为单列。
- 发布按钮固定在右下角或底部操作区。
- 管理端表格改为卡片列表。

---

## 16. 推荐 CSS Token 汇总

```css
:root {
  --primary-500: #7566F6;
  --primary-600: #6554E8;
  --primary-400: #8B7CFF;
  --primary-100: #EFEAFE;

  --gradient-primary: linear-gradient(135deg, #7566F6 0%, #9B7CFF 52%, #F59ACB 100%);

  --bg-page: #F7F8FF;
  --bg-lilac: #F8F5FF;
  --bg-card: rgba(255, 255, 255, 0.78);
  --bg-card-solid: #FFFFFF;

  --text-title: #20233F;
  --text-primary: #343753;
  --text-secondary: #7B7F9A;
  --text-tertiary: #A4A7BA;
  --text-white: #FFFFFF;

  --blue-soft: #EAF5FF;
  --green-soft: #EAF8EF;
  --yellow-soft: #FFF6D8;
  --pink-soft: #FFEAF3;
  --purple-soft: #F0EAFE;
  --orange-soft: #FFF0DC;

  --success: #32C776;
  --warning: #F5A623;
  --danger: #EF5B75;
  --info: #5B8DFF;
  --disabled: #C8CBDD;

  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 18px;
  --radius-xl: 24px;
  --radius-xxl: 32px;
  --radius-pill: 999px;

  --shadow-card: 0 12px 32px rgba(104, 112, 180, 0.12);
  --shadow-soft: 0 8px 24px rgba(104, 112, 180, 0.10);
  --shadow-button: 0 10px 24px rgba(117, 102, 246, 0.28);
  --shadow-note: 0 10px 22px rgba(104, 112, 180, 0.14);

  --space-4: 4px;
  --space-8: 8px;
  --space-12: 12px;
  --space-16: 16px;
  --space-20: 20px;
  --space-24: 24px;
  --space-32: 32px;
  --space-40: 40px;
  --space-48: 48px;
}
```

---

## 17. 页面气质总结

CampusMate 的界面应当像一个温柔的校园公告栏：

- 左侧是清晰的功能导航。
- 中间是充满便利贴感的星伴需求广场。
- 右侧是个人状态、安全提醒和陪伴引导。
- 整体浅色、圆润、柔和、有插画感。
- 所有操作都围绕“校园认证、安全匹配、轻量陪伴、公共区域见面”展开。

一句话概括：

> **用柔和的 Dashboard 结构承载校园便利贴广场，让用户感到轻松、可信、愿意发起一次安全的校园陪伴。**
