package net.datasa.nanum.Controller.Member;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
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
    // @GetMapping("loginForm")
    // public String login(@RequestParam(value = "error", required = false) String error,
    //                     @RequestParam(value = "exception", required = false) String exception, Model model) {
    //     log.debug("login.html 이동");
    //     log.debug("Error: " + error);
    //     log.debug("Exception: " + exception);
    //     if (error != null) {
    //         model.addAttribute("error", error);
    //         model.addAttribute("exception", exception);
    //     }
    //     return "memberView/login";
    // }

    @GetMapping("loginForm")
    public String login(HttpServletRequest request, Model model) {
        // 세션에서 에러 메시지를 가져옴
        String errorMessage = (String) request.getSession().getAttribute("errorMessage");
        if (errorMessage != null) {
            model.addAttribute("error", true);
            model.addAttribute("exception", errorMessage);
            request.getSession().removeAttribute("errorMessage"); // 메시지 처리 후 삭제
        }
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

}
