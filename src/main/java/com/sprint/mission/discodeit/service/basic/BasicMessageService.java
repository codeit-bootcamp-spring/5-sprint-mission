package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("basicMessageService")
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    @Qualifier("fileMessageRepository")
    private final MessageRepository messageRepository;


    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        Message message = new Message(userId, channelId,content);
        return messageRepository.save(message);
    }

    @Override
    public Message readByIdMessage(UUID message) {
        return  messageRepository.findById(message).orElse(null);
    }

    @Override
    public void readAllMessage() {
        List<Message> messageList = messageRepository.findAll();
        long num = messageRepository.count();
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
        if(messageRepository.existsById(messageUUID)){
            if(messageRepository.update(messageUUID,content)){
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
        if(messageRepository.existsById(message)) {
            if (messageRepository.delete(message)) {
                System.out.println("삭제 성공하였습니다.");
            } else {
                System.out.println("삭제 실패하였습니다.");
            }
        }else{
            System.out.println("메시지UUID가 존재하지 않습니다.");
        }
    }
}

