package net.datasa.nanum.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
    public void Save(ShareBoardDTO DTO, String uploadPath, MultipartFile upload) throws IOException {

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
                   .shareBoard(shareEntity)   //imageEntity의 외래키 shareBoardEntity
                   .imageFileName(fileName)
                   .build();
            //shareEntity에 imageFileNmae 저장
            shareEntity.setImageFileName(fileName);
            //생성된 imageEntity를 저장
            imageRepository.save(imageEntity);
        }
    }

    /**
     * 모든게시글을 가져오는 함수
     * @return 모든 dto리스트를 반환
     */
    public List<ShareBoardDTO> getListAll() {
        Sort sort = Sort.by(Sort.Direction.DESC, "shareNum");        
        //전체 보기
        List<ShareBoardEntity> entityList = shareBoardRepository.findAll(sort);
        log.debug("전체 글목록 조회 : {}", entityList);
        //DTO로 변환할 리스트 생성
        List<ShareBoardDTO> dtoList = new ArrayList<>();
        //entityList를 DTO로 변환해서 dtoList에 저장
        for (ShareBoardEntity entity : entityList) {
            ShareBoardDTO dto = ShareBoardDTO.builder()
                    .shareTitle(entity.getShareTitle())
                    .memberNickname(entity.getMember().getMembeNickname()) //테이블에 없는 닉네임 DTO 따로 만들어 Nickname 저장 
                    .shareDate(entity.getShareDate())
                    .shareNum(entity.getShareNum())
                    .imageFileName(entity.getImageFileName())
                    .build();
            dtoList.add(dto);
        }
        //dtoList를 반환
        return dtoList;
    }
    
    /**
     * 파일 다운로드
     * @param boardNum          글 번호
     * @param response          응답 정보
     * @param uploadPath        파일 저장 경로
     */
    public void download(Integer boardNum, HttpServletResponse response, String uploadPath) {
        //전달된 글 번호로 글 정보 조회
        ShareBoardEntity shareBoardEntity = shareBoardRepository.findById(boardNum)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

        response.setHeader("Content-Disposition", "attachment;filename="+ shareBoardEntity.getImageFileName());

        //저장된 파일 경로
        String fullPath = uploadPath + "/" + shareBoardEntity.getImageFileName();

        //서버의 파일을 읽을 입력 스트림과 클라이언트에게 전달할 출력스트림
        FileInputStream filein = null;
        ServletOutputStream fileout = null;

        try {
            filein = new FileInputStream(fullPath);
            fileout = response.getOutputStream();

            //Spring의 파일 관련 유틸 이용하여 출력
            FileCopyUtils.copy(filein, fileout);

            filein.close();
            fileout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
