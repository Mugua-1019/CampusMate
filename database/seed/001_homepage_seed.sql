delete from home_post;
delete from home_pending_meet;
delete from home_user_stat;
delete from home_user_summary;
delete from profile_publish;
delete from profile_recent_chat;
delete from profile_safety_item;
delete from profile_safety;
delete from profile_activity_bar;
delete from profile_activity_summary;
delete from profile_preference;
delete from profile_campus_verify;
delete from user_profile;
delete from user_account;
delete from home_plaza_category;
delete from home_plaza_tab;

insert into home_plaza_tab
(plaza_key, label, description, sort_weight, enabled)
values
('match', '匹配广场', '学习、吃饭、运动、比赛和活动同行', 10, 1),
('vent', '倾诉广场', '找人聊聊、情绪陪伴和日常分享', 20, 1);

insert into home_plaza_category
(plaza_key, category_name, sort_weight, enabled)
values
('match', '全部', 0, 1),
('match', '学习搭子', 10, 1),
('match', '饭搭子', 20, 1),
('match', '运动搭子', 30, 1),
('match', '比赛组队', 40, 1),
('match', '活动同行', 50, 1),
('match', '闲聊陪伴', 60, 1),
('match', '其他', 70, 1),
('vent', '全部', 0, 1),
('vent', '想找人聊聊', 10, 1),
('vent', '心情不好', 20, 1),
('vent', '学业压力', 30, 1),
('vent', '考试焦虑', 40, 1),
('vent', '生活分享', 50, 1),
('vent', '只想被听见', 60, 1);

insert into home_user_summary
(user_id, verified, nickname)
values
(1, 0, '小星同学');

insert into user_profile
(user_id, avatar_url, nickname, gender, grade, college, major, interest_tags, bio, real_name, verified, verify_status)
values
(1, '/avatars/default.png', '小星同学', '女', '2023级', '计算机学院', '软件工程', '自习,跑步,音乐', '想找到靠谱的校园搭子。', '陈小星', 0, 'pending');

insert into user_account
(user_id, account, phone, email, password_salt, password_hash, enabled)
values
(1, 'demo', '13800000000', 'demo@campusmate.local', 'campusmate-demo-salt', '83031fa8dc640bad4a8cea76b59a709c261dd02d818268d4bf8382c2d50fa338', 1);

insert into home_user_stat
(user_id, stat_label, stat_value, sort_weight)
values
(1, '今日可匹配', 3, 10),
(1, '进行中聊天', 2, 20),
(1, '待确认匹配', 1, 30),
(1, '已完成匹配', 8, 40);

insert into profile_campus_verify
(user_id, title, description, action_text)
values
(1, '校园认证', '完成校园认证后，解锁更多功能和信任标识。', '去认证');

insert into profile_preference
(user_id, label, icon, score, tone, sort_weight)
values
(1, '学习搭子', 'reading', 6, 'purple', 10),
(1, '运动搭子', 'promotion', 5, 'coral', 20),
(1, '倾听搭子', 'headset', 4, 'pink', 30);

insert into profile_activity_summary
(user_id, activity_score, percentile)
values
(1, 42, 68);

insert into profile_activity_bar
(user_id, day_label, activity_value, sort_weight)
values
(1, '一', 32, 10),
(1, '二', 44, 20),
(1, '三', 52, 30),
(1, '四', 40, 40),
(1, '五', 26, 50),
(1, '六', 38, 60),
(1, '日', 42, 70);

insert into profile_safety
(user_id, status_label, credit_score, safety_level)
values
(1, '良好', 92, '高');

insert into profile_safety_item
(user_id, item_text, sort_weight)
values
(1, '无违规记录', 10),
(1, '资料已保护', 20),
(1, '实名认证待完成', 30);

insert into profile_recent_chat
(user_id, avatar_text, display_name, tag, message, display_time, unread_count, tone, sort_weight)
values
(1, '风', '风来轻扬晚步', '学习搭子', '你好呀！我们可以一起复习高数。', '10:24', 2, 'warm', 10),
(1, '桐', '桐子淌水', '运动搭子', '周末有羽毛球局，要一起吗？', '昨天', 1, 'pink', 20),
(1, '书', '书山有路', '学习搭子', '分享了一份资料给你。', '05-18', null, 'green', 30);

insert into profile_publish
(user_id, short_label, title, tag, period, description, location, display_time, matched_count, tone, sort_weight)
values
(1, '学', '求高数期末复习搭子', '学习搭子', '长期', '求一起复习高数的小伙伴，互相监督，共同进步。', '图书馆附近', '2024-05-20 12:30', 3, 'study', 10),
(1, '运', '周末羽毛球运动搭子', '运动搭子', '周末', '每周六下午，有一起打羽毛球的小伙伴吗？', '体育馆', '2024-05-19 09:48', 2, 'sport', 20);

insert into home_pending_meet
(user_id, title, partner, category, meet_time, location, status, active)
values
(1, '周末操场跑步', '风一同学', '运动搭子', '明天 08:00', '操场西侧入口', '等待你确认', 1);

insert into home_post
(plaza, category, title, status, tags, description, expected_time, expected_location, publisher_name, avatar_text, anonymous, verified, current_count, max_count, review_status, sort_weight)
values
('match', '学习搭子', '今晚图书馆自习', 'matching', '自习,专注互勉,已认证', '一起到图书馆安静学习，互相监督，效率加倍。', '今晚 19:00', '图书馆三楼自习区', '小星星不熬夜', '星', 0, 1, 3, 4, 'visible', 90),
('match', '饭搭子', '二食堂午饭搭子', 'matching', '饭搭子,不赶时间', '中午一起吃饭，想试试新开的窗口，顺便聊聊天。', '今天 12:10', '第二食堂二楼', '可可不甜', '可', 0, 1, 2, 4, 'visible', 80),
('match', '运动搭子', '周末操场跑步', 'matching', '跑步,打卡', '慢跑三公里，新手友好，结束后一起拉伸。', '周六 08:00', '操场西侧入口', '风一同学', '风', 0, 1, 4, 5, 'visible', 70),
('match', '比赛组队', '数学建模缺队友', 'matching', '建模,写作,已认证', '已有两人，想找一位擅长论文排版和表达的伙伴。', '本周内', '线上 + 创客空间', '模型小队长', '模', 0, 1, 2, 3, 'visible', 60),
('match', '活动同行', '讲座一起占座', 'matching', '讲座,活动同行', '人工智能公开讲座，想找同学一起去，结束后可交流笔记。', '明晚 18:30', '大学生活动中心', '小鹿同学', '鹿', 0, 1, 1, 5, 'visible', 50),
('match', '运动搭子', '周五夜骑校园', 'full', '夜骑,放松', '微风正好，绕校园慢骑一圈，当前名额已约满。', '周五 20:00', '校门口集合', '骑行少年', '骑', 0, 1, 5, 5, 'visible', 40),
('match', '闲聊陪伴', '晚饭后湖边散步', 'matching', '闲聊,散步', '不赶路，不输出大道理，就轻松聊聊今天发生的事。', '今晚 21:00', '明湖东侧步道', '月亮邮差', '月', 0, 1, 1, 2, 'visible', 30),
('vent', '考试焦虑', '复习焦虑想聊聊', 'matching', '倾听,考试焦虑', '最近复习节奏有点乱，想找人互相鼓励十分钟。', '今晚', '线上文字', '匿名同学', '匿', 1, 0, 0, 1, 'visible', 90),
('vent', '心情不好', '今天有点低落', 'matching', '只想被听见,匿名', '不需要建议，只想有人听我把今天说完。', '随时', '线上语音前先文字', '匿名同学', '匿', 1, 0, 0, 1, 'visible', 80),
('vent', '生活分享', '想分享一件小事', 'matching', '生活分享,轻松聊', '今天遇到一件很暖的小事，想找人一起开心一下。', '今天下午', '校园咖啡角', '橘子汽水', '橘', 0, 1, 1, 2, 'visible', 70);
