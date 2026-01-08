package smu.nuda.domain.file.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.file.policy.UploadType;

import java.util.List;

@Getter
@NoArgsConstructor
public class PresignedUrlRequest {
    private UploadType type;
    private List<FileMeta> files;

    @Getter
    public static class FileMeta {
        private String fileName;
        private String contentType;
    }
}
