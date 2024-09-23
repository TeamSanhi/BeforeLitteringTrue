package net.datasa.nanum.Controller.Member;

import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    /**
     * 이메일 ajax 요청을 받아 처리하는 부분
     * 
     * @param email
     * @param session
     * @return
     */
    @ResponseBody
    @PostMapping("sendEmail")
    public String sendEmail(
            @RequestParam("email") String email,
            HttpSession session) {

        String code = generateVerificationCode(); // 랜덤 인증번호 생성
        // 이메일 전송을 위한 객체 생성
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email); // 전송할 이메일
        message.setSubject("이메일 인증 코드"); // 이메일 제목
        message.setText("인증번호: " + code); // 내용추가 인증번호
        emailSender.send(message); // 메시지 전송

        // 인증번호를 세션에 저장
        session.setAttribute("verificationCode", code);

        return "인증번호가 전송되었습니다.";
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
