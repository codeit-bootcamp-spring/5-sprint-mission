package com.sprint.mission.discodeit.domain.entityprod;

import com.sprint.mission.discodeit.domain.enums.ChannelType;
import com.sprint.mission.discodeit.util.Validators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "channels")
public class ProdChannel extends ProdBaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType type;

    @Setter
    @Column(nullable = false)
    private boolean isPrivate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id", nullable = false)
    private ProdGuild guild;

    @ManyToMany
    @JoinTable(
            name = "channel_joined_users",
            joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uq_channel_joined_users_channel_user",
                    columnNames = {"channel_id", "user_id"}
            )
    )
    private Set<ProdUser> jpaUsers = new HashSet<>();

    public ProdChannel(ProdGuild guild, String name, ChannelType type) {
        this.guild = Objects.requireNonNull(guild, "Guild must not be null");
        setName(name);
        setType(type);
    }

    public void setName(String name) {
        this.name = Validators.validateChannelName(name);
    }

    public void setType(ChannelType type) {
        this.type = Objects.requireNonNull(type, "Channel type must not be null");
    }

    public Set<ProdUser> getJpaUsers() {
        return Collections.unmodifiableSet(jpaUsers);
    }

    public boolean addUser(ProdUser jpaUser) {
        return jpaUsers.add(Objects.requireNonNull(jpaUser, "User must not be null"));
    }

    public boolean removeUser(ProdUser jpaUser) {
        return jpaUsers.remove(Objects.requireNonNull(jpaUser, "User must not be null"));
    }

    @Override
    public String toString() {
        return String.format("Channel[id=%s, name=%s, type=%s, isPrivate=%s, userCount=%d]",
                getId(), name, type, isPrivate, jpaUsers.size());
    }
}
