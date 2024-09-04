package net.datasa.nanum.Controller.Recycle;

<<<<<<< Updated upstream
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
=======
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RequestMapping("recycle")
@Slf4j
@Controller
@RequiredArgsConstructor
public class RecycleController {
    
    @GetMapping("recycle")
    public String recycleList(@RequestParam String param) {

        return "recycleView/recycleList";
    }
    
>>>>>>> Stashed changes
}
