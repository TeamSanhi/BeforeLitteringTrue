package net.datasa.nanum.repository;

import net.datasa.nanum.domain.entity.RecycleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecycleRepository extends JpaRepository<RecycleEntity, Integer> {

    // 제목으로 검색
    Page<RecycleEntity> findByRecycleNameContaining(String recycleName, Pageable pageable);

    // 내용으로 검색
    Page<RecycleEntity> findByRecycleContentsContaining(String recycleContents, Pageable pageable);

    // 카테고리로 검색 **추가**
    Page<RecycleEntity> findByRecycleCategoryContaining(String recycleCategory, Pageable pageable);

    // 제목 또는 내용으로 검색
    Page<RecycleEntity> findByRecycleNameContainingOrRecycleContentsContaining(String recycleName, String recycleContents, Pageable pageable);
}
