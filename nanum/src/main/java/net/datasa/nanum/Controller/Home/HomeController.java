package net.datasa.nanum.Controller.Home;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.ShareBoardDTO;
import net.datasa.nanum.service.HomeService;

/**
 * 홈 컨트롤러
 */
@RequiredArgsConstructor
@Controller
@Slf4j
public class HomeController {

    // 홈서비스의 함수 사용하기
    private final HomeService homeService;

    /**
     * 홈으로 이동하는 GetMapping
     * 인기 게시글들을 가여와서 model에 저장하여 뿌려준다.
     * 
     * @return
     */
    @GetMapping({ "", "/" })
    public String home(Model model) {

        log.debug("homeController를 지나간다");

        // 북마크 순서로 상위 8개의 글을 불러온다.
        List<ShareBoardDTO> DTOList = homeService.hotList();

        // 전달받은 DTOList를 확인
        log.debug("HomeController DTOList : {} ", DTOList);

        // 모ㄷ
        model.addAttribute("hotList", DTOList);

        return "homeView/home";
    }

}
