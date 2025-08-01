package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public ChannelDto.DetailResponse create(ChannelDto.CreateRequest request) {

        Channel channel = null;

        // 분리된 각각의 메소드를 DTO를 활용해 파라미터를 그룹화합니다.
        if(request.getType().equals(ChannelType.PRIVATE)){
            channel = createPrivate(request);
        }
        else {
            channel = createPublic(request);
        }

        return ChannelDto.DetailResponse.builder()
            .id(channel.getId())
            .name(channel.getName())
            .description(channel.getDescription())
            .lastMessageCreatedAt(null)
            .userIds(channel.getUserIds())
            .build();
    }

    private Channel createPrivate(ChannelDto.CreateRequest request){
        // 채널에 참여하는 User의 정보를 받아 User 별 ReadStatus 정보를 생성합니다.
        // name과 description 속성은 생략합니다.
        return new Channel(ChannelType.PRIVATE, "", "", request.getAdminUserId());
    }

    private Channel createPublic(ChannelDto.CreateRequest request){
        // PUBLIC 채널을 생성할 때에는 기존 로직을 유지합니다.
        return new Channel(ChannelType.PUBLIC, request.getName()
            , request.getDescription(), request.getAdminUserId());
    }

    public ChannelDto.DetailResponse findById(UUID id){

        Channel channel = channelRepository.findById(id).orElse(null);

        if(channel == null){
            return null;
        }

        if(channel.getType().equals(ChannelType.PRIVATE)){
            // 해당 채널의 가장 최근 메시지의 시간 정보를 포함합니다.
            // PRIVATE 채널인 경우 참여한 User의 id 정보를 포함합니다. > 근데 난 원래 갖고 있었는데?
            return ChannelDto.DetailResponse.builder()
                .id(channel.getId())
                .name(channel.getName())
                .description(channel.getDescription())
//                .lastMessageCreatedAt() // TODO 마지막 메세지 시간
                .userIds(channel.getUserIds())
                .build();
        }

        return ChannelDto.DetailResponse.builder()
            .id(channel.getId())
            .name(channel.getName())
            .description(channel.getDescription())
//                .lastMessageCreatedAt() // TODO 마지막 메세지 시간
            .userIds(channel.getUserIds())
            .build();
    }

    public List<ChannelDto.DetailResponse> findAll(){
        List<Channel> channels = channelRepository.findAll();

        return channels.stream().map(c -> {
            return ChannelDto.DetailResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
//                .lastMessageCreatedAt()
                .userIds(c.getUserIds())
                .build();
        }).collect(Collectors.toList());
    }

    public List<ChannelDto.DetailResponse> findAllByUserId(UUID userId){
        // 특정 User가 볼 수 있는 Channel 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다. findAllByUserId
        // PUBLIC 채널 목록은 전체 조회합니다.
        // PRIVATE 채널은 조회한 User가 참여한 채널만 조회합니다.

        List<Channel> channels = channelRepository.findAllByUserId(userId);

        return channels.stream().map(c ->
            ChannelDto.DetailResponse.builder()
            .id(c.getId())
            .name(c.getName())
            .description(c.getDescription())
//                .lastMessageCreatedAt()
            .userIds(c.getUserIds())
            .build()).collect(Collectors.toList());
    }

    public ChannelDto.DetailResponse update(ChannelDto.UpdateRequest request){
        // 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
        // PRIVATE 채널은 수정할 수 없습니다.

        return null;
    }

    @Override
    public void delete(UUID id) {
        // 관련된 도메인도 같이 삭제합니다. Message, ReadStatus
        Channel channel = channelRepository.findById(id).orElse(null);

        if (channel != null) {
            channelRepository.deleteById(id);
        }
    }


    // TODO mission 3 인터페이스 정리 예정 : create, find, findall, update, delete
    @Override
    public Channel create(Channel channel) {

        if (channel == null) {
            return null;
        }

        return channelRepository.save(channel);
    }

    @Override
    public Channel create(ChannelType type, String name, String description, UUID adminUserId) {

        if (type == null || name == null || adminUserId == null) {
            return null;
        }

        return channelRepository.save(new Channel(type, name, description, adminUserId));
    }

    @Override
    public List<Channel> getAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel get(UUID id) {
        return channelRepository.findById(id).orElse(null);
    }

    @Override
    public Channel update(UUID id, String name, String description) {
        Channel channel = channelRepository.findById(id).orElse(null);

        if (channel == null) {
            return null;
        }

        channel.update(name, description);
        return channelRepository.save(channel);
    }

    @Override
    public void deleteAll() {
        channelRepository.deleteAll();
    }
}
