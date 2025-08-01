package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message create(MessageDto.Create dto);

    List<Message> findAll();


    /**
     * 문자가 포함된 메시지 검색
     *
     * @param str 찾고 싶은 문자
     * @return 문자가 포함된 Message 객체 리스트
     */
    List<Message> findByStr(String str);

    /**
     * 메시지 수정
     *
     * @param id  User 객체
     * @param message Channel 객체
     * @return 업데이트된 Message 객체
     */
    Message update(UUID id, String message);

    /**
     * 메시지 삭제
     * @param id 메시지 아이디
     * @return 삭제 성공 여부 (true: 삭제됨, false: 존재하지 않음)
     * */
    boolean deleteById(UUID id);
}
