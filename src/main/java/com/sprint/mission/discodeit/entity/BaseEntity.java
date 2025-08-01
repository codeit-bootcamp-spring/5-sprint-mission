    package com.sprint.mission.discodeit.entity;

    import lombok.Getter;

    import java.io.Serializable;
    import java.time.Instant;
    import java.util.UUID;

    @Getter
    public class BaseEntity implements Serializable {
        protected static final long serialVersionUID = 1L;
        protected UUID id;
        protected Instant createdAt;
        protected Instant updatedAt;

        public BaseEntity() {
            this.id = UUID.randomUUID();
            this.createdAt = Instant.now();
        }

        public void updateTimestamp() {
            this.updatedAt = Instant.now();
        }
    }
