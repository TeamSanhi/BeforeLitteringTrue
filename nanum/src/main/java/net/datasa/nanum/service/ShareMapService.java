package net.datasa.nanum.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.ImageDTO;
import net.datasa.nanum.domain.dto.ShareBoardDTO;
import net.datasa.nanum.domain.entity.ImageEntity;
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
    // 날짜형식을 포멧하여 저장하기
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd");

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
            // entity 를 dto에 하나씩 저장하여 dtoList에 추가
            ShareBoardDTO dto = ShareBoardDTO.builder()
                    .memberNum(entity.getMember().getMemberNum()) // 게시글을 작성한 사용자번호
                    .shareNum(entity.getShareNum()) // 게시글 번호
                    .shareTitle(entity.getShareTitle()) // 제목
                    .shareContents(entity.getShareContents()) // 내용
                    .memberNickname(entity.getMember().getMemberNickname()) // share_board에 없는 컬럼 DTO 따로 만들어 Nickname저장
                    .shareDate(entity.getShareDate()) // 게시글 작성 날짜
                    .formatDate(dateFormat.format(entity.getShareDate())) // String 타입으로 포멧된 데이터 형식
                    .shareLat(entity.getShareLat()) // 위도
                    .shareLng(entity.getShareLng()) // 경도
                    .build();

            // image정보를 shareBoardDTO에 저장하기
            // shareBoardDTO의 이미지 리스트에 저장할 ImageDTO List를 생성한다.
            List<ImageDTO> imageList = new ArrayList<ImageDTO>();
            // shareBoardEntity에서 imageList를 하나씩 ImageDTO에 저장한다.
            for (ImageEntity imageEntity : entity.getImageList()) {
                // ImageDTO로 ImageEntity를 변환
                ImageDTO imageDTO = ImageDTO.builder()
                        .imageNum(imageEntity.getImageNum())
                        .shareNum(imageEntity.getShareBoard().getShareNum())
                        .imageFileName(imageEntity.getImageFileName())
                        .build();
                // 변환한 DTO를 shareBoradDTO에 저장할 imageList에 하나씩 저장
                imageList.add(imageDTO);
            }
            // 완성된 imageList를 shareBoardDTO의 imageList에 저장한다.
            dto.setImageList(imageList);

            dtoList.add(dto);
        }

        // DTO로 변환한 entityList를 return
        return dtoList;
    }

}
