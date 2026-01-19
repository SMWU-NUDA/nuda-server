package smu.nuda.domain.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.nuda.domain.file.dto.PresignedUrlRequest;
import smu.nuda.domain.file.dto.PresignedUrlResponse;
import smu.nuda.domain.file.policy.UploadPolicy;
import smu.nuda.domain.file.storage.StorageClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PresignedUploadService {

    private final StorageClient storageClient;
    private final UploadPolicy uploadPolicy;

    public List<PresignedUrlResponse> create(PresignedUrlRequest request) {
        uploadPolicy.validate(request.getType(), request.getFiles());

        return request.getFiles().stream()
                .map(file ->
                        storageClient.createPresignedUrl(
                                request.getType(),
                                file.getFileName(),
                                file.getContentType()
                        )
                )
                .toList();
    }
}
