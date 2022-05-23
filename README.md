# PaaS-TA 컨테이너 플랫폼 API

PaaS-TA에서 제공하는 컨테이너 플랫폼 제어에 필요한 REST API를 제공합니다.

- [시작하기](#시작하기)
  - [컨테이너 플랫폼 API 빌드 방법](#컨테이너-플랫폼-API-빌드-방법)
- [문서](#문서)
- [개발 환경](#개발-환경)
- [라이선스](#라이선스)

## 시작하기
PaaS-TA 컨테이너 플랫폼 API가 수행하는 관리 작업은 다음과 같습니다.

- 컨테이너 플랫폼 자원 관리 API
- 권한 관리 API
- 사용자 관리 API

### 컨테이너 플랫폼 API 빌드 방법
PaaS-TA 컨테이너 플랫폼 API 소스 코드를 활용하여 로컬 환경에서 빌드가 필요한 경우 다음 명령어를 입력합니다.
```
$ gradle build
```


## 문서
- 컨테이너 플랫폼 활용에 대한 정보는 [PaaS-TA 컨테이너 플랫폼](https://github.com/PaaS-TA/paas-ta-container-platform)을 참조하십시오.


## 개발 환경
PaaS-TA 컨테이너 플랫폼 API의 개발 환경은 다음과 같습니다.

| Situation                      | Version |
| ------------------------------ | ------- |
| JDK                            | 8       |
| Gradle                         | 6.5     |
| Spring Boot                    | 2.3.3   |
| Spring Boot Management         | 1.0.10  |
| ApacheHttpClient               | 4.5.12  |
| JJWT                           | 0.9.1   |
| Gson                           | 2.8.6   |
| Lombok		                     | 1.18.12 |
| Jacoco		                     | 0.8.5   |
| Swagger	                       | 2.9.2   |



## 라이선스
PaaS-TA 컨테이너 플랫폼 API는 [Apache-2.0 License](http://www.apache.org/licenses/LICENSE-2.0)를 사용합니다.
