package net.datasa.nanum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.datasa.nanum.domain.entity.MessageEntity;

public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {
    
}
