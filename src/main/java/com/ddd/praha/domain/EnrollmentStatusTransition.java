package com.ddd.praha.domain;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EnrollmentStatusTransition {
  Map<EnrollmentStatus, Set<EnrollmentStatus>> allowed;
  {
    allowed = new HashMap<>();
    allowed.put(EnrollmentStatus.在籍中, EnumSet.of(EnrollmentStatus.休会中, EnrollmentStatus.退会済));
    allowed.put(EnrollmentStatus.休会中, EnumSet.of(EnrollmentStatus.在籍中, EnrollmentStatus.退会済));
    allowed.put(EnrollmentStatus.退会済, EnumSet.of(EnrollmentStatus.在籍中));
  }

  boolean canTransit(EnrollmentStatus from, EnrollmentStatus to) {
    Set<EnrollmentStatus> allowedStates = allowed.get(from);
    return allowedStates.contains(to);
  }
}
