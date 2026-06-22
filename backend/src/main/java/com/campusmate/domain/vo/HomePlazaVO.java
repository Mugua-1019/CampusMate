package com.campusmate.domain.vo;

import java.util.List;

public class HomePlazaVO {

    private String activePlaza;
    private String selectedCategory;
    private List<PlazaTabVO> tabs;
    private UserSummaryVO userSummary;
    private List<HomePostVO> posts;

    public String getActivePlaza() {
        return activePlaza;
    }

    public void setActivePlaza(String activePlaza) {
        this.activePlaza = activePlaza;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public List<PlazaTabVO> getTabs() {
        return tabs;
    }

    public void setTabs(List<PlazaTabVO> tabs) {
        this.tabs = tabs;
    }

    public UserSummaryVO getUserSummary() {
        return userSummary;
    }

    public void setUserSummary(UserSummaryVO userSummary) {
        this.userSummary = userSummary;
    }

    public List<HomePostVO> getPosts() {
        return posts;
    }

    public void setPosts(List<HomePostVO> posts) {
        this.posts = posts;
    }

    public static class PlazaTabVO {
        private String key;
        private String label;
        private String description;
        private List<String> categories;

        public PlazaTabVO(String key, String label, String description, List<String> categories) {
            this.key = key;
            this.label = label;
            this.description = description;
            this.categories = categories;
        }

        public String getKey() {
            return key;
        }

        public String getLabel() {
            return label;
        }

        public String getDescription() {
            return description;
        }

        public List<String> getCategories() {
            return categories;
        }
    }

    public static class UserSummaryVO {
        private boolean verified;
        private String nickname;
        private String avatarUrl;
        private List<StatVO> stats;
        private PendingMeetVO pendingMeet;

        public boolean isVerified() {
            return verified;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public List<StatVO> getStats() {
            return stats;
        }

        public void setStats(List<StatVO> stats) {
            this.stats = stats;
        }

        public PendingMeetVO getPendingMeet() {
            return pendingMeet;
        }

        public void setPendingMeet(PendingMeetVO pendingMeet) {
            this.pendingMeet = pendingMeet;
        }
    }

    public static class StatVO {
        private String label;
        private int value;

        public StatVO(String label, int value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public int getValue() {
            return value;
        }
    }

    public static class PendingMeetVO {
        private String title;
        private String partner;
        private String category;
        private String time;
        private String location;
        private String status;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPartner() {
            return partner;
        }

        public void setPartner(String partner) {
            this.partner = partner;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class HomePostVO {
        private Long id;
        private String plaza;
        private String category;
        private String title;
        private String status;
        private List<String> tags;
        private String description;
        private String time;
        private String location;
        private String aaFee;
        private Long publisherUserId;
        private String publisherName;
        private String publisherAvatarUrl;
        private String publisherStatus;
        private String publisherStatusNote;
        private String avatarText;
        private boolean anonymous;
        private boolean verified;
        private int currentCount;
        private int maxCount;
        private boolean full;
        private List<String> currentState;
        private List<String> hopeYouCan;
        private List<String> preferredWay;
        private List<String> gentleReplies;
        private List<HomePostReplyVO> replies;
        private List<HomePostVO> similarPosts;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getPlaza() {
            return plaza;
        }

        public void setPlaza(String plaza) {
            this.plaza = plaza;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getAaFee() {
            return aaFee;
        }

        public void setAaFee(String aaFee) {
            this.aaFee = aaFee;
        }

        public Long getPublisherUserId() {
            return publisherUserId;
        }

        public void setPublisherUserId(Long publisherUserId) {
            this.publisherUserId = publisherUserId;
        }

        public String getPublisherName() {
            return publisherName;
        }

        public void setPublisherName(String publisherName) {
            this.publisherName = publisherName;
        }

        public String getPublisherAvatarUrl() {
            return publisherAvatarUrl;
        }

        public void setPublisherAvatarUrl(String publisherAvatarUrl) {
            this.publisherAvatarUrl = publisherAvatarUrl;
        }

        public String getPublisherStatus() {
            return publisherStatus;
        }

        public void setPublisherStatus(String publisherStatus) {
            this.publisherStatus = publisherStatus;
        }

        public String getPublisherStatusNote() {
            return publisherStatusNote;
        }

        public void setPublisherStatusNote(String publisherStatusNote) {
            this.publisherStatusNote = publisherStatusNote;
        }

        public String getAvatarText() {
            return avatarText;
        }

        public void setAvatarText(String avatarText) {
            this.avatarText = avatarText;
        }

        public boolean isAnonymous() {
            return anonymous;
        }

        public void setAnonymous(boolean anonymous) {
            this.anonymous = anonymous;
        }

        public boolean isVerified() {
            return verified;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
        }

        public int getCurrentCount() {
            return currentCount;
        }

        public void setCurrentCount(int currentCount) {
            this.currentCount = currentCount;
        }

        public int getMaxCount() {
            return maxCount;
        }

        public void setMaxCount(int maxCount) {
            this.maxCount = maxCount;
        }

        public boolean isFull() {
            return full;
        }

        public void setFull(boolean full) {
            this.full = full;
        }

        public List<String> getCurrentState() {
            return currentState;
        }

        public void setCurrentState(List<String> currentState) {
            this.currentState = currentState;
        }

        public List<String> getHopeYouCan() {
            return hopeYouCan;
        }

        public void setHopeYouCan(List<String> hopeYouCan) {
            this.hopeYouCan = hopeYouCan;
        }

        public List<String> getPreferredWay() {
            return preferredWay;
        }

        public void setPreferredWay(List<String> preferredWay) {
            this.preferredWay = preferredWay;
        }

        public List<String> getGentleReplies() {
            return gentleReplies;
        }

        public void setGentleReplies(List<String> gentleReplies) {
            this.gentleReplies = gentleReplies;
        }

        public List<HomePostReplyVO> getReplies() {
            return replies;
        }

        public void setReplies(List<HomePostReplyVO> replies) {
            this.replies = replies;
        }

        public List<HomePostVO> getSimilarPosts() {
            return similarPosts;
        }

        public void setSimilarPosts(List<HomePostVO> similarPosts) {
            this.similarPosts = similarPosts;
        }
    }

    public static class HomePostReplyVO {
        private Long id;
        private Long postId;
        private Long userId;
        private String authorName;
        private String avatarText;
        private String content;
        private int likeCount;
        private String createdAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getPostId() {
            return postId;
        }

        public void setPostId(Long postId) {
            this.postId = postId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getAuthorName() {
            return authorName;
        }

        public void setAuthorName(String authorName) {
            this.authorName = authorName;
        }

        public String getAvatarText() {
            return avatarText;
        }

        public void setAvatarText(String avatarText) {
            this.avatarText = avatarText;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(int likeCount) {
            this.likeCount = likeCount;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class HomeNotificationVO {
        private Long id;
        private String message;
        private int count;
        private boolean unread;
        private String updatedAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public boolean isUnread() {
            return unread;
        }

        public void setUnread(boolean unread) {
            this.unread = unread;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    public static class HomePostComfortVO {
        private Long postId;
        private int currentCount;

        public Long getPostId() {
            return postId;
        }

        public void setPostId(Long postId) {
            this.postId = postId;
        }

        public int getCurrentCount() {
            return currentCount;
        }

        public void setCurrentCount(int currentCount) {
            this.currentCount = currentCount;
        }
    }
}
