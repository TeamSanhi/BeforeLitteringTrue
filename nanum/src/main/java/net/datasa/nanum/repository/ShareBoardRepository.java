package net.datasa.nanum.repository;

import java.util.List;
import net.datasa.nanum.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import net.datasa.nanum.domain.entity.ShareBoardEntity;

/**
 * shareBoard 리퍼지토리
 */
@Repository
public interface ShareBoardRepository extends JpaRepository<ShareBoardEntity, Integer> {
    // 사용자가 작성한 글의 개수
    @Query("select count(*) from ShareBoardEntity where member = :memberNum")
    Integer shareCount(@Param("memberNum") MemberEntity memberNum);

    // 남서 북동 좌펴안의 게시판 글들을 부러오는 함수
    @Query("SELECT e FROM ShareBoardEntity e WHERE e.shareLat > :swLat AND e.shareLat < :neLat AND e.shareLng > :swLng AND e.shareLng < :neLng")
    List<ShareBoardEntity> findMapList(
            @Param("swLat") double swLat,
            @Param("swLng") double swLng,
            @Param("neLat") double neLat,
            @Param("neLng") double neLng);

}