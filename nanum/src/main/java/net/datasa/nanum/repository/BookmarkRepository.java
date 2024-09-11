package net.datasa.nanum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.datasa.nanum.domain.entity.BookMarkEntity;

/**
 * 북마크 관련 repository
 */
@Repository
public interface BookMarkRepository extends JpaRepository<BookMarkEntity, Integer> {

}