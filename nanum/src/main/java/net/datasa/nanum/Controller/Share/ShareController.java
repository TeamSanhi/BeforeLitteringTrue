package net.datasa.nanum.Controller.Share;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.ShareBoardDTO;
import net.datasa.nanum.security.AuthenticatedUser;
import net.datasa.nanum.service.ShareService;

import org.springframework.web.bind.annotation.PostMapping;



/**
 * 나눔 게시판 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("share")
@RequiredArgsConstructor
public class ShareController {
    //서비스 객체 생성
    private final ShareService shareService;

    //application.properties 파일의 게시판 관련 설정값
    //페이지 사이즈
    @Value("${board.pageSize}")
    int pageSize;
    //페이지 블록 크기
    @Value("${board.linkSize}")
    int linkSize;
    //파일 저장경로 
    @Value("${board.uploadPath}")
    String uploadPath;

    /**
     * 나눔 list 페이지로 이동 
     * @param param
     * @return
     */
    @GetMapping("list")
    public String shareList(Model model) {
        //모든 게시글을 가져온다.
        List<ShareBoardDTO> shareList = shareService.getListAll();
        //Model에 저장한다.
        model.addAttribute("shareList", shareList);
        log.debug("sharelist 컨트롤러 지나감");
        return "shareView/shareList";
    }
    
    /**
     * 나눔글작성으로 이동 
     * @return
     */
    @GetMapping("save")
    public String shareSave() {
        log.debug("shareSave 컨트롤러 지나감");
        return "shareView/shareSave";
    }
    /**
     * 나눔 게시글 작성 후 저장 
     * @param DTO       정보를 받아옴
     * @param user      로그인한 유저 정보를 받아옴
     * @param upload    업로드할 파일 정보를 받아옴
     * @return
     */
    @PostMapping("save")
    public String postMethodName(
        @ModelAttribute ShareBoardDTO DTO,
        @AuthenticationPrincipal AuthenticatedUser user,
        @RequestParam("upload") MultipartFile upload) {
        //로그인한 유저 정보를 DTO에 저장 
        DTO.setMemberNum(user.getNum());

        //저장하여 받아온 데이터와 로그인한 유저를 확인 
        log.debug("ShareBoardDTO, user정보 확인 : {}", DTO);

        //업로드된 첨부파일
          if (upload != null) {
            log.debug("Empty : {}", upload.isEmpty());
            log.debug("파라미터 이름 : {}", upload.getName());
            log.debug("파일명 : {}", upload.getOriginalFilename());
            log.debug("파일크기 : {}", upload.getSize());
            log.debug("파일종류 : {}", upload.getContentType());
        }
        try {
           //데이터를 저장하는 함수 실행 
            shareService.Save(DTO, uploadPath, upload);
            return "redirect:/share/list";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "shareView/shareSave";
        }
    }
    
    
}
