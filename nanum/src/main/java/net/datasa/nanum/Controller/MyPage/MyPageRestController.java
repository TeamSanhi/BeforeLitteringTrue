package net.datasa.nanum.Controller.MyPage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.security.AuthenticatedUser;
import net.datasa.nanum.service.AlarmService;
import net.datasa.nanum.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("myPage")
@RestController
public class MyPageRestController {

    private final AlarmService alarmService;
    private final MemberService memberService;

    // 알람 추가 메소드
    @PostMapping("alarmEdit")
    public ResponseEntity<Map<String, Boolean>> alarmEdit(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestParam Integer alarmDay, @RequestParam String alarmContents) {
        Integer userNum = authenticatedUser.getNum();
        MemberEntity member = memberService.getMemberByNum(userNum);

        log.debug("요일 : {}, 알림 내용 : {}", alarmDay, alarmContents);

        boolean isAlarmEdited = alarmService.alarmEdit(member, alarmDay, alarmContents);

        Map<String, Boolean> response = new HashMap<>();
        response.put("alarmEdit", isAlarmEdited);
        
        log.debug("알림 저장 여부 : {}", isAlarmEdited);

        return ResponseEntity.ok(response);
    }

    // 비밀번호 확인 메소드
//    @PostMapping("/checkPassword")
//    public ResponseEntity<Map<String, Boolean>> checkPassword(@RequestParam String password, @RequestParam Long userId) {
//        // 비밀번호가 맞는지 확인하는 로직 실행
//        boolean isPasswordMatch = memberService.checkPassword(userId, password);
//
//        // 결과를 클라이언트로 보냄
//        Map<String, Boolean> response = new HashMap<>();
//        response.put("passwordMatch", isPasswordMatch);
//
//        return ResponseEntity.ok(response);
//    }
//
//    // 탈퇴 처리 메소드
//    @PostMapping("/delete-member")
//    public ResponseEntity<Map<String, Boolean>> deleteMember(@RequestParam Long userId) {
//        // 탈퇴 처리 로직 실행
//        boolean isDeleted = memberService.deleteMember(userId);
//
//        // 결과를 클라이언트로 보냄
//        Map<String, Boolean> response = new HashMap<>();
//        response.put("success", isDeleted);
//
//        return ResponseEntity.ok(response);
//    }
}
