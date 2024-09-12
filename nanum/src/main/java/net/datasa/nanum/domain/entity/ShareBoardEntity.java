package net.datasa.nanum.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SHARE_BOARD")
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 기능 사용
public class ShareBoardEntity {

    // 게시판 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_num")
    private Integer shareNum;

    // 게시글 작성자 외래키
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num", referencedColumnName = "member_num")
    private MemberEntity member;

    // 게시글 구매자 외래키
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_num", referencedColumnName = "member_num")
    private MemberEntity receiver;

    // 제목
    @Column(name = "share_title", nullable = false, length = 200)
    private String shareTitle;

    // 내용
    @Column(name = "share_contents", nullable = false, columnDefinition = "TEXT")
    private String shareContents;

    // 나눔 위도
    @Column(name = "share_lat", nullable = false)
    private Double shareLat;

    // 나눔 경도
    @Column(name = "share_lng", nullable = false)
    private Double shareLng;

    // 나눔 완료여부
    @Column(name = "share_completed", nullable = false, columnDefinition = "CHECK (share_completed IN (0,1))")
    private Boolean shareCompleted = false;

    // 나눔 날짜
    @CreatedDate // 날짜 생성
    @Column(name = "share_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime shareDate;

    // 신고 갯수
    @Column(name = "report_count", columnDefinition = "INTEGER DEFAULT 0")
    private Integer reportCount = 0;

    // 북마크 갯수
    @Column(name = "bookmark_count", columnDefinition = "INTEGER DEFAULT 0")
    private Integer bookmarkCount = 0;

    // 해당글 번호를 외래키로 들고있는 사진들을 저장할 엔티티
    @OneToMany(mappedBy = "shareBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageEntity> imageList;

}

// CREATE TABLE SHARE_BOARD (
// share_num INTEGER AUTO_INCREMENT PRIMARY KEY, -- 게시글 번호
// member_num INTEGER, -- 작성자 번호
// receiver_num INTEGER, -- 수령자 번호
// share_title VARCHAR(200) NOT NULL, -- 게시글 제목
// share_contents TEXT NOT NULL, -- 게시글 내용
// share_lat DOUBLE NOT NULL, -- 나눔 장소 위도: 게시글 상세 페이지의 지도에서 값을 받아 해당 위치를 표시
// share_lng DOUBLE NOT NULL, -- 나눔 장소 경도: 게시글 상세 페이지의 지도에서 값을 받아 해당 위치를 표시
// share_completed TINYINT(1) DEFAULT 0 CHECK (share_completed IN (0,1)), -- 나눔
// 완료 여부: 0: 나눔 중, 1: 나눔 완료
// share_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 게시글 등록일
// report_count INTEGER DEFAULT 0, -- 신고 횟수: 3번 이상 신고당하면 삭제
// bookmark_count INTEGER DEFAULT 0, -- 북마크 개수
// image_file_name VARCHAR(100), -- 대표사진
// CONSTRAINT FOREIGN KEY (member_num) REFERENCES MEMBER (member_num) ON DELETE
// SET NULL,
// CONSTRAINT FOREIGN KEY (receiver_num) REFERENCES MEMBER (member_num) ON
// DELETE SET NULL
// );