package com.sprint.mission.discodeit.domain.entity;

import java.util.Objects;

import com.sprint.mission.discodeit.domain.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.domain.enums.ChannelType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Channel extends BaseUpdatableEntity {

	@Enumerated(EnumType.STRING)
	@JoinColumn(nullable = false)
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
