package com.sprint.mission.discodeit.service;

//인터페이스
//기능의 약속을 정의하며 다중 구현이 가능
//CRUD(생성,읽기,모두읽기,수정,삭제)의 기능 구현하기

import com.sprint.mission.discodeit.dto.MessageDto;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  //메세지 생성: content,channelId, authorId
  MessageDto create(MessageDto dto, List<MultipartFile> attachments);

  //하나만 찾기
  MessageDto findById(UUID id);

  //특정 채널에 속한 모든 메세지 기록 가져옴
  List<MessageDto> findAllByChannelId(UUID id);

  //기존 메세지 수정
  MessageDto update(UUID id, MessageDto dto); // 수정

  //메세지 삭제
  void delete(UUID id); //삭제

}