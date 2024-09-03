package net.datasa.nanum.Controller.Member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.MemberDTO;

@Slf4j
@Controller
@RequestMapping("member")
public class MemberController {
    /**
     * 로그인 화면으로 이동
     * @return memberView/login.html
     */
    @GetMapping("login")
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

    @PostMapping("join")
    public String join (MemberDTO dto) {
        
        log.debug("입력값: {}", dto);

        return "redirect:/";
    }

}
