package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sprint.mission.discodeit.entity.common.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "channels")
@Getter @Setter @SuperBuilder /*@ToString*/
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseUpdatableEntity implements Serializable {
//	@Serial
//	private static final long serialVersionUID = 1L;

    @Column(nullable = false)
	private ChannelType type = ChannelType.PUBLIC; // 채널 타입, PUBLIC 또는 PRIVATE
    @Column(unique = true, nullable = false, length = 100)
	private String name;
    @Column(length = 1000)
	private String description;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReadStatus> readStatus;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Message> messages;


	public Channel(String name, String description) {
		this.name = Objects.requireNonNull(name, "채널 이름은 필수 입력값입니다.");
		this.description = description;
	}

	public Channel(List<UUID> userUUIDs) {
		type = ChannelType.PRIVATE;
		name = "private-"+super.getId();
	}

	public Channel(Channel original) {
        super(original.getId(), original.getCreatedAt(), original.getUpdatedAt());
		this.name = original.name;
		this.type = original.type;
		this.description = original.description;
        this.readStatus = original.readStatus;
        this.messages = original.messages;
	}

	public Channel copy() {
		return new Channel(this);
	}
}
