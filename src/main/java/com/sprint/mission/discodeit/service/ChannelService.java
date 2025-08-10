package com.sprint.mission.discodeit.service; // 서비스 패키지

import java.util.List; // List 임포트
import java.util.UUID; // UUID 임포트
import com.sprint.mission.discodeit.service.dto.channel.CreatePublicChannelRequest; // DTO 임포트
import com.sprint.mission.discodeit.service.dto.channel.CreatePrivateChannelRequest; // DTO 임포트
import com.sprint.mission.discodeit.service.dto.channel.UpdateChannelRequest; // DTO 임포트
import com.sprint.mission.discodeit.service.dto.channel.ChannelView; // DTO 임포트

public interface ChannelService { // 인터페이스 선언
    ChannelView createPublic(CreatePublicChannelRequest request); // PUBLIC 채널 생성
    ChannelView createPrivate(CreatePrivateChannelRequest request); // PRIVATE 채널 생성
    ChannelView find(UUID channelId); // 단건 조회(최신 메시지, PRIVATE 참여자 포함)
    List<ChannelView> findAllByUserId(UUID userId); // 사용자 기준 조회(PUBLIC 전부 + PRIVATE 내가 포함된 것)
    ChannelView update(UpdateChannelRequest request); // 채널 수정(PUBLIC만)
    void delete(UUID channelId); // 삭제(Message/ReadStatus 연쇄 삭제)
}
