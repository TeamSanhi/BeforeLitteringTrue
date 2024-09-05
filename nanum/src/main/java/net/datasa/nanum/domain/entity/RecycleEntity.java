package net.datasa.nanum.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedDate;
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
@Table(name="recycle_board")
public class RecycleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="recycle_num")
    private Integer recycleNum;

    @Column(name="recycle_file_name", nullable = false, length = 100)
    private String recycleFileName;
    
    @Column(name="recycle_category", nullable = false, length = 20)
    private String recycleCategory;

    @Column(name="recycle_name", nullable = false, length = 30)
    private String recycleName;

    @Column(name="recycle_contents", nullable = false, columnDefinition = "TEXT")
    private String recycleContents;

    @Column(name="view_count", columnDefinition = "integer default 0")
    private Integer viewCount = 0;

    @LastModifiedDate
    @Column(name="update_date", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime updateDate;

    public Object getTitle() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTitle'");
    } 
}
