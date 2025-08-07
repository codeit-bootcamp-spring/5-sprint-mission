package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.AddMessageDto;
import com.sprint.mission.discodeit.dto.request.UpdateMessageDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message addMessage(AddMessageDto addMessageDto) {
        Message message = new Message(addMessageDto.messageContent(), addMessageDto.userId(), addMessageDto.channelId());

        for(UUID attachmentId : addMessageDto.attachmentIds()){
            message.addAttachmentId(attachmentId);
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
    public Message updateMessage(UpdateMessageDto updateMessageDto) {
        Message message = messageRepository.findById(updateMessageDto.messageId()).orElseThrow();
        message.updateContent(updateMessageDto.messageContent());

        List<UUID> attachmentIds = message.getAttachmentIds();
        message.removeAllAttachmentId();

        for(UUID attachmentId : attachmentIds){
            binaryContentRepository.deleteById(attachmentId);
        }

        for(UUID attachmentId : updateMessageDto.attachmentIds()){
            message.addAttachmentId(attachmentId);
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
