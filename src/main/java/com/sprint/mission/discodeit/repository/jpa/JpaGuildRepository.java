package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.domain.entity.guild.Guild;
import com.sprint.mission.discodeit.repository.GuildRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Profile("prod")
public class JpaGuildRepository extends JpaBaseRepository<Guild> implements GuildRepository {

    protected JpaGuildRepository() {
        super(Guild.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Guild> findDiscoverableGuilds() {
        return entityManager.createQuery(
                "SELECT g FROM Guild g WHERE g.discoverable = true AND g.deleted = false",
                Guild.class
        ).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Guild> findGuildsOwnedByUser(UUID userId) {
        return entityManager.createQuery(
                        "SELECT g FROM Guild g WHERE g.owner.id = :userId AND g.deleted = false",
                        Guild.class
                ).setParameter("userId", userId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Guild> searchGuilds(String keyword) {
        if (keyword == null || keyword.isBlank()) return List.of();

        return entityManager.createQuery(
                        "SELECT g FROM Guild g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND g.deleted = false",
                        Guild.class)
                .setParameter("keyword", keyword.strip())
                .getResultList();
    }
}