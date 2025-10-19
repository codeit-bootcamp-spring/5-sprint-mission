## 요구사항

## 기본

### 애플리케이션 컨테이너화

#### Dockerfile 작성

- [X] Amazon Corretto 17 이미지를 베이스 이미지로 사용하세요.
- [X] 작업 디렉토리를 설정하세요. (/app)
- [X] 프로젝트 파일을 컨테이너로 복사하세요. 단, 불필요한 파일은 .dockerignore를 활용해 제외하세요.
- [X] Gradle Wrapper를 사용하여 애플리케이션을 빌드하세요.
- [X] 80 포트를 노출하도록 설정하세요.
- [X] 프로젝트 정보를 환경 변수로 설정하세요.
    - 실행할 jar 파일의 이름을 추론하는데 활용됩니다.
    - PROJECT_NAME: discodeit
    - PROJECT_VERSION: 1.2-M8
- [X] JVM 옵션을 환경 변수로 설정하세요.
    - JVM_OPTS: 기본값은 빈 문자열로 정의
- [X] 애플리케이션 실행 명령어를 설정하세요. 이때 환경변수로 정의한 프로젝트 정보를 활용하세요.

<br>

#### 이미지 빌드 및 실행 테스트

- [X] Docker 이미지를 빌드하고 태그(local)를 지정하세요.
- [X] 빌드된 이미지를 활용해서 컨테이너를 실행하고 애플리케이션을 테스트하세요.
- [X] prod 프로필로 실행하세요.
- [X] 데이터베이스는 로컬 환경에서 구동 중인 PostgreSQL 서버를 활용하세요.
- [X] http://localhost:8081로 접속 가능하도록 포트를 매핑하세요.

<br>

#### Docker Compose 구성

- 개발 환경용 docker-compose.yml 파일을 작성합니다.
- [X] 애플리케이션과 PostgreSQL 서비스를 포함하세요.
- [X] 각 서비스에 필요한 모든 환경 변수를 설정하세요.
    - .env 파일을 활용하되, .env는 형상관리에서 제외하여 보안을 유지하세요.
- [X] 애플리케이션 서비스를 로컬 Dockerfile에서 빌드하도록 구성하세요.
- [X] 애플리케이션 볼륨을 구성하여 컨테이너가 재시작되어도 BinaryContentStorage 데이터가 유지되도록 하세요.
- [X] PostgreSQL 볼륨을 구성하여 컨테이너가 재시작되어도 데이터가 유지되도록 하세요.
- [X] PostgreSQL 서비스 실행 후 schema.sql이 자동으로 실행되도록 구성하세요.
- [X] 서비스 간 의존성을 설정하세요(depends_on).
- [X] 필요한 포트 매핑을 구성하세요.
- [X] Docker Compose를 사용하여 서비스를 시작하고 테스트하세요.
- --build 플래그를 사용하여 서비스 시작 전에 이미지를 빌드하도록 합니다.

<br><br>

### BinaryContentStorage 고도화 (AWS S3)

#### AWS S3 버킷 구성

- [x] AWS S3 버킷을 생성하세요.
- [x] 버킷 이름을 discodeit-binary-content-storage-(사용자 이니셜) 형식으로 지정하세요.
- [x] 퍼블릭 액세스 차단 설정을 활성화하세요(모든 퍼블릭 액세스 차단).
- [x] 버전 관리는 비활성화 상태로 두세요.

#### AWS S3 접근을 위한 IAM 구성

- [x]  S3 버킷에 접근하기 위한 IAM 사용자(discodeit)를 생성하세요.
- [x]  AmazonS3FullAccess 권한을 할당하고, 사용자 생성을 완료하세요.
- [x]  생성된 사용자에 엑세스 키를 생성하세요.
- [x]  발급받은 키를 포함해서 AWS 관련 정보는 .env 파일에 추가합니다.

``` .env
...
# AWS
AWS_S3_ACCESS_KEY=**엑세스_키**
AWS_S3_SECRET_KEY=**시크릿_키**
AWS_S3_REGION=**ap-northeast-2**
AWS_S3_BUCKET=**버킷_이름**
```

- 작성한 .env 파일은 리뷰를 위해 PR에 별도로 첨부해주세요. 단, 엑세스 키와 시크릿 키는 제외하세요.

<br>

#### AWS S3 테스트

- [x]  AWS S3 SDK 의존성을 추가하세요.

implementation 'software.amazon.awssdk:s3:2.31.7'

- [x]  S3 API를 간단하게 테스트하세요.
- 패키지명: com.sprint.mission.discodeit.stoarge.s3
- 클래스명: AWSS3Test
- [x] Properties 클래스를 활용해서 .env에 정의한 AWS 정보를 로드하세요.
- [x] 작업 별 테스트 메소드를 작성하세요.
    - 업로드
    - 다운로드
    - PresignedUrl 생성

#### AWS S3를 활용한 BinaryContentStroage 고도화

- [x]  앞서 작성한 테스트 메소드를 참고해 S3BinaryContentStorage를 구현하세요.
- [x]  discodeit.storage.type 값이 s3인 경우에만 Bean으로 등록되어야 합니다.
- [x]  S3BinaryContentStorageTest를 함께 작성하면서 구현하세요.
- [x]  BinaryContentStorage 설정을 유연하게 제어할 수 있도록 application.yaml을 수정하세요.

```application.yaml
discodeit:
storage:
type: local
type: ${STORAGE_TYPE:local} # local | s3 (기본값: local)
local:
root-path: .discodeit/storage
root-path: ${STORAGE_LOCAL_ROOT_PATH:.discodeit/storage}
s3:
access-key: ${AWS_S3_ACCESS_KEY}
secret-key: ${AWS_S3_SECRET_KEY}
region: ${AWS_S3_REGION}
bucket: ${AWS_S3_BUCKET}
presigned-url-expiration: ${AWS_S3_PRESIGNED_URL_EXPIRATION:600} # (기본값: 10분)
```

- [x] AWS 관련 정보는 형상관리하면 안되므로 .env 파일에 작성된 값을 임포트하는 방식으로 설정하세요.
- [x] Docker Compose에서도 위 설정을 주입할 수 있도록 수정하세요.
- [x]  download 메소드는 PresignedUrl을 활용해 리다이렉트하는 방식으로 구현하세요.

<br><br>

### AWS를 활용한 배포 (AWS RDS, ECR, ECS)

#### AWS RDS 구성

- [ ]  AWS RDS PostgreSQL 인스턴스를 생성하세요.
- 이외 설정은 기본값을 유지하세요.

- [ ]  과금이 발생할 수 있으니 다음 항목은 한번 더 확인해주세요.
- [ ] 템플릿: 프리티어
- [ ] 퍼블릭 액세스: 아니오
- [ ] 모니터링 > 보존기간: 7일
- [ ] 모니터링 > 추가 모니터링 설정: 모두 체크 해제
- [ ] 추가 구성 > 백업: 비활성화
- [ ]  SSH 터널링을 통해 개발 환경에서 접근할 수 있도록 EC2를 구성하세요.

- [ ]  EC2 인스턴스를 생성하세요.

- [ ]  보안 그룹에서 인바운드 규칙을 편집하세요.
- 유형: SSH
- 소스: 내 IP
- 작업 환경의 네트워크(와이파이 등)가 달라지면 계속 수정해주어야 할 수 있습니다.

- [ ]  DataGrip을 통해 연결 후 데이터베이스와 사용자, 테이블을 초기화하세요.
- [ ]  데이터 소스 추가 시 SSH/SSL > Use SSH tunnel 설정을 활성화하세요. 이때 이전에 다운로드한 .pem 파일을 활용하세요.
- [ ]  연결이 성공하면 데이터베이스와 사용자, 테이블을 초기화하세요.

```
-- 1. 새 유저 'discodeit_user' 생성 (비밀번호는 원하는 값으로 설정)
CREATE USER discodeit_user WITH PASSWORD 'discodeit1234';

-- 2. postgres 계정은 AWS RDS 환경 특성상 완전한 super user가 아니므로, discodeit_user에 대한 권한을 추가로 부여해야함.  
GRANT discodeit_user TO postgres;

-- 3. 'discodeit' 데이터베이스 생성 (소유자는 'discodeit_user')
CREATE DATABASE discodeit OWNER discodeit_user;

-- 4. schema.sql 실행하여 테이블 생성
```

- [ ]  구성이 완료되면 rds-ssh 인스턴스는 완전히 삭제하여 과금에 유의하세요.

#### AWS ECR 구성

- [ ]  이미지를 배포할 퍼블릭 레포지토리(discodeit)를 생성하세요.

- 프라이빗 레포지토리는 용량 제한이 있으므로 퍼블릭 레포지토리로 생성합니다.
- [ ]  AWS CLI를 설치하세요.

- [ ]  aws configure 실행 후 앞서 생성한 discodeit IAM 사용자 정보를 입력하세요.
- 엑세스 키
- 시크릿 키
- region: ap-northeast-2
- output format: json
-
- [ ]  discodeit IAM 사용자가 ECR에 접근할 수 있도록 다음 권한을 부여하세요.
- AmazonElasticContainerRegistryPublicFullAccess

- [ ]  Docker 클라이언트를 배포할 레지스트리에 대해 인증합니다.
- AWS 콘솔을 통해 생성한 레포지토리 페이지로 이동 후 우측 상단 푸시 명령 보기를 클릭하면 관련 명령어를 확인할 수 있습니다.

```
# 예시
aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin
public.ecr.aws/...
```

- [ ]  멀티플랫폼을 지원하도록 애플리케이션 이미지를 빌드하고, discodeit 레포지토리에 push 하세요.
- 태그명: latest, 1.2-M8
- 멀티플랫폼: linux/amd64,linux/arm64

- [ ]  AWS 콘솔에서 푸시된 이미지를 확인하세요.

#### AWS ECS 구성

- [ ]  배포 환경에서 컨테이너 실행 간 사용할 환경 변수를 정의하고, S3에 업로드하세요.

- [ ]  discodeit.env 파일을 만들어 다음의 내용을 작성하세요.

``` discodeit.env
# Spring Configuration

SPRING_PROFILES_ACTIVE=prod

# Application Configuration

STORAGE_TYPE=s3
AWS_S3_ACCESS_KEY=엑세스_키
AWS_S3_SECRET_KEY=시크릿_키
AWS_S3_REGION=ap-northeast-2
AWS_S3_BUCKET=버킷_이름
AWS_S3_PRESIGNED_URL_EXPIRATION=600

# DataSource Configuration

RDS_ENDPOINT=RDS_엔드포인트(포트 포함)
SPRING_DATASOURCE_URL=jdbc:postgresql://${RDS_ENDPOINT}/discodeit
SPRING_DATASOURCE_USERNAME=RDS_유저네임(DataGrip을 통해 생성했던 유저)
SPRING_DATASOURCE_PASSWORD=RDS_비밀번호

# JVM Configuration (프리티어 고려)

JVM_OPTS="-Xmx384m -Xms256m -XX:MaxMetaspaceSize=64m -XX:+UseSerialGC"
```

- [ ]  이 파일을 S3에 업로드하세요.

- [ ]  이 파일은 형상관리되지 않도록 주의하세요.

- [ ]  AWS ECS 콘솔에서 클러스터를 생성하세요.

- [ ]  태스크를 정의하세요.

- [ ] 태스크 생성 후 태스크 실행 역할에 S3 관련 권한을 추가하세요.
    - 환경 변수 파일을 읽기위해 필요합니다.

- [ ]  discodeit 클러스터 상세 화면에서 서비스를 생성하세요.

- [ ]  태스크의 EC2 보안 그룹의 인바운드 규칙을 설정하여 어디서든 접근할 수 있도록 하세요.

- [ ] EC2 보안 그룹에서 인바운드 규칙을 편집하세요.
- [ ] 규칙 유형으로 HTTP를 선택하세요.
- [ ] 소스로 Anywhere-IPv4를 선택하여 모든 IP를 허용하세요.
- [ ]  태스크 실행이 완료되면 해당 EC2의 퍼블릭 IP에 접속해보세요.

<br><br><br>

## 심화

- [ ] 심화 항목 1
- [ ] 심화 항목 2

## 주요 변경사항
- 

-

## 스크린샷

![image](이미지url)

## 멘토에게

- 셀프 코드 리뷰를 통해 질문 이어가겠습니다.
- 
