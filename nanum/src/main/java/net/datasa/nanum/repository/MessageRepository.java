package net.datasa.nanum.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.datasa.nanum.domain.entity.MessageEntity;
import net.datasa.nanum.domain.entity.RoomEntity;

/**
 * 쪽지 기능 처리 Repository
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {

    // 쪽지방 정보를 통해 해당 쪽지방의 쪽지 목록을 전부 가져온다.
    List<MessageEntity> findAllByRoom(RoomEntity room);
    // 쪽지방의 최신 쪽지 하나를 가져온다.
    @Query("SELECT m FROM MessageEntity m WHERE m.room = :room ORDER BY m.deliverDate DESC")
    List<MessageEntity> findLatestMessageByRoom(@Param("room") RoomEntity room);
    // 현재 회원이 읽지 않은 쪽지가 있는 방의 개수를 구한다. 단, 그 방에 읽지 않은 쪽지가 내가 보낸 쪽지만 있다면, 세지 않는다.
    @Query("SELECT COUNT(DISTINCT m.room) FROM MessageEntity m " +
    "WHERE m.isRead = false " +
    "AND (m.room.creator.memberNum = :memberNum OR m.room.receiver.memberNum = :memberNum) " +
    "AND m.sender.memberNum != :memberNum")
    long countRoomsWithUnreadMessagesByMemberNum(@Param("memberNum") int memberNum);
    // 안읽은 쪽지가 있는 방을 찾는다.
    List<MessageEntity> findByRoom_RoomNumAndIsReadFalse(Integer roomNum);
}
