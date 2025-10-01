package com.sprint.mission.discodeit.domain.entity;

import java.util.Objects;

import com.sprint.mission.discodeit.domain.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.domain.enums.ChannelType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@Entity
@SuperBuilder
public class Channel extends BaseUpdatableEntity {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ChannelType type;
	private String name;
	private String description;

	public Channel(ChannelType channelType, String name, String description) {
		this.type = channelType;
		this.name = name;
		this.description = description;
	}

	public Channel(ChannelType channelType) {
		this.type = channelType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Channel channel = (Channel)o;
		return Objects.equals(id, channel.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}
