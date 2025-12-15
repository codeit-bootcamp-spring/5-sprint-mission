package com.sprint.mission.discodeit.entity;


import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder(toBuilder = true)
public class Notification extends BaseEntity {
    String receiverId;
    String title;
    String content;


    public Notification(User user, String title, String content) {
        this.receiverId = user.getId().toString();
        this.title = title;
        this.content = content;
    }

    public Notification(UUID userId, String title, String content) {
        this.receiverId = userId.toString();
        this.title = title;
        this.content = content;
    }

    public Notification(String userId, String title, String content) {
        this.receiverId = userId;
        this.title = title;
        this.content = content;
    }

}
