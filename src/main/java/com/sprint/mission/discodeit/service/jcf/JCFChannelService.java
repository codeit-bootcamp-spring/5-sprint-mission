package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data = new ArrayList<>();

    public static void main(String[] args) {
        JCFUserService jcfu = new JCFUserService();
        JCFChannelService jcfc = new JCFChannelService();

        System.out.println("------------ user create ------------");
        User user1 = jcfu.createUser("kkk@kkk.com", "james", "1234", "#4756", "online");
        User user2 = jcfu.createUser("jjj@jjj.com", "john", "3454", "#3132", "offline");
        User user3 = jcfu.createUser("sss@sss.com", "kim", "1133", "#5666", "online");
        User user4 = jcfu.createUser("ttt@ttk.com", "park", "1564", "#4786", "afk");
        User user5 = jcfu.createUser("kyy@yyk.com", "elis", "1777", "#9876", "good");

        System.out.println("user 1: " + user1.toString());
        System.out.println("user 2: " + user2.toString());
        System.out.println("user 3: " + user3.toString());
        System.out.println("user 4: " + user4.toString());
        System.out.println("user 5: " + user5.toString());


        try {
            System.out.println("------------- channel create ------------");
            Channel chnl1 = jcfc.createChannel(null, "BE-SPRING", false);
            Channel chnl2 = jcfc.createChannel(user3, "Chickens", false);
            Channel chnl3 = jcfc.createChannel(user4, "Movies", true);

            System.out.println("channel 1:\n" + chnl1.toString());
            System.out.println("\nchannel 2:\n" + chnl2.toString());
            System.out.println("\nchannel 3:\n" + chnl3.toString());
            System.out.println("------------ add members  ------------");
            chnl1.addMembers(user2);
            List<User> members1 = chnl1.addMembers(user3);
            List<User> members2 = chnl2.addMembers(user2);
            chnl2.addMembers(user5);
            System.out.println("channel 1's Members : ");
            for(User u : members1) {
                System.out.println(u.toString());
            }
            System.out.println("channel 2's Members : ");
            for(User u : members2) {
                System.out.println(u.toString());
            }
//            chnl2.addMembers(null);
//            chnl2.addMembers(user3);

            System.out.println("------------ find channel  ------------");
            Channel findCh2 = jcfc.findById(chnl2.getId());
            System.out.println("channel 2 : " + chnl2.toString());
            System.out.println("search ch 2 : " + findCh2.toString());
            System.out.println("------------ find all channel  ------------");
            List<Channel> channels =  jcfc.findAll();
            channels.forEach((o) -> System.out.println(o.getChannelName()+ " : " + o.toString()));
            System.out.println("------------ delete channel  ------------");
            Channel delCh3 = jcfc.deleteById(chnl3.getId());
//            Channel delCh3 = jcfc.deleteById(null);
//            Channel delCh3 = jcfc.deleteById(UUID.randomUUID());
            System.out.println("deleted channel : " + delCh3);
            System.out.println("------------ update channel  ------------");
            ChannelDTO channelDTO = jcfc.createChannelDTO(chnl2.getId(), "IntelliJ", user5, true);
//            ChannelDTO channelDTO = jcfc.createChannelDTO(chnl2.getId(), "IntelliJ", user4, true);
//            ChannelDTO channelDTO = jcfc.createChannelDTO(UUID.randomUUID(), "IntelliJ", user5, true);
//            ChannelDTO channelDTO = jcfc.createChannelDTO(chnl2.getId(), "IntelliJ", user3, true);
            System.out.println("channel DTO : " + channelDTO.toString());
            System.out.println("Before updating : " + chnl2.toString());
            jcfc.update(channelDTO);
            System.out.println("update " + chnl2.getChannelName() + " : " + chnl2.toString());
        } catch (NullPointerException | IllegalArgumentException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    public JCFChannelService() {}

    @Override
    public Channel createChannel(User user, String channelName, boolean nsfw) throws IllegalArgumentException, NullPointerException {
        if(user == null) {
            throw new NullPointerException("A channel object is empty.");
        } if(channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("channelName is null or blank ");
        }

        Channel channel = new Channel(user, channelName, nsfw);
        data.add(channel);

        return channel;
    }

    @Override
    public Channel findById(UUID channelId) throws NullPointerException, IllegalArgumentException {
        if(channelId == null) {
            throw new NullPointerException("channel id is null.");
        }
        for(Channel channel : data) {
            if(channel.getId().equals(channelId)) {
                return channel;
            }
        }

        throw new IllegalArgumentException("Channel not found.");
    }

    @Override
    public List<Channel> findAll() { return data; }

    @Override
    public Channel update(ChannelDTO channelDTO) throws NullPointerException, IllegalArgumentException {
        if(channelDTO.getId() == null) {
            throw new NullPointerException("channel id is null.");
        } if(channelDTO.getChannelName() == null || channelDTO.getChannelName().isBlank()) {
            throw new IllegalArgumentException("channel name is null or blank.");
        } if(channelDTO.getOwnerId() == null) {
            throw new NullPointerException("channel owner id is null.");
        }
        Iterator<Channel> iter = data.iterator();
        while (iter.hasNext()) {
            Channel channel = iter.next();
            if(channelDTO.getId().equals(channel.getId())) {
                try {
                    channel.update(channelDTO);
                } catch (IllegalArgumentException e) {
                    System.out.println((e.getMessage()));
                }
                return channel;
            }
        }

        throw new IllegalArgumentException("Channel not found.");
    }

    @Override
    public Channel deleteById(UUID channelId) throws NullPointerException, IllegalArgumentException {
        if(channelId == null) {
            throw new NullPointerException("channel id is null.");
        }

        Iterator<Channel> iter = data.iterator();
        while(iter.hasNext()) {
            Channel channel = iter.next();
            if(channel.getId().equals(channelId)) {
                data.remove(channel);
                return channel;
            }
        }
        throw new IllegalArgumentException("Channel id is wrong.");
    }

    @Override
    public ChannelDTO createChannelDTO(UUID channelId, String channelName, User owner, boolean nsfw) throws NullPointerException, IllegalArgumentException {
        if(channelId == null) {
            throw new NullPointerException("channel id is null.");
        } if(channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("channelName is null or blank");
        } if(owner == null) {
            throw new NullPointerException("owner is null.");
        }

        return new ChannelDTO(channelId, channelName, owner, nsfw);
    }

}
