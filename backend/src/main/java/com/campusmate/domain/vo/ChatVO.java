package com.campusmate.domain.vo;

import java.math.BigDecimal;
import java.util.List;

public class ChatVO {

    private List<ConversationVO> conversations;
    private List<ConversationVO> archivedConversations;

    public List<ConversationVO> getConversations() {
        return conversations;
    }

    public void setConversations(List<ConversationVO> conversations) {
        this.conversations = conversations;
    }

    public List<ConversationVO> getArchivedConversations() {
        return archivedConversations;
    }

    public void setArchivedConversations(List<ConversationVO> archivedConversations) {
        this.archivedConversations = archivedConversations;
    }

    public static class ConversationVO {
        private Long id;
        private Long peerUserId;
        private String name;
        private String avatarUrl;
        private String avatarText;
        private String badge;
        private String preview;
        private String time;
        private int unread;
        private boolean online;
        private boolean archived;
        private String gender;
        private Integer age;
        private String school;
        private String grade;
        private String major;
        private String matchDate;
        private String source;
        private String commonTagsText;
        private List<String> tags;
        private List<String> commonTags;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getPeerUserId() {
            return peerUserId;
        }

        public void setPeerUserId(Long peerUserId) {
            this.peerUserId = peerUserId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getAvatarText() {
            return avatarText;
        }

        public void setAvatarText(String avatarText) {
            this.avatarText = avatarText;
        }

        public String getBadge() {
            return badge;
        }

        public void setBadge(String badge) {
            this.badge = badge;
        }

        public String getPreview() {
            return preview;
        }

        public void setPreview(String preview) {
            this.preview = preview;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getUnread() {
            return unread;
        }

        public void setUnread(int unread) {
            this.unread = unread;
        }

        public boolean isOnline() {
            return online;
        }

        public void setOnline(boolean online) {
            this.online = online;
        }

        public boolean isArchived() {
            return archived;
        }

        public void setArchived(boolean archived) {
            this.archived = archived;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getMajor() {
            return major;
        }

        public void setMajor(String major) {
            this.major = major;
        }

        public String getMatchDate() {
            return matchDate;
        }

        public void setMatchDate(String matchDate) {
            this.matchDate = matchDate;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getCommonTagsText() {
            return commonTagsText;
        }

        public void setCommonTagsText(String commonTagsText) {
            this.commonTagsText = commonTagsText;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public List<String> getCommonTags() {
            return commonTags;
        }

        public void setCommonTags(List<String> commonTags) {
            this.commonTags = commonTags;
        }
    }

    public static class MessageVO {
        private Long id;
        private Long conversationId;
        private Long senderUserId;
        private Long receiverUserId;
        private boolean mine;
        private String messageType;
        private String text;
        private String attachmentUrl;
        private String attachmentName;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private String time;
        private String createdAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getConversationId() {
            return conversationId;
        }

        public void setConversationId(Long conversationId) {
            this.conversationId = conversationId;
        }

        public Long getSenderUserId() {
            return senderUserId;
        }

        public void setSenderUserId(Long senderUserId) {
            this.senderUserId = senderUserId;
        }

        public Long getReceiverUserId() {
            return receiverUserId;
        }

        public void setReceiverUserId(Long receiverUserId) {
            this.receiverUserId = receiverUserId;
        }

        public boolean isMine() {
            return mine;
        }

        public void setMine(boolean mine) {
            this.mine = mine;
        }

        public String getMessageType() {
            return messageType;
        }

        public void setMessageType(String messageType) {
            this.messageType = messageType;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getAttachmentUrl() {
            return attachmentUrl;
        }

        public void setAttachmentUrl(String attachmentUrl) {
            this.attachmentUrl = attachmentUrl;
        }

        public String getAttachmentName() {
            return attachmentName;
        }

        public void setAttachmentName(String attachmentName) {
            this.attachmentName = attachmentName;
        }

        public BigDecimal getLatitude() {
            return latitude;
        }

        public void setLatitude(BigDecimal latitude) {
            this.latitude = latitude;
        }

        public BigDecimal getLongitude() {
            return longitude;
        }

        public void setLongitude(BigDecimal longitude) {
            this.longitude = longitude;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}
