package net.datasa.nanum.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.datasa.nanum.domain.entity.BookMarkEntity;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.domain.entity.ShareBoardEntity;

/**
 * 북마크 관련 repository
 */
@Repository
public interface BookMarkRepository extends JpaRepository<BookMarkEntity, Integer> {

    // memberNum과 shareNum을 사용해 북마크 존재 여부 확인
    Optional<BookMarkEntity> findByMemberAndShareBoard(MemberEntity member, ShareBoardEntity shareBoard);

    // 특정 회원(member_num)의 북마크 개수를 셉니다.
    Integer countByMember(MemberEntity member);
}