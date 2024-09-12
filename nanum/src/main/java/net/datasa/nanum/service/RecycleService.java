package net.datasa.nanum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.datasa.nanum.domain.dto.RecycleDTO;
import net.datasa.nanum.domain.entity.RecycleEntity;
import net.datasa.nanum.repository.RecycleRepository;

/**
 * 버려요 게시판 관련 서비스
 */
@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class RecycleService {

    private final RecycleRepository recycleRepository;
    
    public Page<RecycleDTO> getList(int page, int pageSize, String searchType, String searchWord) {
        // 페이지는 0부터 시작하므로 1을 빼줍니다.
        page--;
    
        // Pageable 설정 (현재 페이지, 페이지당 글 수, 정렬 순서)
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "recycleNum");
    
        Page<RecycleEntity> entityPage;
    
        if (searchType.equals("title")) {
            entityPage = recycleRepository.findByRecycleNameContaining(searchWord, pageable);
        } else if (searchType.equals("contents")) {
            entityPage = recycleRepository.findByRecycleContentsContaining(searchWord, pageable);
        } else {
            entityPage = recycleRepository.findAll(pageable);
        }
    
        log.debug("조회된 결과 엔티티 페이지: {}", entityPage.getContent());
    
        // 엔티티를 DTO로 변환
        return entityPage.map(this::convertToDTO);
    }
    
    /**
     * DB에서 조회한 게시글 정보인 RecycleEntity 객체를 RecycleDTO 객체로 변환
     * 
     * @param recycleEntity
     * @return
     */
    private RecycleDTO convertToDTO(RecycleEntity recycleEntity) {
        return RecycleDTO.builder()
                .recycleNum(recycleEntity.getRecycleNum())
                .recycleName(recycleEntity.getRecycleName())
                .recycleCategory(recycleEntity.getRecycleCategory())
                .recycleContents(recycleEntity.getRecycleContents())
                .recycleFileName(recycleEntity.getRecycleFileName())
                .viewCount(recycleEntity.getViewCount())
                .updateDate(recycleEntity.getUpdateDate())
                .build();
    }

    public RecycleDTO getRecycle(int recycleNum) {
        RecycleEntity recycleEntity = recycleRepository.findById(recycleNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 글을 찾을 수 없습니다."));
        recycleEntity.setViewCount(recycleEntity.getViewCount() + 1); // 조회수 1 증가

        return convertToDTO(recycleEntity);
    }
}
