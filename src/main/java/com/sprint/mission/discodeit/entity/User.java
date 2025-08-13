package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class User extends BaseEntity {
    private UUID profileId;
    private String email;
    private String userName;
    private String nickname;
    private String password;
    private String phoneNumber;

    public User(UUID profileId, String email, String userName, String nickname, String password, String phoneNumber) {
        this(UUID.randomUUID(), profileId, Instant.now(), email, userName, nickname, password, phoneNumber);
    }

    public User(UUID id, UUID profileId, Instant createAt, String email, String userName, String nickname, String password, String phoneNumber) {
        super(id, createAt);
        this.profileId = profileId;
        this.email = email;
        this.userName = userName;
        this.nickname = nickname;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public void update(UpdateUserRequest request) {
        if (request.email() != null) this.email = request.email();
        if (request.userName() != null) this.userName = request.userName();
        if (request.nickname() != null) this.nickname = request.nickname();
        if (request.password() != null) this.password = request.password();
        if (request.phoneNumber() != null) this.phoneNumber = request.phoneNumber();
        updateTimeStamp();
    }

    public void changeProfileId(UUID profileId) {
        this.profileId = profileId;
        updateTimeStamp();
    }
}
