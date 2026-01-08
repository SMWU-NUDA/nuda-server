package smu.nuda.domain.file.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smu.nuda.domain.file.dto.PresignedUrlResponse;
import smu.nuda.domain.file.policy.UploadType;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageClient implements StorageClient {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public PresignedUrlResponse createPresignedUrl(UploadType type, String fileName, String contentType) {
        String key = generateKey(type, fileName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        return new PresignedUrlResponse(
                presignedRequest.url().toString(),
                buildPublicUrl(key)
        );
    }

    private String generateKey(UploadType type, String fileName) {
        String extension = extractExtension(fileName);

        return type.basePath() + "/"
                + LocalDate.now() + "/"
                + UUID.randomUUID()
                + extension;
    }

    private String extractExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        return index == -1 ? "" : fileName.substring(index);
    }

    private String buildPublicUrl(String key) {
        return "https://nuda.site/" + key;
    }

}
