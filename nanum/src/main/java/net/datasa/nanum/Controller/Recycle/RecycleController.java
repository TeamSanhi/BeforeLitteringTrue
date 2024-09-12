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
import org.springframework.ui.Model;  // 올바른 Model import
import org.springframework.data.domain.Page;  // 페이지네이션을 위한 올바른 import

/**
 * RecycleController
 * 버려요 게시판 목록
 */
@RequestMapping("recycle")
@Slf4j
@Controller
@RequiredArgsConstructor
public class RecycleController {

    private final RecycleService recycleService;

    // application.properties 파일의 게시판 관련 설정값
	@Value("${board.pageSize}")
	int pageSize;

	@Value("${board.linkSize}")
	int linkSize;

	@Value("${board.uploadPath}")
	String uploadPath;
    
    /**
     * 버려요 게시판 목록을 조회하고 페이징 및 검색 기능을 제공
     * @param param
     * @return
     */
    @GetMapping("list")
    public String recycleList(Model model, @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "searchType", defaultValue = "") String searchType,
        @RequestParam(name = "searchWord", defaultValue = "") String searchWord) {

            log.debug("설정 값 : pageSize={}, linkSize={}", pageSize, linkSize);
            log.debug("요청파라미터 : page={}, searchType={}, searchWord={}", page, searchType, searchWord);

    // 글 목록 1페이지
    Page<RecycleDTO> boardPage = recycleService.getList(page, pageSize, searchType, searchWord);

        model.addAttribute("boardPage", boardPage);
        model.addAttribute("page", page);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("linkSize", linkSize);

        log.debug("전체 개수 :{}", boardPage.getTotalElements());
        log.debug("전체 페이지수 :{}", boardPage.getTotalPages());
        log.debug("현재 페이지 :{}", boardPage.getNumber());
        log.debug("페이지당 글수 :{}", boardPage.getSize());
        log.debug("이전페이지 존재 :{}", boardPage.hasPrevious());
        log.debug("다음페이지 존재 :{}", boardPage.hasNext());

        return "recycleView/recycleList";
    }

     /**
      * 버려요 게시글 상세보기
      * @param param
      * @return
      */
@GetMapping("read")
@ResponseBody
public RecycleDTO read(@RequestParam("recycleNum") int recycleNum) {
    // 게시글 번호로 데이터를 가져온다
    RecycleDTO recycleDTO = recycleService.getRecycle(recycleNum);
    return recycleDTO;  // JSON으로 반환
}
    

}
