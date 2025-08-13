# 기본 요구사항

## 컨트롤러 레이어 구현

- [x]  DiscodeitApplication의 테스트 로직은 삭제하세요.

- [x]  지금까지 구현한 서비스 로직을 활용해 웹 API를 구현하세요.  
  이때 @RequestMapping만 사용해 구현해보세요.

- [x]  웹 API의 예외를 전역으로 처리하세요.

## 웹 API 요구사항

### 사용자 관리

- [x] 사용자를 등록할 수 있다.
- [x] 사용자 정보를 수정할 수 있다.
- [x] 사용자를 삭제할 수 있다.
- [x] 모든 사용자를 조회할 수 있다.
- [x] 사용자의 온라인 상태를 업데이트할 수 있다.

### 권한 관리

- [x] 사용자는 로그인할 수 있다.

### 채널 관리

- [ ] 공개 채널을 생성할 수 있다.
- [ ] 비공개 채널을 생성할 수 있다.
- [ ] 공개 채널의 정보를 수정할 수 있다.
- [ ] 채널을 삭제할 수 있다.
- [ ] 특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다.

### 메시지 관리

- [ ] 메시지를 보낼 수 있다.
- [ ] 메시지를 수정할 수 있다.
- [ ] 메시지를 삭제할 수 있다.
- [ ] 특정 채널의 메시지 목록을 조회할 수 있다.

### 메시지 수신 정보 관리

- [ ] 특정 채널의 메시지 수신 정보를 생성할 수 있다.
- [ ] 특정 채널의 메시지 수신 정보를 수정할 수 있다.
- [ ] 특정 사용자의 메시지 수신 정보를 조회할 수 있다.

### 바이너리 파일 다운로드

- [x] 바이너리 파일을 1개 또는 여러 개 조회할 수 있다.

## API 테스트

- [x] Postman을 활용해 컨트롤러를 테스트 하세요.
    - Postman API 테스트 결과를 다음과 같이 export하여 PR에 첨부해주세요.
    - ![](readme1.png)

# 심화 요구사항

## 정적 리소스 서빙

- [x]  사용자 목록 조회, BinaryContent 파일 조회 API를 다음의 조건을 만족하도록 수정하세요.
    - [x]  사용자 목록 조회
        - url: `/api/user/findAll` (저는 `/api/users`로 했습니다.)
        - 요청
            - 파라미터, 바디 없음
        - 응답
            - ```java
              ResponseEntity<List<UserDto>>
              public record UserDto(
              UUID id,
              Instant createdAt,
              Instant updatedAt,
              String username,
              String email,
              UUID profileId,
              Boolean online
              ) {
              }
    - [x]  BinaryContent 파일 조회
        - url: `/api/binaryContent/find` (저는 `/api/binary-contents/{id}`로 했습니다.)
        - 요청
            - 파라미터: `binaryContentId`
            - 바디 없음
        - 응답: `ResponseEntity<BinaryContent>`
- [ ]  다음의 파일을 활용하여 사용자 목록을 보여주는 화면을 서빙해보세요.

> static-resources.zip

![](readme2.png)

- 생성형 AI 활용
    - [ ] 생성형 AI (Claude, ChatGPT 등)를 활용해서 위 이미지와 비슷한 화면을 생성 후 서빙해보세요.
