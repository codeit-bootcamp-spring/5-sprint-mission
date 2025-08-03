package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private FileChannelRepository fcr;
    public FileChannelService() {
        this.fcr = new FileChannelRepository();
    }
    @Override
    public void createChannel(String channelname, String description) {
        Channel channel = new Channel(channelname, description);
        Channel channelresult = fcr.save(channel);
        System.out.println(channelresult.toString());

    }

    @Override
    public Channel readByIdChannel(UUID name) {
        if(fcr.existsById(name)){
            return fcr.findById(name).orElse(null);
        }
        System.out.println("채널 UUID가 존재하지 않습니다.");
        return null;
    }

    @Override
    public void readAllChannel() {
        long num = fcr.count();
        if(num>0) {
            System.out.println("현재 등록된 채널은 "+num+"명 입니다.");
            List<Channel> channelList = fcr.findAll();
            for (Channel channel : channelList) {
                System.out.println(channel.toString());
            }
        }else{
            System.out.println("현재 등록된 유저가 없습니다.");
        }
    }

    @Override
    public void updateChannel(UUID channelUUID, String channelname, String description) {
        if(fcr.existsById(channelUUID)){
            if(fcr.update(channelUUID,channelname,description)){
                System.out.println("사용자 수정을 성공하였습니다.");
            }else{
                System.out.println("사용자 수정을 실패하였습니다.");
            }
        }
    }

    @Override
    public void deleteByIdChannel(UUID channelUUID) {
        if(fcr.existsById(channelUUID)) {
            if(fcr.delete(channelUUID)){
                System.out.println("유저 삭제 성공!");
            }else{
                System.out.println("유저 삭제 실패");
            }
        }else{
            System.out.println("유저UUID가 존재하지 않습니다.");
        }
    }
}
