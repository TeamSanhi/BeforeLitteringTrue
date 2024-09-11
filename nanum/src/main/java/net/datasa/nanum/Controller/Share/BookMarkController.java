package net.datasa.nanum.Controller.Share;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.security.AuthenticatedUser;
import net.datasa.nanum.service.BookMarkService;

/**
 * 북마크 관련 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("share")
@RequiredArgsConstructor
public class BookMarkController {

    // 북마크 서비스 클래스 사용
    private final BookMarkService bookMarkService;

    /**
     * ajax로 요청받은 북마크 처리
     * 
     * @param shareNum
     * @param user
     */
    @ResponseBody
    @GetMapping("bookmark")
    public void bookmark(
            @RequestParam("shareNum") Integer shareNum,
            @AuthenticationPrincipal AuthenticatedUser user) {

        log.debug("전달받은 게시글 번호화 로그인한 유저 정보 : {}, {}", shareNum, user.getNum());
        // 북마크요청 처리
        bookMarkService.bookmark(shareNum, user.getNum());
    }
}
