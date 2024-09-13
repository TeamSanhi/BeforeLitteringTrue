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
    public Integer reportBoard(
            @RequestParam("shareNum") Integer shareNum,
            @AuthenticationPrincipal AuthenticatedUser user) {
        log.debug("ReportController 지나감 shareNum, user.num : {}, {}", shareNum, user.getNum());

        // 서비스의 게시글 신고함수 실행
        // true 받으면 게시글이 삭제됨 false 면 게시글 신고수만 올라간거
        Integer deleteResult = reportBoardService.reportBoard(shareNum, user.getNum());

        log.debug("reportBoard()에서 전달받은 Integer 값 : {}", deleteResult);

        // 삭제된 결과를 리턴
        // 0 이면 이용자가 중복으로 게시글을 신고 중복메시지 전송
        // 1 이면 신고 성공하여 게시글 신고수가 5회 이상이 되어 게시글 삭제 후 /share/list 로 페이지 이동
        // 2 이면 신고 성공
        return deleteResult;
    }
}
