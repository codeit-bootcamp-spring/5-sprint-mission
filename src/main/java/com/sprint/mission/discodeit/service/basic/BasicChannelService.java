package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("basicChannelService")   // Spring Context에 Bean으로 등록
@RequiredArgsConstructor          // 생성자 주입
public class BasicChannelService implements ChannelService {

    private final ChannelRepository repository; // 인터페이스 타입으로 주입

    @Override
    public void create(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("채널이 null입니다.");
        }
        if (channel.getId() == null) {
            throw new IllegalArgumentException("채널 ID가 없습니다.");
        }
        // 중복 방지
        if (repository.findById(channel.getId()) != null) {
            throw new IllegalArgumentException("이미 존재하는 채널입니다.");
        }
        repository.save(channel);
    }

    @Override
    public Channel findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 채널 ID가 null입니다.");
        }
        Channel original = repository.findById(id);
        if (original == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        return new Channel(original); // 복사본 반환
    }

    @Override
    public List<Channel> findAll() {
        return repository.findAll();
    }

    @Override
    public void update(Channel channel) {
        if (channel == null || channel.getId() == null) {
            throw new IllegalArgumentException("수정할 채널 정보가 올바르지 않습니다.");
        }
        if (repository.findById(channel.getId()) == null) {
            throw new IllegalArgumentException("해당 ID의 채널이 존재하지 않습니다.");
        }
        repository.update(channel);
    }

    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("삭제할 채널 ID가 null입니다.");
        }
        if (repository.findById(id) == null) {
            throw new IllegalStateException("삭제할 채널이 존재하지 않습니다: " + id);
        }
        repository.delete(id);
    }
}
