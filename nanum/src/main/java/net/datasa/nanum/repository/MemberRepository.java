package net.datasa.nanum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.datasa.nanum.domain.entity.MemberEntity;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {

    // 사용자의 ID로 DB 검색
    MemberEntity findByMemberIdEquals(String s);

    // 입력한 ID가 DB에 존재하는지 여부
    boolean existsByMemberId(String id);

    // 입력한 닉네임이 DB에 존재하는지 여부
    boolean existsByMemberNickname(String nickname);

    // 입력한 이메일이 DB에 존재하는지 여부
    boolean existsByMemberEmail(String email);

    // 사용자의 이메일로 DB 검색
    MemberEntity findByMemberEmail(String to);

    // 사용자의 일련번호로 DB 검색
    MemberEntity findByMemberNumEquals(Integer num);

    
}
