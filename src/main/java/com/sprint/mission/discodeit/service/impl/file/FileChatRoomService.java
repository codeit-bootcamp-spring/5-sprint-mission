// package com.sprint.mission.discodeit.service.file;
//
// import com.sprint.mission.discodeit.domain.entity.ChatRoom;
// import com.sprint.mission.discodeit.domain.entity.User;
// import com.sprint.mission.discodeit.service.ChannelService;
// import com.sprint.mission.discodeit.service.ChatRoomService;
// import com.sprint.mission.discodeit.service.GuildService;
// import com.sprint.mission.discodeit.service.MessageService;
// import com.sprint.mission.discodeit.service.UserService;
//
// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;
//
// public class FileChatRoomService extends BaseFileService<ChatRoom> implements ChatRoomService {
//     private final UserService userService;
//     private final GuildService guildService;
//     private final ChannelService channelService;
//     private final MessageService messageService;
//
//     public FileChatRoomService(
//             UserService userService,
//             GuildService guildService,
//             ChannelService channelService,
//             MessageService messageService) {
//         super(ChatRoom.class);
//         this.userService = userService;
//         this.guildService = guildService;
//         this.channelService = channelService;
//         this.messageService = messageService;
//     }
//
//     @Override
//     public ChatRoom save(ChatRoom chatRoom) {
//         if (chatRoom.isChannelChatRoom()) {
//             channelService.getOrThrow(chatRoom.getChannel());
//             guildService.getOrThrow(chatRoom.getGuild());
//         } else if (existsByParticipants(chatRoom.participantsHashcode())) {
//             throw new IllegalStateException("대화방이 이미 존재합니다.");
//         }
//         return super.save(chatRoom);
//     }
//
//     @Override
//     public void addMessage(UUID chatRoomId, UUID messageId) {
//         messageService.getOrThrow(messageId);
//         update(chatRoomId, room -> room.addMessage(messageId));
//     }
//
//     @Override
//     public List<UUID> getMessages(UUID chatRoomId) {
//         return getOrThrow(chatRoomId).getMessages();
//     }
//
//     @Override
//     public void addParticipant(UUID chatRoomId, UUID userId) {
//         userService.getOrThrow(userId);
//         ChatRoom chatRoom = getOrThrow(chatRoomId);
//         if (chatRoom.isChannelChatRoom()) {
//             throw new UnsupportedOperationException("채널 기반 ChatRoom은 GuildService에서 멤버를 수정해야 합니다.");
//         }
//         update(chatRoomId, room -> room.addParticipant(userId));
//     }
//
//     @Override
//     public void removeParticipant(UUID chatRoomId, UUID userId) {
//         ChatRoom chatRoom = getOrThrow(chatRoomId);
//         if (chatRoom.isChannelChatRoom()) {
//             throw new UnsupportedOperationException("채널 기반 ChatRoom은 GuildService에서 멤버를 수정해야 합니다.");
//         }
//         update(chatRoomId, room -> room.removeParticipant(userId));
//     }
//
//     @Override
//     public void printMessages(UUID chatRoomId) {
//         getOrThrow(chatRoomId).getMessages().forEach(messageService::printSenderAndContent);
//     }
//
//     @Override
//     public List<String> getParticipantNames(UUID chatRoomId) {
//         return getOrThrow(chatRoomId).getParticipants().stream()
//                 .map(userService::findById)
//                 .flatMap(Optional::stream)
//                 .filter(User::isActive)
//                 .map(User::getGlobalName)
//                 .toList();
//     }
// }
