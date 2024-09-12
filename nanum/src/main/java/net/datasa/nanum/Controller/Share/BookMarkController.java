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
     * 게시글 클릭시 로그인한 사용자의 게시글 북마크 여부를 체크
     * 
     * @param shareNum
     * @param user
     * @return
     */
    @ResponseBody
    @GetMapping("bookmark/check")
    public Boolean checkBookmark(
            @RequestParam("shareNum") Integer shareNum,
            @AuthenticationPrincipal AuthenticatedUser user) {

        // 북마크 존재 여부를 ture, false로 반환
        boolean isBookmarked = bookMarkService.isBookmarked(shareNum, user.getNum());

        // 로그인함 사람의 북마크 여부
        log.debug("북마크 여부 isbookmarked : {}", isBookmarked);

        // 반환받은 true, false 값을 ajax에 응답
        return isBookmarked;
    }

    /**
     * ajax로 요청받은 북마크 처리
     * 
     * @param shareNum
     * @param user
     */
    @ResponseBody
    @GetMapping("bookmark")
    public Boolean bookmark(
            @RequestParam("shareNum") Integer shareNum,
            @AuthenticationPrincipal AuthenticatedUser user) {

        log.debug("bookmark 게시글 번호, 로그인 유저 정보 : {}, {}", shareNum, user.getNum());

        // 북마크요청 처리
        boolean isBookmarked = bookMarkService.bookmark(shareNum, user.getNum());

        log.debug("bookmark()에서 전달받은 boolean 값 : {}", isBookmarked);

        return isBookmarked; // ajax로 boolean값을 전송
    }

}
