package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    FileChannelRepository repo;

    public FileChannelService(){
        this.repo=new FileChannelRepository();
    }

    @Override
    public Channel createChannel(Channel.ChannelType type, String title, String description) {
        if(repo.count()!=0){
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

    @Override
    public Channel getChannelTitleById(UUID channelId) {
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

    @Override
    public List<Channel> getAllChannels() {
        if(repo.count()==0){
            return null;
        }
        return repo.findAll();
    }

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

    @Override
    public Channel deleteChannel(UUID channelId) {
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
    }
}
