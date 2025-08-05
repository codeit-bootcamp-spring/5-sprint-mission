package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {

    //UUID를 키로 해서 Channel 객체 저장
    //고유한 키로 바로 찾기 위해 배열 대신 Map 사용
    private final Map<UUID, Channel> data = new HashMap<>();  //data를 빈 HashMap으로 초기화 -> 메세지 담을 공간 만들어둠

    @Override
    public void save(Channel channel) {
        data.put(channel.getId(), channel);
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = data.get(id);
        if (channel == null) {
            throw new IllegalArgumentException("해당 ID를 가진 채널이 존재하지 않습니다.");
        }
        return new Channel(channel); // 복사본 리턴
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public void update(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("채널이 NULL입니다.");
        }
        if (channel.getId() == null) {
            throw new IllegalArgumentException("채널 ID가 NULL입니다.");
        }
        if (!data.containsKey(channel.getId())) {
            throw new IllegalArgumentException("해당 ID를 가진 채널이 존재하지 않습니다.");
        }
        data.put(channel.getId(), channel);
    }

    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("채널 ID가 NULL입니다.");
        }
        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("해당 ID를 가진 채널이 존재하지 않습니다.");
        }
        data.remove(id);
    }
}
