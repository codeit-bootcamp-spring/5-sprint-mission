package com.sprint.mission.discodeit.service;

import java.util.List; // List 임포트
import java.util.UUID; // UUID 임포트
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest; // DTO 임포트
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;

public interface ChannelService {
    Channel create(PublicChannelCreateRequest request); // 오버로드 방식으로 매개변수만 다르게함
    Channel create(PrivateChannelCreateRequest request); // 오버로드 방식으로 매개변수만 다르게함
    ChannelDto find(UUID channelId); // 반환 타입이 ChannelDto: 외부 응답에 적합한 형태(엔티티 노출 방지)
    List<ChannelDto> findAllByUserId(UUID userId); // 특정 사용자에게 연관된 채널 목록 조회
    Channel update(UUID channelId, PublicChannelUpdateRequest request); // 채널 수정. 지금은 공개 채널 수정 DTO만 받음
    void delete(UUID channelId); // 채널 삭제
}
