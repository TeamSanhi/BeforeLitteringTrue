package net.datasa.nanum.Controller.Share;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.ShareBoardDTO;
import net.datasa.nanum.service.ShareMapService;

@Slf4j
@Controller
@RequestMapping("share")
@RequiredArgsConstructor
public class ShareMapController {

    // ShareMapService 사용
    private final ShareMapService shareMapService;

    /**
     * 남서 북서 위도경도를 받아와 속하는 게시판글들을 반환하는 컨트롤러
     */
    @ResponseBody
    @GetMapping("mapList")
    public List<ShareBoardDTO> mapList(
            @RequestParam("swLat") double swLat,
            @RequestParam("swLng") double swLng,
            @RequestParam("neLat") double neLat,
            @RequestParam("neLng") double neLng,
            @RequestParam("serach") String serach) {
        // 전달 받은 위도 경도 값
        log.debug("swLat:{}, swLng:{}, neLat:{}, neLng:{}", swLat, swLng, neLat, neLng);
        // 전달받은 검색 값
        log.debug("ShareMapControllr.serach: {}", serach);

        // mapList함수를 이용해 해당 위치 값 안에 있는 게시글들을 가져온다.
        List<ShareBoardDTO> shareBoardDTO = shareMapService.mapList(swLat, swLng, neLat, neLng, serach);
        // 올바르게 값을 전달받았는지 디버깅
        log.debug("shareMapController로 전달받은 값 : {}", shareBoardDTO);
        return shareBoardDTO; // JSON 형태로 반환
    }
}
