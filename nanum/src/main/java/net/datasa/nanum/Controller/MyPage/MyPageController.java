package net.datasa.nanum.Controller.MyPage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.AlarmDTO;
import net.datasa.nanum.domain.dto.MemberDTO;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.repository.BookMarkRepository;
import net.datasa.nanum.security.AuthenticatedUser;
import net.datasa.nanum.service.AlarmService;
import net.datasa.nanum.service.MemberService;
import net.datasa.nanum.service.ShareService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Slf4j
@Controller
@RequestMapping("myPage")
@RequiredArgsConstructor
public class MyPageController {
    private final ShareService shareService;

    private final MemberService memberService;

    private final AlarmService alarmService;
    // 북마크 테이블 접근
    private final BookMarkRepository bookMarkRepository;

    @GetMapping("view")
    public String view(Model model, @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Integer userNum = authenticatedUser.getNum();
        String userNickname = authenticatedUser.getNickname();

        // 로그인한 사용자의 entity를 찾는다.
        MemberEntity member = memberService.getMemberByNum(userNum);

        Integer shareCount = shareService.getShareCount(member);
        List<AlarmDTO> alarmDTOTotal = alarmService.getAlarmList(member);

        String[] daysOfWeek = { "일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일" };

        List<String> alarmTotal = new ArrayList<>();

        for (AlarmDTO alarmDTO : alarmDTOTotal) {
            String alarmDay = daysOfWeek[alarmDTO.getAlarmDay()]; // 배열에서 요일 찾기
            alarmTotal.add(alarmDay);
        }

        Map<String, Integer> dayOrderMap = new HashMap<>();
        for (int i = 0; i < daysOfWeek.length; i++) {
            dayOrderMap.put(daysOfWeek[i], i); // 요일과 그 인덱스를 Map에 저장
        }

        // 정렬
        alarmTotal.sort(Comparator.comparingInt(dayOrderMap::get));
        // 알람 갯수
        Integer alarmLength = alarmTotal.size();

        // 북마크 갯수를 받아온다.
        int bookmarkCount = bookMarkRepository.countByMember(member);

        model.addAttribute("userNickname", userNickname);
        model.addAttribute("shareCount", shareCount);
        model.addAttribute("alarmTotal", alarmTotal);
        model.addAttribute("alarmLength", alarmLength); // 알람 갯수
        model.addAttribute("memberNum", member.getMemberNum()); // 회원번호
        model.addAttribute("pointCount", member.getMemberPoint()); // 나눔 포인트
        model.addAttribute("bookmarkCount", bookmarkCount); // 북마크 수

        log.debug("model : {}", model.toString());

        return "myPageView/myPage";
    }

    @GetMapping("profileEdit")
    public String profileEdit(Model model, @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        MemberEntity member = memberService.getMemberByNum(authenticatedUser.getNum());

        log.debug("member : {}", member);

        model.addAttribute("member", member);

        return "myPageView/profileEdit";
    }

    @PostMapping("profileEdit")
    public String profileEdit(@ModelAttribute MemberDTO dto,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        MemberEntity member = memberService.getMemberByNum(authenticatedUser.getNum());

        log.debug("회원 가입 정보 입력값: {}", dto);

        memberService.modify(dto);

        return "redirect:/myPage/profileEdit";
    }

}
