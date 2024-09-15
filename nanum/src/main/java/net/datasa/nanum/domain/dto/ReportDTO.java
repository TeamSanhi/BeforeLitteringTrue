package net.datasa.nanum.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Integer reportNum;          // 신고이력번호
    private Integer memberNum;          // 신고 대상 번호
    private String memberId;            // 신고 대상 ID
    private String memberNickname;      // 신고 대상 닉네임
    private Integer reporterNum;        // 신고자 번호
    private String reporterId;          // 신고자 ID  
    private String reporterNickname;    // 신고자 닉네임
    private String reportReason;        // 신고 사유
}
