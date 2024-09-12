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

    public boolean isRoomExist(int num, int shareNum) {
        MemberEntity member = memberRepository.findById(num)
        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        ShareBoardEntity shareBoard = shareBoardRepository.findById(shareNum)
        .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        return roomRepository.existsByCreatorAndShareBoard(member, shareBoard);
    }


    public RoomEntity findRoom(int num, int shareNum) {
        MemberEntity member = memberRepository.findById(num)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        ShareBoardEntity shareBoard = shareBoardRepository.findById(shareNum)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        
        RoomEntity room = roomRepository.findByCreatorAndShareBoard(member, shareBoard);

        return room;
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


public List<MessageDTO> getMessage(RoomEntity room) {
        List<MessageEntity> messageList = messageRepository.findAllByRoom(room);

        List<MessageDTO> dtoList = new ArrayList<>();

        for (MessageEntity message : messageList) {
                MessageDTO messageDTO = MessageDTO.builder()
                .messageNum(message.getMessageNum())
                .senderNum(message.getSender().getMemberNum())
                .roomNum(message.getRoom().getRoomNum())
                .messageContents(message.getMessageContents())
                .deliverDate(message.getDeliverDate())
                .isRead(message.getIsRead())
                .build();
                dtoList.add(messageDTO);
        }

        return dtoList;
}










}
