package com.xtremax.assessment.domain;

public enum ApplicationStatus {
    APPLICATION_RECEIVED("Application Received","Submitted"),
    UNDER_REVIEW("Under Review","Under Review"),
    PENDING_PRE_SITE_RESUBMISSION("Pending Pre-Site Resubmission","Pending Pre-Site Resubmission"),
    PRE_SITE_RESUBMITTED("Pre-Site Resubmitted","Pre-Site Resubmitted"),
    SITE_VISIT_SCHEDULED("Site Visit Scheduled","Pending Site Visit"),
    SITE_VISIT_DONE("Site Visit Done","Pending Post-Site Clarification"),
    AWAITING_POST_SITE_CLARIFICATION("Awaiting Post-Site Clarification","Pending Post-Site Clarification"),
    PENDING_POST_SITE_RESUBMISSION("Awaiting Post-Site Resubmission","Pending Post-Site Resubmission"),
    POST_SITE_CLARIFICATION_RESUBMITTED("Post-Site Clarification Resubmitted","Post-Site Resubmitted"),
    PENDING_APPROVAL("Route to Approval","Pending Approval"),
    APPROVED("Approved","Approved"),
    REJECTED("Rejected","Rejected");

    private final String officerLabel;
    private final String operatorLabel;

    ApplicationStatus(String officerLabel, String operatorLabel) {
        this.officerLabel = officerLabel;
        this.operatorLabel = operatorLabel;
    }

    public String getOfficerLabel() { return officerLabel; }
    public String getOperatorLabel() { return operatorLabel; }
}
