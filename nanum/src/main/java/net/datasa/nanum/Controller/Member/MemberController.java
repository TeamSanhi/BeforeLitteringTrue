package net.datasa.nanum.Controller.Member;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.MemberDTO;
import net.datasa.nanum.service.MemberService;

@Slf4j
@Controller
@RequestMapping("member")
@RequiredArgsConstructor
public class MemberController {
    // 멤버 서비스 사용
    private final MemberService memberService;
    // 파일 저장경로
    @Value("${profile.uploadPath}")
    String uploadPath;

    @GetMapping("loginForm")
    public String login(HttpServletRequest request, Model model) {

        log.debug("login.html로 이동");

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
     * 
     * @return memberView/joinSave.html
     */
    @GetMapping("join")
    public String join() {
        log.debug("joinSave.html 이동");
        return "memberView/joinSave";
    }

    /**
     * 가입하려는 회원 정보를 dto에 담아서 DB에 저장
     * 
     * @param dto 입력한 회원 정보
     * @return homeView/home.html
     */
    @PostMapping("join")
    public String join(MemberDTO dto) {

        log.debug("회원 가입 정보 입력값: {}", dto);

        memberService.join(dto);

        return "redirect:/";
    }

    /**
     * image테이블 기준으로 게시글 번호를 검색하여 사진을 다운로드 시키는 컨트롤러
     * 
     * @param memberNum
     * @param response
     */
    @GetMapping("profileDownload")
    public void profileDownload(
            @RequestParam("memberNum") Integer memberNum,
            HttpServletResponse response) {
        log.debug("profileDownload 컨트롤러 지나감: {}, {}", memberNum, uploadPath);
        // 파일 다운로드 함수 실행
        memberService.profileDownload(memberNum, response, uploadPath);
    }

}
