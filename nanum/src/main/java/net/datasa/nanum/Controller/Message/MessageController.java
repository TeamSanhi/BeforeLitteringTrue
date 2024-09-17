package net.datasa.nanum.Controller.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.MessageDTO;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.domain.entity.MessageEntity;
import net.datasa.nanum.domain.entity.RoomEntity;
import net.datasa.nanum.domain.entity.ShareBoardEntity;
import net.datasa.nanum.repository.MemberRepository;
import net.datasa.nanum.repository.RoomRepository;
import net.datasa.nanum.repository.ShareBoardRepository;
import net.datasa.nanum.service.MessageService;

@Slf4j
@Controller
@RequestMapping("message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final MemberRepository memberRepository;
    private final ShareBoardRepository shareBoardRepository;
    private final RoomRepository roomRepository;

    /**
     * 받을래요 버튼 클릭 시 쪽지방이 있는지 여부를 확인하고 메세지 내역을 출력
     * @param creatorNum    현재 로그인한 회원 번호
     * @param receiverNum   게시글 주인 번호
     * @param shareNum      게시글 번호
     * @return
     */
    @GetMapping("check")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkAndLoadRoom (
        @RequestParam("creatorNum") Integer creatorNum,
        @RequestParam("receiverNum") Integer receiverNum,
        @RequestParam("shareNum") Integer shareNum) {
            Map<String, Object> response = new HashMap<>();

            log.debug("ajax에서 받아온 회원번호:{}, 게시자 번호: {}, 게시글 번호: {}", creatorNum, receiverNum, shareNum);

            // 게시글에 현재 사용자가 만든 쪽지방이 있는지 확인
            RoomEntity room = messageService.findRoom(creatorNum, shareNum);

            log.debug("쪽지방이 존재하는가? {}", room != null);

            // 쪽지방이 있을 때
            if (room != null) {
                // 쪽지방의 쪽지 내역을 가져옴
                List<MessageDTO> messages = messageService.getMessage(room, creatorNum);
                response.put("roomExists", true);
                response.put("existingMessages", messages);
            } 
            // 쪽지방이 없을 때
            else {
                response.put("roomExists", false);
            }

            return ResponseEntity.ok(response);
    }

    /**
     * 작성한 쪽지를 DB에 저장
     * @param creatorNum        현재 로그인한(&쪽지를 작성한) 회원 번호
     * @param receiverNum       게시글 주인 번호
     * @param messageContents   작성한 쪽지 내용
     * @param shareNum          게시글 번호
     * @return
     */
    @PostMapping("send")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestParam("creatorNum") Integer creatorNum,
            @RequestParam("receiverNum") Integer receiverNum,
            @RequestParam("messageContents") String messageContents,
            @RequestParam("shareNum") Integer shareNum) {
        Map<String, Object> response = new HashMap<>();

        log.debug("ajax에서 보내온 회원번호: {}, 게시자 번호: {}, 쪽지: {}, 게시글 번호 : {}", creatorNum, receiverNum, messageContents, shareNum);
        
        MemberEntity creator = memberRepository.findById(creatorNum)
            .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));
        ShareBoardEntity shareBoard = shareBoardRepository.findById(shareNum)
            .orElseThrow(() -> new EntityNotFoundException("게시글 정보을 찾을 수 없습니다."));
        
        // 쪽지방이 있으면 그 쪽지방을 찾아옴 / 없으면 쪽지방 생성
        RoomEntity room = messageService.findOrCreateRoom(creator, shareBoard);
        
        // 쪽지 엔티티에 정보 저장
        MessageEntity message = MessageEntity.builder()
                .sender(creator)
                .room(room)
                .messageContents(messageContents)
                .isRead(false)
                .build();
        
        // 엔티티를 DB에 저장
         messageService.saveMessage(message);
         
         response.put("success", true);
         return ResponseEntity.ok(response);
    }    

    @GetMapping("rooms")
    @ResponseBody
    public List<MessageDTO> getUserRoomsWithLatestMessages(@RequestParam("memberNum") Integer memberNum) {
        return messageService.getUserRoomsWithLatestMessages(memberNum);
    }

}
