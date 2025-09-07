package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Locked.Read;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@Entity
public class Channel extends BaseUpdatableEntity {
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    private ChannelType type;

    private String name;

    private String description;

    @OneToMany(mappedBy = "channel")
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "channel")
    private List<ReadStatus> readStatuses = new ArrayList<>();

    public void update(String newName, String newDescription) {
        if(newName != null && !newName.equals(this.name)) {
            this.name = newName;
        }
        if(newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
        }
    }
}
