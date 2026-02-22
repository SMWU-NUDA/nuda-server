package smu.nuda.domain.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.log.entity.CommerceLog;

public interface CommerceLogRepository extends JpaRepository<CommerceLog, Long> {
}
