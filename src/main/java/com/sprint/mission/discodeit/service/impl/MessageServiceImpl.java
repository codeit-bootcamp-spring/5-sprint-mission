package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {


  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  /* 메세지 생성
   * 예외 발생시 rollback하기 위해 (예: 첨부파일 저장 실패)
   * Transactional 걸어놓음
   * */
  @Override
  @Transactional
  public Message create(MessageCreateRequest request, List<MultipartFile> attachments) {
    // 1. 작성자 찾기
    User author = userRepository.findById(request.getAuthorId())
        .orElseThrow(() -> new IllegalArgumentException("작성자 없음"));

    //2. 채널 찾기
    Channel channel = channelRepository.findById(request.getChannelId())
        .orElseThrow(() -> new IllegalArgumentException("채널 정보 없음"));

    //3. 메세지 객체 생성
    Message message = new Message(request.getContent(), author, channel);

    //4. 첨부파일 변환 (Miltifile->BinaryContent) 및 연결
    List<BinaryContent> attachmentEntities = new ArrayList<>();

    if (attachments != null && !attachments.isEmpty()) {
      for (MultipartFile file : attachments) {
        try {
          //BinaryCotent 엔티티로 변환
          BinaryContent content = new BinaryContent(
              file.getOriginalFilename(),
              file.getContentType(),
              file.getSize(),
              file.getBytes()
          );
          content.setMessage(message); // 이 파일은 이 메세지에 속해있다
          attachmentEntities.add(content); // 저장 목록 추가
        } catch (Exception e) {
          throw new RuntimeException("첨부파일 처리 실패: " + file.getOriginalFilename(), e);
        }
      }
    }

    //6. 저장
    return messageRepository.save(message);

  }

  //메세지 단건 조회
  @Override
  @Transactional
  public Message findById(UUID id) {
    return messageRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 메세지를 찾을 수 없습니다."));
  }

  //특정 채널에 속한 모든 메세지 불러오기
  @Override
  @Transactional
  public List<Message> findAllByChannelId(UUID channelId) {
    // 메시지 테이블에 channel_id FK로 조회
    return messageRepository.findAll().stream()
        .filter(msg -> msg.getChannel() != null && channelId.equals(msg.getChannel().getId()))
        .toList();
  }

  /* 메세지 수정
   *  예외 발생시 전체 rollback 하기 위해
   * Transactional 걸어놓음
   * */
  @Override
  @Transactional
  public Message update(UUID id, MessageUpdateRequest request) {
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 메세지 없음"));

    if (request.getNewContent() != null) {
      message.updateContent(request.getNewContent());
    }
    return message; // 트랜잭션 끝나면 자동 update
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 메세지 없음"));
    messageRepository.delete(message);

  }
}
