# CampusMate 前端图片资源清单

图片统一放在：`frontend/src/assets/images/`

| 序号 | 用途 | 文件名 | 建议尺寸 | 放置路径 | 说明 |
|---:|---|---|---:|---|---|
| 1 | 左上角星星 Logo | `logo-star-mascot.png` | 256x256 | `frontend/src/assets/images/logo-star-mascot.png` | 透明背景，软萌星星头像，用于品牌区。 |
| 2 | 顶部当前用户头像 | `avatar-current-user.png` | 256x256 | `frontend/src/assets/images/avatar-current-user.png` | 必须正方形，页面会裁成圆形头像。 |
| 3 | 首页横幅主插画 | `hero-campus-partners.png` | 960x420 | `frontend/src/assets/images/hero-campus-partners.png` | 两名学生学习、校园建筑、对话气泡、书本等主视觉。 |
| 4 | 认证引导插画 | `illustration-verification-card.png` | 480x260 | `frontend/src/assets/images/illustration-verification-card.png` | 左下角认证卡片使用，建议小星星+证件或清单。 |
| 5 | 右侧状态星星头像 | `status-star-avatar.png` | 320x320 | `frontend/src/assets/images/status-star-avatar.png` | “我的星伴状态”卡片里的星星头像。 |
| 6 | 待确认见面头像 | `avatar-pending-match.png` | 256x256 | `frontend/src/assets/images/avatar-pending-match.png` | 必须正方形，用于待确认见面信息卡。 |
| 7 | 安全提示盾牌插画 | `illustration-safety-shield.png` | 320x360 | `frontend/src/assets/images/illustration-safety-shield.png` | 盾牌加星星，透明背景。 |
| 8 | 倾诉入口插画 | `illustration-vent-cloud.png` | 360x260 | `frontend/src/assets/images/illustration-vent-cloud.png` | 云朵、耳机或爱心风格插画，放在倾诉卡片右下角。 |
| 9 | 便签胶带装饰 | `note-tape-pink.png` | 240x80 | `frontend/src/assets/images/note-tape-pink.png` | 可选，用于替换当前 CSS 胶带效果。 |
| 10 | 便签星星图钉 | `note-pin-star.png` | 160x160 | `frontend/src/assets/images/note-pin-star.png` | 可选，用于替换当前 CSS 星星图钉。 |

命名规则：

- 全部使用小写英文，单词之间用短横线连接。
- 优先使用 `.png`。
- 插画类资源建议透明背景。
- 头像类资源必须是正方形，避免圆形裁剪后变形。
- 图片压缩后再放入项目，单张图片建议不超过 500KB。
