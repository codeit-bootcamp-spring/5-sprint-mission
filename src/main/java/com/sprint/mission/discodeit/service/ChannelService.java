package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

    /**
     * 새로운 채널을 생성
     *
     * @param name  채널 이름
     * @param topic 채널 주제
     * @return 생성된 Channel 객체
     */
    Channel create(String name, String topic);

    /**
     * 채널 검색
     * @param id 채널아이디
     * @return 찾은 채널 객체 리스트
     * */
    Optional<Channel> findById(UUID id);

    /**
     * 채널 검색
     * @param name 채널이름
     * @return 찾은 채널 객체 리스트
     * */
    List<Channel> findByName(String name);

    /**
     * 모든 채널 조회
     * @return 모든 채널 리스트
     * */
    List<Channel> findAll();

    /**
     * 채널명 업데이트
     * @param id 채널아이디
     * @param name 새로운 채널 이름
     * @return 변경된 채널 객체
     * */
    Channel updateName(UUID id, String name);

    /**
     * 채널주제 업데이트
     * @param id 채널아이디
     * @param topic 새로운 채널 주제
     * @return 변경된 채널 객체
     * */
    Channel updateTopic(UUID id, String topic);

    /**
     * 채널 삭제
     * @param id 채널아이디
     * */
    void deleteById(UUID id);
}
