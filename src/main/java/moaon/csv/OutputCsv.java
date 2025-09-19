package moaon.csv;

import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class OutputCsv {

    private final String filePath;

    public OutputCsv(final String fileName) {
        this.filePath = String.format("src/main/resources/%s", fileName);
    }

    public void writeAll(final List<String[]> rows) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeAll(rows);
        } catch (IOException e) {
            throw new RuntimeException("파일 쓰기 오류: " + filePath, e);
        }
    }
}
