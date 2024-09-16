package net.datasa.nanum.domain.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecycleDTO {
    private Integer recycleNum;                 // 게시글 번호
    private String recycleFileName;             // 파일 이름
    private String recycleCategory;             // 카테고리
    private String recycleName;                 // 게시글 이름 (제목)
    private String recycleContents;             // 게시글 내용
    private Integer viewCount;                  // 조회수
    private LocalDateTime updateDate;           // 수정 시간
}
