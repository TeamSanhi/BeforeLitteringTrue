package net.datasa.nanum.domain.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Integer messageNum;         // 쪽지 번호
    private Integer senderNum;          // 발신자 번호
    private Integer receiverNum;        // 수신자 번호
    private Integer shareNum;           // 게시글 번호
    private Integer roomNum;            // 쪽지방 번호
    private String messageContents;     // 쪽지 내용
    private LocalDateTime deliverDate;  // 전송일
    private Boolean isRead;         // 읽음 여부
}
