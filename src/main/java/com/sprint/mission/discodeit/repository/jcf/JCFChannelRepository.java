package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public static void main(String[] args) {

        ChannelRepository cr = new JCFChannelRepository();

        User user1 = new User("kkk@kkk.com", "james", "1234", "#4756", UserStatus.ONLINE);
        User user2 = new User("jjj@jjj.com", "john", "3454", "#3132", UserStatus.ONLINE);
        User user3 = new User("sss@sss.com", "kim", "1133", "#5666", UserStatus.OFFLINE);
        User user4 = new User("ttt@ttk.com", "park", "1564", "#4786", UserStatus.DND);
        User user5 = new User("kyy@yyk.com", "elis", "1777", "#9876", UserStatus.IDLE);

        System.out.println("user 1: " + user1.toString());
        System.out.println("user 2: " + user2.toString());
        System.out.println("user 3: " + user3.toString());
        System.out.println("user 4: " + user4.toString());
        System.out.println("user 5: " + user5.toString());

        Channel chnl1 = new Channel(user1.getId(), "BE-SPRING", ChannelType.COMMON,false);
        Channel chnl2 = new Channel(user3.getId(), "Chickens", ChannelType.PERSONAL,false);
        Channel chnl3 = new Channel(user4.getId(), "Movies", ChannelType.PERSONAL,true);

        System.out.println("------------- channel save ---------------");

        cr.save(chnl1);
        cr.save(chnl2);
        cr.save(chnl3);

        System.out.println("channel 1:\n" + chnl1.toString());
        System.out.println("\nchannel 2:\n" + chnl2.toString());
        System.out.println("\nchannel 3:\n" + chnl3.toString());

        System.out.println("The number of users : " + cr.count());
        System.out.println("Channel List : ");
        List<Channel> channels = cr.findAll();
        channels.forEach(System.out::println);

        System.out.println("------------- channel find ---------------");
        Channel findCh2 = cr.findById(chnl2.getId()).get();
        System.out.println("Find channel 2 : " +  findCh2.toString());

        System.out.println("------------- delete find ---------------");
        Channel deleteCh3 = cr.delete(chnl3.getId());
        System.out.println("Delete channel 3 : " +  deleteCh3.toString());
    }

    public JCFChannelRepository() {
        data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        data.putIfAbsent(channel.getId(), channel);

        return data.get(channel.getId());
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Channel channel = data.get(id);

        return Optional.ofNullable(channel);
    }

    @Override
    public List<Channel> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public long count() {
        return (long) data.size();
    }

    @Override
    public Channel delete(UUID id) {
        Channel channel = null;
        if(existsById(id)) {
            channel = data.remove(id);
        }
        return channel;
    }

    @Override
    public boolean existsById(UUID id) {
        if(data.containsKey(id)) {
            return true;
        }
        return false;
    }
}
