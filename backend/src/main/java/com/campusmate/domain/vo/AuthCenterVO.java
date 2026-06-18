package com.campusmate.domain.vo;

import java.util.List;

public class AuthCenterVO {

    private SummaryVO summary;
    private List<BenefitVO> benefits;
    private List<SampleVO> samples;
    private List<RightVO> rights;
    private List<RecordVO> records;

    public SummaryVO getSummary() {
        return summary;
    }

    public void setSummary(SummaryVO summary) {
        this.summary = summary;
    }

    public List<BenefitVO> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<BenefitVO> benefits) {
        this.benefits = benefits;
    }

    public List<SampleVO> getSamples() {
        return samples;
    }

    public void setSamples(List<SampleVO> samples) {
        this.samples = samples;
    }

    public List<RightVO> getRights() {
        return rights;
    }

    public void setRights(List<RightVO> rights) {
        this.rights = rights;
    }

    public List<RecordVO> getRecords() {
        return records;
    }

    public void setRecords(List<RecordVO> records) {
        this.records = records;
    }

    public static class SummaryVO {
        private Long userId;
        private String nickname;
        private String avatarUrl;
        private String avatarText;
        private boolean verified;
        private String verifyStatus;
        private String statusText;
        private String levelTitle;
        private String levelHint;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
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

        public String getAvatarText() {
            return avatarText;
        }

        public void setAvatarText(String avatarText) {
            this.avatarText = avatarText;
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

        public String getStatusText() {
            return statusText;
        }

        public void setStatusText(String statusText) {
            this.statusText = statusText;
        }

        public String getLevelTitle() {
            return levelTitle;
        }

        public void setLevelTitle(String levelTitle) {
            this.levelTitle = levelTitle;
        }

        public String getLevelHint() {
            return levelHint;
        }

        public void setLevelHint(String levelHint) {
            this.levelHint = levelHint;
        }
    }

    public static class BenefitVO {
        private String label;
        private String icon;

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
    }

    public static class SampleVO {
        private String label;
        private String tone;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getTone() {
            return tone;
        }

        public void setTone(String tone) {
            this.tone = tone;
        }
    }

    public static class RightVO {
        private String title;
        private String description;
        private String icon;

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

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    public static class RecordVO {
        private Long recordId;
        private String time;
        private String type;
        private String content;
        private String status;
        private String statusClass;
        private String feedback;
        private String action;

        public Long getRecordId() {
            return recordId;
        }

        public void setRecordId(Long recordId) {
            this.recordId = recordId;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatusClass() {
            return statusClass;
        }

        public void setStatusClass(String statusClass) {
            this.statusClass = statusClass;
        }

        public String getFeedback() {
            return feedback;
        }

        public void setFeedback(String feedback) {
            this.feedback = feedback;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }
}
