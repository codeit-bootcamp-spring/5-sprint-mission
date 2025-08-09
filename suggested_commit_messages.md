# 추천 Git 커밋 메시지

---

**1. 프로젝트 초기 설정 및 마이그레이션**
*   `feat: Spring Initializr를 통한 프로젝트 초기 설정 및 기본 의존성 추가`
*   `refactor: 기존 Java 프로젝트를 Spring Boot 3.4.0으로 마이그레이션`
*   `docs: application.properties를 yaml 형식으로 변경`
*   `test: DiscodeitApplication main 메서드 실행 및 로그 확인`

**2. Spring Bean 관리 및 IoC/DI 적용**
*   `feat: Repository 및 Service 인터페이스 구현체 Bean 등록`
*   `refactor: JavaApplication의 Service 초기화 로직을 Spring Context 활용으로 대체`
*   `refactor: JavaApplication의 셋업 및 테스트 코드를 DiscodeitApplication으로 이관`
*   `docs: IoC Container, Dependency Injection, Bean 개념 정리 및 PR 첨부`

**3. Lombok 적용**
*   `refactor: 도메인 모델에 @Getter 적용하여 getter 메서드 대체`
*   `refactor: Basic*Service 생성자에 @RequiredArgsConstructor 적용`

**4. 비즈니스 로직 고도화 - 도메인 및 DTO**
*   `feat: 시간 타입 Instant로 통일하여 가독성 및 확장성 개선`
*   `feat: 새로운 도메인 모델 (ReadStatus, UserStatus, BinaryContent) 추가`
*   `feat: DTO 활용을 위한 기본 구조 및 파라미터 그룹화 적용`
*   `feat: 신규 도메인 모델별 Repository 인터페이스 선언`

**5. 서비스별 고도화 (예시)**
*   **UserService:**
    *   `feat(user): 프로필 이미지 선택적 등록 기능 추가 및 DTO 활용`
    *   `feat(user): username, email 중복 검증 로직 추가`
    *   `feat(user): UserStatus 동시 생성 및 관련 도메인 삭제 로직 구현`
    *   `refactor(user): UserService 의존성 주입 (User, BinaryContent, UserStatus Repository)`
*   **AuthService:**
    *   `feat(auth): 사용자 로그인 기능 구현 (username, password 일치 확인)`
    *   `refactor(auth): AuthService 의존성 주입 (User Repository)`
*   **ChannelService:**
    *   `feat(channel): PRIVATE/PUBLIC 채널 생성 메서드 분리 및 DTO 활용`
    *   `feat(channel): 채널 조회 시 최근 메시지 시간 및 참여 User ID 포함`
    *   `feat(channel): 특정 User가 볼 수 있는 채널 목록 조회 (findAllByUserId)`
    *   `refactor(channel): ChannelService 의존성 주입 (Channel, ReadStatus, Message Repository)`
*   **MessageService:**
    *   `feat(message): 메시지 생성 시 다중 첨부파일 선택적 등록 기능 추가`
    *   `feat(message): 특정 채널 메시지 목록 조회 (findAllByChannelId)`
    *   `feat(message): 메시지 삭제 시 첨부파일 (BinaryContent) 동시 삭제`
    *   `refactor(message): MessageService 의존성 주입 (Message, Channel, User, BinaryContent Repository)`
*   **ReadStatusService:**
    *   `feat(readstatus): ReadStatus 생성, 조회, 업데이트, 삭제 기능 구현`
    *   `feat(readstatus): ReadStatus 생성 시 관련 Channel/User 존재 여부 및 중복 확인`
    *   `refactor(readstatus): ReadStatusService 의존성 주입 (ReadStatus, User, Channel Repository)`
*   **UserStatusService:**
    *   `feat(userstatus): UserStatus 생성, 조회, 업데이트, 삭제 기능 구현`
    *   `feat(userstatus): UserStatus 생성 시 관련 User 존재 여부 및 중복 확인`
    *   `feat(userstatus): userId를 통한 UserStatus 업데이트 기능 추가`
    *   `refactor(userstatus): UserStatusService 의존성 주입 (UserStatus, User Repository)`
*   **BinaryContentService:**
    *   `feat(binarycontent): BinaryContent 생성, 조회, 삭제 기능 구현`
    *   `feat(binarycontent): id 목록으로 BinaryContent 조회 기능 추가`
    *   `refactor(binarycontent): BinaryContentService 의존성 주입 (BinaryContent Repository)`

**6. 새로운 도메인 Repository 구현체 구현**
*   `feat: JCF 기반 Repository 구현체 개발`
*   `feat: File 기반 Repository 구현체 개발`
