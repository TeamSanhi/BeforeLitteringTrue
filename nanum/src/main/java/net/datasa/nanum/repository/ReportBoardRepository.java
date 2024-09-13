package net.datasa.nanum.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.domain.entity.ReportBoardEntity;
import net.datasa.nanum.domain.entity.ShareBoardEntity;

/**
 * report_board 처리하는 repository
 */
@Repository
public interface ReportBoardRepository extends JpaRepository<ReportBoardEntity, Integer> {

    // 멤버 이름과 게시글 번호로 신고 이력을 검색하는 함수
    Optional<ReportBoardEntity> findByMemberAndShareBoard(MemberEntity memberNum,
            ShareBoardEntity shareNum);
}
