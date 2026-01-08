package smu.nuda.domain.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.nuda.domain.file.dto.PresignedUrlRequest;
import smu.nuda.domain.file.dto.PresignedUrlResponse;
import smu.nuda.domain.file.error.FileErrorCode;
import smu.nuda.domain.file.policy.UploadType;
import smu.nuda.domain.file.storage.StorageClient;
import smu.nuda.global.error.DomainException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PresignedUploadService {

    private final StorageClient storageClient;

    public List<PresignedUrlResponse> create(PresignedUrlRequest request) {

        UploadType type = request.getType();

        if (request.getFiles().size() > type.maxCount()) {
            throw new DomainException(FileErrorCode.EXCEED_MAX_UPLOAD_COUNT);
        }

        return request.getFiles().stream()
                .map(file ->
                        storageClient.createPresignedUrl(
                                type,
                                file.getFileName(),
                                file.getContentType()
                        )
                )
                .toList();
    }
}
