package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 생성
    Channel create(String name);
    // 조회(하나만)
    Channel get(String name);
    // 조회(전체)
    List<Channel> getAll();
    // 채녈 이름 수정
    boolean update(UUID uuid, String name);
    // 채널 삭제
    boolean delete(UUID uuid);
}
