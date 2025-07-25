package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.ChatRoom;
import com.sprint.mission.discodeit.entity.Guild;
import com.sprint.mission.discodeit.service.ChatRoomService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.UUID;

public class JcfChatRoomService extends BaseJcfService<ChatRoom> implements ChatRoomService {
  private static final JcfChatRoomService instance = new JcfChatRoomService();

  private UserService userService;
  private JcfGuildService guildService;
  private JcfChannelService channelService;
  private JcfMessageService messageService;

  private JcfChatRoomService() {}

  public static JcfChatRoomService getInstance() {
    return instance;
  }

  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public void setGuildService(JcfGuildService guildService) {
    this.guildService = guildService;
  }

  public void setChannelService(JcfChannelService channelService) {
    this.channelService = channelService;
  }

  public void setMessageService(JcfMessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  public ChatRoom save(ChatRoom chatRoom) {
    if (chatRoom.isChannelChatRoom()) {
      channelService.getOrThrow(chatRoom.getChannelId());
      guildService.getOrThrow(chatRoom.getGuildId());
    }
    return super.save(chatRoom);
  }

  @Override
  public void addMessage(UUID chatRoomId, UUID messageId) {
    messageService.getOrThrow(messageId);
    update(chatRoomId, room -> room.addMessage(messageId));
  }

  @Override
  public List<UUID> getMessages(UUID id) {
    return getOrThrow(id).getMessages();
  }

  @Override
  public void addParticipant(UUID chatRoomId, UUID userId) {
    userService.getOrThrow(userId);
    ChatRoom chatRoom = getOrThrow(chatRoomId);
    if (chatRoom.isChannelChatRoom()) {
      throw new UnsupportedOperationException("채널 기반 ChatRoom은 GuildService에서 멤버를 수정해야 합니다.");
    }
    update(chatRoomId, room -> room.addParticipant(userId));
  }

  @Override
  public void removeParticipant(UUID chatRoomId, UUID userId) {
    ChatRoom chatRoom = getOrThrow(chatRoomId);
    if (chatRoom.isChannelChatRoom()) {
      throw new UnsupportedOperationException("채널 기반 ChatRoom은 GuildService에서 멤버를 수정해야 합니다.");
    }
    update(chatRoomId, room -> room.removeParticipant(userId));
  }

  @Override
  public boolean isParticipant(UUID chatRoomId, UUID userId) {
    ChatRoom chatRoom = getOrThrow(chatRoomId);
    if (chatRoom.isChannelChatRoom()) {
      Guild guild = guildService.getOrThrow(chatRoom.getGuildId());
      return guild.getMembers().containsKey(userId);
    }
    return chatRoom.isParticipant(userId);
  }
}
