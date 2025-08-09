package com.sprint.mission.discodeit.domain.enums.user;

import lombok.Getter;

@Getter
public enum Status {
    ONLINE("온라인"),
    DO_NOT_DISTURB("방해 금지"),
    IDLE("자리 비움"),
    OFFLINE("오프라인 표시");

    private final String description;

    Status(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
