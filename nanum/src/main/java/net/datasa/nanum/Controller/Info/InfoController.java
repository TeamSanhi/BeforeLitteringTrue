package net.datasa.nanum.Controller.Info;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 부가 정보 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("info")
@RequiredArgsConstructor
public class InfoController {
    /**
     * 서비스 소개 페이지로 이동하는 GetMapping
     */
    @GetMapping("service")
    public String service() {
        log.debug("서비스 소개 페이지로 이동");
        return "infoView/service";
    }


    /**
     * 사이트맵으로 이동하는 GetMapping
     */
    @GetMapping("siteMap")
    public String siteMap() {
        log.debug("siteMap으로 이동");
        return "infoView/siteMap";
    }

    /**
     * FAQ로 이동하는 GetMapping
     */
    @GetMapping("faq")
    public String faq() {
        log.debug("faq로 이동");
        return "infoView/faq";
    }

}
