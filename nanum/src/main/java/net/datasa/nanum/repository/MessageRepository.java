package net.datasa.nanum.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.datasa.nanum.domain.entity.MessageEntity;
import net.datasa.nanum.domain.entity.RoomEntity;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {

    // 쪽지방 정보를 통해 해당 쪽지방의 쪽지 목록을 전부 가져온다.
    List<MessageEntity> findAllByRoom(RoomEntity room);


}
