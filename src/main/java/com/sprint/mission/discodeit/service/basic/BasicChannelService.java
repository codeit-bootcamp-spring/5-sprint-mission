package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("basicChannelService")
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    @Qualifier("fileChannelRepository")
    private final ChannelRepository channelRepository;

    @Override
    public void createChannel(String channelname, String description) {
        Channel channel = new Channel(channelname, description);
        Channel channelresult = channelRepository.save(channel);
        System.out.println(channelresult.toString());
    }

    @Override
    public Channel readByIdChannel(UUID name) {
        return channelRepository.findById(name).orElse(null);
    }

    @Override
    public void readAllChannel() {
        List<Channel> channelList = channelRepository.findAll();
        long num = channelRepository.count();
        if(num>0){
            System.out.println("현재 등록된 채널은 "+num+"명 입니다.");
            for(Channel channel : channelList){
                System.out.println(channel.toString());
            }
        }else{
            System.out.println("현재 등록된 채널이 없습니다.");
        }
    }

    @Override
    public void updateChannel(UUID channelUUID, String channelname, String description) {
        if(channelRepository.existsById(channelUUID)){
            if(channelRepository.update(channelUUID,channelname,description)){
                System.out.println("수정 성공하였습니다.");
            }else{
                System.out.println("수정 실패하였습니다.");
            }
        }else{
            System.out.println("채널UUID가 존재하지 않습니다.");
        }
    }

    @Override
    public void deleteByIdChannel(UUID channelUUID) {
        if(channelRepository.existsById(channelUUID)) {
            if (channelRepository.delete(channelUUID)) {
                System.out.println("삭제 성공하였습니다.");
            } else {
                System.out.println("삭제 실패하였습니다.");
            }
        }else{
            System.out.println("채널UUID가 존재하지 않습니다.");
        }
    }
}
