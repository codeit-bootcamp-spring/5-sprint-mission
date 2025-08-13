package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    /**
     * 채널 객체를 저장합니다.
     *
     * @param channel 저장할 채널 객체
     */
    void save(Channel channel);

    /**
     * ID(UUID)를 기준으로 채널을 조회합니다.
     *
     * @param id 조회할 채널의 UUID
     * @return 해당 ID의 채널 객체, 없으면 null
     */
    Optional<Channel> findById(UUID id);

    /**
     * 채널 이름으로 채널 목록을 조회합니다. (부분 일치 포함 가능)
     *
     * @param channelName 검색할 채널 이름
     * @return 이름이 일치하는 채널 리스트
     */
    List<Channel> findByName(String channelName);

    /**
     * 저장된 모든 채널 목록을 반환합니다.
     *
     * @return 전체 채널 리스트
     */
    List<Channel> findAll();

    /**
     * 주어진 ID를 가진 채널을 삭제합니다.
     *
     * @param id 삭제 대상 채널 ID
     * @return 삭제 성공 여부
     */
    boolean delete(UUID id);
}
