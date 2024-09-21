package net.datasa.nanum.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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


        /**
         * 현재 사용자가 생성한 쪽지방을 찾아옴
         * 
         * @param num      현재 사용자
         * @param shareNum 게시글 번호
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


        public List<MessageDTO> getMessage(RoomEntity room, int num) {
                List<MessageEntity> messageList = messageRepository.findAllByRoom(room);
                List<MessageDTO> dtoList = new ArrayList<>();

                log.debug("지금 로그인한 유저: {}", num);
                log.debug("쪽지방 만든 유저: {}", room.getCreator().getMemberNum());
                log.debug("게시글 유저: {}", room.getReceiver().getMemberNum());

                // 내가 쓴 쪽지일때
                if (num == room.getCreator().getMemberNum()) {
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

        public List<MessageDTO> getUserRoomsWithLatestMessages(Integer memberNum) {
                MemberEntity member = memberRepository.findById(memberNum)
                .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

                List<RoomEntity> rooms = roomRepository.findByCreatorOrReceiver(member, member);

                List<MessageDTO> result = rooms.stream()
                .map(room -> {
                        List<MessageEntity> messages = messageRepository.findLatestMessageByRoom(room);
                        MessageEntity latestMessage = messages.isEmpty() ? null : messages.get(0);
                        log.debug("방 번호: {}", room.getRoomNum());
                        log.debug("게시글 번호: {}", room.getShareBoard().getShareNum());
                        int creator = room.getCreator().getMemberNum();
                        int receiver = room.getReceiver().getMemberNum();

                        if (memberNum == creator || memberNum == receiver) {
                                MessageDTO dto = MessageDTO.builder()
                                .messageNum(latestMessage!= null? latestMessage.getMessageNum() : null)
                                .senderNickname(memberNum == creator ? room.getReceiver().getMemberNickname() : room.getCreator().getMemberNickname())
                                .receiverNum(memberNum == creator ? room.getReceiver().getMemberNum() : room.getCreator().getMemberNum())
                                .shareNum(room.getShareBoard().getShareNum())
                                .shareTitle(room.getShareBoard().getShareTitle())
                                .roomNum(room.getRoomNum())
                                .messageContents(latestMessage != null ? latestMessage.getMessageContents() : null)
                                .deliverDate(latestMessage != null ? latestMessage.getDeliverDate() : null)
                                .isRead(latestMessage!= null? latestMessage.getIsRead() : false)
                                .shareWriteNum(room.getShareBoard().getMember().getMemberNum())
                                .shareCompleted(room.getShareBoard().getShareCompleted())
                                .build();

                                return dto;
                        }
                        else {
                                return null;
                        }
                })
                .filter(Objects::nonNull)   // null값 필터링
                .collect(Collectors.toList());
                return result;
        }

        // public void saveMessage(int roomNum, String messageContents, int senderNum) {
        //         RoomEntity room = roomRepository.findById(roomNum)
        //             .orElseThrow(() -> new EntityNotFoundException("쪽지방을 찾을 수 없습니다."));
            
        //         MemberEntity sender = memberRepository.findById(senderNum)
        //             .orElseThrow(() -> new EntityNotFoundException("발신자를 찾을 수 없습니다."));
            
        //         MessageEntity message = MessageEntity.builder()
        //             .room(room)
        //             .sender(sender)
        //             .messageContents(messageContents)
        //             .isRead(false)
        //             .build();
            
        //         messageRepository.save(message);
        //     }
        
        public long countRoomsWithUnreadMessagesByMemberNum(int memberNum) {
                log.debug("레포지토리에서 계산한 값: {}", messageRepository.countRoomsWithUnreadMessagesByMemberNum(memberNum));
                return messageRepository.countRoomsWithUnreadMessagesByMemberNum(memberNum);
            }

}
