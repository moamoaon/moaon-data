package moaon.article;

import static moaon.article.Topic.API_DESIGN;
import static moaon.article.Topic.ARCHITECTURE_DESIGN;
import static moaon.article.Topic.BUNDLING;
import static moaon.article.Topic.CI_CD;
import static moaon.article.Topic.CODE_QUALITY;
import static moaon.article.Topic.DATABASE;
import static moaon.article.Topic.DEPLOYMENT_AND_OPERATION;
import static moaon.article.Topic.DESIGN;
import static moaon.article.Topic.ETC;
import static moaon.article.Topic.MONITORING_AND_LOGGING;
import static moaon.article.Topic.NETWORK;
import static moaon.article.Topic.PERFORMANCE_OPTIMIZATION;
import static moaon.article.Topic.PLANNING;
import static moaon.article.Topic.RETROSPECTIVE;
import static moaon.article.Topic.SECURITY;
import static moaon.article.Topic.STATE_MANAGEMENT;
import static moaon.article.Topic.TEAM_CULTURE;
import static moaon.article.Topic.TECHNOLOGY_ADOPTION;
import static moaon.article.Topic.TESTING;
import static moaon.article.Topic.TROUBLESHOOTING;
import static moaon.article.Topic.UI_UX_IMPROVEMENT;

import java.util.Arrays;
import java.util.List;

public enum Sector {

    FE("프론트엔드 개발 관련 글",
            List.of(
                    TECHNOLOGY_ADOPTION,
                    TROUBLESHOOTING,
                    PERFORMANCE_OPTIMIZATION,
                    TESTING,
                    CODE_QUALITY,
                    STATE_MANAGEMENT,
                    UI_UX_IMPROVEMENT,
                    BUNDLING,
                    ETC
            )),
    BE("백엔드 개발 관련 글",
            List.of(
                    TECHNOLOGY_ADOPTION,
                    TROUBLESHOOTING,
                    PERFORMANCE_OPTIMIZATION,
                    TESTING,
                    CODE_QUALITY,
                    SECURITY,
                    ARCHITECTURE_DESIGN,
                    API_DESIGN,
                    DATABASE,
                    DEPLOYMENT_AND_OPERATION,
                    ETC
            )),
    ANDROID("안드로이드 개발 관련 글",
            List.of(
                    TECHNOLOGY_ADOPTION,
                    TROUBLESHOOTING,
                    PERFORMANCE_OPTIMIZATION,
                    TESTING,
                    CODE_QUALITY,
                    UI_UX_IMPROVEMENT,
                    DEPLOYMENT_AND_OPERATION,
                    ARCHITECTURE_DESIGN,
                    SECURITY,
                    ETC
            )),
    IOS("IOS 개발 관련 글",
            List.of(
                    TECHNOLOGY_ADOPTION,
                    TROUBLESHOOTING,
                    PERFORMANCE_OPTIMIZATION,
                    TESTING,
                    CODE_QUALITY,
                    UI_UX_IMPROVEMENT,
                    DEPLOYMENT_AND_OPERATION,
                    ARCHITECTURE_DESIGN,
                    SECURITY,
                    ETC
            )),
    INFRA("깃, 깃허브, AWS 등 인프라 관련 글. FE, BE, IOS, ANDROID 등 특정 직군에 종속되지 않는 내용",
            List.of(
                    TECHNOLOGY_ADOPTION,
                    TROUBLESHOOTING,
                    PERFORMANCE_OPTIMIZATION,
                    SECURITY,
                    CI_CD,
                    MONITORING_AND_LOGGING,
                    NETWORK,
                    ETC
            )),
    NON_TECH("소프트 스킬, 회고글 등 기술적인 내용과 관련이 없는 내용",
            List.of(
                    TEAM_CULTURE,
                    RETROSPECTIVE,
                    PLANNING,
                    DESIGN,
                    ETC
            ));

    private final String description;
    private final List<Topic> topics;

    Sector(final String description, final List<Topic> topics) {
        this.description = description;
        this.topics = topics;
    }

    public List<Topic> getTopics() {
        return topics;
    }
    
    public String getDescription() {
        return description;
    }

    public static Sector of(String name) {
        return Arrays.stream(Sector.values())
                .filter(sector -> sector.name().equalsIgnoreCase(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 직군이 존재하지 않습니다: " + name));
    }
}
