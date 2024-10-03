package net.datasa.nanum.Controller.Find;

import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.security.AuthenticatedUser;
import net.datasa.nanum.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.service.FindService;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequestMapping("find")
@RequiredArgsConstructor
public class FindController {

    private final FindService findService;
    private final MemberService memberService;

    @GetMapping("idCheck")
    @ResponseBody
    public boolean checkId(@RequestParam("memberId") String memberId) {

        log.debug("입력한 아이디: {}", memberId);
        // 아이디가 있으면 true, 없으면 false리턴
        boolean available = findService.isIdAvailable(memberId);

        log.debug("아이디 중복인가? {}", available);

        // 아이디가 있으면 true, 없으면 false리턴
        return available;
    }

    @GetMapping("nickCheck")
    @ResponseBody
    public boolean checkNickname(@RequestParam("memberNickname") String memberNickname) {

        log.debug("입력한 닉네임: {}", memberNickname);
        // 닉네임이 있으면 true, 없으면 false 리턴받음
        boolean available = findService.isNicknameAvailable(memberNickname);

        log.debug("닉네임 중복인가? {}", available);
        // 아이디가 있으면 true, 없으면 false리턴
        return available;
    }

    @PostMapping("reNickCheck")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkNickname(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam("memberNickname") String memberNickname) {
        Integer userNum = authenticatedUser.getNum();
        MemberEntity member = memberService.getMemberByNum(userNum);

        Map<String, Boolean> response = new HashMap<>();

        response.put("duplication", false);
        response.put("loginUser", false);

        log.debug("입력한 닉네임: {}", memberNickname);
        // 닉네임이 있으면 true, 없으면 false 리턴받음
        if (findService.isNicknameAvailable(memberNickname)) {
            log.debug("duplication : {}", "true");
            response.put("duplication", true);
            if (member.getMemberNickname().equals(memberNickname)) {
                log.debug("loginUser : {}", "true");
                response.put("loginUser", true);
            }
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 아이디 찾기 화면으로 이동
     * 
     * @return findView/idFind.html
     */
    @GetMapping("idFind")
    public String idFind() {
        log.debug("idFind.html로 이동");
        return "findView/idFind";
    }

    /**
     * 비밀번호 찾기 화면으로 이동
     * 
     * @return findView/pwFind.html
     */
    @GetMapping("pwFind")
    public String pwFind() {
        log.debug("pwFind.html로 이동");
        return "findView/pwFind";
    }

    /**
     * 비밀번호 변경 컨트롤러
     * 
     * @param memberEmail
     * @return
     */
    @PostMapping("pwFind")
    @ResponseBody
    public boolean pwFind(
            @RequestParam("memberEmail") String memberEmail,
            @RequestParam("memberPw") String memberPw) {

        log.debug("전달받은 email, pw : {}, {}", memberEmail, memberPw);

        // 전달받은 값들로 비밀번호를 변경한다.
        boolean result = findService.pwFind(memberEmail, memberPw);

        // 변경시 true, 변경실패시 false
        return result;
    }

}
