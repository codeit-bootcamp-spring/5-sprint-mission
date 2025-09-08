package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "channels")
public class Channel extends BaseUpdatableEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType type;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    protected Channel() {} // 왜 쓰는가?

    public Channel(ChannelType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    // update 보조 메서드 추가
    public void update(String newName, String newDescription) {
        if (newName != null && !newName.equals(this.name)) this.name = newName;
        if (newDescription != null && !newDescription.equals(this.description)) this.description = newDescription;
    }


}
