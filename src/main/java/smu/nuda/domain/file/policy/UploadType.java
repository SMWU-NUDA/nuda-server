package smu.nuda.domain.file.policy;

import java.util.Set;

public enum UploadType {

    REVIEW(5, "reviews", Set.of("image/png", "image/jpeg", "image/webp")),
    PROFILE(1, "profiles", Set.of("image/png", "image/jpeg"));

    private final int maxCount;
    private final String basePath;
    private final Set<String> allowedContentTypes;

    UploadType(int maxCount, String basePath, Set<String> allowedContentTypes) {
        this.maxCount = maxCount;
        this.basePath = basePath;
        this.allowedContentTypes = allowedContentTypes;
    }

    public int maxCount() {
        return maxCount;
    }

    public String basePath() {
        return basePath;
    }

    public boolean supports(String contentType) {
        return allowedContentTypes.contains(contentType);
    }
}