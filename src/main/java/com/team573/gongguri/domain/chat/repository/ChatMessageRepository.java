package com.team573.gongguri.domain.chat.repository;


import com.team573.gongguri.domain.chat.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

}
