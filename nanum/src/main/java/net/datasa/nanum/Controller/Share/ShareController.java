package net.datasa.nanum.Controller.Share;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.ShareBoardDTO;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



/**
 * 나눔 게시판 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("share")
public class ShareController {
    
    /**
     * 나눔 list 페이지로 이동 
     * @param param
     * @return
     */
    @GetMapping("shareList")
    public String shareList() {
        log.debug("sharelist 컨트롤러 지나감");
        return "shareView/shareList";
    }
    
    /**
     * 나눔글작성으로 이동 
     * @return
     */
    @GetMapping("shareSave")
    public String shareSave() {
        log.debug("shareSave 컨트롤러 지나감");
        return "shareView/shareSave";
    }
    /**
     * 나눔게시글 작성
     * @param DTO   //작성한 글 정보
     * @return
     */
    @PostMapping("shareSave")
    public String postMethodName(
        @ModelAttribute ShareBoardDTO DTO) {
        log.debug("ShareBoardDTO 확인 : {}", DTO);
        //게시판으로 리턴
        return "redirect:/share/shareList";
    }
    
    
}
