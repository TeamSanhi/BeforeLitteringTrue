package net.datasa.nanum.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import net.datasa.nanum.Util.FileManager;
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
@Service
@RequiredArgsConstructor
public class ShareService {
    //shareBoradRepository 생성자 주입
    private final ShareBoardRepository shareBoardRepository;
    // MemberRepository 생성자 주입
    private final MemberRepository memberRepository;
    // imageRepository 생성자 주입
    private final ImageRepository imageRepository;
    // 파일저장을 위한 FileManager 생성자 주입
    private final FileManager fileManager;

    /**
     * shareSave 메소드
     * @param upload        업로드한 파일
     * @param uploadPath    파일 저장할 경로
     * @param DTO           저장할 글 정보
     * @throws IOException  데이터 저장시 필요
     */
    public void shareSave(ShareBoardDTO DTO, String uploadPath, MultipartFile upload) throws IOException {

        //글작성자가 테이블에 존재하는지 확인
        MemberEntity memberEntity = memberRepository.findById(DTO.getMemberNum())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다"));
        //ShareSave에서 받아온 DTO를 Entity로 변환
        ShareBoardEntity shareEntity = ShareBoardEntity.builder()
                    .shareTitle(DTO.getShareTitle())
                    .shareContents(DTO.getShareContents())
                    .shareLat(DTO.getShareLat())
                    .shareLng(DTO.getShareLng())
                    .member(memberEntity)   // 찾은 글작성자를 외래키 member에 넣어줌 
                    .shareCompleted(false) // 나눔 확인상태 false로 초기화
                    .reportCount(0)            //  신고 수 0으로 초기화
                    .bookmarkCount(0)       // bookmark 수 0으로 초기화
                    .build();

        //변환된 shareBoardEnetity를 저장
        shareBoardRepository.save(shareEntity);

        //첨부파일이 있는 경우 imageEntity로 변환
        if (upload != null && !upload.isEmpty()) {
           String fileName = fileManager.saveFile(uploadPath, upload);
           //imageEntity 생성 및 저장
           ImageEntity imageEntity = ImageEntity.builder()
                   .shareBoardEntity(shareEntity)
                   .imageFileName(fileName)
                   .build();
           //생성된 imageEntity를 저장
           imageRepository.save(imageEntity);
        }
        

    }
    

}
