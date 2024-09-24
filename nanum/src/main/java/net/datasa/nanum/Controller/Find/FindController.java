package net.datasa.nanum.Controller.Find;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.service.FindService;

@Slf4j
@Controller
@RequestMapping("find")
@RequiredArgsConstructor
public class FindController {

    private final FindService findService;

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

}
