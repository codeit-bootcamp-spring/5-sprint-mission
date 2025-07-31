package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    /**
     * 주어진 사용자, 채널, 메시지 내용을 기반으로 메시지를 생성합니다.
     * @param user    메시지를 작성한 사용자
     * @param channel 메시지가 작성될 채널
     * @param message 메시지 본문
     * @return 생성된 메시지 객체
     */
    Message createMessage(User user, Channel channel, String message);

    /**
     * 특정 사용자가 작성한 모든 메시지를 조회합니다.
     * @param user 조회할 사용자
     * @return 해당 사용자의 메시지 목록
     */
    List<Message> getByUser(User user);

    /**
     * 메시지 내용으로 메시지를 검색합니다. (부분 일치 가능)
     * @param message 검색할 메시지 내용
     * @return 해당 내용을 포함한 메시지 목록
     */
    List<Message> getByMessage(String message);

    /**
     * 메시지를 수정합니다.
     * @param id 수정할 메시지의 ID
     * @param user 메시지를 수정하려는 사용자
     * @param channel 메시지가 속한 채널
     * @param originalMessage 원본 메시지 내용
     * @param updateMessage 수정할 메시지 내용
     * @return 수정 성공 여부
     */
    boolean updateById(UUID id, User user, Channel channel, String originalMessage, String updateMessage);

    /**
     * 메시지를 삭제합니다.
     * @param id 삭제할 메시지의 ID
     * @param user 메시지를 삭제하려는 사용자
     * @param channel 메시지가 속한 채널
     * @return 삭제 성공 여부
     */
    boolean removeById(UUID id, User user, Channel channel);
}
