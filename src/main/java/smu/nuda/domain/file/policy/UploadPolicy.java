package smu.nuda.domain.file.policy;

import org.springframework.stereotype.Component;
import smu.nuda.domain.file.dto.PresignedUrlRequest;
import smu.nuda.domain.file.error.FileErrorCode;
import smu.nuda.global.error.DomainException;

import java.util.List;

@Component
public class UploadPolicy {

    public void validate(UploadType type, List<PresignedUrlRequest.FileMeta> files) {
        validateCount(type, files);
        validateContentTypes(type, files);
    }

    private void validateCount(UploadType type, List<PresignedUrlRequest.FileMeta> files) {
        if (files.size() > type.maxCount()) throw new DomainException(FileErrorCode.EXCEED_MAX_UPLOAD_COUNT);
    }

    private void validateContentTypes(UploadType type, List<PresignedUrlRequest.FileMeta> files) {
        for (var file : files) {
            if (!type.supports(file.getContentType())) {
                throw new DomainException(FileErrorCode.INVALID_CONTENT_TYPE);
            }
        }
    }
}
