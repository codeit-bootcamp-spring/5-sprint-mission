package com.sprint.mission.discodeit.entity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 채널 타입
 **/
@Schema(description = "채널 상태")
public enum ChannelType {
    PUBLIC,
    PRIVATE
}
