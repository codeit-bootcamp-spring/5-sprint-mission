package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private FileMessageRepository fmr;
    public FileMessageService() {
        this.fmr = new FileMessageRepository();
    }
    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        //main에서 userId, channelId 일치 확인하고 시작
        Message message = new Message(userId, channelId, content);
        return fmr.save(message);
    }

    @Override
    public Message readByIdMessage(UUID message) {
        if(fmr.existsById(message)){
            return fmr.findById(message).orElse(null);
        }
        System.out.println("메시지 UUID가 존재하지 않습니다.");
        return null;
    }

    @Override
    public void readAllMessage() {
        long num = fmr.count();
        if(num>0) {
            System.out.println("현재 등록된 메세지는 "+num+"개 입니다.");
            List<Message> userList = fmr.findAll();
            for (Message message : userList) {
                System.out.println(message.toString());
            }
        }else{
            System.out.println("현재 등록된 메세지가 없습니다.");
        }

    }

    @Override
    public void updateMessage(UUID messageUUID, String content) {
        if(fmr.existsById(messageUUID)){
            if(fmr.update(messageUUID,content)){
                System.out.println("메세지 수정을 성공하였습니다.");
            }else{
                System.out.println("메세지 수정을 실패하였습니다.");
            }
        }

    }

    @Override
    public void deleteByIdMessage(UUID message) {
        if(fmr.existsById(message)) {
            if(fmr.delete(message)){
                System.out.println("메세지 삭제 성공!");
            }else{
                System.out.println("메세지 삭제 실패");
            }
        }else{
            System.out.println("메세지UUID가 존재하지 않습니다.");
        }
    }
}
