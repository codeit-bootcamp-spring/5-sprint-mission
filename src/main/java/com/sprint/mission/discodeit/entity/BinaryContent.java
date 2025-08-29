package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sprint.mission.discodeit.entity.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Entity
@Table(name = "binary_contents")
@Getter @Setter @SuperBuilder /*@ToString*/
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContent extends BaseEntity implements Serializable {
//	@Serial
//	private static final long serialVersionUID = 1L;

    @Column(nullable = false)
	private String fileName;
    @Column(nullable = false)
	private String contentType;
    @Column(nullable = false)
	private Long size;
    @Column(nullable = false)
	private byte[] bytes;

	public BinaryContent(String fileName, String contentType, Long size, byte[] bytes) {
		this.fileName = fileName;
		this.contentType = contentType;
		this.size = size;
		this.bytes = bytes;
	}

	private BinaryContent(BinaryContent original) {
        super(original.getId(), original.getCreatedAt());
		this.fileName = original.fileName;
		this.contentType = original.contentType;
		this.size = original.size;
		this.bytes = original.bytes;
	}

	public BinaryContent copy(){
		return new BinaryContent(this);
	}
}
