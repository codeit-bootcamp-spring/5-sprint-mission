package com.sprint.mission.discodeit.service;

//인터페이스
//기능의 약속을 정의하며 다중 구현이 가능
//CRUD(생성,읽기,모두읽기,수정,삭제)의 기능 구현하기

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  Message create(MessageCreateRequest request, List<MultipartFile> attachments);

  Message findById(UUID messageId); //하나만 찾기

  List<Message> findAllByChannelId(UUID channelId);

  Message update(UUID massageId, MessageUpdateRequest request); // 수정

  void delete(UUID message); //삭제

}
