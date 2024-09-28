package net.datasa.nanum.Controller.Home;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.MemberDTO;
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
        List<ShareBoardDTO> shareBoardDTOList = homeService.hotList();
        // 포인트가 높은 순서대로 상위 8명을 불러온다.
        List<MemberDTO> memberDTOList = homeService.pointList();

        // 전달받은 DTOList를 확인
        log.debug("HomeController shareBoardDTOList : {} ", shareBoardDTOList);
        // 전달받은 memberDTOList 확인
        log.debug("HomeController memberDTOList : {}", memberDTOList);

        // 모델에 저장
        model.addAttribute("hotList", shareBoardDTOList);
        // 1등
        model.addAttribute("pointFirst", memberDTOList.get(0));
        // 2등
        model.addAttribute("pointSecond", memberDTOList.get(1));
        // 3등
        model.addAttribute("pointThird", memberDTOList.get(2));

        return "homeView/home";
    }

}
