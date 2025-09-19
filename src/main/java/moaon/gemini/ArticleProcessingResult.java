package moaon.gemini;

import java.util.List;

public record ArticleProcessingResult(String summary, String sector, List<String> topics) {
}
