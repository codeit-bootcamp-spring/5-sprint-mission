package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    List<Message> data;

    private final UserService userService;
    private final ChannelService channelService;


    public JCFMessageService(UserService userService, ChannelService channelService) {
        data = new ArrayList<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(Channel channel, String message, UUID author, boolean allMentioned) throws NullPointerException, IllegalArgumentException {
        try {
            userService.findById(author);
            channelService.findById(channel.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message is null or empty.");
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
    public Message update(UUID messageId, String message, boolean allMentioned) {
        if(messageId == null) {
            throw new  NullPointerException("messageId is null.");
        } if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("messageDTO message is null or blank.");
        }
        Iterator<Message> iter =  data.iterator();
        while(iter.hasNext()) {
            Message msg = iter.next();
            if (msg.getId().equals(messageId)) {
                msg.update(message, allMentioned);
                return msg;
            }
        }
        throw new IllegalArgumentException("message not found.");
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
