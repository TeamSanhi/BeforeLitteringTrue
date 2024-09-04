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
    
}
