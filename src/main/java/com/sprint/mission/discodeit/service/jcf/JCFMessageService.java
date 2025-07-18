package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    List<Message> data = new ArrayList<>();

    public JCFMessageService() {}

    @Override
    public Message createMessage(Channel channel, String message, User author, boolean allMentioned) throws NullPointerException, IllegalArgumentException {
        if (channel == null) {
            throw new NullPointerException("channel id is null.");
        } if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message is null or empty.");
        } if (author == null) {
            throw new NullPointerException("author is null.");
        }
        List<User> members = channel.getMembers();
        if (! members.contains(author)) {
            throw new IllegalArgumentException("author is not in the channel.");
        }

        Message msg = new Message(channel.getId(), message, author, allMentioned);
        data.add(msg);

        return msg;
    }

    @Override
    public Message findById(UUID messageId) throws NullPointerException, IllegalArgumentException {
        if(messageId == null) {
            throw new NullPointerException("message id is null.");
        }
        for(Message msg : data) {
            if (msg.getId().equals(messageId)) {
                return msg;
            }
        }

        throw new IllegalArgumentException("message id not found.");
    }

    @Override
    public List<Message> findAll() {
        return data;
    }

    @Override
    public Message update(MessageDTO messageDTO) {
        if(messageDTO == null) {
            throw new  NullPointerException("messageDTO is null.");
        } if (messageDTO.getMessage() == null || messageDTO.getMessage().isBlank()) {
            throw new IllegalArgumentException("messageDTO message is null or blank.");
        }
        Iterator<Message> iter =  data.iterator();
        while(iter.hasNext()) {
            Message msg = iter.next();
            if (msg.getId().equals(messageDTO.getId())) {
                msg.update(messageDTO);
                return msg;
            }
        }
        throw new IllegalArgumentException("message not found.");
    }

    @Override
    public MessageDTO createMessageDTO(UUID messageId, UUID channelId, String message, User author, boolean allMentioned) throws NullPointerException, IllegalArgumentException {
        if (messageId == null) {
            throw new NullPointerException("message id is null.");
        } if (channelId == null) {
            throw new NullPointerException("channel id is null.");
        } if (message == null || message.isBlank()) {
            throw new  IllegalArgumentException("message is null or empty.");
        } if (author == null) {
            throw new  NullPointerException("author is null.");
        }

        return new MessageDTO(messageId, channelId, message, author, allMentioned);
    }

    @Override
    public Message deleteById(UUID messageId) {
        if(messageId == null) {
            throw new NullPointerException("message id is null.");
        }

        Iterator<Message> iter =  data.iterator();
        while(iter.hasNext()) {
            Message msg = iter.next();
            if (msg.getId().equals(messageId)) {
                data.remove(msg);
                return msg;
            }
        }

        throw new IllegalArgumentException("message is not found.");
    }
}
