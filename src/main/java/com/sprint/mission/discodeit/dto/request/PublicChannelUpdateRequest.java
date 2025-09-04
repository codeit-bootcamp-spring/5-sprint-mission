package com.sprint.mission.discodeit.dto.request;

public record PublicChannelUpdateRequest(
    /*@NotBlank*/ String newName,
                  String newDescription
) {

}
