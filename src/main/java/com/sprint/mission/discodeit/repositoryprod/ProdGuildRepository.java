package com.sprint.mission.discodeit.repositoryprod;

import com.sprint.mission.discodeit.domain.entityprod.guild.ProdGuild;

import java.util.List;
import java.util.UUID;

public interface ProdGuildRepository extends ProdBaseRepository<ProdGuild> {

    List<ProdGuild> findDiscoverableGuilds();

    List<ProdGuild> findGuildsOwnedByUser(UUID userId);

    List<ProdGuild> searchGuilds(String keyword);
}
