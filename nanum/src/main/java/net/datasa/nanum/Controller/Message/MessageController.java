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

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.MessageDTO;
import net.datasa.nanum.domain.dto.RoomDTO;
import net.datasa.nanum.domain.dto.ShareBoardDTO;
import net.datasa.nanum.domain.entity.RoomEntity;
import net.datasa.nanum.domain.entity.ShareBoardEntity;
import net.datasa.nanum.repository.RoomRepository;
import net.datasa.nanum.repository.ShareBoardRepository;
import net.datasa.nanum.security.AuthenticatedUser;
import net.datasa.nanum.service.MessageService;

@Slf4j
@Controller
@RequestMapping("message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final ShareBoardRepository shareBoardRepository;
    private final RoomRepository roomRepository;

    /**
     * 사용자의 쪽지 목록으로 이동
     * 
     * @param user
     * @param model
     * @return
     */
    @GetMapping("list")
    public String list(@AuthenticationPrincipal AuthenticatedUser user,
                        Model model) {

        List<RoomDTO> rooms = messageService.getAllUserRooms(user.getNum());

        model.addAttribute("rooms", rooms);
                            
        log.debug("messageView/messageList.html로 이동");



        return "messageView/messageList";
    }

    /**
     * 게시글에서 받을래요 눌렀을 때 쪽지 작성 페이지로 이동
     * @param user      현재 사용자
     * @param shareNum  게시글 번호
     * @param model     모델
     * @return
     */
    @GetMapping("read")
    public String read (@AuthenticationPrincipal AuthenticatedUser user, @RequestParam("shareNum") int shareNum, Model model) {

        log.debug("가져온 shareNum: {}", shareNum);

        ShareBoardEntity shareBoard = shareBoardRepository.findById(shareNum)
        .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        ShareBoardDTO shareDTO = ShareBoardDTO.builder()
        .shareNum(shareBoard.getShareNum())
        .memberNum(shareBoard.getMember().getMemberNum())
        .shareTitle(shareBoard.getShareTitle())
        .shareCompleted(shareBoard.getShareCompleted())
        .memberId(shareBoard.getMember().getMemberId())
        .build();

        // 이 게시글에 쪽지방이 있는지 확인
        boolean roomExist = messageService.isRoomExist(user.getNum(), shareNum);
        log.debug("messageView/messageRead.html로 이동");
        log.debug("방 존재 여부: {}", roomExist);
        
        
        // 쪽지방이 생성되어 있을 시
        if (roomExist) {
            // 이 글에 내가 생성한/포함된 쪽지방을 찾음
            RoomEntity room = messageService.findRoom(user.getNum(), shareNum);
            log.debug("방번호: {}", room.getRoomNum());
            // 이 방에서 주고받았던 쪽지 내역을 가져옴
            List<MessageDTO> messageList = messageService.getMessage(room, user.getNum());
            model.addAttribute("room", room);
            model.addAttribute("roomExist", roomExist);
            model.addAttribute("messageList", messageList);
            model.addAttribute("share", shareDTO);
            //model.addAttribute("thisBoardId", shareBoard.getMember().getMemberId());
            //model.addAttribute("shareCompleted", shareBoard.getShareCompleted());
            log.debug("쪽지방이 이미 생성되어 있을 때");
            return "messageView/messageRead";
        } 
        // 아직 만들어진 쪽지방이 없을 시
        else {
            model.addAttribute("share",shareDTO);
            //model.addAttribute("shareNum", shareNum);
            //model.addAttribute("thisBoardId", shareBoard.getMember().getMemberId());
            //model.addAttribute("shareCompleted", shareBoard.getShareCompleted());
            model.addAttribute("roomExist", roomExist);

            log.debug("생성된 쪽지방이 없을 때");
            return "messageView/messageRead";
        }
    }

    /**
     * 처음 쪽지를 썼을 때 쪽지방을 만들고 그 쪽지방에 입력한 쪽지를 저장
     * @param user              현재 사용자
     * @param messageContents   작성한 쪽지
     * @param shareNum          게시글 번호
     * @return
     */
    @PostMapping("createRoom")
    @ResponseBody
    public String createRoom(@AuthenticationPrincipal AuthenticatedUser user,
                             @RequestParam("messageContents") String messageContents,
                             @RequestParam("shareNum") int shareNum) {
    
    log.debug("쪽지내용: {}", messageContents);
    log.debug("게시글 번호: {}", shareNum);
            
    // RoomDTO에 지금 사용자번호, 게시글 번호를 담음
    RoomDTO roomDTO = RoomDTO.builder()
                        .creatorNum(user.getNum())
                        .shareNum(shareNum)
                        .build();
    
    // 해당 게시글의 현재 사용자의 정보로 쪽지방을 생성                    
    messageService.createRoom(roomDTO);
    // 방금 생성한 쪽지방을 찾아옴
    RoomEntity roomEntity = messageService.findRoom(user.getNum(), shareNum);
    // 찾아온 쪽지방에 작성한 쪽지 내용을 저장
    messageService.messageSave(user.getNum(), roomEntity.getRoomNum(), messageContents);

    return "messageView/messageRead";
    }

    /**
     * 쪽지방이 이미 생성되어 있을 때, 작성한 쪽지 내용을 저장
     * @param user              현재 사용자
     * @param messageContents   작성한 쪽지
     * @param roomNum           쪽지방 번호
     * @return
     */
    @PostMapping("send")
    @ResponseBody
    public String send(@AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam("messageContents") String messageContents,
            @RequestParam("roomNum") int roomNum) {
        // 작성한 쪽지 내용을 저장
        messageService.messageSave(user.getNum(), roomNum, messageContents);

        return "messageView/messageRead";
    }

    @GetMapping("readRoomDetails")
    @ResponseBody
    public String readRoomDetails(@RequestParam("roomNum") int roomNum, @AuthenticationPrincipal AuthenticatedUser user) {
        RoomEntity room = roomRepository.findById(roomNum)
            .orElseThrow(() -> new EntityNotFoundException("쪽지방을 찾을 수 없습니다."));

        List<MessageDTO> messages = messageService.getMessage(room, user.getNum());  
        StringBuilder sb = new StringBuilder();

        for (MessageDTO message : messages) {
            sb.append("<div>");
            sb.append("<strong>").append(message.getSenderNickname()).append("</strong>: ");
            sb.append(message.getMessageContents());
            sb.append(message.getDeliverDate());
            sb.append("</div>");
        }
    
        return sb.toString();  
    }

}
