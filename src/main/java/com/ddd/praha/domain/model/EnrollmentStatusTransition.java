package com.ddd.praha.domain.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EnrollmentStatusTransition {
  private static final Map<EnrollmentStatus, Set<EnrollmentStatus>> ALLOWED_TRANSITIONS = 
      Map.of(
          EnrollmentStatus.在籍中, EnumSet.of(EnrollmentStatus.休会中, EnrollmentStatus.退会済),
          EnrollmentStatus.休会中, EnumSet.of(EnrollmentStatus.在籍中, EnrollmentStatus.退会済),
          EnrollmentStatus.退会済, EnumSet.of(EnrollmentStatus.在籍中)
      );

  public boolean canTransit(EnrollmentStatus from, EnrollmentStatus to) {
    Objects.requireNonNull(from, "遷移元ステータスは必須です");
    Objects.requireNonNull(to, "遷移先ステータスは必須です");
    
    Set<EnrollmentStatus> allowedStates = ALLOWED_TRANSITIONS.get(from);
    return allowedStates != null && allowedStates.contains(to);
  }
}
