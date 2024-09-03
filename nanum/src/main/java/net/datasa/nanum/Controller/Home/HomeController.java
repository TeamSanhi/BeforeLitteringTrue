package net.datasa.nanum.Controller.Home;

import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 홈 컨트롤러 
 */
@Controller
@Slf4j
public class HomeController {
    
    /**
     * 홈으로 이동하는 GetMapping
     * @return
     */
    @GetMapping({"","/"})
    public String home() {
        return "homeView/home";
    }
    
}
