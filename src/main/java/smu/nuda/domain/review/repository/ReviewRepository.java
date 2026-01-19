package smu.nuda.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
