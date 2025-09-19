package moaon;

import java.util.List;
import moaon.article.Article;
import moaon.article.Articles;
import moaon.article.Sector;
import moaon.article.Topic;
import moaon.csv.InputCsv;
import moaon.csv.OutputCsv;
import moaon.gemini.ArticleProcessingResult;
import moaon.gemini.Gemini;

public class Main {

    private static final String INPUT_FILE_NAME = "input.csv";
    private static final String OUTPUT_ARTICLE_FILE_NAME = "output_article.csv";
    private static final String OUTPUT_TOPICS_FILE_NAME = "output_topics.csv";
    private static final String GEMINI_API_KEY = "";

    private final InputCsv input = new InputCsv(INPUT_FILE_NAME);
    private final OutputCsv outputArticle = new OutputCsv(OUTPUT_ARTICLE_FILE_NAME);
    private final OutputCsv outputTopics = new OutputCsv(OUTPUT_TOPICS_FILE_NAME);
    private final Gemini gemini = new Gemini(GEMINI_API_KEY);

    public static void main(String[] args) {
        if (GEMINI_API_KEY.isBlank()) {
            throw new IllegalStateException("API KEY가 없습니다.");
        }

        new Main().run();
    }

    public void run() {
        // input csv 파일은 {id, content, sector} 형식으로 저장되어 있어야 함
        // 첫 번째 줄 부터 읽어옴
        System.out.println("========== CSV 읽기 작업 시작 ==========");
        final List<String[]> inputRows = input.readAll();
        System.out.println("========== CSV 읽기 작업 끝 ==========");

        System.out.println("========== Gemini 작업 시작 ==========");
        final Articles articles = new Articles();
        for (int i = 0; i < 1; i++) {
            final String[] row = inputRows.get(i);
            final Article article = new Article(row);
            System.out.printf("Row: %d | Article ID: %d - 시작%n", i, article.getId());

            // summary, sector, topics를 한 번에 처리
            final ArticleProcessingResult result = gemini.processArticle(article);

            // 결과 적용
            article.setSummary(result.summary());

            try {
                article.setSector(Sector.valueOf(result.sector()));
            } catch (IllegalArgumentException e) {
                System.err.printf("아티클 %d: 유효하지 않은 직군 '%s', 기존 직군 유지%n", article.getId(), result.sector());
                // 기존 직군 유지
            }

            try {
                article.setTopics(result.topics().stream().map(Topic::of).toList());
            } catch (IllegalArgumentException e) {
                System.err.printf("아티클 %d: 유효하지 않은 토픽이 포함됨, 기본 토픽 사용%n", article.getId());
                article.setTopics(List.of(Topic.ETC));
            }

            System.out.printf("Row: %d | Article ID: %d - 통합 처리 완료%n", i, article.getId());

            articles.add(article);
            System.out.printf("Row: %d | Article ID: %d - 끝%n", i, article.getId());
        }
        System.out.println("========== Gemini 작업 끝 ==========");

        // 쓰기
        writeArticleCsv(articles);
        writeTopicsCsv(articles);

        System.out.println("Done.");
    }

    private void writeArticleCsv(final Articles articles) {
        System.out.println("========== 아티클 CSV 쓰기 작업 시작 ==========");
        final List<String[]> articleRows = articles.toArticleRows();
        outputArticle.writeAll(articleRows);
        System.out.printf("outputArticle.csv 파일이 작성되었습니다. 총 %d개의 아티클이 포함되었습니다.%n", articles.size());
        System.out.println("========== 아티클 CSV 쓰기 작업 끝 ==========");
    }

    private void writeTopicsCsv(final Articles articles) {
        System.out.println("========== 토픽 CSV 쓰기 작업 시작 ==========");
        final List<String[]> topicRows = articles.toTopicRows();
        outputTopics.writeAll(topicRows);
        System.out.printf("outputTopics.csv 파일이 작성되었습니다. 총 %d개의 토픽 행이 포함되었습니다.%n", articles.getTotalTopicRows());
        System.out.println("========== 토픽 CSV 쓰기 작업 끝 ==========");
    }
}
