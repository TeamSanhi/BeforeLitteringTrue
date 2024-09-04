package net.datasa.nanum.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "image")
public class ImageEntity {
    
    //사진번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //자동생성
    @Column(name = "image_num")
    private Integer imageNum;
    //게시글 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_num", referencedColumnName = "share_num")
    private ShareBoardEntity shareBoard;
    //사진이름
    @Column(name = "image_file_name", length = 100)
    private String imageFileName;
}

// CREATE TABLE IMAGE (
// 	image_num	    INTEGER     AUTO_INCREMENT PRIMARY KEY,                                         -- 사진 목록 번호
// 	share_num	    INTEGER,                                                                        -- 게시글 번호
//     image_file_name	VARCHAR(100) NOT NULL,                                                          -- 사진 파일
//     CONSTRAINT FOREIGN KEY (share_num) REFERENCES SHARE_BOARD (share_num) ON DELETE CASCADE
// );