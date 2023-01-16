package com.umc.approval.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    USER("ROLE_USER", "일반 사용자");

    private final String key;
    private final String description;
}
