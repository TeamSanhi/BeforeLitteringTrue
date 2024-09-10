package net.datasa.nanum.Controller.MyPage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.AlarmDTO;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.security.AuthenticatedUser;
import net.datasa.nanum.service.AlarmService;
import net.datasa.nanum.service.MemberService;
import net.datasa.nanum.service.ShareService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("myPage")
@RequiredArgsConstructor
public class MyPageController {
    private final ShareService shareService;

    private final MemberService memberService;

    private final AlarmService alarmService;

    @GetMapping("view")
    public String view(Model model, @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Integer userNum = authenticatedUser.getNum();
        String userNickname = authenticatedUser.getNickname();

        MemberEntity member = memberService.getMemberByNum(userNum);

        Integer shareCount = shareService.getShareCount(member);
        List<AlarmDTO> alarmDTOTotal = alarmService.getAlarmList(member);
        List<String> alarmTotal = new ArrayList<>();

        for (AlarmDTO alarmDTO : alarmDTOTotal) {
            String alarmDay = "null";
            switch (alarmDTO.getAlarmDay()) {
                case 0:
                    alarmDay = "일요일";
                    break;
                case 1:
                    alarmDay = "월요일";
                    break;
                case 2:
                    alarmDay = "화요일";
                    break;
                case 3:
                    alarmDay = "수요일";
                    break;
                case 4:
                    alarmDay = "목요일";
                    break;
                case 5:
                    alarmDay = "금요일";
                    break;
                case 6:
                    alarmDay = "토요일";
                    break;
            }
            alarmTotal.add(alarmDay);
        }

        model.addAttribute("userNickname", userNickname);
        model.addAttribute("shareCount", shareCount);
        model.addAttribute("alarmTotal", alarmTotal);


        return "myPageView/myPage";
    }

    @GetMapping("profileEdit")
    public String profileEdit() {
        return "myPageView/profileEdit";
    }
}
