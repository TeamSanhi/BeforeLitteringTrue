package net.datasa.nanum.Controller.Home;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.AlarmDTO;
import net.datasa.nanum.domain.dto.MemberDTO;
import net.datasa.nanum.domain.dto.ShareBoardDTO;
import net.datasa.nanum.domain.entity.AlarmEntity;
import net.datasa.nanum.security.AuthenticatedUser;
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
    public String home(@AuthenticationPrincipal AuthenticatedUser user, Model model) {

        log.debug("homeController를 지나간다");

        // 오늘의 요일 계산
        int todayDayIndex = LocalDateTime.now().getDayOfWeek().getValue() % 7;

        // 로그인 하였을 경우 알람을 조회하여 모델에 저장하는 조건문
        if (user != null) {
            // 로그인한 사용자일 경우
            // 멤버 번호화 오늘의 요일을 파라미터로 주어 해당하는 AlarmEntity를 찾아 저장
            AlarmDTO alarmDTO = homeService.alarmCheck(user.getNum(), todayDayIndex);
            // 전달받은 DTO를 모델에 저장
            model.addAttribute("alarm", alarmDTO);
            // alarmDTO가 null이 아닐 경우에만 switch문 실행
            if (alarmDTO != null) {
                switch (alarmDTO.getAlarmDay()) {
                    case 0:
                        model.addAttribute("week", "일요일");
                        break;
                    case 1:
                        model.addAttribute("week", "월요일");
                        break;
                    case 2:
                        model.addAttribute("week", "화요일");
                        break;
                    case 3:
                        model.addAttribute("week", "수요일");
                        break;
                    case 4:
                        model.addAttribute("week", "목요일");
                        break;
                    case 5:
                        model.addAttribute("week", "금요일");
                        break;
                    case 6:
                        model.addAttribute("week", "토요일");
                        break;
                }
            } else {
                // alarmDTO가 null일 때 기본 요일 메시지 설정
                model.addAttribute("week", "알람이 없는 요일");
            }
        } else {
            // 로그인하지 않은 사용자일 경우
            log.debug("로그인하지 않은 사용자 입니다");
        }

        // 북마크 순서로 상위 8개의 글을 불러온다.
        List<ShareBoardDTO> shareBoardDTOList = homeService.hotList();
        // 포인트가 높은 순서대로 상위 8명을 불러온다.
        List<MemberDTO> memberDTOList = homeService.pointList();
        // 해당일자의

        // 전달받은 DTOList를 확인
        log.debug("HomeController shareBoardDTOList : {} ", shareBoardDTOList);
        // 전달받은 memberDTOList 확인
        log.debug("HomeController memberDTOList : {}", memberDTOList);

        // Model
        // 인기글 리스트 모델에 저장
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
