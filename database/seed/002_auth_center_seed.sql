insert ignore into auth_center_benefit
(label, icon_key, sort_weight)
values
('发布需求', 'Document', 10),
('发起聊天', 'ChatDotRound', 20),
('参与匹配', 'Select', 30),
('提升可信度', 'Lock', 40),
('更多推荐', 'DataLine', 50);

insert ignore into auth_center_material_sample
(label, tone, sort_weight)
values
('学生证', 'sample-id', 10),
('校园卡', 'sample-card', 20),
('在校证明', 'sample-paper', 30),
('课表截图', 'sample-table', 40);

insert ignore into auth_center_right
(title, description, icon_key, sort_weight)
values
('发布需求', '发布学习、生活、活动等各类需求', 'Document', 10),
('发起聊天', '与更多同学发起聊天，拓展校园连接', 'ChatDotRound', 20),
('参与匹配', '解锁更多匹配机会，找到合适伙伴', 'Select', 30),
('提升可信度', '认证标识展示，增强他人信任感', 'Lock', 40);
