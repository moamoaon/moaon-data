package moaon.article;

import java.util.ArrayList;
import java.util.List;

public class Articles {

    private final List<Article> articles;

    public Articles() {
        this.articles = new ArrayList<>();
    }

    public List<Article> getArticles() {
        return new ArrayList<>(articles);
    }

    public int size() {
        return articles.size();
    }

    public void add(final Article article) {
        articles.add(article);
    }

    /**
     * 아티클 CSV 파일용 행 데이터를 생성합니다. 헤더: id, summary, sector
     */
    public List<String[]> toArticleRows() {
        final List<String[]> rows = new ArrayList<>();

        // 헤더 추가
        rows.add(new String[]{"id", "summary", "sector"});

        // 데이터 행 추가
        for (final Article article : articles) {
            rows.add(new String[]{
                    article.getId().toString(),
                    article.getSummary(),
                    article.getSector().toString()
            });
        }

        return rows;
    }

    /**
     * 토픽 CSV 파일용 행 데이터를 생성합니다. 헤더: article_id, topics 아티클 하나당 토픽 하나씩 행으로 작성
     */
    public List<String[]> toTopicRows() {
        final List<String[]> rows = new ArrayList<>();

        // 헤더 추가
        rows.add(new String[]{"article_id", "topics"});

        // 데이터 행 추가 - 아티클 하나당 토픽 하나씩 행으로 작성
        for (final Article article : articles) {
            final List<Topic> topics = article.getTopics();
            for (final Topic topic : topics) {
                rows.add(new String[]{
                        article.getId().toString(),
                        topic.name()
                });
            }
        }

        return rows;
    }

    /**
     * 토픽 행의 총 개수를 반환합니다 (헤더 제외)
     */
    public long getTotalTopicRows() {
        return articles.stream()
                .mapToLong(article -> {
                    final List<Topic> topics = article.getTopics();
                    return (topics != null && !topics.isEmpty()) ? topics.size() : 1;
                })
                .sum();
    }
}
