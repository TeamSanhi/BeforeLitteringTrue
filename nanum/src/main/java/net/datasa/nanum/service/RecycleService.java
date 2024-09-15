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

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class RecycleService {

    private final RecycleRepository recycleRepository;

    // 게시글 목록 조회 및 검색
    public Page<RecycleDTO> getList(int page, int pageSize, String searchType, String searchWord) {
        // 페이지는 0부터 시작하므로 1을 빼줍니다.
        page--;

        // Pageable 설정 (현재 페이지, 페이지당 글 수, 정렬 순서)
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.ASC, "recycleNum");

        Page<RecycleEntity> entityPage;

        // 검색 기능 추가
        if (searchWord != null && !searchWord.trim().isEmpty()) {
            String trimmedSearchWord = searchWord.trim();
            if ("title".equals(searchType)) {
                entityPage = recycleRepository.findByRecycleNameContaining(trimmedSearchWord, pageable);
            } else if ("contents".equals(searchType)) {
                entityPage = recycleRepository.findByRecycleContentsContaining(trimmedSearchWord, pageable);
            } else if ("category".equals(searchType)) {
                entityPage = recycleRepository.findByRecycleCategoryContaining(trimmedSearchWord, pageable);
            } else {
                // 제목 또는 내용으로 검색
                entityPage = recycleRepository.findByRecycleNameContainingOrRecycleContentsContaining(trimmedSearchWord, trimmedSearchWord, pageable);
            }
        } else {
            entityPage = recycleRepository.findAll(pageable);
        }

        // 엔티티를 DTO로 변환하여 반환
        return entityPage.map(this::convertToDTO);
    }

    // 엔티티를 DTO로 변환하는 메서드
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

    // 특정 게시글 조회
    public RecycleDTO getRecycle(int recycleNum) {
        RecycleEntity recycleEntity = recycleRepository.findById(recycleNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 글을 찾을 수 없습니다."));

        // 조회수 증가
        recycleEntity.setViewCount(recycleEntity.getViewCount() + 1);

        return convertToDTO(recycleEntity);
    }
}
