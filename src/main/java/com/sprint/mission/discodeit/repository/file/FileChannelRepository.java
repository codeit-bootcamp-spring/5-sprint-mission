package com.sprint.mission.discodeit.repository.file;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Repository // ChannelRepository의 빈으로 FileChannelRepository를 등록해줌
public class FileChannelRepository implements ChannelRepository {

    private static final String FILE_PATH = "channel.dat";
    private final Map<UUID, Channel> data = load(); //시작할때 로드함

    @Override
    public void save(Channel channel) {
        data.put(channel.getId(), channel);
        saveToFile(); // 파일에 저장
    }

    @Override
    public Channel findById(UUID id) {
        Channel found = data.get(id);
        if (found == null) {
            return null;
        }
        return new Channel(found); // 복사해서 반환
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public void update(Channel channel) {
        save(channel);
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        saveToFile();
    }

    //객체 -> 파일 직렬화
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("채널 저장 실패", e);
        }
    }


    //파일 -> 객체 역직렬화
    private Map<UUID, Channel> load() {
        //저장된 파일객체 생성
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) { // 파일 오류 or 클래스 못찾는 오류시
            throw new RuntimeException("채널 로딩 실패", e);
        }
    }
}
