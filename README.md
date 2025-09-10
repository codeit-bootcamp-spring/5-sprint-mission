# 5-spring-mission

resources

application.yaml
schema.sql 

base entity

entity/base/BaseEntity.java [수정]
entity/base/BaseUpdatableEntity.java [수정]

entity

entity/User.java [수정]
entity/UserStatus.java [수정]
entity/BinaryContent.java [수정]

storage

storage/BinaryContentStorage.java [추가]
storage/local/LocalDiskBinaryContentStorage.java [추가]

controller

controller/BinaryContentController.java [수정]
controller/AuthController.java [수정]
controller/UserController.java [수정] 
controller/ReadStatusController.java [추가]

service

service/UserStatusService.java [수정]
service/basic/BasicUserStatusService.java [수정] 
service/ReadStatusService.java [추가]
service/basic/BasicReadStatusService.java [추가]
service/basic/BasicAuthService.java [수정]

repository

repository/ReadStatusRepository.java [수정]

dto

dto/request/UserStatusUpdateRequest.java [수정]
dto/request/ReadStatusCreateRequest.java [추가]
dto/request/ReadStatusUpdateRequest.java [추가]
dto/data/ReadStatusDto.java [추가]

mapper

mapper/BinaryContentMapper.java [추가]
mapper/ChannelContentMapper.java [추가]
mapper/UserStatusContentMapper.java [추가]
mapper/UserMapper.java [추가]
mapper/MessageMapper.java [추가]
mapper/ReadStatusMapper.java [추가]


exception

exception/GlobalExceptionHandler.java [수정]