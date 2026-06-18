package com.campusmate.domain.vo;

import java.util.List;

public class ProfileVO {

    private Long userId;
    private String avatarUrl;
    private String nickname;
    private String gender;
    private String grade;
    private String college;
    private String major;
    private List<String> interestTags;
    private String bio;
    private String realName;
    private boolean verified;
    private String verifyStatus;
    private int completionPercent;
    private String completionHint;
    private CampusVerifyVO campusVerify;
    private List<PreferenceVO> preferences;
    private ActivityVO activity;
    private SafetyVO safety;
    private List<ChatVO> chats;
    private List<PostVO> posts;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public List<String> getInterestTags() {
        return interestTags;
    }

    public void setInterestTags(List<String> interestTags) {
        this.interestTags = interestTags;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(String verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public int getCompletionPercent() {
        return completionPercent;
    }

    public void setCompletionPercent(int completionPercent) {
        this.completionPercent = completionPercent;
    }

    public String getCompletionHint() {
        return completionHint;
    }

    public void setCompletionHint(String completionHint) {
        this.completionHint = completionHint;
    }

    public CampusVerifyVO getCampusVerify() {
        return campusVerify;
    }

    public void setCampusVerify(CampusVerifyVO campusVerify) {
        this.campusVerify = campusVerify;
    }

    public List<PreferenceVO> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<PreferenceVO> preferences) {
        this.preferences = preferences;
    }

    public ActivityVO getActivity() {
        return activity;
    }

    public void setActivity(ActivityVO activity) {
        this.activity = activity;
    }

    public SafetyVO getSafety() {
        return safety;
    }

    public void setSafety(SafetyVO safety) {
        this.safety = safety;
    }

    public List<ChatVO> getChats() {
        return chats;
    }

    public void setChats(List<ChatVO> chats) {
        this.chats = chats;
    }

    public List<PostVO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostVO> posts) {
        this.posts = posts;
    }

    public static class CampusVerifyVO {
        private String title;
        private String description;
        private String actionText;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getActionText() {
            return actionText;
        }

        public void setActionText(String actionText) {
            this.actionText = actionText;
        }
    }

    public static class PreferenceVO {
        private String label;
        private String icon;
        private int score;
        private String tone;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getTone() {
            return tone;
        }

        public void setTone(String tone) {
            this.tone = tone;
        }
    }

    public static class ActivityVO {
        private int score;
        private int percentile;
        private List<ActivityBarVO> weekBars;

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getPercentile() {
            return percentile;
        }

        public void setPercentile(int percentile) {
            this.percentile = percentile;
        }

        public List<ActivityBarVO> getWeekBars() {
            return weekBars;
        }

        public void setWeekBars(List<ActivityBarVO> weekBars) {
            this.weekBars = weekBars;
        }
    }

    public static class ActivityBarVO {
        private String day;
        private int value;

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class SafetyVO {
        private String status;
        private int creditScore;
        private String safetyLevel;
        private List<String> items;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getCreditScore() {
            return creditScore;
        }

        public void setCreditScore(int creditScore) {
            this.creditScore = creditScore;
        }

        public String getSafetyLevel() {
            return safetyLevel;
        }

        public void setSafetyLevel(String safetyLevel) {
            this.safetyLevel = safetyLevel;
        }

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }
    }

    public static class ChatVO {
        private String avatar;
        private String name;
        private String tag;
        private String message;
        private String time;
        private Integer unread;
        private String tone;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public Integer getUnread() {
            return unread;
        }

        public void setUnread(Integer unread) {
            this.unread = unread;
        }

        public String getTone() {
            return tone;
        }

        public void setTone(String tone) {
            this.tone = tone;
        }
    }

    public static class PostVO {
        private String shortLabel;
        private String title;
        private String tag;
        private String period;
        private String description;
        private String location;
        private String time;
        private int matched;
        private String tone;

        public String getShortLabel() {
            return shortLabel;
        }

        public void setShortLabel(String shortLabel) {
            this.shortLabel = shortLabel;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getMatched() {
            return matched;
        }

        public void setMatched(int matched) {
            this.matched = matched;
        }

        public String getTone() {
            return tone;
        }

        public void setTone(String tone) {
            this.tone = tone;
        }
    }
}
