package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity {
    private String title;

    public Channel(String title) {
        super();
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void updateTitle(String title) {
        this.title = title;
        updateTimestamp();
    }
}
