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
	private String memberFileName;
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

	// ******북마크 리스트******
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BookMarkEntity> bookMarkList;
}

// CREATE TABLE MEMBER (
// member_num INTEGER AUTO_INCREMENT PRIMARY KEY,
// member_id VARCHAR(20) NOT NULL,
// member_pw VARCHAR(100) NOT NULL,
// member_email VARCHAR(30) NOT NULL,
// member_nickname VARCHAR(20) NOT NULL,
// member_file_name VARCHAR(100),
// create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
// role_name VARCHAR(10) DEFAULT "ROLE_USER" CHECK(role_name IN
// ("ROLE_USER","ROLE_ADMIN")),
// member_status INTEGER NOT NULL DEFAULT 0 CHECK(member_status IN (0,1,2)),
// quit_date TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
// );
