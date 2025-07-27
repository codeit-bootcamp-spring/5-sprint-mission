package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private final String DIRECTORY;
    private final String EXTENTSION;

    public static void main(String[] args) {

        FileChannelRepository fcr = new FileChannelRepository();

        User user3 = new User("sss@sss.com", "kim", "1133", "#5666", UserStatus.OFFLINE);
        User user4 = new User("ttt@ttk.com", "park", "1564", "#4786", UserStatus.DND);
        User user5 = new User("kyy@yyk.com", "elis", "1777", "#9876", UserStatus.IDLE);
        User user2 = new User("jjj@jjj.com", "john", "3454", "#3132", UserStatus.ONLINE);
        User user1 = new User("kkk@kkk.com", "james", "1234", "#4756", UserStatus.ONLINE);

        System.out.println("user 1: " + user1.toString());
        System.out.println("user 2: " + user2.toString());
        System.out.println("user 3: " + user3.toString());
        System.out.println("user 4: " + user4.toString());
        System.out.println("user 5: " + user5.toString());

        System.out.println("------------- File Save ---------");
        Channel chnl1 = fcr.save(new Channel(user1.getId(), "BE-SPRING", ChannelType.COMMON,false));
        Channel chnl2 = fcr.save(new Channel(user3.getId(), "Chickens", ChannelType.PERSONAL,false));
        Channel chnl3 = fcr.save(new Channel(user4.getId(), "Movies", ChannelType.PERSONAL,true));

        System.out.println("channel 1:\n" + chnl1.toString());
        System.out.println("\nchannel 2:\n" + chnl2.toString());
        System.out.println("\nchannel 3:\n" + chnl3.toString());

        System.out.println("------------- file findById ---------");
        System.out.println("find channel2 : " + fcr.findById(chnl2.getId()).toString());

        System.out.println("------------- read all channels ---------");
        List<Channel> channels = fcr.findAll();
        channels.forEach(System.out::println);

        System.out.println("------------- files count ---------");
        System.out.println("The number of files: " + fcr.count());

        System.out.println("------------- file deleted ---------");
        Channel delCh3 = fcr.delete(chnl3.getId());
        System.out.println("Deleted channel3 : " + delCh3.toString());
    }

    public FileChannelRepository() {
        this.DIRECTORY = "CHANNEL/ChannelRepository";
        this.EXTENTSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if(!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Channel save(Channel channel) {
        Path path = Paths.get(DIRECTORY, channel.getId() +  EXTENTSION);
        try(FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Channel channel = null;
        Path path = Paths.get(DIRECTORY, id + EXTENTSION);
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis);) {
            channel = (Channel) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(channel);
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channels = new ArrayList<>();
        File dir = new File(DIRECTORY);
        File[] files = dir.listFiles((d, name) -> name.endsWith(EXTENTSION));
        if(files != null) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis)) {
                    Channel channel = (Channel) ois.readObject();
                    channels.add(channel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return channels;
    }

    @Override
    public long count() {
        File file = new File(DIRECTORY);
        Long count = (long) file.listFiles((d, name) -> name.endsWith(EXTENTSION)).length;

        if (count != null) {
            return count;
        }

        return -1L;
    }

    @Override
    public Channel delete(UUID id) {
        Path path = Paths.get(DIRECTORY, id + EXTENTSION);
        Channel channel = null;

        try (FileInputStream fis = new FileInputStream(path.toFile());
        ObjectInputStream ois = new ObjectInputStream(fis);) {
            if(existsById(id)) {
                channel = (Channel) ois.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Files.deleteIfExists(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel;
    }

    @Override
    public boolean existsById(UUID id) {
        File file = new File(DIRECTORY, id + EXTENTSION);
        if(file.exists()) {
            return true;
        }
        return false;
    }
}
