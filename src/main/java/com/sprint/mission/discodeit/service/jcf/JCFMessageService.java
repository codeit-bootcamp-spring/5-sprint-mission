//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.respository.MessageRepository;
//import com.sprint.mission.discodeit.respository.jcf.JCFMessageRepository;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//import java.util.*;
//
//public class JCFMessageService implements MessageService {
//
//    private final MessageRepository messageRepository =  new JCFMessageRepository();
//    private final UserService userService;
//    private final ChannelService channelService;
//
//    public JCFMessageService(UserService userService, ChannelService channelService) {
//        this.userService = userService;
//        this.channelService = channelService;
//    }
//
//
//    // 메시지 조회
//    public List<Message> findAll(){
//        return messageRepository.findAll();
//    }
//
//    // 메시지 생성
//    public Message create(User user, Channel channel, String content) {
//        if (userService.findById(user.getId()).isEmpty()) {
//            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
//        }
//
//        if (channelService.findById(channel.getId()).isEmpty()) {
//            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
//        }
//
//        Message message = new Message(user, channel, content);
//        return messageRepository.save(message);
//    }
//
//    // 메시지 문자열로 찾기
//    public List<Message> findByStr(String message) {
//        return messageRepository.findByStr(message);
//    }
//
//    // 메시지 수정
//    public Message update(UUID id, String message) {
//        Message oldMessage = messageRepository.findById(id);
//        if (oldMessage == null) {
//            throw new NoSuchElementException("메시지를 찾을 수 없습니다.");
//        }
//        oldMessage.updateContent(message);
//        return messageRepository.save(oldMessage);
//    }
//
//    // 메시지 삭제
//    public boolean deleteById(UUID id){
//        messageRepository.deleteById(id);
//        return true;
//    }
//
//}
