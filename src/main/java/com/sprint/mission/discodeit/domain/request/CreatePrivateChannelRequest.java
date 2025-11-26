package com.sprint.mission.discodeit.domain.request;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePrivateChannelRequest {
	@NotNull
	@Size(min = 1)
	private final List<UUID> participantIds;

	@JsonCreator
	public CreatePrivateChannelRequest(
	  @JsonProperty("participantIds") List<UUID> participantIds
	) {
		this.participantIds = participantIds;
	}
}
