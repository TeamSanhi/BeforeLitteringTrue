package net.datasa.nanum.Controller.Message;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.MessageDTO;
import net.datasa.nanum.domain.dto.RoomDTO;
import net.datasa.nanum.domain.entity.RoomEntity;
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
     * 
     * @param user
     * @param model
     * @return
     */
    @GetMapping("messages")
    public String messages(@AuthenticationPrincipal AuthenticatedUser user,
            Model model) {

        log.debug("messageView/messageList.html로 이동");


        return "messageView/messageList";
    }

    @GetMapping("read")
    public String read (@AuthenticationPrincipal AuthenticatedUser user, @RequestParam("shareNum") int shareNum, Model model) {

        log.debug("가져온 shareNum: {}", shareNum);
        //shareNum = 1;

        boolean roomExist = messageService.isRoomExist(user.getNum(), shareNum);
        log.debug("messageView/messageRead.html로 이동");
        log.debug("방 존재 여부: {}", roomExist);
        RoomEntity room = messageService.findRoom(user.getNum(), shareNum);

        if (roomExist) {

            List<MessageDTO> messageList = messageService.getMessage(room);

            model.addAttribute("room", room);
            model.addAttribute("roomExist", roomExist);
            model.addAttribute("messageList", messageList);
            log.debug("쪽지방이 이미 생성되어 있을 때");
            return "messageView/messageRead";
        } else {
            model.addAttribute("shareNum", shareNum);
            model.addAttribute("roomExist", roomExist);
            log.debug("생성된 쪽지방이 없을 때");
            return "messageView/messageRead";
        }
    }


    @PostMapping("createRoom")
    @ResponseBody
    public String createRoom(@AuthenticationPrincipal AuthenticatedUser user,
                             @RequestParam("messageContents") String messageContents,
                             @RequestParam("shareNum") int shareNum) {
    
    log.debug("쪽지내용: {}", messageContents);
    log.debug("게시글 번호: {}", shareNum);

    RoomDTO roomDTO = RoomDTO.builder()
                        .creatorNum(user.getNum())
                        .shareNum(shareNum)
                        .build();
                        
    messageService.createRoom(roomDTO);

    RoomEntity roomEntity = messageService.findRoom(user.getNum(), shareNum);

    messageService.messageSave(user.getNum(), roomEntity.getRoomNum(), messageContents);

    return "messageView/messageRead";
    }

    /**
     * 작성한 쪽지를 DB에 저장
     * 
     * @param dto
     * @param user
     * @param messageContents
     * @param shareNum
     * @return
     */
    @PostMapping("send")
    @ResponseBody
    public String send(@AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam("messageContents") String messageContents,
            @RequestParam("roomNum") int roomNum) {

        messageService.messageSave(user.getNum(), roomNum, messageContents);

        return "messageView/messageRead";
    }
}
