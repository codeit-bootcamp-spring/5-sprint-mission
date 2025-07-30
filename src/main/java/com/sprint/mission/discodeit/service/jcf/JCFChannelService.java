package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel; // Channel 도메인 모델 import
import com.sprint.mission.discodeit.service.ChannelService; // ChannelService 인터페이스 import
import java.util.ArrayList; // ArrayList 클래스 import
import java.util.List; // List 인터페이스 import
import java.util.Map; // Map 인터페이스 import
import java.util.Optional; // Optional 클래스 import
import java.util.UUID; // UUID 클래스 import
import java.util.concurrent.ConcurrentHashMap; // ConcurrentHashMap 클래스 import

//Java Collections Framework (JCF)를 활용하여 Channel 데이터를 관리하는 ChannelService 구현체입니다.
//데이터는 메모리(Map)에 저장됩니다.
public class JCFChannelService implements ChannelService {

    // Channel 데이터를 저장할 Map (UUID를 키로, Channel 객체를 값으로 사용)
    // final로 선언하여 생성자에서만 초기화되도록 합니다.
    private final Map<UUID, Channel> data;

    //JCFChannelService의 생성자입니다.
    //데이터 저장을 위한 Map을 초기화합니다.
    public JCFChannelService() {
        this.data = new ConcurrentHashMap<>(); // ConcurrentHashMap을 사용하여 스레드 안전성 확보
    }

    //새로운 Channel을 생성하고 Map에 저장합니다.
    //@param channel 생성할 Channel 객체
    //@return 생성된 Channel 객체
    @Override
    public Channel create(Channel channel) {
        if (channel == null || data.containsKey(channel.getChannelId())) {
            // 유효하지 않은 Channel 객체이거나 이미 존재하는 ID인 경우 예외 처리 또는 null 반환
            System.err.println("오류: Channel 생성에 실패 했습니다. Channel이 null이거나 Channel ID가 이미 존재합니다: " + (channel != null ? channel.getChannelId() : "null"));
            return null;
        }
        data.put(channel.getChannelId(), channel); // Map에 Channel 객체 저장
        System.out.println("Channel 생성: " + channel);
        return channel;
    }

    //주어진 ID에 해당하는 Channel을 Map에서 조회합니다.
    //@param id 조회할 Channel의 UUID
    //@return 해당 ID의 Channel 객체 (존재하지 않으면 Optional.empty())
    @Override
    public Optional<Channel> findById(UUID channelId) {
        if (channelId == null) {
            System.err.println("오류: findById에 실패햇습니다. channel ID가 null입니다.");
            return Optional.empty();
        }
        return Optional.ofNullable(data.get(channelId)); // Map에서 ID에 해당하는 Channel을 찾아 Optional로 반환
    }

    //Map에 저장된 모든 Channel 객체를 조회합니다.
    //@return 모든 Channel 객체의 리스트
    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values()); // Map의 모든 값(Channel 객체)들을 ArrayList로 변환하여 반환
    }

    //주어진 ID에 해당하는 Channel을 Map에서 업데이트합니다.
    //Channel 객체의 update 메서드를 호출하여 필드를 수정하고, updatedAt을 업데이트합니다.
    //@param id 업데이트할 Channel의 UUID
    //@param updatedChannel 업데이트할 내용을 담은 Channel 객체 (id, createdAt 제외)
    //@return 업데이트된 Channel 객체 (존재하지 않으면 Optional.empty())

    @Override
    public Optional<Channel> update(UUID channelId, Channel updatedChannel) {
        if (channelId == null || updatedChannel == null) {
            System.err.println("에러: Channel 업데이트에 실패했습니다. Channel ID 또는 updatedChannel이 null입니다.");
            return Optional.empty();
        }
        return Optional.ofNullable(data.get(channelId))
                .map(existingChannel -> {
                    // 기존 Channel 객체의 update 메서드를 호출하여 필드 업데이트
                    existingChannel.updateChannel(updatedChannel.getChannelName(), updatedChannel.getDescription());
                    System.out.println("Channel 업데이트: " + existingChannel);
                    return existingChannel;
                });
    }

    //주어진 ID에 해당하는 Channel을 Map에서 삭제합니다.
    //@param channelId 삭제할 Channel의 UUID
    //@return 삭제 성공 여부 (true: 성공, false: 실패)
    @Override
    public boolean delete(UUID channelId) {
        if (channelId == null) {
            System.err.println("오류: Channel 삭제에 실패했습니다. Channel ID가 null입니다.");
            return false;
        }
        Channel removedChannel = data.remove(channelId); // Map에서 ID에 해당하는 Channel 삭제
        if (removedChannel != null) {
            System.out.println("Channel 삭제: " + removedChannel);
            return true; // 삭제 성공
        }
        System.out.println("Channel ID:" + channelId + " 인 Channel이 존재하지 않아 삭제 실패하였습니다");
        return false; // 해당 ID의 Channel이 존재하지 않아 삭제 실패
    }
}