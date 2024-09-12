package net.datasa.nanum.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportBoardDTO {

    private Integer reportBoardNum; // 신고 이력 번호
    private Integer memberNum; // 신고 대상 회원 번호
    private Integer shareNum; // 신고된 게시글 번호

}
