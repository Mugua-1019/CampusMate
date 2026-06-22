package com.campusmate.domain.vo;

import java.util.List;

public class HomeMatchVO {

    private List<MetricVO> metrics;
    private List<MatchCardVO> startedPosts;
    private List<MatchCardVO> joinedPosts;
    private List<RecentMatchVO> recentMatches;

    public List<MetricVO> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<MetricVO> metrics) {
        this.metrics = metrics;
    }

    public List<MatchCardVO> getStartedPosts() {
        return startedPosts;
    }

    public void setStartedPosts(List<MatchCardVO> startedPosts) {
        this.startedPosts = startedPosts;
    }

    public List<MatchCardVO> getJoinedPosts() {
        return joinedPosts;
    }

    public void setJoinedPosts(List<MatchCardVO> joinedPosts) {
        this.joinedPosts = joinedPosts;
    }

    public List<RecentMatchVO> getRecentMatches() {
        return recentMatches;
    }

    public void setRecentMatches(List<RecentMatchVO> recentMatches) {
        this.recentMatches = recentMatches;
    }

    public static class MetricVO {
        private String label;
        private String value;
        private String note;
        private String tone;

        public MetricVO(String label, String value, String note, String tone) {
            this.label = label;
            this.value = value;
            this.note = note;
            this.tone = tone;
        }

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }

        public String getNote() {
            return note;
        }

        public String getTone() {
            return tone;
        }
    }

    public static class MatchCardVO {
        private Long requestId;
        private Long postId;
        private Long peerUserId;
        private String title;
        private String category;
        private String description;
        private String location;
        private String time;
        private String partner;
        private String avatarUrl;
        private int requestCount;
        private String status;
        private String stateText;
        private List<String> tags;
        private String elapsed;

        public Long getRequestId() {
            return requestId;
        }

        public void setRequestId(Long requestId) {
            this.requestId = requestId;
        }

        public Long getPostId() {
            return postId;
        }

        public void setPostId(Long postId) {
            this.postId = postId;
        }

        public Long getPeerUserId() {
            return peerUserId;
        }

        public void setPeerUserId(Long peerUserId) {
            this.peerUserId = peerUserId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
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

        public String getPartner() {
            return partner;
        }

        public void setPartner(String partner) {
            this.partner = partner;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public int getRequestCount() {
            return requestCount;
        }

        public void setRequestCount(int requestCount) {
            this.requestCount = requestCount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStateText() {
            return stateText;
        }

        public void setStateText(String stateText) {
            this.stateText = stateText;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public String getElapsed() {
            return elapsed;
        }

        public void setElapsed(String elapsed) {
            this.elapsed = elapsed;
        }
    }

    public static class RecentMatchVO {
        private String title;
        private String category;
        private String time;
        private String avatarUrl;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
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

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }
}
