package smu.nuda.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import smu.nuda.domain.review.entity.ReviewImage;
import smu.nuda.domain.review.repository.projection.ReviewImageProjection;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    @Query("""
    SELECT
        ri.review.id as reviewId,
        ri.imageUrl as imageUrl
    FROM ReviewImage ri
    WHERE ri.review.id IN :reviewIds
    ORDER BY ri.sequence ASC NULLS LAST
""")
    List<ReviewImageProjection> findImages(@Param("reviewIds") List<Long> reviewIds);
}
