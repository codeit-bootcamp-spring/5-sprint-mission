package com.sprint.mission.discodeit.service;

//인터페이스
//기능의 약속을 정의하며, 다중 구현이 가능

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  //채널 객체 받아서 DB에 저장
  void create(Channel channel); //저장

  //UUID로 채널 찾기
  Channel findById(UUID id);

  //모든 채널 리스트로 조회
  List<Channel> findAll();

  //채널 정보 수정
  void update(UUID id, PublicChannelUpdateRequest channel); // 채널 수정

  //채널 삭제
  void delete(UUID id);

  //비공개 채널 생성
  void createPrivateChannel(PrivateChannelCreateRequest request);

  //공개 채널 생성
  void createPublicChannel(PublicChannelCreateRequest request);


}
