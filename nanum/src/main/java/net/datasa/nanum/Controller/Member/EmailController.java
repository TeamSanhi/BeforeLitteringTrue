package net.datasa.nanum.Controller.Member;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.security.AuthenticatedUser;
import net.datasa.nanum.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.repository.MemberRepository;

/**
 * 이메일 사용을 위한 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("member")
@RequiredArgsConstructor
public class EmailController {

    // 이메일 전송을 위한 기능
    private final JavaMailSender emailSender;
    // 이메일 중복확인 하기 위한 리퍼지토리
    private final MemberRepository memberRepository;
    // 멤버 서비스 사용
    private final MemberService memberService;

    /**
     * 이메일 ajax 요청을 받아 처리하는 부분
     * 
     * @param email
     * @param session
     * @return
     */
    @ResponseBody
    @PostMapping("sendEmail")
    public boolean sendEmail(
            @RequestParam("email") String email,
            HttpSession session) {

        // 이메일 중복확인하기 위해 repository에 검색
        if (memberRepository.existsByMemberEmail(email)) {
            return true;
        }

        String code = generateVerificationCode(); // 랜덤 인증번호 생성
        // 이메일 전송을 위한 객체 생성
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email); // 전송할 이메일
        message.setSubject("이메일 인증 코드"); // 이메일 제목
        message.setText("인증번호: " + code); // 내용추가 인증번호
        emailSender.send(message); // 메시지 전송

        // 인증번호를 세션에 저장
        session.setAttribute("verificationCode", code);

        // 이메일이 중복이면 true 리턴
        // 이메일이 중복되지 않으면 false 리턴
        return false;
    }

    // 프로필 수정에서 이메일 전송 시 맵핑
    @ResponseBody
    @PostMapping("/reSendEmail")
    public ResponseEntity<Map<String, Boolean>> sendEmail(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam("email") String email,
            HttpSession session) {
        Integer userNum = authenticatedUser.getNum();
        MemberEntity member = memberService.getMemberByNum(userNum);

        Map<String, Boolean> response = new HashMap<>();

        response.put("duplication", false);
        response.put("loginUser", false);

        // 이메일 중복확인하기 위해 repository에 검색
        if (memberRepository.existsByMemberEmail(email)) {
            log.debug("duplication : {}", "true");
            response.put("duplication", true);
            if (member.getMemberEmail().equals(email)) {
                log.debug("loginUser : {}", "true");
                response.put("loginUser", true);
            }
            return ResponseEntity.ok(response);
        }

        String code = generateVerificationCode(); // 랜덤 인증번호 생성

        // 이메일 전송을 위한 객체 생성
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email); // 전송할 이메일
        message.setSubject("이메일 인증 코드"); // 이메일 제목
        message.setText("인증번호: " + code); // 내용추가 인증번호
        emailSender.send(message); // 메시지 전송

        // 인증번호를 세션에 저장
        session.setAttribute("verificationCode", code);

        return ResponseEntity.ok(response);
    }

    /**
     * 전송한 이메일 확인하는 컨트롤러
     * 
     * @param emailCode
     * @param session
     * @return
     */
    @PostMapping("verifyEmail")
    @ResponseBody
    public String verifyCode(@RequestParam("emailCode") String emailCode, HttpSession session) {

        // 세션에 인증번호를 가져온다.
        String sessionCode = (String) session.getAttribute("verificationCode");

        // 인증번호 맞는지 안맞는지 확인
        if (sessionCode != null && sessionCode.equals(emailCode)) {
            // 인증 성공, 세션에서 인증번호 삭제
            // session.removeAttribute("verificationCode");
            return "인증 성공";
        } else {
            return "인증 실패";
        }
    }

    /**
     * 6자리의 무작위 숫자를 생성하기 위한 함수
     * 
     * @return
     */
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

}
