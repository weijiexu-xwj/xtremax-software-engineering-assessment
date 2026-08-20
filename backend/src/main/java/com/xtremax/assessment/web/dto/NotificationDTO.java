package com.xtremax.assessment.web.dto;

import java.time.Instant;
import java.util.UUID;

public class NotificationDTO {
    public UUID id;
    public String recipient;
    public String message;
    public Instant sentAt;
}
