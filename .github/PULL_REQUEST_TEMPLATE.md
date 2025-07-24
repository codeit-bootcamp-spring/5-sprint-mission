
## 기본 요구사항

- [x] `JCF` 기반 저장소 구현 완료
- [x] `FileIO` 기반 저장소 구현 완료
- [x] `직렬화(Serialization)`를 사용하여 데이터를 파일로 저장
- [x] `FileStore<T>` 추상 클래스 구현
- [x] 서비스 구현 클래스 (FileUserService, FileChannelService, FileMessageService) 구현 완료
- [x] JavaApplication에서 `File*Service` 실행 테스트 완료

---

## 서비스 구현체 분석

- [x] `JCF*Service` vs `File*Service` 공통점, 차이점 파악
- [x] 비즈니스 로직과 저장 로직을 구분하여 식별
- [x] 저장 로직을 별도의 저장소(Repository)로 분리

---

## 레포지토리 설계 및 구현

- [x] 도메인별 저장소 인터페이스 선언
    - `UserRepository`
    - `ChannelRepository`
    - `MessageRepository`
- [x] `JCF` 저장소 구현체
    - `JCFUserRepository`
    - `JCFChannelRepository`
    - `JCFMessageRepository`
- [x] `File` 저장소 구현체
    - `FileUserRepository`
    - `FileChannelRepository`
    - `FileMessageRepository`

---

## 심화 요구사항

- [x] `BasicUserService`, `BasicChannelService`, `BasicMessageService` 구현
- [x] 생성자 주입을 통해 Repository 구현체 주입
- [x] 비즈니스 로직과 저장 로직 완전 분리
- [x] JavaApplication에서 Basic*Service 조합 테스트



## 스크린샷
![image](이미지url)

## 멘토에게
- 셀프 코드 리뷰를 통해 질문 이어가겠습니다.
- 
