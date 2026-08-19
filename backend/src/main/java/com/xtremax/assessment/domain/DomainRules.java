package com.xtremax.assessment.domain;

import java.util.*;

public final class DomainRules {
    private static final Map<ApplicationStatus, Set<ApplicationStatus>> ALLOWED = new EnumMap<>(ApplicationStatus.class);
    static {
        ALLOWED.put(ApplicationStatus.APPLICATION_RECEIVED, Set.of(ApplicationStatus.UNDER_REVIEW));
        ALLOWED.put(ApplicationStatus.UNDER_REVIEW, Set.of(
                ApplicationStatus.PENDING_PRE_SITE_RESUBMISSION,
                ApplicationStatus.SITE_VISIT_SCHEDULED,
                ApplicationStatus.PENDING_APPROVAL,
                ApplicationStatus.REJECTED
        ));

        ALLOWED.put(ApplicationStatus.PENDING_PRE_SITE_RESUBMISSION, Set.of(ApplicationStatus.PRE_SITE_RESUBMITTED));
        ALLOWED.put(ApplicationStatus.PRE_SITE_RESUBMITTED, Set.of(ApplicationStatus.UNDER_REVIEW));

        ALLOWED.put(ApplicationStatus.SITE_VISIT_SCHEDULED, Set.of(ApplicationStatus.SITE_VISIT_DONE));
        ALLOWED.put(ApplicationStatus.SITE_VISIT_DONE, Set.of(ApplicationStatus.AWAITING_POST_SITE_CLARIFICATION));
        ALLOWED.put(ApplicationStatus.AWAITING_POST_SITE_CLARIFICATION, Set.of(ApplicationStatus.PENDING_POST_SITE_RESUBMISSION));
        ALLOWED.put(ApplicationStatus.PENDING_POST_SITE_RESUBMISSION, Set.of(ApplicationStatus.POST_SITE_CLARIFICATION_RESUBMITTED));
        ALLOWED.put(ApplicationStatus.POST_SITE_CLARIFICATION_RESUBMITTED, Set.of(ApplicationStatus.UNDER_REVIEW));

        ALLOWED.put(ApplicationStatus.PENDING_APPROVAL, Set.of(ApplicationStatus.APPROVED, ApplicationStatus.REJECTED));

        ALLOWED.put(ApplicationStatus.APPROVED, Set.of());
        ALLOWED.put(ApplicationStatus.REJECTED, Set.of());
    }

    private DomainRules() {}

    public static boolean isAllowedTransition(ApplicationStatus from, ApplicationStatus to) {
        if (from == null || to == null) return false;
        return ALLOWED.getOrDefault(from, Collections.emptySet()).contains(to);
    }

    public static Set<ApplicationStatus> allowedFrom(ApplicationStatus from) {
        return Collections.unmodifiableSet(ALLOWED.getOrDefault(from, Collections.emptySet()));
    }
}
