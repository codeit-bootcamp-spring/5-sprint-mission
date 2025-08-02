package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/*실제 구현체*/
public class JCFChannelService implements ChannelService {

    //데이터 저장소
    //UUID를 키로 해서 Channel 객체를 저장
    //고유한 키로 바로 찾기 위해 배열 대신 Map 사용
    //채널 하나 생성 -> 그 채널을 Map에 넣어 CRUD 쓸수 있도록
    private final Map<UUID, Channel> data; //final 필드는 무조건 초기화 한번 해줘야함!

    //생성자에서 초기화
    //data를 빈 HashMap으로 초기화 -> 메세지 담을 공간 만들어둠
    public JCFChannelService() {
        this.data = new HashMap<>();
    }


    //오버라이드
    //부모 클래스나 인터페이스에 정의된 메서드를 자식 클래스에서 재정의
    @Override
    public void create(Channel channel) {
        data.put(channel.getId(), channel); //채널 객체 받아 map에 uuid-채널 구조로 저장
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = data.get(id);
        if (channel == null) {
            throw new IllegalArgumentException(("해당 ID를 가진 채널이 존재하지 않습니다."));
        }
        return new Channel(channel); //원본 수정 방지용
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(data.values()); //map에 저장된 키값만 꺼내서 불변 리스트로 return해줌
    }

    @Override
    public void update(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("채널이 null입니다.");
        }
        if (channel.getId() == null) {
            throw new IllegalArgumentException("채널 ID가 null입니다.");
        }
        // 현재 data Map 안에, 이 채널의 ID를 가진 값이 없다면
        if (!data.containsKey(channel.getId())) {
            throw new IllegalArgumentException("해당 ID를 가진 채널이 존재하지 않습니다.");
        }
        data.put(channel.getId(), channel);

    }

    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID가 NULL입니다.");
        }
        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("해당 ID를 가진 채널이 존재하지 않습니다. + id");
        }
        data.remove(id);
    }
}
