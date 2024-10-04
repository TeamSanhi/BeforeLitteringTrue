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

        // 사용자가 작성한 나눔글
        @Query("select s from ShareBoardEntity s where s.member = :memberNum")
        List<ShareBoardEntity> findGiveListByMemberNum(@Param("memberNum") MemberEntity memberNum);

        // 사용자가 수령한 나눔글
        @Query("select s from ShareBoardEntity s where s.receiver = :memberNum")
        List<ShareBoardEntity> findReceiveListByMemberNum(@Param("memberNum") MemberEntity memberNum);

        // 사용자가 북마크한 나눔글
        @Query("SELECT s FROM ShareBoardEntity s JOIN BookMarkEntity b ON s = b.shareBoard WHERE b.member = :memberNum")
        List<ShareBoardEntity> findBookmarkListByMemberNum(@Param("memberNum") MemberEntity memberNum);

        // 거래가 완료되지 않은 게시글을 북마크 개수 기준으로 상위 8개만 가져오는 메서드
        List<ShareBoardEntity> findTop10ByShareCompletedFalseOrderByBookmarkCountDesc();

        // 남서 북동 좌표안의 ***거래가 완료되지 않은 ***게시판 글들을 가져오는 함수 + 제목과 내용을 검색하는 기능 추가
        @Query("SELECT e FROM ShareBoardEntity e " +
                        "WHERE e.shareLat > :swLat AND e.shareLat < :neLat " +
                        "AND e.shareLng > :swLng AND e.shareLng < :neLng " +
                        "AND (:search = '' OR e.shareTitle LIKE %:search% OR e.shareContents LIKE %:search%) " +
                        "AND e.shareCompleted = false " + // 거래 완료전 게시글을 테이블에서 검색
                        "ORDER BY e.shareDate DESC")
        List<ShareBoardEntity> findMapList(
                        @Param("swLat") double swLat,
                        @Param("swLng") double swLng,
                        @Param("neLat") double neLat,
                        @Param("neLng") double neLng,
                        @Param("search") String search);

        // 남서 북동 좌표안의 ****거래가 완료된**** 게시글들을 불러오는 쿼리문
        @Query("SELECT e FROM ShareBoardEntity e " +
                        "WHERE e.shareLat > :swLat AND e.shareLat < :neLat " +
                        "AND e.shareLng > :swLng AND e.shareLng < :neLng " +
                        "AND e.shareCompleted = true") // 거래가 완료된 게시글을 테이블에서 검색
        List<ShareBoardEntity> findServiceList(
                        @Param("swLat") double swLat,
                        @Param("swLng") double swLng,
                        @Param("neLat") double neLat,
                        @Param("neLng") double neLng);

}