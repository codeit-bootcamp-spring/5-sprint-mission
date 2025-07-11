package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.UUID;

public interface ChannelService {

    void save(Channel channel);

    //읽기
    Channel find(UUID id);

    //모두 읽기
    ArrayList<Channel> allFind();

    //업데이트
    void update(UUID id, Channel channel);

    //삭제
    void delete(UUID id);
}
