package net.datasa.nanum.Controller.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import net.datasa.nanum.security.AuthenticatedUser;
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

    @Value("${board.uploadPath}")
    String uploadPath;

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

                MemberEntity receiver = room.getReceiver();

                int receiverMemberNum = receiver.getMemberNum();

                // 상대방의 프로필 이미지 URL 생성
                String receiverProfileImage = "/member/profileDownload?memberNum=" + receiverMemberNum;

                response.put("receiverNickname", receiver.getMemberNickname());
                response.put("receiverProfileImage", receiverProfileImage); // 프로필 이미지 URL 필드

                log.debug("프로필사진: {}", receiverProfileImage);

            } 
            // 쪽지방이 없을 때
            else {
                MemberEntity nReceiver = memberRepository.findById(receiverNum)
                    .orElseThrow(() -> new EntityNotFoundException("회원 정보가 존재하지 않습니다."));

                String nReceiverNickname = nReceiver.getMemberNickname();
                int nReceiverMemberNum = nReceiver.getMemberNum();

            // 상대방의 프로필 이미지 URL 생성
            String nReceiverProfileImage = "/member/profileDownload?memberNum=" + nReceiverMemberNum;

                response.put("roomExists", false);
                response.put("receiverNickname", nReceiverNickname);            // 상대 닉네임
                response.put("receiverProfileImage", nReceiverProfileImage); // 프로필 이미지 URL 필드

                log.debug("닉네임: {}, 이미지: {}",nReceiverNickname, nReceiverProfileImage);
            }

            return ResponseEntity.ok(response);
    }

    /**
     * 받을래요에서 작성한 쪽지를 DB에 저장
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

    // 홈에서 쪽지 버튼 클릭시 쪽지방을 조회
    @GetMapping("rooms")
    @ResponseBody
    public List<MessageDTO> getUserRoomsWithLatestMessages(@RequestParam("memberNum") Integer memberNum) {

        log.debug("가져오는 쪽지 정보: {}", messageService.getUserRoomsWithLatestMessages(memberNum));

        return messageService.getUserRoomsWithLatestMessages(memberNum);
    }


    // 홈 모달창의 쪽지방을 눌렀을 때 해당 쪽지방의 내역을 모두 출력
    // @GetMapping("details")
    // @ResponseBody
    // public List<MessageDTO> getMessageDetails (@RequestParam("roomNum") int roomNum, @RequestParam("userNum") int userNum) {
    //     RoomEntity room = roomRepository.findById(roomNum).orElseThrow(()-> new EntityNotFoundException("쪽지방이 존재하지 않습니다."));

    //     return messageService.getMessage(room, userNum);
    // }

    @GetMapping("details")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMessageDetails(@RequestParam("roomNum") int roomNum, @RequestParam("userNum") int userNum) {
        

        RoomEntity room = roomRepository.findById(roomNum)
            .orElseThrow(() -> new EntityNotFoundException("쪽지방이 존재하지 않습니다."));
    
        // 메시지 내역 가져오기
        List<MessageDTO> messages = messageService.getMessage(room, userNum);
    
        // // 상대 정보 가져오기
        // String receiverNickname = (userNum == room.getCreator().getMemberNum()) 
        //     ? room.getReceiver().getMemberNickname() 
        //     : room.getCreator().getMemberNickname();
    
        // String receiverProfileImage = (userNum == room.getCreator().getMemberNum()) 
        //     ? room.getReceiver().getMemberFileName() 
        //     : room.getCreator().getMemberFileName();

        // 상대방 정보 가져오기
        MemberEntity receiver = (userNum == room.getCreator().getMemberNum()) 
            ? room.getReceiver() 
            : room.getCreator();

        String receiverNickname = receiver.getMemberNickname();
        int receiverMemberNum = receiver.getMemberNum();

        // 상대방의 프로필 이미지 URL 생성
        String receiverProfileImage = "/member/profileDownload?memberNum=" + receiverMemberNum;

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("messages", messages);
        response.put("receiverNickname", receiverNickname);
        response.put("receiverProfileImage", receiverProfileImage);
    
        return ResponseEntity.ok(response);
    }    

    @PostMapping("detailSend")
    @ResponseBody
    public ResponseEntity<String> sendMessage(@RequestParam("roomNum") int roomNum,
                                            @RequestParam("messageContents") String messageContents,
                                            @AuthenticationPrincipal AuthenticatedUser user){

        MemberEntity member = memberRepository.findById(user.getNum())
            .orElseThrow(() -> new EntityNotFoundException("회원 정보가 존재하지 않습니다."));                                        

        RoomEntity room = roomRepository.findById(roomNum)
            .orElseThrow(()-> new EntityNotFoundException("쪽지방이 존재하지 않습니다."));

        MessageEntity message = MessageEntity.builder()
        .sender(member)
        .room(room)
        .messageContents(messageContents)
        .isRead(false)
        .build();

        messageService.saveMessage(message);


        return ResponseEntity.ok("success");
    }

    @GetMapping("/rooms/unreadCount/{memberNum}")
    @ResponseBody
    public ResponseEntity<Long> getUnreadMessageRoomCountByMember(@PathVariable("memberNum") int memberNum) {

        log.debug("가져온 회원번호: {}", memberNum);
        long count = messageService.countRoomsWithUnreadMessagesByMemberNum(memberNum);
        log.debug("안읽은 개수: {}",count);
        return ResponseEntity.ok(count);
    }
    

}
