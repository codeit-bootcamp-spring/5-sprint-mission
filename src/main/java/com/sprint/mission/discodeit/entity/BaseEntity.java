package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public abstract class BaseEntity {
    private UUID id;
    private long createdAt;
    private long updatedAt;

    //기본생성자
    public BaseEntity() {
        this.id=UUID.randomUUID(); //고유식별자 생성
        this.createdAt=System.currentTimeMillis();//생성시간
        this.updatedAt=this.createdAt;//초기수정시간은 생성시간과 같게
    }


    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }
    public void update(){ //파라미터 추가 필요
        this.updatedAt=System.currentTimeMillis();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BaseEntity{");
        sb.append("id=").append(id);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append('}');
        return sb.toString();
    }
}
