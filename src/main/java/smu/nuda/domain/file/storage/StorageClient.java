package smu.nuda.domain.file.storage;

import smu.nuda.domain.file.dto.PresignedUrlResponse;
import smu.nuda.domain.file.policy.UploadType;

public interface StorageClient {
    PresignedUrlResponse createPresignedUrl(
            UploadType type,
            String fileName,
            String contentType
    );
}
