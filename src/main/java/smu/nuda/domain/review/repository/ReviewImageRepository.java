package smu.nuda.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.review.entity.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
}
