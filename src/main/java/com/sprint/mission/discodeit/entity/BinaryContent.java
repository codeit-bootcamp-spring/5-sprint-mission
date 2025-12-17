package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sprint.mission.discodeit.entity.common.BaseEntity;
import com.sprint.mission.discodeit.entity.common.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Entity
@Table(name = "binary_contents")
@Getter
@Setter
@SuperBuilder /*@ToString*/
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContent extends BaseUpdatableEntity implements Serializable {
//	@Serial
//	private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    private String contentType;
    @Column(nullable = false)
    private Long size;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BinaryContentStatus status;


    public BinaryContent(String fileName, String contentType, Long size) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.status = BinaryContentStatus.PROCESSING;
    }

    private BinaryContent(BinaryContent original) {
        super(original.getId(), original.getCreatedAt(), original.getUpdatedAt());
        this.fileName = original.fileName;
        this.contentType = original.contentType;
        this.size = original.size;
        this.status = original.status;
    }

    public BinaryContent copy() {
        return new BinaryContent(this);
    }

    public void updateStatus(BinaryContentStatus status) {
        this.status = status;
    }
}
