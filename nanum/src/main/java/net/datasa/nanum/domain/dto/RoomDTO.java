package net.datasa.nanum.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private Integer roomNum;        // 쪽지방 번호
    private Integer creatorNum;     // 생성자 번호
    private Integer receiverNum;    // 게시글 주인 번호
    private Integer shareNum;       // 게시글 번호

    // entity에 없는 변수명
    private String shareTitle;      // 게시글 제목
}
