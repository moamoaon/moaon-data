# Moaon Data Processing

블로그 아티클을 AI를 활용하여 자동으로 요약, 직군 분류, 토픽 분류하는 프로그램입니다.

## 📋 기능

- **요약 생성**: 블로그 글을 웹사이트 미리보기용 200자 이내로 요약
- **직군 분류**: 아티클을 적절한 직군(FE, BE, ANDROID, IOS, INFRA, NON_TECH)으로 분류
- **토픽 분류**: 직군에 맞는 토픽을 1-3개 선택하여 분류

## 🚀 사용법

### 1. API Key 설정

Google Gemini API Key가 필요합니다.

#### API Key 발급 방법:

1. [Google AI Studio](https://aistudio.google.com/) 접속
2. Google 계정으로 로그인
3. "Get API Key" 클릭
4. "Create API Key" 선택
5. 발급받은 API Key를 복사

#### API Key 설정:

`src/main/java/moaon/Main.java` 파일의 16번째 줄을 수정하세요:

```java
private static final String GEMINI_API_KEY = "YOUR_API_KEY_HERE";
```

### 2. 입력 파일 준비

#### 파일 위치:

```
src/main/resources/input.csv
```

#### 파일 형식:

CSV 파일의 첫 번째 줄부터 데이터를 읽습니다.
엑셀 파일의 경우 CSV로 추출하기 기능을 이용하세요.

| 컬럼      | 설명        | 예시                                    |
|---------|-----------|---------------------------------------|
| id      | 아티클 고유 ID | 1, 2, 3, ...                          |
| content | 블로그 글 내용  | "React의 성능 최적화에 대해..."                |
| sector  | 기존 직군     | FE, BE, ANDROID, IOS, INFRA, NON_TECH |

#### 예시:

```csv
1,"React의 성능 최적화 기법에 대해 설명합니다. useMemo와 useCallback을 활용한 렌더링 최적화 방법을 다룹니다.",FE
2,"Spring Boot에서 JPA를 사용한 데이터베이스 연동 방법을 설명합니다.",BE
3,"GitHub Actions를 활용한 CI/CD 파이프라인 구축 방법을 다룹니다.",INFRA
```

### 3. 결과 파일 확인

프로그램 실행 후 두 개의 파일이 생성됩니다.

#### 📄 output_article.csv

**위치**: `src/main/resources/output_article.csv`

**내용**: 아티클별 요약 정보

- 헤더: `id`, `summary`, `sector`
- 각 아티클이 하나의 행을 차지

#### 📄 output_topics.csv

**위치**: `src/main/resources/output_topics.csv`

**내용**: 아티클-토픽 매핑 정보

- 헤더: `article_id`, `topics`
- 아티클 하나당 토픽 하나씩 별도 행으로 작성 (정규화된 형태)

