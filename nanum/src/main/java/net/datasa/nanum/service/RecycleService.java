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

    /**
     * 게시글 목록 조회 및 검색 기능
     * 
     * @param page       - 현재 페이지 번호
     * @param pageSize   - 페이지당 글 수
     * @param searchType - 검색 유형 (제목, 내용, 카테고리 등)
     * @param searchWord - 검색어
     * @return Page<RecycleDTO> - 페이징된 게시글 목록 (DTO)
     */
    public Page<RecycleDTO> getList(int page, int pageSize, String searchType, String searchWord) {
        // 페이지는 0부터 시작하므로 1을 빼줍니다.
        page--;

        // 조회수(viewCount)를 기준으로 내림차순 정렬
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "viewCount").and(Sort.by(Sort.Direction.ASC, "recycleNum")));

        Page<RecycleEntity> entityPage;

        // 검색어가 있는지 여부에 따라 다른 쿼리 실행
        if (searchWord != null && !searchWord.trim().isEmpty()) {
            String trimmedSearchWord = searchWord.trim();
            // 검색 유형에 따라 다른 검색 방식 적용
            if ("category".equals(searchType)) {
                entityPage = recycleRepository.findByRecycleCategoryContaining(trimmedSearchWord, pageable);
            } else {
                // 제목 또는 내용에서 검색
                entityPage = recycleRepository.findByRecycleNameContainingOrRecycleContentsContaining(trimmedSearchWord, trimmedSearchWord, pageable);
            }
        } else {
            // 검색어가 없으면 모든 게시글을 가져옴
            entityPage = recycleRepository.findAll(pageable);
        }

        // 엔티티 페이지를 DTO 페이지로 변환하여 반환
        return entityPage.map(this::convertToDTO);
    }

    /**
     * 엔티티를 DTO로 변환하는 메서드
     *
     * @param recycleEntity - 변환할 RecycleEntity 객체
     * @return RecycleDTO - 변환된 DTO 객체
     */
    private RecycleDTO convertToDTO(RecycleEntity recycleEntity) {
        return RecycleDTO.builder()
                .recycleNum(recycleEntity.getRecycleNum())              // 게시글 번호
                .recycleName(recycleEntity.getRecycleName())            // 게시글 제목
                .recycleCategory(recycleEntity.getRecycleCategory())    // 카테고리
                .recycleContents(recycleEntity.getRecycleContents())    // 내용
                .recycleFileName(recycleEntity.getRecycleFileName())    // 이미지 파일명
                .recyclePossible(recycleEntity.getRecyclePossible())    // 재활용 여부 추가
                .viewCount(recycleEntity.getViewCount())                // 조회수
                .updateDate(recycleEntity.getUpdateDate())              // 마지막 수정 날짜
                .build();
    }

    /**
     * 특정 게시글을 조회하고 조회수를 증가시킴
     *
     * @param recycleNum - 조회할 게시글 번호
     * @return RecycleDTO - 조회된 게시글의 DTO 객체
     */
    public RecycleDTO getRecycle(int recycleNum) {
        // 게시글 번호로 해당 게시글을 조회, 없을 경우 예외 발생
        RecycleEntity recycleEntity = recycleRepository.findById(recycleNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 글을 찾을 수 없습니다."));

        // 조회수 증가 처리
        recycleEntity.setViewCount(recycleEntity.getViewCount() + 1);

        // 조회된 엔티티를 DTO로 변환하여 반환
        return convertToDTO(recycleEntity);
    }
}
