# 🕐 통학 시간을 지키고 싶다면, Do → Reburn

<img width="867" alt="image" src="https://github.com/user-attachments/assets/99773c75-5cfe-4207-92fb-6c57d611e6bc" />

# 👨‍👧‍👦 팀 소개

### **팀명 : 두부**

| **분야** | **이름** | **포지션** | **내용** |
| --- | --- | --- | --- |
| 기획 | 오예은 | 📈 서비스 기획 | 유저 리서치, 와이어프레임 작성, 서비스 정책 확립, <br /> 비즈니스 모델 구축 |
| 기획 | 이정수 | 📋 서비스 기획 | 유저 리서치, 와이어프레임 작성, 서비스 정책 확립, <br /> 비즈니스 모델 구축 |
| 디자인 | 서가영 | 🎨 기획&디자인 리드 | UI/UX디자인, GUI 디자인 |
| 디자인 | 최예지 | 🎨 디자인 | UI/UX디자인, GUI 디자인 |
| 개발 | 박규한 | 📱  프론트엔드 | 화면 UI 구현, API 연동 |
| 개발 | 손영진 | 📱 프론트엔드 | 화면 UI 구현, API 연동 |
| 개발 | 문희상 | 💻 개발 리드 | API 구현, ERD 설계, 서버 배포 |
| 개발 | 김현원 | 💻 백엔드 | API 구현, ERD 설계 |

# 🎨 기획&디자인 링크

[기획&디자인 최종 산출물 링크 [figma]](https://www.figma.com/design/4fCa8NJFAmSbPJSLnaJM4h/Handoff_Do-reburn?node-id=0-1&p=f&t=jFLkPtaI60o34zv5-0)

# 🤝 그라운드 룰

## 회의

- 데일리스크럼: 10:00 ~ 최대 10:30분까지 진행
    - 진행했던 작업 공유
    - 할 예정인 작업 공유
- 주간 회고
    - 매주 금요일 18:30 - 19:00
    - 목표했던 작업을 잘 지켰는지, 잘 못 지켰는지, 왜 잘 못지키게 되었는지 회고
    - 주말에 작업할 내용 공유
    - 주간 목표 세우기

## 소통

- **시간 약속 잘 지키기**
    - 작업 일정이 밀릴 것 같으면 미리 이야기해서 조율하기
    - 지각하지 말기(10시 까지) → 지각한 사람이 지각 안한 사람 커피 사기

- **자유롭게 질문하는 분위기 형성**
    - 이름표를 올려놓으면 집중 시간으로 판단하고 배려해서 최대 30분 이후에 질문하기
    - 해결 여부와 상관없이 이야기가 끝나면 문서화하기

- 비대면 소통
    - 카톡: 일상 또는 긴급한 일일 때 사용
    - Discord: 프로젝트 관련 소통에만 사용
    - 연락 받은 후 반응 남기기
        - 👀: 질문은 확인했고, 개인 사정으로 자정 전에 답변한다는 의미
        - ✅: 질문자가 질문이 해결되었다면 체크 표시
- 식사는 기/디/개 같이
    - 부족한 의사소통 시간은 식사 시간 알아서 해결!

## 문서화

- 회의록
    - 데일리 스크럼 + 긴급 회의
    - 안건 / 논의 / 결론
- 일일 회고 - 최대한 간단히 부담없이 적기
    - 오늘의 기분 점수 10점 중 몇 점? 이유?
    - 오늘 작업하면서 고민한 내용
    - 질문을 통해 해결한 내용
        - ex) [고민] 인증
    - 논의 사항
- 트러블 슈팅
    - 문제 상황
        - 문제가 된 코드
        - 에러 상황
    - 문제 원인
        - 에러가 발생한 이유
    - 해결
        - 해결 된 코드

# 깃 컨벤션 (git convention)

## 브랜치 전략

git-flow + feat & refactor & fix 브랜치 사용

develop: default branch로 설정. 개발할 땐 feat/refactor/fix → develop 머지

main: 기능 구현 완료 후 안정적인 버전일 때 develop → main 머지

## 브랜치 종류

```tsx
feat/#1
```

- **`main`** → 운영(prod) 환경
- **`develop`** → 개발(dev) 환경
- **`feat/#{이슈번호}`** → 기능 개발 브랜치
- **`fix/#{이슈번호}`** → 버그 수정 브랜치
- **`refactor/#{이슈번호}`** → 리팩토링 브랜치

## 커밋 컨벤션

```
feat: [커밋 내용] #1
```

| **타입** | **설명** |
| --- | --- |
| feat | 새로운 기능 구현 |
| fix | 버그 수정 (기능을 잘못 구현한 경우) |
| refactor | 코드 리팩토링 (기능은 그대로인데, 코드 개선, 폴더 변경, 변수명/함수명 수정) |
| docs | 문서 작업 |
| chore | .gitignore, yml, eslint, prettier, package.json와 같은 설정 파일 |
| design | css와 같은 스타일링 |
| init | 프로젝트 첫 세팅 |
| merge | local에서 conflict 해결 또는 PR merge |
| test | 테스트코드"만" 추가, 수정했을 때 |


## 개발 기술스택
- Language, Framework, Library
  
    ![IntelliJ IDEA Badge](https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=flat-square&logo=intellij-idea&logoColor=white)
      ![Java Badge](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=java&logoColor=white)
      ![Springboot Badge](https://img.shields.io/badge/Springboot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
      ![Gradle Badge](https://img.shields.io/badge/Gradle-02303A.svg?style=flat-square&logo=Gradle&logoColor=white)
      ![Spring Data JPA Badge](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=flat-square&logo=spring&logoColor=white)
      ![QueryDSL Badge](https://img.shields.io/badge/QueryDSL-FF9900?style=flat-square&logo=querydsl&logoColor=white)  


    **IntelliJ IDEA:** 강력한 Java IDE로, 생산적인 코드 작성과 디버깅 환경을 제공합니다.
  
    **Java:** 안정적이고 확장성이 뛰어난 범용 프로그래밍 언어입니다.

    **Spring Boot:** 스프링 기반 애플리케이션 개발을 단순화하여 빠른 프로토타입 제작을 지원합니다.

    **Gradle:** 효율적인 빌드 자동화 도구로, 의존성 관리와 다양한 플러그인 지원이 특징입니다.

    **Spring Data JPA:** 데이터베이스 접근을 간소화하는 ORM 솔루션으로, CRUD 작업을 쉽게 구현할 수 있습니다.

    **QueryDSL:** 타입 안전한 SQL 쿼리 작성을 지원하여, 컴파일 시점에 쿼리 오류를 예방할 수 있습니다.

- Test

  ![JUnit Badge](https://img.shields.io/badge/JUnit-25A162?style=flat-square&logo=junit5&logoColor=white)
  ![Mockito Badge](https://img.shields.io/badge/Mockito-FF4080?style=flat-square&logo=mockito&logoColor=white)

  **JUnit:** 자바 단위 테스트 프레임워크로, 테스트 자동화를 통해 코드 품질을 보장합니다.

  **Mockito:** 모의 객체를 활용해 의존성을 분리하고, 테스트 대상의 행위만 집중적으로 검증합니다.

- Cloud

  ![AWS Badge](https://img.shields.io/badge/AWS-232F3E?style=flat-square&logo=amazon-aws&logoColor=white)
  ![AWS EC2 Badge](https://img.shields.io/badge/AWS%20EC2-232F3E?style=flat-square&logo=amazon-ec2&logoColor=white)
  ![AWS RDS Badge](https://img.shields.io/badge/AWS%20RDS-232F3E?style=flat-square&logo=amazon-rds&logoColor=white)

  **AWS:** 다양한 클라우드 서비스를 제공하는 글로벌 플랫폼으로, 컴퓨팅, 스토리지, 데이터베이스 등 폭넓은 서비스를 지원합니다.
  **AWS EC2:**  탄력적인 클라우드 컴퓨팅 인스턴스를 제공하여, 필요에 따라 쉽게 확장 및 축소할 수 있는 가상 서버입니다.
  **AWS RDS:** 관리형 관계형 데이터베이스 서비스로, 데이터베이스 운영과 관리를 자동화하여 안정적이고 효율적인 데이터 관리를 지원합니다.
  
- CI/CD

  ![GitHub Actions Badge](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=flat-square&logo=github-actions&logoColor=white)
  ![Docker Badge](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)

  **GitHub Actions:** 자동화된 워크플로우를 통해 코드 변경 시 테스트, 빌드, 배포 등을 수행하여 개발 주기를 단축합니다.
  **Docker:** 애플리케이션을 컨테이너화하여 일관된 실행 환경을 보장하고, 배포 시 환경 차이로 인한 오류를 최소화합니다.

- Database

  ![MySQL Badge](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)
  ![Redis Badge](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)

   **MySQL:** 안정적인 데이터 저장과 관리를 제공하는 관계형 데이터베이스 관리 시스템입니다.
      **Redis:** 인메모리 데이터 저장소로 빠른 데이터 접근과 캐싱 기능을 지원합니다.

- Messaging

  ![RabbitMQ Badge](https://img.shields.io/badge/RabbitMQ-FF6600?style=flat-square&logo=rabbitmq&logoColor=white)

  **RabbitMQ:** RabbitMQ는 오픈 소스 메시지 브로커 소프트웨어로, 애플리케이션 간 비동기 메시징을 지원합니다.

- API 테스트, 명세서
  
  ![Notion Badge](https://img.shields.io/badge/Notion-000000?style=flat-square&logo=notion&logoColor=white)
  ![Postman Badge](https://img.shields.io/badge/Postman-FF6C37?style=flat-square&logo=postman&logoColor=white)
  ![Spring REST Docs Badge](https://img.shields.io/badge/Spring%20REST%20Docs-6DB33F?style=flat-square&logo=spring&logoColor=white)
  ![Swagger Badge](https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=white)

  **Notion** 협업 및 문서 관리 도구로, 프로젝트 관리와 정보 공유에 최적화되어 있습니다.
  
  **Postman** API 개발 및 테스트 도구로, API의 설계, 디버깅, 문서화를 지원합니다.

  **Spring REST Docs** 테스트 코드와 연계하여 API 문서를 자동 생성, 실제 코드와 문서의 일관성을 보장합니다.

  **Swagger** API 문서를 시각화하여 제공하며, 개발자와 비개발자 모두가 API를 쉽게 테스트하고 이해할 수 있도록 돕습니다.

- 협업 툴

  ![Discord Badge](https://img.shields.io/badge/Discord-5865F2?style=flat-square&logo=discord&logoColor=white)
  ![Slack Badge](https://img.shields.io/badge/Slack-4A154B?style=flat-square&logo=slack&logoColor=white)
  ![FigJam Badge](https://img.shields.io/badge/FigJam-FF7262?style=flat-square&logo=figma&logoColor=white)

## 회의록
[두부팀 회의록](https://sulky-koala-70d.notion.site/18394660e59780799826f7f454edb758?v=1a194660e59780d49217000cd680f7e5&pvs=74)

## 회고
### 일일 회고
[박규한 개인회고](https://sulky-koala-70d.notion.site/3e30b0d1c111493c95249b1f37338246?pvs=73)

[손영진 개인회고](https://sulky-koala-70d.notion.site/18994660e5978096888ee10a71819b9c)

[문희상 개인회고](https://sulky-koala-70d.notion.site/18994660e59780819014df71918f1f35?pvs=73)

[김현원 개인회고](https://sulky-koala-70d.notion.site/18994660e59780359cfbeec5f781f91b?pvs=73)

### 주간 회고
<img width="1025" alt="image" src="https://github.com/user-attachments/assets/812a038f-fcff-4028-bac6-8ce6883067b6" />
<img width="1302" alt="image" src="https://github.com/user-attachments/assets/8beab4ac-bd38-4478-91ae-0c3886d7e0b7" />
<img width="1032" alt="image" src="https://github.com/user-attachments/assets/b766adc3-c515-456f-93b1-0282b9c67050" />



