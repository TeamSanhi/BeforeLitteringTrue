package net.datasa.nanum.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.nanum.domain.entity.ImageEntity;

/**
 * 게시판 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareBoardDTO {

    private Integer shareNum; // 게시글 번호
    private Integer memberNum; // 회원 번호
    private Integer receiverNum; // reciver 번호
    private String shareTitle; // 제목
    private String shareContents; // 내용
    private Double shareLat; // 위도
    private Double shareLng; // 경도
    private Boolean shareCompleted; // 완료 여부
    private LocalDateTime shareDate;// 게시글 등록일
    private Integer reportCount; // 신고 횟수
    private Integer bookmarkCount; // 북마크 갯수
    private String imageFileName; // 파일 이름

    // entity에 없는 변수명 추가
    private String memberNickname; // 회원 이름
    private String memberId; // 회원 ID
    private String formatDate; // shareList sciprt에서 사용하기 위해 날짜
    private List<ImageDTO> imageList; // 리플 목록

}