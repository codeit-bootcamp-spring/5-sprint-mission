# 5-spring-mission

설정/DDL

main/resources/application.yaml
↳ PostgreSQL 연결, JPA/Auditing 로그, OSIV=false, storage(local) 설정.

main/resources/schema.sql
↳ ERD 기반 DDL(ENUM channel_type, FK/UK, 인덱스).

베이스 엔티티 (Auditing)

com/sprint/mission/discodeit/entity/base/BaseEntity.java [수정]
↳ @CreatedDate, UUID 자동 생성(@PrePersist).

com/sprint/mission/discodeit/entity/base/BaseUpdatableEntity.java [수정]
↳ @LastModifiedDate 추가.

엔티티(연관관계/파일메타)

com/sprint/mission/discodeit/entity/User.java [수정]
↳ @OneToOne(mappedBy="user", cascade=ALL, orphanRemoval=true)로 status 매핑 정정.

com/sprint/mission/discodeit/entity/UserStatus.java [수정]
↳ @OneToOne @JoinColumn(name="user_id", unique=true)(연관관계 주인) + update(Instant).

com/sprint/mission/discodeit/entity/BinaryContent.java [수정]
↳ 메타 전용(파일명/크기/타입) 모델로 정리(바이트는 스토리지로 이동).

스토리지(바이너리 외부화)

com/sprint/mission/discodeit/storage/BinaryContentStorage.java [추가]
↳ put(UUID, byte[]), get(UUID), download(BinaryContentDto).

(로컬 구현) com/sprint/mission/discodeit/storage/local/LocalDiskBinaryContentStorage.java [추가]
↳ root-path 초기화, UUID 경로 규칙, ResponseEntity<Resource> 다운로드.

컨트롤러

com/sprint/mission/discodeit/controller/BinaryContentController.java [수정]
↳ GET /api/binaryContents/{id}/download → Storage에 위임.

com/sprint/mission/discodeit/controller/AuthController.java [수정]
↳ 로그인 응답을 UserDto로 반환(매퍼 사용).

com/sprint/mission/discodeit/controller/UserController.java [수정]

회원가입/수정: multipart 처리, UserDto 반환.

/{userId}/userStatus: PUT|PATCH, @RequestBody(required=false)로 바디 없이도 동작, UserDto 반환.

com/sprint/mission/discodeit/controller/ReadStatusController.java [추가]
↳ 생성/수정/userId로 조회 API.

서비스

com/sprint/mission/discodeit/service/basic/BasicAuthService.java [수정]
↳ 인증 실패 시 ResponseStatusException(401)로 명확화.

com/sprint/mission/discodeit/service/UserStatusService.java [수정]
↳ updateByUserId(UUID, UserStatusUpdateRequest)를 업서트 용도로 정의(바디 null 허용).

com/sprint/mission/discodeit/service/basic/BasicUserStatusService.java [수정]
↳ 업서트 구현: 없으면 생성, 있으면 lastActiveAt 단조 증가로 갱신.

com/sprint/mission/discodeit/service/ReadStatusService.java [추가],
com/sprint/mission/discodeit/service/basic/BasicReadStatusService.java [추가]
↳ 읽음 상태 CRUD(+userId 조회).

레포지토리

com/sprint/mission/discodeit/repository/ReadStatusRepository.java [추가]
↳ findAllByUser_Id, findByUser_IdAndChannel_Id, deleteAllByChannel_Id 등.

(기존) UserStatusRepository에 existsByUser_Id, findByUser_Id 활용.

DTO / 매퍼

dto/request/UserStatusUpdateRequest.java [수정]
↳ 필드 optional로 변경(바디 없이 호출 가능).

dto/request/ReadStatusCreateRequest.java, ReadStatusUpdateRequest.java [추가]

dto/data/ReadStatusDto.java [추가]

mapper/ReadStatusMapper.java [추가]

mapper/BinaryContentMapper.java [수정]
↳ null-safe 단건/리스트 변환(메타만 매핑).

mapper/UserMapper.java [수정]
↳ null-safe, online 오버로드, 리스트 변환 유틸.

mapper/MessageMapper.java [수정]
↳ null-safe(channel/author/attachments).

예외 처리

com/sprint/mission/discodeit/exception/GlobalExceptionHandler.java [수정]
↳ ResponseStatusException 원래 상태코드 유지,
IllegalArgumentException(400), NoSuchElementException(404),
HttpMessageNotReadableException(400) 추가, 최종만 500.
