package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.AddMessageRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message addMessage(AddMessageRequest addMessageRequest) {
        Message message = new Message(addMessageRequest.messageContent(), addMessageRequest.userId(), addMessageRequest.channelId());

        if(addMessageRequest.attachmentIds() != null){
            for(UUID attachmentId : addMessageRequest.attachmentIds()){
                message.addAttachmentId(attachmentId);
            }
        }


        return messageRepository.save(message).orElseThrow();
    }

    @Override
    public Message getMessageById(UUID messageId) {
        return messageRepository.findById(messageId).orElseThrow();
    }

    @Override
    public List<Message> getAllMessage() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> getAllByChannelId(UUID channelId){
        return messageRepository.findAllByChannelId(channelId);
    }

    @Override
    public Message updateMessage(UUID messageId ,UpdateMessageRequest updateMessageRequest) {
        Message message = messageRepository.findById(messageId).orElseThrow();
        message.updateContent(updateMessageRequest.messageContent());

        List<UUID> attachmentIds = message.getAttachmentIds();
        message.removeAllAttachmentId();

        for(UUID attachmentId : attachmentIds){
            binaryContentRepository.deleteById(attachmentId);
        }

        if(updateMessageRequest.attachmentIds() != null){
            for(UUID attachmentId : updateMessageRequest.attachmentIds()){
                message.addAttachmentId(attachmentId);
            }
        }

        return messageRepository.save(message).orElseThrow();
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow();
        List<UUID> attachmentIds = message.getAttachmentIds();
        for(UUID attachmentId:attachmentIds) {
            binaryContentRepository.deleteById(attachmentId);
        }
        messageRepository.delete(messageId);
    }

    @Override
    public void deleteAllMessage() {
        for(Message message : messageRepository.findAll()){
            List<UUID> attachmentIds = message.getAttachmentIds();
            for(UUID attachmentId:attachmentIds) {
                binaryContentRepository.deleteById(attachmentId);
            }
        }
        messageRepository.deleteAll();
    }
}
