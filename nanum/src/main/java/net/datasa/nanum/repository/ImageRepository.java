package net.datasa.nanum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.datasa.nanum.domain.entity.ImageEntity;

/**
 * image 테이블 관련 리퍼지토리
 */
@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Integer> {
    
}
