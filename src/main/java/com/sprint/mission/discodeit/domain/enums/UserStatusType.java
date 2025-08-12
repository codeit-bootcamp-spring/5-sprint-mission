package com.sprint.mission.discodeit.domain.enums;

import lombok.Getter;

@Getter
public enum UserStatusType {
    ONLINE("온라인"),
    DO_NOT_DISTURB("방해 금지"),
    IDLE("자리 비움"),
    OFFLINE("오프라인 표시");

    private final String displayName;

    UserStatusType(String displayName) {
        this.displayName = displayName;
    }

    public static UserStatusType fromCodeIgnoreCase(String name) {
        for (UserStatusType t : values()) {
            if (t.name().equalsIgnoreCase(name)) return t;
        }
        throw new IllegalArgumentException("Unknown UserStatusType: " + name);
    }
}
