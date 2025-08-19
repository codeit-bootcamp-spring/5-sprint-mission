package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    /**
     * 새로운 메시지를 생성합니다.
     *
     * @param request 메시지 생성 요청
     * @return 생성된 메시지 응답 객체
     */
    MessageResponse createMessage(CreateMessageRequest request);

    /**
     * 특정 채널에 속한 모든 메시지를 조회합니다.
     *
     * @param channelId 채널 ID
     * @return 해당 채널의 메시지 목록
     */
    List<MessageResponse> getAllByChannelId(UUID channelId);

    /**
     * 특정 사용자가 작성한 모든 메시지를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 해당 사용자가 작성한 메시지 목록
     */
    List<MessageResponse> getAllByUserId(UUID userId);

    /**
     * 메시지 내용으로 메시지를 검색합니다. (부분 일치 가능)
     * @param content 검색할 메시지 내용
     * @return 해당 내용을 포함한 메시지 목록
     */
    List<MessageResponse> getAllByMessage(String content);

    /**
     * 메시지를 수정합니다.
     *
     * @param request 메시지 수정 요청
     * @return 수정된 메시지 응답 객체
     */
    MessageResponse update(UpdateMessageRequest request);

    /**
     * 메시지를 삭제합니다.
     *
     * @param messageId 삭제할 메시지의 ID
     * @return 삭제 성공 여부
     */
    boolean remove(UUID messageId);
}
