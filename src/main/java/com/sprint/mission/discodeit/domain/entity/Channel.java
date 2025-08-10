package com.sprint.mission.discodeit.domain.entity;

import com.sprint.mission.discodeit.domain.enums.channel.ChannelType;
import com.sprint.mission.discodeit.util.Validators;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity {

    private String name;
    private ChannelType type;
    private boolean isPrivate;
    private final UUID guildId;
    private final Set<UUID> userIds = new HashSet<>();

    public Channel(UUID guildId, String name, ChannelType type) {
        this.guildId = Objects.requireNonNull(guildId, "Guild id must not be null");
        setName(name);
        setType(type);
    }

    public void setName(String name) {
        String v = Validators.validateChannelName(name);
        if (!Objects.equals(this.name, v)) {
            this.name = v;
            touch();
        }
    }

    public void setType(ChannelType type) {
        Objects.requireNonNull(type, "Channel type must not be null");
        if (this.type != type) {
            this.type = type;
            touch();
        }
    }

    public void setPrivate(boolean isPrivate) {
        if (this.isPrivate != isPrivate) {
            this.isPrivate = isPrivate;
            touch();
        }
    }

    public Set<UUID> getUserIds() {
        return Collections.unmodifiableSet(userIds);
    }

    public boolean hasUser(UUID userId) {
        return userIds.contains(Objects.requireNonNull(userId, "User id must not be null"));
    }

    public int userCount() {
        return userIds.size();
    }

    public boolean addUser(UUID userId) {
        boolean added = userIds.add(Objects.requireNonNull(userId, "User id must not be null"));
        if (added) touch();
        return added;
    }

    public boolean addUsers(Collection<UUID> userIds) {
        Objects.requireNonNull(userIds, "User ids must not be null");
        boolean changed = false;
        for (UUID id : userIds) {
            if (id == null) throw new NullPointerException("User id must not be null");
            if (this.userIds.add(id)) changed = true;
        }
        if (changed) touch();
        return changed;
    }

    public boolean removeUser(UUID userId) {
        boolean removed = userIds.remove(Objects.requireNonNull(userId, "User id must not be null"));
        if (removed) touch();
        return removed;
    }

    public boolean removeUsers(Collection<UUID> userIds) {
        Objects.requireNonNull(userIds, "User ids must not be null");
        boolean changed = false;
        for (UUID id : userIds) {
            if (id == null) throw new NullPointerException("User id must not be null");
            if (this.userIds.remove(id)) changed = true;
        }
        if (changed) touch();
        return changed;
    }

    @Override
    public String toString() {
        return String.format(
                "Channel[id=%s, name=%s, type=%s, isPrivate=%s, userCount=%d]",
                getId(), name, type, isPrivate, userIds.size()
        );
    }
}
