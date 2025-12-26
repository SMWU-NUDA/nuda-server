package smu.nuda.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.member.entity.MemberVector;

public interface MemberVectorRepository extends JpaRepository<MemberVector, Long> {
}
