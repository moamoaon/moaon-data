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

    /**
     * 데이터를 추가로 씁니다 (기존 파일에 이어서)
     */
    public void appendData(final List<String[]> rows) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, true))) {
            writer.writeAll(rows);
        } catch (IOException e) {
            throw new RuntimeException("파일 추가 쓰기 오류: " + filePath, e);
        }
    }
}
