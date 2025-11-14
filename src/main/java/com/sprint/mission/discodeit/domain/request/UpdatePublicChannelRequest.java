package com.sprint.mission.discodeit.domain.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter

public class UpdatePublicChannelRequest {
	private final String newName;
	private final String newDescription;

	@JsonCreator
	public UpdatePublicChannelRequest(
	  @JsonProperty("newName") String newName,
	  @JsonProperty("newDescription") String newDescription) {
		this.newName = newName;
		this.newDescription = newDescription;
	}
}
