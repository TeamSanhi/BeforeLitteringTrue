package net.datasa.nanum.Controller.Member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        return "memberView/login";
    }

    /**
     * 회원 가입 화면으로 이동
     * @return memberView/joinSave.html
     */ 
    @GetMapping("join")
    public String join () {
        return "memberView/joinSave";
    }

    /**
     * 가입하려는 회원 정보를 dto에 담아서 DB에 저장
     * @param dto   입력한 회원 정보
     * @return      homeView/home.html
     */
    @PostMapping("join")
    public String join (MemberDTO dto) {
        
        log.debug("입력값: {}", dto);

        memberService.join(dto);      

        return "redirect:/";
    }

}
