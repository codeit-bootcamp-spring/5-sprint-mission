package com.sprint.mission.discodeit.repositoryprod.impl;

import com.sprint.mission.discodeit.domain.entityprod.ProdGuild;
import com.sprint.mission.discodeit.repositoryprod.ProdGuildRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Profile("prod")
public class JpaGuildRepository extends JpaBaseRepository<ProdGuild> implements ProdGuildRepository {

    protected JpaGuildRepository() {
        super(ProdGuild.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdGuild> findDiscoverableGuilds() {
        return entityManager.createQuery(
                "SELECT g FROM ProdGuild g WHERE g.discoverable = true AND g.deleted = false",
                ProdGuild.class
        ).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdGuild> findGuildsOwnedByUser(UUID userId) {
        return entityManager.createQuery(
                        "SELECT g FROM ProdGuild g WHERE g.owner.id = :userId AND g.deleted = false",
                        ProdGuild.class
                ).setParameter("userId", userId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdGuild> searchGuilds(String keyword) {
        if (keyword == null || keyword.isBlank()) return List.of();

        return entityManager.createQuery(
                        "SELECT g FROM ProdGuild g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND g.deleted = false",
                        ProdGuild.class)
                .setParameter("keyword", keyword.strip())
                .getResultList();
    }
}