package com.sprint.mission.discodeit.entity;

import java.util.UUID;

//엔티티
public class Channel {
    //필드
    private UUID id; //회원 한명한명의 고유 id
    private Long createAt; //객체 생성시간
    private Long updateAt; //객체 수정시간

    //생성자 주입
    //파라미터로 값을 받지않고 생성자 내부에서 직접 초기화
    public Channel() {
        this.id = UUID.randomUUID();
        this.createAt = System.currentTimeMillis();
        this.updateAt = createAt; //처음 생성시 수정시간을 생성시간으로 맞춰줌
    }

    //Getter
    public UUID getId() {
        return id;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

   //메서드
   public void updateTime() {
        this.updateAt = System.currentTimeMillis();
   }


   //toString
   //다른 파일에서 객체 출력하기 위해
    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                '}';
    }
}
