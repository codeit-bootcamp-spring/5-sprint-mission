// package com.sprint.mission.discodeit.service.file;
//
// import com.sprint.mission.discodeit.domain.entity.Message;
// import com.sprint.mission.discodeit.domain.entity.User;
// import com.sprint.mission.discodeit.service.MessageService;
// import com.sprint.mission.discodeit.service.UserService;
//
// import java.util.List;
// import java.util.UUID;
//
// public class FileMessageService extends BaseFileService<Message> implements MessageService {
//     private final UserService userService;
//
//     public FileMessageService(UserService userService) {
//         super(Message.class);
//         this.userService = userService;
//     }
//
//     @Override
//     public Message save(Message message) {
//         if (existsById(message.getId())) throw new IllegalArgumentException("중복된 ID가 존재합니다.");
//         UUID senderId = message.getSenderId();
//         User sender = userService.getOrThrow(senderId);
//         if (sender.isBanned() || sender.isDeactivated()) throw new IllegalStateException("비활성화되었거나 정지된 유저입니다.");
//         return super.save(message);
//     }
//
//     @Override
//     public void updateContent(UUID messageId, String content) {
//         update(messageId, m -> m.setContent(content));
//     }
//
//     @Override
//     public void updateFiles(UUID messageId, List<String> files) {
//         update(messageId, m -> m.setFiles(files));
//     }
//
//     @Override
//     public void printSenderAndContent(UUID messageId) {
//         Message message = getOrThrow(messageId);
//         User sender = userService.getOrThrow(message.getSenderId());
//         System.out.println(sender.getGlobalName() + ": " + message.getContent());
//     }
// }
