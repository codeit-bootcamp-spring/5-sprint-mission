package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelRepository {

  // 채널 객체 받아 저장
  void save(Channel channel);

  //UUID로 채널 조회
  Channel findById(UUID id);

  //전체 채널 목록 리스트로 반환
  List<Channel> findAll();

  //채널 받아 DB 업데이트
  void update(Channel channel);

  //채널 삭제
  void delete(UUID id);
}
