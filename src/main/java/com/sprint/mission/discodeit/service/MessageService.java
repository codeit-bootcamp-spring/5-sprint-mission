package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message; // Message 도메인 모델 import
import java.util.List; // List 인터페이스 import
import java.util.Optional; // Optional 클래스 import
import java.util.UUID; // UUID 클래스 import

//메시지(Message) 도메인 모델에 대한 CRUD(생성, 읽기, 수정, 삭제) 작업을 정의하는 인터페이스.
//심화 요구사항을 위해 User와 Channel 서비스에 대한 의존성을 가진다.
public interface MessageService {
    //새로운 Message를 생성하고 저장합니다.
    // 메시지 생성 시, 연관된 사용자(User)와 채널(Channel)의 존재 여부를 검증합니다.
    // @param message 생성할 Message 객체
    // @return 생성된 Message 객체 (유효하지 않은 User 또는 Channel ID인 경우 null)
    Message create(Message message);

    //주어진 ID에 해당하는 Message를 조회합니다.
    // @param messageId 조회할 Message의 UUID
    // @return 해당 ID의 Message 객체 (존재하지 않으면 Optional.empty())
    Optional<Message> findById(UUID messageId);

    //모든 Message 객체를 조회
    // @return 모든 Message 객체의 리스트
    List<Message> findAll();

    //주어진 ID에 해당하는 Message를 업데이트.
    //@param messageId 업데이트할 Message의 UUID
    //@param updatedMessage 업데이트할 내용을 담은 Message 객체 (messageId, createdAt, userId, channelId 제외)
    //@return 업데이트된 Message 객체 (존재하지 않으면 Optional.empty())
    Optional<Message> update(UUID messageId, Message updatedMessage);

    //주어진 ID에 해당하는 Message를 삭제.
    //@param messageId 삭제할 Message의 UUID
    // @return 삭제 성공 여부 (true: 성공, false: 실패)
    boolean delete(UUID messageId);
}
