package com.sprint.mission.discodeit.domain.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePublicChannelRequest {
	@NotNull
	@NotBlank
	private final String name;
	@NotNull
	@NotBlank
	private final String description;

	@JsonCreator
	public CreatePublicChannelRequest(
	  @JsonProperty("name") String name,
	  @JsonProperty("description") String description
	) {
		this.name = name;
		this.description = description;
	}

}
