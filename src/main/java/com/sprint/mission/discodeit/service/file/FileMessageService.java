//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.*;
//import com.sprint.mission.discodeit.respository.MessageRepository;
//import com.sprint.mission.discodeit.respository.file.FileMessageRepository;
//import com.sprint.mission.discodeit.service.MessageService;
//import java.util.*;
//
//public class FileMessageService implements MessageService {
//
//    private final MessageRepository messageRepository = new FileMessageRepository();
//
//    @Override
//    public Message create(User user, Channel channel, String content) {
//        Message message = new Message(user, channel, content);
//        return messageRepository.save(message);
//    }
//
//    @Override
//    public List<Message> findAll() {
//        return messageRepository.findAll();
//    }
//
//    @Override
//    public List<Message> findByStr(String str) {
//        List<Message> result = new ArrayList<>();
//        for (Message message : messageRepository.findAll()) {
//            if (message.getContent().contains(str)) {
//                result.add(message);
//            }
//        }
//        return result;
//    }
//
//    @Override
//    public Message update(UUID id, String newMessage) {
//        Message message = messageRepository.findById(id);
//        if (message != null) {
//            message.updateContent(newMessage);
//            messageRepository.save(message);
//        }
//        return message;
//    }
//
//    @Override
//    public boolean deleteById(UUID id) {
//        return messageRepository.deleteById(id);
//    }
//
//}
