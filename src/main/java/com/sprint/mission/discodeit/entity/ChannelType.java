package com.sprint.mission.discodeit.entity;

/**
 * 채널 타입
 * <p>{@link #TEXT} - 일반 텍스트 메시지를 주고받는 채널</p>
 * <p>{@link #VOICE} - 음성 기반 채널</p>
 * <p>{@link #DM} - 1:1 다이렉트 메시지 채널</p>
 **/
public enum ChannelType {
    TEXT,
    VOICE,
    DM
}
