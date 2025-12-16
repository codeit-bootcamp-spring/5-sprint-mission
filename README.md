# 웹소켓 구현하기

- [x] **웹소켓 환경 구성**
  spring-boot-starter-websocket 의존성을 추가하세요.

```gradle
implementation 'org.springframework.boot:spring-boot-starter-websocket'
```

웹소켓 메시지 브로커 설정

```java

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {...
}
```

메모리 기반 SimpleBroker를 사용하세요.

```java

@Override
public void configureMessageBroker(MessageBrokerRegistry config) {...}
```

* SimpleBroker의 Destination Prefix는 **/sub** 으로 설정하세요.
  클라이언트에서 메시지를 구독할 때 사용합니다.
* Application Destination Prefix는 **/pub** 으로 설정하세요.
  클라이언트에서 메시지를 발행할 때 사용합니다.

```java

@Override
public void registerStompEndpoints(StompEndpointRegistry registry) {...}
```

* STOMP 엔드포인트는 **/ws** 로 설정하고,
  **SockJS 연결을 지원**해야 합니다.

---

- [x] **메시지 송신**
  첨부파일이 없는 단순 텍스트 메시지인 경우 STOMP를 통해 메시지를 전송할 수 있도록 컨트롤러를 구현하세요.

```java

@Controller
public class MessageWebSocketController {
    ...
  @MessageMapping(...)
}
```

* 클라이언트는 웹소켓으로 **/pub/messages** 엔드포인트에 메시지를 전송할 수 있어야 합니다.
* `@MessageMapping` 을 활용하세요.
* 메시지 전송 요청의 페이로드 타입은 **MessageCreateRequest** 를 그대로 활용합니다.
* 첨부파일이 포함된 메시지는 기존의 API **POST /api/messages** 를 그대로 활용합니다.

---

- [x] **메시지 수신**

* 클라이언트는 채널 입장 시 웹소켓으로
  **/sub/channels.{channelId}.messages** 를 구독해 메시지를 수신합니다.

이를 고려해 메시지가 생성되면 해당 엔드포인트로 메시지를 보내는 컴포넌트를 구현하세요.

```java

@Component
public class WebSocketRequiredEventListener {
    ...
  private final SimpMessagingTemplate messagingTemplate;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessage(MessageCreatedEvent event) {...}
}
```

* MessageCreatedEvent를 통해 새로운 메시지 생성 이벤트를 확인하세요.
* SimpMessagingTemplate 를 통해 적절한 엔드포인트로 메시지를 전송하세요.

---

# SSE 구현하기

- [x]  SSE 환경을 구성하세요.

클라이언트에서 SSE 연결을 위한 엔드포인트를 구현하세요.

GET /api/sse

사용자별 SseEmitter 객체를 생성하고 메시지를 전송하는 컴포넌트를 구현하세요.

```
@Service
public class SseService {

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {...}

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {...}

  public void broadcast(String eventName, Object data) {...}

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {...}

  private boolean ping(SseEmitter sseEmitter) {...}
}
```

connect: SseEmitter 객체를 생성합니다.
send, broadcast: SseEmitter 객체를 통해 이벤트를 전송합니다.
cleanUp: 주기적으로 ping을 보내서 만료된 SseEmitter 객체를 삭제합니다.
ping: 최초 연결 또는 만료 여부를 확인하기 위한 용도로 더미 이벤트를 보냅니다.

SseEmitter 객체를 메모리에서 저장하는 컴포넌트를 구현하세요.

```
@Repository
public class SseEmitterRepository {
  private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();
    ...
}
```

ConcurrentMap: 스레드 세이프한 자료구조를 사용합니다.
List<SseEmitter>: 사용자 당 N개의 연결을 허용할 수 있도록 합니다. (예: 다중 탭)

이벤트 유실 복원을 위해 SSE 메시지를 저장하는 컴포넌트를 구현하세요.

```
@Repository
public class SseMessageRepository {

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();
    ...
}
```

각 메시지 별로 고유한 ID를 부여합니다.
클라이언트에서 LastEventId를 전송해 이벤트 유실 복원이 가능하도록 해야 합니다.

- [x]  기존에 클라이언트에서 폴링 방식으로 주기적으로 요청하던 데이터를 SSE를 이용해 서버에서 실시간으로 전달하는 방식으로 리팩토링하세요.

- [x] 새로운 알림 이벤트 전송

- [x] 파일 업로드 상태 변경 이벤트 전송

- [x] 채널 갱신 이벤트 전송

- [x] 사용자 갱신 이벤트 전송

---

## 배포 아키텍처 구성하기

- [x]  다음의 다이어그램에 부합하는 배포 아키텍처를 Docker Compose를 통해 구현하세요.

---

## Reverse Proxy

Nginx 기반의 리버스 프록시 컨테이너를 구성하세요.

역할 및 설정은 다음과 같습니다:

* /api/*, /ws/* 요청은 Backend 컨테이너로 프록시 처리합니다.
* 이 외의 모든 요청은 정적 리소스(프론트엔드 빌드 결과)를 서빙합니다.
* 프론트엔드 정적 리소스는 Nginx 컨테이너 내부의 적절한 경로(/usr/share/nginx/html 등)에 복사하세요.
* 외부에서 접근 가능한 유일한 컨테이너이며, 3000번 포트를 통해 접근할 수 있어야 합니다.

---

## Backend

Spring Boot 기반의 백엔드 서버를 Docker 컨테이너로 구성하세요.

* Reverse Proxy를 통해 /api/*, /ws/* 요청이 이 서버로 전달됩니다.

---

## DB, Memory DB, Message Broker

Backend 컨테이너가 접근 가능한 다음의 인프라 컨테이너들을 구성하세요

* DB: PostgreSQL
* Memory DB: Redis
* Message Broker: Kafka

각 컨테이너는 Docker Compose 네트워크를 통해 백엔드에서 통신할 수 있어야 합니다.
외부 네트워크와 단절되어야 합니다.

---

## 웹소켓 인증/인가 처리하기

- [x]  인증 처리
- 디스코드잇 클라이언트는 CONNECT 프레임의 헤더에 다음과 같이 Authorization 토큰을 포함합니다.

```
CONNECT
Authorization:Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3b29keSIsImV4cCI6MTc0OTM5MzA0OCwiaWF0IjoxNzQ5MzkyNDQ4LCJ1c2VyRHRvIjp7ImlkIjoiMDQwZTk2ZWMtMjdmNC00Y2MxLWI4MWQtNTMyM2ExZWQ5NTZhIiwidXNlcm5hbWUiOiJ3b29keSIsImVtYWlsIjoid29vZHlAZGlzY29kZWl0LmNvbSIsInByb2ZpbGUiOm51bGwsIm9ubGluZSI6bnVsbCwicm9sZSI6IlVTRVIifX0.JOkvCpnR0e0KMQYLh_hUWglgTvUIlfQOT58eD4Cym5o
accept-version:1.2,1.1,1.0
heart-beat:4000,4000
```

- [x] 서버 측에서는 ChannelInterceptor를 구현하여 연결 시 토큰을 검증하고, 인증된 사용자 정보를 SecurityContext에 설정해야 합니다.

- [x] CONNECT 프레임일 때 엑세스 토큰을 검증하는 JwtAuthenticationChannelInterceptor 구현체를 정의하세요.
    - 검증 로직은 이전에 구현한 JwtAuthenticationFilter를 참고하세요.
    - 인증이 완료되면 SecurityContext에 인증정보를 저장하는 대신 accessor 객체에 저장하세요.

- [x] SecurityContextChannelInterceptor를 등록하여 이후 메시지 처리 흐름에서도 인증 정보를 활용할 수 있도록 구성하세요.

[ ]  인가 처리

- AuthorizationChannelInterceptor를 사용해 메시지 권한 검사를 수행합니다.

- [x] AuthorizationChannelInterceptor를 활용하기 위해의존성을 추가하세요.

```
implementation 'org.springframework.security:spring-security-messaging'
```

- [x] MessageMatcherDelegatingAuthorizationManager를 활용해 인가 정책을 정의하고, 채널에 추가하세요.

```
private AuthorizationChannelInterceptor authorizationChannelInterceptor() {
return new AuthorizationChannelInterceptor(
MessageMatcherDelegatingAuthorizationManager.builder()
.anyMessage().hasRole(Role.USER.name())
.build()
);
}
@Override
public void configureClientInboundChannel(ChannelRegistration registration) {
registration.interceptors(
jwtAuthenticationChannelInterceptor,
new SecurityContextChannelInterceptor(),
authorizationChannelInterceptor()
);
}
```

---

## 분산 환경 배포 아키텍처 구성하기

- [x]  다음의 다이어그램에 부합하는 배포 아키텍처를 Docker Compose를 통해 구현하세요.

- Backend-*
    - deploy.replicas 설정을 활용하세요.
    - Reverse Proxy
        - upstream 블록을 수정해 다음의 로드밸런싱 전략을 적용해 Backend로 트래픽을 분산시켜보세요.
            - Round Robin 기본값
            - Least Connections
            - IP Hash
            - Weight
    - $upstream_addr 변수를 활용해 실제 요청을 처리하는 서버의 IP를 헤더에 추가하고 브라우저 개발자 도구를 활용해 비교해보세요.
<img width="949" height="297" alt="{E716DE8A-BCB2-4BCC-92C3-2768A924DC2F}" src="https://github.com/user-attachments/assets/c7ba7f7c-97ab-48d8-843b-37a5d503dfe1" />

<img width="630" height="521" alt="{99E044EB-31F4-40C6-824B-ACD93F95CDE7}" src="https://github.com/user-attachments/assets/3bef2558-92f1-403c-b021-d46867e83526" />


- [x]  분산환경에 따른 InMemoryJwtRegistry의 한계점을 식별하고 Redis를 활용해 리팩토링하세요.
- 어떤 한계가 있는지 식별하고 PR에 남겨주세요.
    - 분산환경에서는 하나의 백엔드 인스턴스가 아닌 여러개의 백엔드 인스턴스가 있기에 InmemoryRegistry를 사용하게 되면 인스턴스간 상태 공유가 불가능 해집니다.
    각 서버가 자체 메모리를 사용하기에 JWT 상태가 서버마다 분리가 됩니다. 지금 구조는 서비스가 분리된것은 아니지만 Ngnix가 3개의 백엔드 서버에 
    분산 처리가 되기 때문에 상태가 하나의 InmemoryCache에 남을 수 밖에 없다.
    그래서 Redis를 사용하면 독립적인 인프라 컴포넌트로 되기 때문에 모든 인스턴스가 동일한 Cache 즉 동일한 JWT상태를 조회 및 갱신이 가능하게 된다.
    
- RedisJwtRegistry 구현체를 활용하세요.

- [ ] 분산환경에 따른 웹소켓과 SSE의 한계점을 식별하고 Kafka를 활용해 리팩토링하세요.
    - 웹소켓은 지속적인 연결을 유지하는 구조로, 분산 환경에서는 연결 상태가 특정 서버에 종속되어 서버 간 메시지 전달 및 확장에 한계가 있다.
    - SSE 또한 특정 서버에서 발생한 이벤트를 다른 서버의 연결로 전달하기 어려워, 이벤트 누락 또는 중복이 발생할 수 있다.
      
     한계가 있는지 식별하고 PR에 남겨주세요.
    - 일반적인 카프카 이벤트와 다르게 각 서버 인스턴스마다 이벤트를 받을 수 있어야 합니다. 따라서 컨슈머 group id를 적절히 설정하세요.
