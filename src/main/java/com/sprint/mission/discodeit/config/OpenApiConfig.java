package com.sprint.mission.discodeit.config; // 설정 클래스 패키지

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 스프링 설정 클래스로 등록
public class OpenApiConfig { // OpenAPI 관련 전역 설정을 담는 클래스

  @Bean // IoC 컨테이너에 OpenAPI 스펙 객체를 빈으로 등록
  public OpenAPI discodeitOpenAPI() { // Swagger UI가 읽어갈 OpenAPI 정의 반환
    // 문서 상단 기본 정보(제목/설명/버전/연락처/라이선스)를 구성
    Info info = new Info() // Info 객체 생성
        .title("Discodeit API") // 문서 제목
        .description("Discodeit 백엔드 REST API 명세") // 문서 설명
        .version("v1.0.0") // 문서 버전
        .contact(new Contact() // 문의용 연락처 정보
            .name("Discodeit Team") // 팀/담당자명
            .url("https://example.com") // 홈페이지(필요 시 실제 URL로 수정)
            .email("team@example.com")) // 이메일(필요 시 실제 메일로 수정)
        .license(new License() // 라이선스 정보(선택)
            .name("Apache 2.0") // 라이선스명
            .url("https://www.apache.org/licenses/LICENSE-2.0.html")); // 라이선스 링크

    // 서버 정보 등록(여러 환경이 있으면 .addServersItem(...)을 여러 번 호출)
    Server defaultServer = new Server() // 서버 엔트리 생성
        .url("/") // 기본 베이스 URL(리버스 프록시/게이트웨이 있으면 실제 베이스 경로로 변경)
        .description("Default Server"); // 설명

    // OpenAPI 루트 객체를 만들어 Info/Servers를 붙여 반환
    return new OpenAPI() // OpenAPI 루트
        .info(info) // 상단 문서 정보 적용
        .addServersItem(defaultServer); // 서버 목록에 기본 서버 추가
  }
}
