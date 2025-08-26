package com.gaprio.service;

import com.gaprio.entities.Attachment;
import com.gaprio.entities.User;
import com.gaprio.exceptions.ResourceNotFoundException;
import com.gaprio.repository.AttachmentRepository;
import com.gaprio.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Save attachment metadata and query.
 * Actual file upload should happen directly to S3/MinIO via presigned URLs.
 */
@Service
@Transactional
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;

    public AttachmentService(AttachmentRepository attachmentRepository, UserRepository userRepository) {
        this.attachmentRepository = attachmentRepository;
        this.userRepository = userRepository;
    }

    /**
     * Save attachment metadata to DB.
     */
    public Attachment saveAttachment(String url, String mimeType, Long size, UUID uploaderId) {
        User uploader = userRepository.findById(uploaderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", uploaderId.toString()));

        Attachment a = Attachment.builder()
                .url(url)
                .mimeType(mimeType)
                .size(size)
                .uploadedAt(System.currentTimeMillis())
                .uploadedBy(uploader)
                .build();
        return attachmentRepository.save(a);
    }

    /**
     * Get attachments uploaded by a user.
     */
    public List<Attachment> getAttachmentsByUser(UUID userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));
        return attachmentRepository.findByUploadedBy(u);
    }
}
