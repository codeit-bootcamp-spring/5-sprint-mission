package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;


public class JCFChannelService implements ChannelService {
      private static JCFChannelService instance;
      private final Map<UUID, Channel> data = new HashMap<>();

      private JCFChannelService() {
      }

      public static synchronized JCFChannelService getInstance() {
          if (instance == null) {
              instance = new JCFChannelService();
          }
          return instance;
      }


      @Override
      public Channel create(String channelName, String description) {
          if (channelName == null || channelName.isBlank()) {
              throw new IllegalArgumentException("Channel name cannot be null or blank.");
          }
          // 채널 이름 중복 검증
          if (data.values().stream().anyMatch(channel -> channel.getChannelName().equals(channelName))) {
              throw new IllegalArgumentException("Channel with name '" + channelName + "' already exists.");
          }

          Channel channel = new Channel(channelName, description);
          data.put(channel.getId(), channel);
          return channel;
      }

      @Override
      public Channel find(UUID channelId) {
          Channel channel = data.get(channelId);
          if (channel == null) {
              throw new NoSuchElementException("Channel not found: " + channelId);
          }
          return channel;
      }

      @Override
      public List<Channel> findAll() {
          return data.values().stream().collect(Collectors.toList());
      }

      @Override
      public Channel update(UUID channelId, String channelName, String description) {
          Channel channel = find(channelId); // find method now throws NoSuchElementException
          channel.update(channelName, description);
          return channel;
      }

      @Override
      public void delete(UUID channelId) {
          if (!data.containsKey(channelId)) {
              throw new NoSuchElementException("Channel not found: " + channelId);
          }
          data.remove(channelId);
      }

      @Override
      public void clear() {
          data.clear();
      }
}
