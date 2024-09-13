package net.datasa.nanum.Controller.Share;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.security.AuthenticatedUser;
import net.datasa.nanum.service.ReportBoardService;

/**
 * 신고버튼 관련 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("share")
@RequiredArgsConstructor
public class ReportBoardController {

    private final ReportBoardService reportBoardService;

    /**
     * ajax 신고요청을 받는 Controller
     * 
     * @param shareNum 신고받은 게시글번호
     */
    @ResponseBody
    @PostMapping("report")
    public void reportBoard(
            @RequestParam("shareNum") Integer shareNum,
            @AuthenticationPrincipal AuthenticatedUser user) {
        log.debug("ReportController 지나감 shareNum, user.num : {}, {}", shareNum, user.getNum());

        // 서비스의 게시글 신고함수 실행
        reportBoardService.reportBoard(shareNum, user.getNum());

    }
}
