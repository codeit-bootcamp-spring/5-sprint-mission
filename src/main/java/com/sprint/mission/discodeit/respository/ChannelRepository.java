package com.sprint.mission.discodeit.respository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    /**
     * 채널을 저장하거나 업데이트
     *
     * @param channel 저장할 Channel 객체
     * @return 저장된 Channel 객체
     */
    Channel save(Channel channel);

    /**
     * 주어진 ID에 해당하는 채널을 조회
     *
     * @param id 조회할 채널의 UUID
     * @return 해당 ID의 Channel 객체, 없으면 null 반환
     */
    Channel findById(UUID id);

    /**
     * 주어진 이름과 일치하는 채널 목록을 조회
     *
     * @param name 조회할 채널 이름
     * @return 이름이 일치하는 Channel 객체 리스트
     */
    List<Channel> findByName(String name);

    /**
     * 모든 채널을 조회
     *
     * @return 전체 Channel 객체 리스트
     */
    List<Channel> findAll();

    /**
     * 주어진 ID에 해당하는 채널의 이름을 수정
     *
     * @param id 수정할 채널의 UUID
     * @param name 새 채널 이름
     * @return 수정된 Channel 객체, 없으면 null 반환
     */
    Channel updateName(UUID id, String name);

    /**
     * 주어진 ID에 해당하는 채널의 주제를 수정
     *
     * @param id 수정할 채널의 UUID
     * @param topic 새 채널 주제
     * @return 수정된 Channel 객체, 없으면 null 반환
     */
    Channel updateTopic(UUID id, String topic);

    /**
     * 채널 ID에 해당하는 채널을 삭제
     *
     * @param id 삭제할 채널의 UUID
     * @return 삭제에 성공하면 true, 해당 ID가 존재하지 않아 삭제되지 않으면 false
     */
    boolean delete(UUID id);
}
