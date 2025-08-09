package com.sprint.mission.discodeit.domain.entitydev;

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
public class DevChannel extends DevBaseEntity {

    private String name;
    private ChannelType type;
    private boolean isPrivate;
    private final UUID guild;
    private final Set<UUID> users = new HashSet<>();

    public DevChannel(UUID guild, String name, ChannelType type) {
        this.guild = Objects.requireNonNull(guild, "Guild id must not be null");
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
        if (this.type != type) { // enum 비교
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

    public Set<UUID> getUsers() {
        return Collections.unmodifiableSet(users);
    }

    public boolean hasUser(UUID userId) {
        return users.contains(Objects.requireNonNull(userId, "User id must not be null"));
    }

    public int userCount() {
        return users.size();
    }

    public boolean addUser(UUID userId) {
        boolean added = users.add(Objects.requireNonNull(userId, "User id must not be null"));
        if (added) touch();
        return added;
    }

    public boolean addUsers(Set<UUID> userIds) {
        Objects.requireNonNull(userIds, "User ids must not be null");
        boolean changed = false;
        for (UUID id : userIds) {
            if (id == null) throw new NullPointerException("User id must not be null");
            if (users.add(id)) changed = true;
        }
        if (changed) touch();
        return changed;
    }

    public boolean removeUser(UUID userId) {
        boolean removed = users.remove(Objects.requireNonNull(userId, "User id must not be null"));
        if (removed) touch();
        return removed;
    }

    public boolean removeUsers(Collection<UUID> userIds) {
        Objects.requireNonNull(userIds, "User ids must not be null");
        boolean changed = false;
        for (UUID id : userIds) {
            if (id == null) throw new NullPointerException("User id must not be null");
            if (users.remove(id)) changed = true;
        }
        if (changed) touch();
        return changed;
    }

    @Override
    public String toString() {
        return String.format(
                "Channel[id=%s, name=%s, type=%s, isPrivate=%s, userCount=%d]",
                getId(), name, type, isPrivate, users.size()
        );
    }
}
