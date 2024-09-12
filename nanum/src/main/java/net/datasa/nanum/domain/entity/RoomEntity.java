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
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "room")
public class RoomEntity {
    // 쪽지방 번호    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_num")
    private Integer roomNum;

    // 생성자 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_num", referencedColumnName = "member_num")
    private MemberEntity creator;

    // 게시글 주인 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_num", referencedColumnName = "member_num")
    private MemberEntity receiver;

    // 게시글 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_num", referencedColumnName = "share_num")
    private ShareBoardEntity shareBoard;

}

// CREATE TABLE ROOM (
// 	room_num            INTEGER     AUTO_INCREMENT PRIMARY KEY,                                     -- 쪽지방 번호
//  creator_num         INTEGER,                                                                    -- 생성자 번호
// 	receiver_num        INTEGER,                                                                	-- 게시글 주인 번호
//  share_num           INTEGER,                                                                    -- 게시글 번호
// 	CONSTRAINT FOREIGN KEY (creator_num) REFERENCES MEMBER (member_num) ON DELETE SET NULL,
// 	CONSTRAINT FOREIGN KEY (receiver_num) REFERENCES MEMBER (member_num) ON DELETE SET NULL,
// 	CONSTRAINT FOREIGN KEY (share_num) 	REFERENCES SHARE_BOARD (share_num) ON DELETE SET NULL
// );