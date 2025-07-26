package com.sprint.mission.discodeit.service;

//인터페이스
//기능의 약속을 정의하며, 다중 구현이 가능
//CRUD(생성,읽기,모두읽기,수정,삭제) 기능 구현하기

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    //약속
    void create(Channel channel); //저장
    Channel findById(UUID id); //UUID로 채널 찾기
    List<Channel> findAll(); //모든 채널 리스트로 반환
    void update(Channel channel); // 채널 수정
    void delete(UUID id); //UUID로 채널 삭제
}
