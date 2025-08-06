package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.util.Objects;

public class Channel extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;
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
        if (name != null && !this.name.equals(name) && description != null && !this.description.equals(description)) {
            this.name = name;
            this.description = description;
            this.setUpdatedAt(System.currentTimeMillis());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Channel channel = (Channel) o;
        return Objects.equals(getId(), channel.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return super.toString() + " Channel{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
