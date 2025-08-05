package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {

    /**
     * 메시지를 저장합니다.
     * @param message 저장할 메시지 객체
     */
    void save(Message message);

    /**
     * 특정 사용자가 작성한 메시지를 조회합니다.
     * @param user 조회할 사용자
     * @return 해당 사용자의 메시지 목록
     */
    List<Message> findByUser(User user);

    /**
     * 메시지 내용으로 메시지를 검색합니다. (부분 일치 가능)
     * @param message 검색할 메시지 내용
     * @return 해당 내용을 포함한 메시지 목록
     */
    List<Message> findByMessage(String message);

    /**
     * 모든 메시지를 조회합니다.
     * @return 메시지 목록
     */
    List<Message> findAll();

    /**
     * 메시지를 수정합니다.
     * @param id 수정할 메세지의 ID
     * @param message 수정할 메시지 객체
     */
    void update(UUID id, Message message);

    /**
     * 메시지를 삭제합니다.
     * @param id 삭제할 메시지의 ID
     * @return 삭제 성공 여부
     */
    boolean delete(UUID id);
}
