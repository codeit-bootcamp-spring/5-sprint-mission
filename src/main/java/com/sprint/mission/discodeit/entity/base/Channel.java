package com.sprint.mission.discodeit.entity.base;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name="channels")
@Getter
public class Channel extends BaseUpdatableEntity{
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private ChannelType type;
    @Column(name="name", length = 100)
    private String name;
    @Column(name="description", length = 500)
    private String description;
}
