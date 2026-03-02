package smu.nuda.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import smu.nuda.domain.review.entity.ReviewImage;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    @Query("""
    select ri.review.id as reviewId,
           ri.imageUrl as imageUrl
    from ReviewImage ri
    where ri.review.id in :reviewIds
""")
    List<Object[]> findImages(@Param("reviewIds") List<Long> reviewIds);
}
