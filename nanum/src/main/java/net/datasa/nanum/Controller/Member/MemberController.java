package net.datasa.nanum.Controller.Member;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.MemberDTO;
import net.datasa.nanum.service.MemberService;

@Slf4j
@Controller
@RequestMapping("member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 로그인 화면으로 이동
     * @return memberView/login.html
     */
    @GetMapping("loginForm")
    public String login () {

        log.debug("login.html 이동");
        return "memberView/login";
    }

    /**
     * 회원 가입 화면으로 이동
     * @return memberView/joinSave.html
     */ 
    @GetMapping("join")
    public String join () {
        log.debug("joinSave.html 이동");
        return "memberView/joinSave";
    }

    /**
     * 가입하려는 회원 정보를 dto에 담아서 DB에 저장
     * @param dto   입력한 회원 정보
     * @return      homeView/home.html
     */
    @PostMapping("join")
    public String join (MemberDTO dto) {
        
        log.debug("회원 가입 정보 입력값: {}", dto);

        memberService.join(dto);      

        return "redirect:/";
    }

    /**
     * ID 중복 확인 창으로 이동
     * @return  findView/idCheck.html
     */
    @GetMapping("idCheck")
    public String idCheck (@RequestParam("memberId") String memberId,
                           @RequestParam("memberNickname") String memberNickname,
                           Model model) {

        boolean idDuplicate = memberService.idDuplicate(memberId);

        boolean nickDuplicate = memberService.nickDuplicate(memberNickname);

        log.debug("가져온 memberId값: {}", memberId);
        log.debug("가져온 memberNickname값: {}", memberNickname);

        model.addAttribute("idDuplicate", idDuplicate);
        model.addAttribute("nickDuplicate", nickDuplicate);
        model.addAttribute("memberId", memberId);
        model.addAttribute("memberNickname", memberNickname);

        return "findView/idCheck";
    }
}
