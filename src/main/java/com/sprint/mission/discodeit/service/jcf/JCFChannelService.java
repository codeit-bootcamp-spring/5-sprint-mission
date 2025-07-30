package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    JCFChannelRepository repo;

    public JCFChannelService(){
        this.repo=new JCFChannelRepository();
    }

    public Channel createChannel(Channel.ChannelType type, String title,String description){
        if(repo.count()!= 0){
            for(Channel channel:repo.findAll()){
                if(channel.getTitle().equals(title)){
                    System.out.println("title 중복");
                    return null;
                }
            }
        }

        Channel target=new Channel(type,title,description);
        repo.save(target);
        System.out.println("채널 생성 성공");
        return target;
    }

//    @Override
//    public UUID addUser(UUID channelId, UUID userId) {
//        if(data.size()==0){
//            return null;
//        }
//        User target =null;
//
//        for(Channel c:data){
//            if(c.getId().equals(channelId)){
//                c.updateUserId(userId);
//                System.out.println("channel에 user 추가 성공");
//                return userId;
//            }
//        }
//        return null;
//
//    }
//
//    @Override
//    public UUID addMessage(UUID channelId, UUID messageId) {
//        if(data.size()==0){
//            return null;
//        }
//        Message target =null;
//
//        for(Channel c:data){
//            if(c.getId().equals(channelId)){
//                c.updateMessageId(messageId);
//                System.out.println("channel에 message 추가 성공");
//                return messageId;
//
//            }
//        }
//        return null;
//    }

    public Channel getChannelTitleById(UUID channelId){
        if(repo.count()==0){
            return null;
        }
        for(Channel channel:repo.findAll()){
            if(channel.getId().equals(channelId)){
                return channel;
            }
        }
        return null;
    }

    @Override
    public UUID getChannelIdByTitle(String title) {
        if(repo.count()==0){
            return null;
        }
        for(Channel chan:repo.findAll()){
            if(chan.getTitle().equals(title)){
                return chan.getId();
            }
        }
        return null;
    }

    ;

    public List<Channel> getAllChannels(){
        if(repo.count()==0){
            return null;
        }
        return repo.findAll();
    }


//    @Override
//    public List<UUID> findAllUsersId(UUID channelId) {
//        if(data.size()==0){
//            return null;
//        }
//
//        List<UUID> users=new ArrayList<>();
//        for(Channel channel:data){
//            if(channel.getId().equals(channelId)){
//                if(channel.getUserId()==null){
//                    return null;
//                }
//                for(UUID id:channel.getUserId()){
//                    users.add(id);
//                }
//            }
//
//        }
//        return users;
//    }
//
//    @Override
//    public List<UUID> findAllMessagesId(UUID channelId) {
//        if(data.size()==0){
//            return null;
//        }
//
//        List<UUID> messages=new ArrayList<>();
//        for(Channel channel:data){
//            if(channel.getId().equals(channelId)){
//                if(channel.getMessageId()==null){
//                    return null;
//                }
//                for(UUID id:channel.getMessageId()){
//                    messages.add(id);
//                }
//            }
//
//        }
//        return messages;
//    }




    @Override
    public Channel updateChannelTitle(UUID channelId, String title) {
        if(repo.count() ==0){
            return null;
        }
        Channel target=null;
        for(Channel channel:repo.findAll()){
            if(channel.getId().equals(channelId)){
                if(channel.getTitle().equals(title)){
                    return null;
                }
                channel.updateTitle(title);
                target=channel;
            }
        }
        return target;
    }

    @Override
    public Channel updateChannelDescription(UUID channelId, String description) {
        if(repo.count()==0){
            return null;
        }
        Channel target=null;
        for(Channel channel:repo.findAll()){
            if(channel.getId().equals(channelId)){
                if(channel.getDescription().equals(description)){
                    return null;
                }
                channel.updateDescription(description);
                target=channel;
            }
        }
        return target;
    }

    @Override
    public Channel updateChannelType(UUID channelId, Channel.ChannelType type) {
        if(repo.count()==0){
            return null;
        }
        Channel target=null;
        for(Channel channel:repo.findAll()){
            if(channel.getId().equals(channelId)){
                if(channel.getType().equals(type)){
                    return null;
                }
                channel.updateType(type);
                target=channel;
            }
        }
        return target;
    }


    public Channel deleteChannel(UUID channelId){
        Channel target=null;
        if(repo.count()==0){
            return null;
        }
        for(Channel channel:repo.findAll()){
            if(channel.getId().equals(channelId)){
                target=channel;
                repo.delete(channelId);
            }
        }
        return target;
//        List<Channel> toRemove = new ArrayList<>();
//        for (Channel channel1 : data) {
//            if (channel1.getId().equals(channelId)) {
//                toRemove.add(channel1);
//            }
//        }
//        System.out.println(toRemove);
//        data.removeAll(toRemove);
    };

}
