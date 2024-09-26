package net.datasa.nanum.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.ImageDTO;
import net.datasa.nanum.domain.dto.ShareBoardDTO;
import net.datasa.nanum.domain.entity.ImageEntity;
import net.datasa.nanum.domain.entity.ShareBoardEntity;
import net.datasa.nanum.repository.ShareBoardRepository;

/**
 * 홈 컨트롤러의 요청을 처리하는 서비스
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class HomeService {

    // shareBoardRepository를 주입
    private final ShareBoardRepository shareBoardRepository;

    /**
     * 인기게시글을 불러오는 함수
     * 
     * @return DTOList로 리턴한다.
     */
    public List<ShareBoardDTO> hotList() {

        // 북마크수 기준 상위 8개의 게시글을 불러온다.
        List<ShareBoardEntity> EntityList = shareBoardRepository
                .findTop8ByShareCompletedFalseOrderByBookmarkCountDesc();

        // 반환할 DTO 생성
        List<ShareBoardDTO> DTOList = new ArrayList<ShareBoardDTO>();

        // DTO로 변환하여 List에 넣는다.
        for (ShareBoardEntity entity : EntityList) {
            // DTOList에 넣을 dto 생성하여 entity 변환하여 저장.
            ShareBoardDTO dto = convertToDTO(entity);
            // DTOList에 dto 저장
            DTOList.add(dto);
        }

        return DTOList;
    }

    /**
     * 나눔글 entity에서 dto로 변환
     * 
     * @param shareBoardEntity 나눔글 엔티티
     * @return 나눔글 DTO
     */
    private ShareBoardDTO convertToDTO(ShareBoardEntity shareBoardEntity) {
        ShareBoardDTO shareBoardDTO = new ShareBoardDTO();
        shareBoardDTO.setShareNum(shareBoardEntity.getShareNum());
        shareBoardDTO.setMemberNum(shareBoardEntity.getMember().getMemberNum());
        if (shareBoardEntity.getReceiver() != null) {
            shareBoardDTO.setReceiverNum(shareBoardEntity.getReceiver().getMemberNum());
        } else {
            shareBoardDTO.setReceiverNum(null);
        }
        shareBoardDTO.setShareTitle(shareBoardEntity.getShareTitle());
        shareBoardDTO.setShareContents(shareBoardEntity.getShareContents());
        shareBoardDTO.setBookmarkCount(shareBoardEntity.getBookmarkCount());
        shareBoardDTO.setMemberNickname(shareBoardEntity.getMember().getMemberNickname()); // 닉네임 받아오기

        List<ImageDTO> imageDTOList = new ArrayList<>();
        for (ImageEntity imageEntity : shareBoardEntity.getImageList()) {
            if (imageEntity != null) {
                ImageDTO imageDTO = new ImageDTO(
                        imageEntity.getImageNum(),
                        imageEntity.getShareBoard().getShareNum(),
                        imageEntity.getImageFileName());
                imageDTOList.add(imageDTO);
            }
        }
        shareBoardDTO.setImageList(imageDTOList);

        return shareBoardDTO;
    }

}
