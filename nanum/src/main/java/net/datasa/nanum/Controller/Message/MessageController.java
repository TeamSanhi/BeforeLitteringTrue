package net.datasa.nanum.Controller.Message;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.MessageDTO;
import net.datasa.nanum.domain.entity.MessageEntity;
import net.datasa.nanum.domain.entity.ShareBoardEntity;
import net.datasa.nanum.security.AuthenticatedUser;
import net.datasa.nanum.service.MessageService;

@Slf4j
@Controller
@RequestMapping("message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 사용자의 쪽지 목록으로 이동
     * @param user
     * @param model
     * @return
     */
    @GetMapping("messages")
    public String messages (@AuthenticationPrincipal AuthenticatedUser user, 
                        Model model) {

        log.debug("messageView/messageList.html로 이동");                    

        // 게시글별로 주고받은 쪽지 조회 
        Map<ShareBoardEntity, MessageEntity> messagesByShareNum = messageService.getMessagesGroupedByShareNum(user.getNum());

        // 모델에 담아서 뷰로 전달
        model.addAttribute("messages", messagesByShareNum);

        return "messageView/messageList";
    }

    @GetMapping("/messages/full/{shareNum}")
    public String viewFullMessagesByPost(@PathVariable("shareNum") Integer shareNum, Model model, @AuthenticationPrincipal AuthenticatedUser user) {

        log.debug("messageView/messageRead.html로 이동");  

        log.debug("게시글 번호: {}", shareNum);

        // 게시글 번호와 사용자의 번호로 모든 메시지 조회
        List<MessageEntity> fullMessages = messageService.getMessagesByShareNumAndUser(shareNum, user.getNum());

        model.addAttribute("fullMessages", fullMessages);
        model.addAttribute("shareNum", shareNum);

        return "messageView/messageRead"; // 전체 쪽지 내역을 보여줄 템플릿
    }


    /**
     * 작성한 쪽지를 DB에 저장
     * @param dto
     * @param user
     * @param messageContents
     * @param shareNum
     * @return
     */
    @PostMapping("send")
    public String send (@ModelAttribute MessageDTO dto, 
                        @AuthenticationPrincipal AuthenticatedUser user, 
                        @RequestParam("messageContents") String messageContents, 
                        @RequestParam("shareNum") int shareNum) {
        
        dto.setReceiverNum(user.getNum());
        dto.setMessageContents(messageContents);
        dto.setShareNum(shareNum);

        messageService.saveMessage(dto);

        return "messageView/messageRead";
    }
}
