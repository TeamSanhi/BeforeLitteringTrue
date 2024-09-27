package net.datasa.nanum.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 회원 정보 엔티티
 */

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
@EntityListeners(AuditingEntityListener.class)
public class MemberEntity {
	// 회원 번호
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_num")
	private Integer memberNum;
	// 회원 ID
	@Column(name = "member_id", nullable = false, length = 20)
	private String memberId;
	// 회원 비밀번호
	@Column(name = "member_pw", nullable = false, length = 100)
	private String memberPw;
	// 회원 이메일
	@Column(name = "member_email", nullable = false, length = 30)
	private String memberEmail;
	// 회원 닉네임
	@Column(name = "member_nickname", nullable = false, length = 20)
	private String memberNickname;
	// 프로필 사진 이름
	@Column(name = "member_file_name", length = 100)
	private String memberFileName = "/images/profile-basic.png";
	// 회원 가입일
	@CreatedDate
	@Column(name = "create_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createDate;
	// 권한
	@Column(name = "role_name", columnDefinition = "VARCHAR(10) DEFAULT 'ROLE_USER' CHECK(role_name IN ('ROLE_USER','ROLE_ADMIN'))", length = 10)
	private String roleName;
	// 회원 상태
	@Column(name = "member_status", nullable = false, columnDefinition = "INTEGER NOT NULL DEFAULT 0 CHECK(member_status IN (0,1,2))")
	private Integer memberStatus;
	// 계정 삭제일
	@Column(name = "quit_date", columnDefinition = "TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private LocalDateTime quitDate;
	// 나눔 포인트
	@Column(name = "member_point", columnDefinition = "INTEGER DEFAULT 0")
	private Integer memberPoint;

	// ******북마크 리스트******
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private List<BookMarkEntity> bookMarkList;
}

// CREATE TABLE SHARE_BOARD (
// share_num INTEGER AUTO_INCREMENT PRIMARY KEY, -- 게시글 번호
// member_num INTEGER, -- 작성자 번호
// receiver_num INTEGER, -- 수령자 번호
// share_title VARCHAR(200) NOT NULL, -- 게시글 제목
// share_contents TEXT NOT NULL, -- 게시글 내용
// share_lat DOUBLE NOT NULL, -- 나눔 장소 위도: 상세 페이지의 지도에서 값을 받아 해당 위치를 표시
// share_lng DOUBLE NOT NULL, -- 나눔 장소 경도: 상세 페이지의 지도에서 값을 받아 해당 위치를 표시
// share_completed TINYINT(1) DEFAULT 0 CHECK (share_completed IN (0, 1)), -- 나눔
// 완료 여부: 0: 나눔 중, 1: 나눔 완료
// share_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 게시글 등록일
// report_count INTEGER DEFAULT 0, -- 신고 횟수: 3번 이상 신고당하면 삭제
// bookmark_count INTEGER DEFAULT 0, -- 북마크 개수
// member_points INTEGER DEFAULT 0, -- 회원 포인트
// CONSTRAINT FOREIGN KEY (member_num) REFERENCES MEMBER (member_num) ON DELETE
// SET NULL,
// CONSTRAINT FOREIGN KEY (receiver_num) REFERENCES MEMBER (member_num) ON
// DELETE SET NULL
// );
