package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository cr;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.cr = channelRepository;
    }

    @Override
    public void createChannel(String channelname, String description) {
        Channel channel = new Channel(channelname, description);
        Channel channelresult = cr.save(channel);
        System.out.println(channelresult.toString());
    }

    @Override
    public Channel readByIdChannel(UUID name) {
        return cr.findById(name).orElse(null);
    }

    @Override
    public void readAllChannel() {
        List<Channel> channelList = cr.findAll();
        long num = cr.count();
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
        if(cr.existsById(channelUUID)){
            if(cr.update(channelUUID,channelname,description)){
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
        if(cr.existsById(channelUUID)) {
            if (cr.delete(channelUUID)) {
                System.out.println("삭제 성공하였습니다.");
            } else {
                System.out.println("삭제 실패하였습니다.");
            }
        }else{
            System.out.println("채널UUID가 존재하지 않습니다.");
        }
    }
}
