
## 기본 요구사항
컨트롤러 레이어 구현
- [ ] DiscodeitApplication의 테스트 로직은 삭제하세요.
- [ ] 지금까지 구현한 서비스 로직을 활용해 웹 API를 구현하세요. 이때 @RequestMapping만 사용해 구현해보세요.
- [ ]  웹 API의 예외를 전역으로 처리하세요.

#### [Spring 핵심 개념 이해하기]
기존에는 `new` 연산자를 사용해 수동으로 객체를 생성했지만, `DiscodeitApplication`에서는 Spring의 
IoC Container가 객체의 생성과 관리를 담당하도록 변경했습니다.
Service 클래스는 이제 직접 생성하지 않고 Spring이 관리하는 Bean으로 등록되어, 필요한 곳에서 자동으로 주입됩니다.
이 과정을 DI(Dependency Injection)이라고 합니다.

- [x] Lombok 적용 : 도메인 모델에 getter 메소드를 `@Getter`로 대체
- [x] Basic*Service의 생성자를 `@RequiredArgsConstructor`로 대체

--- 

## 추가 기능 요구 사항

- [x] 시간 타입 변경하기 : Long -> Instant
- [x] 새로운 도메인 추가하기: `UserStatus`, `ReadStatus`,`BinaryContent`
- [x] DTO 활용하기
- [x] UserService 고도화
- [x] AuthService 구현
- [x] ChannelService 고도화
- [x] MessageService 고도화
- [x] ReadStatusService 구현
- [x] UserStatusService 고도화
- [x] BinaryContentService 구현

---

## 심화 요구사항
- [x] Bean 다루기: application.yaml

## 스크린샷
![image](이미지url)

## 멘토에게
- 셀프 코드 리뷰를 통해 질문 이어가겠습니다.
- 
