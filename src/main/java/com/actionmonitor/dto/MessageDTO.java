package com.actionmonitor.dto;

import java.io.Serializable;
import java.time.Instant;

public class MessageDTO implements Serializable {

    private Instant instant;
    private String content;
    private String from;
    private String to;

    public MessageDTO(Instant instant, String content, String from, String to) {
        this.instant = instant;
        this.content = content;
        this.from = from;
        this.to = to;
    }

    public MessageDTO() {
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
