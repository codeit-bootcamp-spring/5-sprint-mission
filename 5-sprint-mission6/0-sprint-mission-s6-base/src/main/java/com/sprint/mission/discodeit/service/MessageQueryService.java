package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MessageQueryService {

  private static final int PAGE_SIZE = 50;

  private final MessageRepository messageRepository;
  private final MessageMapper messageMapper;

  @Transactional(readOnly = true)
  public Slice<MessageDto> findRecentByChannel(UUID channelId, int page) {
    Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
    return messageRepository
        .findByChannel_IdOrderByCreatedAtDesc(channelId, pageable) // count 없이 +1 조회
        .map(messageMapper::toDto);
  }
}