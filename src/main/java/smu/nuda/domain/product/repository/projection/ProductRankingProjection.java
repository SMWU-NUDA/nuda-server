package smu.nuda.domain.product.repository.projection;

public interface ProductRankingProjection {
    Long getProductId();
    String getThumbnailImg();
    Long getBrandId();
    String getBrandName();
    String getProductName();
    Double getAverageRating();
    Integer getReviewCount();
    Integer getLikeCount();
    Integer getCostPrice();
}
