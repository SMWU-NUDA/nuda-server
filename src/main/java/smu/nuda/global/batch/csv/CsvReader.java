package smu.nuda.global.batch.csv;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CsvReader<T> {
    List<T> read(MultipartFile file);
}
