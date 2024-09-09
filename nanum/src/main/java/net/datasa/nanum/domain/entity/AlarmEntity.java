package net.datasa.nanum.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ALARM")
@EntityListeners(AuditingEntityListener.class)
public class AlarmEntity {
    // 알림 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_num")
    private Integer alarmNum;

    // 회원 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num", referencedColumnName = "member_num")
    private MemberEntity memberNum;
    
    // 요일
    // 0: 일요일, 1: 월요일, 2: 화요일, 3: 수요일, 4: 목요일, 5: 금요일, 6: 토요일
    @Column(name = "alarm_day", columnDefinition = "CHECK (alarm_day IN (0,1,2,3,4,5,6)")
    private Integer alarmDay;
    
    // 알림 내용
    @Column(name = "alarm_contents", length = 30)
    private String alarmContents;
}
