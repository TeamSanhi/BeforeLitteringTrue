package net.datasa.nanum.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.ShareBoardDTO;
import net.datasa.nanum.domain.entity.ShareBoardEntity;
import net.datasa.nanum.repository.ShareBoardRepository;

/**
 * 지도 관련 서비스
 */
@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class ShareMapService {

    // ShareBoardReposityory를 사용
    private final ShareBoardRepository shareBoardRepository;

    /**
     * 해당 위치값 안에 있는 게시물들을 불러오는 함수
     * 
     * @param swLat
     * @param swLng
     * @param neLat
     * @param neLng
     * @return
     */
    public List<ShareBoardDTO> mapList(double swLat, double swLng, double neLat, double neLng) {
        // entityList 에 게시글들을 받는다.
        List<ShareBoardEntity> entityList = shareBoardRepository.findMapList(swLat, swLng, neLat, neLng);
        log.debug("ShareMapService에서 전달받은 entityList : {}", entityList);
        // DTO로 변환할 리스트 생성
        List<ShareBoardDTO> dtoList = new ArrayList<>();
        // entityList를 DTO로 변환해서 dtoList에 저장
        for (ShareBoardEntity entity : entityList) {
            ShareBoardDTO dto = ShareBoardDTO.builder()
                    .memberNum(entity.getMember().getMemberNum()) // entity.getMember().getMemberNum() 사용
                    .shareTitle(entity.getShareTitle())
                    .memberNickname(entity.getMember().getMemberNickname()) // share_board에 없는 컬럼 DTO 따로 만들어 Nickname저장
                    .shareDate(entity.getShareDate())
                    .shareNum(entity.getShareNum())
                    .imageFileName(entity.getImageFileName())
                    .shareLat(entity.getShareLat()) // 위도
                    .shareLng(entity.getShareLng()) // 경도
                    .build();
            dtoList.add(dto);
        }
        // DTO로 변환한 entityList를 return
        return dtoList;
    }

}
