package com.sprint.mission.discodeit.domain.entityprod;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"sender", "receiver"}, callSuper = false)
@Entity
@Table(name = "friend_requests", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sender_id", "receiver_id"})
})
public class ProdFriendRequest extends ProdBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private ProdUser sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private ProdUser receiver;

    public ProdFriendRequest(ProdUser sender, ProdUser receiver) {
        validateUsers(sender, receiver);
        this.sender = sender;
        this.receiver = receiver;
    }

    private static void validateUsers(ProdUser sender, ProdUser receiver) {
        if (sender == null || receiver == null)
            throw new IllegalArgumentException("Sender and receiver must not be null.");
        if (sender.equals(receiver)) throw new IllegalArgumentException("Cannot send friend request to self.");
    }

    @Override
    public String toString() {
        return String.format("FriendRequest[from=%s, to=%s]",
                sender != null ? sender.getId() : "null",
                receiver != null ? receiver.getId() : "null");
    }
}
