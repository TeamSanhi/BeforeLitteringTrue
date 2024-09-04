package net.datasa.nanum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.datasa.nanum.domain.entity.ShareBoardEntity;

/**
 * shareBoard 리퍼지토리
 */
@Repository
public interface ShareBoardRepository extends JpaRepository<ShareBoardEntity, Integer> {

}