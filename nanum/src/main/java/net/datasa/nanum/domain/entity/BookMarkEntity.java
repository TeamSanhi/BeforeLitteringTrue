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

/**
 * 북마크 entity
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookmark")
public class BookMarkEntity {

    // 북마크 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_num")
    private Integer bookmarkNum;

    // 회원번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num", referencedColumnName = "member_num")
    private MemberEntity member;

    // 게시글 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_num", referencedColumnName = "share_num")
    private ShareBoardEntity shareBoard;
}

// CREATE TABLE BOOKMARK (
// bookmark_num INTEGER AUTO_INCREMENT PRIMARY KEY, -- 북마크 키
// member_num INTEGER, -- 회원 번호
// share_num INTEGER, -- 게시글 번호
// CONSTRAINT FOREIGN KEY (member_num) REFERENCES MEMBER (member_num) ON DELETE
// CASCADE,
// CONSTRAINT FOREIGN KEY (share_num) REFERENCES SHARE_BOARD (share_num) ON
// DELETE CASCADE
// );