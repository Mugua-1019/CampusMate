package com.campusmate.domain.dto;

import java.util.List;

public class ProfilePreferenceUpdateRequest {

    private Long userId;
    private List<Item> preferences;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Item> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<Item> preferences) {
        this.preferences = preferences;
    }

    public static class Item {
        private String label;
        private Integer score;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }
    }
}
