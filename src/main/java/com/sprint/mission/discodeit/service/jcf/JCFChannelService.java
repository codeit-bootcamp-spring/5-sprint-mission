package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

/*실제 구현체*/
public class JCFChannelService implements ChannelService {

    //의존성 주입
    private final ChannelRepository repository = new JCFChannelRepository();

    //오버라이드
    //부모 클래스나 인터페이스에 정의된 메서드를 자식 클래스에서 재정의
    @Override
    public void create(Channel channel) {
        repository.save(channel);
    }

    @Override
    public Channel findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Channel> findAll() {
        return repository.findAll();
    }

    @Override
    public void update(Channel channel) {
        repository.update(channel);

    }

    @Override
    public void delete(UUID id) {
        repository.delete(id);
    }
}
