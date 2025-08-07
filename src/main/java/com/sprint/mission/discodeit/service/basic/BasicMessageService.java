package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.AddMessageDto;
import com.sprint.mission.discodeit.dto.request.UpdateMessageDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
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

        message.getAttachmentIds().clear();
        for(UUID attachmentId : updateMessageDto.attachmentIds()){
            message.addAttachmentId(attachmentId);
        }
        return messageRepository.save(message).orElseThrow();
    }

    @Override
    public void deleteMessage(UUID messageId) {
        messageRepository.delete(messageId);
    }

    @Override
    public void deleteAllMessage() {
        messageRepository.deleteAll();
    }
}
