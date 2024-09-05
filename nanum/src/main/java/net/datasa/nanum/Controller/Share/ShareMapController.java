package net.datasa.nanum.Controller.Share;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("share")
public class ShareMapController {


    /**
     * 남서 북서 위도경도를 받아와 속하는 게시판글들을 반환하는 컨트롤러
     */
    @ResponseBody
    @GetMapping("mapList")
    public void mapList(
        @RequestParam("swLat") double swLat,
        @RequestParam("swLng") double swLng,
        @RequestParam("neLat") double neLat,
        @RequestParam("neLng") double neLng) {
        //전달 받은 위도 경도 값 
        log.debug("swLat: {}, swLng: {}, neLat: {}, neLng: {}", swLat, swLng, neLat, neLng);
    }
}    
