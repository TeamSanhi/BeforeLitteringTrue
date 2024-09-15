package net.datasa.nanum.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.MessageDTO;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.domain.entity.MessageEntity;
import net.datasa.nanum.domain.entity.RoomEntity;
import net.datasa.nanum.domain.entity.ShareBoardEntity;
import net.datasa.nanum.repository.MemberRepository;
import net.datasa.nanum.repository.MessageRepository;
import net.datasa.nanum.repository.RoomRepository;
import net.datasa.nanum.repository.ShareBoardRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MemberRepository memberRepository;
    private final ShareBoardRepository shareBoardRepository;
    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;

//     /**
//      * 해당 게시글에 현재 사용자의 정보가 있는 쪽지방이 존재하는지 확인
//      * @param num
//      * @param shareNum
//      * @return
//      */
//     public boolean isRoomExist(int num, int shareNum) {
//         MemberEntity member = memberRepository.findById(num)
//         .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

//         ShareBoardEntity shareBoard = shareBoardRepository.findById(shareNum)
//         .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

//         return roomRepository.existsByCreatorAndShareBoard(member, shareBoard);
//     }

    /**
     * 현재 사용자가 생성한 쪽지방을 찾아옴
     * @param num       현재 사용자
     * @param shareNum  게시글 번호
     * @return
     */
    public RoomEntity findRoom(int creatorNum, int shareNum) {
        MemberEntity member = memberRepository.findById(creatorNum)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        ShareBoardEntity shareBoard = shareBoardRepository.findById(shareNum)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        
        log.debug("서비스 전달값: {}, {}", creatorNum, shareNum);
   
        RoomEntity room = roomRepository.findByCreatorAndShareBoard(member, shareBoard);
        return room;
    }


// public void createRoom(RoomDTO roomDTO) {
//         MemberEntity member = memberRepository.findById(roomDTO.getCreatorNum())
//                 .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

//         ShareBoardEntity shareBoard = shareBoardRepository.findById(roomDTO.getShareNum())
//                 .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));    
        
//         RoomEntity roomEntity = RoomEntity.builder()
//                                 .creator(member)
//                                 .receiver(shareBoard.getMember())
//                                 .shareBoard(shareBoard)
//                                 .build();
        
//         roomRepository.save(roomEntity);
        
// }


// public void messageSave(int num, Integer roomNum, String messageContents) {
//         MemberEntity member = memberRepository.findById(num)
//                 .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
//         RoomEntity room = roomRepository.findById(roomNum)
//                 .orElseThrow(() -> new EntityNotFoundException("쪽지방을 찾을 수 없습니다."));
        
//         MessageEntity messageEntity = MessageEntity.builder()
//                                 .sender(member)
//                                 .room(room)
//                                 .messageContents(messageContents)
//                                 .isRead(false)                        
//                                 .build();

//         messageRepository.save(messageEntity);                        
        
// }


public List<MessageDTO> getMessage(RoomEntity room, int num) {
        List<MessageEntity> messageList = messageRepository.findAllByRoom(room);
        List<MessageDTO> dtoList = new ArrayList<>();

        log.debug("지금 로그인한 유저: {}", num);
        log.debug("쪽지방 만든 유저: {}", room.getCreator().getMemberNum());
        log.debug("게시글 유저: {}", room.getReceiver().getMemberNum());

        // 내가 쓴 쪽지일때
        if(num == room.getCreator().getMemberNum()) {
                for (MessageEntity message : messageList) {
                        MessageDTO dto = MessageDTO.builder()
                        .messageNum(message.getMessageNum())
                        .senderNum(message.getSender().getMemberNum())
                        .senderNickname(message.getSender().getMemberNickname())
                        .roomNum(message.getRoom().getRoomNum())
                        .messageContents(message.getMessageContents())
                        .deliverDate(message.getDeliverDate())
                        .isRead(message.getIsRead())
                        .build();
                        dtoList.add(dto);
                }
        }
        // 내가 받은 쪽지일때
        else if (num == room.getReceiver().getMemberNum()) {
                for (MessageEntity message : messageList) {
                        message.setIsRead(true);
                        messageRepository.save(message);
        
                        MessageDTO dto = MessageDTO.builder()
                        .messageNum(message.getMessageNum())
                        .senderNum(message.getSender().getMemberNum())
                        .senderNickname(message.getSender().getMemberNickname())
                        .roomNum(message.getRoom().getRoomNum())
                        .messageContents(message.getMessageContents())
                        .deliverDate(message.getDeliverDate())
                        .isRead(message.getIsRead())
                        .build();
                        dtoList.add(dto);
                }
        } else {
                return null;
        }
        return dtoList;
}

// public List<RoomDTO> getAllUserRooms(int userNum) {
//         MemberEntity member = memberRepository.findById(userNum)
//                 .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

//     List<RoomEntity> rooms = roomRepository.findByCreatorOrReceiver(member, member);
//     return rooms.stream()
//                 .map(room -> RoomDTO.builder()
//                                      .roomNum(room.getRoomNum())
//                                      .creatorNum(room.getCreator().getMemberNum())
//                                      .receiverNum(room.getReceiver().getMemberNum())
//                                      .shareNum(room.getShareBoard().getShareNum())
//                                      .build())
//                 .collect(Collectors.toList());
// }

public RoomEntity findOrCreateRoom(MemberEntity creator, ShareBoardEntity shareBoard) {

        RoomEntity room = findRoom(creator.getMemberNum(), shareBoard.getShareNum());
        MemberEntity receiver = memberRepository.findById(shareBoard.getMember().getMemberNum())
                .orElseThrow(() -> new EntityNotFoundException("게시자 정보를 찾을 수 없습니다."));
        if (room == null) {
                room = new RoomEntity();
                room.setCreator(creator);
                room.setReceiver(receiver);
                room.setShareBoard(shareBoard);
                roomRepository.save(room);
        }
        return room;
}


public void saveMessage(MessageEntity message) {
        messageRepository.save(message);
}

}
