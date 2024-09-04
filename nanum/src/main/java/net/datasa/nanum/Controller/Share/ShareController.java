package net.datasa.nanum.Controller.Share;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;


/**
 * 나눔 게시판 컨트롤러
 */
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
        return "shareView/shareList";
    }
    
    /**
     * 나눔글작성으로 이동 
     * @return
     */
    @GetMapping("shareRead")
    public String shareRead() {
        return "shareView/shareRead";
    }
    
}
