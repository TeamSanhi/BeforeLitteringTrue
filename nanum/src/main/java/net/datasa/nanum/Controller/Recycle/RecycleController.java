package net.datasa.nanum.Controller.Recycle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("recycle")
@RequiredArgsConstructor
public class RecycleController {
    /**
     * 사이트맵으로 이동하는 GetMapping
     */
    @GetMapping("recycleList")
    public String recycleList() {
        log.debug("recycleList로 이동");
        return "recycleView/recycleList";
    }
}
