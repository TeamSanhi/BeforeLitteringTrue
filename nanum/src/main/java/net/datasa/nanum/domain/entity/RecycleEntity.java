package net.datasa.nanum.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "recycle_board")
public class RecycleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recycle_num")
    private Integer recycleNum;

    @Column(name = "recycle_file_name", nullable = false, length = 100)
    private String recycleFileName;

    @Column(name = "recycle_category", nullable = false, length = 20)
    private String recycleCategory;

    @Column(name = "recycle_name", nullable = false, length = 30)
    private String recycleName; // 게시글 이름 (제목)

    @Column(name = "recycle_contents", nullable = false, columnDefinition = "TEXT")
    private String recycleContents;

    @Column(name = "recycle_possible", nullable = false, length = 10)
    private String recyclePossible;

    @Column(name = "view_count", columnDefinition = "integer default 0")
    private Integer viewCount = 0; // 기본값 설정

    @CreatedDate
    @Column(name = "update_date", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime updateDate; // 수정 시간
}

// CREATE TABLE RECYCLE_BOARD (
// recycle_num INTEGER AUTO_INCREMENT PRIMARY KEY, -- 목록 번호
// recycle_file_name VARCHAR(100) NOT NULL, -- 이미지
// recycle_category VARCHAR(50) NOT NULL, -- 카테고리
// recycle_name VARCHAR(50) NOT NULL, -- 이름
// recycle_possible VARCHAR(10) NOT NULL, -- 재활용 여부
// recycle_contents TEXT NOT NULL, -- 분리수거 규정
// view_count INTEGER DEFAULT 0, -- 조회수
// update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
// -- 갱신일
// );