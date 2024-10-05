package net.datasa.nanum.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.Util.FileManager;
import net.datasa.nanum.domain.dto.ImageDTO;
import net.datasa.nanum.domain.dto.ShareBoardDTO;
import net.datasa.nanum.domain.entity.ImageEntity;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.domain.entity.ShareBoardEntity;
import net.datasa.nanum.repository.ImageRepository;
import net.datasa.nanum.repository.MemberRepository;
import net.datasa.nanum.repository.ShareBoardRepository;

/**
 * shareService 클래스
 */
@Slf4j
@Service
@Transactional // 영속성
@RequiredArgsConstructor
public class ShareService {
    // shareBoradRepository 생성자 주입
    private final ShareBoardRepository shareBoardRepository;
    // MemberRepository 생성자 주입
    private final MemberRepository memberRepository;
    // imageRepository 생성자 주입
    private final ImageRepository imageRepository;
    // 파일관리를 위한 FileManager 생성자 주입
    private final FileManager fileManager;

    /**
     * shareSave 메소드
     *
     * @param uploads    업로드한 파일
     * @param uploadPath 파일 저장할 경로
     * @param DTO        저장할 글 정보
     * @throws IOException 데이터 저장시 필요
     */
    public void save(ShareBoardDTO DTO, String uploadPath, List<MultipartFile> uploads) throws IOException {
        // 글작성자가 테이블에 존재하는지 확인
        MemberEntity memberEntity = memberRepository.findById(DTO.getMemberNum())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다"));

        // ShareSave에서 받아온 DTO를 Entity로 변환
        ShareBoardEntity shareBoardEntity = ShareBoardEntity.builder()
                .shareTitle(DTO.getShareTitle())
                .shareContents(DTO.getShareContents())
                .shareLat(DTO.getShareLat())
                .shareLng(DTO.getShareLng())
                .member(memberEntity) // 찾은 글작성자를 외래키 member에 넣어줌
                .shareCompleted(false) // 나눔 확인상태 false로 초기화
                .reportCount(0) // 신고 수 0으로 초기화
                .bookmarkCount(0) // bookmark 수 0으로 초기화
                .build();

        // 변환된 shareBoardEnetity를 저장
        shareBoardRepository.save(shareBoardEntity);

        // 첨부파일이 있는 경우 각 파일을 imageEntity로 변환
        if (uploads != null && !uploads.isEmpty()) {
            for (MultipartFile upload : uploads) {
                if (!upload.isEmpty()) {
                    // 각 파일을 저장
                    String fileName = fileManager.saveFile(uploadPath, upload);

                    // imageEntity 생성 및 저장
                    ImageEntity imageEntity = ImageEntity.builder()
                            .shareBoard(shareBoardEntity) // imageEntity의 외래키 shareBoardEntity
                            .imageFileName(fileName)
                            .build();
                    // 이미지 엔티티 저장
                    imageRepository.save(imageEntity);
                }
            }
        }
    }

    /**
     * 글 상세 조회
     *
     * @param shareNum 글 번호
     * @return ShareBoardDTO를 반환
     */
    public ShareBoardDTO read(Integer shareNum) {

        // 전달된 글 번호로 글 정보 조회
        ShareBoardEntity shareBoardEntity = shareBoardRepository.findById(shareNum)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

        // 전달된 게시글ShareBoardDTO로 변환해서 반환
        ShareBoardDTO shareBoardDTO = ShareBoardDTO.builder()
                .memberNum(shareBoardEntity.getMember().getMemberNum()) // 작성자 번호
                .memberNickname(shareBoardEntity.getMember().getMemberNickname()) // 작성자 별명
                .shareNum(shareBoardEntity.getShareNum()) // 게시글 번호
                .shareTitle(shareBoardEntity.getShareTitle()) // 게시글 제목
                .shareContents(shareBoardEntity.getShareContents()) // 게시글 내용
                .shareDate(shareBoardEntity.getShareDate()) // 게시글 작성 날짜
                .memberId(shareBoardEntity.getMember().getMemberId()) // 게시글 작성자 이름
                .shareLat(shareBoardEntity.getShareLat()) // 게시글의 위도
                .shareLng(shareBoardEntity.getShareLng()) // 게시글의 경도
                .build();

        // ***********image정보를 shareBoardDTO에 저장하기*************************
        // shareBoardDTO의 이미지 리스트에 저장할 ImageDTO List를 생성한다.
        List<ImageDTO> imageList = new ArrayList<ImageDTO>();
        // shareBoardEntity에서 imageList를 하나씩 ImageDTO에 저장한다.
        for (ImageEntity imageEntity : shareBoardEntity.getImageList()) {
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
        shareBoardDTO.setImageList(imageList);

        // DTO를 반환
        return shareBoardDTO;
    }

    /**
     * 게시글 삭제
     *
     * @param shareNum   삭제할 글번호
     * @param username   로그인한 아이디
     * @param uploadPath 파일 저장경로
     */
    public void delete(int shareNum, String username, String uploadPath) {
        // 게시글의 존제여부 확인
        ShareBoardEntity shareBoardEntity = shareBoardRepository.findById(shareNum)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));
        // 로그인 유저와 게시글 작성자가 일치하는지 확인
        if (!shareBoardEntity.getMember().getMemberId().equals(username)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        // 첨부파일이 있는 경우 파일 삭제
        try {
            for (ImageEntity imageEntity : shareBoardEntity.getImageList()) {
                // 경로와 파일이름을 받아서 일치하는 것을 삭제한다.
                fileManager.deleteFile(uploadPath, imageEntity.getImageFileName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 게시글을 삭제
        shareBoardRepository.delete(shareBoardEntity);
    }

    /**
     * 게시글 수정
     *
     * @param shareBoardDTO 수정할 글정보
     * @param username      로그인한 아이디
     * @param uploadPath    파일 저장할 경로
     * @param uploads       업로드된 파일
     */
    public void edit(ShareBoardDTO shareBoardDTO, String username, String uploadPath, List<MultipartFile> uploads)
            throws Exception {
        // 게시글이 있는지 확인
        ShareBoardEntity shareBoardEntity = shareBoardRepository.findById(shareBoardDTO.getShareNum())
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));
        // 로그인한 유저와 게시글 작성자가 일치하는지 확인
        if (!shareBoardEntity.getMember().getMemberId().equals(username)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }
        // 전달된 정보 수정
        shareBoardEntity.setShareTitle(shareBoardDTO.getShareTitle());
        shareBoardEntity.setShareContents(shareBoardDTO.getShareContents());
        shareBoardEntity.setShareLat(shareBoardDTO.getShareLat());
        shareBoardEntity.setShareLng(shareBoardDTO.getShareLng());

        // 업로드된 파일이 있으면 기존 파일 삭제하고 새로 저장
        if (uploads != null && !uploads.isEmpty()) {
            // 기존 이미지 삭제
            if (shareBoardEntity.getImageList() != null && !shareBoardEntity.getImageList().isEmpty()) {
                for (ImageEntity imageEntity : shareBoardEntity.getImageList()) {
                    // 경로와 파일이름을 받아서 일치하는 것을 삭제한다.
                    fileManager.deleteFile(uploadPath, imageEntity.getImageFileName());
                }
                // 이미지 리스트 비우기 (orphanRemoval을 통해 참조되고 있는 image테이블의 entity도 삭제된다. 위해)
                shareBoardEntity.getImageList().clear();
            }
            // 새 이미지 저장
            for (MultipartFile upload : uploads) {
                if (!upload.isEmpty()) {
                    // 각 파일을 저장
                    String fileName = fileManager.saveFile(uploadPath, upload);

                    // imageEntity 생성 및 저장
                    ImageEntity imageEntity = ImageEntity.builder()
                            .shareBoard(shareBoardEntity) // imageEntity의 외래키 shareBoardEntity 설정
                            .imageFileName(fileName)
                            .build();

                    // 이미지 엔티티 저장 및 게시글 엔티티에 추가
                    imageRepository.save(imageEntity);
                    // 이미지 리스트에 추가
                    shareBoardEntity.getImageList().add(imageEntity);
                }
            }
        }
    }

    /**
     * imageRepository에서 이미지를 다운로드 하거나 보여주는 함수
     */
    public void download(Integer imageNum, HttpServletResponse response, String uploadPath) {

        // 전달된 글 번호로 image테이블에서 글 정보 조회
        ImageEntity imageEntity = imageRepository.findById(imageNum)
                .orElseThrow(() -> new EntityNotFoundException("이미지가 없습니다."));

        // response setHeader 설정
        response.setHeader("Content-Disposition", "attachment;filename=" + imageEntity.getImageFileName());

        // 저장된 파일 경로와 파일 이름 합한다.
        String fullPath = uploadPath + "/" + imageEntity.getImageFileName();

        // 서버의 파일을 읽을 입력 스트림과 클라이언트에게 전달할 출력스트림
        FileInputStream filein = null;
        ServletOutputStream fileout = null;

        try {
            filein = new FileInputStream(fullPath);
            fileout = response.getOutputStream();

            // Spring의 파일 관련 유틸 이용하여 출력
            FileCopyUtils.copy(filein, fileout);

            filein.close();
            fileout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 나눔글 수 합산
     *
     * @param memberNum 멤버 엔티티
     * @return 멤버 나눔글 수 합산
     */
    public Integer getShareCount(MemberEntity memberNum) {
        Integer shareCount = shareBoardRepository.shareCount(memberNum);
        return shareCount;
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
        shareBoardDTO.setShareLat(shareBoardEntity.getShareLat());
        shareBoardDTO.setShareLng(shareBoardEntity.getShareLng());
        shareBoardDTO.setShareCompleted(shareBoardEntity.getShareCompleted());
        shareBoardDTO.setShareDate(shareBoardEntity.getShareDate());
        shareBoardDTO.setReportCount(shareBoardEntity.getReportCount());
        shareBoardDTO.setBookmarkCount(shareBoardEntity.getBookmarkCount());
        shareBoardDTO.setMemberNickname(shareBoardEntity.getMember().getMemberNickname()); // 닉네임 가져옴

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

    /**
     * 작성한 나눔글 리스트 entity에서 dto로 변환
     * 
     * @param member 멤버 엔티티
     * @return 나눔글 DTO 리스트
     */
    public List<ShareBoardDTO> getGiveList(MemberEntity member) {
        List<ShareBoardEntity> shareBoardEntities = shareBoardRepository.findGiveListByMemberNum(member);
        return shareBoardEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 받은 나눔글 리스트 entity에서 dto로 변환
     * 
     * @param member 멤버 엔티티
     * @return 나눔글 DTO 리스트
     */
    public List<ShareBoardDTO> getReceiveList(MemberEntity member) {
        List<ShareBoardEntity> shareBoardEntities = shareBoardRepository.findReceiveListByMemberNum(member);
        return shareBoardEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 북마크한 나눔글 리스트 entity에서 dto로 변환
     * 
     * @param member 멤버 엔티티
     * @return 나눔글 DTO 리스트
     */
    public List<ShareBoardDTO> getBookmarkList(MemberEntity member) {
        List<ShareBoardEntity> shareBoardEntities = shareBoardRepository.findBookmarkListByMemberNum(member);
        return shareBoardEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 게시글의 나눔 완료 여부를 "완료"로 변경
    public void completeShare(Integer shareNum, Integer receiverNum) {

        // 나눔을 완료할 게시글 찾기
        ShareBoardEntity shareBoard = shareBoardRepository.findById(shareNum)
                .orElseThrow(() -> new EntityNotFoundException("게시글 정보가 없습니다."));
        // 나눔게시글을 작성한 작성자
        MemberEntity member = memberRepository.findById(shareBoard.getMember().getMemberNum())
                .orElseThrow(() -> new EntityNotFoundException("나눔글을 작성한 회원의 정보가 없습니다."));
        // 나눔을 받을 회원 정보 찾기
        MemberEntity receiver = memberRepository.findById(receiverNum)
                .orElseThrow(() -> new EntityNotFoundException("나눔받는 회원의 정보가 없습니다."));
        // 나눔이 완료된 게시글을 나눔 완료처리한다.
        shareBoard.setShareCompleted(true);
        // 나눔받은 사람의 정보를 게시글에 넣어준다.
        shareBoard.setReceiver(receiver);
        // 게시글 작성자에게 포인트를 부여한다.
        member.setMemberPoint(member.getMemberPoint() + 50);
        // 트렌젝션이라 필요없는데
        shareBoardRepository.save(shareBoard);
    }

}