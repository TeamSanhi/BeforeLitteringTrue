package net.datasa.nanum.Controller.MyPage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.AlarmDTO;
import net.datasa.nanum.domain.dto.ShareBoardDTO;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.security.AuthenticatedUser;
import net.datasa.nanum.service.AlarmService;
import net.datasa.nanum.service.MemberService;
import net.datasa.nanum.service.ShareService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
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
    private final ShareService shareService;

    // 기존 알람 존재 여부 확인 메소드
    @PostMapping("/checkAlarm")
    public ResponseEntity<Map<String, Boolean>> checkAlarm(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestParam("alarmDay") String alarmDay) {

        Integer userNum = authenticatedUser.getNum();
        MemberEntity member = memberService.getMemberByNum(userNum);

        log.debug("요일 : {}", alarmDay);

        Boolean alarmExists = alarmService.isAlarmExists(member, alarmDay);

        Map<String, Boolean> response = new HashMap<>();
        response.put("alarmExists", alarmExists);

        log.debug("알림 저장 여부 : {}", alarmExists);

        return ResponseEntity.ok(response);
    }

    // 알람 추가 메소드
    @PostMapping("/alarmAdd")
    public ResponseEntity<Map<String, Boolean>> alarmAdd(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestParam("alarmDay") String alarmDay, @RequestParam("alarmContents") String alarmContents) {
        Boolean isAlarmAdded = false;
        Integer userNum = authenticatedUser.getNum();
        MemberEntity member = memberService.getMemberByNum(userNum);

        log.debug("요일 : {}, 알림 내용 : {}", alarmDay, alarmContents);

        isAlarmAdded = alarmService.alarmAdd(member, alarmDay, alarmContents);

        Map<String, Boolean> response = new HashMap<>();
        response.put("alarmAdd", isAlarmAdded);

        log.debug("알림 저장 여부 : {}", isAlarmAdded);

        return ResponseEntity.ok(response);
    }

    // 알람 편집 메소드
    @PostMapping("/alarmEdit")
    public ResponseEntity<Map<String, Boolean>> alarmEdit(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestParam("alarmDay") String alarmDay, @RequestParam("alarmContents") String alarmContents) {
        Boolean isAlarmEdited = false;
        Integer userNum = authenticatedUser.getNum();
        MemberEntity member = memberService.getMemberByNum(userNum);

        log.debug("요일 : {}, 알림 내용 : {}", alarmDay, alarmContents);

        if (alarmService.alarmEdit(member, alarmDay, alarmContents))

        isAlarmEdited = true;

        Map<String, Boolean> response = new HashMap<>();
        response.put("alarmEdit", isAlarmEdited);

        log.debug("알림 저장 여부 : {}", isAlarmEdited);

        return ResponseEntity.ok(response);
    }

    // 비밀번호 확인 메소드
    @PostMapping("checkPassword")
    public ResponseEntity<Map<String, Boolean>> checkPassword(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestParam("enteredPw") String enteredPw) {
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

    // 알람 현황 제공 메소드
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

    // 작성한 나눔글 현황 제공 메소드
    @PostMapping("showGive")
    public ResponseEntity<List<Map<String, String>>> showGive(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Integer userNum = authenticatedUser.getNum();
        MemberEntity member = memberService.getMemberByNum(userNum);

        List<ShareBoardDTO> giveListDTOTotal = shareService.getGiveList(member);
        return ResponseEntity.ok(processShareBoardList(giveListDTOTotal, true));
    }

    // 받은 나눔글 현황 제공 메소드
    @PostMapping("showReceive")
    public ResponseEntity<List<Map<String, String>>> showReceive(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Integer userNum = authenticatedUser.getNum();
        MemberEntity member = memberService.getMemberByNum(userNum);

        List<ShareBoardDTO> receiveListDTOTotal = shareService.getReceiveList(member);
        return ResponseEntity.ok(processShareBoardList(receiveListDTOTotal, false));
    }

    // 북마크한 나눔글 현황 제공 메소드
    @PostMapping("showBookmark")
    public ResponseEntity<List<Map<String, String>>> showBookmark(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Integer userNum = authenticatedUser.getNum();
        MemberEntity member = memberService.getMemberByNum(userNum);

        List<ShareBoardDTO> bookmarkListDTOTotal = shareService.getBookmarkList(member);
        return ResponseEntity.ok(processShareBoardList(bookmarkListDTOTotal, true));
    }

    // 데이터 리스트로 받아오는 공통 메소드
    private List<Map<String, String>> processShareBoardList(List<ShareBoardDTO> shareList, boolean includeShareCompleted) {
        List<Map<String, String>> responseList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (ShareBoardDTO dto : shareList) {
            Map<String, String> response = new HashMap<>();
            String shareDate = dto.getShareDate().format(formatter);

            response.put("shareNum", dto.getShareNum().toString());
            response.put("shareTitle", dto.getShareTitle());
            response.put("shareContents", dto.getShareContents());
            response.put("shareDate", shareDate);

            if (includeShareCompleted) {
                response.put("shareCompleted", dto.getShareCompleted() ? "나눴어요" : "나눠요");
            }

            if (dto.getImageList() != null && !dto.getImageList().isEmpty()) {
                response.put("shareImageList", dto.getImageList().get(0).getImageFileName());
            } else {
                response.put("shareImageList", null);
            }

            responseList.add(response);
        }

        return responseList;
    }
}
