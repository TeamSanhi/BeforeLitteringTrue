package net.datasa.nanum.Controller.MyPage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.AlarmDTO;
import net.datasa.nanum.domain.dto.MemberDTO;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.security.AuthenticatedUser;
import net.datasa.nanum.service.AlarmService;
import net.datasa.nanum.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("myPage")
@RestController
public class MyPageRestController {

    private final AlarmService alarmService;
    private final MemberService memberService;

    // 알람 추가 메소드
    @PostMapping("/alarmEdit")
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
    @PostMapping("checkPassword")
    public ResponseEntity<Map<String, Boolean>> checkPassword(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestParam("enterPw") String enteredPw) {
        Integer userNum = authenticatedUser.getNum();

        log.debug("입력받은 패스워드 : {}", enteredPw);

        Boolean isPasswordMatch = memberService.checkPassword(userNum, enteredPw);

        Map<String, Boolean> response = new HashMap<>();
        response.put("passwordMatch", isPasswordMatch);

        log.debug("패스워드 일치 여부 : {}", isPasswordMatch);

        return ResponseEntity.ok(response);
    }

    // 탈퇴 처리 메소드
    @PostMapping("deleteMember")
    public ResponseEntity<Map<String, Boolean>> deleteMember(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Integer userNum = authenticatedUser.getNum();

        boolean isDeleted = memberService.deleteMember(userNum);

        log.debug("멤버 탈퇴 여부 : {}", isDeleted);
        Map<String, Boolean> response = new HashMap<>();

        response.put("deleteMember", isDeleted);

        return ResponseEntity.ok(response);
    }


    @PostMapping("showAlarm")
    public ResponseEntity<List<Map<String, String>>> showAlarm(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Integer userNum = authenticatedUser.getNum();
        MemberEntity member = memberService.getMemberByNum(userNum);

        List<AlarmDTO> alarmDTOTotal = alarmService.getAlarmList(member);

        log.debug("alarmDTOTotal: {}", alarmDTOTotal);

        List<Map<String, String>> responseList = new ArrayList<>();

        String[] daysOfWeek = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};

        for (AlarmDTO alarmDTO : alarmDTOTotal) {
            Map<String, String> response = new HashMap<>();
            String alarmDay = daysOfWeek[alarmDTO.getAlarmDay()]; // 배열에서 요일 찾기

            log.debug("alarmDay: {}", alarmDay);
            log.debug("alarmContents: {}", alarmDTO.getAlarmContents());

            response.put("alarmDay", alarmDay);
            response.put("alarmContents", alarmDTO.getAlarmContents());
            responseList.add(response);
        }

        log.debug("responseList: {}", responseList);

        return ResponseEntity.ok(responseList);
    }

    @PostMapping("showGive")
    public ResponseEntity<List<Map<String, String>>> showGive(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Integer userNum = authenticatedUser.getNum();
        MemberEntity member = memberService.getMemberByNum(userNum);





        return ResponseEntity.ok();
    }


    //@PostMapping("showBookmark")

    //@PostMapping("showMessages")

}
