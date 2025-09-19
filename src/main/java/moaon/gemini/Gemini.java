package moaon.gemini;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import moaon.article.Article;
import moaon.article.Sector;
import moaon.article.Topic;

public class Gemini {

    private final Client client;

    public Gemini(final String apiKey) {
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    /**
     * 아티클의 summary, sector, topics를 한 번의 API 호출로 처리합니다.
     */
    public ArticleProcessingResult processArticle(final Article article) {
        final String content = article.getContent();
        final String oldSector = article.getSector().toString();

        final String prompt = getIntegratedPromptTemplate(oldSector, content);
        final String response = execute(prompt);

        return parseIntegratedResponse(response, oldSector);
    }

    private String execute(final String prompt) {
        final GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                prompt,
                null
        );
        return response.text();
    }

    private String getIntegratedPromptTemplate(final String oldSector, final String content) {
        // Sector enum의 description과 각 직군별 토픽 정보를 포함한 프롬프트 생성
        final StringBuilder sectorAndTopicsInfo = new StringBuilder();
        for (final Sector s : Sector.values()) {
            sectorAndTopicsInfo.append(String.format("- %s: %s%n", s.name(), s.getDescription()));
            sectorAndTopicsInfo.append("  사용 가능한 토픽: ");
            final String topics = s.getTopics().stream()
                    .map(topic -> String.format("%s(%s)", topic.name(), topic.getDescription()))
                    .collect(Collectors.joining(", "));
            sectorAndTopicsInfo.append(topics).append("%n%n");
        }

        final String template = String.join("%n",
                "다음 블로그 글에 대해 다음 3가지를 한 번에 처리해주세요:",
                "1. 웹사이트 미리보기 용도로 200자 이내 요약",
                "2. 직군 분류 (INFRA 직군으로 변경할 필요가 있는지 판단)",
                "3. 결정된 직군에 맞는 적절한 토픽 선택 (1-3개)",
                "",
                "각 직군의 설명과 사용 가능한 토픽:",
                "%s",
                "기존 직군: %s",
                "블로그 글: %s",
                "",
                "답변 형식 (정확히 이 형식으로만 답변해주세요):",
                "SUMMARY: [요약 내용]",
                "SECTOR: [직군명]",
                "TOPICS: [토픽1, 토픽2, 토픽3]",
                "",
                "중요:",
                "- SECTOR는 반드시 다음 중 하나만: FE, BE, ANDROID, IOS, INFRA, NON_TECH",
                "- TOPICS는 위에서 결정한 SECTOR의 '사용 가능한 토픽' 중에서만 선택",
                "- 토픽명은 정확히 위에 나열된 형식으로만 사용 (예: TECHNOLOGY_ADOPTION)",
                "- 설명이나 추가 텍스트 없이 정확한 형식으로만 답변"
        );
        return String.format(template, sectorAndTopicsInfo.toString(), oldSector, content);
    }

    private String parseSectorResponse(final String response, final String fallback) {
        final String cleanResponse = response.trim().toUpperCase();

        // Sector enum의 모든 값들 사용
        for (final Sector sector : Sector.values()) {
            if (cleanResponse.contains(sector.name())) {
                return sector.name();
            }
        }

        // 유효한 값이 없으면 기존 값 반환
        System.out.printf("AI 응답에서 유효한 직군을 찾을 수 없습니다. 응답: '%s', 기존 값 사용: %s%n", response, fallback);
        return fallback;
    }

    private List<String> parseTopicsResponse(final String response, final List<Topic> availableTopics) {
        final String cleanResponse = response.trim();

        // 쉼표로 분리하고 공백 제거
        final List<String> topics = Arrays.stream(cleanResponse.split(","))
                .map(String::trim)
                .filter(topic -> !topic.isEmpty())
                .toList();

        // 유효한 토픽만 필터링 (해당 직군에서 사용 가능한 토픽 중에서만)
        final List<String> validTopics = topics.stream()
                .filter(topic -> availableTopics.stream()
                        .anyMatch(availableTopic -> availableTopic.name().equals(topic)))
                .toList();

        if (validTopics.isEmpty()) {
            System.out.printf("AI 응답에서 유효한 토픽을 찾을 수 없습니다. 응답: '%s', 기본 토픽 사용: ETC%n", response);
            return List.of("ETC");
        }

        return validTopics;
    }

    private ArticleProcessingResult parseIntegratedResponse(final String response, final String fallbackSector) {
        final String[] lines = response.split("\\n");
        String summary = "";
        String sector = fallbackSector;
        List<String> topics = List.of("ETC");

        // 먼저 SECTOR를 파싱하여 변경된 직군을 확인
        for (final String line : lines) {
            final String trimmedLine = line.trim();
            if (trimmedLine.startsWith("SECTOR:")) {
                final String sectorResponse = trimmedLine.substring("SECTOR:".length()).trim();
                sector = parseSectorResponse(sectorResponse, fallbackSector);
                break;
            }
        }

        // 변경된 직군의 토픽 목록 가져오기
        final List<Topic> availableTopics = Sector.valueOf(sector).getTopics();

        // 나머지 정보 파싱
        for (final String line : lines) {
            final String trimmedLine = line.trim();
            if (trimmedLine.startsWith("SUMMARY:")) {
                summary = trimmedLine.substring("SUMMARY:".length()).trim();
            } else if (trimmedLine.startsWith("TOPICS:")) {
                final String topicsResponse = trimmedLine.substring("TOPICS:".length()).trim();
                topics = parseTopicsResponse(topicsResponse, availableTopics);
            }
        }

        // summary가 비어있으면 기본값 설정
        if (summary.isEmpty()) {
            summary = "요약을 생성할 수 없습니다.";
        }

        return new ArticleProcessingResult(summary, sector, topics);
    }
}
