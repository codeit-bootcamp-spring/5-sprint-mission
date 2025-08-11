package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository mr;

    public BasicMessageService(MessageRepository messageRepository) {
        this.mr = messageRepository;
    }
    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        Message message = new Message(userId, channelId,content);
        return mr.save(message);
    }

    @Override
    public Message readByIdMessage(UUID message) {
        return  mr.findById(message).orElse(null);
    }

    @Override
    public void readAllMessage() {
        List<Message> messageList = mr.findAll();
        long num = mr.count();
        if(num>0){
            System.out.println("현재 등록된 메시지는 "+num+"개 입니다.");
            for(Message message : messageList){
                System.out.println(message.toString());
            }
        }else{
            System.out.println("현재 등록된 메시지가 없습니다.");
        }
    }

    @Override
    public void updateMessage(UUID messageUUID, String content) {
        if(mr.existsById(messageUUID)){
            if(mr.update(messageUUID,content)){
                System.out.println("수정 성공하였습니다.");
            }else{
                System.out.println("수정 실패하였습니다.");
            }
        }else{
            System.out.println("메시지UUID가 존재하지 않습니다.");
        }
    }

    @Override
    public void deleteByIdMessage(UUID message) {
        if(mr.existsById(message)) {
            if (mr.delete(message)) {
                System.out.println("삭제 성공하였습니다.");
            } else {
                System.out.println("삭제 실패하였습니다.");
            }
        }else{
            System.out.println("메시지UUID가 존재하지 않습니다.");
        }
    }
}

