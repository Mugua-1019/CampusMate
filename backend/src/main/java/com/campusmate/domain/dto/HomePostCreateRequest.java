package com.campusmate.domain.dto;

import java.util.List;

public class HomePostCreateRequest {

    private Long userId;
    private String plaza;
    private String category;
    private String title;
    private String description;
    private String time;
    private String location;
    private String aaFee;
    private Integer maxCount;
    private Boolean anonymous;
    private List<String> tags;
    private List<String> currentState;
    private List<String> hopeYouCan;
    private List<String> preferredWay;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Integer getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
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
}
