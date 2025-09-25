# spring-mission-7
스프린트 미션 7 답안 리포지토리입니다.

### 메인 패키지 구조
```yaml
com.sprint.mission.discodeit
 ├── common               # 페이징 관련 Dto & Mapper
 │    ├── dto         
 │    └── mapper
 │
 ├── config
 │    ├── AppConfig.java
 │    ├── MDCLoggingInterceptor.java
 │    ├── SwaggerConfig.java
 │    └── WebMvcConfig.java
 │
 ├── domain
 │    ├── auth
 │    ├── binarycontent
 │    ├── channel
 │    ├── readstatus
 │    ├── user
 │    └── userstatus
 │ 
 ├── entity
 │    ├── base                # 공통 엔티티 (상속용)
 │    │    └── BaseEntity.java
 │    │
 │    ├── User.java
 │    ├── UserStatus.java
 │    ├── Channel.java
 │    ├── ChannelType.java    # enum
 │    ├── Message.java
 │    ├── ReadStatus.java
 │    └── BinaryContent.java
 │
 ├── exception
 │    ├── GlobalExceptionHandler.java   # 글로벌 예외 처리
 │    ├── DiscodeitException.java
 │    ├── ErrorResponse.java
 │    └── ErrorCode.java      # enum
 │
 ├── storage           # 저장소 추상화
 │    ├── local
 │    └── BinaryContentStorage
 │
 └── DiscodeitApplication.java
```

### domain 패키지 구조
```yaml
domain            # 도메인 단위 패키지
├── auth
│    ├── dto
│    ├── AuthApi.java
│    ├── AuthController.java
│    └── AuthService.java
│
├── user
│    ├── dto
│    ├── exception
│    ├── mapper
│    ├── UserApi.java
│    ├── UserController.java
│    ├── UserRepository.java
│    └── UserService.java
│
├── channel
│    ├── dto
│    ├── exception
│    ├── mapper
│    ├── ChannelApi.java
│    ├── ChannelController.java
│    ├── ChannelRepository.java
│    └── ChannelService.java
│
├── message
│    ├── dto
│    ├── mapper
│    ├── MessageApi.java
│    ├── MessageController.java
│    ├── MessageRepository.java
│    └── MessageService.java
│
├── readstatus
│    ├── dto
│    ├── mapper
│    ├── ReadStatusApi.java
│    ├── ReadStatusController.java
│    ├── ReadStatusRepository.java
│    └── ReadStatusService.java
│
├── userstatus
│    ├── dto
│    ├── mapper
│    ├── UserStatusRepository.java
│    └── UserStatusService.java
│
└── binarycontent
├── dto
├── mapper
├── BinaryContentApi.java
├── BinaryContentController.java
├── BinaryContentRepository.java
└── BinaryContentService.java
```

