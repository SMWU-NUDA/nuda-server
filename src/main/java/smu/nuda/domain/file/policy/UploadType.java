package smu.nuda.domain.file.policy;

public enum UploadType {

    REVIEW(5, "reviews"),
    PROFILE(1, "profiles");

    private final int maxCount;
    private final String basePath;

    UploadType(int maxCount, String basePath) {
        this.maxCount = maxCount;
        this.basePath = basePath;
    }

    public int maxCount() {
        return maxCount;
    }

    public String basePath() {
        return basePath;
    }
}