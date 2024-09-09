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
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
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
    // 제공자 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giver_num", referencedColumnName = "member_num")
    private MemberEntity giver;
    // 신청자 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_num", referencedColumnName = "member_num")
    private MemberEntity receiver;
    // 게시글 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_num", referencedColumnName = "share_num")
    private ShareBoardEntity shareBoard;
    // 쪽지 내용
    @Column(name = "message_contents", nullable = false, columnDefinition = "TEXT NOT NULL")
    private String messageContents;
    // 전송일
    @CreatedDate
    @Column(name = "deliver_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime deliverDate;
}

// CREATE TABLE MESSAGE (
// 	message_num	        INTEGER     AUTO_INCREMENT PRIMARY KEY,                                     -- 쪽지 번호
// 	giver_num	        INTEGER,                                                                    -- 제공자 번호
// 	receiver_num	    INTEGER,                                                                    -- 신청자 번호
// 	share_num	        INTEGER,                                                                    -- 게시글 번호
// 	message_contents	TEXT    	NOT NULL,                                                       -- 쪽지 내용
// 	deliver_date	    TIMESTAMP	DEFAULT CURRENT_TIMESTAMP,                                      -- 전송일
//     CONSTRAINT FOREIGN KEY (giver_num)    REFERENCES MEMBER (member_num) ON DELETE SET NULL,
//     CONSTRAINT FOREIGN KEY (receiver_num) REFERENCES MEMBER (member_num) ON DELETE SET NULL,
//     CONSTRAINT FOREIGN KEY (share_num)    REFERENCES SHARE_BOARD (share_num) ON DELETE SET NULL
// );
