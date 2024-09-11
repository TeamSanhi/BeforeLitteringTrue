package net.datasa.nanum.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@Table(name = "message")
@EntityListeners(AuditingEntityListener.class)
public class MessageEntity {
    // 쪽지 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_num")
    private Integer messageNum;
    // 발신자 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_num", referencedColumnName = "member_num")
    private MemberEntity sender;
    // 수신자 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_num", referencedColumnName = "member_num")
    private MemberEntity receiver;
    // 게시글 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_num", referencedColumnName = "share_num")
    private ShareBoardEntity shareBoard;
    // 쪽지방 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_num", referencedColumnName = "room_num")
    private RoomEntity room;
    // 쪽지 내용
    @Column(name = "message_contents", nullable = false, columnDefinition = "TEXT NOT NULL")
    private String messageContents;
    // 전송일
    @CreatedDate
    @Column(name = "deliver_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime deliverDate;
    // 읽음 여부
    @Column(name = "is_read", columnDefinition = "TINYINT(1) DEFAULT 0 CHECK(read in (0,1))")
    private Boolean isRead;
}

// CREATE TABLE MESSAGE (
// 	message_num	        INTEGER     AUTO_INCREMENT PRIMARY KEY,                                     -- 쪽지 번호
// 	sender_num	        INTEGER,                                                                    -- 발신자 번호
// 	receiver_num	    INTEGER,                                                                    -- 수신자 번호
// 	share_num	        INTEGER,                                                                    -- 게시글 번호
//  room_num	        INTEGER,                                                                    -- 쪽지방 번호
// 	message_contents	TEXT    	NOT NULL,                                                       -- 쪽지 내용
// 	deliver_date	    TIMESTAMP	DEFAULT CURRENT_TIMESTAMP,                                      -- 전송일
//  is_read				TINYINT(1)	DEFAULT 0 CHECK(read in (0,1)), 								-- 읽음 여부
//     CONSTRAINT FOREIGN KEY (sender_num)    REFERENCES MEMBER (member_num) ON DELETE SET NULL,
//     CONSTRAINT FOREIGN KEY (receiver_num) REFERENCES MEMBER (member_num) ON DELETE SET NULL,
//     CONSTRAINT FOREIGN KEY (share_num)    REFERENCES SHARE_BOARD (share_num) ON DELETE SET NULL,
//     CONSTRAINT FOREIGN KEY (room_num)     REFERENCES ROOM (room_num) ON DELETE SET NULL
// );
