package net.datasa.nanum.domain.dto;

import org.antlr.v4.runtime.misc.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Image DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {
    // 사진 번호
    private Integer imageNum;
    // 게시글 번호
    private Integer shareNum;
    // 파일 이름
    private String imageFileName;
}
