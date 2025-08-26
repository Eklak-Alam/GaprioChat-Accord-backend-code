package com.gaprio.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatRequest {
    private UUID userId1;
    private UUID userId2;   // Start private chat between two users

    public UUID getUserId1() {
        return userId1;
    }

    public void setUserId1(UUID userId1) {
        this.userId1 = userId1;
    }

    public UUID getUserId2() {
        return userId2;
    }

    public void setUserId2(UUID userId2) {
        this.userId2 = userId2;
    }
}
