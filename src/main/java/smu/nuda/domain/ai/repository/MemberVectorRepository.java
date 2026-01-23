package smu.nuda.domain.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.ai.entity.MemberVector;

public interface MemberVectorRepository extends JpaRepository<MemberVector, Long> {
}
