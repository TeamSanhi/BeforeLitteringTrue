package net.datasa.nanum.Controller.Member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("member")
public class MemberController {
    /**
     * 로그인 화면으로 이동
     * @return login.html
     */
    @GetMapping("login")
    public String login () {
        return "memberView/login";
    }

    
}
