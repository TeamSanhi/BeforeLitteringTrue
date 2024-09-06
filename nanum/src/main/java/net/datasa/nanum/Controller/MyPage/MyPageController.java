package net.datasa.nanum.Controller.MyPage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("myPage")
@RequiredArgsConstructor
public class MyPageController {
    @GetMapping("view")
    public String view() {
        return "myPageView/myPage";
    }
}
