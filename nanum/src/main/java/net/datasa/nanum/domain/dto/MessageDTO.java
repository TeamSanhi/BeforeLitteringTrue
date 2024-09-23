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
    private String senderNickname;      // 발신자 닉네임
    private Integer shareNum;           // 게시글 번호
    private String shareTitle;          // 게시글 제목
    private Integer roomNum;            // 쪽지방 번호
    private String messageContents;     // 쪽지 내용
    private LocalDateTime deliverDate;  // 전송일
    private Boolean isRead;             // 읽음 여부

    // entity에 없는 변수명
    private Integer receiverNum;        // 수신자 번호
    private Integer shareWriteNum;      // 게시글 작성자 번호
    private Boolean shareCompleted;     // 나눔 완료 여부
    private Boolean hasUnreadMessages; // 읽지 않은 쪽지 여부
}
