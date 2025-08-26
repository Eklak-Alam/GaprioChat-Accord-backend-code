package com.gaprio.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddGroupMemberRequest {
    private Long groupId;
    private Long userId;
}
