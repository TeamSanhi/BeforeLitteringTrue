package net.datasa.nanum.Controller.Find;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.repository.MemberRepository;
import net.datasa.nanum.service.FindService;
import net.datasa.nanum.service.MailService;

@Slf4j
@Controller
@RequestMapping("find")
@RequiredArgsConstructor
public class FindController {

    private final MemberRepository memberRepository;
    private final FindService findService;
    private final MailService mailService;

    // /**
    //  * ID 중복 확인 창으로 이동
    //  * @return  findView/idCheck.html
    //  */
    // @GetMapping("idCheck")
    // public String idCheck (@RequestParam("memberId") String memberId,
    //                        @RequestParam("memberNickname") String memberNickname,
    //                        Model model) {

    //     // id 중복 여부 확인                    
    //     boolean idDuplicate = findService.idDuplicate(memberId);
    //     // 닉네임 중복 여부 확인 
    //     boolean nickDuplicate = findService.nickDuplicate(memberNickname);

    //     log.debug("가져온 memberId값: {}", memberId);
    //     log.debug("가져온 memberNickname값: {}", memberNickname);

    //     model.addAttribute("idDuplicate", idDuplicate);
    //     model.addAttribute("nickDuplicate", nickDuplicate);
    //     model.addAttribute("memberId", memberId);
    //     model.addAttribute("memberNickname", memberNickname);

    //     return "findView/idCheck";
    // }

    @GetMapping("idCheck")
    @ResponseBody
    public Map<String, Object> checkId(@RequestParam("memberId") String memberId) {

        log.debug("입력한 아이디: {}", memberId);
        Map<String, Object> response = new HashMap<>();
        boolean available = findService.isIdAvailable(memberId);

        log.debug("아이디 중복인가? {}", available);
        response.put("available", available);
        return response;
    }

    @GetMapping("nickCheck")
    @ResponseBody
    public Map<String, Object> checkNickname(@RequestParam("memberNickname") String memberNickname) {
        log.debug("입력한 닉네임: {}", memberNickname);
        Map<String, Object> response = new HashMap<>();
        boolean available = findService.isNicknameAvailable(memberNickname);
        log.debug("닉네임 중복인가? {}", available);
        response.put("available", available);
        return response;
    }

    /**
     * 아이디 찾기 화면으로 이동
     * @return findView/idFind.html
     */
    @GetMapping("idFind")
    public String idFind() {
        log.debug("idFind.html로 이동");
        return "findView/idFind";
    }

    /**
     * 비밀번호 찾기 화면으로 이동
     * @return findView/pwFind.html
     */
    @GetMapping("pwFind")
    public String pwFind() {
        log.debug("pwFind.html로 이동");
        return "findView/pwFind";
    }
    
}
