package net.datasa.nanum.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 게시글 신고 entity
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Report_board")
public class ReportBoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_board_num")
    private Integer reportBoardNum; // 신고 이력 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num", referencedColumnName = "member_num")
    private MemberEntity member; // 신고 대상 회원 (회원 엔티티와 연결)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_num", referencedColumnName = "share_num")
    private ShareBoardEntity shareBoard; // 신고된 게시글 (게시글 엔티티와 연결)
}

// CREATE TABLE Report_board (
// report_board_num INTEGER AUTO_INCREMENT PRIMARY KEY, -- 게시글 신고 이력
// member_num INTEGER, -- 신고 대상 번호
// share_num INTEGER, -- 신고 게시글 번호
// CONSTRAINT FOREIGN KEY (member_num) REFERENCES MEMBER (member_num) ON DELETE
// SET NULL,
// CONSTRAINT FOREIGN KEY (share_num) REFERENCES SHARE_BOARD (share_num) ON
// DELETE CASCADE
// )