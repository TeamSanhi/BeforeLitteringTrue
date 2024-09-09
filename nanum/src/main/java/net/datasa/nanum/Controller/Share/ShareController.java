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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
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
    // 서비스 객체 생성
    private final ShareService shareService;

    // application.properties 파일의 게시판 관련 설정값
    // 페이지 사이즈
    @Value("${board.pageSize}")
    int pageSize;
    // 페이지 블록 크기
    @Value("${board.linkSize}")
    int linkSize;
    // 파일 저장경로
    @Value("${board.uploadPath}")
    String uploadPath;

    /**
     * 나눔 list 페이지로 이동
     * 
     * @param param
     * @return
     */
    @GetMapping("list")
    public String shareList(Model model) {
        // ajax로 지도가 변할때 마다 지도에 표시되는 게시글을 업데이트 하며 게시판으로 보여줌으로 필요없어 졌음으로 경로의 역할만 한다.
        // // 모든 게시글을 가져온다.
        // List<ShareBoardDTO> shareList = shareService.getListAll();
        // // Model에 저장한다.
        // model.addAttribute("shareList", shareList);
        log.debug("sharelist 컨트롤러 지나감");
        return "shareView/shareList";
    }

    /**
     * 나눔글작성으로 이동
     * 
     * @return
     */
    @GetMapping("save")
    public String shareSave() {
        log.debug("shareSave 컨트롤러 지나감");
        return "shareView/shareSave";
    }

    /**
     * 나눔 게시글 작성 후 저장
     * 
     * @param DTO    정보를 받아옴
     * @param user   로그인한 유저 정보를 받아옴
     * @param upload 업로드할 파일 정보를 받아옴
     * @return
     */
    @PostMapping("save")
    public String postMethodName(
            @ModelAttribute ShareBoardDTO DTO,
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam("upload") MultipartFile upload) {
        // 로그인한 유저 정보를 DTO에 저장
        DTO.setMemberNum(user.getNum());

        // 저장하여 받아온 데이터와 로그인한 유저를 확인
        log.debug("ShareBoardDTO, user정보 확인 : {}", DTO);

        // 업로드된 첨부파일
        if (upload != null) {
            log.debug("Empty : {}", upload.isEmpty());
            log.debug("파라미터 이름 : {}", upload.getName());
            log.debug("파일명 : {}", upload.getOriginalFilename());
            log.debug("파일크기 : {}", upload.getSize());
            log.debug("파일종류 : {}", upload.getContentType());
        }
        try {
            // 데이터를 저장하는 함수 실행
            shareService.save(DTO, uploadPath, upload);
            return "redirect:/share/list";
        } catch (Exception e) {
            e.printStackTrace();// +
            return "shareView/shareSave";
        }

    }

    /**
     * 첨부 파일 다운로드
     * 
     * @param shareNum 게시글 번호
     * @param response 응답 정보
     */
    @GetMapping("download")
    public void download(
            @RequestParam("shareNum") Integer shareNum, HttpServletResponse response) {
        // 파일 다운로드 함수 실행
        shareService.download(shareNum, response, uploadPath);
        log.debug("download 컨트롤러 지나감");

    }

    /**
     * 게시판 읽기 기능
     * 
     * @param model
     * @param shareNum
     * @return
     */
    @GetMapping("read")
    public String read(
            Model model,
            @RequestParam("shareNum") Integer shareNum) {
        log.debug("share/read 컨트롤러 지나감 shareNum : {}", shareNum);
        // DTO생성 후 해당 게시글 번호의 게시글 정보를 저장
        ShareBoardDTO shareBoardDTO = shareService.read(shareNum);
        log.debug("전달받은 DTO : {}", shareBoardDTO);
        // 모델에 저장
        model.addAttribute("shareBoard", shareBoardDTO);
        // read로 이동
        return "shareView/shareRead";
    }

    /**
     * 게시글을 삭제하기 위한 ajax 요청을 받는 컨트롤러
     * 
     * @param shareNum 게시글 번호
     * @param user     로그인한 유저 정보
     */
    @ResponseBody
    @GetMapping("delete")
    public void delete(
            @RequestParam("shareNum") Integer shareNum,
            @AuthenticationPrincipal AuthenticatedUser user) {
        log.debug("share/delete 컨트롤러 지나감 shareNum, user.getUsername : {}, {}", shareNum, user.getUsername());
        try {
            // 게시글 삭제함수 실행
            shareService.delete(shareNum, user.getUsername(), uploadPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 게시글 수정 폼으로 이동
     * 
     * @param shareNum 수정할 글번호
     * @param user     로그인한 사용자 정보
     * @return 수정폼 HTML
     */
    @GetMapping("edit")
    public String edit(
            Model model, @RequestParam("shareNum") Integer shareNum, @AuthenticationPrincipal AuthenticatedUser user) {
        log.debug("share/edit 컨트롤러 지나감 shareNum, user.getUsername : {}, {}", shareNum, user.getUsername());
        try {
            // 글읽기 함수 실행
            ShareBoardDTO shareBoardDTO = shareService.read(shareNum);
            if (!user.getUsername().equals(shareBoardDTO.getMemberId())) {
                throw new RuntimeException("수정 권한이 없습니다.");
            }
            // model에 DTO 저장
            model.addAttribute("shareBoard", shareBoardDTO);
            return "shareView/shareEdit";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:list";
        }
    }

    /**
     * 게시글 수정 처리
     * 
     * @param boardDTO 수정할 글 정보
     * @param user     로그인한 사용자 정보
     * @return 수정폼 HTML
     */
    @PostMapping("edit")
    public String edit(
            @ModelAttribute ShareBoardDTO shareBoardDTO, @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam("upload") MultipartFile upload) {
        log.debug("share/edit 컨트롤러 지나감 shareBoardDTO, user.getUsername : {}, {}", shareBoardDTO, user.getUsername());

        try {
            // 수정 함수 edit 실행
            shareService.edit(shareBoardDTO, user.getUsername(), uploadPath, upload);
            return "redirect:read?shareNum=" + shareBoardDTO.getShareNum();
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:list";
        }
    }
}