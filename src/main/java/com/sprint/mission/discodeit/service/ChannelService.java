package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel; // Channel 도메인 모델 import
import java.util.List; // List 인터페이스 import
import java.util.Optional; // Optional 클래스 import
import java.util.UUID; // UUID 클래스 import

//채널(Channel) 도메인 모델에 대한 CRUD(생성, 읽기, 수정, 삭제) 작업을 정의하는 인터페이스
public interface ChannelService {

    //새로운 Channel을 생성하고 저장합니다.
    //@param channel 생성할 Channel 객체
    //@return 생성된 Channel 객체
    Channel create(Channel channel);

    //주어진 ID에 해당하는 Channel을 조회합니다.
    //@param channelId 조회할 Channel의 UUID
    //@return 해당 ID의 Channel 객체 (존재하지 않으면 Optional.empty())
    Optional<Channel> findById(UUID channelId);

    //모든 Channel 객체를 조회.
    //@return 모든 Channel 객체의 리스트
    List<Channel> findAll();

    //주어진 ID에 해당하는 Channel을 업데이트.
    //@param channelId 업데이트할 Channel의 UUID
    //@param updatedChannel 업데이트할 내용을 담은 Channel 객체 (id, createdAt 제외)
    //@return 업데이트된 Channel 객체 (존재하지 않으면 Optional.empty()
    Optional<Channel> update(UUID channelId, Channel updatedChannel);

    //주어진 ID에 해당하는 Channel을 삭제.
    //@param channelId 삭제할 Channel의 UUID
    //@return 삭제 성공 여부 (true: 성공, false: 실패)
    boolean delete(UUID channelId);
}
