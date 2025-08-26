package com.gaprio.repository;

import com.gaprio.entities.ChatMessage;
import com.gaprio.entities.MessageReceipt;
import com.gaprio.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageReceiptRepository extends JpaRepository<MessageReceipt, UUID> {
    List<MessageReceipt> findByMessage(ChatMessage message);
    List<MessageReceipt> findByUser(User user);
    List<MessageReceipt> findByUserAndStatus(User user, String status); // SENT, DELIVERED, READ
}
