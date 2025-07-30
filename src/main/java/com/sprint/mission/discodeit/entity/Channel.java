package com.sprint.mission.discodeit.entity;

import java.util.Objects;

public class Channel extends BaseEntity {
    private String name;
    private String description;

    public Channel(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;

        setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(name, channel.name) && Objects.equals(description, channel.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    @Override
    public String toString() {
        return super.toString() + " Channel{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
