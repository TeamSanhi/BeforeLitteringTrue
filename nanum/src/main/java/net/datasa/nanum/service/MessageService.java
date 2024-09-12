package net.datasa.nanum.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.MessageDTO;
import net.datasa.nanum.domain.dto.RoomDTO;
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

    /**
     * 해당 게시글에 현재 사용자의 정보가 있는 쪽지방이 존재하는지 확인
     * @param num
     * @param shareNum
     * @return
     */
    public boolean isRoomExist(int num, int shareNum) {
        MemberEntity member = memberRepository.findById(num)
        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        ShareBoardEntity shareBoard = shareBoardRepository.findById(shareNum)
        .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));


        log.debug("나인가 아닌가: {}", num == shareBoard.getMember().getMemberNum());

        // 게시글 주인이 나일 경우
        if (num == shareBoard.getMember().getMemberNum()) {
                return roomRepository.existsByReceiverAndShareBoard(member, shareBoard);
        } 
        // 게시글 주인이 내가 아닐 경우
        else {
                return roomRepository.existsByCreatorAndShareBoard(member, shareBoard);
        }
        
    }

    /**
     * 현재 사용자가 포함된 쪽지방을 찾아옴
     * @param num       현재 사용자
     * @param shareNum  게시글 번호
     * @return
     */
    public RoomEntity findRoom(int num, int shareNum) {
        MemberEntity member = memberRepository.findById(num)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        ShareBoardEntity shareBoard = shareBoardRepository.findById(shareNum)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        
        log.debug("서비스 전달값: {}, {}", num, shareNum);        
        // 게시글 주인이 나일 경우
        if (num == shareBoard.getMember().getMemberNum()) {
                RoomEntity room = roomRepository.findByReceiverAndShareBoard(member, shareBoard);
                return room;
        } 
        // 게시글 주인이 내가 아닐 경우
        else {       
                RoomEntity room = roomRepository.findByCreatorAndShareBoard(member, shareBoard);
                return room;
        }        
    }


public void createRoom(RoomDTO roomDTO) {
        MemberEntity member = memberRepository.findById(roomDTO.getCreatorNum())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        ShareBoardEntity shareBoard = shareBoardRepository.findById(roomDTO.getShareNum())
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));    
        
        RoomEntity roomEntity = RoomEntity.builder()
                                .creator(member)
                                .receiver(shareBoard.getMember())
                                .shareBoard(shareBoard)
                                .build();
        
        roomRepository.save(roomEntity);
        
}


public void messageSave(int num, Integer roomNum, String messageContents) {
        MemberEntity member = memberRepository.findById(num)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        RoomEntity room = roomRepository.findById(roomNum)
                .orElseThrow(() -> new EntityNotFoundException("쪽지방을 찾을 수 없습니다."));
        
        MessageEntity messageEntity = MessageEntity.builder()
                                .sender(member)
                                .room(room)
                                .messageContents(messageContents)
                                .isRead(false)                        
                                .build();

        messageRepository.save(messageEntity);                        
        
}


public List<MessageDTO> getMessage(RoomEntity room, String userName) {
        List<MessageEntity> messageList = messageRepository.findAllByRoom(room);
        List<MessageDTO> dtoList = new ArrayList<>();

        // 내가 쓴 쪽지일때
        if(userName == room.getCreator().getMemberId()) {
                for (MessageEntity message : messageList) {
                        log.debug("쪽지: {}", message);
                        MessageDTO messageDTO = MessageDTO.builder()
                        .messageNum(message.getMessageNum())
                        .senderNum(message.getSender().getMemberNum())
                        .senderNickname(message.getSender().getMemberNickname())
                        .roomNum(message.getRoom().getRoomNum())
                        .messageContents(message.getMessageContents())
                        .deliverDate(message.getDeliverDate())
                        .isRead(message.getIsRead())
                        .build();
                        dtoList.add(messageDTO);
                }
                return dtoList;
        }
        // 내가 받은 쪽지일때
        else if (userName == room.getReceiver().getMemberId()) {
                for (MessageEntity message : messageList) {
                        message.setIsRead(true);
                        messageRepository.save(message);
        
                        MessageDTO messageDTO = MessageDTO.builder()
                        .messageNum(message.getMessageNum())
                        .senderNum(message.getSender().getMemberNum())
                        .senderNickname(message.getSender().getMemberNickname())
                        .roomNum(message.getRoom().getRoomNum())
                        .messageContents(message.getMessageContents())
                        .deliverDate(message.getDeliverDate())
                        .isRead(message.getIsRead())
                        .build();
                        dtoList.add(messageDTO);
                }
                return dtoList;
        } else {
                return null;
        }
}

/**
 * 현재 게시글이 내가 올린 게시글인지 아닌지 확인
 * @param num           현재 회원번호
 * @param shareNum      게시글 번호
 * @return
 */
public boolean isMe(int num, int shareNum) {
        ShareBoardEntity shareBoard = shareBoardRepository.findById(shareNum)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        
        if(num == shareBoard.getMember().getMemberNum()) {
                return true;
        } else{
                return false;
        }       
}










}
