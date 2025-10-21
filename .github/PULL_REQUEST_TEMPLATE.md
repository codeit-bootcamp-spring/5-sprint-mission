## 애플리케이션 컨테이너화
- [ ] Amazon Corretto 17 이미지를 베이스 이미지로 사용하세요.
- [ ] 작업 디렉토리를 설정하세요. (/app)
- [ ] 프로젝트 파일을 컨테이너로 복사하세요. 단, 불필요한 파일은 .dockerignore를 활용해 제외하세요.
- [ ] Gradle Wrapper를 사용하여 애플리케이션을 빌드하세요.
- [ ] 80 포트를 노출하도록 설정하세요.
- [ ] 프로젝트 정보를 환경 변수로 설정하세요.
  - 실행할 jar 파일의 이름을 추론하는데 활용됩니다.
  - PROJECT_NAME: discodeit
  - PROJECT_VERSION: 1.2-M8
- [ ] JVM 옵션을 환경 변수로 설정하세요.
    - JVM_OPTS: 기본값은 빈 문자열로 정의
-[ ] 애플리케이션 실행 명령어를 설정하세요. 이때 환경변수로 정의한 프로젝트 정보를 활용하세요.

### 이미지 빌드 및 실행 테스트
- [ ] Docker 이미지를 빌드하고 태그(local)를 지정하세요.
- [ ] 빌드된 이미지를 활용해서 컨테이너를 실행하고 애플리케이션을 테스트하세요.
- [ ] prod 프로필로 실행하세요.
- [ ] 데이터베이스는 로컬 환경에서 구동 중인 PostgreSQL 서버를 활용하세요.
- [ ] http://localhost:8081로 접속 가능하도록 포트를 매핑하세요.
  - Docker Compose 구성
  - 개발 환경용 docker-compose.yml 파일을 작성합니다.
- [ ] 애플리케이션과 PostgreSQL 서비스를 포함하세요.
- [ ] 각 서비스에 필요한 모든 환경 변수를 설정하세요.
  - .env 파일을 활용하되, .env는 형상관리에서 제외하여 보안을 유지하세요.
- [ ] 애플리케이션 서비스를 로컬 Dockerfile에서 빌드하도록 구성하세요.
- [ ] 애플리케이션 볼륨을 구성하여 컨테이너가 재시작되어도 BinaryContentStorage 데이터가 유지되도록 하세요.
- [ ] PostgreSQL 볼륨을 구성하여 컨테이너가 재시작되어도 데이터가 유지되도록 하세요.
- [ ] PostgreSQL 서비스 실행 후 schema.sql이 자동으로 실행되도록 구성하세요.
- [ ] 서비스 간 의존성을 설정하세요(depends_on).
- [ ] 필요한 포트 매핑을 구성하세요.
- [ ] Docker Compose를 사용하여 서비스를 시작하고 테스트하세요.
  - --build 플래그를 사용하여 서비스 시작 전에 이미지를 빌드하도록 합니다.

## BinaryContentStorage 고도화 (AWS S3)
### AWS S3 버킷 구성
- [ ] AWS S3 버킷을 생성하세요.
- [ ] 버킷 이름을 discodeit-binary-content-storage-(사용자 이니셜) 형식으로 지정하세요.
- [ ] 퍼블릭 액세스 차단 설정을 활성화하세요(모든 퍼블릭 액세스 차단).
- [ ] 버전 관리는 비활성화 상태로 두세요.
  - AWS S3 접근을 위한 IAM 구성
- [ ] S3 버킷에 접근하기 위한 IAM 사용자(discodeit)를 생성하세요.
- [ ] AmazonS3FullAccess 권한을 할당하고, 사용자 생성을 완료하세요.
- [ ] 생성된 사용자에 엑세스 키를 생성하세요.
- [ ] 발급받은 키를 포함해서 AWS 관련 정보는 .env 파일에 추가합니다.
  - 작성한 .env 파일은 리뷰를 위해 PR에 별도로 첨부해주세요. 단, 엑세스 키와 시크릿 키는 제외하세요.


### AWS S3 테스트
- [ ] AWS S3 SDK 의존성을 추가하세요.
- [ ] S3 API를 간단하게 테스트하세요.
  - 패키지명: com.sprint.mission.discodeit.stoarge.s3
  - 클래스명: AWSS3Test
- [ ] Properties 클래스를 활용해서 .env에 정의한 AWS 정보를 로드하세요.
- [ ] 작업 별 테스트 메소드를 작성하세요.
  - 업로드
  - 다운로드
  - PresignedUrl 생성

### AWS S3를 활용한 BinaryContentStroage 고도화
- [ ] 앞서 작성한 테스트 메소드를 참고해 S3BinaryContentStorage를 구현하세요.
- [ ] discodeit.storage.type 값이 s3인 경우에만 Bean으로 등록되어야 합니다.
- [ ] S3BinaryContentStorageTest를 함께 작성하면서 구현하세요.
- [ ] BinaryContentStorage 설정을 유연하게 제어할 수 있도록 application.yaml을 수정하세요.
  - [ ] AWS 관련 정보는 형상관리하면 안되므로 .env 파일에 작성된 값을 임포트하는 방식으로 설정하세요.
  - [ ] Docker Compose에서도 위 설정을 주입할 수 있도록 수정하세요.
- [ ] download 메소드는 PresignedUrl을 활용해 리다이렉트하는 방식으로 구현하세요.

## 스크린샷

## 멘토에게
- 셀프 코드 리뷰를 통해 질문 이어가겠습니다.
- 
