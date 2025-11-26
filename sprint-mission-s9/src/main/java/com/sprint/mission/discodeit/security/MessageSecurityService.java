package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSecurityService {

  private final MessageRepository messageRepository;

  public boolean isMessageAuthor(UUID messageId, Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }

    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
    UUID currentUserId = userDetails.getUserDto().id();

    return messageRepository.findById(messageId)
        .map(Message::getAuthor)
        .map(author -> author.getId().equals(currentUserId))
        .orElse(false);
  }
}
