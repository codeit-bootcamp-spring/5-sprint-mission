package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {


  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final MessageMapper messageMapper;

  /* Mapper: 엔티티와 DTO 변환로직 메서드 담당
   * ServiceImpl: 메서드를 호출하여 비즈니스 로직 처리
   * */


  /* 메세지 생성
   * 예외 발생시 rollback하기 위해 (예: 첨부파일 저장 실패)
   * Transactional 걸어놓음
   * */
  @Override
  @Transactional
  public MessageDto create(MessageDto dto, List<MultipartFile> attachments) { // ✅ 반환 타입 변경
    // 1. 작성자 찾기
    User author = userRepository.findById(dto.getAuthorId())
        .orElseThrow(() -> new IllegalArgumentException("작성자 없음"));

    //2. 채널 찾기
    Channel channel = channelRepository.findById(dto.getChannelId())
        .orElseThrow(() -> new IllegalArgumentException("채널 정보 없음"));

    //3. 메세지 객체 생성
    Message message = messageMapper.toEntityForCreate(dto, author, channel);

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
          log.info("첨부파일 처리 완료: filename={}", file.getOriginalFilename());
        } catch (Exception e) {
          log.error("첨부파일 처리 실패: filename={}", file.getOriginalFilename(), e);
          throw new RuntimeException("첨부파일 처리 실패: " + file.getOriginalFilename(), e);
        }
      }
    }

    //6. 저장 후 DTO로 변환하여 반환
    Message savedMessage = messageRepository.save(message);
    return messageMapper.toDto(savedMessage);
  }

  //메세지 단건 조회
  @Override
  @Transactional
  public MessageDto findById(UUID id) {
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 메세지를 찾을 수 없습니다."));
    log.info("메시지 단건조회 성공: id={}", id);
    return messageMapper.toDto(message);
  }

  //특정 채널에 속한 모든 메세지 불러오기
  @Override
  @Transactional
  public List<MessageDto> findAllByChannelId(UUID channelId) {
    log.info("채널 내 모든 메시지 조회 시도: channelId={}", channelId);
    List<MessageDto> dtos = messageRepository.findAll().stream()
        .filter(msg -> msg.getChannel() != null && channelId.equals(msg.getChannel().getId()))
        .map(messageMapper::toDto)
        .toList();
    log.info("채널 내 모든 메시지 조회 완료: 반환 개수={}", dtos.size());
    return dtos;
  }

  /* 메세지 수정
   * 예외 발생시 전체 rollback 하기 위해
   * Transactional 걸어놓음
   * */
  @Override
  @Transactional
  public MessageDto update(UUID id, MessageDto dto) {
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 메세지 없음"));
    messageMapper.updateEntityFromDto(message, dto);
    log.info("메시지 수정 완료: id={}", id);
    return messageMapper.toDto(message);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 메세지 없음"));
    messageRepository.delete(message);
    log.info("메시지 삭제 완료: id={}", id);
  }
}