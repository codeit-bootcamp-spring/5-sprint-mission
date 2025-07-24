package com.sprint.mission.discodeit.respository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    /**
     * 메시지를 저장합니다.
     *
     * @param message 저장할 메시지
     * @return 저장된 메시지
     */
    Message save(Message message);

    /**
     * ID로 메시지를 조회합니다.
     *
     * @param id 메시지 ID
     * @return 메시지, 없으면 null
     */
    Message findById(UUID id);

    /**
     * 저장된 모든 메시지를 반환합니다.
     *
     * @return 메시지 리스트
     */
    List<Message> findAll();

    /**
     * 문자열이 포함된 메시지를 검색합니다.
     *
     * @param str 포함된 문자열
     * @return 일치하는 메시지 리스트
     */
    List<Message> findByStr(String str);

    /**
     * 메시지를 삭제합니다.
     *
     * @param id 삭제할 메시지 ID
     * @return 삭제 성공 여부
     */
    boolean deleteById(UUID id);
}
