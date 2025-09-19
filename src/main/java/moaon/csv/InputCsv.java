package moaon.csv;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class InputCsv {

    private final String fileName;

    public InputCsv(final String fileName) {
        this.fileName = String.format("/%s", fileName);
    }

    public List<String[]> readAll() {
        try (InputStream inputStream = getClass().getResourceAsStream(fileName);
             CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            return reader.readAll();
        } catch (NullPointerException e) {
            throw new RuntimeException("파일이 존재하지 않습니다: " + fileName, e);
        } catch (IOException | CsvException e) {
            throw new RuntimeException("파일 읽기 오류: " + fileName, e);
        }
    }
}
