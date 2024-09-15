package net.datasa.nanum.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="report")
public class ReportEntity {
    // 신고 이력 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="report_num")
    private Integer reportNum;
    // 신고 대상 번호(신고 대상 MemberEntity)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num", referencedColumnName = "member_num")
    private MemberEntity member;
    // 신고자 번호(신고자 MemberEntity)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_num", referencedColumnName = "member_num")
    private MemberEntity reporter;
    // 신고 사유
    @Column(name="report_reason", nullable=false, columnDefinition = "TEXT")
    private String reportReason;
}

// CREATE TABLE REPORT (
// 	report_num	    INTEGER     AUTO_INCREMENT PRIMARY KEY,                                         -- 신고 이력 번호
// 	member_num	    INTEGER,                                                                        -- 신고 대상 번호
// 	reporter_num	INTEGER,                                                                        -- 신고자 번호
// 	report_reason	TEXT	    NOT NULL,                                                           -- 신고 사유
//     CONSTRAINT FOREIGN KEY (member_num)   REFERENCES MEMBER (member_num) ON DELETE CASCADE,
//     CONSTRAINT FOREIGN KEY (reporter_num) REFERENCES MEMBER (member_num) ON DELETE SET NULL
// );
