package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.CreateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.ChannelAccessibility;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    /**
     * 새로운 채널을 생성합니다.
     * @param request 채널 생성 요청 정보
     * @return 생성된 채널 응답 DTO
     */
    ChannelResponse createChannel(CreateChannelRequest request);

    /**
     * 채널 ID로 채널을 조회합니다.
     * @param channelId 조회할 채널 UUID
     * @return 채널 응답 DTO (없으면 Optional.empty)
     */
    Optional<ChannelResponse> getById(UUID channelId);

    /**
     * 채널명으로 채널을 조회합니다
     * @param channelName 검색할 채널명
     * @return 일치하는 채널 목록
     */
    List<ChannelResponse> getByChannelName(String channelName);

    /**
     * 특정 사용자가 볼 수 있는 채널 목록을 조회합니다.
     * <p>
     * - PUBLIC 채널: 전체 포함<br>
     * - PRIVATE 채널: 해당 사용자가 참여한 채널만 포함<br>
     *
     * @param userId 조회 대상 사용자 ID
     * @return 사용자가 볼 수 있는 채널 목록
     */
    List<ChannelResponse> findAllByUserId(UUID userId);

    /**
     * 모든 채널을 조회합니다.
     * @return 전체 채널 목록
     */
    List<ChannelResponse> getAll();

    /**
     * 채널을 수정합니다.
     * <p>
     * - PRIVATE 채널은 수정할 수 없습니다.<br>
     * - 요청에 포함된 값만 부분 업데이트합니다. (null은 변경 없음)
     *
     * @param request 수정 요청 정보 (수정 대상 ID 포함)
     * @return 수정된 채널 응답 DTO
     */
    ChannelResponse update(UpdateChannelRequest request);

    /**
     * 채널을 삭제합니다.
     * @param channelId 삭제할 채널의 ID
     * @return 삭제 성공 여부
     */
    boolean removeById(UUID channelId);
}
