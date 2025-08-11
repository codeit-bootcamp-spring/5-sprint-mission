package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public abstract class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    protected final UUID id;
    protected Instant createdAt;
    protected Instant updatedAt;

    //기본생성자
    public BaseEntity() {
        this.id=UUID.randomUUID(); //고유식별자 생성
        this.createdAt=Instant.now(); //생성시간
        this.updatedAt=this.createdAt;//초기수정시간은 생성시간과 같게
    }

    public void updateTimestamp(){
        this.updatedAt=Instant.now();
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
