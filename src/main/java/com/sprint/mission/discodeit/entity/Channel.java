package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.*;

public class Channel {
    private final UUID id;
    private UUID ownerId;

    private final Long createAt;
    private Long modifyAt;

    private final List<User> members;
    private String channelName;
    private boolean nsfw;

    public Channel(User user, String channelName, boolean nsfw) {
        this.id = UUID.randomUUID();
        Instant now = Instant.now();
        this.createAt = now.getEpochSecond();

        this.ownerId = user.getId();
        this.channelName = channelName;
        this.nsfw = nsfw;
        this.members = new ArrayList<>();
        members.add(user);
    }

    public UUID getId() {
        return id;
    }

    public long getCreateAt() {
        return createAt;
    }

    public long getModifyAt() {
        return modifyAt;
    }

    public UUID getOwnerId() { return ownerId; }

    public List<User> getMembers() { return members; }

    public String getChannelName() { return channelName; }

    public boolean isNsfw() { return nsfw; }

    public void updateModifyAt() {
        Instant now = Instant.now();
        this.modifyAt = now.getEpochSecond();
    }

    public List<User> addMembers(User user) throws NullPointerException {
        if(user == null) {
            throw new NullPointerException("user is null.");
        }
        Iterator<User> iterator = members.iterator();
        while(iterator.hasNext()) {
            User member = iterator.next();
            if(member.getId().equals(user.getId())) {
                System.out.println("[alarm] : The user already exists in the channel.");
                return members;
            }
        }
        members.add(user);
        updateModifyAt();
        return members;
    }

    public void update(ChannelDTO channelDTO) throws IllegalArgumentException {
        if(this.ownerId.equals(channelDTO.getOwnerId())) {
            System.out.println("[Alarm] : The original channel owner and the owner to be changed are the same.");
        } else {
            int count = 0;
            for (User user : members) {
                if (user.getId().equals(channelDTO.getOwnerId())) {
                    this.ownerId = channelDTO.getOwnerId();
                    break;
                }
                count++;
            }
            if (count == members.size()) {
                throw new IllegalArgumentException("User does not exist in this channel.");
            }
        }
        this.channelName = channelDTO.getChannelName();
        this.nsfw = channelDTO.isNsfw();

        updateModifyAt();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Channel{");
        sb.append("id=").append(id);
        sb.append(", channelName='").append(channelName).append('\'');
        sb.append(", ownerId=").append(ownerId);

        sb.append(", \nmembers=[");
//        .append(members);
        for(User user : members){
            sb.append("\n\t").append(user);
        }
        sb.append("\n],");
        sb.append("\n nsfw=").append(nsfw);
        sb.append(", createAt=").append(createAt);
        sb.append(", modifyAt=").append(modifyAt);
        sb.append('}');
        return sb.toString();
    }
}
