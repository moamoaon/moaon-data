package moaon.article;

import java.util.Arrays;

public enum Topic {

    TECHNOLOGY_ADOPTION("기술 도입 과정이나 이유를 설명한 글"),
    TROUBLESHOOTING("트러블슈팅 과정을 설명한 글"),
    PERFORMANCE_OPTIMIZATION("성능 최적화 관련 글"),
    TESTING("테스트"),
    CODE_QUALITY("코드 품질, 리팩터링 관련 글"),
    SECURITY("보안"),
    ETC("기타 (적절한 토픽이 없을 때만 사용)"),
    STATE_MANAGEMENT("상태 관리"),
    UI_UX_IMPROVEMENT("UI/UX"),
    BUNDLING("번들링"),
    ARCHITECTURE_DESIGN("아키텍쳐 설계"),
    API_DESIGN("API 설계"),
    DATABASE("데이터베이스"),
    DEPLOYMENT_AND_OPERATION("배포와 운영에 관련된 글"),
    CI_CD("Github Action, Jenkins 등 CI/CD 관련 글"),
    MONITORING_AND_LOGGING("모니터링, 로깅 방법 및 전략 등"),
    NETWORK("네트워크 관련 내용"),
    TEAM_CULTURE("팀문화"),
    RETROSPECTIVE("회고"),
    PLANNING("서비스 기획"),
    DESIGN("UI 디자인");

    private final String description;

    Topic(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Topic of(String name) {
        return Arrays.stream(Topic.values())
                .filter(topic -> topic.name().equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 토픽이 존재하지 않습니다: " + name));
    }
}
