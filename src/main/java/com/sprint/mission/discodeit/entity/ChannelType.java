package com.sprint.mission.discodeit.entity;

import lombok.Getter;

//Enum 클래스
@Getter
public enum ChannelType {
    TEXT, // 일반 텍스트 채널
    VOICE, // 음성 채널
    PRIVATE_CHANNEL,
    PUBLIC_CHANNEL
}
