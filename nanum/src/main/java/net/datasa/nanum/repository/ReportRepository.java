package net.datasa.nanum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.datasa.nanum.domain.entity.ReportEntity;

/**
 * 유저 신고 기능 처리 Repository
 */
@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Integer> {
    
}
