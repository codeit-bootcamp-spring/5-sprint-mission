package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@IdClass(ChannelParticipantId.class)
@Table(name = "channel_participants")
public class ChannelParticipant {

    @Id
    @EqualsAndHashCode.Include
    private UUID channelId;

    @Id
    @EqualsAndHashCode.Include
    private UUID userId;

    @Override
    public String toString() {
        return "ChannelParticipant[channelId=%s, userId=%s]"
            .formatted(channelId, userId);
    }
}
