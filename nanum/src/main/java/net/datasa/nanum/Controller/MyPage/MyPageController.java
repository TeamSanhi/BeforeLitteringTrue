package net.datasa.nanum.Controller.MyPage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.security.AuthenticatedUser;
import net.datasa.nanum.service.MemberService;
import net.datasa.nanum.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("myPage")
@RequiredArgsConstructor
public class MyPageController {
    @Autowired
    private ShareService shareService;

    @Autowired
    private MemberService memberService;

    @GetMapping("view")
    public String view(Model model, @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Integer userNum = authenticatedUser.getNum();
        String userNickname = authenticatedUser.getNickname();

        Optional<MemberEntity> member = memberService.getMemberByNum(userNum);

        Integer shareCount = shareService.getShareCount(member);

        model.addAttribute("userNickname", userNickname);
        model.addAttribute("shareCount", shareCount);

        return "myPageView/myPage";
    }

    @GetMapping("profileEdit")
    public String profileEdit() {
        return "myPageView/profileEdit";
    }
}
