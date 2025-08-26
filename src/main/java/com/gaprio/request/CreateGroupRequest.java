package com.gaprio.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGroupRequest {
    private String groupName;
    private UUID creatorId;
    private List<UUID> memberIds;  // Initial group members


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public UUID getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(UUID creatorId) {
        this.creatorId = creatorId;
    }

    public List<UUID> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<UUID> memberIds) {
        this.memberIds = memberIds;
    }
}
