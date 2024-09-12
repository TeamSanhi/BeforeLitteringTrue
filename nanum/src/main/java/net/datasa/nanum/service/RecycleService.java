package net.datasa.nanum.service;

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
    private final MemberService memberService;

    // public Page<RecycleDTO> getList(int page, int pageSize, String searchType,
    // String searchWord) {
    // page--; // 페이지 인덱스는 0부터 시작하므로 1 감소시킴

    // // 페이지 조회 조건 (현재 페이지, 페이지당 글수, 정렬 순서, 정렬 기준 컬럼)
    // Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC,
    // "recycleNum");

    // Page<RecycleEntity> entityPage;

    // switch (searchType) {
    // case "title":
    // entityPage = recycleRepository.findByTitleContaining(searchWord, pageable);
    // break;
    // case "content":
    // entityPage = recycleRepository.findByContentContaining(searchWord, pageable);
    // break;
    // case "id":
    // entityPage = recycleRepository.findByMember_MemberId(searchWord, pageable);
    // break;
    // default:
    // entityPage = recycleRepository.findAll(pageable);
    // break;
    // }

    // Page<RecycleDTO> recycleDTOPage = entityPage.map(this::convertToDTO);
    // return recycleDTOPage;
    // }

    /**
     * DB에서 조회한 게시글 정보인 RecycleEntity 객체를 RecycleDTO 객체로 변환
     * 
     * @param recycleEntity
     * @return
     */
    private RecycleDTO convertToDTO(RecycleEntity recycleEntity) {
        // RecycleEntity를 RecycleDTO로 변환하는 로직 구현
        // 예시:
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
                .orElseThrow(() -> new IllegalArgumentException("해당 글을 찾을 수 을 수 없습니다."));
        recycleEntity.setViewCount(recycleEntity.getViewCount() + 1); // 조회수 1

        RecycleDTO dto = convertToDTO(recycleEntity);

        return dto;
    }

}
