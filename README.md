# 5-sprint-mission

- 작성자 : 박주환

# mission-1

## 요구사항

### 프로젝트 초기화

- [x] IntelliJ를 통해 다음의 조건으로 Java 프로젝트를 생성합니다.
- [x] IntelliJ에서 제공하는 프로젝트 템플릿 중 Java를 선택합니다.
- [x] 프로젝트의 경로는 스프린트 미션 리포지토리의 경로와 같게 설정합니다.
- [x] Create Git Repository 옵션은 체크하지 않습니다.
- [x] Build system은 Gradle을 사용합니다. Gradle DSL은 Groovy를 사용합니다.
- [x] JDK 17을 선택합니다.
- [x] GroupId는 com.sprint.mission로 설정합니다.
- [x] ArtifactId는 수정하지 않습니다.
- [x] .gitignore에 IntelliJ와 관련된 파일이 형상관리 되지 않도록 .idea디렉토리를 추가합니다.

### 도메인 모델링

- [x] 디스코드 서비스를 활용해보면서 각 도메인 모델에 필요한 정보를 도출하고, Java Class로 구현하세요.
- [x] 패키지명: com.sprint.mission.discodeit.entity
- [x] 도메인 모델 정의
    - [x] 공통
        - [x] id: 객체를 식별하기 위한 id로 UUID 타입으로 선언합니다.
        - [x] createdAt, updatedAt: 각각 객체의 생성, 수정 시간을 유닉스 타임스탬프로 나타내기 위한 필드로 Long 타입으로 선언합니다.
    - [x] User
    - [x] Channel
    - [x] Message
- [x] 생성자
    - [x] id는 생성자에서 초기화하세요.
    - [x] createdAt는 생성자에서 초기화하세요.
    - [x] id, createdAt, updatedAt을 제외한 필드는 생성자의 파라미터를 통해 초기화하세요.
- [x] 메소드
    - [x] 각 필드를 반환하는 Getter 함수를 정의하세요.
    - [x] 필드를 수정하는 update 함수를 정의하세요.

### 서비스 설계 및 구현

- [x] 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언하세요.
- [x] 인터페이스 패키지명: com.sprint.mission.discodeit.service
- [x] 인터페이스 네이밍 규칙: [도메인 모델 이름]Service
- [x] 다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
    - [x] 클래스 패키지명: com.sprint.mission.discodeit.service.jcf
    - [x] 클래스 네이밍 규칙: JCF[인터페이스 이름]
    - [x] Java Collections Framework를 활용하여 데이터를 저장할 수 있는 필드(data)를 final로 선언하고 생성자에서 초기화하세요.
    - [x] data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드를 구현하세요.

### 메인 클래스 구현

- [x] 메인 메소드가 선언된 JavaApplication 클래스를 선언하고, 도메인 별 서비스 구현체를 테스트해보세요.
    - [x] 등록
    - [x] 조회(단건, 다건)
    - [x] 수정
    - [x] 수정된 데이터 조회
    - [x] 삭제
    - [x] 조회를 통해 삭제되었는지 확인

### 기본 요구사항 커밋 태그

- [x] 여기까지 진행 후 반드시 커밋해주세요. 그리고 sprint1-basic 태그를 생성해주세요..

### 심화 요구 사항

#### 서비스 간 의존성 주입

- [x] 도메인 모델 간 관계를 고려해서 검증하는 로직을 추가하고, 테스트해보세요.
    - 힌트: Message를 생성할 때 연관된 도메인 모델 데이터 확인하기

## 멘토에게

- 우선 기본적인 요구사항만 작업했습니다. 시간적 여유가 되면 추가 요소들도 진행하겠습니다.

------

# mission-2

## 파일 IO 및 레이어 분리 요구사항

### File IO를 통한 데이터 영속화

- [x] 다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
    - [x] 클래스 패키지명: com.sprint.mission.discodeit.service.file
    - [x] 클래스 네이밍 규칙: File[인터페이스 이름]
    - [x] JCF 대신 FileIO와 객체 직렬화를 활용해 메소드를 구현하세요.

### 객체 직렬화/역직렬화 가이드

- [x] Application에서 서비스 구현체를 File*Service로 바꾸어 테스트해보세요.

### 서비스 구현체 분석

- [x] JCF*Service 구현체와 File*Service 구현체를 비교하여 공통점과 차이점을 발견해보세요.
- [x] "비즈니스 로직"과 관련된 코드를 식별해보세요.
- [x] "저장 로직"과 관련된 코드를 식별해보세요.

### 레포지토리 설계 및 구현

- [x] "저장 로직"과 관련된 기능을 도메인 모델 별 인터페이스로 선언하세요.
    - [x] 인터페이스 패키지명: com.sprint.mission.discodeit.repository
    - [x] 인터페이스 네이밍 규칙: [도메인 모델 이름]Repository

#### JCF 기반 저장소 구현

- [x] 다음의 조건을 만족하는 레포지토리 인터페이스의 구현체를 작성하세요.
    - [x] 클래스 패키지명: com.sprint.mission.discodeit.repository.jcf
    - [x] 클래스 네이밍 규칙: JCF[인터페이스 이름]
    - [x] 기존에 구현한 JCF*Service 구현체의 "저장 로직"과 관련된 코드를 참고하여 구현하세요.

#### File 기반 저장소 구현

- [x] 다음의 조건을 만족하는 레포지토리 인터페이스의 구현체를 작성하세요.
    - [x] 클래스 패키지명: com.sprint.mission.discodeit.repository.file
    - [x] 클래스 네이밍 규칙: File[인터페이스 이름]
    - [x] 기존에 구현한 File*Service 구현체의 "저장 로직"과 관련된 코드를 참고하여 구현하세요.

### 심화 요구 사항 - 관심사 분리를 통한 레이어 간 의존성 주입

- [x] 다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
    - [x] 클래스 패키지명: com.sprint.mission.discodeit.service.basic
    - [x] 클래스 네이밍 규칙: Basic[인터페이스 이름]
    - [x] 기존에 구현한 서비스 구현체의 "비즈니스 로직"과 관련된 코드를 참고하여 구현하세요.
    - [x] 필요한 Repository 인터페이스를 필드로 선언하고 생성자를 통해 초기화하세요.
    - [x] "저장 로직"은 Repository 인터페이스 필드를 활용하세요. (직접 구현하지 마세요.)
    - [x] Basic*Service 구현체를 활용하여 테스트해보세요.

---

# mission-3

- [x] Spring Initializr를 통해 zip 파일을 다운로드하세요.
    - [x] 빌드 시스템은 Gradle - Groovy를 사용합니다.
    - [x] 언어는 Java 17를 사용합니다.
    - [x] Spring Boot의 버전은 ~~3.4.0~~ (3.5.4로 했음)입니다.
    - [x] GroupId는 com.sprint.mission입니다.
    - [x] ArtifactId와 Name은 discodeit입니다.
    - [x] packaging 형식은 Jar입니다
    - [x] Dependency를 추가합니다.
        - [x] Lombok
        - [x] Spring Web
    - [x] zip 파일을 압축해제하고 원래 진행 중이던 프로젝트에 붙여넣기하세요. 일부 파일은 덮어쓰기할 수 있습니다.
    - [x] application.properties 파일을 yaml 형식으로 변경하세요.
    - [x] DiscodeitApplication의 main 메서드를 실행하고 로그를 확인해보세요.

### Bean 선언 및 테스트

- [x] File*Repository 구현체를 Repository 인터페이스의 Bean으로 등록하세요.
- [x] Basic*Service 구현체를 Service 인터페이스의 Bean으로 등록하세요.
- [x] JavaApplication에서 테스트했던 코드를 DiscodeitApplication에서 테스트해보세요.
    - [x] JavaApplication 의 main 메소드를 제외한 모든 메소드를 DiscodeitApplication클래스로 복사하세요.
    - [x] JavaApplication의 main 메소드에서 Service를 초기화하는 코드를 Spring Context를 활용하여 대체하세요.
    - [x]  JavaApplication의 main 메소드의 셋업, 테스트 부분의 코드를 DiscodeitApplication클래스로 복사하세요.

### Spring 핵심 개념 이해하기

- [x] JavaApplication과 DiscodeitApplication에서 Service를 초기화하는 방식의 차이에 대해 다음의 키워드를 중심으로 정리해보세요.
    - [x] IoC Container
    - [x] Dependency Injection
    - [x] Bean

### Lombok 적용

- [x] 도메인 모델의 getter 메소드를 @Getter로 대체해보세요.
- [x] Basic*Service의 생성자를 @RequiredArgsConstructor로 대체해보세요.

## 비즈니스 로직 고도화

### 시간 타입 변경하기

- [x] 시간을 다루는 필드의 타입은 Instant로 통일합니다.
  기존에 사용하던 Long보다 가독성이 뛰어나며, 시간대(Time Zone) 변환과 정밀한 시간 연산이 가능해 확장성이 높습니다.

### 새로운 도메인 추가하기

도메인 모델 간 참조 관계를 참고하세요.

- [x]  공통: 앞서 정의한 도메인 모델과 동일하게 공통 필드(id, createdAt, updatedAt)를 포함합니다.
- [x]  ReadStatus : 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델입니다. 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용합니다.
- [x]  UserStatus : 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델입니다. 사용자의 온라인 상태를 확인하기 위해 활용합니다.
- [x] 마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드를 정의하세요. 마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로
  간주합니다.
- [x]  BinaryContent :이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델입니다. 사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용합니다.
- [x] 수정 불가능한 도메인 모델로 간주합니다. 따라서 updatedAt 필드는 정의하지 않습니다.
- [x] User, Message 도메인 모델과의 의존 관계 방향성을 잘 고려하여 id 참조 필드를 추가하세요.
- [x]  각 도메인 모델 별 레포지토리 인터페이스를 선언하세요.

레포지토리 구현체(File, JCF)는 아직 구현하지 마세요. 이어지는 서비스 고도화 요구사항에 따라 레포지토리 인터페이스에 메소드가 추가될 수 있어요.

### DTO 활용하기

#### UserService 고도화

- create
    - [x] 선택적으로 프로필 이미지를 같이 등록할 수 있습니다.
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
        - 유저를 등록하기 위해 필요한 파라미터, 프로필 이미지를 등록하기 위해 필요한 파라미터 등
    - [x] username과 email은 다른 유저와 같으면 안됩니다.
    - [x] UserStatus를 같이 생성합니다.
- find, findAll
    - DTO를 활용하여:
    - [x] 사용자의 온라인 상태 정보를 같이 포함하세요.
    - [x] 패스워드 정보는 제외하세요.
- update
    - [x] 선택적으로 프로필 이미지를 대체할 수 있습니다.
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
        - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
- delete
    - [x] 관련된 도메인도 같이 삭제합니다.
        - BinaryContent(프로필), UserStatus

#### AuthService 구현

- login
    - [x] username, password과 일치하는 유저가 있는지 확인합니다.
    - [x] 일치하는 유저가 있는 경우: 유저 정보 반환
    - [x] 일치하는 유저가 없는 경우: 예외 발생
    - [x] DTO를 활용해 파라미터를 그룹화합니다.

#### ChannelService 고도화

- create
    - PRIVATE 채널과 PUBLIC 채널을 생성하는 메소드를 분리합니다.
    - [x] 분리된 각각의 메소드를 DTO를 활용해 파라미터를 그룹화합니다.
    - PRIVATE 채널을 생성할 때:
        - [x] 채널에 참여하는 User의 정보를 받아 User 별 ReadStatus 정보를 생성합니다.
        - [x] name과 description 속성은 생략합니다.
    - PUBLIC 채널을 생성할 때에는 기존 로직을 유지합니다.
- find
    - DTO를 활용하여:
        - [x] 해당 채널의 가장 최근 메시지의 시간 정보를 포함합니다.
        - [x] PRIVATE 채널인 경우 참여한 User의 id 정보를 포함합니다.
- findAll
    - DTO를 활용하여:
        - [x] 해당 채널의 가장 최근 메시지의 시간 정보를 포함합니다.
        - [x] PRIVATE 채널인 경우 참여한 User의 id 정보를 포함합니다.
    - [x] 특정 User가 볼 수 있는 Channel 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다. findAllByUserId
    - [x] PUBLIC 채널 목록은 전체 조회합니다.
    - [x] PRIVATE 채널은 조회한 User가 참여한 채널만 조회합니다.
- update
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
        - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
    - [x] PRIVATE 채널은 수정할 수 없습니다.
- delete
    - [x] 관련된 도메인도 같이 삭제합니다.
        - Message, ReadStatus

#### MessageService 고도화

- create
    - [x] 선택적으로 여러 개의 첨부파일을 같이 등록할 수 있습니다.
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
- findAll
    - [x] 특정 Channel의 Message 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다. findallByChannelId
- update
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
        - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
- delete
    - [x] 관련된 도메인도 같이 삭제합니다.
        - 첨부파일(BinaryContent)

#### ReadStatusService 구현

- create
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
    - [x] 관련된 Channel이나 User가 존재하지 않으면 예외를 발생시킵니다.
    - [x] 같은 Channel과 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
- find
    - [x] id로 조회합니다.
- findAllByUserId
    - [x] userId를 조건으로 조회합니다.
- update
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
        - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
- delete
    - [x] id로 삭제합니다.

#### UserStatusService 고도화

- create
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
    - [x] 관련된 User가 존재하지 않으면 예외를 발생시킵니다.
    - [x] 같은 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
- find
    - [x] id로 조회합니다.
- findAll
    - [x] 모든 객체를 조회합니다.
- update
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
        - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
- updateByUserId
    - [x] userId 로 특정 User의 객체를 업데이트합니다.
- delete
    - [x] id로 삭제합니다.

#### BinaryContentService 구현

- create
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
- find
    - [x] id로 조회합니다.
- findAllByIdIn
    - [x] id 목록으로 조회합니다.
- delete
    - [x] id로 삭제합니다.

## 심화 요구사항

- [x]  Repository 구현체 중에 어떤 구현체를 Bean으로 등록할지 Java 코드의 변경 없이 application.yaml 설정 값을 통해 제어해보세요.
- [x] discodeit.repository.type 설정값에 따라 Repository 구현체가 정해집니다.
    - [x] 값이 jcf 이거나 없으면 JCF*Repository 구현체가 Bean으로 등록되어야 합니다.
    - [x] 값이 file 이면 File*Repository 구현체가 Bean으로 등록되어야 합니다.
- [x] File*Repository 구현체의 파일을 저장할 디렉토리 경로를 application.yaml 설정 값을 통해 제어해보세요.

---

# mission-4

## 컨트롤러 레이어 구현

- [x] `DiscodeitApplication`의 테스트 로직은 삭제하세요.
- [x] 지금까지 구현한 서비스 로직을 활용해 웹 API를 구현하세요.  
  이때 `@RequestMapping`만 사용해 구현해보세요.

---

### 웹 API 요구사항

#### 전역 예외 처리

- [x] 웹 API의 예외를 전역으로 처리하세요.

---

### API 테스트

- [ ] Postman을 활용해 컨트롤러를 테스트 하세요.
- [ ] Postman API 테스트 결과를 export하여 PR에 첨부하세요.

---

### 웹 API 상세 요구사항

#### 사용자 관리

- [x] 사용자를 등록할 수 있다.
- [x] 사용자 정보를 수정할 수 있다.
- [x] 사용자를 삭제할 수 있다.
- [x] 모든 사용자를 조회할 수 있다.
- [x] 사용자의 온라인 상태를 업데이트할 수 있다.

#### 권한 관리

- [x] 사용자는 로그인할 수 있다.

#### 채널 관리

- [x] 공개 채널을 생성할 수 있다.
- [x] 비공개 채널을 생성할 수 있다.
- [x] 공개 채널의 정보를 수정할 수 있다.
- [x] 채널을 삭제할 수 있다.
- [x] 특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다.

#### 메시지 관리

- [x] 메시지를 보낼 수 있다.
- [x] 메시지를 수정할 수 있다.
- [x] 메시지를 삭제할 수 있다.
- [x] 특정 채널의 메시지 목록을 조회할 수 있다.

#### 메시지 수신 정보 관리

- [x] 특정 채널의 메시지 수신 정보를 생성할 수 있다.
- [x] 특정 채널의 메시지 수신 정보를 수정할 수 있다.
- [x] 특정 사용자의 메시지 수신 정보를 조회할 수 있다.

#### 바이너리 파일 다운로드

- [x] 바이너리 파일을 1개 또는 여러 개 조회할 수 있다.

---

### API 수정 요구사항

#### 사용자 목록 조회 API

- [x] URL: `/api/user/findAll`
- 요청
    - 파라미터: 없음
    - 바디: 없음
- 응답
    - `ResponseEntity<List<UserDto>>`

#### BinaryContent 파일 조회 API

- [x] URL: `/api/binaryContent/find`
- 요청
    - 파라미터: `binaryContentId`
    - 바디: 없음
- 응답
    - `ResponseEntity<BinaryContent>`

---

### 심화 요구사항

#### 화면 서빙

- [x] 제공된 파일을 활용하여 사용자 목록을 보여주는 화면을 서빙하세요.

#### 생성형 AI 활용

- [ ] 생성형 AI(Claude, ChatGPT 등)를 활용해 제공된 이미지와 유사한 화면을 생성 후 서빙하세요.

---

# mission-5

### 기본 요구사항

- [x] 스프린트 미션#4에서 구현한 API를 RESTful API로 다시 설계해보세요.
- [x] Postman을 활용해 컨트롤러를 테스트 하세요.
- [x] springdoc-openapi를 활용하여 Swagger 기반의 API 문서를 생성하세요.
- [x] Swagger-UI를 활용해 API를 테스트해보세요.

### 심화 요구사항

- [x] 주어진 정적 리소스를 서빙하여 프론트엔드와 통합해보세요. API 스펙을 준수했다면 잘 동작할거예요.
- [x] Railway.app을 활용하여 애플리케이션을 배포해보세요.

---

# mission-6

## 기본 요구사항

### 데이터베이스

- [x] **데이터베이스 환경 설정**
    - 데이터베이스: `discodeit`
    - 유저: `discodeit_user`
    - 패스워드: `discodeit1234`
- [x] **ERD 기반 DDL 작성 및 테이블 생성**
    - DDL 파일 경로: `/src/main/resources/schema.sql`
    - 참고:
        - PK: Primary Key
        - UK: Unique Key
        - NN: Not Null
        - FK: Foreign Key
        - `ON DELETE CASCADE`: 연관 엔티티 삭제 시 같이 삭제
        - `ON DELETE SET NULL`: 연관 엔티티 삭제 시 NULL로 변경

### Spring Data JPA 적용

- [x] Spring Data JPA와 PostgreSQL 의존성 추가
- [x] `application.yaml`에 DB 연결 설정 추가
- [x] `application.yaml`에 SQL 로그 디버깅 설정 추가

### 엔티티 정의

- [x] **공통 속성 추상 클래스 정의**
    - Serializable 제외
    - 패키지: `com.sprint.mission.discodeit.entity.base`
- [x] `@CreatedDate`, `@LastModifiedDate`로 `createdAt`, `updatedAt` 자동 설정
- [x] 클래스 참조 관계 수정 (필요 시 생성자 및 `update` 메서드 수정)
- [x] **연관관계 매핑 정보 표 작성** (PR에 첨부)
  ```
  | 엔티티 관계          | 다중성 | 방향성            | 부모-자식 관계           | 연관관계의 주인     |
  |--------------------|-------|-----------------|-----------------------|-----------------|
  | Channel:Message    | 1:N   | Message → Channel | 부모: Channel, 자식: Message | Message        |
  | Channel:ReadStatus | 1:N   | ReadStatus → Channel | 부모: Channel, 자식: ReadStatus | ReadStatus    |
  | Message:User       | N:1   | Message → User    | 부모: User, 자식: Message | Message        |
  | Message:BinaryContent | N:M | 양방향 (Message ↔ BinaryContent) | -                     | Message        |
  | ReadStatus:User    | N:1   | ReadStatus → User | 부모: User, 자식: ReadStatus | ReadStatus    |
  | User:BinaryContent | 1:1   | User → BinaryContent | 부모: User, 자식: BinaryContent | User          |
  | User:UserStatus    | 1:1   | UserStatus → User | 부모: User, 자식: UserStatus | UserStatus    |

  ```
- [x] JPA 어노테이션 적용 (`@Entity`, `@Table`, `@Column`, `@Enumerated`,  
  `@OneToMany`, `@OneToOne`, `@ManyToOne`, `@JoinColumn`, `@JoinTable`)
- [x] **영속성 전이(cascade) & 고아 객체(orphanRemoval)** 설정

### 레포지토리 & 서비스 JPA 도입

- [x] 기존 Repository → `JpaRepository` 변경
- [x] `FileRepository`, `JCFRepository` 구현체 삭제
- [x] 서비스 레이어를 **영속성 컨텍스트 특성**에 맞게 수정  
  (트랜잭션, 영속성 전이, 변경 감지, 지연 로딩)

### DTO 적극 도입

- [x] Entity를 Controller에 그대로 노출했을 때 문제점 정리 (PR에 첨부)
- [x] DTO 도입 시 장점 정리
- [x] 클래스 다이어그램 참고하여 DTO 정의
- [x] Entity ↔ DTO 매핑 Mapper 구현
    - 패키지: `com.sprint.mission.discodeit.mapper`

### BinaryContent 저장 로직 고도화

- [x] BinaryContent 엔티티에서 `bytes` 속성 제거, 메타 정보만 유지
- [x] BinaryContentStorage 인터페이스 설계
    - 기능: `UUID put(UUID, byte[])`, `InputStream get(UUID)`,
      `ResponseEntity<?> download(BinaryContentDto)`
- [x] 서비스 레이어에서 BinaryContentStorage 활용하도록 리팩토링
- [x] **파일 다운로드 API 추가**
    - `GET /api/binaryContents/{binaryContentId}/download`
- [x] 로컬 디스크 저장 방식 구현 (`discodeit.storage.type=local`)

### 페이징 & 정렬

- [x] 메시지 목록 조회 시 **50개씩 최근 메시지 순으로 페이지네이션**
- [x] 제네릭 기반 DTO로 페이지네이션 응답 구현
    - 패키지: `com.sprint.mission.discodeit.dto.response`
    - 필드: `content`, `number`, `size`, `totalElements` (nullable)
- [x] Slice/Page 객체 → DTO 변환 Mapper 구현 (제네릭 메서드로 확장성 확보)

## 심화 요구사항

### N+1 문제

- [x] N+1 문제가 발생하는 쿼리를 찾고 해결해보세요.

### 읽기전용 트랜잭션 활용

- [x] 프로덕션 환경에서는 OSIV를 비활성화하는 경우가 많습니다. 이때 서비스 레이어의 조회 메소드에서 발생할 수 있는 문제를 식별하고, 읽기 전용 트랜잭션을 활용해 문제를
  해결해보세요.

**OSIV 비활성화 설정**

```yaml
spring:
  jpa:
    open-in-view: false
```

### 페이지네이션 최적화

- [x] 오프셋 페이지네이션과 커서 페이지네이션 방식의 차이에 대해 정리해보세요. (이 내용은 PR에 첨부해주세요.)
- [x] 기존에 구현한 오프셋 페이지네이션을 커서 페이지네이션으로 리팩토링하세요.
- [x] PageResponse는 다음과 같이 변경하세요. (참고 이미지: 73leqaemv-image.png)
- [x] 아래의 API 명세를 준수하세요.
    - API 스펙 v1.2
    - API 스펙을 준수하면 프론트엔드 코드(v1.2.4)와 호환됩니다.

**참고 사항**

- 정적 리소스 v1.2.4
- 소스 코드(참고용) v1.2.4
- 프론트엔드 소스는 참고용으로만 사용하세요. 수정 시 이어지는 요구사항 수행에 어려움이 있을 수 있습니다.

### MapStruct 적용

- [x] Entity와 DTO를 매핑하는 보일러플레이트 코드를 MapStruct 라이브러리를 활용해 간소화해보세요.
