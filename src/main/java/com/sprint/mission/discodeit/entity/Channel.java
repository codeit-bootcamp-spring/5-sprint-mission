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

    private static final int NAME_MAX_LENGTH = 100;
    private static final int DESCRIPTION_MAX_LENGTH = 500;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ChannelType type;

    @Column(length = NAME_MAX_LENGTH)
    private String name;

    @Column(length = DESCRIPTION_MAX_LENGTH)
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
            validateName(name);
            validateDescription(description);
        }

        this.type = type;
        this.name = name;
        this.description = description;
    }

    public Channel update(String newName, String newDescription) {
        if (hasText(newName)) {
            validateName(newName);
            this.name = newName;
        }

        if (newDescription != null) {
            validateDescription(newDescription);
            this.description = newDescription;
        }
        return this;
    }

    private void validateName(String name) {
        if (name.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("name must not exceed " + NAME_MAX_LENGTH + " characters");
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("description must not exceed " + DESCRIPTION_MAX_LENGTH + " characters");
        }
    }

    @Override
    public String toString() {
        return "Channel[id=%s, type=%s, name=%s, description=%s]"
            .formatted(getId(), type, name, description);
    }
}
