package net.datasa.nanum.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.MessageDTO;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.domain.entity.MessageEntity;
import net.datasa.nanum.domain.entity.ShareBoardEntity;
import net.datasa.nanum.repository.MemberRepository;
import net.datasa.nanum.repository.MessageRepository;
import net.datasa.nanum.repository.ShareBoardRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MemberRepository memberRepository;
    private final ShareBoardRepository shareBoardRepository;
    private final MessageRepository messageRepository;


    /**
     * 현재 사용자가 주고받은 모든 쪽지를 가져와서, 게시글별로 그룹화하여 반환
     * @param num
     * @return
     */
    public Map<ShareBoardEntity, MessageEntity> getMessagesGroupedByShareNum(int num) {
        List<MessageEntity> allMessages = messageRepository.findAllByGiverMemberNumOrReceiverMemberNum(num, num);

        if (allMessages.isEmpty()) {
            log.error("쪽지가 없습니다.");
            throw new EntityNotFoundException("쪽지가 없습니다.");
        }


        // 쪽지를 게시글별로 그룹화하고, 게시글과 쪽지대상, 가장 최신 쪽지와 시간을 가져옴
         return allMessages.stream()
            .collect(Collectors.groupingBy(
                MessageEntity::getShareBoard, 
                Collectors.collectingAndThen(
                    Collectors.maxBy(Comparator.comparing(MessageEntity::getDeliverDate)),
                    Optional::get
                )
            ));

    }

    // 게시글 번호와 회원 번호를 가져와 해당 게시글의 유저가 보낸/받은 쪽지 목록을 가져옴
    public List<MessageEntity> getMessagesByShareNumAndUser(Integer shareNum, Integer num) {
        MemberEntity memberEntity = memberRepository.findById(num)
        .orElseThrow(() -> new EntityNotFoundException("회원 정보가 없습니다."));

        ShareBoardEntity shareBoardEntity = shareBoardRepository.findById(shareNum)
        .orElseThrow(() -> new EntityNotFoundException("게시글 정보가 없습니다."));


        return messageRepository.findByThree(shareBoardEntity, memberEntity, memberEntity);
    }
    

    public void saveMessage(MessageDTO dto) {
        MemberEntity memberEntity = memberRepository.findById(dto.getReceiverNum())
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        ShareBoardEntity shareBoardEntity = shareBoardRepository.findById(dto.getShareNum())
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));
                       
        MessageEntity messageEntity = MessageEntity.builder()
                                    .sender(memberEntity)
                                    .receiver(shareBoardEntity.getMember())
                                    .shareBoard(shareBoardEntity)
                                    .messageContents(dto.getMessageContents())
                                    .build();

        messageRepository.save(messageEntity);                            
    }


}
