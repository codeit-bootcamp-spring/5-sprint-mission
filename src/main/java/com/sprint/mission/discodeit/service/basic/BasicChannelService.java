package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public static void main(String[] args) {
        UserRepository ur = new FileUserRepository();
        ChannelService cs = new BasicChannelService(ur);

        System.out.println("----------- create users ---------------");
        User user1 = ur.save(new User("kkk@kkk.com", "james", "1234", "#4756", UserStatus.ONLINE));
        User user2 = ur.save(new User("jjj@jjj.com", "john", "3454", "#3132", UserStatus.ONLINE));
        User user3 = ur.save(new User("sss@sss.com", "kim", "1133", "#5666", UserStatus.OFFLINE));
        User user4 = ur.save(new User("ttt@ttk.com", "park", "1564", "#4786", UserStatus.DND));
        User user5 = ur.save(new User("kyy@yyk.com", "elis", "1777", "#9876", UserStatus.IDLE));

        System.out.println("user 1: " + user1.toString());
        System.out.println("user 2: " + user2.toString());
        System.out.println("user 3: " + user3.toString());
        System.out.println("user 4: " + user4.toString());
        System.out.println("user 5: " + user5.toString());

        System.out.println("------------ create channels ---------------");
        Channel chnl1 = cs.createChannel(user1.getId(), "BE-SPRING", ChannelType.COMMON,false);
        Channel chnl2 = cs.createChannel(user3.getId(), "Chickens", ChannelType.PERSONAL,false);
        Channel chnl3 = cs.createChannel(user4.getId(), "Movies", ChannelType.PERSONAL,true);

        System.out.println("channel 1:\n" + chnl1.toString());
        System.out.println("\nchannel 2:\n" + chnl2.toString());
        System.out.println("\nchannel 3:\n" + chnl3.toString());

        System.out.println("------------ find channels ---------------");
        Channel findCh2 = cs.findById(chnl2.getId());
        System.out.println("search ch 2 : " + findCh2.toString());

        System.out.println("------------ read all channels ---------------");
        List<Channel> channels =  cs.findAll();
        channels.forEach(System.out::println);

        System.out.println("------------ delete channels ---------------");
        Channel delCh3 = cs.deleteById(chnl3.getId());
        System.out.println("delete ch 3 : " + delCh3.toString());

        System.out.println("------------ update channels ---------------");
        System.out.println("Change Value[ChannelId, UserId, ChannelName, nsfw]: " + chnl2.getId() + ", " + user5.getId() + ", IntelliJ" + ", " + true);
        System.out.println("Before updating : " + chnl2.toString());
        Channel updateC2 = cs.update(chnl2.getId(), UUID.randomUUID(), "IntelliJ", true);
        System.out.println("After update : " + updateC2.toString());
        System.out.println("Check update : " + cs.findById(chnl2.getId()).toString());

    }

    public BasicChannelService(UserRepository userRepository) {
        channelRepository = new FileChannelRepository();
        this.userRepository = userRepository;
    }

    @Override
    public Channel createChannel(UUID userId, String channelName, ChannelType channelType, boolean nsfw) {
        if(userId == null || !userRepository.existsById(userId)) {
            throw new IllegalArgumentException("[Error] : 사용자가 존재하지 않습니다.");
        } if(channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("channelName is null or blank.");
        } if(channelType == null) {
            throw new IllegalArgumentException("channelType is null.");
        }

        return channelRepository.save(new Channel(userId, channelName, channelType, nsfw));
    }

    @Override
    public Channel findById(UUID channelId) {
        Channel findChannel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("[Error]: id{" + channelId + "}는 존재하지 않는 사용자입니다."));

        return findChannel;
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID channelId, UUID ownerId, String channelName, boolean nsfw) {
        if(ownerId == null) {
            throw new NullPointerException("A channel object is empty.");
        } if(channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("channelName is null or blank.");
        }
        if(!userRepository.existsById(ownerId)) {
            throw new NoSuchElementException("존재하지 않는 유저입니다.");
        }
        Channel existChannel = findById(channelId);
        existChannel.update(channelId, channelName, nsfw);

        return channelRepository.save(existChannel);
    }

    @Override
    public Channel deleteById(UUID channelId) {
        return channelRepository.delete(channelId);
    }
}
