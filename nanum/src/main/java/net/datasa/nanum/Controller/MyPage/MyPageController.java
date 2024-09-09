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
        List<AlarmDTO> alarmTotal = alarmService.getAlarmList(member);

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
