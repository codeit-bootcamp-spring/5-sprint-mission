package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static org.springframework.util.StringUtils.hasText;

@Entity
@Table(name = "channels")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseUpdatableEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ChannelType type;

    @Column(length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    public Channel(
        ChannelType type,
        String name,
        String description
    ) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (type == ChannelType.PUBLIC) {
            if (!hasText(name)) {
                throw new IllegalArgumentException("name must not be blank for PUBLIC channel");
            }
            if (name.length() > 100) {
                throw new IllegalArgumentException("name must not exceed 100 characters");
            }
            if (description != null && description.length() > 500) {
                throw new IllegalArgumentException("description must not exceed 500 characters");
            }
        }

        this.type = type;
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Channel[id=%s, type=%s, name=%s, description=%s]"
            .formatted(getId(), type, name, description);
    }

    public void update(
        String newName,
        String newDescription
    ) {
        if (hasText(newName)) {
            if (newName.length() > 100) {
                throw new IllegalArgumentException("name must not exceed 100 characters");
            }
            this.name = newName;
        }

        if (newDescription != null) {
            if (newDescription.length() > 500) {
                throw new IllegalArgumentException("description must not exceed 500 characters");
            }
            this.description = newDescription;
        }
    }
}
