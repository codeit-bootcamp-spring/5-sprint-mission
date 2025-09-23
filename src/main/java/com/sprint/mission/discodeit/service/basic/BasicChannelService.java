package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.channel.ChannelLeaveRequest;
import com.sprint.mission.discodeit.dto.request.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelDeleteResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelLeaveResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.*;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public ChannelResponse create(PublicChannelCreateRequest request) {
        log.info("[Service] 공개 채널 생성 시도");
        log.debug("[Service] 공개 채널 생성 요청 데이터: {}", request);
        if (channelRepository.existsByName(request.getName())) {
            log.warn("[Service] 중복된 채널 이름 생성, 채널 이름: {}", request.getName());
            throw DuplicateChannelNameException.withChannelName(request.getName());
        }

        Channel channel = new Channel(request.getName(), request.getDescription());
        channelRepository.save(channel);

        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus);
        }

        log.info("[Service] 공개 채널 생성 성공");
        log.debug("[Service] 공개 채널 생성 완료 데이터: {}", channel);
        return createChannelByType(channel);
    }

    @Override
    @Transactional
    public ChannelResponse create(PrivateChannelCreateRequest request) {
        log.info("[Service] 비공개 채널 생성 시도");
        log.debug("[Service] 비공개 채널 생성 요청 데이터: {}", request);
        List<UUID> participantIds = request.getParticipantIds().stream().distinct().toList();

        if (participantIds.size() < 2) {
            throw InvalidParticipantException.insufficientCount(
                    participantIds.size(), 2, participantIds
            );
        }

        for (int i = 0; i < participantIds.size(); i++) {
            UUID userId = participantIds.get(i);
            if (userId == null) {
                log.warn("[Service] 참여자 id가 null이 포함되어 있음");
                throw InvalidParticipantException.nullParticipant(i, participantIds);
            }
            if (!userRepository.existsById(userId)) {
                log.warn("[Service] 존재하지 않는 유저가 참여자로 포함됨, userId: {}", userId);
                throw InvalidParticipantException.invalidUser(userId, participantIds);
            }
        }

        Channel channel = new Channel(participantIds);
        channelRepository.save(channel);

        for (UUID userId : participantIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> InvalidParticipantException.invalidUser(userId, participantIds));

            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus);
        }

        log.info("[Service] 비공개 채널 생성 성공");
        log.debug("[Service] 비공개 채널 생성 완료 데이터: {}", channel);
        return createChannelByType(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelResponse findByName(String channelName) {
        Channel channel = channelRepository.findByName(channelName)
                .orElseThrow(() -> ChannelNotFoundException.withChannelName(channelName));

        return createChannelByType(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelResponse find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId));

        return createChannelByType(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelResponse> findChannelsByUserId(UUID userId) {

        List<UUID> myChannelIds = readStatusRepository.findByUserId(userId).stream()
                .map(rs -> rs.getChannel().getId())
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel -> myChannelIds.contains(channel.getId()))
                .map(this::createChannelByType)
                .toList();
    }


    // updateChannel 메서드
    @Override
    @Transactional
    public ChannelResponse updateChannel(UUID channelId, ChannelUpdateRequest request) {
        log.info("[Service] 채널 정보 수정 시도");
        log.debug("[Service] 채널 정보 수정 요청 데이터: channelId={}, request={}", channelId, request);
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId));

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            log.warn("[Service] 비공개 채널 정보 수정 시도, 채널 ID: {}", channelId);
            throw PrivateChannelUpdateException.withChannelId(channelId);
        }

        if (request.getNewName() != null && channelRepository.existsByName(request.getNewName())) {
            log.warn("[Service] 중복된 채널 이름 수정 시도, 채널 이름: {}", request.getNewName());
            throw DuplicateChannelNameException.withChannelName(request.getNewName());
        }

        if (request.getNewName() != null) {
            channel.setName(request.getNewName());
        }
        if (request.getNewDescription() != null) {
            channel.setDescription(request.getNewDescription());
        }
        channelRepository.save(channel);

        log.info("[Service] 채널 정보 수정 성공");
        log.debug("[Service] 채널 정보 수정 완료 데이터: {}", channel);
        return createChannelByType(channel);
    }

    @Override
    @Transactional
    public ChannelLeaveResponse leaveChannel(ChannelLeaveRequest request) {
        log.info("[Service] 채널 나가기 시도");
        log.debug("[Service] 채널 나가기 요청 데이터: {}", request);
        Channel channel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> ChannelNotFoundException.withId(request.getChannelId()));

        ReadStatus readStatus = readStatusRepository.findByChannelIdAndUserId(
                request.getChannelId(), request.getUserId());

        if (readStatus == null) {
            throw NotChannelMemberException.withUserIdAndChannelId(request.getUserId(), request.getChannelId());
        }

        readStatusRepository.deleteById(readStatus.getId());

        channelRepository.save(channel);

        String nickname = userRepository.findById(request.getUserId())
                .map(User::getDefaultNickname)
                .orElseThrow(() -> UserNotFoundException.withId(request.getUserId()));

        log.info("[Service] 채널 나가기 성공");
        log.debug("[Service] 채널 나가기 완료 데이터: channelId={}, userId={}", request.getChannelId(), request.getUserId());
        return ChannelLeaveResponse.success(channel, request.getUserId(), nickname);
    }

    @Override
    @Transactional
    public ChannelDeleteResponse deleteChannel(UUID channelId) {
        log.info("[Service] 채널 삭제 시도");
        log.debug("[Service] 채널 삭제 요청 데이터: channelId={}", channelId);
        // userId는 나중에 admin 혹은 권한 체크를 위해서 남겨둠

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId));
        channelRepository.deleteById(channelId);
        log.info("[Service] 채널 삭제 성공");
        log.debug("[Service] 채널 삭제 완료 데이터: {}", channel);
        return ChannelDeleteResponse.success(channel);
    }

    private ChannelResponse createChannelByType(Channel channel) {
        Instant lastMessageTime = getLastMessageTime(channel.getId());

        if (ChannelType.PRIVATE.equals(channel.getType())) {
            List<User> participants = readStatusRepository.findUsersByChannelId(channel.getId());
            List<UserResponse> participantResponses = participants.stream()
                    .map(UserResponse::success)
                    .toList();

            return ChannelResponse.fromPrivateChannel(channel, lastMessageTime, participantResponses);
        } else {
            return ChannelResponse.fromPublicChannel(channel, lastMessageTime);
        }
    }

    private Instant getLastMessageTime(UUID channelId) {
        return messageRepository.findLatestMessageTimeByChannelId(channelId)
                .orElse(null);
    }
}
