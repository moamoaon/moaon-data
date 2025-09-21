package moaon;

import java.util.List;
import java.util.Scanner;
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

    private final InputCsv input;
    private final OutputCsv outputArticle;
    private final OutputCsv outputTopics;
    private final Gemini gemini;

    public Main(String apiKey) {
        this.input = new InputCsv(INPUT_FILE_NAME);
        this.outputArticle = new OutputCsv(OUTPUT_ARTICLE_FILE_NAME);
        this.outputTopics = new OutputCsv(OUTPUT_TOPICS_FILE_NAME);
        this.gemini = new Gemini(apiKey);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("========== Moaon Data Processing ==========");
        System.out.print("Gemini API Key를 입력하세요: ");
        String apiKey = scanner.nextLine().trim();

        if (apiKey.isBlank()) {
            System.out.println("API Key가 입력되지 않았습니다. 프로그램을 종료합니다.");
            System.exit(1);
        }

        Main main = new Main(apiKey);

        System.out.println("처리 방식을 선택하세요:");
        System.out.println("1. 기존 방식 (모든 데이터 처리 후 한 번에 저장)");
        System.out.println("2. 배치 방식 (배치 단위로 처리하며 중간 저장)");
        System.out.print("선택 (1 또는 2): ");

        int choice = scanner.nextInt();

        if (choice == 1) {
            main.runOriginal();
        } else if (choice == 2) {
            System.out.print("배치 크기를 입력하세요 (기본값: 10): ");
            int batchSize = scanner.nextInt();
            if (batchSize <= 0) {
                batchSize = 10;
            }
            main.runBatch(batchSize);
        } else {
            System.out.println("잘못된 선택입니다. 프로그램을 종료합니다.");
            System.exit(1);
        }

        scanner.close();
    }

    /**
     * 기존 방식: 모든 데이터를 처리한 후 한 번에 저장
     */
    public void runOriginal() {
        // input csv 파일은 {id, content, sector} 형식으로 저장되어 있어야 함
        // 첫 번째 줄 부터 읽어옴
        System.out.println("========== CSV 읽기 작업 시작 ==========");
        final List<String[]> inputRows = input.readAll();
        System.out.println("========== CSV 읽기 작업 끝 ==========");

        System.out.println("========== Gemini 작업 시작 (기존 방식) ==========");
        final Articles articles = new Articles();
        for (int i = 0; i < inputRows.size(); i++) {
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

    /**
     * 배치 방식: 배치 단위로 처리하며 중간 저장
     */
    public void runBatch(int batchSize) {
        // input csv 파일은 {id, content, sector} 형식으로 저장되어 있어야 함
        // 첫 번째 줄 부터 읽어옴
        System.out.println("========== CSV 읽기 작업 시작 ==========");
        final List<String[]> inputRows = input.readAll();
        System.out.println("========== CSV 읽기 작업 끝 ==========");

        System.out.printf("========== 배치 처리 시작 (배치 크기: %d) ==========%n", batchSize);

        boolean isFirstBatch = true;

        for (int startIndex = 0; startIndex < inputRows.size(); startIndex += batchSize) {
            int endIndex = Math.min(startIndex + batchSize, inputRows.size());
            int batchNumber = (startIndex / batchSize) + 1;

            System.out.printf("========== 배치 %d 처리 시작 (인덱스 %d-%d) ==========%n",
                    batchNumber, startIndex, endIndex - 1);

            final Articles batchArticles = new Articles();

            for (int i = startIndex; i < endIndex; i++) {
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

                batchArticles.add(article);
                System.out.printf("Row: %d | Article ID: %d - 끝%n", i, article.getId());
            }

            // 배치 결과를 파일에 저장
            writeBatchResults(batchArticles, isFirstBatch);
            isFirstBatch = false;

            System.out.printf("========== 배치 %d 처리 완료 ==========%n", batchNumber);
        }

        System.out.println("========== 전체 배치 처리 완료 ==========");
        System.out.println("Done.");
    }

    /**
     * 기존 방식용: 모든 데이터를 한 번에 저장
     */
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

    /**
     * 배치 방식용: 배치별로 결과 저장
     */
    private void writeBatchResults(final Articles articles, final boolean isFirstBatch) {
        System.out.println("========== 배치 결과 저장 시작 ==========");

        final List<String[]> articleRows = articles.toArticleRows();
        final List<String[]> topicRows = articles.toTopicRows();

        if (isFirstBatch) {
            // 첫 번째 배치: 헤더 포함하여 새로 작성
            outputArticle.writeAll(articleRows);
            outputTopics.writeAll(topicRows);
        } else {
            // 두 번째 배치부터: 헤더 제외하고 데이터만 추가
            if (articleRows.size() > 1) {
                outputArticle.appendData(articleRows.subList(1, articleRows.size()));
            }
            if (topicRows.size() > 1) {
                outputTopics.appendData(topicRows.subList(1, topicRows.size()));
            }
        }

        System.out.printf("배치 결과 저장 완료 - 아티클 %d개, 토픽 행 %d개%n",
                articles.size(), articles.getTotalTopicRows());
        System.out.println("========== 배치 결과 저장 끝 ==========");
    }
}
