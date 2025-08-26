package com.gaprio.repository;

import com.gaprio.entities.Attachment;
import com.gaprio.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findByUploadedBy(User user);
}
