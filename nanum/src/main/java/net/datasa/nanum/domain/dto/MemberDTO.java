package net.datasa.nanum.domain.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원 정보 DTO
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Integer memberNum; // 회원 번호
    private String memberId; // 회원 ID
    private String memberPw; // 회원 비밀번호
    private String memberEmail; // 회원 이메일
    private String memberNickname; // 회원 닉네임
    private String memberFileName; // 프로필 사진 이름
    private LocalDateTime createDate; // 회원 가입일
    private String roleName; // 권한
    private Integer memberStatus; // 회원 상태
    private LocalDateTime quitDate; // 계정 삭제일 \
    private Integer memberPoint; // 나눔 포인트
}
