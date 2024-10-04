package net.datasa.nanum.Controller.Recycle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.RecycleDTO;
import net.datasa.nanum.service.RecycleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import java.util.List;

@RequestMapping("recycle")
@Slf4j
@Controller
@RequiredArgsConstructor
public class RecycleController {

    private final RecycleService recycleService;

    // application.properties 파일의 게시판 관련 설정값을 주입받음
    @Value("${board.pageSize}")
    int defaultPageSize;

    @Value("${board.linkSize}")
    int linkSize;

    @Value("${board.uploadPath}")
    String uploadPath;

    /**
     * 버려요 게시판 목록을 조회하고 페이징 및 검색 기능을 제공
     *
     * @param model      - 뷰로 데이터를 전달하는 역할
     * @param page       - 요청 페이지 번호, 기본값은 1
     * @param searchType - 검색 유형 (제목, 내용 등)
     * @param searchWord - 검색어
     * @return recycleView/recycleList 페이지
     */
  @GetMapping("list")
public String recycleList(Model model,
                          @RequestParam(name = "searchType", defaultValue = "") String searchType,
                          @RequestParam(name = "searchWord", defaultValue = "") String searchWord) {

    // 매우 큰 페이지 크기 설정
    int pageSize = Integer.MAX_VALUE;

    // 첫 번째 페이지 가져오기
    int page = 1;

    // 서비스 계층에서 글 목록 가져오기
    Page<RecycleDTO> boardPage = recycleService.getList(page, pageSize, searchType, searchWord);

    // 가져온 게시물 목록을 모델에 추가
    List<RecycleDTO> recycleList = boardPage.getContent();
    model.addAttribute("recycleList", recycleList);

    // 검색 관련 정보를 모델에 추가
    model.addAttribute("searchType", searchType);
    model.addAttribute("searchWord", searchWord);

    // Thymeleaf 템플릿을 반환
    return "recycleView/recycleList";
}


    /**
     * 버려요 게시글 상세보기 (팝업에서 사용)
     *
     * @param recycleNum - 조회할 게시물 번호
     * @return RecycleDTO - 게시물 데이터를 JSON으로 반환
     */
    @GetMapping("read")
    @ResponseBody
    public RecycleDTO read(@RequestParam("recycleNum") int recycleNum) {
        // 서비스 계층에서 게시글 번호로 데이터를 가져옴
        RecycleDTO recycleDTO = recycleService.getRecycle(recycleNum);
        log.debug("정보 열람: {}", recycleDTO);
        return recycleDTO;  // JSON 형태로 데이터를 반환
    }
}
