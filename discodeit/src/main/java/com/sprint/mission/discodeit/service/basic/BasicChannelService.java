package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@Slf4j
@Service("basicChannelService")
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

//    public BasicChannelService(ChannelRepository channelRepository) {
//        this.channelRepository = channelRepository;
//    }

    @Override
    public Channel createPublic(ChannelCreateRequest request) {
        Channel channel = new Channel(request.type(), request.name(), request.description());
        return channelRepository.save(channel);
    }


    @Override
    public Channel createPrivate(ChannelType type, User user) {
        Channel channel = new Channel(type,user.getId());
        ReadStatus readStatus = new ReadStatus(user);
        readStatus.setId(channel.getId());
        readStatusRepository.save(new ReadStatus(user));

        return channelRepository.save(channel);

    }

    @Override
    public ChannelFindResponse find(UUID channelId) {
        Channel channel=channelRepository.findById(channelId).orElseThrow(NoSuchElementException::new);
        Instant time=Instant.MIN;
        if(channel.getType().equals(ChannelType.PRIVATE)){
            if(messageRepository.findAll()!= null){
                for(Message message:messageRepository.findAll()){
                    if(message.getChannelId().equals(channelId)) {
                        if(time.isAfter(message.getCreatedAt())){
                            time=message.getCreatedAt();
                        }
                    }
                }
            }
            return new ChannelFindResponse(channelId,time,channelRepository.findById(channelId).orElseThrow(NoSuchElementException::new).getId());
        }

        if(messageRepository.findAll()!= null){
            for(Message message:messageRepository.findAll()){
                if(message.getChannelId().equals(channelId)) {
                    if(time.isAfter(message.getCreatedAt())){
                        time=message.getCreatedAt();
                    }
                }
            }
        }
        return new ChannelFindResponse(channelId,time,null);
    }


    @Override
    public List<ChannelFindResponse> findAllByUserId(User user) {
        List<Channel> channel=channelRepository.findAll();
        List<ChannelFindResponse> findChannelResponse=new ArrayList<>();

        Instant time=Instant.MIN;
        for(Channel cha:channel) {
            if (cha.getType().equals(ChannelType.PRIVATE) && cha.getUserIds().contains(user.getId())) {
                if (messageRepository.findAll() != null) {
                    for (Message message : messageRepository.findAll()) {
                        if (message.getChannelId().equals(cha.getId())) {
                            if (time.isAfter(message.getCreatedAt())) {
                                time = message.getCreatedAt();
                            }
                        }
                    }
                }
                findChannelResponse.add(new ChannelFindResponse(cha.getId(), time, user.getId()));
            }

            if(messageRepository.findAll()!= null){
                for(Message message:messageRepository.findAll()){
                    if(message.getChannelId().equals(cha.getId())) {
                        if(time.isAfter(message.getCreatedAt())){
                            time=message.getCreatedAt();
                        }
                    }
                }
                findChannelResponse.add(new ChannelFindResponse(cha.getId(),time,null));
            }

        }
        if (findChannelResponse.isEmpty()) {
            return new ArrayList<>();
        }
        return findChannelResponse;
    }


    @Override
    public Channel update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + request.channelId() + " not found"));
        if(channel.getType().equals(ChannelType.PRIVATE)){
            throw new RuntimeException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.update(request.newName(), request.newDescription());
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        for(Message message:messageRepository.findAll()){
            if(message.getChannelId().equals(channelId)) {
                messageRepository.deleteById(message.getId());
            }
        }
        for(ReadStatus readStatus:readStatusRepository.findAll()){
            if(readStatus.getId().equals(channelId)) {
                readStatusRepository.deleteById(readStatus.getId());
            }
        }


        channelRepository.deleteById(channelId);
    }
}
