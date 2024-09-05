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

//    private final RecycleService recycleService;

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
    public String recycleList(
        // Model model, @RequestParam(name="page", defaultValue="1") int page, 
        // @RequestParam(name="searchType", defaultValue="") String searchType,
        // @RequestParam(name="searchWord", defaultValue="") String searchWord
        ) {
    //     log.debug("설정값 : pageSize={}, linkSize={}", pageSize, linkSize);
        
    //    Page<RecycleDTO> recyclePage = recycleService.getList(page, pageSize, searchType, searchWord);

    //     model.addAttribute("recyclePage", recyclePage);
    //     model.addAttribute("page", page);
    //     model.addAttribute("searchType", searchType);
    //     model.addAttribute("searchWord", searchWord);
    //     model.addAttribute("linkSize", linkSize);
    
        return "recycleView/recycleList";
    }

//     /**
//      * 버려요 게시글 상세보기
//      * @param param
//      * @return
//      */
//     @GetMapping("read")
//     public String read(Model model, @RequestParam("recycleNum") int recycleNum) {
//         log.debug("버려요 게시글 조회", recycleNum);

//         try {
//             RecycleDTO recycleDTO = recycleService.getRecycle(recycleNum);
//             model.addAttribute("recycle", recycleDTO);
//             return "recycleView/recycleRead";  // 추가: 조회 후의 뷰로 리턴
//         } catch (Exception e) {
//             log.error("게시글 조회 중 오류 발생", e);
//             model.addAttribute("error", "게시글을 조회하는 중 오류가 발생했습니다.");
//             return "redirect:list";
//             }
// }
    

}
