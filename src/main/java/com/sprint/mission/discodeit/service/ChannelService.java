package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    /**
     * 새로운 채널을 생성합니다.
     * @param channelName 생성할 채널의 이름
     * @param channelDescription 생성할 채널의 설명
     * @return 생성된 채널 객체
     */
    Channel createChannel(String channelName, String channelDescription);

    /**
     * 채널 ID로 채널을 조회합니다.
     * @param channelId 조회할 채널 UUID
     * @return 조회된 채널, 없으면 null
     */
    Channel getById(UUID channelId);

    /**
     * 채널명으로 채널을 조회합니다
     * @param channelName 검색할 채널명
     * @return 검색된 채널들
     */
    List<Channel> getByChannelName(String channelName);

    /**
     * 모든 채널을 조회합니다.
     * @return 전체 채널 목록
     */
    List<Channel> getAll();

    /**
     * 채널을 수정합니다.
     * @param channelId 수정할 채널의 ID
     * @param channelName 수정할 채널명
     * @param channelDescription 수정할 채널 설명
     * @return 수정 성공 여부
     */
    Channel updateById(UUID channelId, String channelName, String channelDescription);

    /**
     * 채널을 삭제합니다.
     * @param channelId 삭제할 채널의 ID
     * @return 삭제 성공 여부
     */
    boolean removeById(UUID channelId);
}
