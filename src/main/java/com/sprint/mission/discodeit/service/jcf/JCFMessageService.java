package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;


public class JCFMessageService implements MessageService {
      private static JCFMessageService instance;
      private final Map<UUID, Message> data = new HashMap<>();
      private final UserService userService;
      private final ChannelService channelService;

      private JCFMessageService(UserService userService, ChannelService channelService) {
          this.userService = userService;
          this.channelService = channelService;
      }

      public static synchronized JCFMessageService getInstance() {
          if (instance == null) {
              UserService userServiceInstance = JCFUserService.getInstance();
              ChannelService channelServiceInstance = JCFChannelService.getInstance();
              instance = new JCFMessageService(userServiceInstance, channelServiceInstance);
          }
          return instance;
      }


      @Override
      public Message create(UUID channelId, UUID authorId, String content) {
          if (content == null || content.isBlank()) {
              throw new IllegalArgumentException("Message content cannot be null or blank.");
          }
          try {
              userService.find(authorId);
          } catch (NoSuchElementException e) {
              throw new IllegalArgumentException("User not found: " + authorId, e);
          }
          try {
              channelService.find(channelId);
          } catch (NoSuchElementException e) {
              throw new IllegalArgumentException("Channel not found: " + channelId, e);
          }
          Message message = new Message(channelId, authorId, content);
          data.put(message.getId(), message);
          return message;
      }

      @Override
      public Message find(UUID messageId) {
          Message message = data.get(messageId);
          if (message == null) {
              throw new NoSuchElementException("Message not found: " + messageId);
          }
          return message;
      }

      @Override
      public List<Message> findAll() {
          return data.values().stream().collect(Collectors.toList());
      }

      @Override
      public Message update(UUID messageId, String content) {
          Message message = find(messageId);
          message.update(content);
          return message;
      }

      @Override
      public void delete(UUID messageId) {
          if (!data.containsKey(messageId)) {
              throw new NoSuchElementException("Message not found: " + messageId);
          }
          data.remove(messageId);
      }

      @Override
      public void clear() {
          data.clear();
      }
}
