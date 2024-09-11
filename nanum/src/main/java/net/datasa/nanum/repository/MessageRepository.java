package net.datasa.nanum.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.domain.entity.MessageEntity;
import net.datasa.nanum.domain.entity.ShareBoardEntity;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {
    
    @Query("SELECT m FROM MessageEntity m WHERE m.shareBoard = :shareBoard AND (m.sender = :sender OR m.receiver = :receiver)")
    List<MessageEntity> findByThree(@Param("shareBoard") ShareBoardEntity shareBoard, @Param("sender") MemberEntity sender, @Param("receiver") MemberEntity receiver);

    // 게시글 번호와 회원 번호를 이용하여 모든 쪽지 목록을 가져온다.
    //List<MessageEntity> findByShareBoardShareNumAndGiverMemberNumOrReceiverMemberNum(Integer shareNum, Integer giverNum, Integer receiverNum);

    // 회원 번호를 이용하여 보낸 쪽지와 받은 쪽지를 모두 가져온다.
    List<MessageEntity> findAllByGiverMemberNumOrReceiverMemberNum(int num, int num2);
}
