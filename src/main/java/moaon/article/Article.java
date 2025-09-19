package moaon.article;

import java.util.List;

public class Article {

    private Long id;
    private String content;
    private String summary;
    private Sector sector;
    private List<Topic> topics;

    public Article(final String[] row) {
        this.id = Long.parseLong(row[0]);
        this.content = row[1];
        this.sector = Sector.of(row[2]);
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getSummary() {
        return summary;
    }

    public Sector getSector() {
        return sector;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public void setSector(final Sector sector) {
        this.sector = sector;
    }

    public void setTopics(final List<Topic> topics) {
        this.topics = topics;
    }
}
