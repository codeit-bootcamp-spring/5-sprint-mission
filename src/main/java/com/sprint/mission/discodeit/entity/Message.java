package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private Long createAt;
    private Long updateAt;

    //내가 디스코드에서 필요한 필드를 추가적으로 설계하는 자리
    private String content;
    private UUID channelId;
    private UUID authorId;

    //추가적인 정보들, 공지, ... 창의적으로 생각해서

    //권한, 이메일, 이후 필요한 정보를 더 선언해도 된다

    public Message(UUID id) {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

//    public static void main(String[] args) {
//        System.out.println("Hello World");
//    }
}
