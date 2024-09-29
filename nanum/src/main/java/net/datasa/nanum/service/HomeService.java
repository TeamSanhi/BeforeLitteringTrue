package net.datasa.nanum.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.AlarmDTO;
import net.datasa.nanum.domain.dto.ImageDTO;
import net.datasa.nanum.domain.dto.MemberDTO;
import net.datasa.nanum.domain.dto.ShareBoardDTO;
import net.datasa.nanum.domain.entity.AlarmEntity;
import net.datasa.nanum.domain.entity.ImageEntity;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.domain.entity.ShareBoardEntity;
import net.datasa.nanum.repository.AlarmRepository;
import net.datasa.nanum.repository.MemberRepository;
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

    // MemberRepository를 주입
    private final MemberRepository memberRepository;

    // alaermrepository를 주입
    private final AlarmRepository alarmRepository;

    /**
     * 인기게시글을 불러오는 함수
     * 
     * @return DTOList로 리턴한다.
     */
    public List<ShareBoardDTO> hotList() {

        // 북마크수 기준 상위 8개의 게시글을 불러온다.
        List<ShareBoardEntity> EntityList = shareBoardRepository
                .findTop10ByShareCompletedFalseOrderByBookmarkCountDesc();

        // 반환할 DTO 생성
        List<ShareBoardDTO> DTOList = new ArrayList<ShareBoardDTO>();

        // DTO로 변환하여 List에 넣는다.
        for (ShareBoardEntity entity : EntityList) {
            // DTOList에 넣을 dto 생성하여 entity 변환하여 저장.
            ShareBoardDTO dto = boardConvertToDTO(entity);
            // DTOList에 dto 저장
            DTOList.add(dto);
        }

        return DTOList;
    }

    /**
     * 포인트가 높은 상위 8명을 불러오는 함수
     * 
     * @return
     */
    public List<MemberDTO> pointList() {

        // 포인트가 높은 상위 10명을 불러온다.
        List<MemberEntity> memberEntityList = memberRepository.findTop10ByOrderByMemberPointDesc();

        // 반환할 DTO 리스트
        List<MemberDTO> memberDTOList = new ArrayList<MemberDTO>();

        // 필요한 정보를 memberEntityList에서 반복하여 memberDTOList에 변환한다.
        for (MemberEntity entity : memberEntityList) {
            MemberDTO memberDTO = MemberDTO.builder()
                    .memberNum(entity.getMemberNum()) // 프로필 이미지 전달을 위한 회원번혼 전송
                    .memberNickname(entity.getMemberNickname()) // 닉네임
                    .memberFileName(entity.getMemberFileName()) // 프로필 사진
                    .memberPoint(entity.getMemberPoint()) // 나눔 포인트
                    .build();

            // dto 를 dtoList에 저장
            memberDTOList.add(memberDTO);
        }

        return memberDTOList;
    }

    /**
     * 전달받은 파라미터 값으로 알람을 찾는 기능
     */
    public AlarmDTO alarmCheck(Integer memberNum, Integer alarm) {

        // 전달받은 회원번호로 회원 조회
        MemberEntity memberEntity = memberRepository.findById(memberNum)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        // 해당하는 알람이 있는지 찾는다.
        AlarmEntity alarmEntity = alarmRepository.findByMemberNumAndAlarmDay(memberEntity, alarm);

        if (alarmEntity != null) {
            // AlarmDTO로 변환하여 반환
            AlarmDTO alarmDTO = AlarmDTO.builder()
                    .alarmNum(alarmEntity.getAlarmNum())
                    .memberNum(alarmEntity.getMemberNum().getMemberNum())
                    .alarmDay(alarmEntity.getAlarmDay())
                    .alarmContents(alarmEntity.getAlarmContents())
                    .build();
            // 변환된 DTO를 반환
            return alarmDTO;
        } else {
            // 알림이 없을 경우 null 처리
            return null;
        }

    }

    /**
     * 나눔글 entity에서 dto로 변환
     * 
     * @param shareBoardEntity 나눔글 엔티티
     * @return 나눔글 DTO
     */
    private ShareBoardDTO boardConvertToDTO(ShareBoardEntity shareBoardEntity) {
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
