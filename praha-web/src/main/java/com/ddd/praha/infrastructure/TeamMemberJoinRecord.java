package com.ddd.praha.infrastructure;

public record TeamMemberJoinRecord(
    String teamId,
    String teamName,
    String memberId,
    String memberName,
    String memberEmail,
    String memberStatus
) {}