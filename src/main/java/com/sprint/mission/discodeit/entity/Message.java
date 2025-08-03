package com.sprint.mission.discodeit.entity;

import java.util.UUID;

    private UUID id;
    private String content;
    private UUID authorId;

        this.id = UUID.randomUUID();
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getContent() {
        return content;
    }

    public UUID getAuthorId() {
        return authorId;
    }

        }

    }
}
