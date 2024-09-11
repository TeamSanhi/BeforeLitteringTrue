package net.datasa.nanum.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 북마크 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookMarkDTO {

    private Integer bookmarkNum; // 북마크 번호
    private Integer memberNum; // Member의 ID
    private Integer shareNum; // 게시글 번호

}