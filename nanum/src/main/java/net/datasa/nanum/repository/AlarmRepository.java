package net.datasa.nanum.repository;

import net.datasa.nanum.domain.entity.AlarmEntity;
import net.datasa.nanum.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, Integer> {
    // 알림 리스트 생성
    @Query("SELECT a FROM AlarmEntity a WHERE a.memberNum = :memberNum")
    List<AlarmEntity> listAlarmByMemberNum(@Param("memberNum") MemberEntity memberNum);
}
