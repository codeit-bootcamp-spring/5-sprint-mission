package com.sprint.mission.discodeit.domain.entity;

public enum AuthAuditEventType {
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    LOGOUT,
    TOKEN_REFRESH,
    TOKEN_REFRESH_FAILURE,
    ROLE_UPDATED
}
