package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.domain.entity.Guild;
import com.sprint.mission.discodeit.domain.entity.GuildPermissions;
import com.sprint.mission.discodeit.dto.response.guild.GuildPermissionsResponse;
import com.sprint.mission.discodeit.dto.response.guild.GuildResponse;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GuildMapper {

  public static GuildResponse toGuildResponse(Guild g) {
    if (g == null) {
      return null;
    }

    Set<GuildPermissionsResponse> perms = g.getPermissions().stream()
        .map(GuildMapper::toGuildPermissionsResponse)
        .collect(Collectors.toUnmodifiableSet());

    return new GuildResponse(
        g.getId(),
        g.getCreatedAt(),
        g.getUpdatedAt(),
        g.getName(),
        g.isDiscoverable(),
        g.getOwnerId(),
        g.getUserIds(),
        g.getChannelIds(),
        g.getBannedUserIds(),
        perms
    );
  }

  private static GuildPermissionsResponse toGuildPermissionsResponse(GuildPermissions gp) {
    return new GuildPermissionsResponse(
        gp.getUserId(),
        gp.getPermissions()
    );
  }
}
