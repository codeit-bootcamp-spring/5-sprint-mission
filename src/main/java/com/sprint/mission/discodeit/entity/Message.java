package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    // 내가 디스 코드에서 필요한 필드를 추가적으로 설계하는 자리
    private String content;

    // 소속된 채널과 글쓴이 표현
    private UUID channelId;
    private UUID authorId;

    // 필요한 나머지 설계 (공지, ....)




}
