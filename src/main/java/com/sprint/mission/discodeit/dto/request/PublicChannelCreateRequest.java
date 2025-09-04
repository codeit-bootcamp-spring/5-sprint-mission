package com.sprint.mission.discodeit.dto.request;

public record PublicChannelCreateRequest(
    /*@NotBlank*/ String name,
                  String description
) {

}
