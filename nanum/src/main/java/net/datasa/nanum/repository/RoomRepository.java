package net.datasa.nanum.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.domain.entity.RoomEntity;
import net.datasa.nanum.domain.entity.ShareBoardEntity;

/**
 * ROOM (쪽지방) 처리 repository
 */
@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Integer> {

    // 해당 게시글의 현재 사용자가 생성한 쪽지방을 검색하여 반환
    RoomEntity findByCreatorAndShareBoard(MemberEntity member, ShareBoardEntity shareBoard);
    // 해당 게시글의 현재 사용자가 생성한 쪽지방이 있는지 여부 검색
    boolean existsByCreatorAndShareBoard(MemberEntity member, ShareBoardEntity shareBoard);
    // 현재 사용자가 있는 쪽지방을 모두 검색
    List<RoomEntity> findByCreatorOrReceiver(MemberEntity creator, MemberEntity receiver);
}
